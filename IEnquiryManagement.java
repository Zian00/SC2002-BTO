

public interface IEnquiryManagement {
    void submitEnquiry(String text);
    void editEnquiry(Enquiry enquiry, String newText);
    void deleteEnquiry(Enquiry enquiry);
}
