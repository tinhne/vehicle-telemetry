-- Vehicle Telemetry DB — runs once on first container creation
CREATE TABLE IF NOT EXISTS telemetry_records (
    id          BIGSERIAL PRIMARY KEY,
    vehicle_id  VARCHAR(50)      NOT NULL,
    recorded_at TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    speed_kmh   DOUBLE PRECISION NOT NULL,
    rpm         INTEGER          NOT NULL,
    fuel_pct    DOUBLE PRECISION NOT NULL,
    engine_temp DOUBLE PRECISION NOT NULL,
    battery_v   DOUBLE PRECISION NOT NULL,
    latitude    DOUBLE PRECISION,
    longitude   DOUBLE PRECISION,
    tire_fl     DOUBLE PRECISION,
    tire_fr     DOUBLE PRECISION,
    tire_rl     DOUBLE PRECISION,
    tire_rr     DOUBLE PRECISION,
    has_warning BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_vt_time ON telemetry_records (vehicle_id, recorded_at DESC);

CREATE TABLE IF NOT EXISTS warning_logs (
    id            BIGSERIAL PRIMARY KEY,
    vehicle_id    VARCHAR(50) NOT NULL,
    occurred_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    warning_type  VARCHAR(50) NOT NULL,
    severity      VARCHAR(20) NOT NULL,
    message       TEXT        NOT NULL,
    trigger_val   DOUBLE PRECISION,
    threshold_val DOUBLE PRECISION
);
CREATE INDEX IF NOT EXISTS idx_warn_time ON warning_logs (vehicle_id, occurred_at DESC);
