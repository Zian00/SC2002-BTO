package controllers;
import java.util.List;
import models.Enquiry;
import models.User;
import models.interfaces.*;

public class EnquiryCTRL implements IEnquiryResponse, IEnquirySubmission {

	private List<Enquiry> enquiries;

	/**
	 * 
	 * @param currentUser
	 */
	public EnquiryCTRL(User currentUser) {
		// TODO - implement EnquiryCTRL.EnquiryCTRL
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param projName
	 */
	public void displayEnquiry(String projName) {
		// TODO - implement EnquiryCTRL.displayEnquiry
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param message
	 */
	public void createEnquiry(String message) {
		// TODO - implement EnquiryCTRL.createEnquiry
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param enquiry
	 * @param newText
	 */
	public void editEnquiry(Enquiry enquiry, String newText) {
		// TODO - implement EnquiryCTRL.editEnquiry
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param enquiry
	 */
	public void deleteEnquiry(Enquiry enquiry) {
		// TODO - implement EnquiryCTRL.deleteEnquiry
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param enquiry
	 * @param response
	 */
	public void responseEnquiry(Enquiry enquiry, String response) {
		// TODO - implement EnquiryCTRL.responseEnquiry
		throw new UnsupportedOperationException();
	}

}