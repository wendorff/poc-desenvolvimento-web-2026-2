// Envia o token CSRF do Spring Security nas requisições do htmx (ex.: hx-delete)
document.addEventListener('htmx:configRequest', (event) => {
  const header = document.querySelector('meta[name="csrf-header"]')
  const token = document.querySelector('meta[name="csrf-token"]')
  if (header && token) {
    event.detail.headers[header.content] = token.content
  }
})
