module com.physics.particlesimulator.particlesimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens com.physics.particlesimulator to javafx.fxml;
    exports com.physics.particlesimulator;
}