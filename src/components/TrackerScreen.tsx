import { Activity } from 'lucide-react';
import { motion } from 'framer-motion';

export default function TrackerScreen() {
  const tensionProgress = 70;

  return (
    <div className="max-w-4xl mx-auto p-4 md:p-6 lg:p-8">
      <div className="mb-8 flex items-center gap-3">
        <div className="p-2 bg-zinc-900 rounded-lg border border-zinc-800">
          <Activity className="text-emerald-400" size={24} />
        </div>
        <h1 className="text-2xl font-bold text-zinc-100">Narrative Tracker</h1>
      </div>

      <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-6">
        <h2 className="text-lg font-medium text-zinc-200 mb-6">Metacognitive Dashboard</h2>
        
        <div className="space-y-6">
          <div>
            <div className="flex justify-between text-sm font-medium mb-2">
              <span className="text-zinc-400">Tension Meter</span>
              <span className="text-emerald-400">{tensionProgress}%</span>
            </div>
            <div className="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
              <motion.div 
                className="h-full bg-emerald-500 rounded-full"
                initial={{ width: 0 }}
                animate={{ width: `${tensionProgress}%` }}
                transition={{ duration: 1, ease: "easeOut" }}
              />
            </div>
          </div>

          <div>
            <div className="flex justify-between text-sm font-medium mb-2">
              <span className="text-zinc-400">Continuity Integrity</span>
              <span className="text-cyan-400">92%</span>
            </div>
            <div className="h-2 w-full bg-zinc-800 rounded-full overflow-hidden">
              <motion.div 
                className="h-full bg-cyan-500 rounded-full"
                initial={{ width: 0 }}
                animate={{ width: "92%" }}
                transition={{ duration: 1, ease: "easeOut", delay: 0.2 }}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
