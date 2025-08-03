// module.exports = {
//   testEnvironment: 'jsdom',
//   setupFilesAfterEnv: ['<rootDir>/src/setupTests.ts'],
//   collectCoverageFrom: [
//     'src/**/*.{ts,tsx}',
//     '!src/**/*.d.ts',
//     '!src/index.tsx',
//     '!src/vite-env.d.ts',
//     '!src/setupTests.ts',
//     '!src/services/__mocks__/**',
//   ],
//   coverageThreshold: {
//     global: {
//       branches: 100,
//       functions: 100,
//       lines: 100,
//       statements: 100,
//     },
//   },
//   coverageReporters: ['text', 'lcov', 'html'],
//   testMatch: [
//     '<rootDir>/src/**/__tests__/**/*.{ts,tsx}',
//     '<rootDir>/src/**/*.{test,spec}.{ts,tsx}',
//   ],
//   moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json'],
//   preset: 'ts-jest',
//   transform: {
//     '^.+\\.(ts|tsx)$': ['ts-jest', {
//       tsconfig: {
//         jsx: 'react-jsx',
//       },
//     }],
//   },
//   transformIgnorePatterns: [
//     'node_modules/(?!(.*\\.mjs$))',
//   ],
//   moduleNameMapper: {
//     '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
//   },
//   testEnvironmentOptions: {
//     customExportConditions: ['node', 'node-addons'],
//   },
//   globals: {
//     'import.meta': {
//       env: {
//         VITE_API_BASE_URL: 'http://localhost',
//         VITE_DRUGS_SERVICE_URL: 'http://localhost:8081',
//         VITE_SUBSCRIPTION_SERVICE_URL: 'http://localhost:8082',
//         VITE_REFILL_SERVICE_URL: 'http://localhost:8083',
//         VITE_SWAGGER_URL: 'http://localhost:8085',
//       },
//     },
//   },
// };