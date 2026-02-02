import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { base44 } from './api/base44Client';
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { 
  Scissors, 
  Calendar, 
  User, 
  Menu, 
  X,
  LogOut,
  LayoutDashboard,
  CalendarCheck
} from "lucide-react";

export default function Layout({ children, currentPageName }) {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const loadUser = async () => {
      try {
        const isAuth = await base44.auth.isAuthenticated();
        if (isAuth) {
          const currentUser = await base44.auth.me();
          setUser(currentUser);
        }
      } catch (e) {
        console.log('Not logged in');
      } finally {
        setIsLoading(false);
      }
    };
    loadUser();
  }, []);

  const handleLogout = () => {
    base44.auth.logout(createPageUrl('Home'));
  };

  // Pages without header
  const noHeaderPages = ['BookingSuccess'];
  const adminPages = ['AdminDashboard', 'AdminServices', 'AdminProfessionals', 'AdminHours', 'AdminAppointments', 'AdminClients'];
  
  if (noHeaderPages.includes(currentPageName) || adminPages.includes(currentPageName)) {
    return <>{children}</>;
  }

  return (
    <div className="min-h-screen bg-stone-50">
      {/* Header */}
      <header className="bg-white border-b border-stone-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            {/* Logo */}
            <Link to={createPageUrl('Home')} className="flex items-center gap-2">
              <div className="w-9 h-9 rounded-lg bg-amber-600 flex items-center justify-center">
                <Scissors className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-bold text-stone-800">BarberShop</span>
            </Link>

            {/* Desktop Navigation */}
            <nav className="hidden md:flex items-center gap-6">
              <Link 
                to={createPageUrl('Home')} 
                className={`text-sm font-medium transition-colors ${
                  currentPageName === 'Home' ? 'text-amber-600' : 'text-stone-600 hover:text-stone-900'
                }`}
              >
                Inicio
              </Link>
              <Link 
                to={createPageUrl('Booking')} 
                className={`text-sm font-medium transition-colors ${
                  currentPageName === 'Booking' ? 'text-amber-600' : 'text-stone-600 hover:text-stone-900'
                }`}
              >
                Reservar
              </Link>
              {user && (
                <Link 
                  to={createPageUrl('MyAppointments')} 
                  className={`text-sm font-medium transition-colors ${
                    currentPageName === 'MyAppointments' ? 'text-amber-600' : 'text-stone-600 hover:text-stone-900'
                  }`}
                >
                  Mis Turnos
                </Link>
              )}
            </nav>

            {/* Right Section */}
            <div className="flex items-center gap-3">
              {!isLoading && (
                <>
                  {user ? (
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" className="flex items-center gap-2">
                          <div className="w-8 h-8 rounded-full bg-amber-100 flex items-center justify-center">
                            <span className="text-sm font-medium text-amber-700">
                              {user.full_name?.charAt(0) || user.email?.charAt(0)?.toUpperCase()}
                            </span>
                          </div>
                          <span className="hidden sm:block text-sm font-medium text-stone-700">
                            {user.full_name || user.email?.split('@')[0]}
                          </span>
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end" className="w-48">
                        {user.role === 'admin' && (
                          <>
                            <DropdownMenuItem onClick={() => navigate(createPageUrl('AdminDashboard'))}>
                              <LayoutDashboard className="w-4 h-4 mr-2" />
                              Panel Admin
                            </DropdownMenuItem>
                            <DropdownMenuSeparator />
                          </>
                        )}
                        <DropdownMenuItem onClick={() => navigate(createPageUrl('MyAppointments'))}>
                          <CalendarCheck className="w-4 h-4 mr-2" />
                          Mis Turnos
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem onClick={handleLogout} className="text-red-600">
                          <LogOut className="w-4 h-4 mr-2" />
                          Cerrar Sesión
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  ) : (
                    <Button 
                      onClick={() => base44.auth.redirectToLogin()}
                      className="bg-amber-600 hover:bg-amber-700 text-white"
                    >
                      Iniciar Sesión
                    </Button>
                  )}
                </>
              )}

              {/* Mobile Menu Button */}
              <Button 
                variant="ghost" 
                size="icon"
                className="md:hidden"
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              >
                {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
              </Button>
            </div>
          </div>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden bg-white border-t border-stone-100">
            <div className="px-4 py-4 space-y-3">
              <Link 
                to={createPageUrl('Home')} 
                className="block py-2 text-stone-700"
                onClick={() => setMobileMenuOpen(false)}
              >
                Inicio
              </Link>
              <Link 
                to={createPageUrl('Booking')} 
                className="block py-2 text-stone-700"
                onClick={() => setMobileMenuOpen(false)}
              >
                Reservar
              </Link>
              {user && (
                <Link 
                  to={createPageUrl('MyAppointments')} 
                  className="block py-2 text-stone-700"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Mis Turnos
                </Link>
              )}
              {user?.role === 'admin' && (
                <Link 
                  to={createPageUrl('AdminDashboard')} 
                  className="block py-2 text-amber-600 font-medium"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Panel Admin
                </Link>
              )}
            </div>
          </div>
        )}
      </header>

      {/* Main Content */}
      <main>{children}</main>
    </div>
  );
}