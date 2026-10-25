
public class Guest {
    private final int id;
    private final String fullName;
    private final String phone;
    private final String passportNumber;

    public Guest(int id, String fullName, String phone, String passportNumber) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID гостя має бути додатним.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("ПІБ гостя не може бути порожнім.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Номер телефону не може бути порожнім.");
        }
        if (passportNumber == null || passportNumber.isBlank()) {
            throw new IllegalArgumentException("Номер паспорта не може бути порожнім.");
        }

        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.passportNumber = passportNumber;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getPassportNumber() { return passportNumber; }
}


