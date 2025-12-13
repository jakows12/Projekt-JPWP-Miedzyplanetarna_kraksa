module com.interplanetarycrash {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.interplanetarycrash to javafx.fxml;
    exports com.interplanetarycrash;
    exports com.interplanetarycrash.core;
    exports com.interplanetarycrash.states;
    exports com.interplanetarycrash.rendering;
    exports com.interplanetarycrash.assets;
    exports com.interplanetarycrash.input;
    
}
