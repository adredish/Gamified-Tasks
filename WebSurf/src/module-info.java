module WebSurf {
    requires GamifiedComputationalPsychiatryModules;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.media;

    exports WebSurfMain;

    opens TravelScreen;
    opens ZonePackage;
}