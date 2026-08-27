import React from 'react';
import { useNavigate } from 'react-router-dom';
import GradientButton from '../components/GradientButton';

const WelcomeScreen = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col p-8 items-center justify-between">
      <div className="flex-1 flex flex-col items-center justify-center">
        <div className="w-24 h-24 bg-primary-gradient rounded-3xl flex items-center justify-center mb-8 shadow-2xl">
          <svg className="w-12 h-12 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
          </svg>
        </div>
        <h1 className="text-4xl font-bold mb-4 text-center">Stay On Track</h1>
        <p className="text-textGray text-center max-w-xs">
          Your personalized health companion for prescriptions and daily wellness.
        </p>
      </div>

      <div className="w-full max-w-md space-y-4">
        <GradientButton
          text="Get Started"
          onClick={() => navigate('/signup')}
        />
        <button
          onClick={() => navigate('/signin')}
          className="w-full text-center text-accentTeal font-medium py-2"
        >
          Already have an account? Sign In
        </button>
      </div>
    </div>
  );
};

export default WelcomeScreen;
