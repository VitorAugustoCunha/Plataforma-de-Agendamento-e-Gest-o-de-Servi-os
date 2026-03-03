-- Seed data for development
-- Users: 1 admin, 1 client, 2 providers

-- Admin user
INSERT INTO users (id, name, email, password_hash, role, active)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Admin User', 'admin@agenda.local', 
        '$2a$10$slYQmyNdGzin7olVnwAjuu34mGJ8x8RQ/R0K0RYtKWKmLZqPmjnBi', 'ADMIN', true)
ON CONFLICT DO NOTHING;

-- Client user
INSERT INTO users (id, name, email, password_hash, role, active)
VALUES ('550e8400-e29b-41d4-a716-446655440002', 'João Cliente', 'cliente@agenda.local', 
        '$2a$10$slYQmyNdGzin7olVnwAjuu34mGJ8x8RQ/R0K0RYtKWKmLZqPmjnBi', 'CLIENT', true)
ON CONFLICT DO NOTHING;

-- Provider 1: Barbeiro
INSERT INTO users (id, name, email, password_hash, role, active)
VALUES ('550e8400-e29b-41d4-a716-446655440003', 'Carlos Barbeiro', 'barbeiro@agenda.local', 
        '$2a$10$slYQmyNdGzin7olVnwAjuu34mGJ8x8RQ/R0K0RYtKWKmLZqPmjnBi', 'PROVIDER', true)
ON CONFLICT DO NOTHING;

-- Provider 2: Personal Trainer
INSERT INTO users (id, name, email, password_hash, role, active)
VALUES ('550e8400-e29b-41d4-a716-446655440004', 'Maria Personal', 'personal@agenda.local', 
        '$2a$10$slYQmyNdGzin7olVnwAjuu34mGJ8x8RQ/R0K0RYtKWKmLZqPmjnBi', 'PROVIDER', true)
ON CONFLICT DO NOTHING;

-- Provider Profiles
INSERT INTO provider_profile (id, user_id, bio, location_text, min_advance_minutes, cancel_window_minutes, slot_step_minutes)
VALUES ('550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440003', 
        'Barbeiro experiente com 10 anos de atuação', 'Centro, São Paulo', 30, 120, 30)
ON CONFLICT DO NOTHING;

INSERT INTO provider_profile (id, user_id, bio, location_text, min_advance_minutes, cancel_window_minutes, slot_step_minutes)
VALUES ('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440004', 
        'Personal trainer certificado em musculação e aeróbico', 'Vila Mariana, São Paulo', 60, 240, 60)
ON CONFLICT DO NOTHING;

-- Services
INSERT INTO service_offering (id, provider_id, name, description, duration_minutes, price_cents, active)
VALUES ('550e8400-e29b-41d4-a716-446655440021', '550e8400-e29b-41d4-a716-446655440011', 
        'Corte Simples', 'Corte padrão com secagem', 30, 5000, true)
ON CONFLICT DO NOTHING;

INSERT INTO service_offering (id, provider_id, name, description, duration_minutes, price_cents, active)
VALUES ('550e8400-e29b-41d4-a716-446655440022', '550e8400-e29b-41d4-a716-446655440011', 
        'Barba Completa', 'Corte de barba com design', 30, 3000, true)
ON CONFLICT DO NOTHING;

INSERT INTO service_offering (id, provider_id, name, description, duration_minutes, price_cents, active)
VALUES ('550e8400-e29b-41d4-a716-446655440023', '550e8400-e29b-41d4-a716-446655440012', 
        'Sessão Personal - 1h', 'Treino personalizado com avaliação', 60, 15000, true)
ON CONFLICT DO NOTHING;

INSERT INTO service_offering (id, provider_id, name, description, duration_minutes, price_cents, active)
VALUES ('550e8400-e29b-41d4-a716-446655440024', '550e8400-e29b-41d4-a716-446655440012', 
        'Avaliação Física', 'Análise corporal completa', 30, 5000, true)
ON CONFLICT DO NOTHING;

-- Provider Availability (Mon-Fri 9am-6pm for barber, Mon-Fri 6am-8pm for personal)
INSERT INTO provider_availability (id, provider_id, day_of_week, start_time, end_time)
VALUES 
    ('550e8400-e29b-41d4-a716-446655550001', '550e8400-e29b-41d4-a716-446655440011', 1, '09:00', '18:00'),
    ('550e8400-e29b-41d4-a716-446655550002', '550e8400-e29b-41d4-a716-446655440011', 2, '09:00', '18:00'),
    ('550e8400-e29b-41d4-a716-446655550003', '550e8400-e29b-41d4-a716-446655440011', 3, '09:00', '18:00'),
    ('550e8400-e29b-41d4-a716-446655550004', '550e8400-e29b-41d4-a716-446655440011', 4, '09:00', '18:00'),
    ('550e8400-e29b-41d4-a716-446655550005', '550e8400-e29b-41d4-a716-446655440011', 5, '09:00', '18:00'),
    ('550e8400-e29b-41d4-a716-446655550006', '550e8400-e29b-41d4-a716-446655440012', 1, '06:00', '20:00'),
    ('550e8400-e29b-41d4-a716-446655550007', '550e8400-e29b-41d4-a716-446655440012', 2, '06:00', '20:00'),
    ('550e8400-e29b-41d4-a716-446655550008', '550e8400-e29b-41d4-a716-446655440012', 3, '06:00', '20:00'),
    ('550e8400-e29b-41d4-a716-446655550009', '550e8400-e29b-41d4-a716-446655440012', 4, '06:00', '20:00'),
    ('550e8400-e29b-41d4-a716-446655550010', '550e8400-e29b-41d4-a716-446655440012', 5, '06:00', '20:00')
ON CONFLICT DO NOTHING;
