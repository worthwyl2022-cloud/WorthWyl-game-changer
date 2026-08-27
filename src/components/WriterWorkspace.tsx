import { useState } from 'react';
import { PenTool, Upload, Mic, Image as ImageIcon } from 'lucide-react';

export default function WriterWorkspace() {
  const [text, setText] = useState('');

  return (
    <div className="flex flex-col h-full max-w-4xl mx-auto p-4 md:p-6 lg:p-8">
      <div className="mb-6 flex items-center gap-3">
        <div className="p-2 bg-zinc-900 rounded-lg border border-zinc-800">
          <PenTool className="text-cyan-400" size={24} />
        </div>
        <h1 className="text-2xl font-bold text-zinc-100">Writer Workspace</h1>
      </div>
      
      <div className="flex-1 flex flex-col gap-4 min-h-0">
        <textarea
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="Write your story..."
          className="flex-1 w-full bg-zinc-900 border border-zinc-800 rounded-xl p-4 text-zinc-200 placeholder:text-zinc-600 focus:outline-none focus:ring-2 focus:ring-cyan-500/50 focus:border-cyan-500/50 resize-none"
        />
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 shrink-0">
          <button 
            className="flex items-center justify-center gap-2 bg-zinc-100 hover:bg-white text-zinc-900 font-medium py-3 px-4 rounded-xl transition-colors md:col-span-2"
          >
            <PenTool size={18} />
            Write Next Episode
          </button>
          
          <button 
            className="flex items-center justify-center gap-2 bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 text-zinc-200 font-medium py-3 px-4 rounded-xl transition-colors"
          >
            <Upload size={18} />
            Export to KDP
          </button>
          
          <button 
            className="flex items-center justify-center gap-2 bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 text-emerald-400 font-medium py-3 px-4 rounded-xl transition-colors"
          >
            <Mic size={18} />
            Narrate Episode
          </button>

          <button 
            className="flex items-center justify-center gap-2 bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 text-purple-400 font-medium py-3 px-4 rounded-xl transition-colors md:col-span-2"
          >
            <ImageIcon size={18} />
            Generate Cover Art
          </button>
        </div>
      </div>
    </div>
  );
}
