package frc.robot.subsystems.webserver;

import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.networktables.StringArraySubscriber;
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
    private final StringArrayPublisher autoPublisher;
    private final StringArraySubscriber autoSubscriber;

    public WebServer() {
        // Initialize NetworkTables
        var reefTable = NetworkTableInstance.getDefault().getTable("reefTable");
        var sourceTable = NetworkTableInstance.getDefault().getTable("sourceTable");
        var allianceTable = NetworkTableInstance.getDefault().getTable("allianceTable");
        var autoTable = NetworkTableInstance.getDefault().getTable("autoTable");

        // Publishers and Subscribers
        reefPositionPublisher = reefTable.getStringTopic("positionValue").publish();
        reefPositionSubscriber = reefTable.getStringTopic("positionValue").subscribe(new String(), PubSubOption.sendAll(true));
        sourcePositionPublisher = sourceTable.getStringTopic("sourcePositionValue").publish();
        sourcePositionSubscriber = sourceTable.getStringTopic("sourcePositionValue").subscribe(new String(), PubSubOption.sendAll(true));

        // Array for selected auto positions
        autoPublisher = autoTable.getStringArrayTopic("autoPositions").publish();
        autoSubscriber = autoTable.getStringArrayTopic("autoPositions").subscribe(new String[] {}, PubSubOption.sendAll(true)); 

        // Alliance Publisher
        alliancePublisher = allianceTable.getBooleanTopic("isBlue").publish();

        // Start the web server
        var app = Javalin.create(config -> {
            config.staticFiles.add(
                Paths.get(Filesystem.getDeployDirectory().getAbsolutePath(), "web").toString(),
                Location.EXTERNAL);
        });

        // Handle POST request to toggle value and update arrays
        app.post("/toggle", ctx -> {
            var mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(ctx.body(), Map.class);
        
            if (body.containsKey("variable") && body.get("variable") instanceof String &&
                body.containsKey("value") && body.get("value") instanceof List) {  // Expecting a List (array) here
                String variable = (String) body.get("variable");
                List<String> value = (List<String>) body.get("value"); // Get the list of selected auto positions
        
                if (variable.equals("autoValue")) {
                    String[] updatedAuto = value.toArray(new String[0]); // Convert List to Array
                    autoPublisher.set(updatedAuto); // Update auto positions in NetworkTables
                    System.out.println("Updated auto positions in NetworkTables: " + value);
                }
        
                ctx.status(200);
            } else {
                ctx.status(400).result("Invalid input");
            }
        });
        app.post("/setPosition", ctx -> {
            var mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(ctx.body(), Map.class);
        
            if (body.containsKey("variable") && body.get("variable") instanceof String &&
                body.containsKey("value") && body.get("value") instanceof String) {  // Expecting a List (array) here
                String variable = (String) body.get("variable");
                String value = (String) body.get("value"); // Get the list of selected auto positions
        
                if (variable.equals("reefPositionValue")) {
                     // Convert List to Array
                    reefPositionPublisher.set(value); // Update auto positions in NetworkTables
                }

                if (variable.equals("sourcePositionValue")) {
                    // Convert List to Array
                   sourcePositionPublisher.set(value); // Update auto positions in NetworkTables
               }
        
                ctx.status(200);
            } else {
                ctx.status(400).result("Invalid input");
            }
        });
        app.start(5800);
    }

    // Method to set alliance status
    public void setAlliance() {
        alliancePublisher.set(DriverStation.getAlliance().get() == Alliance.Blue);
    }

    // Getters for selected positions
    public String getSelectedScorePosition() {
        return reefPositionSubscriber.get();
    }

    public String getSelectedIntakePosition() {
        return sourcePositionSubscriber.get();
    }

    public String[] getSelectedAuto() {
        return autoSubscriber.get();  // Returns an array of selected auto positions
    }
}
