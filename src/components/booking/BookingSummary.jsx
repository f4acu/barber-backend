import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { Calendar, Clock, User, Scissors, CreditCard, Loader2 } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

export default function BookingSummary({ 
  service, 
  professional, 
  date, 
  time, 
  onConfirm,
  isLoading 
}) {
  const isComplete = service && professional && date && time;

  return (
    <Card className="sticky top-4 border-stone-200 shadow-lg">
      <CardHeader className="pb-4">
        <CardTitle className="flex items-center gap-2 text-stone-800">
          <Scissors className="w-5 h-5 text-amber-600" />
          Resumen de tu turno
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {service ? (
          <div className="flex items-start gap-3 p-3 bg-stone-50 rounded-lg">
            <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center flex-shrink-0">
              <Scissors className="w-5 h-5 text-amber-600" />
            </div>
            <div>
              <p className="font-medium text-stone-800">{service.name}</p>
              <p className="text-sm text-stone-500">{service.duration} minutos</p>
            </div>
          </div>
        ) : (
          <div className="p-3 border-2 border-dashed border-stone-200 rounded-lg text-center text-stone-400">
            Selecciona un servicio
          </div>
        )}

        {professional ? (
          <div className="flex items-start gap-3 p-3 bg-stone-50 rounded-lg">
            <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center flex-shrink-0">
              <User className="w-5 h-5 text-amber-600" />
            </div>
            <div>
              <p className="font-medium text-stone-800">{professional.name}</p>
              {professional.specialty && (
                <p className="text-sm text-stone-500">{professional.specialty}</p>
              )}
            </div>
          </div>
        ) : (
          <div className="p-3 border-2 border-dashed border-stone-200 rounded-lg text-center text-stone-400">
            Selecciona un profesional
          </div>
        )}

        {date && time ? (
          <div className="flex items-start gap-3 p-3 bg-stone-50 rounded-lg">
            <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center flex-shrink-0">
              <Calendar className="w-5 h-5 text-amber-600" />
            </div>
            <div>
              <p className="font-medium text-stone-800">
                {format(date, "EEEE d 'de' MMMM", { locale: es })}
              </p>
              <p className="text-sm text-stone-500 flex items-center gap-1">
                <Clock className="w-3 h-3" />
                {time} hrs
              </p>
            </div>
          </div>
        ) : (
          <div className="p-3 border-2 border-dashed border-stone-200 rounded-lg text-center text-stone-400">
            Selecciona fecha y hora
          </div>
        )}

        <Separator />

        <div className="flex justify-between items-center">
          <span className="text-stone-600">Total</span>
          <span className="text-2xl font-bold text-amber-700">
            ${service ? service.price.toLocaleString() : '0'}
          </span>
        </div>

        <Button 
          className="w-full bg-amber-600 hover:bg-amber-700 text-white h-12 text-base"
          disabled={!isComplete || isLoading}
          onClick={onConfirm}
        >
          {isLoading ? (
            <>
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
              Procesando...
            </>
          ) : (
            <>
              <CreditCard className="w-4 h-4 mr-2" />
              Confirmar Turno
            </>
          )}
        </Button>

        <p className="text-xs text-center text-stone-400">
          Recibirás un email de confirmación
        </p>
      </CardContent>
    </Card>
  );
}