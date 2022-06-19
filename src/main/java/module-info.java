module org.hedbor.evan.classictalents {
    requires transitive kotlin.stdlib;
    requires kotlin.stdlib.jdk8;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.kotlin;

    opens org.hedbor.evan.classictalents.control to javafx.fxml;
    opens org.hedbor.evan.classictalents.model to javafx.base;
    opens org.hedbor.evan.classictalents.dto to com.fasterxml.jackson.databind;

    exports org.hedbor.evan.classictalents;
}