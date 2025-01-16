package frc.robot.subsystems.webserver;

import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.PubSubOptions;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.wpilibj.Filesystem;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebServer {
    private final StringPublisher reefPositionPublisher;
    private final StringSubscriber reefPositionSubscriber;
    private final StringPublisher sourcePositionPublisher;
    private final StringSubscriber sourcePositionSubscriber;


    public WebServer() {
        // Initialize NetworkTables
        var reefTable = NetworkTableInstance.getDefault().getTable("reefTable");
        var sourceTable = NetworkTableInstance.getDefault().getTable("sourceTable");
        reefPositionPublisher = reefTable.getStringTopic("positionValue").publish();
        reefPositionSubscriber = reefTable.getStringTopic("positionValue").subscribe(new String(), PubSubOption.sendAll(true));
        sourcePositionPublisher = sourceTable.getStringTopic("sourcePositionValue").publish();
        sourcePositionSubscriber = sourceTable.getStringTopic("sourcePositionValue").subscribe(new String(), PubSubOption.sendAll(true));
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
                }
        
                ctx.status(200);
            } else {
                ctx.status(400).result("Invalid input");
            }
        });
    
        app.start(5800);
    }

    public String getSelectedReefPosition() {
        return reefPositionSubscriber.get();
    }

    public String getSelectedSourcePosition() {
        return sourcePositionSubscriber.get();
    }
}
