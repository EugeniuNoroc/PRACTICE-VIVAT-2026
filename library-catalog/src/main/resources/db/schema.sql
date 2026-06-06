CREATE TABLE IF NOT EXISTS authors (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       full_name VARCHAR(255) NOT NULL,
                                       birth_year INT,
                                       biography TEXT
);

CREATE TABLE IF NOT EXISTS books (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     title VARCHAR(255) NOT NULL,
                                     isbn VARCHAR(20) UNIQUE NOT NULL,
                                     year INT,
                                     author_id UUID REFERENCES authors(id),
                                     copies_total INT NOT NULL DEFAULT 0,
                                     copies_available INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS readers (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       full_name VARCHAR(255) NOT NULL,
                                       email VARCHAR(255) UNIQUE NOT NULL,
                                       registration_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS loans (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     book_id UUID NOT NULL REFERENCES books(id),
                                     reader_id UUID NOT NULL REFERENCES readers(id),
                                     loan_date DATE NOT NULL,
                                     return_date DATE,
                                     returned BOOLEAN NOT NULL DEFAULT FALSE
);