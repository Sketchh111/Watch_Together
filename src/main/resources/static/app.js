// ==================================================
// GLOBAL VARIABLES
// ==================================================

let stompClient = null;

let isSyncing = false;

let currentRoomId = "";


// ==================================================
// GET VIDEO
// ==================================================

const video = document.getElementById("video");


// ==================================================
// CONNECT TO ROOM
// ==================================================

function connect() {

    const roomId =
        document
            .getElementById("roomId")
            .value
            .trim();


    const username =
        document
            .getElementById("username")
            .value
            .trim();


    // ------------------------------
    // VALIDATION
    // ------------------------------

    if (!roomId) {

        alert("Enter Room ID!");

        return;
    }


    if (!username) {

        alert("Enter your name!");

        return;
    }


    currentRoomId = roomId;


    // ------------------------------
    // DISCONNECT OLD CONNECTION
    // ------------------------------

    if (stompClient !== null) {

        try {

            stompClient.disconnect();

        } catch (error) {

            console.log(error);

        }

    }


    // ------------------------------
    // CREATE SOCKJS CONNECTION
    // ------------------------------

    const socket =
        new SockJS(
            "http://localhost:8080/ws"
        );


    stompClient =
        Stomp.over(socket);


    // Disable unnecessary STOMP logs

    stompClient.debug = null;


    // ------------------------------
    // CONNECT
    // ------------------------------

    stompClient.connect(
        {},
        function () {

            console.log("Connected ✅");


            updateConnectionStatus(true);


            // ==========================================
            // VIDEO SUBSCRIPTION
            // ==========================================

            stompClient.subscribe(
                "/topic/video/" + roomId,
                function (message) {

                    handleVideoMessage(message);

                }
            );


            // ==========================================
            // CHAT SUBSCRIPTION
            // ==========================================

            stompClient.subscribe(
                "/topic/chat/" + roomId,
                function (message) {

                    handleChatMessage(message);

                }
            );
            stompClient.subscribe(
                "/topic/video/" + roomId,
                function (message) {
                    handleVideoMessage(message);
                }
            );

            // Chat subscription
            stompClient.subscribe(
                "/topic/chat/" + roomId,
                function (message) {
                    handleChatMessage(message);
                }
            );

            // Load previous messages
            loadChatHistory(roomId);

            console.log(
                "Joined room:",
                roomId
            );

        },
        function (error) {

            console.error(
                "WebSocket connection error:",
                error
            );


            updateConnectionStatus(false);


            alert(
                "Could not connect to server."
            );

        }
    );
}


// ==================================================
// CONNECTION STATUS
// ==================================================

function updateConnectionStatus(connected) {

    const status =
        document.getElementById(
            "connectionStatus"
        );


    if (connected) {

        status.innerHTML =
            "🟢 Connected";

        status.classList.remove(
            "disconnected"
        );

        status.classList.add(
            "connected"
        );

    } else {

        status.innerHTML =
            "🔴 Disconnected";

        status.classList.remove(
            "connected"
        );

        status.classList.add(
            "disconnected"
        );

    }
}


// ==================================================
// HANDLE VIDEO MESSAGE
// ==================================================

function handleVideoMessage(message) {

    const data =
        JSON.parse(message.body);


    console.log(
        "VIDEO EVENT:",
        data
    );


    // Prevent event loop

    isSyncing = true;


    // ==========================================
    // PLAY
    // ==========================================

    if (data.action === "play") {

        video.currentTime =
            Number(data.time);


        const playPromise =
            video.play();


        if (
            playPromise !== undefined
        ) {

            playPromise.catch(
                function (error) {

                    console.log(
                        "Play prevented:",
                        error
                    );

                }
            );

        }

    }


    // ==========================================
    // PAUSE
    // ==========================================

    else if (data.action === "pause") {

        video.currentTime =
            Number(data.time);


        video.pause();

    }


    // ==========================================
    // SEEK
    // ==========================================

    else if (data.action === "seek") {

        video.currentTime =
            Number(data.time);

    }


    // Allow local events again

    setTimeout(
        function () {

            isSyncing = false;

        },
        200
    );
}


// ==================================================
// SEND PLAY
// ==================================================

function sendPlay() {

    if (stompClient === null) {

        alert(
            "Please join the room first!"
        );

        return;
    }


    if (!currentRoomId) {

        return;
    }


    console.log(
        "Sending PLAY:",
        video.currentTime
    );


    stompClient.send(
        "/app/sync",
        {},
        JSON.stringify({

            roomId: currentRoomId,

            action: "play",

            time: video.currentTime

        })
    );
}


// ==================================================
// SEND PAUSE
// ==================================================

function sendPause() {

    if (stompClient === null) {

        alert(
            "Please join the room first!"
        );

        return;
    }


    if (!currentRoomId) {

        return;
    }


    console.log(
        "Sending PAUSE:",
        video.currentTime
    );


    stompClient.send(
        "/app/sync",
        {},
        JSON.stringify({

            roomId: currentRoomId,

            action: "pause",

            time: video.currentTime

        })
    );
}


// ==================================================
// SEND SEEK
// ==================================================

function sendSeek() {

    if (stompClient === null) {

        return;
    }


    if (!currentRoomId) {

        return;
    }


    console.log(
        "Sending SEEK:",
        video.currentTime
    );


    stompClient.send(
        "/app/sync",
        {},
        JSON.stringify({

            roomId: currentRoomId,

            action: "seek",

            time: video.currentTime

        })
    );
}


