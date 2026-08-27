import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { BrainCircuit } from 'lucide-react';

export default function SplashScreen() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate('/forge');
    }, 2500);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="flex flex-col items-center justify-center h-full bg-zinc-950 text-zinc-50 relative overflow-hidden">
      {/* Background glow */}
      <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
        <div className="w-[500px] h-[500px] bg-cyan-500/10 rounded-full blur-[100px]" />
      </div>

      <motion.div
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 0.8, ease: "easeOut" }}
        className="relative z-10 flex flex-col items-center"
      >
        <div className="p-4 bg-zinc-900 border border-zinc-800 rounded-2xl shadow-2xl mb-8 relative group">
          <div className="absolute inset-0 bg-cyan-500/20 blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-700" />
          <BrainCircuit size={64} className="text-cyan-400 relative z-10" />
        </div>
        
        <h1 className="text-4xl font-bold tracking-tight text-zinc-100 mb-4">
          Cranium Core
        </h1>
        
        <p className="text-zinc-400 text-lg font-medium tracking-wide">
          Affective-Dynamical Substrate
        </p>

        <motion.div 
          className="mt-12 w-48 h-1 bg-zinc-800 rounded-full overflow-hidden"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.5 }}
        >
          <motion.div 
            className="h-full bg-cyan-500"
            initial={{ width: "0%" }}
            animate={{ width: "100%" }}
            transition={{ duration: 1.5, delay: 0.5, ease: "easeInOut" }}
          />
        </motion.div>
      </motion.div>
    </div>
  );
}
