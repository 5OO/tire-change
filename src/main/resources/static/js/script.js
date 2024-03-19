const tireShopDropdown = document.getElementById("tireShopDropdown");
const vehicleTypeCheckboxes = document.getElementById("vehicleTypeCheckboxes");

tireShopDropdown.addEventListener("change", () => {
    const selectedShopName = tireShopDropdown.value;
    fetchAvailableVehicleTypes(selectedShopName)
        .then(vehicleTypes => updateVehicleCheckboxes(vehicleTypes));
});

function fetchAvailableVehicleTypes(shopName) {
    return fetch(`/tire-shops/${shopName}/vehicles`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Could not fetch vehicle types'); // Handle errors
            }
            return response.json();
        });
}

function updateVehicleCheckboxes(vehicleTypes) {
    vehicleTypeCheckboxes.innerHTML = ''; // Clear existing checkboxes
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
