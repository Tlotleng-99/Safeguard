const form = document.getElementById("scanForm");
const fileInput = document.getElementById("fileInput");
const dropArea = document.getElementById("dropArea");
const fileLabel = document.getElementById("fileLabel");
const fileHint = document.getElementById("fileHint");
const scanButton = document.getElementById("scanButton");
const statusMessage = document.getElementById("statusMessage");
const results = document.getElementById("results");

const MAX_SIZE = 10 * 1024 * 1024;

function selectFile(file) {
    if (!file) return;

    if (file.size > MAX_SIZE) {
        fileInput.value = "";
        fileLabel.textContent = "Choose a file to scan";
        fileHint.textContent = "Click here or drag and drop a file";
        statusMessage.textContent = "The file exceeds the 10 MB limit.";
        return;
    }

    fileLabel.textContent = file.name;
    fileHint.textContent = `${(file.size / 1024).toFixed(2)} KB selected`;
    statusMessage.textContent = "";
}

fileInput.addEventListener("change", () => {
    selectFile(fileInput.files[0]);
});

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
    const file = event.dataTransfer.files[0];

    if (!file) return;

    const transfer = new DataTransfer();
    transfer.items.add(file);
    fileInput.files = transfer.files;
    selectFile(file);
});

form.addEventListener("submit", async event => {
    event.preventDefault();

    const file = fileInput.files[0];

    if (!file) {
        statusMessage.textContent = "Please select a file first.";
        return;
    }

    if (file.size === 0) {
        statusMessage.textContent = "The selected file is empty.";
        return;
    }

    if (file.size > MAX_SIZE) {
        statusMessage.textContent = "The file exceeds the 10 MB limit.";
        return;
    }

    const data = new FormData();
    data.append("file", file);

    scanButton.disabled = true;
    scanButton.textContent = "Scanning...";
    statusMessage.textContent = "Inspecting and hashing your file...";
    results.hidden = true;

    try {
        const response = await fetch("/api/scan", {
            method: "POST",
            body: data
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "The scan failed.");
        }

        document.getElementById("resultName").textContent =
            result.fileName;
        document.getElementById("resultSize").textContent =
            `${result.sizeBytes} bytes`;
        document.getElementById("resultHash").textContent =
            result.sha256;
        document.getElementById("resultMessage").textContent =
            result.message;

        const badge = document.getElementById("resultBadge");

        if (result.signatureMatched) {
            badge.textContent = "SIGNATURE MATCH";
            badge.className = "result-badge detected";
        } else {
            badge.textContent = "NO MATCH FOUND";
            badge.className = "result-badge clear";
        }

        results.hidden = false;
        statusMessage.textContent = "Scan completed.";
    } catch (error) {
        statusMessage.textContent = error.message ||
            "Unable to contact the scanner.";
    } finally {
        scanButton.disabled = false;
        scanButton.textContent = "Scan file →";
    }
});
