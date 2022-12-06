package ms_sql_coursework.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import lombok.Data;
import ms_sql_coursework.MS_SQL_App;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Data
public class ResultController {
    @FXML
    public Label tableName;
    @FXML
    public TableView table;

    private ObservableList<ObservableList> data;

    public ResultController() {
        data = FXCollections.observableArrayList();
        table = new TableView();
    }

    public void fillTable(ResultSet rs, String name) {
        tableName.setText(name);
        if (rs == null) {
            MS_SQL_App.showExceptionWindow("Нет совпадений по вашему запросу");
        } else {
            try {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                //добавление столбцов
                for (int i = 0; i < columnCount; i++) {
                    final int j = i;
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                    col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                            ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));

                    table.getColumns().add(col);
                    System.out.println("Column [" + i + "] ");
                }
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= columnCount; i++) {
                        if (rs.getString(i) == null) {
                            row.add("-");
                        } else {
                            row.add(rs.getString(i));
                        }
                    }
                    System.out.println("Row [1] added " + row);
                    data.add(row);
                }

                table.setItems(data);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
