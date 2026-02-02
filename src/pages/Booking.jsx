import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { base44 } from '@/api/base44Client';
import { useQuery, useMutation } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Calendar } from "@/components/ui/calendar";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { motion, AnimatePresence } from "framer-motion";
import { format, addDays, isSameDay, startOfDay } from "date-fns";
import { es } from "date-fns/locale";
import { 
  ArrowLeft, 
  ArrowRight, 
  Scissors, 
  User, 
  Calendar as CalendarIcon,
  Clock,
  CheckCircle2,
  Loader2
} from "lucide-react";
import ServiceCard from '../components/ui/ServiceCard';
import ProfessionalCard from '../components/ui/ProfessionalCard';
import TimeSlotPicker from '../components/booking/TimeSlotPicker';
import BookingSummary from '../components/booking/BookingSummary';

const STEPS = [
  { id: 1, title: "Servicio", icon: Scissors },
  { id: 2, title: "Profesional", icon: User },
  { id: 3, title: "Fecha y Hora", icon: CalendarIcon },
  { id: 4, title: "Confirmar", icon: CheckCircle2 }
];

export default function Booking() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [selectedService, setSelectedService] = useState(null);
  const [selectedProfessional, setSelectedProfessional] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);
  const [clientInfo, setClientInfo] = useState({ name: '', email: '', phone: '', notes: '' });
  const [user, setUser] = useState(null);
  const [isLoadingUser, setIsLoadingUser] = useState(true);

  useEffect(() => {
    const loadUser = async () => {
      try {
        const isAuth = await base44.auth.isAuthenticated();
        if (isAuth) {
          const currentUser = await base44.auth.me();
          setUser(currentUser);
          setClientInfo(prev => ({
            ...prev,
            name: currentUser.full_name || '',
            email: currentUser.email || '',
            phone: currentUser.phone || ''
          }));
        }
      } catch (e) {
        console.log('User not logged in');
      } finally {
        setIsLoadingUser(false);
      }
    };
    loadUser();
  }, []);

  const { data: services = [] } = useQuery({
    queryKey: ['services'],
    queryFn: () => base44.entities.Service.filter({ is_active: true }),
  });

  const { data: professionals = [] } = useQuery({
    queryKey: ['professionals'],
    queryFn: () => base44.entities.Professional.filter({ is_active: true }),
  });

  const { data: businessHours = [] } = useQuery({
    queryKey: ['businessHours'],
    queryFn: () => base44.entities.BusinessHours.list(),
  });

  const { data: appointments = [] } = useQuery({
    queryKey: ['appointments', selectedDate, selectedProfessional?.id],
    queryFn: () => {
      if (!selectedDate) return [];
      return base44.entities.Appointment.filter({
        date: format(selectedDate, 'yyyy-MM-dd'),
        status: 'confirmed'
      });
    },
    enabled: !!selectedDate
  });

  const createAppointmentMutation = useMutation({
    mutationFn: (data) => base44.entities.Appointment.create(data),
    onSuccess: async (result) => {
      // Send confirmation email
      try {
        await base44.integrations.Core.SendEmail({
          to: clientInfo.email,
          subject: `Confirmación de turno - ${selectedService.name}`,
          body: `
            Hola ${clientInfo.name},
            
            Tu turno ha sido confirmado:
            
            📅 Fecha: ${format(selectedDate, "EEEE d 'de' MMMM, yyyy", { locale: es })}
            🕐 Hora: ${selectedTime}
            ✂️ Servicio: ${selectedService.name}
            👤 Profesional: ${selectedProfessional.name}
            💰 Precio: $${selectedService.price.toLocaleString()}
            
            ¡Te esperamos!
            
            BarberShop
          `
        });
      } catch (e) {
        console.log('Email could not be sent');
      }
      navigate(createPageUrl('BookingSuccess') + `?id=${result.id}`);
    }
  });

  const generateTimeSlots = () => {
    if (!selectedDate || !businessHours.length) return [];
    
    const dayOfWeek = selectedDate.getDay();
    const dayHours = businessHours.find(h => h.day_of_week === dayOfWeek);
    
    if (!dayHours || !dayHours.is_open) return [];
    
    const slots = [];
    const [openHour, openMin] = (dayHours.open_time || '09:00').split(':').map(Number);
    const [closeHour, closeMin] = (dayHours.close_time || '20:00').split(':').map(Number);
    
    let currentHour = openHour;
    let currentMin = openMin;
    
    while (currentHour < closeHour || (currentHour === closeHour && currentMin < closeMin)) {
      const timeStr = `${String(currentHour).padStart(2, '0')}:${String(currentMin).padStart(2, '0')}`;
      
      // Check if slot is not already booked
      const isBooked = appointments.some(apt => 
        apt.time === timeStr && 
        apt.professional_id === selectedProfessional?.id
      );
      
      // Check if it's in break time
      let isBreak = false;
      if (dayHours.break_start && dayHours.break_end) {
        const [breakStartH, breakStartM] = dayHours.break_start.split(':').map(Number);
        const [breakEndH, breakEndM] = dayHours.break_end.split(':').map(Number);
        const slotMinutes = currentHour * 60 + currentMin;
        const breakStartMinutes = breakStartH * 60 + breakStartM;
        const breakEndMinutes = breakEndH * 60 + breakEndM;
        isBreak = slotMinutes >= breakStartMinutes && slotMinutes < breakEndMinutes;
      }
      
      if (!isBooked && !isBreak) {
        slots.push(timeStr);
      }
      
      currentMin += 30;
      if (currentMin >= 60) {
        currentHour += 1;
        currentMin = 0;
      }
    }
    
    return slots;
  };

  const handleConfirm = async () => {
    if (!user) {
      base44.auth.redirectToLogin(window.location.href);
      return;
    }

    const appointmentData = {
      client_email: clientInfo.email,
      client_name: clientInfo.name,
      client_phone: clientInfo.phone,
      service_id: selectedService.id,
      service_name: selectedService.name,
      professional_id: selectedProfessional.id,
      professional_name: selectedProfessional.name,
      date: format(selectedDate, 'yyyy-MM-dd'),
      time: selectedTime,
      duration: selectedService.duration,
      price: selectedService.price,
      status: 'confirmed',
      payment_status: 'pending',
      notes: clientInfo.notes
    };

    createAppointmentMutation.mutate(appointmentData);
  };

  const canProceed = () => {
    switch (step) {
      case 1: return !!selectedService;
      case 2: return !!selectedProfessional;
      case 3: return !!selectedDate && !!selectedTime;
      case 4: return clientInfo.name && clientInfo.email;
      default: return false;
    }
  };

  const disabledDays = (date) => {
    if (date < startOfDay(new Date())) return true;
    if (date > addDays(new Date(), 30)) return true;
    
    const dayOfWeek = date.getDay();
    const dayHours = businessHours.find(h => h.day_of_week === dayOfWeek);
    if (dayHours && !dayHours.is_open) return true;
    
    return false;
  };

  if (isLoadingUser) {
    return (
      <div className="min-h-screen bg-stone-50 flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-amber-600" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-stone-50">
      {/* Header */}
      <div className="bg-white border-b border-stone-200 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <Button 
              variant="ghost" 
              onClick={() => step > 1 ? setStep(step - 1) : navigate(createPageUrl('Home'))}
              className="text-stone-600"
            >
              <ArrowLeft className="w-5 h-5 mr-2" />
              {step > 1 ? 'Anterior' : 'Inicio'}
            </Button>
            <h1 className="text-xl font-semibold text-stone-800">Reservar Turno</h1>
            <div className="w-24" />
          </div>
        </div>
      </div>

      {/* Progress Steps */}
      <div className="bg-white border-b border-stone-100">
        <div className="max-w-4xl mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            {STEPS.map((s, i) => (
              <React.Fragment key={s.id}>
                <div className="flex flex-col items-center">
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center transition-colors ${
                    step >= s.id 
                      ? 'bg-amber-600 text-white' 
                      : 'bg-stone-100 text-stone-400'
                  }`}>
                    <s.icon className="w-5 h-5" />
                  </div>
                  <span className={`text-xs mt-2 hidden sm:block ${
                    step >= s.id ? 'text-amber-700 font-medium' : 'text-stone-400'
                  }`}>
                    {s.title}
                  </span>
                </div>
                {i < STEPS.length - 1 && (
                  <div className={`flex-1 h-0.5 mx-2 ${
                    step > s.id ? 'bg-amber-600' : 'bg-stone-200'
                  }`} />
                )}
              </React.Fragment>
            ))}
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <AnimatePresence mode="wait">
              {/* Step 1: Services */}
              {step === 1 && (
                <motion.div
                  key="step1"
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                >
                  <h2 className="text-2xl font-bold text-stone-800 mb-2">Selecciona un servicio</h2>
                  <p className="text-stone-500 mb-6">Elige el servicio que deseas reservar</p>
                  
                  <div className="grid sm:grid-cols-2 gap-4">
                    {services.map((service) => (
                      <ServiceCard
                        key={service.id}
                        service={service}
                        selected={selectedService?.id === service.id}
                        onSelect={setSelectedService}
                      />
                    ))}
                  </div>

                  {services.length === 0 && (
                    <Card className="border-dashed">
                      <CardContent className="py-12 text-center text-stone-500">
                        <Scissors className="w-12 h-12 mx-auto mb-4 opacity-30" />
                        <p>No hay servicios disponibles</p>
                      </CardContent>
                    </Card>
                  )}
                </motion.div>
              )}

              {/* Step 2: Professionals */}
              {step === 2 && (
                <motion.div
                  key="step2"
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                >
                  <h2 className="text-2xl font-bold text-stone-800 mb-2">Elige un profesional</h2>
                  <p className="text-stone-500 mb-6">Selecciona quién te atenderá</p>
                  
                  <div className="grid sm:grid-cols-2 gap-4">
                    {professionals.map((pro) => (
                      <ProfessionalCard
                        key={pro.id}
                        professional={pro}
                        selected={selectedProfessional?.id === pro.id}
                        onSelect={setSelectedProfessional}
                      />
                    ))}
                  </div>

                  {professionals.length === 0 && (
                    <Card className="border-dashed">
                      <CardContent className="py-12 text-center text-stone-500">
                        <User className="w-12 h-12 mx-auto mb-4 opacity-30" />
                        <p>No hay profesionales disponibles</p>
                      </CardContent>
                    </Card>
                  )}
                </motion.div>
              )}

              {/* Step 3: Date & Time */}
              {step === 3 && (
                <motion.div
                  key="step3"
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                >
                  <h2 className="text-2xl font-bold text-stone-800 mb-2">Fecha y hora</h2>
                  <p className="text-stone-500 mb-6">Elige cuándo quieres venir</p>
                  
                  <div className="grid md:grid-cols-2 gap-6">
                    <Card>
                      <CardContent className="p-4">
                        <Calendar
                          mode="single"
                          selected={selectedDate}
                          onSelect={(date) => {
                            setSelectedDate(date);
                            setSelectedTime(null);
                          }}
                          disabled={disabledDays}
                          locale={es}
                          className="rounded-md"
                        />
                      </CardContent>
                    </Card>

                    <Card>
                      <CardHeader className="pb-2">
                        <CardTitle className="text-lg flex items-center gap-2">
                          <Clock className="w-5 h-5 text-amber-600" />
                          Horarios disponibles
                        </CardTitle>
                        {selectedDate && (
                          <p className="text-sm text-stone-500">
                            {format(selectedDate, "EEEE d 'de' MMMM", { locale: es })}
                          </p>
                        )}
                      </CardHeader>
                      <CardContent>
                        {selectedDate ? (
                          <TimeSlotPicker
                            availableSlots={generateTimeSlots()}
                            selectedTime={selectedTime}
                            onSelectTime={setSelectedTime}
                          />
                        ) : (
                          <div className="text-center py-8 text-stone-400">
                            <CalendarIcon className="w-12 h-12 mx-auto mb-3 opacity-50" />
                            <p>Selecciona una fecha primero</p>
                          </div>
                        )}
                      </CardContent>
                    </Card>
                  </div>
                </motion.div>
              )}

              {/* Step 4: Confirm */}
              {step === 4 && (
                <motion.div
                  key="step4"
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                >
                  <h2 className="text-2xl font-bold text-stone-800 mb-2">Confirmar datos</h2>
                  <p className="text-stone-500 mb-6">Verifica tu información de contacto</p>
                  
                  <Card>
                    <CardContent className="p-6 space-y-4">
                      <div className="grid sm:grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <Label htmlFor="name">Nombre completo *</Label>
                          <Input
                            id="name"
                            value={clientInfo.name}
                            onChange={(e) => setClientInfo({...clientInfo, name: e.target.value})}
                            placeholder="Tu nombre"
                          />
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="email">Email *</Label>
                          <Input
                            id="email"
                            type="email"
                            value={clientInfo.email}
                            onChange={(e) => setClientInfo({...clientInfo, email: e.target.value})}
                            placeholder="tu@email.com"
                            disabled={!!user}
                          />
                        </div>
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="phone">Teléfono</Label>
                        <Input
                          id="phone"
                          value={clientInfo.phone}
                          onChange={(e) => setClientInfo({...clientInfo, phone: e.target.value})}
                          placeholder="+54 11 1234-5678"
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="notes">Notas adicionales</Label>
                        <Textarea
                          id="notes"
                          value={clientInfo.notes}
                          onChange={(e) => setClientInfo({...clientInfo, notes: e.target.value})}
                          placeholder="¿Alguna preferencia o comentario?"
                          rows={3}
                        />
                      </div>

                      {!user && (
                        <div className="bg-amber-50 border border-amber-200 rounded-lg p-4">
                          <p className="text-sm text-amber-800">
                            Para confirmar tu turno, necesitas iniciar sesión o crear una cuenta.
                          </p>
                          <Button 
                            className="mt-3 bg-amber-600 hover:bg-amber-700"
                            onClick={() => base44.auth.redirectToLogin(window.location.href)}
                          >
                            Iniciar Sesión / Registrarse
                          </Button>
                        </div>
                      )}
                    </CardContent>
                  </Card>
                </motion.div>
              )}
            </AnimatePresence>

            {/* Navigation Buttons */}
            <div className="flex justify-between mt-8">
              <Button
                variant="outline"
                onClick={() => setStep(step - 1)}
                disabled={step === 1}
                className="border-stone-300"
              >
                <ArrowLeft className="w-4 h-4 mr-2" />
                Anterior
              </Button>
              
              {step < 4 ? (
                <Button
                  onClick={() => setStep(step + 1)}
                  disabled={!canProceed()}
                  className="bg-amber-600 hover:bg-amber-700 text-white"
                >
                  Siguiente
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Button>
              ) : (
                <Button
                  onClick={handleConfirm}
                  disabled={!canProceed() || createAppointmentMutation.isPending || !user}
                  className="bg-amber-600 hover:bg-amber-700 text-white"
                >
                  {createAppointmentMutation.isPending ? (
                    <>
                      <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                      Confirmando...
                    </>
                  ) : (
                    <>
                      <CheckCircle2 className="w-4 h-4 mr-2" />
                      Confirmar Turno
                    </>
                  )}
                </Button>
              )}
            </div>
          </div>

          {/* Summary Sidebar */}
          <div className="hidden lg:block">
            <BookingSummary
              service={selectedService}
              professional={selectedProfessional}
              date={selectedDate}
              time={selectedTime}
              onConfirm={handleConfirm}
              isLoading={createAppointmentMutation.isPending}
            />
          </div>
        </div>
      </div>
    </div>
  );
}