# react-native-local-notifications
Manageable local notifications for React Native on iOS and Android. Create, update and delete local notifications by their unique id. The push notification title is the app name. When you open the app all displayed local notifications will be removed and the badge counter will be reset on iOS. 

NOTICE:
- for React Native < 0.47 use react-native-immediate-phone-call <1.x.x
- for React Native > 0.47 use react-native-immediate-phone-call >=1.x.x

## Setup

Fast and easy:
```bash
npm install react-native-local-notifications --save
react-native react-native-local-notifications
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

```javascript
import RNLocalNotifications from 'react-native-local-notifications';
...
//RNLocalNotifications.createNotification(id, text, datetime, sound);
RNLocalNotifications.createNotification(1, 'Some text', '2017-01-02 12:30', 'default');

//RNLocalNotifications.updateNotification(id, text, datetime, sound);
RNLocalNotifications.updateNotification(1, 'Some modifications to text', '2017-01-02 12:35', 'silence');

//RNLocalNotifications.createNotification(id);
RNLocalNotifications.createNotification(1);
...
```
####Parameter explanation:
* id (Integer): Unique value to be able to edit or cancel scheduled notifications.
* text (String): The message text.
* datetime (String): The date + time to show the notification, as a string in the format 'yyyy-mm-dd hh:mm'.
* sound (String): Which sound is played: '' or 'silence' for vibration only, any other value for default alarm sound. (Future releases: your own custom sounds, name-based!)

## Versioning

This project uses semantic versioning: MAJOR.MINOR.PATCH.
This means that releases within the same MAJOR version are always backwards compatible. For more info see [semver.org](http://semver.org/).