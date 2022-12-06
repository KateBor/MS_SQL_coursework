module com.example.mssql_coursework {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires lombok;
    requires org.slf4j;
    requires java.datatransfer;
    requires java.sql;
    requires google.collections;
    requires org.apache.commons.codec;
    requires com.microsoft.sqlserver.jdbc;

    opens ms_sql_coursework to javafx.fxml;
    exports ms_sql_coursework.controllers;
    exports ms_sql_coursework.model;
    exports ms_sql_coursework.services;
    exports ms_sql_coursework;
    opens ms_sql_coursework.controllers to javafx.fxml;

}