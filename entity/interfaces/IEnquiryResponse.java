package entity.interfaces;
import entity.Enquiry;

public interface IEnquiryResponse {

	/**
	 * 
	 * @param enquiry
	 * @param response
	 */
	boolean responseEnquiry(Enquiry enquiry, String response);

}