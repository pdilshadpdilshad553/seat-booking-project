// Backend address
const API = "http://localhost:8080/api";

// These variables remember what the user is doing
let currentShowId = 0;       // which show is open
let seatPrice = 0;           // price of one seat
let selectedSeats = [];      // seats the user clicked, e.g. [{id: 15, label: "A5"}]


// ---------------------------------------------------
// STEP 1: Load the shows when the page opens
// ---------------------------------------------------
async function loadShows() {
    const response = await fetch(API + "/shows");
    const shows = await response.json();

    const showList = document.getElementById("showList");
    showList.innerHTML = "";

    for (let i = 0; i < shows.length; i++) {
        const show = shows[i];

        // make one button for each show
        const button = document.createElement("button");
        button.className = "show-btn";
        button.innerHTML = "<strong>" + show.screenName + "</strong><br>" +
                           show.movieTitle + "<br>" +
                           formatTime(show.showTime);

        // when the button is clicked, open that show
        button.addEventListener("click", function () {
            openShow(show.id);
        });

        showList.appendChild(button);
    }
}


// ---------------------------------------------------
// STEP 2: Open one show and get its seats from backend
// ---------------------------------------------------
async function openShow(showId) {
    currentShowId = showId;
    selectedSeats = [];

    document.getElementById("confirmation").classList.add("hidden");
    document.getElementById("message").textContent = "";

    const response = await fetch(API + "/shows/" + showId + "/seats");
    const data = await response.json();

    seatPrice = data.price;

    // fill the movie information
    document.getElementById("infoMovie").textContent = data.movieTitle;
    document.getElementById("infoScreen").textContent = data.screenName;
    document.getElementById("infoTime").textContent = formatTime(data.showTime);
    document.getElementById("infoPrice").textContent = "₹" + data.price;

    // fill the counts
    document.getElementById("availableCount").textContent = data.availableCount;
    document.getElementById("bookedCount").textContent = data.bookedCount;
    document.getElementById("blockedCount").textContent = data.blockedCount;

    drawSeats(data.seats);
    updateSummary();

    // show the booking section
    document.getElementById("bookingSection").classList.remove("hidden");
}


// ---------------------------------------------------
// STEP 3: Draw all the seats (8 rows x 10 columns)
// ---------------------------------------------------
function drawSeats(seats) {
    const grid = document.getElementById("seatGrid");
    grid.innerHTML = "";

    for (let i = 0; i < seats.length; i++) {
        const seat = seats[i];
        const label = seat.row + seat.number;       // example: A5

        const box = document.createElement("div");
        box.textContent = label;

        // status is AVAILABLE, BOOKED or BLOCKED
        // the css class gives it a color: available, booked or blocked
        box.className = "seat " + seat.status.toLowerCase();

        // only available seats can be clicked
        if (seat.status === "AVAILABLE") {
            box.addEventListener("click", function () {
                clickSeat(seat.id, label, box);
            });
        }

        grid.appendChild(box);
    }
}


// ---------------------------------------------------
// STEP 4: User clicks a seat (select or un-select)
// ---------------------------------------------------
function clickSeat(seatId, label, box) {
    // check if this seat is already selected
    let position = -1;
    for (let i = 0; i < selectedSeats.length; i++) {
        if (selectedSeats[i].id === seatId) {
            position = i;
        }
    }

    if (position === -1) {
        // not selected before, so select it
        selectedSeats.push({ id: seatId, label: label });
        box.classList.remove("available");
        box.classList.add("selected");
    } else {
        // already selected, so remove it
        selectedSeats.splice(position, 1);
        box.classList.remove("selected");
        box.classList.add("available");
    }

    updateSummary();
}


// Show selected seats and total amount
function updateSummary() {
    let names = "";
    for (let i = 0; i < selectedSeats.length; i++) {
        names = names + selectedSeats[i].label;
        if (i < selectedSeats.length - 1) {
            names = names + ", ";
        }
    }

    if (names === "") {
        names = "None";
    }

    document.getElementById("selectedSeats").textContent = names;
    document.getElementById("totalAmount").textContent = "₹" + (seatPrice * selectedSeats.length);
}


// ---------------------------------------------------
// STEP 5: User clicks "Book Now"
// ---------------------------------------------------
async function bookSeats() {
    const name = document.getElementById("customerName").value.trim();
    const phone = document.getElementById("phone").value.trim();

    // simple checks before calling the backend
    if (selectedSeats.length === 0) {
        showMessage("Please select at least one seat.", "error");
        return;
    }
    if (name === "") {
        showMessage("Please enter your name.", "error");
        return;
    }
    if (phone.length !== 10) {
        showMessage("Phone number must be 10 digits.", "error");
        return;
    }

    // collect only the seat ids
    const seatIds = [];
    for (let i = 0; i < selectedSeats.length; i++) {
        seatIds.push(selectedSeats[i].id);
    }

    // this is the JSON we send to the backend
    const bookingData = {
        showId: currentShowId,
        customerName: name,
        phone: phone,
        seatIds: seatIds
    };

    const response = await fetch(API + "/bookings", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(bookingData)
    });

    const result = await response.json();

    if (response.ok) {
        // booking success (status 201)
        showConfirmation(result);
        document.getElementById("customerName").value = "";
        document.getElementById("phone").value = "";
        showMessage("", "");
    } else {
        // error from backend (400, 404 or 409)
        showMessage(result.message, "error");
    }

    // reload the seats so the latest colors and counts show
    openShow(currentShowId);
}


// Show the booking confirmation box
function showConfirmation(booking) {
    const box = document.getElementById("confirmation");

    box.innerHTML =
        "<h2>✅ Booking confirmed!</h2>" +
        "<p><b>Booking ID:</b> " + booking.bookingId + "</p>" +
        "<p><b>Movie:</b> " + booking.movieTitle + " (" + booking.screenName + ")</p>" +
        "<p><b>Time:</b> " + formatTime(booking.showTime) + "</p>" +
        "<p><b>Name:</b> " + booking.customerName + "</p>" +
        "<p><b>Seats:</b> " + booking.seats.join(", ") + "</p>" +
        "<p><b>Total paid:</b> ₹" + booking.totalAmount + "</p>";

    box.classList.remove("hidden");
}


// ---------------------------------------------------
// Small helper functions
// ---------------------------------------------------
function showMessage(text, type) {
    const message = document.getElementById("message");
    message.textContent = text;
    message.className = "message " + type;
}

function formatTime(timeText) {
    const date = new Date(timeText);
    return date.toLocaleString("en-IN", {
        day: "numeric",
        month: "short",
        hour: "numeric",
        minute: "2-digit"
    });
}


// ---------------------------------------------------
// Start the app
// ---------------------------------------------------
document.getElementById("bookBtn").addEventListener("click", bookSeats);
loadShows();