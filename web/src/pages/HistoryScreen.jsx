import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, ChevronRight, Activity, Scale, FileText, CheckCircle, History } from 'lucide-react';
import { ViewModelContext } from '../App';

const HistoryScreen = () => {
  const navigate = useNavigate();
  const { historyLogs, clearHistory } = useContext(ViewModelContext);

  const todayStr = new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  const todayLogs = historyLogs.filter(log => log.date === todayStr);

  const days = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'];

  // Static Calendar for August 2026 as per Android App
  const renderCalendar = () => {
    const cells = [];
    for (let i = 0; i < 35; i++) {
      const day = i - 5;
      if (day >= 1 && day <= 31) {
        const isToday = day === 25;
        cells.push(
          <div
            key={i}
            className={`w-9 h-9 flex items-center justify-center rounded-lg text-sm ${isToday ? 'bg-accentBlue' : ''}`}
          >
            {day}
          </div>
        );
      } else {
        cells.push(<div key={i} className="w-9 h-9" />);
      }
    }
    return cells;
  };

  const getIcon = (type) => {
    switch(type) {
      case 'Pain Tracked': return Activity;
      case 'Weight Logged': return Scale;
      case 'Prescription Uploaded':
      case 'Prescription Added': return FileText;
      case 'Medicine Taken': return CheckCircle;
      default: return History;
    }
  };

  return (
    <div className="min-h-screen p-6 max-w-2xl mx-auto pb-24">
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center">
          <button onClick={() => navigate(-1)} className="mr-4">
            <ChevronLeft className="w-6 h-6" />
          </button>
          <div>
            <h1 className="text-2xl font-bold leading-none">History</h1>
            <span className="text-textGray text-xs">Activity and completion log</span>
          </div>
        </div>
        {historyLogs.length > 0 && (
          <button onClick={clearHistory} className="text-errorRed font-bold text-sm">Clear All</button>
        )}
      </div>

      <div className="card-style p-4 mb-8">
        <div className="flex justify-between items-center mb-6 px-2">
          <ChevronLeft className="w-5 h-5" />
          <span className="font-bold">August 2026</span>
          <ChevronRight className="w-5 h-5" />
        </div>
        <div className="grid grid-cols-7 gap-y-2 text-center">
          {days.map(d => <span key={d} className="text-textGray text-xs mb-2">{d}</span>)}
          {renderCalendar()}
        </div>
        <div className="flex space-x-6 mt-6 px-2">
          <div className="flex items-center space-x-2">
            <div className="w-3 h-3 rounded-full bg-accentTeal" />
            <span className="text-xs text-textGray">Completed</span>
          </div>
          <div className="flex items-center space-x-2">
            <div className="w-3 h-3 rounded-full bg-accentBlue" />
            <span className="text-xs text-textGray">Today</span>
          </div>
        </div>
      </div>

      <h3 className="text-xl font-bold mb-6">Recent Activity</h3>

      {todayLogs.length === 0 ? (
        <div className="card-style h-40 flex items-center justify-center text-textGray">
          No activities logged today.
        </div>
      ) : (
        <div className="space-y-4">
          <span className="text-accentTeal text-sm font-bold block mb-2">{todayStr}</span>
          {todayLogs.map((log, idx) => {
            const Icon = getIcon(log.type);
            return (
              <div key={idx} className="card-style p-4 flex items-center">
                <div className="w-10 h-10 rounded-full bg-accentTeal/20 flex items-center justify-center mr-4">
                  <Icon className="w-5 h-5 text-accentTeal" />
                </div>
                <div>
                  <h5 className="font-bold">{log.type}</h5>
                  <p className="text-textGray text-xs">{log.detail}</p>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default HistoryScreen;
