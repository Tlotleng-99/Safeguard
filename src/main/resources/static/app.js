"use strict";
const form = document.getElementById("scanForm");
const fileInput = document.getElementById("fileInput");
const dropArea = document.getElementById("dropArea");
const fileLabel = document.getElementById("fileLabel");
const fileHint = document.getElementById("fileHint");
const scanButton = document.getElementById("scanButton");
const statusMessage = document.getElementById("statusMessage");
const results = document.getElementById("results");

const MAX_SIZE = 10 * 1024 * 1024;

const dashboardStatus = document.getElementById("dashboardStatus");
const totalScans = document.getElementById("totalScans");
const matchedScans = document.getElementById("matchedScans");
const unmatchedScans = document.getElementById("unmatchedScans");
const historyBody = document.getElementById("historyBody");
const refreshHistory = document.getElementById("refreshHistory");

const resultName = document.getElementById("resultName");
const resultSize = document.getElementById("resultSize");
const resultHash = document.getElementById("resultHash");
const resultMessage = document.getElementById("resultMessage");
const resultBadge = document.getElementById("resultBadge");
const resultWarning = document.getElementById("resultWarning");

let historyRequestInProgress = false;



function resetFileSelection() {
    if (fileInput) {
        fileInput.value = "";
    }

    if (fileLabel) {
        fileLabel.textContent = "Choose a file to scan";
    }

    if (fileHint) {
        fileHint.textContent = "Click here or drag and drop a file";
    }
}

function validateFile(file) {
    if (!file) {
        return "Please select a file first.";
    }

    if (file.size === 0) {
        return "The selected file is empty.";
    }

    if (file.size > MAX_SIZE) {
        return "The file exceeds the 10 MB limit.";
    }

    return null;
}

function selectFile(file) {
    if (!file) return;

    const error = validateFile(file);

    if (error) {
        resetFileSelection();

        if (statusMessage) {
            statusMessage.textContent = error;
        }

        return;
    }

    if (fileLabel) {
        fileLabel.textContent = file.name;
    }

    if (fileHint) {
        fileHint.textContent = `${formatSize(file.size)} selected`;
    }

    if (statusMessage) {
        statusMessage.textContent = "📄 File selected and ready to scan.";
    }
}

if (fileInput) {
    fileInput.addEventListener("change", () => {
        selectFile(fileInput.files[0]);
    });
}



if (dropArea && fileInput) {
    ["dragenter", "dragover"].forEach(eventName => {
        dropArea.addEventListener(eventName, event => {
            event.preventDefault();
            dropArea.classList.add("dragging");
        });
    });

    ["dragleave", "drop"].forEach(eventName => {
        dropArea.addEventListener(eventName, event => {
            event.preventDefault();
            dropArea.classList.remove("dragging");
        });
    });

    dropArea.addEventListener("drop", event => {
        const file = event.dataTransfer?.files?.[0];

        if (!file) return;

        const error = validateFile(file);

        if (error) {
            resetFileSelection();

            if (statusMessage) {
                statusMessage.textContent = error;
            }

            return;
        }

        try {
            const transfer = new DataTransfer();
            transfer.items.add(file);
            fileInput.files = transfer.files;
            selectFile(file);
        } catch (error) {
            if (statusMessage) {
                statusMessage.textContent =
                    "Unable to attach the dropped file. Please choose it manually.";
            }

            console.error("File drop error:", error);
        }
    });
}


function formatSize(bytes) {
    const size = Number(bytes);

    if (!Number.isFinite(size) || size < 0) {
        return "Unknown size";
    }

    if (size < 1024) {
        return `${size} B`;
    }

    if (size < 1024 * 1024) {
        return `${(size / 1024).toFixed(1)} KB`;
    }

    return `${(size / (1024 * 1024)).toFixed(2)} MB`;
}

function formatTimestamp(timestamp) {
    if (!timestamp) {
        return "Unknown";
    }

    const date = new Date(timestamp);

    if (Number.isNaN(date.getTime())) {
        return "Unknown";
    }

    return date.toLocaleString(undefined, {
        dateStyle: "medium",
        timeStyle: "short"
    });
}

function addCell(row, value) {
    const cell = document.createElement("td");

    // textContent prevents API-provided text being interpreted as HTML.
    cell.textContent = value == null ? "" : String(value);

    row.appendChild(cell);

    return cell;
}

function setStatus(message, type = "normal") {
    if (!statusMessage) return;

    statusMessage.textContent = message;
    statusMessage.dataset.status = type;
}

function setDashboardStatus(message) {
    if (dashboardStatus) {
        dashboardStatus.textContent = message;
    }
}

function setButtonBusy(isBusy) {
    if (!scanButton) return;

    scanButton.disabled = isBusy;
    scanButton.textContent = isBusy
        ? "🔍 Scanning..."
        : "🛡️ Scan file →";
}

async function readJsonResponse(response) {
    try {
        return await response.json();
    } catch {
        throw new Error(
            "The server returned an invalid response. Please try again."
        );
    }
}


