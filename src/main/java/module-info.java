module com.physics.particlesimulator.ParticleApplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.xml;


    opens com.physics.particlesimulator to javafx.fxml;
    exports com.physics.particlesimulator;
}