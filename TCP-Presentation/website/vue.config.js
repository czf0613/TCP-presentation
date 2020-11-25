module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:10086/api',
                secure: false,
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                }
            }
        }
    }
}