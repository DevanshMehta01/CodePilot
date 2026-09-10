const projectListEl = document.getElementById("project-list");
const errorBoxEl = document.getElementById("error-box");
const createFormEl = document.getElementById("create-project-form");

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

async function loadProjects() {
    try {
        const projects = await Api.get("/api/projects");
        renderProjects(projects);
    } catch (err) {
        showError(err.message);
    }
}

function renderProjects(projects) {
    if (projects.length === 0) {
        projectListEl.innerHTML = `
            <div class="empty-state">
                <strong>No projects yet</strong>
                Create one above, then upload source files and index it to start asking questions.
            </div>`;
        return;
    }

    const rows = projects
        .slice()
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .map(p => `
            <a class="project-row" href="project.html?id=${p.id}">
                <div class="project-row-main">
                    <div class="project-row-name">${escapeHtml(p.name)}</div>
                    <div class="project-row-desc">${escapeHtml(p.description || "No description")}</div>
                </div>
                <div class="project-row-meta">#${p.id}</div>
            </a>
        `)
        .join("");

    projectListEl.innerHTML = `<div class="project-list">${rows}</div>`;
}

createFormEl.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearError();

    const submitBtn = createFormEl.querySelector("button[type=submit]");
    const name = document.getElementById("project-name").value;
    const description = document.getElementById("project-description").value;

    submitBtn.disabled = true;
    try {
        await Api.postJson("/api/projects", { name, description });
        document.getElementById("project-name").value = "";
        document.getElementById("project-description").value = "";
        await loadProjects();
    } catch (err) {
        showError(err.message);
    } finally {
        submitBtn.disabled = false;
    }
});

loadProjects();
