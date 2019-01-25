# react-native-local-notifications
Manageable local notifications for React Native on iOS and Android. Create, update and delete local notifications by their unique id. The push notification title is the app name. When you open the app all displayed local notifications will be removed and the badge counter will be reset on iOS. 

NOTICE:
- for React Native < 0.47 use react-native-local-notifications <1.x.x
- for React Native > 0.47 use react-native-local-notifications >=1.x.x
- for Android API lvl >=19 use react-native-local-notifications >=2.x.x

NOTIFICATIONS WILL NOT BE SHOWN WHEN YOUR APP IS IN THE FOREGROUND. (options to handle this situation might be added in future releases)

For latest Android versions, please check notification settings and battery management settings if notifications are not shown at the specified time. This libary aims to deliver exact notifications, which are not delayed by the system.
Creating a never ending back- or foregroundservice (API lvl >=26) can help stopping the device to kill your apps and not show your notifications on exact times. 
Also see https://dontkillmyapp.com for more info and tips to solve this problem for devices with custom battery saving packages.

## Setup

Fast and easy:
```bash
npm install react-native-local-notifications --save
react-native link react-native-local-notifications
```
Keep in mind that the link step only links the project in the native projects, more steps have to be done for both platforms, which are described in the Android and iOS section below... please verify manually that the link command successfully linked the libray to your project!

Or manual: add the latest version as dependeny to your package.json.

```javascript
{
  "name": "YourProject",
  ...
  },
  "dependencies": {
    ...
    "react-native-local-notifications": "2.0.0",
    ...
  }
```

#### iOS
* {auto-link} Add RNLocalNotifications.xcoderproj into your project in the Libraries folder.
* {auto-link}Add the .a file on the General tab of your target under Linked Frameworks And Libraries
* {auto-link}Add the .a file on the Build Phases tab of your target under Link Binary With Libraries
* In the AppDelegate.m file of your xcode project add: (this will clear all notifications when you open the app)
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
* In the AppDelegate.m file of your xcode project, in the didFinishLaunchingWithOptions function, add: (ask the user to allow notifications)
    ```
    if ([UIApplication instancesRespondToSelector:@selector(registerUserNotificationSettings:)]) {
        [[UIApplication sharedApplication] registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:UIUserNotificationTypeAlert|UIUserNotificationTypeSound categories:nil]];
      }
  ```
* Add Alarm.caf and Silence.caf to the Resources folder of your xcode project. (can be found in react-native-local-notifications/ios/RNLocalNotifications)

#### Android
* In the AndroidManifest.xml file of your android studio project add:
    ```
    <receiver android:process=":remote" android:name="com.github.wumke.RNLocalNotifications.AlarmReceiver" android:exported="true"></receiver>
    ```
* In the MainActivity.java file of your android studio project add: (this will clear all notifications when you open the app)
  ```
  import android.content.Context;
  ...
  @Override
      public void onResume() {
          super.onResume();
          NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          nMgr.cancelAll();
      }
  ```
* {auto-link}In the settings.gradle
  ```
    include ':react-native-local-notifications', ':app'
    project(':react-native-local-notifications').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-local-notifications/android')
  ```
* {auto-link}In the build.gradle
  ```
    compile project(':react-native-local-notifications')
  ```
* {auto-link}In MainApplication.java
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
 * Aside from the big icon, for which the ic_launcher icon in mipmap folder is used by default, you also need a small transparent with white foreground icon which will be displayed in the status bar.
 See Android developer specifications for correct sizes, by default 'notification_small' from the drawable folder is used... 
  
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

See https://www.npmjs.com/package/react-native-local-notifications?activeTab=versions

#### 2.0.0

Breaking changes
- none

New features / Updates
- Changed licence to MIT
- Custom sounds
- Hidden data
- Set Android notification icons type/name
- compileSdkVersion, buildToolsVersion and targetSdkVersion (equal to compileSdkVersion)

Fixes
- Readme delete notification example
- Updated android part to schedule exact notifications based on api lvl for api lvl 19, 21 and 23 (tested up to 26)
- Added com.android.support:support-v4:+ dependency

Todo
- iOS UILocalNotification deprecation
