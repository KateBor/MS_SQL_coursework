CREATE TABLE jobs
(
    job_id        INT IDENTITY (1,1) PRIMARY KEY,
    job_name      VARCHAR(50) NOT NULL UNIQUE,
    "description" VARCHAR(150)
);

ALTER TABLE jobs
    ADD CONSTRAINT uq_job_name UNIQUE (job_name)

CREATE TABLE departments
(
    department_id   INT IDENTITY (1,1) PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    "description"   VARCHAR(150)
);

ALTER TABLE departments
    ADD CONSTRAINT uq_department_name UNIQUE (department_name)

CREATE TABLE staff --то же, что и аккаунты
(
    staff_id      INT IDENTITY (1,1) PRIMARY KEY,
    role_name     VARCHAR(100)        NOT NULL CHECK (role_name IN ('Администратор', 'Менеджер съёмок', 'Сотрудник')),
    email         VARCHAR(100) UNIQUE NOT NULL, --типа логин
    password_hash VARCHAR(100)        NOT NULL,
    first_name    VARCHAR(30)         NOT NULL,
    last_name     VARCHAR(30)         NOT NULL,
    birthdate     DATE                NOT NULL,
    phone_number  VARCHAR(30) UNIQUE  NOT NULL,

    CONSTRAINT ck_staff__phone_number CHECK (phone_number LIKE
                                             '+[0-9]-[0-9][0-9][0-9]-[0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'),
    CONSTRAINT ck_staff__email CHECK (email LIKE '%[a-zA-Z]%@[a-zA-Z]%.[a-zA-Z]%')
);

CREATE TABLE worker_positions
(
    worker_position_id INT IDENTITY (1,1) PRIMARY KEY,
    staff_id           INT NOT NULL,
    job_id             INT NOT NULL,
    department_id      INT NOT NULL,
    active_staff       BIT DEFAULT 1,

    CONSTRAINT uq_staff_job_department UNIQUE (staff_id, job_id, department_id),
    CONSTRAINT uq_staff_job UNIQUE (staff_id, job_id),
    CONSTRAINT fk_worker_positions__staff FOREIGN KEY (staff_id) REFERENCES staff (staff_id) ON DELETE CASCADE,
    CONSTRAINT fk_worker_positions__jobs FOREIGN KEY (job_id) REFERENCES jobs (job_id) ON DELETE CASCADE,
    CONSTRAINT fk_worker_positions__departments FOREIGN KEY (department_id) REFERENCES departments (department_id) ON DELETE CASCADE
);

CREATE TABLE staff_history
(
    worker_position_id INT         NOT NULL,
    date_action        DATE        NOT NULL,
    "action"           VARCHAR(10) NOT NULL CHECK ("action" IN ('Нанят', 'Уволен')),

    CONSTRAINT pk_staff_history PRIMARY KEY (worker_position_id, date_action, "action"),
    CONSTRAINT fk_staff_history__worker_positions FOREIGN KEY (worker_position_id) REFERENCES worker_positions (worker_position_id)
);

CREATE TABLE movies
(
    movie_id INT IDENTITY (1,1) PRIMARY KEY,
    title    VARCHAR(150) NOT NULL UNIQUE,
    premiere DATE,
    expenses MONEY CHECK (expenses > 0),
    revenue  MONEY CHECK (revenue > 0),
);

ALTER TABLE movies
    ADD CONSTRAINT uq_title UNIQUE (title)

CREATE TABLE movies_history
(
    movie_id    INT         NOT NULL,
    date_action DATE        NOT NULL,
    "action"    VARCHAR(10) NOT NULL CHECK ("action" IN ('Начат', 'Заморожен', 'Разморожен', 'Закрыт', 'Снят')),

    CONSTRAINT pk_movie_history PRIMARY KEY (movie_id, date_action, "action"),
    CONSTRAINT fk_movie_history__movies FOREIGN KEY (movie_id) REFERENCES movies (movie_id)
);

CREATE TABLE employments
(
    employment_id      INT IDENTITY (1,1) PRIMARY KEY,
    worker_position_id INT NOT NULL,
    movie_id           INT NOT NULL,
    fee                MONEY CHECK (fee > 0),

    CONSTRAINT uq_staff_movie UNIQUE (worker_position_id, movie_id),
    CONSTRAINT fk_employments__worker_positions FOREIGN KEY (worker_position_id) REFERENCES worker_positions (worker_position_id) ON DELETE CASCADE,
    CONSTRAINT fk_employments__movies FOREIGN KEY (movie_id) REFERENCES movies (movie_id) ON DELETE CASCADE,
);

CREATE TABLE shooting_locations
(
    location_id INT IDENTITY (1,1) PRIMARY KEY,
    country     VARCHAR(150) NOT NULL,
    city        VARCHAR(150) NOT NULL,
    "address"   VARCHAR(150) NOT NULL,

    CONSTRAINT uq_country_city_address UNIQUE (country, city, "address")
);

CREATE TABLE filmings
(
    filming_id  INT IDENTITY (1,1) PRIMARY KEY,
    movie_id    INT  NOT NULL,
    location_id INT  NOT NULL,
    datefilming DATE NOT NULL,

    CONSTRAINT uq_movie_location_date UNIQUE (movie_id, location_id, datefilming),
    CONSTRAINT uq_movie_date UNIQUE (movie_id, datefilming),
    CONSTRAINT fk_filmings__movies FOREIGN KEY (movie_id) REFERENCES movies (movie_id) ON DELETE CASCADE,
    CONSTRAINT fk_filmings__locations FOREIGN KEY (location_id) REFERENCES shooting_locations (location_id) ON DELETE CASCADE
);

CREATE TABLE staff_filmings
(
    worker_position_id INT NOT NULL,
    filming_id         INT NOT NULL,

    CONSTRAINT pk_staff_filmings PRIMARY KEY (worker_position_id, filming_id),

    CONSTRAINT fk_staff_filmings__worker_positions FOREIGN KEY (worker_position_id) REFERENCES worker_positions (worker_position_id) ON DELETE CASCADE,
    CONSTRAINT fk_staff_filmings__filmings FOREIGN KEY (filming_id) REFERENCES filmings (filming_id) ON DELETE CASCADE
);