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
    private final StringPublisher positionPublisher;
    private final StringSubscriber positionSubscriber;

    public WebServer() {
        // Initialize NetworkTables
        var table = NetworkTableInstance.getDefault().getTable("reefTable");
        positionPublisher = table.getStringTopic("positionValue").publish();
        positionSubscriber = table.getStringTopic("positionValue").subscribe(new String(), PubSubOption.sendAll(true));
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
            // Parse the JSON body
            var mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(ctx.body(), Map.class);

            // Update NetworkTables value
            if (body.containsKey("value") && body.get("value") instanceof String) {
                String value = (String) body.get("value");
                positionPublisher.set(value);
                ctx.status(200);
            } else {
                ctx.status(400).result("Invalid input");
            }
        });

        app.start(5800);

        System.out.println("Server started on http://localhost:5800");
    }

    public String getSelectedPosition() {
        return positionSubscriber.get();
    }


}
