module Daw2Step {
    exports GamblingTaskMain;

    requires GamifiedComputationalPsychiatryModules;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.media;

    opens GamblingTaskMain to javafx.fxml, javafx.controls, javafx.base;
}