// Genera el banner PNG para Android TV (640x360px = 320x180dp a xhdpi)
import sharp from '../../turnos-saas/node_modules/sharp/lib/index.js'
import { writeFileSync, mkdirSync } from 'fs'
import { join, dirname } from 'path'
import { fileURLToPath } from 'url'

const __dirname = dirname(fileURLToPath(import.meta.url))
const root = join(__dirname, '..')

const W = 640, H = 360

const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${W}" height="${H}" viewBox="0 0 ${W} ${H}">
  <!-- Fondo púrpura -->
  <rect width="${W}" height="${H}" fill="#1a0533"/>

  <!-- Degradado sutil -->
  <defs>
    <linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#2a0548"/>
      <stop offset="100%" stop-color="#1a0533"/>
    </linearGradient>
  </defs>
  <rect width="${W}" height="${H}" fill="url(#bg)"/>

  <!-- Ícono calendario (lado izquierdo) -->
  <!-- Marco -->
  <rect x="60" y="108" width="130" height="130" rx="18"
        fill="none" stroke="#d8b15f" stroke-width="7"/>
  <!-- Argolla izquierda -->
  <rect x="85" y="88" width="22" height="38" rx="11" fill="#d8b15f"/>
  <!-- Argolla derecha -->
  <rect x="143" y="88" width="22" height="38" rx="11" fill="#d8b15f"/>
  <!-- Arco del reloj -->
  <path d="M 83 208 A 52 52 0 0 1 125 95 A 52 52 0 0 1 167 115"
        fill="none" stroke="#d8b15f" stroke-opacity="0.85" stroke-width="6"
        stroke-linecap="round"/>
  <!-- Checkmark -->
  <polyline points="85,192 108,215 158,165"
            fill="none" stroke="#d8b15f" stroke-width="10"
            stroke-linecap="round" stroke-linejoin="round"/>

  <!-- Línea separadora dorada -->
  <rect x="218" y="75" width="3" height="210" rx="1.5" fill="#d8b15f" fill-opacity="0.6"/>

  <!-- Texto "vetturno" -->
  <text x="238" y="195"
        font-family="Georgia, serif"
        font-size="82"
        font-weight="700"
        fill="#d8b15f"
        letter-spacing="-1">vetturno</text>

  <!-- Texto "TV" pequeño -->
  <text x="604" y="278"
        font-family="Arial, sans-serif"
        font-size="38"
        font-weight="400"
        fill="#d8b15f"
        fill-opacity="0.75"
        text-anchor="end"
        letter-spacing="4">TV</text>

  <!-- Línea dorada decorativa abajo -->
  <rect x="238" y="212" width="360" height="2" rx="1" fill="#d8b15f" fill-opacity="0.3"/>
</svg>`

const densities = [
  { folder: 'drawable-xhdpi',  scale: 1 },    // 640x360
  { folder: 'drawable-xxhdpi', scale: 1.5 },  // 960x540
]

for (const { folder, scale } of densities) {
  const w = Math.round(W * scale), h = Math.round(H * scale)
  const scaledSvg = svg.replace(`width="${W}" height="${H}" viewBox`, `width="${w}" height="${h}" viewBox`)
  const outDir = join(root, 'app', 'src', 'main', 'res', folder)
  mkdirSync(outDir, { recursive: true })
  await sharp(Buffer.from(scaledSvg)).resize(w, h).png().toFile(join(outDir, 'tv_banner.png'))
  console.log(`Banner generado: ${folder}/tv_banner.png (${w}x${h}px)`)
}
