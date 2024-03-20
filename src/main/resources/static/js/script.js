const vehicleTypeCheckboxes = document.getElementById("vehicleTypeCheckboxes");
const tireShopDropdown = document.getElementById("tireShopDropdown");


// Function to get selected tire shops
function getSelectedTireShops() {
    const selectedOptions = [...tireShopDropdown.selectedOptions];
    return selectedOptions.map(option => option.value);
}

tireShopDropdown.addEventListener("change", () => {
    const selectedShopName = tireShopDropdown.value;
    fetchAvailableVehicleTypes(selectedShopName)
        .then(vehicleTypes => updateVehicleCheckboxes(vehicleTypes));
});



// Function to get selected vehicle types
function getSelectedVehicleTypes() {
    const checkboxes = document.querySelectorAll("#vehicleTypeCheckboxes input[type='checkbox']:checked");
    return [...checkboxes].map(checkbox => checkbox.value);
}
function fetchAvailableVehicleTypes(shopName) {
    return fetch(`/tire-shops/${shopName}/vehicles`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Could not fetch vehicle types'); // Handle errors
            }
            return response.json();
        });
}


const vehicleTypeCheckboxesDiv = document.getElementById("vehicleTypeCheckboxes");

function updateVehicleCheckboxes(vehicleTypes) {
    vehicleTypeCheckboxes.innerHTML = ''; // Clear existing checkboxes

    // Only calculate allVehicleTypes if needed for initial population
    let allVehicleTypes = null;
    if (!vehicleTypes) {
        allVehicleTypes = new Set();
        tireShops.forEach(shop => shop.vehicleTypes.forEach(type => allVehicleTypes.add(type)));
    }

    const types = vehicleTypes || [...allVehicleTypes]; // Use provided types or all types

    vehicleTypes.forEach(vehicleType => {
        let checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.name = 'vehicleType';
        checkbox.value = vehicleType;

        let label = document.createElement('label');
        label.htmlFor = vehicleType;
        label.appendChild(document.createTextNode(vehicleType));

        vehicleTypeCheckboxes.appendChild(checkbox);
        vehicleTypeCheckboxes.appendChild(label);
    });
}

// ... (Flatpickr initialization and 'Apply' button logic
flatpickr("#dateRange", {
    mode: "range", // Enable range selection mode
});

// Apply button logic
const applyButton = document.getElementById("applyFilters");
applyButton.addEventListener("click", () => {
    const dateRange = document.getElementById("dateRange").value;
    // Split the date range into start and end dates (Example, adjust if needed)
    const [startDate, endDate] = dateRange.split(" to ");
    const selectedTireShops = getSelectedTireShops();
    const selectedVehicleTypes = getSelectedVehicleTypes();

    // Construct query parameters for AJAX
    const queryParams = new URLSearchParams({
        from: startDate,
        until: endDate,
        tireShops: selectedTireShops.join(","), // Comma-separated if multiple
        vehicleTypes: selectedVehicleTypes.join(",")
    }).toString();

    // Send AJAX request to backend with startDate and endDate

    fetch("/tire-changes-available?from=" + startDate + "&until=" + endDate)
        .then(response => response.text())
        .then(htmlString => {
            // Update table display
            updateTable(htmlString);
        })
        .catch(error => console.error("Error fetching data:", error));

    function updateTable(data) {
        const tableBody = document.getElementById("resultsTable").querySelector("tbody");
        tableBody.innerHTML = ''; // Clear existing rows

        const tempContainer = document.createElement('div'); // Create a temporary container
        tempContainer.innerHTML = data; // Put the received HTML into the container

        const newRows = tempContainer.querySelectorAll("table tbody tr"); // Extract the table rows

        newRows.forEach(row => {
            tableBody.appendChild(row.cloneNode(true)); // Clone and append rows to result table
        });
    }


    function formatDateTime(bookingTime) {
        const dateTime = new Date(bookingTime);
        const options =  { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        return dateTime.toLocaleDateString(undefined, options);
    }

});
