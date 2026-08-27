import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Lock } from 'lucide-react';
import AppTextField from '../components/AppTextField';
import GradientButton from '../components/GradientButton';

const ResetPasswordScreen = () => {
  const navigate = useNavigate();
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleReset = () => {
    if (password && password === confirmPassword) {
      alert("Password reset successfully!");
      navigate('/signin');
    }
  };

  return (
    <div className="min-h-screen p-8 max-w-lg mx-auto">
      <button onClick={() => navigate(-1)} className="mb-8">
        <ChevronLeft className="w-6 h-6" />
      </button>

      <h2 className="text-3xl font-bold mb-2">Reset Password</h2>
      <p className="text-textGray mb-8">Create a new secure password</p>

      <div className="space-y-4">
        <AppTextField
          label="New Password"
          value={password}
          onChange={setPassword}
          placeholder="Enter new password"
          type="password"
          icon={Lock}
        />
        <AppTextField
          label="Confirm Password"
          value={confirmPassword}
          onChange={setConfirmPassword}
          placeholder="Confirm new password"
          type="password"
          icon={Lock}
        />
      </div>

      <GradientButton
        text="Reset Password"
        className="mt-12"
        onClick={handleReset}
      />
    </div>
  );
};

export default ResetPasswordScreen;
