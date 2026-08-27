import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyCoGrfNU_aSqPWpJ7VExRTpfLnmNqaqDWY",
  authDomain: "oc-monitoring-6c327.firebaseapp.com",
  projectId: "oc-monitoring-6c327",
  storageBucket: "oc-monitoring-6c327.firebasestorage.app",
  messagingSenderId: "1073747195659",
  appId: "1:1073747195659:web:87788ae0d6d42fc864940e" // App ID for web is usually different, but I'll use this as a base
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
export const storage = getStorage(app);