function displayScanResult(result) {
    if (resultName) {
        resultName.textContent = result.fileName || "Unknown file";
    }

    if (resultSize) {
        resultSize.textContent =
            `${Number(result.sizeBytes) || 0} bytes`;
    }

    if (resultHash) {
        resultHash.textContent = result.sha256 || "Unavailable";
    }

    if (resultMessage) {
        resultMessage.textContent =
            result.message || "The scan has completed.";
    }

    if (resultBadge) {
        resultBadge.className = "result-badge";

        if (result.signatureMatched === true) {
            resultBadge.classList.add("detected");
            resultBadge.textContent = "🚨 KNOWN SIGNATURE MATCH";
        } else if (result.signatureMatched === false) {
            resultBadge.classList.add("unknown");
            resultBadge.textContent = "✅ NO KNOWN MATCH";
        } else {
            resultBadge.classList.add("unknown");
            resultBadge.textContent = "⚠️ SCAN STATUS UNKNOWN";
        }
    }

    if (resultWarning) {
        if (result.signatureMatched === true) {
            resultWarning.textContent =
                "🚨 Warning: A known signature matched. Treat this file as suspicious.";

            resultWarning.style.borderColor = "#f87171";
            resultWarning.style.background =
                "rgba(248, 113, 113, 0.12)";
            resultWarning.style.color = "#fca5a5";
        } else if (result.signatureMatched === false) {
            resultWarning.textContent =
                "✅ No known signature matched. This does not prove the file is safe.";

            resultWarning.style.borderColor = "#34d399";
            resultWarning.style.background =
                "rgba(52, 211, 153, 0.10)";
            resultWarning.style.color = "#6ee7b7";
        } else {
            resultWarning.textContent =
                "⚠️ The scan result is uncertain. Review the scan status before trusting this file.";

            resultWarning.style.borderColor = "#fbbf24";
            resultWarning.style.background =
                "rgba(251, 191, 36, 0.10)";
            resultWarning.style.color = "#fcd34d";
        }

        resultWarning.hidden = false;
    }

    if (results) {
        results.hidden = false;
    }
}



function displayEmptyHistory(message) {
    if (!historyBody) return;

    historyBody.replaceChildren();

    const row = document.createElement("tr");
    const cell = addCell(row, message);

    cell.colSpan = 4;
    cell.classList.add("empty-history");

    historyBody.appendChild(row);
}

function displayHistory(scans) {
    if (!historyBody) return;

    historyBody.replaceChildren();

    if (totalScans) {
        totalScans.textContent = scans.length;
    }

    const matches = scans.filter(
        scan => scan.outcome === "MATCHED"
    ).length;

    const noMatches = scans.filter(
        scan => scan.outcome === "NO_MATCH"
    ).length;

    if (matchedScans) {
        matchedScans.textContent = matches;
    }

    if (unmatchedScans) {
        unmatchedScans.textContent = noMatches;
    }

    if (scans.length === 0) {
        displayEmptyHistory("📭 No scan records yet.");
        return;
    }

    scans.forEach(scan => {
        const row = document.createElement("tr");

        addCell(row, formatTimestamp(scan.scannedAt));
        addCell(row, scan.fileName || "Unknown");
        addCell(row, formatSize(Number(scan.sizeBytes) || 0));

        const outcomeCell = addCell(row, "");
        const outcome = document.createElement("span");

        outcome.classList.add("history-badge");

        if (scan.outcome === "MATCHED") {
            outcome.classList.add("detected");
            outcome.textContent = "🚨 MATCHED";
        } else if (scan.outcome === "NO_MATCH") {
            outcome.classList.add("unknown");
            outcome.textContent = "✅ NO MATCH";
        } else {
            outcome.classList.add("unknown");
            outcome.textContent =
                `⚠️ ${scan.outcome || "UNKNOWN"}`;
        }

        outcomeCell.appendChild(outcome);
        historyBody.appendChild(row);
    });
}

async function loadHistory() {
    if (historyRequestInProgress) return;

    historyRequestInProgress = true;

    if (refreshHistory) {
        refreshHistory.disabled = true;
    }

    setDashboardStatus("🔄 Loading scan history...");

    try {
        const response = await fetch("/api/history", {
            method: "GET",
            headers: {
                Accept: "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(
                `Unable to load history (HTTP ${response.status}).`
            );
        }

        const scans = await readJsonResponse(response);

        if (!Array.isArray(scans)) {
            throw new Error("The history response was not a list.");
        }

        displayHistory(scans);

        setDashboardStatus(
            `✅ Loaded ${scans.length} recent scan record(s).`
        );
    } catch (error) {
        if (totalScans) totalScans.textContent = "—";
        if (matchedScans) matchedScans.textContent = "—";
        if (unmatchedScans) unmatchedScans.textContent = "—";

        displayEmptyHistory(
            "⚠️ Unable to load scan history. Try refreshing."
        );

        setDashboardStatus(
            `⚠️ ${error.message || "Unable to load scan history."}`
        );

        console.error("History loading error:", error);
    } finally {
        historyRequestInProgress = false;

        if (refreshHistory) {
            refreshHistory.disabled = false;
        }
    }
}

if (refreshHistory) {
    refreshHistory.addEventListener("click", loadHistory);
}



if (form) {
    form.addEventListener("submit", async event => {
        event.preventDefault();

        const file = fileInput?.files?.[0];
        const validationError = validateFile(file);

        if (validationError) {
            setStatus(`⚠️ ${validationError}`, "error");
            return;
        }

        const data = new FormData();
        data.append("file", file);

        setButtonBusy(true);
        setStatus("🔍 Inspecting and hashing your file...");

        if (results) {
            results.hidden = true;
        }

        try {
            const response = await fetch("/api/scan", {
                method: "POST",
                body: data,
                headers: {
                    Accept: "application/json"
                }
            });

            const result = await readJsonResponse(response);

            if (!response.ok) {
                throw new Error(
                    result.error || `Scan failed (HTTP ${response.status}).`
                );
            }

            displayScanResult(result);

            setStatus("✅ Scan completed.", "success");

            // Refresh the history after a successful scan.
            await loadHistory();
        } catch (error) {
            setStatus(
                `❌ ${error.message || "Unable to contact the scanner."}`,
                "error"
            );

            console.error("Scan error:", error);
        } finally {
            setButtonBusy(false);
        }
    });
}


setButtonBusy(false);
loadHistory();
