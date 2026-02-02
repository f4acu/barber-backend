import React from 'react';
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Clock, DollarSign } from "lucide-react";
import { motion } from "framer-motion";

const categoryIcons = {
  corte: "✂️",
  color: "🎨",
  barba: "🧔",
  peinado: "💇",
  tratamiento: "✨",
  combo: "🎁"
};

export default function ServiceCard({ service, onSelect, selected }) {
  return (
    <motion.div
      whileHover={{ y: -4 }}
      transition={{ duration: 0.2 }}
    >
      <Card 
        className={`cursor-pointer transition-all duration-300 overflow-hidden group ${
          selected 
            ? 'ring-2 ring-amber-600 bg-amber-50/50' 
            : 'hover:shadow-lg border-stone-200'
        }`}
        onClick={() => onSelect(service)}
      >
        <div className="relative h-32 bg-gradient-to-br from-stone-100 to-stone-200 flex items-center justify-center">
          <span className="text-5xl opacity-80 group-hover:scale-110 transition-transform duration-300">
            {categoryIcons[service.category] || "💈"}
          </span>
          {selected && (
            <div className="absolute top-2 right-2 w-6 h-6 bg-amber-600 rounded-full flex items-center justify-center">
              <span className="text-white text-xs">✓</span>
            </div>
          )}
        </div>
        <CardContent className="p-4">
          <h3 className="font-semibold text-stone-800 mb-1">{service.name}</h3>
          {service.description && (
            <p className="text-sm text-stone-500 mb-3 line-clamp-2">{service.description}</p>
          )}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-1 text-stone-600">
              <Clock className="w-4 h-4" />
              <span className="text-sm">{service.duration} min</span>
            </div>
            <div className="flex items-center gap-1 text-amber-700 font-semibold">
              <DollarSign className="w-4 h-4" />
              <span>{service.price.toLocaleString()}</span>
            </div>
          </div>
        </CardContent>
      </Card>
    </motion.div>
  );
}