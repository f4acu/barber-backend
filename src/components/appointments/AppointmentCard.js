import React from 'react';
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Calendar, Clock, User, Scissors, X, Edit2 } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

const statusConfig = {
  pending: { label: "Pendiente", color: "bg-yellow-100 text-yellow-800 border-yellow-200" },
  confirmed: { label: "Confirmado", color: "bg-green-100 text-green-800 border-green-200" },
  completed: { label: "Completado", color: "bg-blue-100 text-blue-800 border-blue-200" },
  cancelled: { label: "Cancelado", color: "bg-red-100 text-red-800 border-red-200" }
};

const paymentConfig = {
  pending: { label: "Pago pendiente", color: "bg-orange-100 text-orange-800" },
  paid: { label: "Pagado", color: "bg-emerald-100 text-emerald-800" },
  refunded: { label: "Reembolsado", color: "bg-gray-100 text-gray-800" }
};

export default function AppointmentCard({ 
  appointment, 
  onCancel, 
  onEdit,
  onStatusChange,
  showActions = true,
  isAdmin = false 
}) {
  const status = statusConfig[appointment.status] || statusConfig.pending;
  const payment = paymentConfig[appointment.payment_status] || paymentConfig.pending;
  const isPast = new Date(appointment.date) < new Date().setHours(0, 0, 0, 0);
  const canModify = !isPast && appointment.status !== 'cancelled' && appointment.status !== 'completed';

  return (
    <Card className="border-stone-200 hover:shadow-md transition-shadow duration-200">
      <CardContent className="p-5">
        <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
          <div className="flex-1 space-y-3">
            <div className="flex items-center gap-2 flex-wrap">
              <Badge className={`${status.color} border`}>
                {status.label}
              </Badge>
              <Badge className={payment.color}>
                {payment.label}
              </Badge>
            </div>

            <div className="space-y-2">
              <div className="flex items-center gap-2 text-stone-700">
                <Scissors className="w-4 h-4 text-amber-600" />
                <span className="font-medium">{appointment.service_name}</span>
                <span className="text-stone-400">•</span>
                <span className="text-amber-700 font-semibold">${appointment.price?.toLocaleString()}</span>
              </div>

              <div className="flex items-center gap-2 text-stone-600">
                <Calendar className="w-4 h-4 text-stone-400" />
                <span>{format(new Date(appointment.date), "EEEE d 'de' MMMM, yyyy", { locale: es })}</span>
              </div>

              <div className="flex items-center gap-2 text-stone-600">
                <Clock className="w-4 h-4 text-stone-400" />
                <span>{appointment.time} hrs</span>
                <span className="text-stone-400">•</span>
                <span>{appointment.duration} minutos</span>
              </div>

              <div className="flex items-center gap-2 text-stone-600">
                <User className="w-4 h-4 text-stone-400" />
                <span>{appointment.professional_name || "Sin asignar"}</span>
              </div>

              {isAdmin && (
                <div className="pt-2 border-t border-stone-100 mt-2">
                  <p className="text-sm text-stone-600">
                    <span className="font-medium">Cliente:</span> {appointment.client_name}
                  </p>
                  <p className="text-sm text-stone-500">{appointment.client_email}</p>
                  {appointment.client_phone && (
                    <p className="text-sm text-stone-500">{appointment.client_phone}</p>
                  )}
                </div>
              )}
            </div>
          </div>

          {showActions && canModify && (
            <div className="flex sm:flex-col gap-2">
              {isAdmin ? (
                <>
                  <Button
                    size="sm"
                    variant="outline"
                    className="border-green-200 text-green-700 hover:bg-green-50"
                    onClick={() => onStatusChange(appointment.id, 'completed')}
                  >
                    Completar
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    className="border-red-200 text-red-700 hover:bg-red-50"
                    onClick={() => onStatusChange(appointment.id, 'cancelled')}
                  >
                    Cancelar
                  </Button>
                </>
              ) : (
                <>
                  <Button
                    size="sm"
                    variant="outline"
                    className="border-stone-200"
                    onClick={() => onEdit(appointment)}
                  >
                    <Edit2 className="w-4 h-4 mr-1" />
                    Modificar
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    className="border-red-200 text-red-600 hover:bg-red-50"
                    onClick={() => onCancel(appointment.id)}
                  >
                    <X className="w-4 h-4 mr-1" />
                    Cancelar
                  </Button>
                </>
              )}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}