# Silentium Morse Messenger

### Building
Required steps:

 + Java version  
   Java version required to build: `11`
   
 + Gradle version  
   Gradle version required to build: `7.4`  
   Gradle 'wrapper' task is configured to install correct version if wrapper jar is present

+ Google Services configuration JSON  
  File is required for app to build, must be located at `./app/google-services.json`  
  
NB! If launched on emulator, application requires image with bundled Google Play Services.

NB! Application instance should be added to Firebase Console for Google SignIn using.

Additional file that has to be present for building `./firebase.properties`  
It should have following structure:
```properties
web.key=[Cloud Messaging API (Legacy) Server Key]
```  
Cloud Messaging API (Legacy) Server Key can be found in firebase settings

Command that should be used for building: `gradle buildRelease` (or `gradle buildDebug` for debug)

### Instrumental Testing
Additional file that has to be present for instrumental testing `./test.properties`  
It should have following structure:
```properties
auth.number=+XYYYYYYYYYY
auth.code=ZZZZZZ
```  
where:  
`+XYYYYYYYYYY` is a phone number for testing (defined in firebase project)  
`ZZZZZZ` is a verification code for testing (defined in firebase project)

Command that should be used for instrumental testing: `gradle connectedDebugAndroidTest`

### Unit Testing
Command that should be used for unit testing: `gradle testDryUnitTest`
