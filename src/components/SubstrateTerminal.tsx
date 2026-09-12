import { useState, useEffect, useRef } from 'react';
import { Terminal, Activity, Cpu, ShieldAlert, Send, History } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { collection, getDocs, addDoc, onSnapshot, query, orderBy, serverTimestamp, limit } from 'firebase/firestore';
import { db } from '../lib/firebase';

interface TraceLog {
  step: string;
  status: 'PASS' | 'ACTIVE' | 'LOCKED' | 'PROCESSING' | 'SUCCESS';
  detail: string;
}

interface Metrics {
  identityPressure: number;
  fieldConflict: number;
  arousal: number;
  coherence: number;
}

interface StateHistoryEntry {
  id: string;
  metrics: Metrics;
  directives: string[];
  intention: string;
  createdAt: any;
}

export default function SubstrateTerminal() {
  const [intention, setIntention] = useState('');
  const [isInjecting, setIsInjecting] = useState(false);
  const [output, setOutput] = useState<string | null>(null);
  const [trace, setTrace] = useState<TraceLog[]>([]);
  const [metrics, setMetrics] = useState<Metrics | null>(null);
  const [activeDirectives, setActiveDirectives] = useState<string[]>([]);
  const [rounds, setRounds] = useState(0);
  const [stateHistory, setStateHistory] = useState<StateHistoryEntry[]>([]);

  const logsEndRef = useRef<HTMLDivElement>(null);
  const historyEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (logsEndRef.current) {
      logsEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [trace]);

  // Subscribe to state history from Firestore
  useEffect(() => {
    const q = query(collection(db, 'cognitiveStateLog'), orderBy('createdAt', 'desc'), limit(50));
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const history: StateHistoryEntry[] = [];
      snapshot.forEach((doc) => {
        history.push({ id: doc.id, ...doc.data() } as StateHistoryEntry);
      });
      setStateHistory(history);
    });
    return () => unsubscribe();
  }, []);

  const handleInject = async () => {
    if (!intention.trim()) return;
    
    setIsInjecting(true);
    setOutput(null);
    setTrace([]);
    setMetrics(null);
    setActiveDirectives([]);
    setRounds(0);

    try {
      // 1. Compile canonical context
      const snapshot = await getDocs(collection(db, 'canonicalMemory'));
      const entries = snapshot.docs.map(doc => doc.data());
      const response = await fetch('/api/substrate/inject', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          intention,
          memory: entries
        }),
      });
      
      const data = await response.json();
      
      if (!response.ok) {
        throw new Error(data.error || 'Cognitive failure');
      }
      
      // 3. Stagger logs to animate real-time processing
      for (let i = 0; i < data.trace.length; i++) {
        setTimeout(() => {
          setTrace(prev => [...prev, data.trace[i]]);
        }, i * 400); // 400ms delay per log
      }

      // 4. Reveal final output after logs complete
      setTimeout(async () => {
        setOutput(data.output);
        setMetrics(data.metrics);
        setActiveDirectives(data.directives);
        setRounds(data.rounds);
        
        // Log to Firestore state history
        await addDoc(collection(db, 'cognitiveStateLog'), {
          intention,
          metrics: data.metrics,
          directives: data.directives,
          createdAt: serverTimestamp(),
        });
        
        setIsInjecting(false);
      }, data.trace.length * 400 + 200);

    } catch (error: any) {
      console.error(error);
      setTrace([{ step: 'SYSTEM_ERROR', status: 'LOCKED', detail: error.message }]);
      setIsInjecting(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch(status) {
      case 'PASS': return 'text-emerald-400';
      case 'SUCCESS': return 'text-emerald-400';
      case 'ACTIVE': return 'text-cyan-400';
      case 'LOCKED': return 'text-amber-400';
      case 'PROCESSING': return 'text-purple-400';
      default: return 'text-zinc-400';
    }
  };

  return (
    <div className="flex flex-col lg:flex-row h-full max-w-[1800px] mx-auto p-6 md:p-8 lg:p-10 gap-8">
      
      {/* Left Column: Intention & Memory */}
      <div className="w-full lg:w-1/3 flex flex-col gap-8">
        <div className="flex items-center gap-4 px-2">
          <div className="p-2.5 bg-cyan-500/10 rounded-xl border border-cyan-500/20 shadow-[0_0_15px_rgba(6,182,212,0.15)]">
            <Cpu className="text-cyan-400" size={24} />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-zinc-100 tracking-tight">Cognitive Terminal</h1>
            <p className="text-zinc-500 text-[11px] font-mono uppercase tracking-widest mt-1">Reasoning Scale Engine</p>
          </div>
        </div>

        {/* Intention Input */}
        <div className="bg-[#09090b] border border-white/5 shadow-2xl shadow-black/50 rounded-[24px] p-6 flex-shrink-0 flex flex-col relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-cyan-500/20 to-transparent"></div>
          <label className="text-[11px] font-mono text-zinc-400 mb-4 uppercase tracking-widest flex items-center gap-2">
            <Terminal size={14} className="text-cyan-500/70" /> User Intention Injection
          </label>
          <textarea
            value={intention}
            onChange={(e) => setIntention(e.target.value)}
            placeholder="Input directive or query..."
            disabled={isInjecting}
            className="w-full bg-[#121214] border border-white/5 rounded-xl p-4 text-zinc-200 font-mono text-sm focus:outline-none focus:border-cyan-500/40 focus:ring-1 focus:ring-cyan-500/20 transition-all resize-none h-40 mb-5 disabled:opacity-50"
          />
          <button 
            onClick={handleInject}
            disabled={isInjecting || !intention.trim()}
            className="w-full flex items-center justify-center gap-2 bg-cyan-500/10 hover:bg-cyan-500/20 border border-cyan-500/30 disabled:opacity-30 disabled:hover:bg-cyan-500/10 text-cyan-400 font-medium py-3.5 px-4 rounded-xl transition-all font-mono text-xs uppercase tracking-widest"
          >
            {isInjecting ? 'Processing...' : 'Execute Injection'}
            <Send size={16} />
          </button>
        </div>

        {/* Resonance Field Metrics */}
        <div className="bg-[#09090b] border border-white/5 shadow-2xl shadow-black/50 rounded-[24px] p-6 flex-1 min-h-[250px] flex flex-col overflow-hidden relative">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-purple-500/20 to-transparent"></div>
          <label className="text-[11px] font-mono text-zinc-400 mb-6 uppercase tracking-widest flex items-center gap-2 shrink-0">
            <Activity size={14} className="text-purple-500/70" /> Resonance Field Metrics
          </label>
          
          <div className="space-y-5 shrink-0">
            <MetricBar label="Identity Pressure" value={metrics?.identityPressure ?? 0} color="bg-rose-500" />
            <MetricBar label="Field Conflict" value={metrics?.fieldConflict ?? 0} color="bg-amber-500" />
            <MetricBar label="Arousal" value={metrics?.arousal ?? 0} color="bg-purple-500" />
            <MetricBar label="Coherence Gate" value={metrics?.coherence ?? 0} color="bg-emerald-500" />
          </div>

          {metrics && (
            <div className="mt-8 pt-6 border-t border-white/5 grid grid-cols-2 gap-6 shrink-0">
              <div>
                <div className="text-[10px] text-zinc-500 font-mono uppercase tracking-widest mb-1.5">Scale Rounds</div>
                <div className="text-2xl font-bold text-zinc-200 font-mono">{rounds} <span className="text-zinc-600 text-lg">/ 4</span></div>
              </div>
              <div>
                <div className="text-[10px] text-zinc-500 font-mono uppercase tracking-widest mb-2">Directives</div>
                <div className="flex flex-wrap gap-1.5">
                  {activeDirectives.map((d, i) => (
                    <span key={i} className="text-[10px] font-bold text-amber-400 font-mono bg-amber-400/10 border border-amber-400/20 px-2 py-0.5 rounded-md">
                      {d}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Middle Column: State History */}
      <div className="w-full lg:w-1/3 flex flex-col gap-8 h-full">
        <div className="bg-[#09090b] border border-white/5 shadow-2xl shadow-black/50 rounded-[24px] p-6 flex-1 flex flex-col relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-amber-500/20 to-transparent"></div>
          <label className="text-[11px] font-mono text-zinc-400 mb-6 uppercase tracking-widest flex items-center gap-2 shrink-0">
            <History size={14} className="text-amber-500/70" /> Cognitive State Log
          </label>
          
          <div className="flex-1 overflow-y-auto space-y-4 pr-2">
            {stateHistory.length === 0 ? (
              <div className="h-full flex items-center justify-center text-zinc-600 italic font-mono text-xs text-center border border-dashed border-white/5 rounded-xl">
                No state history recorded.
              </div>
            ) : (
              <AnimatePresence>
                {stateHistory.map((entry) => (
                  <motion.div 
                    key={entry.id}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="bg-[#121214] border border-white/5 hover:border-white/10 transition-colors rounded-xl p-4 group"
                  >
                    <div className="flex justify-between items-start mb-3">
                      <div className="text-[10px] font-mono text-zinc-500 tracking-wider">
                        T-MINUS {entry.createdAt?.toDate ? entry.createdAt.toDate().toLocaleTimeString([], {hour12: false}) : 'Just now'}
                      </div>
                      <div className="flex gap-1.5 flex-wrap justify-end opacity-80 group-hover:opacity-100 transition-opacity">
                        {entry.directives.map((dir, i) => (
                          <span key={i} className="text-[9px] font-mono font-bold bg-amber-400/10 border border-amber-400/20 text-amber-400 px-1.5 py-0.5 rounded-md">
                            {dir}
                          </span>
                        ))}
                      </div>
                    </div>
                    
                    <div className="text-sm text-zinc-300 font-sans mb-4 line-clamp-2 leading-relaxed">
                      "{entry.intention}"
                    </div>
                    
                    <div className="grid grid-cols-2 gap-x-4 gap-y-3">
                      <CompactMetric label="IDP" value={entry.metrics.identityPressure} color="text-rose-400" />
                      <CompactMetric label="CFL" value={entry.metrics.fieldConflict} color="text-amber-400" />
                      <CompactMetric label="ARS" value={entry.metrics.arousal} color="text-purple-400" />
                      <CompactMetric label="COH" value={entry.metrics.coherence} color="text-emerald-400" />
                    </div>
                  </motion.div>
                ))}
              </AnimatePresence>
            )}
            <div ref={historyEndRef} />
          </div>
        </div>
      </div>

      {/* Right Column: Trace Logs & Output */}
      <div className="w-full lg:w-1/3 flex flex-col gap-8 h-full">
        
        {/* Trace Logs */}
        <div className="bg-[#09090b] border border-white/5 shadow-2xl shadow-black/50 rounded-[24px] p-6 h-72 flex flex-col relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-emerald-500/20 to-transparent"></div>
          <label className="text-[11px] font-mono text-zinc-400 mb-5 uppercase tracking-widest flex items-center gap-2 shrink-0">
            <ShieldAlert size={14} className="text-emerald-500/70" /> Deliberation Trace
          </label>
          
          <div className="flex-1 overflow-y-auto space-y-2 pr-2 font-mono text-xs">
            {trace.length === 0 && !isInjecting && (
              <div className="text-zinc-600 h-full flex items-center justify-center italic border border-dashed border-white/5 rounded-xl">
                Awaiting injection sequence...
              </div>
            )}
            
            <AnimatePresence>
              {trace.map((log, i) => (
                <motion.div 
                  key={i}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="flex gap-4 py-1.5 border-b border-white/5 last:border-0"
                >
                  <span className="text-zinc-600 shrink-0">[{new Date().toISOString().substring(11, 23)}]</span>
                  <span className={`shrink-0 font-bold ${getStatusColor(log.status)} w-20`}>{log.status}</span>
                  <div className="flex flex-col gap-0.5">
                    <span className="text-zinc-300 font-medium">{log.step}</span>
                    <span className="text-zinc-500 text-[11px] break-words">{log.detail}</span>
                  </div>
                </motion.div>
              ))}
            </AnimatePresence>
            <div ref={logsEndRef} />
          </div>
        </div>

        {/* Final Synthesized Output */}
        <div className="bg-[#09090b] border border-white/5 shadow-2xl shadow-black/50 rounded-[24px] p-6 flex-1 flex flex-col min-h-[300px] relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-zinc-400/20 to-transparent"></div>
          <label className="text-[11px] font-mono text-zinc-400 mb-5 uppercase tracking-widest flex items-center gap-2 shrink-0">
             Output Synthesis
          </label>
          
          <div className="flex-1 overflow-y-auto pr-2">
            {output ? (
              <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="text-zinc-300 text-sm leading-loose whitespace-pre-wrap font-sans"
              >
                {output}
              </motion.div>
            ) : (
              <div className="h-full flex items-center justify-center text-zinc-600 font-mono text-xs italic border border-dashed border-white/5 rounded-xl">
                {isInjecting ? 'Governance loop executing...' : 'No synthesis output generated.'}
              </div>
            )}
          </div>
        </div>

      </div>
    </div>
  );
}

function MetricBar({ label, value, color }: { label: string, value: number, color: string }) {
  const percentage = Math.max(0, Math.min(100, Math.round(value * 100)));
  
  return (
    <div>
      <div className="flex justify-between text-[10px] font-mono font-medium mb-2 uppercase tracking-widest">
        <span className="text-zinc-500">{label}</span>
        <span className="text-zinc-300">{percentage}%</span>
      </div>
      <div className="h-2 w-full bg-[#121214] rounded-full overflow-hidden border border-white/5">
        <motion.div 
          className={`h-full ${color}`}
          initial={{ width: 0 }}
          animate={{ width: `${percentage}%` }}
          transition={{ duration: 1, ease: "easeOut" }}
        />
      </div>
    </div>
  );
}

function CompactMetric({ label, value, color }: { label: string, value: number, color: string }) {
  const percentage = Math.max(0, Math.min(100, Math.round(value * 100)));
  return (
    <div className="flex justify-between items-center text-[9px] font-mono border border-white/5 bg-[#09090b] px-2 py-1.5 rounded-md">
      <span className="text-zinc-500 uppercase tracking-widest">{label}</span>
      <span className={`${color} font-bold`}>{percentage}%</span>
    </div>
  );
}
