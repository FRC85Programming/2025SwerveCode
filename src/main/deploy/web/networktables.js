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

        fetch("/setPosition", {
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

        fetch("/setPosition", {
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

    let autoSelections = [];

    function selectAutoPosition(position) {
        if (autoSelections.length >= 4) {
            console.log("Maximum of 4 selections allowed.");
            return;
        }

        const container = document.querySelector('.autocontainer');
        const selectionBox = document.createElement('div');
        selectionBox.classList.add('selection-box');

        const indicator = document.createElement('span');
        indicator.textContent = position.toUpperCase();
        indicator.classList.add('indicator');

        const dropdown1 = document.createElement('select');
        dropdown1.innerHTML = `<option value="4">L4</option>
                            <option value="3">L3</option>
                            <option value="2">L2</option>`;

        const dropdown2 = document.createElement('select');
        dropdown2.innerHTML = `<option value="1">Left Source</option>
                            <option value="2">Right Source</option>`;

        selectionBox.appendChild(indicator);
        selectionBox.appendChild(dropdown1);
        selectionBox.appendChild(dropdown2);

        container.appendChild(selectionBox);

        autoSelections.push({ position, dropdown1, dropdown2 });
    }

    function updateAutoSelection() {
        const packagedSelections = autoSelections.map(selection => {
            const dropdown1Value = selection.dropdown1.value;
            const dropdown2Value = selection.dropdown2.value;
            return `${selection.position.charAt(selection.position.length - 1).toUpperCase()}${dropdown1Value}${dropdown2Value}`;
        });

        fetch("/toggle", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ variable: "autoValue", value: packagedSelections })
        })
        .then(response => {
            if (response.ok) {
                console.log("Selections sent to NetworkTables:", packagedSelections);
            } else {
                console.error("Failed to send selections.");
            }
        })
        .catch(error => console.error("Error:", error));

    }

    function clearAutoSelections() {
        document.querySelectorAll('.selection-box').forEach(box => box.remove());
        autoSelections = [];
    }

    function selectCage(position) {
        var snd = new Audio("sounds/metalpipe.mp3");
        snd.play();
        snd.currentTime=0;

        updateButtonSelection("reef", position);
        fetch("/setPosition", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ variable: "cagePositionValue", value: position })
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
