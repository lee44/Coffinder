const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.insertIntoDB = functions.https.onRequest((req, res) => 
{
    const text = req.query.text;
    admin.database().ref('/Users').push({Text: 'lakers'}).then(snapshot => 
    {
        res.redirect(303, snapshot.ref);
    })
});

// Listens for new messages added to /Users/pushId/text and creates an uppercase version of the message to /Users/pushId/Uppercase
exports.makeUppercase = functions.database.ref('/Users/{pushId}/Text').onCreate((snapshot, context) => 
{
  // snapshot gives access to current value of what was written to the Realtime Database.
  // context give access to the pushId  
  const text = snapshot.val();
  console.log('Uppercasing', context.params.pushId, text);
  const uppercase = text.toUpperCase();
  // You must return a Promise when performing asynchronous tasks inside a Functions such as
  // writing to the Firebase Realtime Database.
  // Setting an "Uppercase" node which is a sibling of the "Text" node in the Realtime Database returns a Promise.
  return snapshot.ref.parent.child('Uppercase').set(uppercase);
});

exports.chatUpdate = functions.database.ref('/Chat/{chatId}/{pushId}/text').onCreate((snapshot, context) => 
{
  const text = snapshot.val();
  console.log('Uppercasing', context.params.pushId, text);
  const uppercase = text.toUpperCase();
  return snapshot.ref.parent.child('Uppercase').set(uppercase);
});

exports.pushNotification = functions.database.ref('/Chat/{chatId}/{pushId}/text').onCreate((snapshot, context) => 
{
    return admin.database().ref('/Users/{userID}/DeviceToken').once('value').then(function(tokensSnapshot)
    {
        const tokens = Object.keys(tokensSnapshot.val());

        const payload = 
        {
            notification: 
            {
                title: 'Message',
                body: snapshot.val(),
                sound: "default"
            }   
        };

        return admin.messaging().sendToDevice(tokens, payload);
    });
});