// ==================================================
// VIDEO SEEK EVENT
// ==================================================

video.addEventListener(
    "seeked",
    function () {

        // If seek came from another user,
        // don't send it back.

        if (isSyncing) {

            return;
        }


        sendSeek();

    }
);


// ==================================================
// CHAT MESSAGE HANDLER
// ==================================================

function handleChatMessage(message) {

    console.log("CHAT RECEIVED:", message.body);

    const data = JSON.parse(message.body);

    const chatBox =
        document.getElementById("chatBox");

    const currentUser =
        document
            .getElementById("username")
            .value
            .trim();

    // Remove welcome message
    const welcome =
        chatBox.querySelector(".welcome-message");

    if (welcome) {
        welcome.remove();
    }

    // Create message container
    const msgDiv =
        document.createElement("div");

    msgDiv.classList.add("message");

    // Current user = right
    if (data.sender === currentUser) {

        msgDiv.classList.add("right");

    } else {

        msgDiv.classList.add("left");
    }

    // Sender
    const senderDiv =
        document.createElement("strong");

    senderDiv.textContent =
        data.sender;

    // Message
    const messageDiv =
        document.createElement("div");

    messageDiv.textContent =
        data.message;

    // Time
    const timeDiv =
        document.createElement("small");

    timeDiv.classList.add("message-time");

    timeDiv.textContent =
        formatTime(data.sentAt);

    // Add everything
    msgDiv.appendChild(senderDiv);

    msgDiv.appendChild(messageDiv);

    msgDiv.appendChild(timeDiv);

    chatBox.appendChild(msgDiv);

    // Auto scroll
    chatBox.scrollTop =
        chatBox.scrollHeight;
}


// ==================================================
// SEND CHAT MESSAGE
// ==================================================

function sendMessage() {

    // ------------------------------
    // CHECK CONNECTION
    // ------------------------------

    if (stompClient === null) {

        alert(
            "Please join the room first!"
        );

        return;
    }


    // ------------------------------
    // GET VALUES
    // ------------------------------

    const roomId =
        document
            .getElementById("roomId")
            .value
            .trim();


    const sender =
        document
            .getElementById("username")
            .value
            .trim();


    const message =
        document
            .getElementById("chatInput")
            .value
            .trim();


    // ------------------------------
    // VALIDATION
    // ------------------------------

    if (!roomId) {

        alert(
            "Enter Room ID!"
        );

        return;
    }


    if (!sender) {

        alert(
            "Enter your name!"
        );

        return;
    }


    if (!message) {

        return;
    }


    // ------------------------------
    // SEND
    // ------------------------------

    stompClient.send(
        "/app/chat",
        {},
        JSON.stringify({

            roomId: roomId,

            sender: sender,

            message: message,

            type: "MESSAGE"

        })
    );


    // ------------------------------
    // CLEAR INPUT
    // ------------------------------

    document
        .getElementById("chatInput")
        .value = "";
}


// ==================================================
// ENTER KEY FOR CHAT
// ==================================================

document
    .getElementById("chatInput")
    .addEventListener(
        "keydown",
        function (event) {

            if (
                event.key === "Enter"
            ) {

                sendMessage();

            }

        }
    );


// ==================================================
// FORMAT CHAT TIME
// ==================================================

function formatTime(time) {

    if (!time) {
        return "";
    }

    const date = new Date(time);

    return date.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
    });
}

async function loadChatHistory(roomId) {

    try {

        const response = await fetch(
            "http://localhost:8080/api/rooms/"
            + roomId
            + "/messages"
        );

        if (!response.ok) {
            console.error("Failed to load chat history");
            return;
        }

        const messages = await response.json();

        const chatBox =
            document.getElementById("chatBox");

        const welcome =
            chatBox.querySelector(".welcome-message");

        if (welcome) {
            welcome.remove();
        }

        messages.forEach(function (data) {
            displayChatMessage(data);
        });

        chatBox.scrollTop =
            chatBox.scrollHeight;

    } catch (error) {

        console.error(
            "Chat history error:",
            error
        );
    }
}

function displayChatMessage(data) {
    console.log("CHAT DATA:", data);

    const chatBox =
        document.getElementById("chatBox");

    const currentUser =
        document
            .getElementById("username")
            .value
            .trim();

    const msgDiv =
        document.createElement("div");

    msgDiv.classList.add("message");

    const senderName =
        data.sender ||
        (data.user && data.user.username) ||
        data.username ||
        "Unknown User";

    if (senderName === currentUser) {
        msgDiv.classList.add("right");
    } else {
        msgDiv.classList.add("left");
    }

    // Sender
    const senderDiv =
        document.createElement("div");

    senderDiv.classList.add("sender-name");

    senderDiv.textContent =
        senderName;

    // Message
    const messageDiv =
        document.createElement("div");

    messageDiv.textContent =
        data.message;

    // Time
    const timeDiv =
        document.createElement("small");

    timeDiv.classList.add("message-time");

    timeDiv.textContent =
        formatTime(data.sentAt);

    msgDiv.appendChild(senderDiv);
    msgDiv.appendChild(messageDiv);
    msgDiv.appendChild(timeDiv);

    chatBox.appendChild(msgDiv);
}
