// Dica flutuante para elementos com data-tooltip="<id do conteúdo>". O conteúdo fica escondido
// junto do elemento (e é lido pelo leitor de tela via aria-describedby); aqui ele só é copiado
// para a dica visível. Atende WCAG 1.4.13: abre com mouse ou teclado, fecha com Esc e continua
// aberta enquanto o mouse estiver sobre ela. Usa position: fixed para não ser cortada pela
// rolagem horizontal da tabela.
const GAP = 8
const HIDE_DELAY_MS = 150

let activeTrigger = null
let hideTimer = null

function show(trigger) {
  const tooltip = document.getElementById('tooltip')
  const content = document.getElementById(trigger.dataset.tooltip)
  if (!tooltip || !content) {
    return
  }
  clearTimeout(hideTimer)
  if (trigger === activeTrigger && !tooltip.hidden) {
    return
  }
  activeTrigger = trigger
  tooltip.replaceChildren(...content.cloneNode(true).childNodes)
  tooltip.hidden = false
  place(tooltip, trigger)
}

// Acima do elemento, centralizada; abaixo se não couber; sempre dentro da janela
function place(tooltip, trigger) {
  tooltip.style.top = '0px'
  tooltip.style.left = '0px'
  const anchor = trigger.getBoundingClientRect()
  const box = tooltip.getBoundingClientRect()
  const above = anchor.top - box.height - GAP
  const top = above >= GAP ? above : anchor.bottom + GAP
  const centered = anchor.left + anchor.width / 2 - box.width / 2
  const left = Math.min(Math.max(centered, GAP), window.innerWidth - box.width - GAP)
  tooltip.style.top = `${top}px`
  tooltip.style.left = `${left}px`
}

function hide() {
  clearTimeout(hideTimer)
  const tooltip = document.getElementById('tooltip')
  if (tooltip) {
    tooltip.hidden = true
  }
  activeTrigger = null
}

function hideSoon() {
  clearTimeout(hideTimer)
  hideTimer = setTimeout(hide, HIDE_DELAY_MS)
}

// Só mouse e caneta "passam por cima". No toque o navegador dá o dedo como saído logo depois de
// levantá-lo, o que fecharia a dica; o toque é tratado pelo foco e pelo clique, mais abaixo.
const canHover = (event) => event.pointerType === 'mouse' || event.pointerType === 'pen'

document.addEventListener('pointerover', (event) => {
  if (!canHover(event)) {
    return
  }
  const trigger = event.target.closest?.('[data-tooltip]')
  if (trigger) {
    show(trigger)
  } else if (event.target.closest?.('#tooltip')) {
    clearTimeout(hideTimer)
  }
})

document.addEventListener('pointerout', (event) => {
  if (canHover(event) && event.target.closest?.('[data-tooltip], #tooltip')) {
    hideSoon()
  }
})

document.addEventListener('focusin', (event) => {
  const trigger = event.target.closest?.('[data-tooltip]')
  if (trigger) {
    show(trigger)
  }
})

document.addEventListener('focusout', (event) => {
  if (event.target.closest?.('[data-tooltip]')) {
    hide()
  }
})

// Tela de toque não tem mouse para passar por cima: tocar no elemento abre, tocar fora fecha
document.addEventListener('click', (event) => {
  const trigger = event.target.closest?.('[data-tooltip]')
  if (trigger) {
    show(trigger)
  } else if (activeTrigger && !event.target.closest?.('#tooltip')) {
    hide()
  }
})

document.addEventListener('keydown', (event) => {
  if (event.key === 'Escape' && activeTrigger) {
    hide()
  }
})

// A posição é calculada na abertura; rolar a página ou a tabela fecha a dica
document.addEventListener('scroll', () => activeTrigger && hide(), { capture: true, passive: true })
