public interface IEnquirySubmission {

	/**
	 * 
	 * @param message
	 */
	void createEnquiry(String message);

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