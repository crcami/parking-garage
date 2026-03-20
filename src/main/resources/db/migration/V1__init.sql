CREATE TABLE garage_sectors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    max_capacity INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_garage_sectors_code UNIQUE (code)
);

CREATE TABLE parking_spots (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    external_spot_id BIGINT NOT NULL,
    sector_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    occupied BIT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_parking_spots_external_spot_id UNIQUE (external_spot_id),
    CONSTRAINT fk_parking_spots_sector
        FOREIGN KEY (sector_id) REFERENCES garage_sectors (id)
);

CREATE TABLE parking_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    sector_code VARCHAR(30) NOT NULL,
    spot_id BIGINT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP NULL,
    pricing_multiplier DECIMAL(5, 2) NOT NULL,
    hourly_rate_snapshot DECIMAL(10, 2) NOT NULL,
    amount_charged DECIMAL(10, 2) NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_parking_sessions_spot
        FOREIGN KEY (spot_id) REFERENCES parking_spots (id)
);

CREATE INDEX idx_parking_sessions_plate_status
    ON parking_sessions (license_plate, status);

CREATE INDEX idx_parking_sessions_exit_sector
    ON parking_sessions (exit_time, sector_code);
