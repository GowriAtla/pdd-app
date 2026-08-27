import { useState, useEffect } from 'react';
import { db } from '../services/firebase';
import {
  collection,
  addDoc,
  query,
  orderBy,
  onSnapshot,
  where,
  doc,
  deleteDoc,
  getDocs,
  Timestamp,
  serverTimestamp
} from 'firebase/firestore';

export const useAppViewModel = () => {
  const [userId, setUserId] = useState(localStorage.getItem('userId') || "");
  const [userName, setUserName] = useState(localStorage.getItem('userName') || "");
  const [userEmail, setUserEmail] = useState(localStorage.getItem('userEmail') || "");

  const [authError, setAuthError] = useState(null);
  const [prescriptionUri, setPrescriptionUri] = useState(null);
  const [isNewUpload, setIsNewUpload] = useState(false);

  const [prescriptions, setPrescriptions] = useState([]);
  const [todayReminders, setTodayReminders] = useState([]);
  const [nextReminderText, setNextReminderText] = useState("No upcoming reminders");
  const [nextReminderMedicine, setNextReminderMedicine] = useState("");

  const [painRecords, setPainRecords] = useState([]);
  const [historyLogs, setHistoryLogs] = useState([]);

  useEffect(() => {
    if (userId) {
      localStorage.setItem('userId', userId);
      const painQuery = query(collection(db, `users/${userId}/pain_records`), orderBy('timestamp', 'desc'));
      const unsubPain = onSnapshot(painQuery, (snapshot) => {
        setPainRecords(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })));
      });

      const historyQuery = query(collection(db, `users/${userId}/history_logs`), orderBy('timestamp', 'desc'));
      const unsubHistory = onSnapshot(historyQuery, (snapshot) => {
        setHistoryLogs(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })));
      });

      const prescriptionQuery = query(collection(db, `users/${userId}/prescriptions`), orderBy('timestamp', 'desc'));
      const unsubPrescription = onSnapshot(prescriptionQuery, (snapshot) => {
        const pList = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        setPrescriptions(pList);

        if (pList.length > 0 && !isNewUpload) {
          if (pList[0].prescriptionUri) {
             setPrescriptionUri(pList[0].prescriptionUri);
          }
        }
      });

      const todayStr = new Date().toISOString().split('T')[0];
      const reminderQuery = query(collection(db, `users/${userId}/reminder_events`), where('dateString', '==', todayStr));
      const unsubReminders = onSnapshot(reminderQuery, (snapshot) => {
        setTodayReminders(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })));
      });

      return () => {
        unsubPain();
        unsubHistory();
        unsubPrescription();
        unsubReminders();
      };
    }
  }, [userId, isNewUpload]);

  useEffect(() => {
    updateNextReminder();
  }, [prescriptions, todayReminders]);

  const updateNextReminder = () => {
    let nearestTime = Infinity;
    let nearestMed = "";
    let nearestStr = "";
    const now = new Date();

    const todayStr = now.toISOString().split('T')[0];
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tomorrowStr = tomorrow.toISOString().split('T')[0];

    prescriptions.forEach(p => {
      if (now.getTime() < p.startDate || now.getTime() > p.endDate) return;

      p.times.forEach(timeStr => {
        const [h, m] = timeStr.split(':').map(Number);
        const cal = new Date(now);
        cal.setHours(h, m, 0, 0);

        let isTomorrow = false;
        if (cal.getTime() <= now.getTime()) {
          cal.setDate(cal.getDate() + 1);
          isTomorrow = true;
        }

        const eventDateStr = isTomorrow ? tomorrowStr : todayStr;
        const completed = todayReminders.some(r => r.prescriptionId === p.id && r.timeString === timeStr && r.dateString === eventDateStr && r.status === 'Completed');

        if (!completed && cal.getTime() < nearestTime) {
          nearestTime = cal.getTime();
          nearestMed = p.medicineName;
          const displayH = h > 12 ? h - 12 : h === 0 ? 12 : h;
          const ampm = h >= 12 ? 'PM' : 'AM';
          nearestStr = `${displayH}:${m.toString().padStart(2, '0')} ${ampm} ${isTomorrow ? 'Tomorrow' : 'Today'}`;
        }
      });
    });

    if (nearestTime !== Infinity) {
      setNextReminderMedicine(nearestMed);
      setNextReminderText(nearestStr);
    } else {
      setNextReminderMedicine("");
      setNextReminderText("No upcoming reminders");
    }
  };

  const addPainRecord = async (level, notes) => {
    const emoji = level <= 3 ? "😊" : level <= 7 ? "😐" : "😫";
    const todayStr = new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    const record = { level, notes, date: todayStr, emoji, timestamp: Date.now() };

    await addDoc(collection(db, `users/${userId}/pain_records`), record);
    await addDoc(collection(db, `users/${userId}/history_logs`), {
      type: "Pain Tracked",
      detail: `Level ${level} (${emoji})`,
      date: todayStr,
      timestamp: Date.now()
    });
  };

  const addPrescription = (uri) => {
    setIsNewUpload(true);
    setPrescriptionUri(uri);
    const todayStr = new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    addDoc(collection(db, `users/${userId}/history_logs`), {
      type: "Prescription Uploaded",
      detail: "New image added",
      date: todayStr,
      timestamp: Date.now()
    });
  };

  const savePrescription = async (prescription) => {
    setIsNewUpload(false);
    const q = query(collection(db, `users/${userId}/prescriptions`));
    const snapshot = await getDocs(q);
    snapshot.docs.forEach(async (d) => {
      await deleteDoc(d.ref);
    });

    await addDoc(collection(db, `users/${userId}/prescriptions`), {
      ...prescription,
      timestamp: Date.now()
    });

    const todayStr = new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    await addDoc(collection(db, `users/${userId}/history_logs`), {
      type: "Schedule Updated",
      detail: `${prescription.frequency}x Daily`,
      date: todayStr,
      timestamp: Date.now()
    });
  };

  const markReminderDone = async (prescriptionId, medicineName, timeStr) => {
    const now = new Date();
    const todayStr = now.toISOString().split('T')[0];
    const displayDate = now.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });

    await addDoc(collection(db, `users/${userId}/reminder_events`), {
      prescriptionId,
      medicineName,
      timeString: timeStr,
      dateString: todayStr,
      status: "Completed",
      timestamp: Date.now()
    });

    await addDoc(collection(db, `users/${userId}/history_logs`), {
      type: "Medicine Taken",
      detail: medicineName,
      date: displayDate,
      timestamp: Date.now()
    });
  };

  const clearHistory = async () => {
    const q = query(collection(db, `users/${userId}/history_logs`));
    const snapshot = await getDocs(q);
    snapshot.docs.forEach(async (d) => {
      await deleteDoc(d.ref);
    });
  };

  const signIn = async (email, password, onSuccess) => {
    setAuthError(null);
    const q = query(collection(db, "users"), where("email", "==", email), where("password", "==", password));
    const snapshot = await getDocs(q);
    if (snapshot.empty) {
      setAuthError("Invalid email or password.");
    } else {
      const u = snapshot.docs[0];
      setUserId(u.id);
      setUserName(u.data().name);
      setUserEmail(u.data().email);
      localStorage.setItem('userName', u.data().name);
      localStorage.setItem('userEmail', u.data().email);
      onSuccess();
    }
  };

  const signUp = async (name, email, password, onSuccess) => {
    setAuthError(null);
    const q = query(collection(db, "users"), where("email", "==", email));
    const snapshot = await getDocs(q);
    if (!snapshot.empty) {
      setAuthError("User already exists. Please sign in.");
    } else {
      const docRef = await addDoc(collection(db, "users"), { name, email, password });
      setUserId(docRef.id);
      setUserName(name);
      setUserEmail(email);
      localStorage.setItem('userName', name);
      localStorage.setItem('userEmail', email);
      onSuccess();
    }
  };

  const logout = () => {
    setUserId("");
    setUserName("");
    setUserEmail("");
    localStorage.clear();
  };

  return {
    userId, userName, userEmail, authError, prescriptionUri, isNewUpload,
    prescriptions, todayReminders, nextReminderText, nextReminderMedicine,
    painRecords, historyLogs,
    addPainRecord, addPrescription, savePrescription, markReminderDone, clearHistory,
    signIn, signUp, logout
  };
};
