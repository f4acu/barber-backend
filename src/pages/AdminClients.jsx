import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { base44 } from '@/api/base44Client';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { format } from "date-fns";
import { es } from "date-fns/locale";
import { 
  Users,
  Search,
  ArrowLeft,
  Loader2,
  Mail,
  Phone,
  Calendar
} from "lucide-react";

export default function AdminClients() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const loadUser = async () => {
      try {
        const isAuth = await base44.auth.isAuthenticated();
        if (!isAuth) {
          base44.auth.redirectToLogin(createPageUrl('AdminClients'));
          return;
        }
        const currentUser = await base44.auth.me();
        if (currentUser.role !== 'admin') {
          navigate(createPageUrl('Home'));
          return;
        }
        setUser(currentUser);
      } catch (e) {
        base44.auth.redirectToLogin(createPageUrl('AdminClients'));
      } finally {
        setIsLoading(false);
      }
    };
    loadUser();
  }, [navigate]);

  const { data: users = [], isLoading: loadingUsers } = useQuery({
    queryKey: ['users'],
    queryFn: () => base44.entities.User.list('-created_date', 200),
    enabled: !!user
  });

  const { data: appointments = [] } = useQuery({
    queryKey: ['allAppointments'],
    queryFn: () => base44.entities.Appointment.list(),
    enabled: !!user
  });

  const getClientStats = (email) => {
    const clientAppointments = appointments.filter(apt => apt.client_email === email);
    const completed = clientAppointments.filter(apt => apt.status === 'completed').length;
    const total = clientAppointments.reduce((sum, apt) => sum + (apt.price || 0), 0);
    return { count: clientAppointments.length, completed, total };
  };

  const filteredUsers = users.filter(u => 
    u.role !== 'admin' && (
      !searchTerm ||
      u.full_name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      u.email?.toLowerCase().includes(searchTerm.toLowerCase())
    )
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
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
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
                <h1 className="text-2xl font-bold text-stone-800">Clientes</h1>
                <p className="text-stone-500">Lista de clientes registrados</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Search */}
      <div className="bg-white border-b border-stone-100">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="relative max-w-md">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-stone-400" />
            <Input
              placeholder="Buscar cliente..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Card>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow className="bg-stone-50">
                  <TableHead>Cliente</TableHead>
                  <TableHead>Contacto</TableHead>
                  <TableHead>Turnos</TableHead>
                  <TableHead>Total Gastado</TableHead>
                  <TableHead>Registro</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {loadingUsers ? (
                  <TableRow>
                    <TableCell colSpan={5} className="text-center py-8">
                      <Loader2 className="w-6 h-6 animate-spin mx-auto text-amber-600" />
                    </TableCell>
                  </TableRow>
                ) : filteredUsers.length > 0 ? (
                  filteredUsers.map((client) => {
                    const stats = getClientStats(client.email);
                    return (
                      <TableRow key={client.id}>
                        <TableCell>
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center">
                              <span className="text-amber-700 font-medium">
                                {client.full_name?.charAt(0) || client.email?.charAt(0)?.toUpperCase()}
                              </span>
                            </div>
                            <span className="font-medium text-stone-800">
                              {client.full_name || 'Sin nombre'}
                            </span>
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="space-y-1">
                            <p className="flex items-center gap-2 text-sm text-stone-600">
                              <Mail className="w-3 h-3" />
                              {client.email}
                            </p>
                            {client.phone && (
                              <p className="flex items-center gap-2 text-sm text-stone-500">
                                <Phone className="w-3 h-3" />
                                {client.phone}
                              </p>
                            )}
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center gap-2">
                            <Badge variant="outline">{stats.count} turnos</Badge>
                            {stats.completed > 0 && (
                              <Badge className="bg-green-100 text-green-800">
                                {stats.completed} completados
                              </Badge>
                            )}
                          </div>
                        </TableCell>
                        <TableCell className="font-semibold text-amber-700">
                          ${stats.total.toLocaleString()}
                        </TableCell>
                        <TableCell className="text-stone-500 text-sm">
                          {client.created_date ? 
                            format(new Date(client.created_date), "d MMM yyyy", { locale: es }) :
                            '-'
                          }
                        </TableCell>
                      </TableRow>
                    );
                  })
                ) : (
                  <TableRow>
                    <TableCell colSpan={5} className="text-center py-12">
                      <Users className="w-12 h-12 mx-auto mb-4 text-stone-300" />
                      <p className="text-stone-500">No hay clientes registrados</p>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}