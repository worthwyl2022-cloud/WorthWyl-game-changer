import { useState } from 'react';
import { Settings, Wand2 } from 'lucide-react';

export default function StyleSyncScreen() {
  const [sampleText, setSampleText] = useState('');
  const [status, setStatus] = useState('Ready');
  const [isSyncing, setIsSyncing] = useState(false);

  const handleSync = () => {
    if (!sampleText.trim()) return;
    
    setIsSyncing(true);
    setStatus('Analyzing writing style...');
    
    setTimeout(() => {
      setStatus('Style Synced Successfully!');
      setIsSyncing(false);
    }, 2000);
  };

  return (
    <div className="flex flex-col h-full max-w-4xl mx-auto p-4 md:p-6 lg:p-8">
      <div className="mb-6 flex items-center gap-3 shrink-0">
        <div className="p-2 bg-zinc-900 rounded-lg border border-zinc-800">
          <Settings className="text-cyan-400" size={24} />
        </div>
        <h1 className="text-2xl font-bold text-zinc-100">Style Sync Wizard</h1>
      </div>

      <div className="flex-1 flex flex-col gap-4 min-h-0">
        <textarea
          value={sampleText}
          onChange={(e) => setSampleText(e.target.value)}
          placeholder="Paste your sample writing here to sync the AI's voice to your style..."
          className="flex-1 w-full bg-zinc-900 border border-zinc-800 rounded-xl p-4 text-zinc-200 placeholder:text-zinc-600 focus:outline-none focus:ring-2 focus:ring-cyan-500/50 focus:border-cyan-500/50 resize-none"
        />

        <div className="flex items-center justify-between shrink-0 bg-zinc-900 border border-zinc-800 rounded-xl p-4">
          <span className={`text-sm font-medium ${isSyncing ? 'text-cyan-400 animate-pulse' : status.includes('Successfully') ? 'text-emerald-400' : 'text-zinc-400'}`}>
            {status}
          </span>
          
          <button 
            onClick={handleSync}
            disabled={isSyncing || !sampleText.trim()}
            className="flex items-center gap-2 bg-cyan-600 hover:bg-cyan-500 disabled:opacity-50 disabled:hover:bg-cyan-600 text-white font-medium py-2 px-6 rounded-lg transition-colors"
          >
            <Wand2 size={18} />
            {isSyncing ? 'Syncing...' : 'Sync Style'}
          </button>
        </div>
      </div>
    </div>
  );
}
