import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Save, AlertTriangle, Info } from 'lucide-react';
import { ViewModelContext } from '../App';
import GradientButton from '../components/GradientButton';

const PainTrackerScreen = () => {
  const navigate = useNavigate();
  const { addPainRecord } = useContext(ViewModelContext);
  const [painLevel, setPainLevel] = useState(3);
  const [notes, setNotes] = useState("");

  const getEmoji = (level) => level <= 3 ? "😊" : level <= 7 ? "😐" : "😫";
  const getStatus = (level) => level <= 3 ? "Mild pain" : level <= 7 ? "Moderate pain" : "Severe pain";

  const handleSave = async () => {
    await addPainRecord(painLevel, notes);
    if (painLevel > 7) {
      alert("Please meet your doctor immediately!");
    } else if (painLevel > 3) {
      alert("Recovering soon! Keep monitoring.");
    } else {
      alert("Great! You are doing well.");
    }
    navigate(-1);
  };

  return (
    <div className="min-h-screen p-6 max-w-2xl mx-auto pb-24">
      <div className="flex items-center mb-8">
        <button onClick={() => navigate(-1)} className="mr-4">
          <ChevronLeft className="w-6 h-6" />
        </button>
        <h1 className="text-2xl font-bold">Pain Tracker</h1>
      </div>

      <div className="card-style p-8 flex flex-col items-center mb-8">
        <span className="text-7xl mb-4">{getEmoji(painLevel)}</span>
        <h2 className="text-5xl font-bold mb-2">{painLevel}</h2>
        <span className="text-textGray">{getStatus(painLevel)}</span>

        <input
          type="range"
          min="0"
          max="10"
          step="1"
          value={painLevel}
          onChange={(e) => setPainLevel(parseInt(e.target.value))}
          className="w-full h-2 bg-white/10 rounded-lg appearance-none cursor-pointer accent-accentTeal mt-8"
        />
        <div className="w-full flex justify-between text-textGray text-xs mt-4">
          <span>😊 No pain</span>
          <span>😫 Worst</span>
        </div>
      </div>

      {painLevel > 3 && (
        <div className={`flex p-4 rounded-2xl mb-8 border ${painLevel > 7 ? 'bg-errorRed/20 border-errorRed text-white' : 'bg-warningOrange/20 border-warningOrange text-white'}`}>
          <div className={`p-2 rounded-lg h-fit mr-4 ${painLevel > 7 ? 'bg-errorRed' : 'bg-warningOrange'}`}>
            {painLevel > 7 ? <AlertTriangle className="w-6 h-6" /> : <Info className="w-6 h-6" />}
          </div>
          <div>
            <h4 className="font-bold">{painLevel > 7 ? 'Severe Pain Detected' : 'Moderate Pain Detected'}</h4>
            <p className="text-textGray text-xs mt-1">
              Your pain level is {painLevel}/10. Please meet your doctor as soon as possible for a medical evaluation.
            </p>
            <button className="w-full bg-white/10 h-10 rounded-xl mt-4 text-sm">Dismiss</button>
          </div>
        </div>
      )}

      <div className="mb-8">
        <h4 className="mb-2">Additional Notes (Optional)</h4>
        <textarea
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
          placeholder="Describe your pain or any additional details..."
          className="w-full h-32 bg-cardBackground rounded-2xl border border-white/10 p-4 text-white focus:outline-none focus:border-white/20"
        />
      </div>

      <GradientButton
        text="Save Pain Record"
        icon={Save}
        onClick={handleSave}
      />
    </div>
  );
};

export default PainTrackerScreen;
