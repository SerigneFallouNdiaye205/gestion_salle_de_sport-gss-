module com.gss.gss {

    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql;
    requires jdk.jshell;

    opens com.gss.gss to javafx.fxml;
    opens com.gss.gss.controller to javafx.fxml;

    exports com.gss.gss;
    exports com.gss.gss.controller;
}