USE moviestudio;

--ПРОЦЕДУРЫ:

/*1.	Добавление пользователя
Входные параметры: роль пользователя, его почта, хэш пароля, имя, фамилия, дата рождения, номер телефона
Используемые таблицы: Staff*/
GO
CREATE PROCEDURE add_user(@role_name AS VARCHAR(100), @email AS VARCHAR(100),
                          @password_hash AS VARCHAR(100), @first_name AS VARCHAR(30),
                          @last_name AS VARCHAR(30),
                          @birthdate AS DATE, @phone_number AS VARCHAR(30)) AS
INSERT
INTO
    staff(role_name, email, password_hash, first_name, last_name, birthdate, phone_number)
VALUES
    (@role_name, @email, @password_hash, @first_name, @last_name, @birthdate, @phone_number);
GO


/*2.	Добавление связи сотрудника, должности и отдела, а также добавление записи об этом событии
Входные параметры: Id сотрудника, id отдела, id должности
Используемые таблицы: Worker_positions, Staff_history*/
GO
CREATE PROCEDURE add_worker_position(@staff_id AS INT, @department_id AS INT, @job_id AS INT) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  worker_positions
              WHERE
                    staff_id = @staff_id
                AND job_id = @job_id
                AND department_id = @department_id
                AND active_staff = 0)
        BEGIN
            UPDATE worker_positions
            SET
                active_staff = 1
            WHERE
                    worker_position_id = (SELECT worker_position_id
                                          FROM
                                              worker_positions
                                          WHERE
                                                staff_id = @staff_id
                                            AND job_id = @job_id
                                            AND department_id = @department_id)
        END
    ELSE
        BEGIN
            INSERT
            INTO
                worker_positions(staff_id, department_id, job_id)
            VALUES (@staff_id, @department_id, @job_id);
        END
    INSERT
    INTO
        staff_history(worker_position_id, date_action, "action")
    VALUES
        ((SELECT worker_position_id
          FROM
              worker_positions
          WHERE
                staff_id = @staff_id
            AND job_id = @job_id
            AND department_id = @department_id), GETDATE(), 'Нанят');
END
GO

/*3.	Добавление сотрудника в состав фильма
Входные параметры: id сотрудника, id должности, id фильма, гонорар
Используемые таблицы: Employment, worker_positions*/
GO
CREATE PROCEDURE add_employment(@staff_id AS INT, @job_id AS INT, @movie_id AS INT, @fee AS MONEY) AS
INSERT
INTO
    employments(worker_position_id, movie_id, fee)
VALUES
    ((SELECT worker_positions.worker_position_id
      FROM
          worker_positions
      WHERE
            staff_id = @staff_id
        AND job_id = @job_id), @movie_id, @fee);
GO

/*4.	Изменение гонорара
Входные параметры: id добавленного в фильм сотрудника(employment_id), новый гонорар
Используемые таблицы: Employment*/
GO
CREATE PROCEDURE change_fee(@employment_id AS INT, @fee AS MONEY) AS
UPDATE employments
SET
    fee = @fee
FROM
    employments
WHERE
    employment_id = @employment_id
GO

/*5.	Добавление должности
Входные параметры: название должности, описание
Используемые таблицы: Jobs*/
GO
CREATE PROCEDURE add_job(@job_name AS VARCHAR(50), @description AS VARCHAR(150)=NULL) AS
INSERT
INTO
    jobs(job_name, "description")
VALUES
    (@job_name, @description);
GO

/*6.	Изменение названия должности
Входные параметры: id должности, название должности
Используемые таблицы: Jobs*/
GO
CREATE PROCEDURE change_job_name(@job_id AS INT, @job_name AS VARCHAR(50)) AS
UPDATE jobs
SET
    job_name = @job_name
FROM
    jobs
WHERE
    job_id = @job_id
GO

/*7.	Добавление отдела
Входные параметры: название отдела, описание
Используемые таблицы: Departments*/
GO
CREATE PROCEDURE add_department(@department_name AS VARCHAR(50), @description AS VARCHAR(150)=NULL) AS
INSERT
INTO
    departments(department_name, "description")
VALUES
    (@department_name, @description);
GO

