module org.hedbor.evan.classictalents {
    requires transitive kotlin.stdlib;
    requires javafx.controls;
    requires javafx.fxml;

    opens org.hedbor.evan.classictalents.controller to javafx.fxml;

    exports org.hedbor.evan.classictalents;
}