/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        backgroundDark: '#131733',
        backgroundLight: '#202A54',
        cardBackground: '#1E2541',
        accentTeal: '#00D1C1',
        accentBlue: '#2D7FF9',
        textWhite: '#FFFFFF',
        textGray: '#B0B3C8',
        buttonSecondary: '#333A61',
        successGreen: '#4CAF50',
        errorRed: '#E91E63',
        warningOrange: '#FFFF9800',
      },
      borderRadius: {
        '2xl': '1rem',
        '3xl': '1.5rem',
      },
      backgroundImage: {
        'app-gradient': 'linear-gradient(to bottom, #131733, #202A54)',
        'primary-gradient': 'linear-gradient(to right, #2D7FF9, #00D1C1)',
      }
    },
  },
  plugins: [],
}
