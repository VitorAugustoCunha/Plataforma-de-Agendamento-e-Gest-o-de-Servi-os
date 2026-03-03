-- Create ENUM types
CREATE TYPE user_role AS ENUM ('CLIENT', 'PROVIDER', 'ADMIN');
CREATE TYPE appointment_status AS ENUM ('SCHEDULED', 'CANCELED', 'COMPLETED', 'NO_SHOW');
CREATE TYPE payment_status AS ENUM ('PENDING', 'PAID', 'FAILED', 'REFUNDED');
CREATE TYPE payment_method AS ENUM ('CREDIT_CARD', 'DEBIT_CARD', 'PIX', 'BANK_TRANSFER');
CREATE TYPE notification_type AS ENUM ('APPOINTMENT_SCHEDULED', 'APPOINTMENT_REMINDER', 'APPOINTMENT_CANCELED', 'PAYMENT_CONFIRMED', 'APPOINTMENT_COMPLETED');
CREATE TYPE notification_channel AS ENUM ('EMAIL', 'SMS', 'WHATSAPP', 'IN_APP');
CREATE TYPE notification_status AS ENUM ('SENT', 'PENDING', 'FAILED');

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

-- Provider Profile table
CREATE TABLE provider_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    bio VARCHAR(1000),
    location_text VARCHAR(200),
    min_advance_minutes INTEGER DEFAULT 0,
    cancel_window_minutes INTEGER DEFAULT 0,
    slot_step_minutes INTEGER DEFAULT 30,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_provider_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Service Offering table
CREATE TABLE service_offering (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    duration_minutes INTEGER NOT NULL,
    price_cents INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_offering_provider FOREIGN KEY (provider_id) REFERENCES provider_profile(id) ON DELETE CASCADE
);

CREATE INDEX idx_service_provider_active ON service_offering(provider_id, active);

-- Provider Availability table (weekly schedule)
CREATE TABLE provider_availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL,
    day_of_week INTEGER NOT NULL, -- 1=MONDAY to 7=SUNDAY (using DayOfWeek ordinal)
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_provider_availability_provider FOREIGN KEY (provider_id) REFERENCES provider_profile(id) ON DELETE CASCADE
);

-- Provider Time Off table (exceptions: holidays, sick days, etc)
CREATE TABLE provider_time_off (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL,
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_provider_time_off_provider FOREIGN KEY (provider_id) REFERENCES provider_profile(id) ON DELETE CASCADE
);

-- Appointment table
CREATE TABLE appointment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL,
    provider_id UUID NOT NULL,
    service_id UUID NOT NULL,
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status appointment_status NOT NULL DEFAULT 'SCHEDULED',
    cancel_reason VARCHAR(255),
    canceled_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_client FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_provider FOREIGN KEY (provider_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_service FOREIGN KEY (service_id) REFERENCES service_offering(id) ON DELETE RESTRICT
);

CREATE INDEX idx_appointment_provider_start ON appointment(provider_id, start_at);
CREATE INDEX idx_appointment_client_created ON appointment(client_id, created_at);

-- Payment table
CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_id UUID NOT NULL UNIQUE,
    status payment_status NOT NULL,
    method payment_method NOT NULL,
    amount_cents INTEGER NOT NULL,
    external_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(id) ON DELETE CASCADE
);

-- Notification Log table
CREATE TABLE notification_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    type notification_type NOT NULL,
    channel notification_channel NOT NULL,
    payload TEXT,
    status notification_status NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE,
    error_message VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
