#import <UIKit/UIKit.h>
#import "RCTBridgeModule.h"


@interface RNLocalNotifications : NSObject <RCTBridgeModule>
@end

@implementation RNLocalNotifications

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(createNotification:(NSInteger)id text:(NSString *)text datetime:(NSString *)datetime sound:(NSString *)sound hiddendata:(NSString *)hiddendata)
{
    [self createAlarm:id text:text datetime:datetime sound:sound update:FALSE hiddendata:(NSString *)hiddendata];
};

RCT_EXPORT_METHOD(deleteNotification:(NSInteger)id)
{
    [self deleteAlarm:id];
};

RCT_EXPORT_METHOD(updateNotification:(NSInteger)id text:(NSString *)text datetime:(NSString *)datetime sound:(NSString *)sound hiddendata:(NSString *)hiddendata)
{
    [self createAlarm:id text:text datetime:datetime sound:sound update:TRUE hiddendata:(NSString *)hiddendata];
};

RCT_EXPORT_METHOD(setAndroidIcons:(NSString *)largeIconName largeIconType:(NSString *)largeIconType smallIconName:(NSString *)smallIconName smallIconType:(NSString *)smallIconType)
{
    //Do nothing
};

- (void)createAlarm:(NSInteger)id text:(NSString *)text datetime:(NSString *)datetime sound:(NSString *)sound update:(Boolean)update hiddendata:(NSString *)hiddendata {
    if(update){
        [self deleteAlarm:id];
    }
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    [dateFormat setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSDate *fireDate = [dateFormat dateFromString:datetime];
    if ([[NSDate date]compare: fireDate] == NSOrderedAscending) { //only schedule items in the future!
        UILocalNotification *notification = [[UILocalNotification alloc] init];
        notification.fireDate = fireDate;
        if([sound isEqualToString:@"default"] && ![sound isEqualToString:@"silence"]){
            notification.soundName = UILocalNotificationDefaultSoundName;
        }
        else if([sound isEqualToString:@"silence"]){
            notification.soundName = @"silence.caf";
        }
        else {
            notification.soundName = [NSString stringWithFormat:@"%@.caf", sound];
        }
        notification.timeZone = [NSTimeZone defaultTimeZone];
        notification.alertBody = text;
        notification.alertAction = @"Open";
        NSMutableDictionary *md = [[NSMutableDictionary alloc] init];
        [md setValue:[NSNumber numberWithInteger:id] forKey:@"id"];
        [md setValue:text forKey:@"text"];
        [md setValue:datetime forKey:@"datetime"];
        [md setValue:sound forKey:@"sound"];
        [md setValue:hiddendata forKey:@"hiddendata"];
        notification.userInfo = md;
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication] scheduleLocalNotification:notification];
        });
    }
}

- (void)deleteAlarm:(NSInteger)id {
    for (UILocalNotification * notification in [[UIApplication sharedApplication] scheduledLocalNotifications]) {
        NSMutableDictionary *md = (NSMutableDictionary*)[notification userInfo];
        if ([[md valueForKey:@"id"] integerValue] == [[NSNumber numberWithInteger:id] integerValue]) {
            [[UIApplication sharedApplication] cancelLocalNotification:notification];
        }
    }
}

@end
