# react-native-local-notifications
Manageable local notifications for React Native on iOS and Android. Create, update and delete local notifications by their unique id. The push notification title is the app name. When you open the app all displayed local notifications will be removed and the badge counter will be reset on iOS. 

NOTICE:
- for React Native < 0.47 use react-native-local-notifications <1.x.x
- for React Native > 0.47 use react-native-local-notifications >=1.x.x

## Setup

Fast and easy:
```bash
npm install react-native-local-notifications --save
react-native link react-native-local-notifications
```

Or manual: add the latest version as dependeny to your package.json.

```javascript
{
  "name": "YourProject",
  ...
  },
  "dependencies": {
    ...
    "react-native-local-notifications": "0.1.0",
    ...
  }
```

#### iOS
* Add RNLocalNotifications.xcoderproj into your project in the Libraries folder.
* Add the .a file on the General tab of your target under Linked Frameworks And Libraries
* Add the .a file on the Build Phases tab of your target under Link Binary With Libraries
* In the AppDelegate.m file of your xcode project add:
    ```
    - (void)applicationDidBecomeActive:(UIApplication *)application
    {
      [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0]; //Allways reset number of notifications shown at the icon
      for (UILocalNotification * notification in [[UIApplication sharedApplication] scheduledLocalNotifications]) { //Also remove all shown notifications
        if ([notification.fireDate compare:[NSDate date]] == NSOrderedAscending) {
          [[UIApplication sharedApplication] cancelLocalNotification:notification];
        }
      }
    }
    ```
* Add Alarm.caf and Silence.caf to the Resources folder of your xcode project. (can be found in react-native-local-notifications/ios/RNLocalNotifications)

#### Android
* In the AndroidManifest.xml file of your android studio project add:
    ```
    <receiver android:process=":remote" android:name="com.github.wumke.RNLocalNotifications.AlarmReceiver" android:exported="true"></receiver>
    ```
* In the MainActivity.java file of your android studio project add:
  ```
  @Override
      public void onResume() {
          super.onResume();
          NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          nMgr.cancelAll();
      }
  ```
* In the settings.gradle
  ```
    include ':react-native-local-notifications', ':app'
    project(':react-native-local-notifications').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-local-notifications/android')
  ```
* In the build.gradle
  ```
    compile project(':react-native-local-notifications')
  ```
* In MainApplication.java
  ```
    import com.github.wumke.RNLocalNotifications.RNLocalNotificationsPackage;
    ...
    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
        ...
        new RNLocalNotificationsPackage(),
        ...
      );
    }
    ...
  ```
## Usage

####Examples:
```javascript
import RNLocalNotifications from 'react-native-local-notifications';
...
//RNLocalNotifications.setAndroidIcons(largeIconName, largeIconType, smallIconName, smallIconType);
RNLocalNotifications.setAndroidIcons("ic_launcher", "mipmap", "notification_small", "drawable"); //this are the default values, this function is optional

//RNLocalNotifications.createNotification(id, text, datetime, sound[, hiddendata]);
RNLocalNotifications.createNotification(1, 'Some text', '2017-01-02 12:30', 'default');

//RNLocalNotifications.updateNotification(id, text, datetime, sound[, hiddendata]);
RNLocalNotifications.updateNotification(1, 'Some modifications to text', '2017-01-02 12:35', 'silence');

//RNLocalNotifications.deleteNotification(id);
RNLocalNotifications.deleteNotification(1);
...
```
#### Parameter explanation:
* id (Integer): Unique value to be able to edit or cancel scheduled notifications.
* text (String): The message text.
* datetime (String): The date + time to show the notification, as a string in the format 'yyyy-mm-dd hh:mm'.
* sound (String): Which sound is played: '' or 'silence' for vibration only, 'default' for system alarm sound, custom sound namefor self added ringtones.
* hiddendata (String): Invisible data that can be used to perform custom actions when the mobile app is opened by clicking on the local notification.

#### Add custom sounds:

Convert your ringtone to .caf and .mp3 file formats.

__iOS__: Add yoursound.caf to the Resources folder of your xcode project.  
__Android__: Add yoursound.mp3 to the 'raw' folder

Use 'yoursound' as string for the sound parameter.

#### Hidden/extra data:

When you need to include custom, non-visible, data (for example object id's) to your notifications provide the optional 'hiddendata' parameter to createNotification/updateNotification.

The value will be available as hiddendata (Android) or userData.hiddendata (iOS) when you click the notification.

Note that 'hiddendata' must be a string, so if you want to include json objects you need to encode/decode the data yourself.

## Versioning

This project uses semantic versioning: MAJOR.MINOR.PATCH.
This means that releases within the same MAJOR version are always backwards compatible. For more info see [semver.org](http://semver.org/).

## Licence

MIT (see LICENCE file)

## Release notes

#### 2.0.0

Breaking changes
- none

New features / Updates
- Changed licence to MIT
- Custom sounds
- Hidden data
- Set Android notification icons type/name

Fixes
- Readme delete notification example
