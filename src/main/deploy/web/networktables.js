document.addEventListener("DOMContentLoaded", function () {
    // Ensure elements exist before interacting with them
    const autoselector = document.getElementById('autoselector');
    if (autoselector) {
        autoselector.addEventListener('change', function() {
            var selectedValue = this.value;
            updateNetworkTables(selectedValue, "auto");
        });
    }

    // NetworkTables listener
    NetworkTables.addGlobalListener((key, value) => {
        if (key === "/isBlue") {
            const isBlue = value === true;
            const reefImage = document.getElementById("reef"); // Fixed selector
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

function updateNetworkTables(valueToPost, group) {
    const buttons = document.querySelectorAll(`.${group}container .button`);
    buttons.forEach(button => button.classList.remove("selected"));

    const clickedButton = document.querySelector(`img[onclick*="${valueToPost}"]`);
    if (clickedButton) {
        clickedButton.classList.add("selected");
    }

    const variable = group === "reef" 
        ? "reefPositionValue" 
        : group === "source" 
        ? "sourcePositionValue" 
        : "autoValue";

    fetch("/toggle", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ variable: variable, value: valueToPost })
    })
    .then(response => {
        if (response.ok) {
            console.log(`NetworkTables ${variable} updated to ${valueToPost}!`);
        } else {
            console.error("Failed to update NetworkTables value.");
        }
    })
    .catch(error => console.error("Error:", error));
}