/*8.	Изменение названия отдела
Входные параметры: id отдела, название отдела
Используемые таблицы: Departments*/
GO
CREATE PROCEDURE change_department_name(@department_id AS INT, @department_name AS VARCHAR(50)) AS
UPDATE departments
SET
    department_name = @department_name
FROM
    departments
WHERE
    department_id = @department_id
GO

/*9.	Вывод информации о сотрудниках в отделе
Входные параметры: id отдела
Используемые таблицы: Departments, Staff, Employments*/
GO
CREATE PROCEDURE output_staff_by_department(@department_id AS INT) AS
SELECT staff.email AS почта, first_name AS имя, last_name AS фамилия, job_name AS должность, fee AS гонорар
FROM
    staff
        JOIN worker_positions ON worker_positions.staff_id = staff.staff_id
        AND worker_positions.department_id = @department_id
        JOIN jobs ON jobs.job_id = worker_positions.job_id
        JOIN employments ON employments.worker_position_id = worker_positions.worker_position_id
GO

--Drop procedure Output_staff_by_department
/*10.	Изменение статуса сотрудника(уволен) - изменение позиции на неактивную, добавление события в историю
Входные параметры: id позиции сотрудника
Используемые таблицы: Worker_positions, Staff_history*/
GO
CREATE PROCEDURE change_staff_status(@worker_position_id AS INT) AS
BEGIN
    UPDATE worker_positions
    SET
        active_staff = 0
    FROM
        departments
    WHERE
        worker_position_id = @worker_position_id;
    INSERT
    INTO
        staff_history(worker_position_id, date_action, "action")
    VALUES (@worker_position_id, GETDATE(), 'Уволен');
END
GO

/*11.	Вывод истории трудоустройства сотрудника
Входные параметры: id сотрудника
Используемые таблицы: Staff_history, Staff*/
GO
CREATE PROCEDURE output_staff_history(@staff_id AS INT) AS
SELECT first_name AS имя, last_name AS фамилия, job_name AS должность, date_action AS дата, "action" AS событие
FROM
    staff
        JOIN worker_positions ON worker_positions.staff_id = staff.staff_id
        AND staff.staff_id = @staff_id
        JOIN staff_history ON worker_positions.worker_position_id = staff_history.worker_position_id
        JOIN jobs ON jobs.job_id = worker_positions.job_id
GO

/*12.	Добавление нового фильма
Входные параметры: Название, дата премьеры, расходы, доходы
Используемые таблицы: Movies*/ --movies_history изменила
GO
CREATE PROCEDURE add_movie(@title AS VARCHAR(150), @premiere AS DATE = NULL, @expenses AS INT = NULL,
                           @revenue AS INT = NULL) AS
INSERT
INTO
    movies(title, premiere, expenses, revenue)
VALUES
    (@title, @premiere, @expenses, @revenue);
INSERT
INTO
    movies_history(movie_id, date_action, "action")
VALUES
    ((SELECT movie_id FROM movies WHERE title = @title), GETDATE(), 'Начат');
GO

--Drop procedure add_movie
/*13.	Изменение данных фильма
Входные параметры: id фильма, название/дата премьеры
Используемые таблицы: Movies*/
GO
CREATE PROCEDURE change_movie_name(@movie_id AS INT, @title AS VARCHAR(150)) AS
UPDATE movies
SET
    title = @title
FROM
    movies
WHERE
    movie_id = @movie_id
GO
GO
CREATE PROCEDURE change_movie_premiere(@movie_id AS INT, @premiere AS DATE) AS
UPDATE movies
SET
    premiere = @premiere
FROM
    movies
WHERE
    movie_id = @movie_id
GO

/*14.	Изменение расходов и доходов фильма
Входные параметры: id фильма, расходы/доходы
Используемые таблицы: Movies*/
GO
CREATE PROCEDURE change_movie_expenses(@movie_id AS INT, @expenses AS INT) AS
UPDATE movies
SET
    expenses = @expenses
FROM
    movies
WHERE
    movie_id = @movie_id
GO
GO
CREATE PROCEDURE change_movie_revenue(@movie_id AS INT, @revenue AS INT) AS
UPDATE movies
SET
    revenue = @revenue
FROM
    movies
WHERE
    movie_id = @movie_id
GO

