import React from 'react';

const AppTextField = ({ label, value, onChange, placeholder, type = "text", icon: Icon }) => {
  return (
    <div className="w-full mb-4">
      {label && <label className="block text-textGray text-sm mb-2">{label}</label>}
      <div className="relative flex items-center">
        {Icon && <Icon className="absolute left-4 w-5 h-5 text-accentBlue" />}
        <input
          type={type}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className={`w-full h-14 bg-cardBackground rounded-2xl border border-white/10 px-4 text-white focus:outline-none focus:border-accentBlue/50 transition-colors ${Icon ? 'pl-12' : ''}`}
        />
      </div>
    </div>
  );
};

export default AppTextField;
