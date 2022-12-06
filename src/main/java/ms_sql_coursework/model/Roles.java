package ms_sql_coursework.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Roles {
    ADMIN("Администратор"),
    MANAGER("Менеджер съёмок"),
    WORKER("Сотрудник");

    private final String roleName;
}
