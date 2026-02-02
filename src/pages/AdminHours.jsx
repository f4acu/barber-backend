import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { base44 } from '@/api/base44Client';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { 
  Clock,
  ArrowLeft,
  Loader2,
  Save
} from "lucide-react";

const DAYS = [
  { value: 0, name: 'Domingo' },
  { value: 1, name: 'Lunes' },
  { value: 2, name: 'Martes' },
  { value: 3, name: 'Miércoles' },
  { value: 4, name: 'Jueves' },
  { value: 5, name: 'Viernes' },
  { value: 6, name: 'Sábado' },
];

const defaultHours = DAYS.map(day => ({
  day_of_week: day.value,
  day_name: day.name,
  is_open: day.value !== 0,
  open_time: '09:00',
  close_time: '20:00',
  break_start: '',
  break_end: ''
}));

export default function AdminHours() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [hours, setHours] = useState(defaultHours);
  const [hasChanges, setHasChanges] = useState(false);

  useEffect(() => {
    const loadUser = async () => {
      try {
        const isAuth = await base44.auth.isAuthenticated();
        if (!isAuth) {
          base44.auth.redirectToLogin(createPageUrl('AdminHours'));
          return;
        }
        const currentUser = await base44.auth.me();
        if (currentUser.role !== 'admin') {
          navigate(createPageUrl('Home'));
          return;
        }
        setUser(currentUser);
      } catch (e) {
        base44.auth.redirectToLogin(createPageUrl('AdminHours'));
      } finally {
        setIsLoading(false);
      }
    };
    loadUser();
  }, [navigate]);

  const { data: existingHours = [], isLoading: loadingHours } = useQuery({
    queryKey: ['businessHours'],
    queryFn: () => base44.entities.BusinessHours.list(),
    enabled: !!user
  });

  useEffect(() => {
    if (existingHours.length > 0) {
      const merged = defaultHours.map(dh => {
        const existing = existingHours.find(eh => eh.day_of_week === dh.day_of_week);
        return existing ? { ...dh, ...existing } : dh;
      });
      setHours(merged);
    }
  }, [existingHours]);

  const saveMutation = useMutation({
    mutationFn: async () => {
      // Delete existing hours
      for (const h of existingHours) {
        await base44.entities.BusinessHours.delete(h.id);
      }
      // Create new hours
      for (const h of hours) {
        await base44.entities.BusinessHours.create(h);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['businessHours']);
      setHasChanges(false);
    }
  });

  const updateHour = (dayIndex, field, value) => {
    const newHours = [...hours];
    newHours[dayIndex] = { ...newHours[dayIndex], [field]: value };
    setHours(newHours);
    setHasChanges(true);
  };

  if (isLoading || loadingHours) {
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
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Button 
                variant="ghost" 
                onClick={() => navigate(createPageUrl('AdminDashboard'))}
                className="text-stone-600"
              >
                <ArrowLeft className="w-5 h-5" />
              </Button>
              <div>
                <h1 className="text-2xl font-bold text-stone-800">Horarios de Atención</h1>
                <p className="text-stone-500">Configura los días y horarios de tu negocio</p>
              </div>
            </div>
            <Button 
              onClick={() => saveMutation.mutate()}
              disabled={!hasChanges || saveMutation.isPending}
              className="bg-amber-600 hover:bg-amber-700 text-white"
            >
              {saveMutation.isPending ? (
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
              ) : (
                <Save className="w-4 h-4 mr-2" />
              )}
              Guardar Cambios
            </Button>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="w-5 h-5 text-amber-600" />
              Horario Semanal
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {hours.map((day, index) => (
              <div 
                key={day.day_of_week}
                className={`p-4 rounded-lg border ${day.is_open ? 'bg-white border-stone-200' : 'bg-stone-50 border-stone-100'}`}
              >
                <div className="flex flex-col sm:flex-row sm:items-center gap-4">
                  <div className="flex items-center gap-3 sm:w-40">
                    <Switch
                      checked={day.is_open}
                      onCheckedChange={(checked) => updateHour(index, 'is_open', checked)}
                    />
                    <span className={`font-medium ${day.is_open ? 'text-stone-800' : 'text-stone-400'}`}>
                      {day.day_name}
                    </span>
                  </div>

                  {day.is_open && (
                    <div className="flex flex-wrap gap-4 flex-1">
                      <div className="flex items-center gap-2">
                        <Label className="text-sm text-stone-500 w-14">Abre</Label>
                        <Input
                          type="time"
                          value={day.open_time}
                          onChange={(e) => updateHour(index, 'open_time', e.target.value)}
                          className="w-28"
                        />
                      </div>
                      <div className="flex items-center gap-2">
                        <Label className="text-sm text-stone-500 w-14">Cierra</Label>
                        <Input
                          type="time"
                          value={day.close_time}
                          onChange={(e) => updateHour(index, 'close_time', e.target.value)}
                          className="w-28"
                        />
                      </div>
                      <div className="flex items-center gap-2">
                        <Label className="text-sm text-stone-500 w-14">Pausa</Label>
                        <Input
                          type="time"
                          value={day.break_start || ''}
                          onChange={(e) => updateHour(index, 'break_start', e.target.value)}
                          className="w-28"
                          placeholder="Inicio"
                        />
                        <span className="text-stone-400">-</span>
                        <Input
                          type="time"
                          value={day.break_end || ''}
                          onChange={(e) => updateHour(index, 'break_end', e.target.value)}
                          className="w-28"
                          placeholder="Fin"
                        />
                      </div>
                    </div>
                  )}

                  {!day.is_open && (
                    <span className="text-stone-400 text-sm">Cerrado</span>
                  )}
                </div>
              </div>
            ))}
          </CardContent>
        </Card>

        <Card className="mt-6">
          <CardContent className="p-6">
            <h3 className="font-semibold text-stone-800 mb-2">Consejos</h3>
            <ul className="text-sm text-stone-500 space-y-1">
              <li>• Los turnos se generan cada 30 minutos dentro del horario configurado</li>
              <li>• Configura la pausa si tu negocio cierra durante el almuerzo</li>
              <li>• Los cambios se aplicarán inmediatamente a las nuevas reservas</li>
            </ul>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}