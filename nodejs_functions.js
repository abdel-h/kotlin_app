'use-strict';

const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
exports.addFriendNotification = functions.database
    .ref('users/{user_id}/friends/{friend_id}')
    .onWrite((change, context) => {
        const to_user_id = context.params.user_id;
        const friend_id = change.after.val();
        console.log(`Friend Request to ${to_user_id} from ${friend_id}`);
        // get target user device token - stored at users/:to_user_id/token_id

        let to_user_ref = admin.database().ref(`users/${to_user_id}`);

        return to_user_ref
            .once('value')
            .then(snapshot => {
                let token_id = snapshot.child('token_id').val();
                return token_id;
            })
            .then(token_id => {
                let message = {
                    data: {
                        title: 'New Friend Request',
                        body: 'Someone wants to connect with you',
                        action: 'new_friend_request'
                    },
                    token: token_id
                };
                return admin.messaging().send(message);
            })
            .then(res => {
                console.log(`Message ${res}`);
                return;
            })
            .catch(err => {
                console.log('Error' + err);
            });
    });

exports.locationInviteNotification = functions.database
    .ref('users/{user_id}/invites/{friend_id}')
    .onWrite((change, context) => {
        const to_user_id = context.params.user_id;
        const from_user_id = change.after.val();
        console.log(
            `Location invite Request to ${to_user_id} from ${from_user_id}`
        );
        // get target user device token - stored at users/:to_user_id/token_id

        let to_user_ref = admin.database().ref(`users/${to_user_id}`);

        return to_user_ref
            .once('value')
            .then(snapshot => {
                let token_id = snapshot.child('token_id').val();
                return token_id;
            })
            .then(token_id => {
                let message = {
                    data: {
                        title: 'Location Share invite',
                        body: 'Someone wants to connect with you',
                        action: 'location_invite'
                    },
                    token: token_id
                };
                return admin.messaging().send(message);
            })
            .then(res => {
                console.log(`Message ${res}`);
                return;
            })
            .catch(err => {
                console.log('Error' + err);
            });
    });
