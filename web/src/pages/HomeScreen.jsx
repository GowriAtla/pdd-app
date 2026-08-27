import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  User,
  FileText,
  Calendar,
  Activity,
  TrendingUp,
  History
} from 'lucide-react';
import { ViewModelContext } from '../App';
import BottomNav from '../components/BottomNav';

const HomeScreen = () => {
  const navigate = useNavigate();
  const {
    userName,
    nextReminderMedicine,
    nextReminderText
  } = useContext(ViewModelContext);

  const dayName = new Date().toLocaleDateString('en-US', { weekday: 'long' });

  const quickAccess = [
    { title: 'Prescription', icon: FileText, color: 'bg-blue-500', route: '/prescription' },
    { title: 'Schedule', icon: Calendar, color: 'bg-purple-500', route: '/schedule' },
    { title: 'Pain Tracker', icon: Activity, color: 'bg-pink-500', route: '/pain-tracker' },
    { title: 'Progress', icon: TrendingUp, color: 'bg-teal-500', route: '/progress' },
    { title: 'History', icon: History, color: 'bg-orange-500', route: '/history' },
  ];

  return (
    <div className="min-h-screen pb-24 p-6 max-w-2xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h2 className="text-accentTeal font-semibold">Happy {dayName}!</h2>
          <h1 className="text-3xl font-bold">Hi, {userName || 'User'}!</h1>
        </div>
        <button
          onClick={() => navigate('/profile')}
          className="w-12 h-12 bg-white/10 rounded-full flex items-center justify-center"
        >
          <User className="text-white" />
        </button>
      </div>

      <div className="card-style p-6 mb-8">
        <div className="flex justify-between items-start mb-6">
          <div className="flex-1">
            <span className="text-textGray text-sm">Next Reminder</span>
            <h3 className="text-2xl font-bold mt-1">
              {nextReminderMedicine || 'No Meds'}
            </h3>
            <span className="text-accentTeal text-sm">{nextReminderText}</span>
          </div>
        </div>
        <button
          onClick={() => navigate('/schedule')}
          className="w-full bg-accentTeal h-12 rounded-xl font-bold flex items-center justify-center"
        >
          View Schedule
        </button>
      </div>

      <h3 className="text-xl font-bold mb-6">Quick Access</h3>
      <div className="grid grid-cols-3 gap-4">
        {quickAccess.map((item) => (
          <button
            key={item.title}
            onClick={() => navigate(item.route)}
            className="card-style flex flex-col items-center justify-center p-4 aspect-square"
          >
            <div className={`p-3 rounded-xl ${item.color} bg-opacity-20 mb-3`}>
              <item.icon className={`w-6 h-6 ${item.color.replace('bg-', 'text-')}`} />
            </div>
            <span className="text-xs text-center font-medium">{item.title}</span>
          </button>
        ))}
      </div>

      <BottomNav />
    </div>
  );
};

export default HomeScreen;
