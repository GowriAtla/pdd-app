import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { Mail, Lock } from 'lucide-react';
import { ViewModelContext } from '../App';
import AppTextField from '../components/AppTextField';
import GradientButton from '../components/GradientButton';

const SignInScreen = () => {
  const navigate = useNavigate();
  const { signIn, authError } = useContext(ViewModelContext);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSignIn = () => {
    if (email && password) {
      signIn(email, password, () => navigate('/home'));
    }
  };

  return (
    <div className="min-h-screen p-8 max-w-lg mx-auto flex flex-col justify-center">
      <h2 className="text-3xl font-bold mb-2">Welcome Back</h2>
      <p className="text-textGray mb-8">Sign in to continue tracking</p>

      {authError && (
        <div className="bg-errorRed/20 text-errorRed p-4 rounded-xl mb-6 text-sm">
          {authError}
        </div>
      )}

      <div className="space-y-4">
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
          placeholder="Enter your password"
          type="password"
          icon={Lock}
        />
      </div>

      <button
        onClick={() => navigate('/forgot-password')}
        className="text-right w-full text-textGray text-sm mt-2 mb-8"
      >
        Forgot Password?
      </button>

      <GradientButton
        text="Sign In"
        onClick={handleSignIn}
      />

      <div className="mt-8 text-center">
        <span className="text-textGray">Don't have an account? </span>
        <button onClick={() => navigate('/signup')} className="text-accentTeal font-bold">
          Create one
        </button>
      </div>
    </div>
  );
};

export default SignInScreen;
