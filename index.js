import {NativeModules} from 'react-native';

var RNLocalNotifications = {
  createNotification: function(id, text, datetime,sound) {
        NativeModules.RNLocalNotifications.createNotification(id, text, datetime, sound);
  },
  deleteNotification: function(id) {
        NativeModules.RNLocalNotifications.deleteNotification(id);
  },
  updateNotification: function(id, text, datetime, sound) {
        NativeModules.RNLocalNotifications.updateNotification(id, text, datetime, sound);
  },
};

export default RNLocalNotifications;
