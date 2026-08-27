import React, { useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Image as ImageIcon, Plus, Save, Clock } from 'lucide-react';
import { ViewModelContext } from '../App';
import GradientButton from '../components/GradientButton';

const PrescriptionScreen = () => {
  const navigate = useNavigate();
  const { prescriptionUri, isNewUpload, addPrescription, savePrescription, prescriptions } = useContext(ViewModelContext);

  const [showForm, setShowForm] = useState(false);
  const [frequency, setFrequency] = useState(1);
  const [times, setTimes] = useState(["09:00"]);

  const defaultTimes = ["09:00", "14:00", "20:00"];

  useEffect(() => {
    const existing = prescriptions[0];
    if (existing && !isNewUpload) {
      setFrequency(existing.frequency);
      setTimes(existing.times);
      setShowForm(true);
    }
  }, [prescriptions, isNewUpload]);

  useEffect(() => {
    if (isNewUpload || prescriptions.length === 0) {
      const newTimes = Array.from({ length: frequency }, (_, i) =>
        times[i] || defaultTimes[i] || "12:00"
      );
      setTimes(newTimes);
    }
  }, [frequency, isNewUpload, prescriptions.length]);

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        addPrescription(reader.result);
        setShowForm(true);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSave = async () => {
    const start = Date.now();
    const end = start + (10 * 365 * 24 * 60 * 60 * 1000); // 10 years

    const prescription = {
      medicineName: "Prescription",
      dosage: "",
      frequency,
      times,
      startDate: start,
      endDate: end,
      notes: "",
      prescriptionUri: prescriptionUri
    };

    await savePrescription(prescription);
    navigate(-1);
  };

  const formatTime = (timeStr) => {
    const [h, m] = timeStr.split(':').map(Number);
    const displayH = h > 12 ? h - 12 : h === 0 ? 12 : h;
    const ampm = h >= 12 ? 'PM' : 'AM';
    return `${displayH}:${m.toString().padStart(2, '0')} ${ampm}`;
  };

  return (
    <div className="min-h-screen p-6 max-w-2xl mx-auto pb-24">
      <div className="flex items-center mb-8">
        <button onClick={() => navigate(-1)} className="mr-4">
          <ChevronLeft className="w-6 h-6" />
        </button>
        <h1 className="text-2xl font-bold">Upload Prescription</h1>
      </div>

      <div className="card-style aspect-[4/3] relative mb-6 flex items-center justify-center">
        {prescriptionUri ? (
          <img src={prescriptionUri} alt="Prescription" className="w-full h-full object-cover" />
        ) : (
          <div className="flex flex-col items-center">
            <ImageIcon className="w-16 h-16 text-white/10 mb-4" />
            <span className="text-textGray">No image selected</span>
          </div>
        )}
      </div>

      <div className="relative mb-8">
        <input
          type="file"
          accept="image/*"
          onChange={handleImageUpload}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
        />
        <GradientButton
          text={prescriptionUri ? "Change Image" : "Upload from Gallery"}
          icon={Plus}
          variant="primary"
          onClick={() => {}} // Controlled by input
        />
      </div>

      {showForm && (
        <div className="space-y-8">
          <div>
            <h3 className="text-xl font-bold mb-4">Prescription Details</h3>
            <span className="text-textGray text-sm block mb-4">Frequency (Times per day)</span>
            <div className="flex justify-between">
              {[1, 2, 3].map((num) => (
                <button
                  key={num}
                  onClick={() => setFrequency(num)}
                  className={`w-[30%] h-12 rounded-xl font-bold transition-colors ${frequency === num ? 'bg-accentBlue text-white' : 'bg-cardBackground text-white'}`}
                >
                  {num}
                </button>
              ))}
            </div>
          </div>

          <div>
            <h3 className="text-lg font-bold mb-4">Schedule Reminder Times</h3>
            <div className="space-y-4">
              {times.map((time, idx) => {
                const label = frequency === 1 ? 'Time' : idx === 0 ? 'Morning' : idx === 1 ? 'Afternoon' : 'Evening';
                return (
                  <div key={idx} className="flex justify-between items-center py-2 border-b border-white/5">
                    <span className="text-textGray">{label}</span>
                    <div className="flex items-center space-x-2 text-white font-bold cursor-pointer">
                      <input
                        type="time"
                        value={time}
                        onChange={(e) => {
                          const newTimes = [...times];
                          newTimes[idx] = e.target.value;
                          setTimes(newTimes);
                        }}
                        className="bg-transparent border-none text-white focus:outline-none"
                      />
                      <Clock className="w-5 h-5 text-accentBlue" />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <GradientButton
            text="Save & Schedule"
            icon={Save}
            variant="success"
            onClick={handleSave}
          />
        </div>
      )}
    </div>
  );
};

export default PrescriptionScreen;
