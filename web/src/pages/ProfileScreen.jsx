import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, User, Mail, Logout } from 'lucide-react';
import { ViewModelContext } from '../App';
import GradientButton from '../components/GradientButton';

const ProfileScreen = () => {
  const navigate = useNavigate();
  const { userName, userEmail, logout } = useContext(ViewModelContext);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="min-h-screen p-8 max-w-2xl mx-auto flex flex-col">
      <div className="flex items-center mb-12">
        <button onClick={() => navigate(-1)} className="mr-4">
          <ChevronLeft className="w-6 h-6" />
        </button>
      </div>

      <div className="flex flex-col items-center mb-12">
        <div className="w-24 h-24 bg-white/10 rounded-full flex items-center justify-center mb-4">
          <User className="w-12 h-12 text-white" />
        </div>
        <h2 className="text-2xl font-bold">{userName}</h2>
      </div>

      <h3 className="text-xl font-bold mb-6">Personal Information</h3>

      <div className="card-style p-6 space-y-6 mb-12">
        <div className="flex items-center">
          <div className="w-10 h-10 bg-white/10 rounded-xl flex items-center justify-center mr-4">
            <User className="w-5 h-5 text-accentBlue" />
          </div>
          <div>
            <span className="text-textGray text-xs block">Full Name</span>
            <span className="font-medium">{userName}</span>
          </div>
        </div>

        <div className="w-full h-[1px] bg-white/5" />

        <div className="flex items-center">
          <div className="w-10 h-10 bg-white/10 rounded-xl flex items-center justify-center mr-4">
            <Mail className="w-5 h-5 text-accentBlue" />
          </div>
          <div>
            <span className="text-textGray text-xs block">Email Address</span>
            <span className="font-medium">{userEmail}</span>
          </div>
        </div>
      </div>

      <div className="mt-auto">
        <GradientButton
          text="Logout"
          variant="danger"
          onClick={handleLogout}
          icon={(props) => (
            <svg {...props} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" /><polyline points="16 17 21 12 16 7" /><line x1="21" y1="12" x2="9" y2="12" />
            </svg>
          )}
        />
      </div>
    </div>
  );
};

export default ProfileScreen;
