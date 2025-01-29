function updateNetworkTables(valueToPost, group) {
    const buttons = document.querySelectorAll(`.${group}container .button`);
    buttons.forEach(button => button.classList.remove("selected"));

    const clickedButton = document.querySelector(`img[onclick*="${valueToPost}"]`);
    if (clickedButton) {
        clickedButton.classList.add("selected");
    }

    const variable = group === "reef" ? "reefPositionValue" : source ? "sourcePositionValue" : "autoValue";

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

document.addEventListener("DOMContentLoaded", function () {
    // Wait until NetworkTables is ready
    NetworkTables.addGlobalListener((key, value) => {
        if (key === "/isBlue") {
            const isBlue = value === true;
            const reefImage = document.querySelector("reef");
            const buttons = document.querySelectorAll(".button");

            // Set reef image
            reefImage.src = `images/reefs/reef-${isBlue ? "blue" : "red"}.jpg`;

            // Set button border colors
            buttons.forEach(button => {
                button.style.borderColor = isBlue ? "#0048a7" : "#a20010";
            });
        }
    }, true);
});

