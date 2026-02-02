import React from 'react';
import { Link } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { base44 } from '@/api/base44Client';
import { useQuery } from '@tanstack/react-query';
import { motion } from "framer-motion";
import { 
  Scissors, 
  Calendar, 
  Clock, 
  Star, 
  ArrowRight, 
  Phone,
  MapPin,
  Instagram
} from "lucide-react";
import ServiceCard from '../components/ui/ServiceCard';

export default function Home() {
  const { data: services = [] } = useQuery({
    queryKey: ['services'],
    queryFn: () => base44.entities.Service.filter({ is_active: true }),
  });

  const { data: professionals = [] } = useQuery({
    queryKey: ['professionals'],
    queryFn: () => base44.entities.Professional.filter({ is_active: true }),
  });

  return (
    <div className="min-h-screen bg-stone-50">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-br from-stone-900 via-stone-800 to-stone-900 text-white">
        <div className="absolute inset-0 opacity-10">
          <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=1920')] bg-cover bg-center" />
        </div>
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 lg:py-32">
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="max-w-3xl"
          >
            <div className="flex items-center gap-2 mb-6">
              <div className="w-12 h-0.5 bg-amber-500" />
              <span className="text-amber-400 font-medium tracking-wider text-sm uppercase">
                Barbería Premium
              </span>
            </div>
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold leading-tight mb-6">
              Donde el estilo<br />
              <span className="text-amber-400">se encuentra</span><br />
              con la excelencia
            </h1>
            <p className="text-lg text-stone-300 mb-8 max-w-xl">
              Experimenta el arte del cuidado personal con nuestros profesionales expertos. 
              Reserva tu turno y transforma tu look.
            </p>
            <div className="flex flex-wrap gap-4">
              <Link to={createPageUrl('Booking')}>
                <Button size="lg" className="bg-amber-600 hover:bg-amber-700 text-white h-14 px-8 text-base">
                  <Calendar className="w-5 h-5 mr-2" />
                  Reservar Turno
                </Button>
              </Link>
              <a href="#services">
                <Button size="lg" variant="outline" className="border-white/30 text-white hover:bg-white/10 h-14 px-8 text-base">
                  Ver Servicios
                  <ArrowRight className="w-5 h-5 ml-2" />
                </Button>
              </a>
            </div>
          </motion.div>
        </div>
        <div className="absolute bottom-0 left-0 right-0 h-24 bg-gradient-to-t from-stone-50 to-transparent" />
      </section>

      {/* Features */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[
              { icon: Calendar, title: "Reserva Online", desc: "Agenda tu turno 24/7 desde cualquier dispositivo" },
              { icon: Star, title: "Profesionales Top", desc: "Barberos certificados con años de experiencia" },
              { icon: Clock, title: "Puntualidad", desc: "Respetamos tu tiempo, sin esperas innecesarias" }
            ].map((feature, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1 }}
              >
                <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
                  <CardContent className="p-6 text-center">
                    <div className="w-14 h-14 mx-auto mb-4 rounded-full bg-amber-100 flex items-center justify-center">
                      <feature.icon className="w-7 h-7 text-amber-600" />
                    </div>
                    <h3 className="font-semibold text-lg text-stone-800 mb-2">{feature.title}</h3>
                    <p className="text-stone-500">{feature.desc}</p>
                  </CardContent>
                </Card>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Services */}
      <section id="services" className="py-20 bg-stone-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <motion.div 
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            className="text-center mb-12"
          >
            <h2 className="text-3xl sm:text-4xl font-bold text-stone-800 mb-4">Nuestros Servicios</h2>
            <p className="text-stone-500 max-w-2xl mx-auto">
              Ofrecemos una amplia gama de servicios para realzar tu estilo personal
            </p>
          </motion.div>

          {services.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {services.slice(0, 8).map((service, i) => (
                <motion.div
                  key={service.id}
                  initial={{ opacity: 0, y: 20 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true }}
                  transition={{ delay: i * 0.05 }}
                >
                  <ServiceCard service={service} onSelect={() => {}} />
                </motion.div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-stone-500">
              <Scissors className="w-16 h-16 mx-auto mb-4 opacity-30" />
              <p>Los servicios se mostrarán aquí</p>
            </div>
          )}

          <div className="text-center mt-12">
            <Link to={createPageUrl('Booking')}>
              <Button size="lg" className="bg-stone-800 hover:bg-stone-900 text-white">
                Ver todos los servicios
                <ArrowRight className="w-5 h-5 ml-2" />
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Team */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <motion.div 
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            className="text-center mb-12"
          >
            <h2 className="text-3xl sm:text-4xl font-bold text-stone-800 mb-4">Nuestro Equipo</h2>
            <p className="text-stone-500 max-w-2xl mx-auto">
              Profesionales apasionados dedicados a brindarte la mejor experiencia
            </p>
          </motion.div>

          {professionals.length > 0 ? (
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
              {professionals.map((pro, i) => (
                <motion.div
                  key={pro.id}
                  initial={{ opacity: 0, scale: 0.95 }}
                  whileInView={{ opacity: 1, scale: 1 }}
                  viewport={{ once: true }}
                  transition={{ delay: i * 0.1 }}
                  className="text-center"
                >
                  <div className="relative w-32 h-32 mx-auto mb-4">
                    {pro.image_url ? (
                      <img 
                        src={pro.image_url} 
                        alt={pro.name}
                        className="w-full h-full rounded-full object-cover ring-4 ring-stone-100"
                      />
                    ) : (
                      <div className="w-full h-full rounded-full bg-gradient-to-br from-stone-200 to-stone-300 flex items-center justify-center ring-4 ring-stone-100">
                        <span className="text-4xl text-stone-500">
                          {pro.name.charAt(0)}
                        </span>
                      </div>
                    )}
                  </div>
                  <h3 className="font-semibold text-stone-800">{pro.name}</h3>
                  <p className="text-sm text-stone-500">{pro.specialty || "Barbero"}</p>
                </motion.div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-stone-500">
              <p>El equipo se mostrará aquí</p>
            </div>
          )}
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 bg-gradient-to-br from-stone-800 to-stone-900 text-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
          >
            <h2 className="text-3xl sm:text-4xl font-bold mb-6">
              ¿Listo para tu nuevo look?
            </h2>
            <p className="text-lg text-stone-300 mb-8">
              Reserva tu turno ahora y deja que nuestros expertos cuiden de ti
            </p>
            <Link to={createPageUrl('Booking')}>
              <Button size="lg" className="bg-amber-600 hover:bg-amber-700 text-white h-14 px-10 text-lg">
                <Calendar className="w-5 h-5 mr-2" />
                Reservar Ahora
              </Button>
            </Link>
          </motion.div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-stone-900 text-stone-400 py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <Scissors className="w-6 h-6 text-amber-500" />
                <span className="text-xl font-bold text-white">BarberShop</span>
              </div>
              <p className="text-sm">
                Tu destino para el cuidado personal de calidad premium
              </p>
            </div>
            <div>
              <h4 className="font-semibold text-white mb-4">Contacto</h4>
              <div className="space-y-2 text-sm">
                <p className="flex items-center gap-2">
                  <Phone className="w-4 h-4" /> +54 11 1234-5678
                </p>
                <p className="flex items-center gap-2">
                  <MapPin className="w-4 h-4" /> Av. Principal 123, Buenos Aires
                </p>
                <p className="flex items-center gap-2">
                  <Instagram className="w-4 h-4" /> @barbershop
                </p>
              </div>
            </div>
            <div>
              <h4 className="font-semibold text-white mb-4">Horarios</h4>
              <div className="space-y-1 text-sm">
                <p>Lunes a Viernes: 9:00 - 20:00</p>
                <p>Sábados: 9:00 - 18:00</p>
                <p>Domingos: Cerrado</p>
              </div>
            </div>
          </div>
          <div className="border-t border-stone-800 mt-8 pt-8 text-center text-sm">
            <p>© 2024 BarberShop. Todos los derechos reservados.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}