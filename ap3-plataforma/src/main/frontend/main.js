import './css/main.css'
import htmx from 'htmx.org'
import './js/csrf.js'
import './js/feedback.js'
import './js/confirm.js'
import './js/cnpj-mask.js'
import './js/tooltip.js'

// htmx em modo estrito, compatível com a Content-Security-Policy do servidor: sem eval, sem
// <script> vindo nas respostas e sem <style> inline para os indicadores (estão no CSS).
// O histórico não guarda cópia das páginas no navegador, que poderia conter dados de
// pacientes; voltar uma página recarrega do servidor.
Object.assign(htmx.config, {
  allowEval: false,
  allowScriptTags: false,
  includeIndicatorStyles: false,
  historyCacheSize: 0,
  refreshOnHistoryMiss: true,
})