/*15.	Изменение статуса фильма - добавление события в историю
Входные параметры: id фильма, дата события, событие
Используемые таблицы: Movies_history*/
GO
CREATE PROCEDURE change_movie_status(@movie_id AS INT, @date_action AS DATE, @action AS VARCHAR(10) = 'Начат') AS
INSERT
INTO
    movies_history(movie_id, date_action, "action")
VALUES
    (@movie_id, @date_action, @action);
GO

/*16.	Добавление локации
Входные параметры: страна, город, адрес
Используемые таблицы: Shooting_locations*/
GO
CREATE PROCEDURE add_location(@country AS VARCHAR(150), @city AS VARCHAR(150), @address AS VARCHAR(150)) AS
INSERT
INTO
    shooting_locations(country, city, "address")
VALUES
    (@country, @city, @address);
GO

/*17.	Вывод истории создания фильма
Входные параметры: id фильма
Используемые таблицы: Movies, Movies_history*/
CREATE PROCEDURE output_movies_history(@movie_id AS INT) AS
SELECT title AS название_фильма, date_action AS дата, "action" AS событие
FROM
    movies_history
        JOIN movies ON movies.movie_id = movies_history.movie_id
        AND movies.movie_id = @movie_id
GO

/*18.	Вывод составов фильмов
Входные параметры: id фильма
Используемые таблицы: Movies, Employments*/
CREATE PROCEDURE output_movies_staff(@movie_id AS INT) AS
SELECT staff.email AS почта, first_name AS имя, last_name AS фамилия, job_name AS должность, fee AS гонорар
FROM
    staff
        JOIN worker_positions ON worker_positions.staff_id = staff.staff_id
        AND worker_positions.active_staff = 1
        JOIN employments ON employments.worker_position_id = worker_positions.worker_position_id
        AND employments.movie_id = @movie_id
        JOIN jobs ON jobs.job_id = worker_positions.job_id
GO

/*19.	Вывод своей личной информации/ 20. Вывод личной информации сотрудника
Входные параметры: id сотрудника
Используемые таблицы: Staff*/
GO
CREATE PROCEDURE output_staff_my_info(@staff_id AS INT) AS
SELECT
    email         AS почта,
    password_hash AS хэш_пароля,
    first_name    AS имя,
    last_name     AS фамилия,
    birthdate     AS дата_рождения,
    phone_number  AS телефон
FROM
    staff
WHERE
    staff_id = @staff_id
GO

/*21.	Изменение данных сотрудника
Входные параметры: id сотрудника, id администратора, пароль/имя/фамилия/дата рождения/номер телефона
Используемые таблицы: Staff*/
GO
CREATE PROCEDURE change_staff_password_hash_by_admin(@staff_id AS INT, @password_hash AS VARCHAR(100)) AS
UPDATE staff
SET
    password_hash = @password_hash
FROM
    staff
WHERE
    staff_id = @staff_id
GO

GO
CREATE PROCEDURE change_staff_name_by_admin(@staff_id AS INT, @first_name AS VARCHAR(30), @last_name AS VARCHAR(30)) AS
UPDATE staff
SET
    first_name = @first_name,
    last_name  = @last_name
FROM
    staff
WHERE
    staff_id = @staff_id
GO

GO
CREATE PROCEDURE change_staff_birthdate_by_admin(@staff_id AS INT, @birthdate AS DATE) AS
UPDATE staff
SET
    birthdate = @birthdate
FROM
    staff
WHERE
    staff_id = @staff_id
GO
GO
CREATE PROCEDURE change_staff_phone_number_by_admin(@staff_id AS INT, @phone_number AS VARCHAR(30)) AS
UPDATE staff
SET
    phone_number = @phone_number
FROM
    staff
WHERE
    staff_id = @staff_id
GO

/*22.	Изменение своей личной информации
Входные параметры: id сотрудника, пароль/имя/фамилия/дата рождения/номер телефона
Используемые таблицы: Staff*/
GO
CREATE PROCEDURE change_staff_password_hash(@staff_id AS INT, @password AS VARCHAR(100)) AS
UPDATE staff
SET
    password_hash = @password --как захешировать?
FROM
    staff
WHERE
    staff_id = @staff_id
GO
GO
CREATE PROCEDURE change_staff_first_name(@staff_id AS INT, @first_name AS VARCHAR(30)) AS
UPDATE staff
SET
    first_name = @first_name
