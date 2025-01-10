package org.example;

import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import io.javalin.Javalin;

public class WebServer {
    private final BooleanPublisher togglePublisher;

    public WebServer() {
        // Initialize NetworkTables
        var table = NetworkTableInstance.getDefault().getTable("reefTable");
        togglePublisher = table.getBooleanTopic("toggleValue").publish();

        // Start the web server
        var app = Javalin.create(config -> {
            config.staticFiles.add("src/main/resources/web"); // Serve static files from this folder
        }).start(5800);

        // Handle toggle endpoint
        app.post("/toggle", ctx -> {
            boolean currentValue = togglePublisher.get();
            togglePublisher.set(!currentValue); // Toggle the value
            ctx.result("Toggled to " + !currentValue);
        });

        System.out.println("Server started on http://localhost:5800");
    }
}