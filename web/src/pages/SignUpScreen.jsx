import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, User, Mail, Lock } from 'lucide-react';
import { ViewModelContext } from '../App';
import AppTextField from '../components/AppTextField';
import GradientButton from '../components/GradientButton';

const SignUpScreen = () => {
  const navigate = useNavigate();
  const { signUp, authError } = useContext(ViewModelContext);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSignUp = () => {
    if (name && email && password) {
      signUp(name, email, password, () => navigate('/home'));
    }
  };

  return (
    <div className="min-h-screen p-8 max-w-lg mx-auto">
      <button onClick={() => navigate(-1)} className="mb-8 text-white">
        <ChevronLeft className="w-6 h-6" />
      </button>

      <h2 className="text-3xl font-bold mb-2">Create Account</h2>
      <p className="text-textGray mb-8">Start your health journey today</p>

      {authError && (
        <div className="bg-errorRed/20 text-errorRed p-4 rounded-xl mb-6 text-sm">
          {authError}
        </div>
      )}

      <div className="space-y-4">
        <AppTextField
          label="Full Name"
          value={name}
          onChange={setName}
          placeholder="Enter your name"
          icon={User}
        />
        <AppTextField
          label="Email Address"
          value={email}
          onChange={setEmail}
          placeholder="name@example.com"
          type="email"
          icon={Mail}
        />
        <AppTextField
          label="Password"
          value={password}
          onChange={setPassword}
          placeholder="Create a password"
          type="password"
          icon={Lock}
        />
      </div>

      <GradientButton
        text="Sign Up"
        className="mt-12"
        onClick={handleSignUp}
      />

      <div className="mt-8 text-center">
        <span className="text-textGray">Already have an account? </span>
        <button onClick={() => navigate('/signin')} className="text-accentTeal font-bold">
          Sign In
        </button>
      </div>
    </div>
  );
};

export default SignUpScreen;
