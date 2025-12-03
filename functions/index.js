const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendAdminNotification = onDocumentCreated("posts/{postId}", (event) => {
    const snapshot = event.data;
    if (!snapshot) {
        return null;
    }

    const newData = snapshot.data();

    // Only alert if status is pending
    if (newData.status === "pending") {
        const payload = {
            notification: {
                title: "New Pending Post",
                body: `New post "${newData.title}" needs approval.`
            },
            topic: "admin_notifications"
        };

        return admin.messaging().send(payload)
            .then((response) => {
                console.log("Successfully sent message:", response);
                return null;
            })
            .catch((error) => {
                console.log("Error sending message:", error);
                return null;
            });
    }

    return null;
});