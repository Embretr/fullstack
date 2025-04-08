import { defineConfig } from 'orval';

export default defineConfig({
  marketplace: {
    output: {
      target: './src/api/generated.ts',
      client: 'vue-query',
      mode: 'tags-split',
      schemas: './src/api/model',
      mock: false,
      prettier: true,
      clean: true,
      
    },
    input: {
      target: 'http://localhost:8080/v3/api-docs',
    },
  },
});
