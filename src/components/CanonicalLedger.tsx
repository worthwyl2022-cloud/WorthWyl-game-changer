import { useState, useEffect } from 'react';
import { Database, Plus, Trash2, Shield } from 'lucide-react';
import { collection, onSnapshot, addDoc, deleteDoc, doc, query, orderBy, serverTimestamp } from 'firebase/firestore';
import { db } from '../lib/firebase';

interface CanonAtom {
  id: string;
  classification: string;
  signature: string;
  content: string;
  createdAt: any;
}

export default function CanonicalLedger() {
  const [atoms, setAtoms] = useState<CanonAtom[]>([]);
  const [isAdding, setIsAdding] = useState(false);
  const [newSignature, setNewSignature] = useState('');
  const [newClassification, setNewClassification] = useState('Entity');
  const [newContent, setNewContent] = useState('');

  useEffect(() => {
    const q = query(collection(db, 'canonicalMemory'), orderBy('createdAt', 'desc'));
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const loadedAtoms: CanonAtom[] = [];
      snapshot.forEach((doc) => {
        loadedAtoms.push({ id: doc.id, ...doc.data() } as CanonAtom);
      });
      setAtoms(loadedAtoms);
    });

    return () => unsubscribe();
  }, []);

  const handleInjectAtom = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newSignature.trim() || !newContent.trim()) return;

    try {
      await addDoc(collection(db, 'canonicalMemory'), {
        signature: newSignature,
        classification: newClassification,
        content: newContent,
        createdAt: serverTimestamp(),
      });
      setIsAdding(false);
      setNewSignature('');
      setNewClassification('Entity');
      setNewContent('');
    } catch (err) {
      console.error("Error injecting atom: ", err);
    }
  };

  const handlePurge = async (id: string) => {
    try {
      await deleteDoc(doc(db, 'canonicalMemory', id));
    } catch (err) {
      console.error("Error purging atom: ", err);
    }
  };

  return (
    <div className="flex flex-col h-full max-w-5xl mx-auto p-6 md:p-8 lg:p-10 relative">
      <div className="mb-8 flex flex-col md:flex-row md:items-center justify-between shrink-0 gap-4">
        <div className="flex items-center gap-4 px-2">
          <div className="p-2.5 bg-emerald-500/10 rounded-xl border border-emerald-500/20 shadow-[0_0_15px_rgba(16,185,129,0.15)]">
            <Database className="text-emerald-400" size={24} />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-zinc-100 tracking-tight">Canonical Ledger</h1>
            <p className="text-zinc-500 text-[11px] font-mono uppercase tracking-widest mt-1">Durable memory substrate. Atoms constrain generation.</p>
          </div>
        </div>
        <div className="flex items-center gap-2 text-[10px] uppercase tracking-widest font-mono text-zinc-500 bg-[#09090b] px-3 py-2 rounded-full border border-white/5">
          <Shield size={14} className="text-cyan-500" />
          Quarantine Protected
        </div>
      </div>

      {isAdding && (
        <form onSubmit={handleInjectAtom} className="bg-[#09090b] border border-white/5 rounded-[24px] p-6 mb-8 shadow-2xl shadow-black/50 relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-emerald-500/30 to-transparent"></div>
          <div className="flex flex-col md:flex-row gap-4 mb-5">
            <input 
              type="text" 
              placeholder="Atom Signature (e.g., Core_Protocol_Override)" 
              value={newSignature}
              onChange={(e) => setNewSignature(e.target.value)}
              className="flex-1 bg-[#121214] border border-white/5 rounded-xl px-4 py-3 text-zinc-200 focus:outline-none focus:border-emerald-500/40 focus:ring-1 focus:ring-emerald-500/20 transition-all font-mono text-sm shadow-inner"
              autoFocus
            />
            <select 
              value={newClassification}
              onChange={(e) => setNewClassification(e.target.value)}
              className="bg-[#121214] border border-white/5 rounded-xl px-4 py-3 text-zinc-200 focus:outline-none focus:border-emerald-500/40 focus:ring-1 focus:ring-emerald-500/20 transition-all font-mono text-sm shadow-inner min-w-[150px]"
            >
              <option value="Entity">Entity</option>
              <option value="Rule">Rule</option>
              <option value="Event">Event</option>
              <option value="State">State</option>
            </select>
          </div>
          <textarea 
            placeholder="Canonical data payload..." 
            value={newContent}
            onChange={(e) => setNewContent(e.target.value)}
            className="w-full bg-[#121214] border border-white/5 rounded-xl px-4 py-3 text-zinc-200 focus:outline-none focus:border-emerald-500/40 focus:ring-1 focus:ring-emerald-500/20 transition-all min-h-[140px] mb-5 font-mono text-sm shadow-inner resize-y"
          />
          <div className="flex justify-end gap-3">
            <button 
              type="button" 
              onClick={() => setIsAdding(false)}
              className="px-5 py-2.5 text-zinc-500 hover:text-zinc-300 text-xs font-mono uppercase tracking-widest transition-colors"
            >
              Cancel
            </button>
            <button 
              type="submit"
              className="px-6 py-2.5 bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/30 text-emerald-400 rounded-xl font-mono text-xs uppercase tracking-widest transition-all shadow-[0_0_15px_rgba(16,185,129,0.1)]"
            >
              Inject Atom
            </button>
          </div>
        </form>
      )}

      <div className="flex-1 overflow-y-auto space-y-5 pb-24">
        {atoms.map((atom) => (
          <div key={atom.id} className="bg-[#09090b] border border-white/5 shadow-xl shadow-black/20 rounded-[20px] p-6 hover:border-white/10 transition-colors group relative overflow-hidden">
            <div className="absolute top-0 left-0 w-1 h-full bg-emerald-500/20 group-hover:bg-emerald-500/40 transition-colors"></div>
            <div className="flex justify-between items-start mb-4">
              <div className="flex items-center gap-4">
                <span className="text-[10px] font-mono font-bold tracking-widest px-2.5 py-1 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 rounded-lg uppercase">
                  {atom.classification}
                </span>
                <h3 className="text-sm font-mono font-bold text-zinc-200">{atom.signature}</h3>
              </div>
              <button 
                onClick={() => handlePurge(atom.id)}
                className="text-zinc-600 hover:text-rose-400 opacity-0 group-hover:opacity-100 transition-opacity p-2 hover:bg-rose-500/10 rounded-lg"
                title="Purge Atom"
              >
                <Trash2 size={16} />
              </button>
            </div>
            <p className="text-zinc-400 text-sm leading-relaxed whitespace-pre-wrap font-sans pl-1">
              {atom.content}
            </p>
          </div>
        ))}
        {atoms.length === 0 && !isAdding && (
          <div className="text-center py-16 text-zinc-600 font-mono text-xs italic border border-dashed border-white/5 rounded-[24px]">
            Ledger empty. Inject canonical memory atoms to establish baseline reality.
          </div>
        )}
      </div>

      {!isAdding && (
        <button 
          onClick={() => setIsAdding(true)}
          className="absolute bottom-8 right-8 lg:bottom-10 lg:right-10 w-14 h-14 bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/30 text-emerald-400 rounded-2xl flex items-center justify-center shadow-[0_0_20px_rgba(16,185,129,0.2)] transition-all hover:scale-105 active:scale-95 z-10 backdrop-blur-sm"
        >
          <Plus size={24} />
        </button>
      )}
    </div>
  );
}
