CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE animals (
                         id UUID PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         birth_date DATE NOT NULL,
                         weight DOUBLE PRECISION NOT NULL,
                         health_status VARCHAR(20) NOT NULL,
                         type VARCHAR(20) NOT NULL
);

CREATE TABLE dogs (
                      animal_id UUID PRIMARY KEY REFERENCES animals(id) ON DELETE CASCADE,
                      breed VARCHAR(100) NOT NULL,
                      obedience_level INT NOT NULL
);

CREATE TABLE cats (
                      animal_id UUID PRIMARY KEY REFERENCES animals(id) ON DELETE CASCADE,
                      breed VARCHAR(100) NOT NULL,
                      indoor_only BOOLEAN NOT NULL
);