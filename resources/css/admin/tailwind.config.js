module.exports = {
  content: [
    "./src/**/*.{clj,cljs}",
    "./node_modules/flowbite/**/*.{js,css}"
  ],
  theme: {
    extend: {
      fontFamily: {
        'app-sans': ['avenir next', 'avenir', 'sans-serif'],
        'app-serif': ['baskerville', 'georgia', 'sans-serif'],
        'app-mono': ['Consolas', 'Monaco', 'monospace']
      }
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
    require('flowbite/plugin')
  ],
}
