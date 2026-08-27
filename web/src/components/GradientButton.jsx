import React from 'react';

const GradientButton = ({ text, onClick, icon: Icon, fullWidth = true, variant = 'primary', className = '' }) => {
  const baseStyles = "h-14 rounded-2xl flex items-center justify-center font-bold text-lg transition-transform active:scale-95";
  const variants = {
    primary: "bg-primary-gradient text-white shadow-lg shadow-accentBlue/20",
    secondary: "bg-buttonSecondary text-white border border-white/10",
    success: "bg-successGreen text-white",
    danger: "bg-errorRed text-white"
  };

  return (
    <button
      onClick={onClick}
      className={`${baseStyles} ${variants[variant]} ${fullWidth ? 'w-full' : 'px-8'} ${className}`}
    >
      {Icon && <Icon className="mr-2 w-5 h-5" />}
      {text}
    </button>
  );
};

export default GradientButton;
