import { useState } from 'react';
import { Database, Plus } from 'lucide-react';

interface BibleEntry {
  id: string;
  title: string;
  type: string;
  content: string;
}

export default function BibleScreen() {
  const [entries] = useState<BibleEntry[]>([
    { id: '1', title: 'The Overcity', type: 'Location', content: 'A sprawling metropolis built above the clouds, strictly segregated by atmospheric density.' },
    { id: '2', title: 'Aria Vance', type: 'Character', content: 'Lead atmospheric engineer. Stubborn, brilliant, secretive.' }
  ]);

  return (
    <div className="flex flex-col h-full max-w-4xl mx-auto p-4 md:p-6 lg:p-8 relative">
      <div className="mb-6 flex items-center gap-3 shrink-0">
        <div className="p-2 bg-zinc-900 rounded-lg border border-zinc-800">
          <Database className="text-amber-400" size={24} />
        </div>
        <h1 className="text-2xl font-bold text-zinc-100">World Building Bible</h1>
      </div>

      <div className="flex-1 overflow-y-auto space-y-4 pb-20">
        {entries.map((entry) => (
          <div key={entry.id} className="bg-zinc-900 border border-zinc-800 rounded-xl p-5 hover:border-zinc-700 transition-colors">
            <div className="flex justify-between items-start mb-2">
              <h3 className="text-lg font-semibold text-zinc-100">{entry.title}</h3>
              <span className="text-xs font-medium px-2 py-1 bg-zinc-800 text-zinc-400 rounded-md">
                {entry.type}
              </span>
            </div>
            <p className="text-zinc-400 leading-relaxed">
              {entry.content}
            </p>
          </div>
        ))}
        {entries.length === 0 && (
          <div className="text-center py-12 text-zinc-500">
            No entries yet. Start building your world.
          </div>
        )}
      </div>

      <button className="absolute bottom-6 right-6 lg:bottom-8 lg:right-8 w-14 h-14 bg-amber-500 hover:bg-amber-400 text-zinc-950 rounded-full flex items-center justify-center shadow-lg shadow-amber-500/20 transition-transform hover:scale-105 active:scale-95">
        <Plus size={28} />
      </button>
    </div>
  );
}