FROM
    staff
WHERE
    staff_id = @staff_id
GO

CREATE PROCEDURE change_staff_last_name(@staff_id AS INT, @last_name AS VARCHAR(30)) AS
UPDATE staff
SET
    last_name = @last_name
FROM
    staff
WHERE
    staff_id = @staff_id
GO

GO
CREATE PROCEDURE change_staff_birthdate(@staff_id AS INT, @birthdate AS DATE) AS
UPDATE staff
SET
    birthdate = @birthdate
FROM
    staff
WHERE
    staff_id = @staff_id
GO
GO
CREATE PROCEDURE change_staff_phone_number(@staff_id AS INT, @phone_number AS VARCHAR(30)) AS
UPDATE staff
SET
    phone_number = @phone_number
FROM
    staff
WHERE
    staff_id = @staff_id
GO

/*23.	Добавление даты съемки
Входные параметры: id фильма, id локации, дата съёмки, id менеджера
Используемые таблицы: Filmings*/
GO
CREATE PROCEDURE add_filming(@movie_id AS INT, @location_id AS INT, @datefilming AS DATE, @staff_id AS INT) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  employments
                      JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
                      AND worker_positions.staff_id = @staff_id
                      AND worker_positions.active_staff = 1
              WHERE
                  movie_id = @movie_id)
        BEGIN
            INSERT
            INTO
                filmings(movie_id, location_id, datefilming)
            VALUES (@movie_id, @location_id, @datefilming);
        END
END
GO

/*24.	Удаление даты съёмки
Входные параметры: id съёмки, id менеджера
Используемые таблицы: Filmings*/
GO
CREATE PROCEDURE delete_filming(@filming_id AS INT, @staff_id AS INT) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  employments
                      JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
                      AND worker_positions.staff_id = @staff_id
                      JOIN filmings ON filmings.movie_id = employments.movie_id
                      AND filmings.filming_id = @filming_id)
        BEGIN
            DELETE
            FROM
                filmings
            WHERE
                filming_id = @filming_id;
        END
END
GO

/*25.	Изменение даты съёмки
Входные параметры: id съёмки, id менеджера, новая дата
Используемые таблицы: Filmings*/
GO
CREATE PROCEDURE change_filming_date(@filming_id AS INT, @staff_id AS INT, @datefilming AS DATE) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  employments
                      JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
                      AND worker_positions.staff_id = @staff_id
                      JOIN filmings ON filmings.movie_id = employments.movie_id
                      AND filmings.filming_id = @filming_id)
        BEGIN
            UPDATE filmings
            SET
                datefilming = @datefilming
            FROM
                filmings
            WHERE
                filming_id = @filming_id
        END
END
GO

/*26.	Изменение локации съёмки
Входные параметры: id съёмки, id менеджера, новая локация
Используемые таблицы: Filmings*/
GO
CREATE PROCEDURE change_filming_location(@filming_id AS INT, @staff_id AS INT, @location_id AS INT) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  employments
                      JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
                      AND worker_positions.staff_id = @staff_id
                      JOIN filmings ON filmings.movie_id = employments.movie_id
                      AND filmings.filming_id = @filming_id)
        BEGIN
            UPDATE filmings
            SET
                location_id = @location_id
            FROM
                filmings
            WHERE
                filming_id = @filming_id
        END
END
GO

/*27.	Вывод списка сотрудников и должностей в фильме
Входные параметры: id фильма, id менеджера
Используемые таблицы: Worker_positions, Jobs, Employments*/
GO
CREATE PROCEDURE output_staff_jobs_by_movie(@movie_id AS INT, @staff_id AS INT) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  employments
                      JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
                      AND worker_positions.staff_id = @staff_id
              WHERE
                  movie_id = @movie_id)
        BEGIN
            SELECT staff.email AS почта, first_name AS имя, last_name AS фамилия, job_name AS должность
            FROM
                staff
                    JOIN worker_positions ON worker_positions.staff_id = staff.staff_id
                    JOIN employments ON employments.worker_position_id = worker_positions.worker_position_id
                    AND employments.movie_id = @movie_id
                    JOIN jobs ON jobs.job_id = worker_positions.job_id
        END
END
GO

