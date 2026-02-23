module.exports = {
    testEnvironment: 'node',
    coveragePathIgnorePatterns: ['/node_modules/'],
    testMatch: ['**/tests/**/*.test.js'],
    collectCoverageFrom: [
        'services/**/*.js',
        'controllers/**/*.js',
        '!services/index.js'
    ],
    coverageThreshold: {
        global: {
            branches: 70,
            functions: 75,
            lines: 80,
            statements: 80
        }
    }
};
