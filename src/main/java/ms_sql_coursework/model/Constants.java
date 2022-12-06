package ms_sql_coursework.model;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public class Constants {

    public static final String EXCEPTION = "Ошибка!";
    public static final String SUCCESS = "Успех!";
    public static final String PHONE_FORMAT = "Формат: +n-nnn-nnn-nn-nn";
    public static final String NULL_FORMAT = "null";
    public static final String START = "Начат";

    public static Map<String, String> adminMap = Map.ofEntries(
            entry("Зарегистрировать пользователя", "EXEC add_user ?,?,?,?,?,?,?"),
            entry("Назначить на должность", "EXEC add_worker_position ?,?,?"),
            entry("Добавить в состав фильма", "EXEC add_employment ?,?,?,?"),
            entry("Редактировать гонорар", "EXEC change_fee ?,?"),
            entry("Добавить должность", "EXEC add_job ?,?"),
            entry("Редактировать название должности","EXEC change_job_name ?,?"),
            entry("Добавить отдел","EXEC add_department ?,?"),
            entry("Редактировать название отдела","EXEC change_department_name ?,?"),
            entry("Посмотреть сотрудников в отделе","EXEC Output_staff_by_department ?"),
            entry("Уволить сотрудника","EXEC change_staff_status ?"),
            entry("Посмотреть историю трудоустройства сотрудника","EXEC Output_staff_history ?"),
            entry("Добавить фильм","EXEC add_movie ?,?,?,?"),

            entry("Изменить данные фильма", "changeMovieInfo"),

            entry("Добавить локацию", "EXEC add_location ?,?,?"),
            entry("Посмотреть историю создания фильма", "EXEC Output_movies_history ?"),
            entry("Посмотреть состав фильма", "EXEC Output_movies_staff ?"),

            entry("Посмотреть личную информацию сотрудника", "EXEC Output_staff_info ?"),
            entry("Посмотреть должности", "EXEC get_jobs"),
            entry("Посмотреть отделы", "EXEC get_departments"),
            entry("Посмотреть фильмы", "EXEC get_movies"),
            entry("Посмотреть локации", "EXEC get_locations"),

            entry("Посмотреть зоны ответственности менеджеров", "select * from managers"),
            entry("Посмотреть законченные фильмы", "select * from finished_movies")
    );


    public static Map<String, String> managerMap = Map.ofEntries(
            entry("Назначить съёмку", "EXEC add_filming ?,?,?,?"),
            entry("Изменить данные съёмки", "changeFilmingInfo"),

            entry("Посмотреть сотрудников", "EXEC Output_staff_jobs_by_movie ?,?"),
            entry("Посмотреть историю создания фильма", "EXEC Output_movies_history_manager ?"),
            entry("Посмотреть состав фильма", "EXEC Output_staff_by_movie ?,?"),
            entry("Добавить сотрудника на съёмку", "EXEC add_staff_filming ?,?,?"),
            entry("Посмотреть расписание съёмок фильма", "EXEC Output_filmings_by_manager ?,?"),
            entry("Посмотреть список моих фильмов", "EXEC Output_movies_by_manager ?"),
            entry("Посмотреть расписание съёмок участников фильма", "EXEC Output_staff_filmings_by_movie ?,?"),

            entry("Посмотреть локации", "EXEC get_locations"),
            entry("Посмотреть зоны ответственности менеджеров", "select * from managers"),
            entry("Посмотреть законченные фильмы", "select * from finished_movies")
    );


    public static Map<String, String> workerMap = Map.ofEntries(
            entry("Посмотреть расписание и локации", "EXEC Output_filmings_locations ?"),
            entry("Посмотреть гонорар", "EXEC Output_fee ?"),
            entry("Посмотреть законченные фильмы", "select * from finished_movies")
    );

    public static Set<String> withoutParams = Set.of(
            "EXEC get_jobs",
            "EXEC get_departments",
            "EXEC get_movies",
            "EXEC get_locations",
            "EXEC Output_movies_by_manager ?",
            "EXEC Output_filmings_locations ?",
            "EXEC Output_fee ?",
            "select * from finished_movies",
            "select * from managers"
    );
}
