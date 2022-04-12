import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import { HttpsError } from "firebase-functions/lib/providers/https";
import { TokenMessage } from "firebase-admin/lib/messaging";


const serviceAccount = require("../adminsdk.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://silentium-morse-messenger-default-rtdb.europe-west1.firebasedatabase.app"
});


interface Contact {
    name?: string | null;
    contact?: string;
    key?: string;
    picture?: string | null;
    token?: string | null;
}

function isContact(arg: any): arg is Contact {
    return Object.keys(arg).every(key => {
        switch (key) {
            case 'name':
            case 'contact':
            case 'key':
            case 'picture':
            case 'token':
                return arg[key] || (typeof arg[key] == 'string');
            default:
                return false;
        }
    });
}


interface Message {
    text: string;
    id: string;
}

function isMessage(arg: any): arg is Message {
    return Object.keys(arg).every(key => {
        switch (key) {
            case 'text':
            case 'id':
                return arg[key] && (typeof arg[key] == 'string');
            default:
                return false;
        }
    });
}


function next(str: string): string {
    if (str == "") return "a";
    let lastZ = str.length - 1;
    while (str.at(lastZ) == 'z' && lastZ >= 0) lastZ--;
    if (lastZ == -1) return `${str}a`;
    else return `${str.slice(0, lastZ)}${String.fromCharCode(str.at(lastZ)!!.charCodeAt(0) + 1)}${str.slice(lastZ + 2)}`;
}


export const setUser = functions.https.onCall(async (data, context) => {
    const uid = context.auth?.uid;
    if (!uid) throw new HttpsError('permission-denied', "User not authenticated!");
    if (!isContact(data)) throw new HttpsError('invalid-argument', "Passed object is not a Contact!");

    if (data.contact) await admin.database().ref(`/${uid}/contact`).set(data.contact);
    if (data.name != undefined) await admin.auth().updateUser(uid, { displayName: data.name });
    if (data.picture != undefined) await admin.auth().updateUser(uid, { photoURL: data.picture });
    if (data.key) await admin.database().ref(`/${uid}/public_key`).set(data.contact);
    if (data.token) await admin.database().ref(`/${uid}/token`).set(data.token);
});

export const deleteUser = functions.auth.user().onDelete((user) => {
    admin.database().ref(`/${user.uid}`).remove().catch((error) => {
        throw new HttpsError('unknown', `Could not delete user folder!\n${error}`);
    });
});

export const queryUsers = functions.https.onCall(async (data, context) => {
    const uid = context.auth?.uid;
    if (!uid) throw new HttpsError('permission-denied', "User not authenticated!");
    if (typeof data != 'string') throw new HttpsError('invalid-argument', "Passed object is not a user contact!");

    const snapshot = await admin.database().ref().orderByChild('contact').startAt(data).endAt(next(data)).get();
    const contacts = Object();
    snapshot.forEach((child) => {
        contacts[child.key!!] = child.val().contact
    });
    return contacts;
});

export const loadUser = functions.https.onCall(async (data, context) => {
    const uid = context.auth?.uid;
    if (!uid) throw new HttpsError('permission-denied', "User not authenticated!");
    if (typeof data != 'string') throw new HttpsError('invalid-argument', "Passed object is not a user id!");

    const userAuth = await admin.auth().getUser(data);
    const userDB = await admin.database().ref(`/${data}`).get();
    return {
        uid: userAuth.uid,
        name: userAuth.displayName,
        contact: userDB.child('contact').val(),
        was_online: userAuth.metadata.lastRefreshTime,
        public_key: userDB.child('public_key').val(),
        profile_pic: userAuth.photoURL
    };
});

export const sendMessage = functions.https.onCall(async (data, context) => {
    const uid = context.auth?.uid;
    if (!uid) throw new HttpsError('permission-denied', "User not authenticated!");
    if (!isMessage(data)) throw new HttpsError('invalid-argument', "Passed object is not a message!");

    const user = await admin.database().ref(`/${data.id}`).get()
    const message: TokenMessage = {
        data: { text: data.text },
        android: { priority: 'high' },
        token: user.child('token').val()
    };
    await admin.messaging().send(message);
});
