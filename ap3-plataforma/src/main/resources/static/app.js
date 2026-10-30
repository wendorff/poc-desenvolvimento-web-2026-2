// Envia o token CSRF do Spring Security nas requisições htmx (ex.: hx-delete)
document.addEventListener('htmx:configRequest', function (event) {
    var header = document.querySelector('meta[name="csrf-header"]');
    var token = document.querySelector('meta[name="csrf-token"]');
    if (header && token) {
        event.detail.headers[header.content] = token.content;
    }
});
