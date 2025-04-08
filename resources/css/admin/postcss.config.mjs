export default {
  plugins: {
    '@tailwindcss/postcss': {},
    ...(process.env.NODE_ENV === 'production' ? {
      'postcss-hash': {
        algorithm: 'sha256',
        trim: 20,
        manifest: 'target/public/css/admin/manifest.json'
      }
    } : {}),
  },
}
