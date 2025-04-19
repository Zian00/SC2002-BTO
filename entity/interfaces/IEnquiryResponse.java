package entity.interfaces;

import entity.Enquiry;

public interface IEnquiryResponse {

	/**
	 * This function takes an Enquiry object and a response string as parameters and
	 * returns a boolean
	 * value.
	 * 
	 * @param enquiry  The `enquiry` parameter is an object of type `Enquiry`, which
	 *                 likely contains
	 *                 information about a question or request that has been made.
	 * @param response The `response` parameter is a String variable that represents
	 *                 the response to a
	 *                 specific enquiry.
	 * @return A boolean value is being returned.
	 */
	boolean responseEnquiry(Enquiry enquiry, String response);

}