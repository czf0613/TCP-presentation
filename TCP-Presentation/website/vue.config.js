module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:12345/api',
                secure: false,
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                }
            }
        }
    }
}