module org.hedbor.evan.classictalents {
    requires transitive kotlin.stdlib;
    requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.kotlin;

    opens org.hedbor.evan.classictalents.controller to javafx.fxml;

    exports org.hedbor.evan.classictalents;
}