import { Sparkles } from 'lucide-react';
import { motion } from 'framer-motion';

export default function ForgeScreen() {
  return (
    <div className="flex flex-col items-center justify-center h-full p-6 text-center">
      <motion.div
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.5 }}
        className="max-w-md w-full"
      >
        <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-cyan-500/10 mb-8 border border-cyan-500/20">
          <Sparkles size={40} className="text-cyan-400" />
        </div>
        
        <h1 className="text-3xl font-semibold text-zinc-100 mb-4">
          Cognitive Core Online
        </h1>
        
        <p className="text-zinc-400 text-lg mb-10">
          Awaiting directive. What shall we forge?
        </p>
        
        <button className="w-full bg-cyan-600 hover:bg-cyan-500 text-white font-medium py-3 px-6 rounded-xl shadow-[0_0_20px_rgba(8,145,178,0.3)] transition-all hover:shadow-[0_0_30px_rgba(8,145,178,0.5)] transform hover:-translate-y-0.5 active:translate-y-0">
          Initialize Cognitive Spiral
        </button>
      </motion.div>
    </div>
  );
}
