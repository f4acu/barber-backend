import React from 'react';
import { Button } from "@/components/ui/button";
import { Clock } from "lucide-react";

export default function TimeSlotPicker({ 
  availableSlots, 
  selectedTime, 
  onSelectTime,
  isLoading 
}) {
  if (isLoading) {
    return (
      <div className="grid grid-cols-4 sm:grid-cols-6 gap-2">
        {[...Array(12)].map((_, i) => (
          <div key={i} className="h-10 bg-stone-100 rounded-lg animate-pulse" />
        ))}
      </div>
    );
  }

  if (!availableSlots || availableSlots.length === 0) {
    return (
      <div className="text-center py-8 text-stone-500">
        <Clock className="w-12 h-12 mx-auto mb-3 opacity-50" />
        <p>No hay horarios disponibles para esta fecha</p>
        <p className="text-sm mt-1">Por favor, selecciona otra fecha</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-4 sm:grid-cols-6 gap-2">
      {availableSlots.map((slot) => (
        <Button
          key={slot}
          variant={selectedTime === slot ? "default" : "outline"}
          className={`h-10 text-sm transition-all duration-200 ${
            selectedTime === slot 
              ? 'bg-amber-600 hover:bg-amber-700 text-white' 
              : 'border-stone-200 hover:border-amber-400 hover:text-amber-700'
          }`}
          onClick={() => onSelectTime(slot)}
        >
          {slot}
        </Button>
      ))}
    </div>
  );
}