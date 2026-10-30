import { fileURLToPath } from 'node:url'
import { defineConfig } from 'vite'

const frontend = fileURLToPath(new URL('./src/main/frontend', import.meta.url))
const assets = fileURLToPath(new URL('./target/classes/static/assets', import.meta.url))

// O Vite empacota htmx, scripts, CSS e fontes em target/classes/static/assets, servido pelo
// Spring em /assets. Os nomes são fixos porque o Spring já acrescenta o hash do conteúdo na
// URL (spring.web.resources.chain), então o navegador nunca usa um arquivo desatualizado.
export default defineConfig({
  root: frontend,
  base: './',
  publicDir: 'public',
  build: {
    outDir: assets,
    emptyOutDir: true,
    modulePreload: false,
    rolldownOptions: {
      input: { app: `${frontend}/main.js` },
      // O eval interno do htmx só roda com htmx.config.allowEval, desligado em main.js
      onLog(level, log, handler) {
        if (log.code === 'EVAL' && log.id?.includes('htmx.org')) {
          return
        }
        handler(level, log)
      },
      output: {
        entryFileNames: '[name].js',
        chunkFileNames: '[name].js',
        assetFileNames: '[name][extname]',
      },
    },
  },
})
