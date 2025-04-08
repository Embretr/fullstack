import { createI18n } from 'vue-i18n';

// Import locale messages
import enMessages from './locales/en.json';
import noMessages from './locales/no.json';

// Function to detect browser language
const getInitialLocale = (): string => {
    const browserLang = navigator.language.split('-')[0]; // Get base language (e.g., 'en' from 'en-US')
    const supportedLangs = ['en', 'no'];
    return supportedLangs.includes(browserLang) ? browserLang : 'en'; // Default to 'en'
};

// Create i18n instance
const i18n = createI18n({
    legacy: false, // Use Composition API mode
    locale: getInitialLocale(), // Set initial locale based on browser
    fallbackLocale: 'en', // Fallback locale
    messages: {
        en: enMessages,
        no: noMessages,
    },
    // Suppress warnings about missing translations during development if needed
    // silentTranslationWarn: true,
    // silentFallbackWarn: true,
});

export default i18n; 