package entity.repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import entity.Receipt;

public class ReceiptCSVRepository {

	private static final String CSV_FILE = "assets/receipt.csv";

	/**
	 * The function `loadReceipts` reads data from a CSV file and populates a list
	 * of Receipt objects with
	 * the parsed information.
	 * 
	 * @return The method `loadReceipts()` returns a List of Receipt objects loaded
	 *         from a CSV file. If
	 *         the file does not exist, an empty list is returned.
	 */
	public List<Receipt> loadReceipts() {
		List<Receipt> receipts = new ArrayList<>();
		File file = new File(CSV_FILE);
		if (!file.exists()) {
			// File doesn't exist yet; return empty list.
			return receipts;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				// Skip header line if present
				if (firstLine) {
					firstLine = false;
					String trimmed = line.trim();
					if (trimmed.startsWith("receiptID")) {
						continue;
					}
				}
				String[] parts = line.split(",");
				if (parts.length < 12) {
					continue; // skip invalid lines
				}
				try {
					Receipt receipt = new Receipt();
					receipt.setReceiptID(Integer.parseInt(parts[0].trim()));
					receipt.setNRIC(parts[1].trim());
					receipt.setApplicantName(parts[2].trim());
					try {
						receipt.setAge(Integer.parseInt(parts[3].trim()));
					} catch (NumberFormatException e) {
						receipt.setAge(0);
					}
					String maritalStr = parts[4].trim();
					if (!maritalStr.isEmpty()) {
						try {
							receipt.setMaritalStatus(entity.enumerations.MaritalState.valueOf(maritalStr));
						} catch (IllegalArgumentException e) {
							receipt.setMaritalStatus(null);
						}
					} else {
						receipt.setMaritalStatus(null);
					}
					receipt.setFlatType(parts[5].trim());
					try {
						receipt.setProjectID(Integer.parseInt(parts[6].trim()));
					} catch (NumberFormatException e) {
						receipt.setProjectID(0);
					}
					receipt.setProjectName(parts[7].trim());
					receipt.setNeighborhood(parts[8].trim());
					receipt.setApplicationOpeningDate(parts[9].trim());
					receipt.setApplicationClosingDate(parts[10].trim());
					receipt.setManager(parts[11].trim());
					receipts.add(receipt);
				} catch (Exception e) {
					System.err.println("Error processing line: " + line + " >> " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading receipt.csv: " + e.getMessage());
		}
		return receipts;

	}

	/**
	 * The `writeReceiptCSV` function writes receipt information to a CSV file,
	 * including a header if the
	 * file is empty or does not exist.
	 * 
	 * @param receipt The `writeReceiptCSV` method you provided is used to write
	 *                receipt information to a
	 *                CSV file. The parameters of the `Receipt` object that are
	 *                being written to the CSV file include:
	 */
	public void writeReceiptCSV(Receipt receipt) {
		File file = new File(CSV_FILE);
		boolean fileExists = file.exists();
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			// If file does not exist or is empty, write header first.
			if (!fileExists || file.length() == 0) {
				pw.println(
						"receiptID,NRIC,applicantName,age,maritalStatus,flatType,projectID,projectName,neighborhood,applicationOpeningDate,applicationClosingDate,manager");
			}
			// Build the row using comma-separated values.
			String newLine = String.join(",",
					String.valueOf(receipt.getReceiptID()),
					receipt.getNRIC(),
					receipt.getApplicantName(),
					String.valueOf(receipt.getAge()),
					(receipt.getMaritalStatus() == null ? "" : receipt.getMaritalStatus().name()),
					receipt.getFlatType(),
					String.valueOf(receipt.getProjectID()),
					receipt.getProjectName(),
					receipt.getNeighborhood(),
					receipt.getApplicationOpeningDate(),
					receipt.getApplicationClosingDate(),
					receipt.getManager());
			pw.println(newLine);
		} catch (IOException e) {
			System.err.println("Error writing to receipt.csv: " + e.getMessage());
		}
	}

	/**
	 * The function getNextReceiptID() returns the next available receipt ID by
	 * loading existing receipts
	 * and incrementing the maximum ID by 1.
	 * 
	 * @return The `getNextReceiptID` method returns the next available receipt ID
	 *         by loading existing
	 *         receipts, extracting the maximum receipt ID, and incrementing it by
	 *         1. If there are no existing
	 *         receipts, it will return 1 as the initial receipt ID.
	 */
	public int getNextReceiptID() {
		List<Receipt> receipts = loadReceipts(); // you must implement loadReceipts() to read and return the receipts
		return receipts.stream()
				.mapToInt(Receipt::getReceiptID)
				.max()
				.orElse(0) + 1;
	}
}