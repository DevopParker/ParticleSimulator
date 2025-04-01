module com.physics.particlesimulator.ParticleApplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires javafx.graphics;


    opens com.physics.particlesimulator to javafx.fxml;
    exports com.physics.particlesimulator;
}