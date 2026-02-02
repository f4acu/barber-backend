import React from 'react';
import { Link } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { base44 } from '@/api/base44Client';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { motion } from "framer-motion";
import { format } from "date-fns";
import { es } from "date-fns/locale";
import { 
  CheckCircle2, 
  Calendar, 
  Clock, 
  User, 
  Scissors,
  Home,
  CalendarCheck
} from "lucide-react";

export default function BookingSuccess() {
  const urlParams = new URLSearchParams(window.location.search);
  const appointmentId = urlParams.get('id');

  const { data: appointment } = useQuery({
    queryKey: ['appointment', appointmentId],
    queryFn: async () => {
      const appointments = await base44.entities.Appointment.filter({ id: appointmentId });
      return appointments[0];
    },
    enabled: !!appointmentId
  });

  return (
    <div className="min-h-screen bg-gradient-to-br from-stone-50 to-stone-100 flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5 }}
        className="max-w-md w-full"
      >
        <Card className="border-0 shadow-xl">
          <CardContent className="p-8 text-center">
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ delay: 0.2, type: "spring", stiffness: 200 }}
              className="w-20 h-20 mx-auto mb-6 rounded-full bg-green-100 flex items-center justify-center"
            >
              <CheckCircle2 className="w-10 h-10 text-green-600" />
            </motion.div>

            <h1 className="text-2xl font-bold text-stone-800 mb-2">
              ¡Turno Confirmado!
            </h1>
            <p className="text-stone-500 mb-6">
              Hemos enviado los detalles a tu email
            </p>

            {appointment && (
              <div className="bg-stone-50 rounded-xl p-6 mb-6 text-left space-y-4">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center">
                    <Scissors className="w-5 h-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-sm text-stone-500">Servicio</p>
                    <p className="font-medium text-stone-800">{appointment.service_name}</p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center">
                    <User className="w-5 h-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-sm text-stone-500">Profesional</p>
                    <p className="font-medium text-stone-800">{appointment.professional_name}</p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center">
                    <Calendar className="w-5 h-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-sm text-stone-500">Fecha</p>
                    <p className="font-medium text-stone-800">
                      {format(new Date(appointment.date), "EEEE d 'de' MMMM", { locale: es })}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center">
                    <Clock className="w-5 h-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-sm text-stone-500">Hora</p>
                    <p className="font-medium text-stone-800">{appointment.time} hrs</p>
                  </div>
                </div>

                <div className="pt-4 border-t border-stone-200 flex justify-between items-center">
                  <span className="text-stone-600">Total a pagar</span>
                  <span className="text-xl font-bold text-amber-700">
                    ${appointment.price?.toLocaleString()}
                  </span>
                </div>
              </div>
            )}

            <div className="space-y-3">
              <Link to={createPageUrl('MyAppointments')} className="block">
                <Button className="w-full bg-amber-600 hover:bg-amber-700 text-white">
                  <CalendarCheck className="w-4 h-4 mr-2" />
                  Ver mis turnos
                </Button>
              </Link>
              <Link to={createPageUrl('Home')} className="block">
                <Button variant="outline" className="w-full border-stone-300">
                  <Home className="w-4 h-4 mr-2" />
                  Volver al inicio
                </Button>
              </Link>
            </div>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  );
}