/*28.	Вывод состава фильма
Входные параметры: id фильма, id менеджера
Используемые таблицы: Employments, Movies*/
GO
CREATE PROCEDURE output_staff_by_movie(@movie_id AS INT, @staff_id AS INT) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  employments
                      JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
                      AND worker_positions.staff_id = @staff_id
              WHERE
                  movie_id = @movie_id)
        BEGIN
            SELECT staff.email AS почта, first_name AS имя, last_name AS фамилия, job_name AS должность, fee AS гонорар
            FROM
                staff
                    JOIN worker_positions ON worker_positions.staff_id = staff.staff_id
                    JOIN employments ON employments.worker_position_id = worker_positions.worker_position_id
                    AND employments.movie_id = @movie_id
                    JOIN jobs ON jobs.job_id = worker_positions.job_id
        END
END
GO

/*29.	Добавление расписания каждому сотруднику
Входные параметры: id менеджера, id позиции сотрудника, id съёмки
Используемые таблицы: Staff_filmings*/
GO
CREATE PROCEDURE add_staff_filming(@worker_position_id AS INT, @filming_id AS INT, @staff_id AS INT) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  employments
                      JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
                      AND worker_positions.staff_id = @staff_id
                      AND worker_positions.active_staff = 1
                      JOIN filmings ON filmings.movie_id = employments.movie_id
                      AND filmings.filming_id = @filming_id)
        BEGIN
            INSERT
            INTO
                staff_filmings(worker_position_id, filming_id)
            VALUES (@worker_position_id, @filming_id);
        END
END
GO

/*30.	Вывод информации о съёмках фильма, к которому относится менеджер
Входные параметры: id фильма, id менеджера
Используемые таблицы: Filmings*/
GO
CREATE PROCEDURE output_filmings_by_manager(@movie_id AS INT, @staff_id AS INT) AS
SELECT title AS фильм, country AS страна, city AS город, "address" AS адрес, filmings.datefilming AS дата_съемки
FROM
    filmings
        JOIN employments ON employments.movie_id = filmings.movie_id
        AND employments.movie_id = @movie_id
        JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
        AND worker_positions.staff_id = @staff_id
        AND worker_positions.active_staff = 1
        JOIN shooting_locations ON shooting_locations.location_id = filmings.location_id
        JOIN movies ON movies.movie_id = filmings.movie_id
GO

/*31.	Вывод списка своих фильмов
Входные параметры: id фильма, id менеджера
Используемые таблицы: Movies, Employments, Worker_positions*/
GO
CREATE PROCEDURE output_movies_by_manager(@staff_id AS INT) AS
SELECT title AS фильм, premiere AS премьера, expenses AS доходы, revenue AS расходы
FROM
    employments
        JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
        AND worker_positions.active_staff = 1
        AND worker_positions.staff_id = @staff_id
        JOIN movies ON movies.movie_id = employments.movie_id
GO

/*32.	Вывод информации о расписании сотрудников своего фильма
Входные параметры: id фильма, id менеджера
Используемые таблицы: Movies, Employments, Worker_positions*/
GO
CREATE PROCEDURE output_staff_filmings_by_movie(@movie_id AS INT, @staff_id AS INT) AS
BEGIN
    IF EXISTS(SELECT *
              FROM
                  employments
                      JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
                      AND worker_positions.staff_id = @staff_id
              WHERE
                  movie_id = @movie_id)
        BEGIN
            SELECT
                staff.first_name     AS имя,
                staff.last_name      AS фамилия,
                job_name             AS должность,
                filmings.datefilming AS дата_съемки
            FROM
                staff
                    JOIN worker_positions ON worker_positions.staff_id = staff.staff_id
                    AND worker_positions.active_staff = 1
                    JOIN staff_filmings ON staff_filmings.worker_position_id = worker_positions.worker_position_id
                    JOIN filmings ON filmings.filming_id = staff_filmings.filming_id
                    JOIN jobs ON jobs.job_id = worker_positions.job_id
            WHERE
                    staff.staff_id IN (SELECT staff.staff_id
                                       FROM
                                           staff
                                               JOIN worker_positions ON worker_positions.staff_id = staff.staff_id
                                               AND worker_positions.active_staff = 1
                                               JOIN employments ON employments.worker_position_id =
                                                                   worker_positions.worker_position_id
                                               AND employments.movie_id = @movie_id)
        END
