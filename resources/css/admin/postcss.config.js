module.exports = {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
    'postcss-import': {},
    ...(process.env.NODE_ENV === 'production' ? {
      'postcss-hash': {
        algorithm: 'sha256',
        trim: 20,
        manifest: 'target/public/css/admin/manifest.json'
      }
    } : {}),
  },
}
