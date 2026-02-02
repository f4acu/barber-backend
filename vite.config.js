import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'path' // Asegúrate de que esto esté aquí

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      // Esto le dice a Vite que "@" es igual a la carpeta "src"
      "@": path.resolve(__dirname, "./src"),
    },
  },
})