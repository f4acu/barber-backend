-- Insertar 3 peluquerías
INSERT INTO barbershops (id, name, address, phone, slug, description, active) 
VALUES 
    (1, 'Barberia 1', 'Av. Corrientes 1234, CABA', '11-5555-0001', 'barberia-1', 'Barbería premium en el corazón de Buenos Aires', true),
    (2, 'Barberia 2', 'Av. Santa Fe 5678, CABA', '11-5555-0002', 'barberia-2', 'Estilo urbano y moderno para hombres y mujeres', true),
    (3, 'Barberia 3', 'Av. Belgrano 9101, CABA', '11-5555-0003', 'barberia-3', 'Tradición y elegancia desde 1985', true)
ON CONFLICT DO NOTHING;

-- Profesionales para barberia 1
INSERT INTO professionals (id, name, specialty, active, barbershop_id) 
VALUES 
    (1, 'Carlos Pérez', 'Corte y barba', true, 1),
    (2, 'Ana García', 'Coloración', true, 1)
ON CONFLICT DO NOTHING;

-- Profesionales para barberia 2
INSERT INTO professionals (id, name, specialty, active, barbershop_id) 
VALUES 
    (3, 'Juan Martínez', 'Fade y diseños', true, 2),
    (4, 'Laura Sánchez', 'Estilismo integral', true, 2)
ON CONFLICT DO NOTHING;

-- Profesionales para barberia 3
INSERT INTO professionals (id, name, specialty, active, barbershop_id) 
VALUES 
    (5, 'Roberto Díaz', 'Corte clásico', true, 3),
    (6, 'María López', 'Peinados y tratamientos', true, 3)
ON CONFLICT DO NOTHING;

-- Servicios para barberia 1
INSERT INTO services (id, name, price, duration, active, barbershop_id) 
VALUES 
    (1, 'Corte simple', 3000.00, 30, true, 1),
    (2, 'Corte + Barba', 5000.00, 45, true, 1),
    (3, 'Coloración completa', 8000.00, 90, true, 1)
ON CONFLICT DO NOTHING;

-- Servicios para barberia 2
INSERT INTO services (id, name, price, duration, active, barbershop_id) 
VALUES 
    (4, 'Fade profesional', 3500.00, 40, true, 2),
    (5, 'Diseño artístico', 4500.00, 60, true, 2)
ON CONFLICT DO NOTHING;

-- Servicios para barberia 3
INSERT INTO services (id, name, price, duration, active, barbershop_id) 
VALUES 
    (6, 'Corte clásico', 2800.00, 30, true, 3),
    (7, 'Afeitado tradicional', 3200.00, 30, true, 3)
ON CONFLICT DO NOTHING;