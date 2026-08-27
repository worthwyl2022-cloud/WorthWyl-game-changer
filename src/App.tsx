import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import { AnimatePresence, motion } from 'framer-motion';
import { BookOpen, PenTool, Sparkles, Activity, Settings, Database, ChevronLeft, Menu } from 'lucide-react';
import { useState } from 'react';

import SplashScreen from './components/SplashScreen';
import ForgeScreen from './components/ForgeScreen';
import WriterWorkspace from './components/WriterWorkspace';
import TrackerScreen from './components/TrackerScreen';
import BibleScreen from './components/BibleScreen';
import StyleSyncScreen from './components/StyleSyncScreen';

export default function App() {
  const location = useLocation();
  const isSplash = location.pathname === '/';
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="flex h-screen bg-zinc-950 text-zinc-50 overflow-hidden font-sans">
      {!isSplash && (
        <Sidebar open={sidebarOpen} setOpen={setSidebarOpen} />
      )}
      
      <main className="flex-1 relative flex flex-col h-full overflow-hidden">
        {!isSplash && (
          <header className="h-16 flex items-center px-4 border-b border-zinc-800 lg:hidden shrink-0">
            <button 
              onClick={() => setSidebarOpen(true)}
              className="p-2 -ml-2 rounded-lg hover:bg-zinc-800 text-zinc-400 hover:text-zinc-50 transition-colors"
            >
              <Menu size={24} />
            </button>
            <span className="ml-4 font-semibold text-zinc-200">Cranium Core</span>
          </header>
        )}

        <div className="flex-1 overflow-y-auto">
          <AnimatePresence mode="wait">
            <Routes location={location} key={location.pathname}>
              <Route path="/" element={<SplashScreen />} />
              <Route path="/forge" element={<PageWrapper><ForgeScreen /></PageWrapper>} />
              <Route path="/writer" element={<PageWrapper><WriterWorkspace /></PageWrapper>} />
              <Route path="/tracker" element={<PageWrapper><TrackerScreen /></PageWrapper>} />
              <Route path="/bible" element={<PageWrapper><BibleScreen /></PageWrapper>} />
              <Route path="/style" element={<PageWrapper><StyleSyncScreen /></PageWrapper>} />
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
    { name: 'Forge', path: '/forge', icon: <Sparkles size={20} /> },
    { name: 'Writer', path: '/writer', icon: <PenTool size={20} /> },
    { name: 'Tracker', path: '/tracker', icon: <Activity size={20} /> },
    { name: 'Bible', path: '/bible', icon: <Database size={20} /> },
    { name: 'Style Sync', path: '/style', icon: <Settings size={20} /> },
  ];

  return (
    <>
      {/* Mobile backdrop */}
      {open && (
        <div 
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={() => setOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={`
        fixed inset-y-0 left-0 z-50 w-64 bg-zinc-900 border-r border-zinc-800 transform transition-transform duration-300 ease-in-out flex flex-col
        lg:relative lg:translate-x-0
        ${open ? 'translate-x-0' : '-translate-x-full'}
      `}>
        <div className="h-16 flex items-center justify-between px-6 border-b border-zinc-800">
          <span className="font-bold text-lg text-zinc-100 flex items-center gap-2">
            <BookOpen className="text-cyan-500" size={24} />
            Cranium Core
          </span>
          <button 
            className="lg:hidden p-1 rounded-md hover:bg-zinc-800 text-zinc-400"
            onClick={() => setOpen(false)}
          >
            <ChevronLeft size={24} />
          </button>
        </div>

        <nav className="flex-1 overflow-y-auto py-4 px-3 space-y-1">
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
                  w-full flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors text-left
                  ${active 
                    ? 'bg-cyan-500/10 text-cyan-400 font-medium' 
                    : 'text-zinc-400 hover:text-zinc-50 hover:bg-zinc-800'
                  }
                `}
              >
                {item.icon}
                {item.name}
              </button>
            )
          })}
        </nav>
        
        <div className="p-4 border-t border-zinc-800">
          <div className="text-xs text-zinc-500 font-medium px-2">SYSTEM STATUS</div>
          <div className="mt-2 flex items-center gap-2 px-2 text-sm text-emerald-400">
            <div className="w-2 h-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)]"></div>
            Online & Ready
          </div>
        </div>
      </div>
    </>
  );
}
