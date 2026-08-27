import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Check } from 'lucide-react';
import { ViewModelContext } from '../App';

const ScheduleScreen = () => {
  const navigate = useNavigate();
  const { prescriptions, todayReminders, markReminderDone } = useContext(ViewModelContext);

  const now = new Date();
  const todayStr = now.toISOString().split('T')[0];

  const items = [];
  prescriptions.forEach(p => {
    if (now.getTime() >= p.startDate && now.getTime() <= p.endDate) {
      p.times.forEach(t => {
        const [h, m] = t.split(':').map(Number);
        items.push({
          timeStr: t,
          hour: h,
          minute: m,
          prescription: p
        });
      });
    }
  });

  items.sort((a, b) => (a.hour * 60 + a.minute) - (b.hour * 60 + b.minute));

  const formatTime = (h, m) => {
    const displayH = h > 12 ? h - 12 : h === 0 ? 12 : h;
    const ampm = h >= 12 ? 'PM' : 'AM';
    return `${displayH}:${m.toString().padStart(2, '0')} ${ampm}`;
  };

  return (
    <div className="min-h-screen p-6 max-w-2xl mx-auto">
      <div className="flex items-center mb-8">
        <button onClick={() => navigate(-1)} className="mr-4">
          <ChevronLeft className="w-6 h-6" />
        </button>
        <h1 className="text-2xl font-bold">Today's Schedule</h1>
      </div>

      <div className="space-y-4">
        {items.length === 0 ? (
          <p className="text-textGray text-center mt-20">No reminders scheduled for today.</p>
        ) : (
          items.map((item, idx) => {
            const isDone = todayReminders.some(r =>
              r.prescriptionId === item.prescription.id &&
              r.timeString === item.timeStr &&
              r.dateString === todayStr &&
              r.status === 'Completed'
            );

            return (
              <div key={idx} className="card-style p-6 flex justify-between items-center">
                <div className="flex-1">
                  <h3 className={`text-2xl font-bold ${isDone ? 'text-textGray' : 'text-white'}`}>
                    {formatTime(item.hour, item.minute)}
                  </h3>
                  <p className={`text-lg mt-1 ${isDone ? 'text-textGray' : 'text-white'}`}>
                    {item.prescription.medicineName}
                  </p>
                  <p className="text-textGray text-sm">{item.prescription.dosage}</p>
                </div>

                <button
                  onClick={() => !isDone && markReminderDone(item.prescription.id, item.prescription.medicineName, item.timeStr)}
                  disabled={isDone}
                  className={`h-12 px-6 rounded-xl flex items-center justify-center font-bold ${isDone ? 'bg-successGreen/20 text-successGreen' : 'bg-accentBlue text-white'}`}
                >
                  {isDone ? (
                    <>
                      <Check className="w-5 h-5 mr-2" />
                      Done
                    </>
                  ) : (
                    'Mark Done'
                  )}
                </button>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};

export default ScheduleScreen;
