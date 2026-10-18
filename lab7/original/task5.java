package variant2_userprofilemanager;

// Опис предметної області: Сервіс управління профілями користувачів у веб-платформі.
// Завдання коду: Оновлення даних профілю користувача, скидання оперативної пам'яті кешу, збереження змін у СУБД та надсилання повідомлення про безпеку.

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

    public static class RedisCacheService {
        public void invalidateCache(String key) {
            System.out.println("[Redis Cache] Видалено кеш за ключем: user:cache:" + key);
        }
    }

    public static class PostgresUserRepository {
        public void updateUser(UserProfile profile) {
            System.out.println("[PostgreSQL DB] Оновлено профіль користувача " + profile.getUserId() + " (" + profile.getUsername() + ") у БД.");
        }
    }

    public static class SendGridNotificationClient {
        public void sendSecurityAlert(String email, String message) {
            System.out.println("[SendGrid API] Сповіщення безпеки надіслано на " + email + ": " + message);
        }
    }

    public static class UserProfileManager {
        private RedisCacheService cacheService;
        private PostgresUserRepository userRepository;
        private SendGridNotificationClient notificationClient;

        public UserProfileManager() {
            this.cacheService = new RedisCacheService();
            this.userRepository = new PostgresUserRepository();
            this.notificationClient = new SendGridNotificationClient();
        }

        public void updateProfile(UserProfile profile) {
            if (profile == null || profile.getUserId() == null || profile.getUserId().isEmpty()) {
                throw new IllegalArgumentException("Некоректний профіль користувача.");
            }

            this.userRepository.updateUser(profile);
            this.cacheService.invalidateCache(profile.getUserId());
            this.notificationClient.sendSecurityAlert(profile.getEmail(), "Дані вашого профілю були успішно оновлені.");
        }
    }
}