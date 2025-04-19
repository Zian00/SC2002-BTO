package entity.interfaces;
import entity.Enquiry;

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
	 * @return 
	 */
	boolean editEnquiry(Enquiry enquiry, String newText);

	/**
	 * 
	 * @param enquiry
	 * @return 
	 */
	boolean deleteEnquiry(Enquiry enquiry);

}