END

/*33.	Вывод информации о своих съёмках и локациях
Входные параметры: id сотрудника
Используемые таблицы: Staff_filmings, Employments, Worker_positions, Shooting_locations, Filmings*/
GO
CREATE PROCEDURE output_filmings_locations(@staff_id AS INT) AS
SELECT DISTINCT
    title                        AS название_фильма,
    job_name                     AS должность,
    filmings.datefilming         AS дата_съемки,
    shooting_locations.country   AS страна,
    shooting_locations.city      AS город,
    shooting_locations."address" AS адрес
FROM
    filmings
        JOIN employments ON employments.movie_id = filmings.movie_id
        JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
        AND worker_positions.staff_id = @staff_id
        AND worker_positions.active_staff = 1
        JOIN staff_filmings ON staff_filmings.filming_id = filmings.filming_id
        JOIN shooting_locations ON filmings.location_id = shooting_locations.location_id
        JOIN movies ON movies.movie_id = filmings.movie_id
        JOIN jobs ON jobs.job_id = worker_positions.job_id
GO

/*34.	Вывод гонорара
Входные параметры: id сотрудника
Используемые таблицы: Employments, Worker_positions*/
CREATE PROCEDURE output_fee(@staff_id AS INT) AS
SELECT fee AS гонорар
FROM
    employments
        JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
        AND worker_positions.staff_id = @staff_id
        AND worker_positions.active_staff = 1
GO

--35. Filmings: Вывод информации о съёмках (всех)
CREATE PROCEDURE get_filmings AS
SELECT
    title                AS название_фильма,
    country              AS страна,
    city                 AS город,
    "address"            AS адрес,
    filmings.datefilming AS дата_съемки
FROM
    filmings
        JOIN shooting_locations ON shooting_locations.location_id = filmings.location_id
        JOIN movies ON movies.movie_id = filmings.movie_id
GO


--Простые запросы (однотабличные):
--Jobs: Вывод списка должностей
CREATE PROCEDURE get_jobs AS
SELECT job_name AS название_должности, "description" AS описание
FROM
    jobs;
GO

--Departments: Вывод списка отделов
CREATE PROCEDURE get_departments AS
SELECT department_name AS название_отдела, "description" AS описание
FROM
    departments;
GO

--Movies: Вывод списка фильмов
CREATE PROCEDURE get_movies AS
SELECT title AS название_фильма, premiere AS дата_премьеры, revenue AS доходы, expenses AS расходы
FROM
    movies;
GO

--Shooting_locations: Вывод списка локаций
CREATE PROCEDURE get_locations AS
SELECT country AS страна, city AS город, "address" AS адрес
FROM
    shooting_locations;


--Представления:
/*1.	Информация о всех менеджерах и фильмах, за которые они отвечают - id менеджера, его имя, фамилия, id фильма, название фильма и текущий статус фильма
Используемые таблицы: Staff, Employments, Worker_positions, Movies, Movies_history*/
GO
CREATE VIEW managers (почта, имя, фамилия, название_фильма, статус) AS --убрать
SELECT DISTINCT
    staff.email,
    staff.first_name,
    staff.last_name,
    movies.title,
    (SELECT TOP 1 "action"
     FROM
         movies_history
     WHERE
         movies.movie_id = movies_history.movie_id
     ORDER BY movies_history.date_action DESC)
FROM
    staff
        JOIN worker_positions ON staff.staff_id = worker_positions.staff_id
        JOIN employments ON worker_positions.worker_position_id = employments.worker_position_id
        JOIN movies ON movies.movie_id = employments.movie_id
        JOIN movies_history ON movies.movie_id = movies_history.movie_id
WHERE
    staff.role_name = 'Менеджер съёмок'
GO

/*2.	Информация о всех завершенных фильмах – id фильма, название, дата премьеры, дата первой съёмки, дата последней съёмки, доходы, расходы, количество человек, которые работали над ним
Используемые таблицы: Employments, Movies, Movies_history, Filmings*/
GO
CREATE VIEW finished_movies
            (название_фильма, дата_премьеры, первая_съемка, последняя_съемка, доходы, расходы,
             количество_сотрудников) AS
