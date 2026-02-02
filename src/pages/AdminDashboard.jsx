import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { base44 } from '@/api/base44Client';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { format, startOfWeek, endOfWeek, isToday } from "date-fns";
import { es } from "date-fns/locale";
import { motion } from "framer-motion";
import { 
  Calendar,
  Users,
  Scissors,
  Clock,
  DollarSign,
  TrendingUp,
  ArrowRight,
  Loader2,
  CalendarCheck,
  CalendarX,
  Settings
} from "lucide-react";

export default function AdminDashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadUser = async () => {
      try {
        const isAuth = await base44.auth.isAuthenticated();
        if (!isAuth) {
          base44.auth.redirectToLogin(createPageUrl('AdminDashboard'));
          return;
        }
        const currentUser = await base44.auth.me();
        if (currentUser.role !== 'admin') {
          navigate(createPageUrl('Home'));
          return;
        }
        setUser(currentUser);
      } catch (e) {
        base44.auth.redirectToLogin(createPageUrl('AdminDashboard'));
      } finally {
        setIsLoading(false);
      }
    };
    loadUser();
  }, [navigate]);

  const { data: appointments = [] } = useQuery({
    queryKey: ['allAppointments'],
    queryFn: () => base44.entities.Appointment.list('-date', 100),
    enabled: !!user
  });

  const { data: services = [] } = useQuery({
    queryKey: ['services'],
    queryFn: () => base44.entities.Service.list(),
    enabled: !!user
  });

  const { data: professionals = [] } = useQuery({
    queryKey: ['professionals'],
    queryFn: () => base44.entities.Professional.list(),
    enabled: !!user
  });

  const { data: users = [] } = useQuery({
    queryKey: ['users'],
    queryFn: () => base44.entities.User.list('-created_date', 100),
    enabled: !!user
  });

  if (isLoading) {
    return (
      <div className="min-h-screen bg-stone-50 flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-amber-600" />
      </div>
    );
  }

  const today = new Date().toISOString().split('T')[0];
  const todayAppointments = appointments.filter(apt => apt.date === today && apt.status !== 'cancelled');
  const pendingAppointments = appointments.filter(apt => apt.status === 'pending' || apt.status === 'confirmed');
  const completedAppointments = appointments.filter(apt => apt.status === 'completed');
  
  const weekStart = startOfWeek(new Date(), { weekStartsOn: 1 });
  const weekEnd = endOfWeek(new Date(), { weekStartsOn: 1 });
  const weekAppointments = appointments.filter(apt => {
    const aptDate = new Date(apt.date);
    return aptDate >= weekStart && aptDate <= weekEnd && apt.status !== 'cancelled';
  });

  const weekRevenue = weekAppointments
    .filter(apt => apt.payment_status === 'paid')
    .reduce((sum, apt) => sum + (apt.price || 0), 0);

  const stats = [
    { 
      title: "Turnos Hoy", 
      value: todayAppointments.length, 
      icon: Calendar, 
      color: "bg-blue-500",
      bgLight: "bg-blue-50"
    },
    { 
      title: "Esta Semana", 
      value: weekAppointments.length, 
      icon: CalendarCheck, 
      color: "bg-green-500",
      bgLight: "bg-green-50"
    },
    { 
      title: "Clientes", 
      value: users.length, 
      icon: Users, 
      color: "bg-purple-500",
      bgLight: "bg-purple-50"
    },
    { 
      title: "Ingresos Semana", 
      value: `$${weekRevenue.toLocaleString()}`, 
      icon: DollarSign, 
      color: "bg-amber-500",
      bgLight: "bg-amber-50"
    },
  ];

  return (
    <div className="min-h-screen bg-stone-50">
      {/* Header */}
      <div className="bg-white border-b border-stone-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-2xl font-bold text-stone-800">Panel de Administración</h1>
              <p className="text-stone-500 mt-1">Bienvenido, {user?.full_name}</p>
            </div>
            <div className="flex gap-3">
              <Link to={createPageUrl('AdminServices')}>
                <Button variant="outline" className="border-stone-300">
                  <Scissors className="w-4 h-4 mr-2" />
                  Servicios
                </Button>
              </Link>
              <Link to={createPageUrl('AdminAppointments')}>
                <Button className="bg-amber-600 hover:bg-amber-700 text-white">
                  <Calendar className="w-4 h-4 mr-2" />
                  Ver Turnos
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {stats.map((stat, i) => (
            <motion.div
              key={stat.title}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.1 }}
            >
              <Card className="border-stone-200">
                <CardContent className="p-6">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="text-sm text-stone-500 mb-1">{stat.title}</p>
                      <p className="text-2xl font-bold text-stone-800">{stat.value}</p>
                    </div>
                    <div className={`w-10 h-10 rounded-lg ${stat.bgLight} flex items-center justify-center`}>
                      <stat.icon className={`w-5 h-5 ${stat.color.replace('bg-', 'text-')}`} />
                    </div>
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          ))}
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Today's Appointments */}
          <div className="lg:col-span-2">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-lg">Turnos de Hoy</CardTitle>
                <Link to={createPageUrl('AdminAppointments')}>
                  <Button variant="ghost" size="sm" className="text-amber-600">
                    Ver todos
                    <ArrowRight className="w-4 h-4 ml-1" />
                  </Button>
                </Link>
              </CardHeader>
              <CardContent>
                {todayAppointments.length > 0 ? (
                  <div className="space-y-3">
                    {todayAppointments.slice(0, 5).map((apt) => (
                      <div 
                        key={apt.id}
                        className="flex items-center justify-between p-4 bg-stone-50 rounded-lg"
                      >
                        <div className="flex items-center gap-4">
                          <div className="w-12 h-12 rounded-full bg-amber-100 flex items-center justify-center">
                            <Clock className="w-6 h-6 text-amber-600" />
                          </div>
                          <div>
                            <p className="font-medium text-stone-800">{apt.client_name}</p>
                            <p className="text-sm text-stone-500">{apt.service_name}</p>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className="font-semibold text-stone-800">{apt.time}</p>
                          <Badge className={apt.status === 'confirmed' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}>
                            {apt.status === 'confirmed' ? 'Confirmado' : 'Pendiente'}
                          </Badge>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-8 text-stone-500">
                    <CalendarX className="w-12 h-12 mx-auto mb-3 opacity-30" />
                    <p>No hay turnos para hoy</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>

          {/* Quick Actions */}
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Acciones Rápidas</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                <Link to={createPageUrl('AdminServices')} className="block">
                  <Button variant="outline" className="w-full justify-start border-stone-200">
                    <Scissors className="w-4 h-4 mr-3 text-amber-600" />
                    Gestionar Servicios
                  </Button>
                </Link>
                <Link to={createPageUrl('AdminProfessionals')} className="block">
                  <Button variant="outline" className="w-full justify-start border-stone-200">
                    <Users className="w-4 h-4 mr-3 text-amber-600" />
                    Gestionar Profesionales
                  </Button>
                </Link>
                <Link to={createPageUrl('AdminHours')} className="block">
                  <Button variant="outline" className="w-full justify-start border-stone-200">
                    <Clock className="w-4 h-4 mr-3 text-amber-600" />
                    Horarios de Atención
                  </Button>
                </Link>
                <Link to={createPageUrl('AdminClients')} className="block">
                  <Button variant="outline" className="w-full justify-start border-stone-200">
                    <Users className="w-4 h-4 mr-3 text-amber-600" />
                    Ver Clientes
                  </Button>
                </Link>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Resumen</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-stone-500">Servicios activos</span>
                  <span className="font-semibold">{services.filter(s => s.is_active).length}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-stone-500">Profesionales</span>
                  <span className="font-semibold">{professionals.filter(p => p.is_active).length}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-stone-500">Turnos pendientes</span>
                  <span className="font-semibold">{pendingAppointments.length}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-stone-500">Turnos completados</span>
                  <span className="font-semibold">{completedAppointments.length}</span>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
}