// Máscara de CNPJ (00.000.000/0000-00) para campos com data-mask="cnpj". Segue o formato
// alfanumérico da Receita: os 12 primeiros caracteres aceitam letras, os 2 últimos (dígitos
// verificadores) só números. A validação de verdade continua no servidor (Cnpj.java).
const SEPARATORS = { 2: '.', 5: '.', 8: '/', 12: '-' }
const SIGNIFICANT = /[0-9A-Z]/i

function formatCnpj(value) {
  const chars = []
  for (const char of value.toUpperCase()) {
    const allowed = chars.length < 12 ? /[0-9A-Z]/ : /[0-9]/
    if (chars.length < 14 && allowed.test(char)) {
      chars.push(char)
    }
  }
  return chars.map((char, index) => (SEPARATORS[index] ?? '') + char).join('')
}

document.addEventListener('input', (event) => {
  const input = event.target
  if (!(input instanceof HTMLInputElement) || input.dataset.mask !== 'cnpj') {
    return
  }
  const caret = input.selectionStart ?? input.value.length
  const typedBeforeCaret = [...input.value.slice(0, caret)].filter((char) => SIGNIFICANT.test(char)).length
  const formatted = formatCnpj(input.value)
  if (formatted === input.value) {
    return
  }
  input.value = formatted

  // Mantém o cursor depois do mesmo caractere digitado, mesmo com a pontuação inserida
  let position = 0
  for (let seen = 0; position < formatted.length && seen < typedBeforeCaret; position++) {
    if (SIGNIFICANT.test(formatted[position])) {
      seen++
    }
  }
  input.setSelectionRange(position, position)
})
