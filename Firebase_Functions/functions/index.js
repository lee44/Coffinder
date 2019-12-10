const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.pushNotification = functions.database.ref('/Chat/{chatId}/{pushId}').onCreate((snapshot, context) => 
{
    //const sender_id = snapshot.child('Sender_ID').val();
    const receiver_id = snapshot.child('Receiver_ID').val();
    const message = snapshot.child('Message').val();

    return admin.database().ref('/Users/'+ receiver_id +'/DeviceToken').once('value').then(function(tokensSnapshot)
    {
        var token = tokensSnapshot.val();

        // console.log("Sender_ID: ",sender_id);
        // console.log("Receiver_ID: ",receiver_id);
        // console.log("Message: ",message);
        // console.log("Device Token: ",token);

        var payload = 
        {
            notification: 
            {
                title: 'Message',
                body: message
            },
            token: token  
        };

        return admin.messaging().send(payload).then(function (response) 
        {
          console.log("Successfully sent message: ", JSON.stringify(response));
          return;
          }).catch(function (error) 
          {
          console.log("Error sending message: ", error);
          return;
        });
    });
});