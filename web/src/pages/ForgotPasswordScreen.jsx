import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Mail } from 'lucide-react';
import AppTextField from '../components/AppTextField';
import GradientButton from '../components/GradientButton';

const ForgotPasswordScreen = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');

  const handleVerify = () => {
    // Simulated verification
    if (email) {
      navigate('/reset-password');
    }
  };

  return (
    <div className="min-h-screen p-8 max-w-lg mx-auto">
      <button onClick={() => navigate(-1)} className="mb-8">
        <ChevronLeft className="w-6 h-6" />
      </button>

      <h2 className="text-3xl font-bold mb-2">Forgot Password</h2>
      <p className="text-textGray mb-8">Enter your email to reset your password</p>

      <AppTextField
        label="Email Address"
        value={email}
        onChange={setEmail}
        placeholder="name@example.com"
        type="email"
        icon={Mail}
      />

      <GradientButton
        text="Verify Email"
        className="mt-12"
        onClick={handleVerify}
      />

      <div className="mt-8 text-center">
        <button onClick={() => navigate(-1)} className="text-accentTeal font-bold">
          Back to Login
        </button>
      </div>
    </div>
  );
};

export default ForgotPasswordScreen;
