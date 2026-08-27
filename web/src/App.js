import React, { createContext, useContext } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAppViewModel } from './hooks/useAppViewModel';

// Screens (To be implemented)
import WelcomeScreen from './pages/WelcomeScreen';
import SignUpScreen from './pages/SignUpScreen';
import SignInScreen from './pages/SignInScreen';
import ForgotPasswordScreen from './pages/ForgotPasswordScreen';
import ResetPasswordScreen from './pages/ResetPasswordScreen';
import HomeScreen from './pages/HomeScreen';
import PrescriptionScreen from './pages/PrescriptionScreen';
import ScheduleScreen from './pages/ScheduleScreen';
import PainTrackerScreen from './pages/PainTrackerScreen';
import ProgressScreen from './pages/ProgressScreen';
import HistoryScreen from './pages/HistoryScreen';
import ProfileScreen from './pages/ProfileScreen';

export const ViewModelContext = createContext();

const ProtectedRoute = ({ children }) => {
  const { userId } = useContext(ViewModelContext);
  if (!userId) return <Navigate to="/" replace />;
  return children;
};

function App() {
  const viewModel = useAppViewModel();

  return (
    <ViewModelContext.Provider value={viewModel}>
      <Router>
        <div className="min-h-screen bg-app-gradient text-textWhite">
          <Routes>
            <Route path="/" element={<WelcomeScreen />} />
            <Route path="/signup" element={<SignUpScreen />} />
            <Route path="/signin" element={<SignInScreen />} />
            <Route path="/forgot-password" element={<ForgotPasswordScreen />} />
            <Route path="/reset-password" element={<ResetPasswordScreen />} />

            <Route path="/home" element={<ProtectedRoute><HomeScreen /></ProtectedRoute>} />
            <Route path="/prescription" element={<ProtectedRoute><PrescriptionScreen /></ProtectedRoute>} />
            <Route path="/schedule" element={<ProtectedRoute><ScheduleScreen /></ProtectedRoute>} />
            <Route path="/pain-tracker" element={<ProtectedRoute><PainTrackerScreen /></ProtectedRoute>} />
            <Route path="/progress" element={<ProtectedRoute><ProgressScreen /></ProtectedRoute>} />
            <Route path="/history" element={<ProtectedRoute><HistoryScreen /></ProtectedRoute>} />
            <Route path="/profile" element={<ProtectedRoute><ProfileScreen /></ProtectedRoute>} />
          </Routes>
        </div>
      </Router>
    </ViewModelContext.Provider>
  );
}

export default App;
