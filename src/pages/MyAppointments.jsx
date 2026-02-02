import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { base44 } from '@/api/base44Client';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { 
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { motion, AnimatePresence } from "framer-motion";
import { 
  Calendar, 
  Clock, 
  Plus, 
  Scissors,
  AlertCircle,
  Loader2,
  CalendarX
} from "lucide-react";
import AppointmentCard from '../components/appointments/AppointmentCard';

export default function MyAppointments() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [cancelDialog, setCancelDialog] = useState({ open: false, id: null });
  const [activeTab, setActiveTab] = useState('upcoming');

  useEffect(() => {
    const loadUser = async () => {
      try {
        const isAuth = await base44.auth.isAuthenticated();
        if (!isAuth) {
          base44.auth.redirectToLogin(createPageUrl('MyAppointments'));
          return;
        }
        const currentUser = await base44.auth.me();
        setUser(currentUser);
      } catch (e) {
        base44.auth.redirectToLogin(createPageUrl('MyAppointments'));
      } finally {
        setIsLoading(false);
      }
    };
    loadUser();
  }, []);

  const { data: appointments = [], isLoading: loadingAppointments } = useQuery({
    queryKey: ['myAppointments', user?.email],
    queryFn: () => base44.entities.Appointment.filter({ client_email: user.email }, '-date'),
    enabled: !!user?.email
  });

  const cancelMutation = useMutation({
    mutationFn: (id) => base44.entities.Appointment.update(id, { status: 'cancelled' }),
    onSuccess: () => {
      queryClient.invalidateQueries(['myAppointments']);
      setCancelDialog({ open: false, id: null });
    }
  });

  const today = new Date().toISOString().split('T')[0];
  
  const upcomingAppointments = appointments.filter(
    apt => apt.date >= today && apt.status !== 'cancelled' && apt.status !== 'completed'
  );
  
  const pastAppointments = appointments.filter(
    apt => apt.date < today || apt.status === 'completed'
  );
  
  const cancelledAppointments = appointments.filter(
    apt => apt.status === 'cancelled'
  );

  if (isLoading) {
    return (
      <div className="min-h-screen bg-stone-50 flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-amber-600" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-stone-50">
      {/* Header */}
      <div className="bg-white border-b border-stone-200">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-2xl font-bold text-stone-800">Mis Turnos</h1>
              <p className="text-stone-500 mt-1">Gestiona tus reservas</p>
            </div>
            <Link to={createPageUrl('Booking')}>
              <Button className="bg-amber-600 hover:bg-amber-700 text-white">
                <Plus className="w-4 h-4 mr-2" />
                Nuevo Turno
              </Button>
            </Link>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="bg-white border border-stone-200 p-1 mb-6">
            <TabsTrigger value="upcoming" className="data-[state=active]:bg-amber-600 data-[state=active]:text-white">
              Próximos ({upcomingAppointments.length})
            </TabsTrigger>
            <TabsTrigger value="past" className="data-[state=active]:bg-amber-600 data-[state=active]:text-white">
              Pasados ({pastAppointments.length})
            </TabsTrigger>
            <TabsTrigger value="cancelled" className="data-[state=active]:bg-amber-600 data-[state=active]:text-white">
              Cancelados ({cancelledAppointments.length})
            </TabsTrigger>
          </TabsList>

          <TabsContent value="upcoming">
            {loadingAppointments ? (
              <div className="space-y-4">
                {[1, 2].map(i => (
                  <Card key={i} className="animate-pulse">
                    <CardContent className="p-6">
                      <div className="h-24 bg-stone-100 rounded" />
                    </CardContent>
                  </Card>
                ))}
              </div>
            ) : upcomingAppointments.length > 0 ? (
              <div className="space-y-4">
                <AnimatePresence>
                  {upcomingAppointments.map((apt, i) => (
                    <motion.div
                      key={apt.id}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -20 }}
                      transition={{ delay: i * 0.05 }}
                    >
                      <AppointmentCard
                        appointment={apt}
                        onCancel={(id) => setCancelDialog({ open: true, id })}
                        onEdit={(apt) => navigate(createPageUrl('Booking') + `?edit=${apt.id}`)}
                      />
                    </motion.div>
                  ))}
                </AnimatePresence>
              </div>
            ) : (
              <Card className="border-dashed">
                <CardContent className="py-16 text-center">
                  <Calendar className="w-16 h-16 mx-auto mb-4 text-stone-300" />
                  <h3 className="text-lg font-medium text-stone-700 mb-2">
                    No tienes turnos próximos
                  </h3>
                  <p className="text-stone-500 mb-6">
                    Reserva tu próxima cita ahora
                  </p>
                  <Link to={createPageUrl('Booking')}>
                    <Button className="bg-amber-600 hover:bg-amber-700 text-white">
                      <Plus className="w-4 h-4 mr-2" />
                      Reservar Turno
                    </Button>
                  </Link>
                </CardContent>
              </Card>
            )}
          </TabsContent>

          <TabsContent value="past">
            {pastAppointments.length > 0 ? (
              <div className="space-y-4">
                {pastAppointments.map((apt) => (
                  <AppointmentCard
                    key={apt.id}
                    appointment={apt}
                    showActions={false}
                  />
                ))}
              </div>
            ) : (
              <Card className="border-dashed">
                <CardContent className="py-16 text-center">
                  <Clock className="w-16 h-16 mx-auto mb-4 text-stone-300" />
                  <h3 className="text-lg font-medium text-stone-700">
                    No tienes turnos pasados
                  </h3>
                </CardContent>
              </Card>
            )}
          </TabsContent>

          <TabsContent value="cancelled">
            {cancelledAppointments.length > 0 ? (
              <div className="space-y-4">
                {cancelledAppointments.map((apt) => (
                  <AppointmentCard
                    key={apt.id}
                    appointment={apt}
                    showActions={false}
                  />
                ))}
              </div>
            ) : (
              <Card className="border-dashed">
                <CardContent className="py-16 text-center">
                  <CalendarX className="w-16 h-16 mx-auto mb-4 text-stone-300" />
                  <h3 className="text-lg font-medium text-stone-700">
                    No tienes turnos cancelados
                  </h3>
                </CardContent>
              </Card>
            )}
          </TabsContent>
        </Tabs>
      </div>

      {/* Cancel Dialog */}
      <Dialog open={cancelDialog.open} onOpenChange={(open) => setCancelDialog({ ...cancelDialog, open })}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <AlertCircle className="w-5 h-5 text-red-500" />
              Cancelar turno
            </DialogTitle>
            <DialogDescription>
              ¿Estás seguro de que deseas cancelar este turno? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setCancelDialog({ open: false, id: null })}>
              Volver
            </Button>
            <Button 
              variant="destructive"
              onClick={() => cancelMutation.mutate(cancelDialog.id)}
              disabled={cancelMutation.isPending}
            >
              {cancelMutation.isPending ? (
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
              ) : null}
              Sí, cancelar turno
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}