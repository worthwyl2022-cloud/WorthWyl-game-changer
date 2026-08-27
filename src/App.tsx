import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import { AnimatePresence, motion } from 'framer-motion';
import { Terminal, Database, ChevronLeft, Menu, BookOpen } from 'lucide-react';
import { useState } from 'react';

import SubstrateTerminal from './components/SubstrateTerminal';
import CanonicalLedger from './components/CanonicalLedger';

export default function App() {
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="flex h-screen bg-black text-zinc-50 overflow-hidden font-sans selection:bg-cyan-500/30">
      <Sidebar open={sidebarOpen} setOpen={setSidebarOpen} />
      
      <main className="flex-1 relative flex flex-col h-full overflow-hidden">
        <header className="h-16 flex items-center px-4 border-b border-white/5 lg:hidden shrink-0 bg-[#09090b]">
          <button 
            onClick={() => setSidebarOpen(true)}
            className="p-2 -ml-2 rounded-lg hover:bg-white/5 text-zinc-400 hover:text-zinc-50 transition-colors"
          >
            <Menu size={24} />
          </button>
          <span className="ml-4 font-semibold text-zinc-200 tracking-tight">Cranium Core Substrate</span>
        </header>

        <div className="flex-1 overflow-y-auto bg-black">
          <AnimatePresence mode="wait">
            <Routes location={location} key={location.pathname}>
              <Route path="/" element={<PageWrapper><SubstrateTerminal /></PageWrapper>} />
              <Route path="/ledger" element={<PageWrapper><CanonicalLedger /></PageWrapper>} />
            </Routes>
          </AnimatePresence>
        </div>
      </main>
    </div>
  );
}

function PageWrapper({ children }: { children: React.ReactNode }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -10 }}
      transition={{ duration: 0.2 }}
      className="h-full"
    >
      {children}
    </motion.div>
  );
}

function Sidebar({ open, setOpen }: { open: boolean, setOpen: (open: boolean) => void }) {
  const navigate = useNavigate();
  const location = useLocation();

  const navItems = [
    { name: 'Substrate Terminal', path: '/', icon: <Terminal size={18} /> },
    { name: 'Canonical Ledger', path: '/ledger', icon: <Database size={18} /> },
  ];

  return (
    <>
      {/* Mobile backdrop */}
      {open && (
        <div 
          className="fixed inset-0 bg-black/80 z-40 lg:hidden backdrop-blur-sm"
          onClick={() => setOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={`
        fixed inset-y-0 left-0 z-50 w-72 bg-[#050505] border-r border-white/5 transform transition-transform duration-300 ease-in-out flex flex-col
        lg:relative lg:translate-x-0 shadow-2xl shadow-black
        ${open ? 'translate-x-0' : '-translate-x-full'}
      `}>
        <div className="h-[88px] flex items-center justify-between px-8 border-b border-white/5 shrink-0 relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-cyan-500/20 to-transparent"></div>
          <span className="font-bold text-xl text-zinc-100 flex items-center gap-3 tracking-tight">
            <div className="p-2 bg-cyan-500/10 rounded-lg border border-cyan-500/20 shadow-[0_0_15px_rgba(6,182,212,0.15)]">
               <BookOpen className="text-cyan-400" size={20} />
            </div>
            Cranium Core
          </span>
          <button 
            className="lg:hidden p-1.5 rounded-lg hover:bg-white/5 text-zinc-400"
            onClick={() => setOpen(false)}
          >
            <ChevronLeft size={24} />
          </button>
        </div>

        <nav className="flex-1 overflow-y-auto py-8 px-4 space-y-2">
          {navItems.map((item) => {
            const active = location.pathname === item.path;
            return (
              <button
                key={item.path}
                onClick={() => {
                  navigate(item.path);
                  setOpen(false);
                }}
                className={`
                  w-full flex items-center gap-4 px-4 py-3.5 rounded-xl transition-all text-left font-medium text-sm
                  ${active 
                    ? 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/20 shadow-[0_0_15px_rgba(6,182,212,0.05)]' 
                    : 'text-zinc-400 hover:text-zinc-200 hover:bg-white/5 border border-transparent'
                  }
                `}
              >
                {item.icon}
                {item.name}
              </button>
            )
          })}
        </nav>
        
        <div className="p-6 border-t border-white/5 bg-[#09090b]">
          <div className="text-[10px] text-zinc-500 font-mono uppercase tracking-widest mb-3 px-2">Substrate Status</div>
          <div className="flex items-center gap-3 px-2 text-xs font-mono text-emerald-400 uppercase tracking-widest">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
            </span>
            Governance Loop Active
          </div>
        </div>
      </div>
    </>
  );
}
