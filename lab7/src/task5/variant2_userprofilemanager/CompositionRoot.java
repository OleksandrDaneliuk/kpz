package variant2_userprofilemanager;

public class CompositionRoot {
    public static void main(String[] args) {
        Variant2_UserProfileManager.UserProfile profile = new Variant2_UserProfileManager.UserProfile();
        profile.setUserId("USR-1092");
        profile.setUsername("ivan_u");
        profile.setEmail("ivan@example.com");

        Variant2_UserProfileManager.ICacheService cacheService = new Variant2_UserProfileManager.RedisCacheService();
        Variant2_UserProfileManager.INotificationService notificationService = new Variant2_UserProfileManager.SendGridNotificationClient();

        Variant2_UserProfileManager.IUserRepository prodRepo = new Variant2_UserProfileManager.PostgresUserRepository();
        Variant2_UserProfileManager.UserProfileManager prodManager =
                new Variant2_UserProfileManager.UserProfileManager(cacheService, prodRepo, notificationService);
        prodManager.updateProfile(profile);

        Variant2_UserProfileManager.IUserRepository mockRepo = new Variant2_UserProfileManager.MockUserRepository();
        Variant2_UserProfileManager.UserProfileManager testManager =
                new Variant2_UserProfileManager.UserProfileManager(cacheService, mockRepo, notificationService);
        testManager.updateProfile(profile);
    }
}