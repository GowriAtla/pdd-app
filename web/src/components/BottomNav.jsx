import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Home, Activity, TrendingUp, User } from 'lucide-react';

const BottomNav = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const items = [
    { label: 'Home', icon: Home, route: '/home' },
    { label: 'Track', icon: Activity, route: '/pain-tracker' },
    { label: 'Progress', icon: TrendingUp, route: '/progress' },
    { label: 'Profile', icon: User, route: '/profile' },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-backgroundDark border-t border-white/5 h-20 flex items-center justify-around px-4 z-50">
      {items.map((item) => {
        const isActive = location.pathname === item.route;
        const Icon = item.icon;

        return (
          <button
            key={item.label}
            onClick={() => navigate(item.route)}
            className="flex flex-col items-center justify-center space-y-1"
          >
            <div className={`p-2 rounded-xl transition-colors ${isActive ? 'text-accentTeal bg-accentTeal/10' : 'text-textGray'}`}>
              <Icon className="w-6 h-6" />
            </div>
            <span className={`text-[10px] ${isActive ? 'text-accentTeal font-bold' : 'text-textGray'}`}>
              {item.label}
            </span>
          </button>
        );
      })}
    </div>
  );
};

export default BottomNav;
