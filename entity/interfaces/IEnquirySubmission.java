package entity.interfaces;

import entity.Enquiry;

public interface IEnquirySubmission {
	/**
	 * Creates a new enquiry for a given project with the specified message.
	 *
	 * @param projectID The ID of the BTO project for which the enquiry is made.
	 * @param message   The message or content of the enquiry.
	 * @return The created {@link Enquiry} object.
	 */
	Enquiry createEnquiry(int projectID, String message);

	/**
	 * This function edits the text of a given enquiry object.
	 * 
	 * @param enquiry The `enquiry` parameter is an object of type Enquiry, which
	 *                likely contains
	 *                information about a customer's inquiry or request.
	 * @param newText The `newText` parameter is a String that represents the
	 *                updated text for the
	 *                enquiry. This parameter will be used to update the text of the
	 *                enquiry object.
	 * @return A boolean value is being returned.
	 */
	boolean editEnquiry(Enquiry enquiry, String newText);

	/**
	 * The function `deleteEnquiry` takes an `Enquiry` object as a parameter and
	 * returns a boolean value
	 * indicating whether the deletion was successful.
	 * 
	 * @param enquiry The `deleteEnquiry` method takes an `Enquiry` object as a
	 *                parameter. This method is
	 *                likely used to delete a specific enquiry from a system or
	 *                database. The `Enquiry` object represents
	 *                the enquiry that needs to be deleted.
	 * @return A boolean value is being returned.
	 */
	boolean deleteEnquiry(Enquiry enquiry);

}