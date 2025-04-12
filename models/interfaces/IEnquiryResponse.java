package models.interfaces;
import models.Enquiry;

public interface IEnquiryResponse {

	/**
	 * 
	 * @param enquiry
	 * @param response
	 */
	void responseEnquiry(Enquiry enquiry, String response);

}