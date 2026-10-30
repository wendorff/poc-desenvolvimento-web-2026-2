import htmx from 'htmx.org'

const SUCCESS_TIMEOUT_MS = 6000

const ERROR_MESSAGES = {
  403: 'Você não tem permissão para esta ação.',
  404: 'O registro não foi encontrado. Ele pode ter sido excluído por outro usuário.',
}

/** Aviso de erro: fica na tela até o usuário fechar. */
function showError(message) {
  const template = document.getElementById('toast-error-template')
  const region = document.getElementById('toast-region')
  if (!template || !region) {
    return
  }
  const toast = template.content.firstElementChild.cloneNode(true)
  toast.querySelector('.toast-message').textContent = message
  region.append(toast)
}

// Aviso de sucesso some sozinho, mas espera enquanto o mouse ou o foco estiver nele
function scheduleDismiss(toast) {
  setTimeout(() => {
    if (toast.matches(':hover, :focus-within')) {
      scheduleDismiss(toast)
    } else {
      toast.remove()
    }
  }, SUCCESS_TIMEOUT_MS)
}

htmx.onLoad((element) => {
  if (element.matches('[data-autodismiss]')) {
    scheduleDismiss(element)
  }
  element.querySelectorAll('[data-autodismiss]').forEach(scheduleDismiss)
})

document.addEventListener('click', (event) => {
  const close = event.target.closest('[data-dismiss="toast"]')
  if (close) {
    close.closest('.toast')?.remove()
  }
})

// Navegação (hx-boost) que termina em erro mostra a página de erro do servidor, como
// aconteceria sem JavaScript, em vez de deixar a tela anterior parada
document.addEventListener('htmx:beforeSwap', (event) => {
  if (event.detail.boosted && event.detail.xhr.status >= 400) {
    event.detail.shouldSwap = true
    event.detail.isError = false
  }
})

document.addEventListener('htmx:responseError', (event) => {
  const status = event.detail.xhr.status
  showError(ERROR_MESSAGES[status]
    ?? `O servidor não concluiu a operação (erro ${status}). Tente novamente.`)
})

document.addEventListener('htmx:sendError', () => {
  showError('Sem comunicação com o servidor. Verifique a conexão e tente novamente.')
})