SELECT
    movies.title,
    movies.premiere,
    (SELECT TOP 1 datefilming FROM filmings ORDER BY datefilming),
    (SELECT TOP 1 datefilming FROM filmings ORDER BY datefilming DESC),
    movies.revenue,
    movies.expenses,
    COUNT(employments.worker_position_id)
FROM
    movies
        JOIN filmings ON movies.movie_id = filmings.movie_id
        JOIN employments ON employments.movie_id = movies.movie_id
        JOIN movies_history ON movies_history.movie_id = movies.movie_id
        AND movies_history.action = 'Снят'
GROUP BY
    movies.movie_id, movies.title, movies.premiere,
    movies.revenue, movies.expenses
GO


--Вспомогательные процедуры для приложения
GO
CREATE PROCEDURE check_account(@email AS VARCHAR(100), @hash AS VARCHAR(100)) AS
SELECT role_name AS роль
FROM
    staff
WHERE
      email = @email
  AND password_hash = @hash


GO
CREATE PROCEDURE get_staff_id_by_email(@email AS VARCHAR(100)) AS
SELECT staff_id
FROM
    staff
WHERE
    email = @email

GO
CREATE PROCEDURE get_movie_id_by_name(@title AS VARCHAR(150)) AS
SELECT movie_id
FROM
    movies
WHERE
    title = @title

GO
EXEC get_staff
CREATE PROCEDURE get_staff AS
SELECT first_name, last_name, email
FROM
    staff

GO
CREATE PROCEDURE get_location_id(@country AS VARCHAR(150), @city AS VARCHAR(150), @address AS VARCHAR(150)) AS
SELECT location_id
FROM
    shooting_locations
WHERE
      country = @country
  AND city = @city
  AND "address" = @address
GO

CREATE PROCEDURE get_worker_position_id(@staff_id AS INT, @job_id AS INT) AS
SELECT worker_position_id
FROM
    worker_positions
WHERE
      staff_id = @staff_id
  AND job_id = @job_id
  AND worker_positions.active_staff = 1
GO

DROP PROCEDURE get_worker_position_id
CREATE PROCEDURE get_staff_job AS
SELECT first_name, last_name, email, job_name
FROM
    staff
        JOIN worker_positions ON worker_positions.staff_id = staff.staff_id
        AND worker_positions.active_staff = 1
        JOIN jobs ON jobs.job_id = worker_positions.job_id

GO
CREATE PROCEDURE get_employment_id(@movieid AS INT, @wpid AS INT) AS
SELECT employment_id
FROM
    employments
WHERE
      movie_id = @movieid
  AND worker_position_id = @wpid
GO

CREATE PROCEDURE get_filming_id(@movieid AS INT, @date AS DATE) AS
SELECT filming_id
FROM
    filmings
WHERE
      movie_id = @movieid
  AND datefilming = @date
GO
CREATE PROCEDURE get_department_id_by_name(@name AS VARCHAR(100)) AS
SELECT department_id
FROM
    departments
WHERE
    department_name = @name

GO
CREATE PROCEDURE get_job_id_by_name(@name AS VARCHAR(100)) AS
SELECT job_id
FROM
    jobs
WHERE
    job_name = @name


GO
CREATE PROCEDURE get_filmings_by_manager(@managerid AS INT) AS
SELECT
    title                AS название_фильма,
    country              AS страна,
    city                 AS город,
    "address"            AS адрес,
    filmings.datefilming AS дата_съемки
FROM
    filmings
        JOIN shooting_locations ON shooting_locations.location_id = filmings.location_id
        JOIN movies ON movies.movie_id = filmings.movie_id
        JOIN employments ON employments.movie_id = movies.movie_id
        JOIN worker_positions ON worker_positions.worker_position_id = employments.worker_position_id
        AND worker_positions.staff_id = @managerid
GO

CREATE PROCEDURE find_staff_filming(@staffid AS INT, @date AS DATE) AS
SELECT *
FROM
    worker_positions
        JOIN staff_filmings ON worker_positions.worker_position_id = staff_filmings.worker_position_id
        JOIN filmings ON filmings.filming_id = staff_filmings.filming_id
        AND filmings.datefilming = @date
WHERE
    worker_positions.staff_id = @staffid