package frc.robot.subsystems.webserver;

import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebServer {
    private final StringPublisher reefPositionPublisher;
    private final StringSubscriber reefPositionSubscriber;
    private final StringPublisher sourcePositionPublisher;
    private final StringSubscriber sourcePositionSubscriber;
    private static BooleanPublisher alliancePublisher;
    private final StringPublisher autoPublisher;
    private final StringSubscriber autoSubscriber;

    
    
        public WebServer() {
            // Initialize NetworkTables
            var reefTable = NetworkTableInstance.getDefault().getTable("reefTable");
            var sourceTable = NetworkTableInstance.getDefault().getTable("sourceTable");
            var allianceTable = NetworkTableInstance.getDefault().getTable("allianceTable");
            var autoTable = NetworkTableInstance.getDefault().getTable("autoTable");
            reefPositionPublisher = reefTable.getStringTopic("positionValue").publish();
            reefPositionSubscriber = reefTable.getStringTopic("positionValue").subscribe(new String(), PubSubOption.sendAll(true));
            sourcePositionPublisher = sourceTable.getStringTopic("sourcePositionValue").publish();
            sourcePositionSubscriber = sourceTable.getStringTopic("sourcePositionValue").subscribe(new String(), PubSubOption.sendAll(true));
            autoPublisher = autoTable.getStringTopic("selectedAuto").publish();
            autoSubscriber = autoTable.getStringTopic("selectedAuto").subscribe(new String(), PubSubOption.sendAll(true));
            // This value should return TRUE if the alliance is blue
            alliancePublisher = allianceTable.getBooleanTopic("isBlue").publish();

        // Start the web server
        var app =
            Javalin.create(
                config -> {
                    config.staticFiles.add(
                        Paths.get(
                                Filesystem.getDeployDirectory().getAbsolutePath(),
                                "web")
                            .toString(),
                        Location.EXTERNAL);
                });

        // Handle POST request to toggle value
        app.post("/toggle", ctx -> {
            var mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(ctx.body(), Map.class);
        
            if (body.containsKey("variable") && body.get("variable") instanceof String &&
                body.containsKey("value") && body.get("value") instanceof String) {
                String variable = (String) body.get("variable");
                String value = (String) body.get("value");
        
                if (variable.equals("reefPositionValue")) {
                    reefPositionPublisher.set(value); // Update reef position
                } else if (variable.equals("sourcePositionValue")) {
                    sourcePositionPublisher.set(value); // Update source position
                } else if (variable.equals("autoValue")){
                    autoPublisher.set(value); // Update source position
                }
        
                ctx.status(200);
            } else {
                ctx.status(400).result("Invalid input");
            }
        });
    
        app.start(5800);
    }

    public void setAlliance() {
        alliancePublisher.set(DriverStation.getAlliance().get() == Alliance.Blue);
    }

    public String getSelectedReefPosition() {
        return reefPositionSubscriber.get();
    }

    public String getSelectedSourcePosition() {
        return sourcePositionSubscriber.get();
    }

    public String getSelectedAuto() {
        return autoSubscriber.get();
    }
}
