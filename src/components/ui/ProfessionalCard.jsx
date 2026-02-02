import React from 'react';
import { Card, CardContent } from "@/components/ui/card";
import { User, Star } from "lucide-react";
import { motion } from "framer-motion";

export default function ProfessionalCard({ professional, onSelect, selected }) {
  return (
    <motion.div
      whileHover={{ scale: 1.02 }}
      transition={{ duration: 0.2 }}
    >
      <Card 
        className={`cursor-pointer transition-all duration-300 ${
          selected 
            ? 'ring-2 ring-amber-600 bg-amber-50/50' 
            : 'hover:shadow-md border-stone-200'
        }`}
        onClick={() => onSelect(professional)}
      >
        <CardContent className="p-4 flex items-center gap-4">
          <div className="relative">
            {professional.image_url ? (
              <img 
                src={professional.image_url} 
                alt={professional.name}
                className="w-16 h-16 rounded-full object-cover"
              />
            ) : (
              <div className="w-16 h-16 rounded-full bg-gradient-to-br from-stone-200 to-stone-300 flex items-center justify-center">
                <User className="w-8 h-8 text-stone-500" />
              </div>
            )}
            {selected && (
              <div className="absolute -top-1 -right-1 w-5 h-5 bg-amber-600 rounded-full flex items-center justify-center">
                <span className="text-white text-xs">✓</span>
              </div>
            )}
          </div>
          <div className="flex-1">
            <h3 className="font-semibold text-stone-800">{professional.name}</h3>
            {professional.specialty && (
              <p className="text-sm text-stone-500">{professional.specialty}</p>
            )}
            <div className="flex items-center gap-1 mt-1">
              {[...Array(5)].map((_, i) => (
                <Star key={i} className="w-3 h-3 fill-amber-400 text-amber-400" />
              ))}
            </div>
          </div>
        </CardContent>
      </Card>
    </motion.div>
  );
}