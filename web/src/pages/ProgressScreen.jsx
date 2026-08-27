import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, CheckCircle } from 'lucide-react';
import { ViewModelContext } from '../App';

const ProgressScreen = () => {
  const navigate = useNavigate();
  const { prescriptions, todayReminders } = useContext(ViewModelContext);

  const now = new Date();
  const todayStr = now.toISOString().split('T')[0];

  const doses = [];
  prescriptions.forEach(p => {
    if (now.getTime() >= p.startDate && now.getTime() <= p.endDate) {
      p.times.forEach(t => {
        const isCompleted = todayReminders.some(r =>
          r.prescriptionId === p.id &&
          r.timeString === t &&
          r.dateString === todayStr &&
          r.status === 'Completed'
        );
        const [h, m] = t.split(':').map(Number);
        const displayH = h > 12 ? h - 12 : h === 0 ? 12 : h;
        const ampm = h >= 12 ? 'PM' : 'AM';

        doses.push({
          timeStr: `${displayH}:${m.toString().padStart(2, '0')} ${ampm}`,
          medicine: p.medicineName,
          isCompleted,
          sortTime: h * 60 + m
        });
      });
    }
  });

  doses.sort((a, b) => a.sortTime - b.sortTime);

  const completedCount = doses.filter(d => d.isCompleted).length;
  const progress = doses.length === 0 ? 0 : (completedCount / doses.size); // wait, doses.length

  const isTodayCompleted = doses.length > 0 && doses.length === completedCount;
  const todayIndex = (new Date().getDay() + 6) % 7; // Monday = 0

  return (
    <div className="min-h-screen p-6 max-w-2xl mx-auto pb-24">
      <div className="flex items-center mb-8">
        <button onClick={() => navigate(-1)} className="mr-4">
          <ChevronLeft className="w-6 h-6" />
        </button>
        <h1 className="text-2xl font-bold">Today's Progress</h1>
      </div>

      <div className="bg-accentBlue rounded-[24px] p-6 mb-8 text-white shadow-xl shadow-accentBlue/20">
        <h4 className="font-bold">Daily Progress</h4>
        <p className="text-white/80 text-sm">{completedCount} of {doses.length} doses completed</p>

        <div className="w-full h-2 bg-white/20 rounded-full mt-4 overflow-hidden">
          <div
            className="h-full bg-white transition-all duration-500"
            style={{ width: `${doses.length === 0 ? 0 : (completedCount / doses.length) * 100}%` }}
          />
        </div>
        <div className="text-right text-xs mt-2 font-bold">
          {doses.length === 0 ? 0 : Math.round((completedCount / doses.length) * 100)}%
        </div>
      </div>

      <h3 className="text-lg font-bold mb-4">Dose Checklist</h3>
      <div className="space-y-4 mb-8">
        {doses.map((dose, idx) => (
          <div key={idx} className="card-style p-4 flex items-center">
            <div className={`w-6 h-6 rounded-full border-2 flex items-center justify-center mr-4 ${dose.isCompleted ? 'bg-successGreen border-successGreen' : 'border-textGray'}`}>
              {dose.isCompleted && <div className="w-2 h-2 bg-white rounded-full" />}
            </div>
            <div className="flex-1">
              <h5 className={`font-bold ${dose.isCompleted ? 'text-textGray' : 'text-white'}`}>{dose.timeStr}</h5>
              <p className="text-textGray text-xs">{dose.medicine}</p>
            </div>
            {dose.isCompleted && (
              <span className="bg-successGreen/10 text-successGreen px-3 py-1 rounded-lg text-xs font-bold">Done</span>
            )}
          </div>
        ))}
      </div>

      <h3 className="text-lg font-bold mb-4">This Week</h3>
      <div className="flex justify-between">
        {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map((day, idx) => {
          const isToday = idx === todayIndex;
          const isCompleted = isToday && isTodayCompleted;

          return (
            <div key={day} className="flex flex-col items-center">
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-2 ${isCompleted ? 'bg-successGreen/20' : isToday ? 'bg-accentBlue' : 'bg-white/5'}`}>
                {isCompleted && <CheckCircle className="w-5 h-5 text-successGreen" />}
              </div>
              <span className="text-xs text-textGray">{day}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ProgressScreen;
