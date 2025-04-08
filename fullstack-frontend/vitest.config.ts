import { defineConfig, mergeConfig } from 'vitest/config'
import viteConfig from './vite.config'

export default mergeConfig(
    viteConfig,
    defineConfig({
        test: {
            globals: true, // Make Vitest globals available (describe, it, expect, etc.)
            environment: 'jsdom', // Use jsdom for DOM simulation
            // Optional: Include setup files if needed
            setupFiles: './src/tests/setup.ts',
        },
    })
) 