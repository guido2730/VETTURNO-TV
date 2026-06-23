// Genera los mipmap PNGs del launcher icon para Android TV
import sharp from '../../turnos-saas/node_modules/sharp/lib/index.js'
import { mkdirSync } from 'fs'
import { join, dirname } from 'path'
import { fileURLToPath } from 'url'

const __dirname = dirname(fileURLToPath(import.meta.url))
const root = join(__dirname, '..')

const svgIcon = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 170 170">
  <rect width="170" height="170" rx="38" fill="#1a0533"/>
  <rect x="39" y="48" width="92" height="92" rx="22"
        fill="none" stroke="#d8b15f" stroke-width="6"/>
  <rect x="53" y="30" width="16" height="28" rx="8" fill="#d8b15f"/>
  <rect x="101" y="30" width="16" height="28" rx="8" fill="#d8b15f"/>
  <path d="M 58.4 122.6 A 39 39 0 0 1 86 56 A 39 39 0 0 1 113.6 67.4"
        fill="none" stroke="#d8b15f" stroke-opacity="0.9" stroke-width="5"
        stroke-linecap="round"/>
  <polyline points="61,110 81,129 120,90"
            fill="none" stroke="#d8b15f" stroke-width="8"
            stroke-linecap="round" stroke-linejoin="round"/>
</svg>`

const sizes = [
  { folder: 'mipmap-mdpi',    size: 48  },
  { folder: 'mipmap-hdpi',    size: 72  },
  { folder: 'mipmap-xhdpi',   size: 96  },
  { folder: 'mipmap-xxhdpi',  size: 144 },
  { folder: 'mipmap-xxxhdpi', size: 192 },
]

for (const { folder, size } of sizes) {
  const outDir = join(root, 'app', 'src', 'main', 'res', folder)
  mkdirSync(outDir, { recursive: true })
  await sharp(Buffer.from(svgIcon))
    .resize(size, size)
    .png()
    .toFile(join(outDir, 'ic_launcher.png'))
  console.log(`Generado: ${folder}/ic_launcher.png (${size}x${size}px)`)
}
