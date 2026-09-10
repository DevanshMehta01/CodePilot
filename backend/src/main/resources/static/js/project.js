const projectId = new URLSearchParams(window.location.search).get("id");

const errorBoxEl = document.getElementById("error-box");
const fileListEl = document.getElementById("file-list");
const chatLogEl = document.getElementById("chat-log");
const indexStatusEl = document.getElementById("index-status");
const indexBtnEl = document.getElementById("index-btn");

const FILE_ICON = `<svg viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.4"><path d="M4 1.5h5L12.5 5v9.5a.5.5 0 0 1-.5.5H4a.5.5 0 0 1-.5-.5v-12a.5.5 0 0 1 .5-.5Z"/><path d="M9 1.5V5h3.5"/></svg>`;

function showError(message) {
    errorBoxEl.textContent = message;
    errorBoxEl.style.display = "flex";
}

function clearError() {
    errorBoxEl.style.display = "none";
}

function escapeHtml(str) {
    const div = document.createElement("div");
    div.textContent = str;
    return div.innerHTML;
}

// --- Project details ---

async function loadProject() {
    try {
        const project = await Api.get(`/api/projects/${projectId}`);
        document.getElementById("project-name").textContent = project.name;
        document.getElementById("project-description").textContent =
            project.description || "No description";
        document.getElementById("topbar-project-name").textContent = project.name;
        document.title = `CodePilot - ${project.name}`;
    } catch (err) {
        showError(err.message);
    }
}

// --- Files ---

async function loadFiles() {
    try {
        const files = await Api.get(`/api/projects/${projectId}/files`);
        renderFiles(files);
    } catch (err) {
        showError(err.message);
    }
}

function renderFiles(files) {
    if (files.length === 0) {
        fileListEl.innerHTML = `<li style="color: var(--text-faint);">No files uploaded yet</li>`;
        return;
    }
    fileListEl.innerHTML = files
        .map(f => `<li>${FILE_ICON}${escapeHtml(f.fileName)}</li>`)
        .join("");
}

document.getElementById("upload-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    clearError();

    const input = document.getElementById("file-input");
    const submitBtn = event.target.querySelector("button[type=submit]");

    if (input.files.length === 0) {
        showError("Choose at least one file to upload.");
        return;
    }

    const formData = new FormData();
    for (const file of input.files) {
        formData.append("files", file);
    }

    submitBtn.disabled = true;
    submitBtn.textContent = "Uploading\u2026";
    try {
        await Api.postFormData(`/api/projects/${projectId}/files`, formData);
        input.value = "";
        await loadFiles();
    } catch (err) {
        showError(err.message);
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = "Upload files";
    }
});

// --- Indexing ---

indexBtnEl.addEventListener("click", async () => {
    clearError();
    indexStatusEl.classList.remove("is-success");
    indexStatusEl.textContent = "Indexing\u2026";
    indexBtnEl.disabled = true;

    try {
        const result = await Api.postJson(`/api/projects/${projectId}/index`, {});
        indexStatusEl.classList.add("is-success");
        indexStatusEl.textContent =
            `Indexed ${result.filesProcessed} file(s) into ${result.chunksIndexed} chunk(s)`;
    } catch (err) {
        indexStatusEl.textContent = "";
        showError(err.message);
    } finally {
        indexBtnEl.disabled = false;
    }
});

// --- Chat ---

function appendUserMessage(text) {
    const div = document.createElement("div");
    div.className = "msg msg-user";
    div.innerHTML = `<div class="msg-bubble">${escapeHtml(text)}</div>`;
    chatLogEl.appendChild(div);
    chatLogEl.scrollTop = chatLogEl.scrollHeight;
    return div;
}

function appendPendingBotMessage() {
    const div = document.createElement("div");
    div.className = "msg msg-bot is-pending";
    div.innerHTML = `
        <div class="msg-label">CodePilot</div>
        <div class="msg-text">Thinking\u2026</div>
    `;
    chatLogEl.appendChild(div);
    chatLogEl.scrollTop = chatLogEl.scrollHeight;
    return div;
}

function fillBotMessage(el, text, sources) {
    el.classList.remove("is-pending");

    let sourcesHtml = "";
    if (sources && sources.length > 0) {
        sourcesHtml = `<div class="msg-sources">${sources
            .map(s => `<span>${escapeHtml(s.fileName)}</span>`)
            .join("")}</div>`;
    }

    el.innerHTML = `
        <div class="msg-label">CodePilot</div>
        <div class="msg-text">${escapeHtml(text)}</div>
        ${sourcesHtml}
    `;
    chatLogEl.scrollTop = chatLogEl.scrollHeight;
}

document.getElementById("chat-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    clearError();

    const input = document.getElementById("chat-input");
    const askBtn = event.target.querySelector("button[type=submit]");
    const question = input.value.trim();
    if (!question) return;

    appendUserMessage(question);
    input.value = "";
    input.disabled = true;
    askBtn.disabled = true;

    const pendingEl = appendPendingBotMessage();

    try {
        const response = await Api.postJson(`/api/projects/${projectId}/chat`, { question });
        fillBotMessage(pendingEl, response.answer, response.sources);
    } catch (err) {
        pendingEl.remove();
        showError(err.message);
    } finally {
        input.disabled = false;
        askBtn.disabled = false;
        input.focus();
    }
});

// --- Init ---

if (!projectId) {
    showError("No project selected.");
} else {
    loadProject();
    loadFiles();
}
