package ms_sql_coursework.model;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import ms_sql_coursework.MS_SQL_App;
import ms_sql_coursework.controllers.AdminController;
import ms_sql_coursework.controllers.Controller;
import ms_sql_coursework.controllers.ManagerController;
import ms_sql_coursework.controllers.WorkerController;

import java.sql.Connection;

import static ms_sql_coursework.model.Roles.*;

@RequiredArgsConstructor
public class RolesContainer {
    private final ImmutableMap<String, Controller> controllerMap;

    public RolesContainer(Connection conn, MS_SQL_App app) throws Exception {

        controllerMap = ImmutableMap.<String, Controller> builder()
                .put(ADMIN.getRoleName(), new AdminController(conn, app))
                .put(MANAGER.getRoleName(), new ManagerController(conn, app))
                .put(WORKER.getRoleName(), new WorkerController(conn, app))
                .build();
    }

    public Controller getController(String name) {
        return controllerMap.get(name);
    }
}
