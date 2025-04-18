package models.interfaces;
import models.Enquiry;

public interface IEnquiryResponse {

	/**
	 * 
	 * @param enquiry
	 * @param response
	 */
	boolean responseEnquiry(Enquiry enquiry, String response);

}