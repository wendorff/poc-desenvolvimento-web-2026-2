// Troca o confirm() do navegador pelo <dialog> do layout em todo hx-confirm. O foco começa
// em Cancelar, então Enter ou Esc por engano nunca confirmam uma exclusão.
document.addEventListener('htmx:confirm', (event) => {
  const dialog = document.getElementById('confirm-dialog')
  if (!event.detail.question || !dialog) {
    return
  }
  event.preventDefault()

  const accept = dialog.querySelector('[data-confirm-accept]')
  dialog.querySelector('#confirm-question').textContent = event.detail.question
  accept.textContent = event.detail.elt.dataset.confirmAction ?? 'Confirmar'

  dialog.returnValue = ''
  dialog.addEventListener('close', () => {
    if (dialog.returnValue === 'confirm') {
      event.detail.issueRequest(true)
    }
  }, { once: true })
  dialog.showModal()
  dialog.querySelector('[value="cancel"]').focus()
})
