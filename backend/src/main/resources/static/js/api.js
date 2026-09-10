// Small shared fetch() wrapper used by every page.
const Api = {
    async get(url) {
        const res = await fetch(url);
        return Api.handle(res);
    },

    async postJson(url, body) {
        const res = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });
        return Api.handle(res);
    },

    async postFormData(url, formData) {
        const res = await fetch(url, {
            method: "POST",
            body: formData
            // No Content-Type header here on purpose: the browser sets the
            // correct multipart/form-data boundary automatically when the
            // body is a FormData object.
        });
        return Api.handle(res);
    },

    async handle(res) {
        if (!res.ok) {
            let message = `Request failed (${res.status})`;
            try {
                const errorBody = await res.json();
                if (errorBody.message) {
                    message = errorBody.message;
                }
            } catch (e) {
                // response wasn't JSON; keep the generic message
            }
            throw new Error(message);
        }
        if (res.status === 204) {
            return null;
        }
        return res.json();
    }
};
