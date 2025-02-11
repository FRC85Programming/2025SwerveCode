document.addEventListener("DOMContentLoaded", function () { 
        // NetworkTables listener
        NetworkTables.addGlobalListener((key, value) => {
            if (key === "/isBlue") {
                const isBlue = value === true;
                const reefImage = document.getElementById("reef");
                const buttons = document.querySelectorAll(".button");

                if (reefImage) {
                    reefImage.src = `images/reefs/reef-${isBlue ? "blue" : "red"}.jpg`;
                }

                buttons.forEach(button => {
                    button.style.borderColor = isBlue ? "#0048a7" : "#a20010";
                });
            }
        }, true);
    });

    // Function to update button selection UI
    function updateButtonSelection(group, value) {
        const buttons = document.querySelectorAll(`.${group}container .button`);
        buttons.forEach(button => button.classList.remove("selected"));

        const clickedButton = document.querySelector(`.${group}container .button[data-value="${value}"]`);
        if (clickedButton) {
            clickedButton.classList.add("selected");
        }
    }

    // Function to send reef position to NetworkTables
    function sendReefPosition(position) {
        updateButtonSelection("reef", position);

        fetch("/toggle", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ variable: "reefPositionValue", value: position })
        })
        .then(response => {
            if (response.ok) {
                console.log(`Reef position ${position} sent to NetworkTables.`);
            } else {
                console.error("Failed to send reef position.");
            }
        })
        .catch(error => console.error("Error:", error));
    }

    function updateAutoSelection(autoValue) {
        updateButtonSelection("auto", autoValue);

        fetch("/toggle", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ variable: "autoValue", value: autoValue })
        })
        .then(response => {
            if (response.ok) {
                console.log(`Auto selection updated to ${autoValue} in NetworkTables.`);
            } else {
                console.error("Failed to update auto selection.");
            }
        })
        .catch(error => console.error("Error:", error));
    }

    // Function to send source position to NetworkTables
    function sendSourcePosition(position) {
        updateButtonSelection("source", position);

        fetch("/toggle", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ variable: "sourcePositionValue", value: position })
        })
        .then(response => {
            if (response.ok) {
                console.log(`Source position ${position} sent to NetworkTables.`);
            } else {
                console.error("Failed to send source position.");
            }
        })
        .catch(error => console.error("Error:", error));
    }

    let selectedAutoPositions = [];

    // Function to handle auto button selection (adding selected positions in order)
    function selectAutoPosition(position) {
        // Add position to the array if it's not already selected
        if (!selectedAutoPositions.includes(position)) {
            selectedAutoPositions.push(position);
        }

        // Update the button appearance (optional)
        updateButtonSelection("auto");
    }

    // Function to handle the update button click (send the entire batch of selected auto positions)
    function updateAutoSelection() {
        // If there are selected positions, send them to NetworkTables
        if (selectedAutoPositions.length > 0) {
            fetch("/toggle", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ variable: "autoValue", value: selectedAutoPositions })
            })
            .then(response => {
                if (response.ok) {
                    console.log("Auto positions sent to NetworkTables:", selectedAutoPositions);
                    // Clear the selections after sending the batch
                    selectedAutoPositions = [];
                } else {
                    console.error("Failed to update auto positions.");
                }
            })
            .catch(error => console.error("Error:", error));
        } else {
            console.log("No positions selected.");
        }
    }
