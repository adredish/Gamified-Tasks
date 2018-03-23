module GamifiedComputationalPsychiatryModules {
    exports CanvasToListPackage;
    exports ClockTimerPackage;
    exports CountDownDelayPackage;
    exports DataFilePackage;
    exports RatingPackage;
    exports StringsFromFile;
    exports TextScreenPackage;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens CanvasToListPackage to javafx.fxml;
    opens CountDownDelayPackage to javafx.fxml;
    opens RatingPackage to javafx.fxml;
    opens TextScreenPackage to javafx.fxml;
}