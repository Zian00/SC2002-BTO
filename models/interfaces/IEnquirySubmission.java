package models.interfaces;
import models.Enquiry;

public interface IEnquirySubmission {

	/**
	 * 
	 * @param message
	 */
	Enquiry createEnquiry(int projectID, String message);

	/**
	 * 
	 * @param enquiry
	 * @param newText
	 */
	void editEnquiry(Enquiry enquiry, String newText);

	/**
	 * 
	 * @param enquiry
	 */
	void deleteEnquiry(Enquiry enquiry);

}