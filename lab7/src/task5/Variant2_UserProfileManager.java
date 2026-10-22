package task5;

public class Variant2_UserProfileManager {

    public static class UserProfile {
        private String userId;
        private String username;
        private String email;
        private String phone;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public interface ICacheService {
        void invalidateCache(String key);
    }

    public interface IUserRepository {
        void updateUser(UserProfile profile);
    }

    public interface INotificationService {
        void sendSecurityAlert(String email, String message);
    }

    public static class RedisCacheService implements ICacheService {
        @Override
        public void invalidateCache(String key) {
            System.out.println("[Redis Cache] Видалено кеш за ключем: user:cache:" + key);
        }
    }

    public static class PostgresUserRepository implements IUserRepository {
        @Override
        public void updateUser(UserProfile profile) {
            System.out.println("[PostgreSQL DB] Оновлено профіль користувача " + profile.getUserId() + " (" + profile.getUsername() + ") у БД.");
        }
    }

    public static class SendGridNotificationClient implements INotificationService {
        @Override
        public void sendSecurityAlert(String email, String message) {
            System.out.println("[SendGrid API] Сповіщення безпеки надіслано на " + email + ": " + message);
        }
    }

    public static class MockUserRepository implements IUserRepository {
        @Override
        public void updateUser(UserProfile profile) {
            System.out.println("[Mock DB] Профіль користувача " + profile.getUserId() + " збережено в пам'яті тесту.");
        }
    }

    public static class UserProfileManager {
        private final ICacheService cacheService;
        private final IUserRepository userRepository;
        private final INotificationService notificationService;

        public UserProfileManager(ICacheService cacheService,
                                  IUserRepository userRepository,
                                  INotificationService notificationService) {
            this.cacheService = cacheService;
            this.userRepository = userRepository;
            this.notificationService = notificationService;
        }

        public void updateProfile(UserProfile profile) {
            if (profile == null || profile.getUserId() == null || profile.getUserId().isEmpty()) {
                throw new IllegalArgumentException("Некоректний профіль користувача.");
            }

            this.userRepository.updateUser(profile);
            this.cacheService.invalidateCache(profile.getUserId());
            this.notificationService.sendSecurityAlert(profile.getEmail(), "Дані вашого профілю були успішно оновлені.");
        }
    }
}