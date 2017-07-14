package com.edilibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;

import com.securitylibrary.SecurityLibrary;

public class EdiSupport {
	JSONObject jMainList = new JSONObject();
	JSONArray jS5List = new JSONArray();
	JSONArray jL11List = new JSONArray();
	boolean addtoS5 = false;
	boolean addtoL11 = false;
	JSONObject jS5 = new JSONObject();
	JSONObject jL11 = new JSONObject();

	protected enum N1 {
		TYPE(0), EIC(1), COMPANY(2), ICQ(3), IC(4);
		private int value;

		private N1(int value) {
			this.value = value;
		}

		private static N1 getValue(int index) {

			return N1.values()[index];
		}
	};

	protected enum N3 {
		TYPE(0), ADDRESS1(1), ADDRESS2(2);
		private int value;

		private N3(int value) {
			this.value = value;
		}

		private static N3 getValue(int index) {

			return N3.values()[index];
		}//
	};

	protected enum N4 {
		TYPE(0), CITY(1), STATE(2), POSTALCODE(3), COUNTRYCODE(4), LOCQ(5), LOCI(
				6);
		private int value;

		private N4(int value) {
			this.value = value;
		}

		protected static N4 getValue(int index) {

			return N4.values()[index];
		}//
	};

	public JSONObject parseEdiStringtoJSON(String string) {

		// Scanner Example - read file line by line in Java using Scanner
		// String encryptionDecryptionKey = "4455414744176343";
		// String ivs = "12345678";

		// returnString=SecurityLibrary.encryptString(returnString,
		// encryptionDecryptionKey, ivs);

		// string = SecurityLibrary.decryptString(string,
		// encryptionDecryptionKey, ivs);

		Scanner scanner = new Scanner(string);

		// reading file line by line
		String currentType;
		String priorType = "";

		boolean changed = false;

		boolean addtoL11 = false;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			// Separate each item with *
			String pattern = "\\*";
			if (line.contains("*")) {
				String[] lineArray = line.split(pattern);
				currentType = lineArray[0];
				if (!currentType.contentEquals(priorType)) {
					changed = true;
				}

				if (currentType.contentEquals("L11")) {
					addtoL11 = true;
				} else

				if (currentType.contentEquals("S5")) {
					addtoS5 = true;
				} else

				if (currentType.contentEquals("L3")) {
					addtoS5 = false;
				}

				try {
					buildArray(lineArray, currentType, changed);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				priorType = currentType;
				changed = false;
				addtoL11 = false;
			}
		}
		scanner.close();
		try {
			jMainList.put("STOPS", jS5List);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jMainList;
	}

	// Format each element to proper length
	protected String formatElement(String string, int min, int max) {
		String segment = string;
		// Don't allow nulls
		if (string == null) {
			string = segment = "";
		}
		if (min == 0 && max == 0) {
			// Segment length is = to string with no formatting
		} else
		// if like 6/6 this means always format max length
		if (min == max) {
			segment = String.format("%-" + max + "s", string);
		} else
		// if like 2/6 this means minimum of 2
		if (min < max) {
			segment = String.format("%-" + min + "s", string);
		} else
		// If string is longer than max size then we have an error
		if (string.length() > max) {
			segment = "ERROR";
		}

		return segment;
	}

	public JSONObject readEdiFiletoJSON(String filePath) {
		BufferedReader br = null;
		String returnString = "";
		boolean mFirst = true;
		JSONObject jsonInfo = new JSONObject();

		try {

			String currentLine;

			br = new BufferedReader(new FileReader(filePath));

			while ((currentLine = br.readLine()) != null) {
				if (mFirst) {
					// Separate each item with *
					String pattern = "\\*";
					if (currentLine.contains("*")) {
						String[] lineArray = currentLine.split(pattern);
						// The first line contains the isa info which is
						// appended to the jsonInfo object
						parseLineArray(lineArray, jsonInfo);
					}
					mFirst = false;
				}
				returnString += currentLine + "\n";
			}
			if (br != null)
				br.close();
			String encryptionDecryptionKey = "4455414744176343";
			String ivs = "12345678";

			returnString = SecurityLibrary.encryptString(returnString,
					encryptionDecryptionKey, ivs);

			// String testmessage = SecurityLibrary.decryptString(returnString,
			// encryptionDecryptionKey, ivs);

			jsonInfo.put("message", returnString);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonInfo;

	}

	public String readEdiFile(String filePath) {
		BufferedReader br = null;
		String returnString = "";
		try {

			String currentLine;

			br = new BufferedReader(new FileReader(filePath));

			while ((currentLine = br.readLine()) != null) {
				returnString += currentLine + "\n";
			}
			if (br != null)
				br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnString;

	}

	public String openEdiFiletoString(String string) throws IOException {
		FileReader fr = new FileReader(string);
		BufferedReader br = new BufferedReader(fr);
		String returnString = "";
		Scanner scanner = new Scanner(br);
		while (scanner.hasNextLine()) {
			returnString += scanner.nextLine() + "\n";
		}
		scanner.close();
		if (br != null)
			br.close();
		if (fr != null)
			fr.close();
		return returnString;
	}

	protected JSONObject parseEdiFiletoArrayList(String string)
			throws FileNotFoundException {
		jMainList = new JSONObject();
		jS5List = new JSONArray();
		jL11List = new JSONArray();
		addtoS5 = false;
		addtoL11 = false;
		jS5 = new JSONObject();
		jL11 = new JSONObject();

		// Scanner Example - read file line by line in Java using Scanner
		FileInputStream fis = new FileInputStream(string);
		Scanner scanner = new Scanner(fis);

		// reading file line by line
		String currentType;
		String priorType = "";

		boolean changed = false;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			// Separate each item with *
			String pattern = "\\*";
			if (line.contains("*")) {
				String[] lineArray = line.split(pattern);
				currentType = lineArray[0];
				if (!currentType.contentEquals(priorType)) {
					changed = true;
				}

				if (currentType.contentEquals("L11")) {
					addtoL11 = true;
				} else

				if (currentType.contentEquals("S5")) {
					addtoS5 = true;
				} else

				if (currentType.contentEquals("L3")) {
					addtoS5 = false;
				}

				try {
					buildArray(lineArray, currentType, changed);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				priorType = currentType;
				changed = false;
				addtoL11 = false;
			}
		}
		scanner.close();
		try {
			jMainList.put("STOPS", jS5List);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// jMainList.add(jS5);

		return jMainList;
	}

	private void buildSubArray(String[] lineitem, String type, boolean newLoop)
			throws JSONException {
		if (newLoop) {
			jL11List = new JSONArray();
		}
		jL11 = new JSONObject();
		jL11 = parseLineArray(lineitem, jL11);
		jL11List.put(jL11);
	}

	private void buildArray(String[] lineitem, String type, boolean newLoop)
			throws JSONException {
		if (type.contentEquals("L11")) {
			buildSubArray(lineitem, type, newLoop);
			return;
		}

		if (type.contentEquals("S5")) {
			if (newLoop) {
				if (jS5.length() > 0) {
					if (jS5.length() > 0) {
					}
					// Check to see if jS5List is valid
					jS5List.put(jS5);
					jS5 = new JSONObject();
				}
			}
			jS5 = parseLineArray(lineitem, jS5);

		} else {
			if (addtoS5) {
				jS5 = parseLineArray(lineitem, jS5);
				jS5.put("DESC", jL11List);
			} else {
				if (type.contentEquals("L3")) {
					// This adds the last stop
					if (jS5.length() > 0) {
						jS5List.put(jS5);
						jS5 = new JSONObject();
					}
					// Add all Stops
					jS5.put("STOPS", jS5List);
				}

				// Adds All other objects to the main array
				// jMainList.add(parseLinetoJson(lineitem));
				jMainList = parseLineArray(lineitem, jMainList);
			}
		}

	}

	// Add all items in line to the main Object i.e. N01.....
	private JSONObject parseLineArray(String linearray[], JSONObject jObject) {

		// for each m create jsonobject like {1:"whatever",2:whatever2}
		for (int i = 0; i < linearray.length; i++) {
			try {
				jObject.put(linearray[0].toString() + String.format("%02d", i),
						linearray[i]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return jObject;
	}

	// Used to convert status code into readable string. These should be stored
	// in strings.xml table for internationalization
	public String getDeliveryStatus(String deliveryCode) {
		String description = "Delivery Status Unavailable";
		if (deliveryCode.contentEquals("02")) {
			description = "Delivery Requested on This Date";
		} else if (deliveryCode.contentEquals("10")) {
			description = "Requested Ship Date/Pick-up Date";
		} else if (deliveryCode.contentEquals("18")) {
			description = "Date Available [for Pick-up]";
		} else if (deliveryCode.contentEquals("37")) {
			description = "Ship Not Before Date";
		} else if (deliveryCode.contentEquals("38")) {
			description = "Ship Not Later Than Date";
		} else if (deliveryCode.contentEquals("53")) {
			description = "Deliver Not Before Date";
		} else if (deliveryCode.contentEquals("54")) {
			description = "Deliver No Later Than Date";
		} else if (deliveryCode.contentEquals("68")) {
			description = "Requested Delivery Date";
		} else if (deliveryCode.contentEquals("69")) {
			description = "Scheduled Pick-Up Date ";
		} else if (deliveryCode.contentEquals("70")) {
			description = "Scheduled Delivery Date";
		} else if (deliveryCode.contentEquals("79")) {
			description = "Pickup Requested Scheduled Date";
		} else if (deliveryCode.contentEquals("80")) {
			description = "Delivery Requested Scheduled Date";
		}

		return description;

	}

	private String separator = "*";
	private String eol = "\n";
	protected SimpleDateFormat ediDate = new SimpleDateFormat("yyyyMMdd",
			Locale.getDefault());
	protected SimpleDateFormat shortDate = new SimpleDateFormat("yyMMdd",
			Locale.getDefault());

	protected SimpleDateFormat ediTime = new SimpleDateFormat("HHmm",
			Locale.getDefault());
	private int lineCnt = 0;

	// Interchange Control Header
	protected String createISA(String authorizationQualifier,
			String authorizationInformation,
			String securityInformationQualifier, String securityInformation,
			String interChangeInformationQualifier, String interChangeSenderId,
			String interChangeIdQualifier, String interChangeReceiverId,
			String interChangeDate, String interChangeTime,
			String repetitionSeparator, String interChangeControlVersionNumber,
			String interChangeControlNumber, String acknowledgementRequested,
			String usageIndicator, String componentElementSeparator) {
		String ISA = "ISA" + separator + authorizationQualifier + separator
				+ authorizationInformation + separator
				+ securityInformationQualifier + separator
				+ securityInformation + separator
				+ interChangeInformationQualifier + separator
				+ interChangeSenderId + separator + interChangeIdQualifier
				+ separator + interChangeReceiverId + separator
				+ interChangeDate + separator + interChangeTime + separator
				+ repetitionSeparator + separator
				+ interChangeControlVersionNumber + separator
				+ interChangeControlNumber + separator
				+ acknowledgementRequested + separator + usageIndicator
				+ separator + componentElementSeparator + eol;
		return ISA;

	}

	// Functional Group Header
	protected String createGS(String functionalIdentifierCode,
			String applicationSendersCode, String applicationReceiversCode,
			String date, String time, String groupControlNo,
			String responsibleAgencyCode, String industryIdentifierCode) {
		String GS = "GS" + separator + functionalIdentifierCode + separator
				+ applicationSendersCode + separator + applicationReceiversCode
				+ separator + date + separator + time + separator
				+ groupControlNo + separator + responsibleAgencyCode
				+ separator + industryIdentifierCode + eol;
		return GS;

	}

	// Start Transaction
	protected String createST(String transactionsetControlNo,
			String transactionSetIdentifierCode) {
		String ST = "ST" + separator + transactionSetIdentifierCode + separator
				+ transactionsetControlNo + eol;
		lineCnt++;
		return ST;

	}

	// Beginning Segment for Booking or Pick-up/Delivery
	protected String createB1(String standardCarrierAlphaCode,
			String shipmentIdentificationNumber, String shipmentStatus) {

		Date date = new Date();
		String cDate = ediDate.format(date);
		String B1 = "B1" + separator + standardCarrierAlphaCode + separator
				+ shipmentIdentificationNumber + separator + cDate + separator
				+ shipmentStatus + eol;
		lineCnt++;
		return B1;

	}

	// Beginning Segment for Carrier�s Invoice
	protected String createB3(String shipmentQualifier, String invoiceNumber,
			String shipmentIdentificationNumber,
			String shipmentMethodofPayment, String weightUnitQualifier,
			String billingDate, String netAmountDue, String deliveryDate,
			String dateTimeQualifier, String standardCarrierAlphaCode,
			String pickpuDate) {

		// Date date = new Date();
		// String cDate = ediDate.format(date);
		String B3 = "B3" + separator + shipmentQualifier + separator
				+ invoiceNumber + separator + shipmentIdentificationNumber
				+ separator + shipmentMethodofPayment + separator
				+ weightUnitQualifier + separator + billingDate + separator
				+ netAmountDue + separator + deliveryDate + separator
				+ dateTimeQualifier + separator + standardCarrierAlphaCode
				+ separator + pickpuDate + eol;
		lineCnt++;
		return B3;

	}

	// Bank ID
	protected String createC2(String bankClientCode,
			String identificationCodeQualifier, String identificationCode) {
		String C2 = "C2" + separator + bankClientCode + separator
				+ identificationCodeQualifier + separator + identificationCode
				+ eol;
		lineCnt++;
		return C2;

	}

	// Currency
	protected String createC3(String currencyCode, String exchangeRate,
			String alternativeCurrencyCode) {
		String C3 = "C3" + separator + currencyCode + separator + exchangeRate
				+ separator + alternativeCurrencyCode + eol;
		lineCnt++;
		return C3;

	}

	// Route Information
	protected String createR3(String standardCarrierAlphaCode,
			String routingSequenceCode, String cityName,
			String transportationMethodCode) {
		String R3 = "R3" + separator + routingSequenceCode + separator
				+ cityName + separator + transportationMethodCode + eol;
		lineCnt++;
		return R3;
	}

	// Assigned Number
	protected String createLX(String assignedNumber) {
		String LX = "LX" + separator + assignedNumber + eol;
		lineCnt++;
		return LX;
	}

	// Description, Marks and Numbers
	protected String createL5(String ladingLineItemNumber,
			String ladingDescription, String codeDescribingACommodity,
			String codeIdentifyingTheCommodity) {
		String L5 = "L5" + separator + ladingLineItemNumber + separator
				+ ladingDescription + separator + codeDescribingACommodity
				+ separator + codeIdentifyingTheCommodity + eol;
		lineCnt++;
		return L5;
	}

	// Line Item � Quantity and Weight
	protected String createL0(String ladingLineItemNumber,
			String ratedAsQuantity, String ratedAsQualifier, String weight,
			String weightQualifier, String volume, String volumeUnitQualifier,
			String ladingQuantity, String packagingFormCode,
			String dunnageDescription, String weightUnitCode) {
		String L0 = "L0" + separator + ladingLineItemNumber + separator
				+ ratedAsQuantity + separator + ratedAsQualifier + separator
				+ weight + separator + weightQualifier + separator + volume
				+ separator + volumeUnitQualifier + separator + ladingQuantity
				+ separator + packagingFormCode + separator
				+ dunnageDescription + separator + weightUnitCode + eol;
		lineCnt++;
		return L0;
	}

	// - Rate and Charges
	protected String createL1(String ladingLineItemNumber, String freightRate,
			String rateValueQualifier, String charge, String advances,
			String prepaidAmount, String rateCombinationPointCode,
			String specialCharge) {
		String L1 = "L1" + separator + ladingLineItemNumber + separator
				+ freightRate + separator + rateValueQualifier + separator
				+ charge + separator + advances + separator + prepaidAmount
				+ separator + rateCombinationPointCode + separator
				+ specialCharge + eol;
		lineCnt++;
		return L1;
	}

	// - Tariff Reference
	protected String createL7(String ladingLineItemNumber,
			String tariffAgencyCode, String tariffNumber, String tariffSection,
			String tariffItemNumber, String tariffItemPart,
			String freightClassCode) {
		String L7 = "L7" + separator + ladingLineItemNumber + separator
				+ tariffAgencyCode + separator + tariffItemNumber + separator
				+ tariffSection + separator + tariffItemNumber + separator
				+ tariffItemPart + separator + freightClassCode + eol;
		lineCnt++;
		return L7;
	}

	// - Total Weight and Charges -
	protected String createL3(String weight, String weightQualifier,
			String freightRate, String rateValueQualifier, String charge,
			String advances, String prepaidAmount,
			String specialChargeorAllowanceCode, String volume,
			String volumeUnitQualifier, String ladingQuantity) {
		String L3 = "L3" + separator + weight + separator + weightQualifier
				+ separator + freightRate + separator + rateValueQualifier
				+ separator + charge + separator + advances + separator
				+ prepaidAmount + separator + specialChargeorAllowanceCode
				+ separator + volume + separator + volumeUnitQualifier
				+ separator + ladingQuantity + eol;
		lineCnt++;
		return L3;
	}

	// Reference Identification
	protected String createN9(String shipmentIdentificationNumber) {
		String N9 = "N9" + separator + "CN" + separator
				+ shipmentIdentificationNumber + eol;
		lineCnt++;
		return N9;

	}

	// Date/Time - Not implemented yet
	protected String createG62(String shipmentIdentificationNumber) {
		String G62 = "G62" + separator + "CN" + separator
				+ shipmentIdentificationNumber + eol;
		lineCnt++;
		return G62;

	}

	// Equipment Details
	protected String createN7(String equipmentNumber, String equipmentCode,
			String equipmentLength, String equipmentType) {
		String N7 = "N7" + separator + "1" + separator + equipmentNumber
				+ separator + "3" + separator + "4" + separator + "5"
				+ separator + "6" + separator + "7" + separator + "EIG"
				+ separator + "9" + separator + "10" + separator
				+ equipmentCode + separator + "12" + separator + "13"
				+ separator + "14" + separator + equipmentLength + separator
				+ "16" + separator + "17" + separator + "1E" + separator + "19"
				+ separator + "20" + separator + "21" + separator
				+ equipmentType + eol;
		lineCnt++;
		return N7;
	}

	// Remarks
	protected String createK1(String driverName, String driverNo) {
		String K1 = "K1" + separator + driverName + separator + driverNo + eol;
		lineCnt++;
		return K1;

	}

	// End Transaction
	protected String createSE(String transactionsetControlNo) {
		lineCnt++;
		String SE = "SE" + separator + transactionsetControlNo + separator
				+ lineCnt + eol;
		return SE;

	}

	// Functional Group Trailer
	protected String createGE(String transactionsetControlNo) {
		int transactionCnt = 1;
		String GE = "GE" + separator + transactionCnt + separator
				+ transactionsetControlNo + eol;
		;
		return GE;

	}

	// Interchange Control Trailer
	protected String createIEA(String transactionsetControlNo) {
		int groupCnt = 1;
		String IEA = "IEA" + separator + groupCnt + separator
				+ transactionsetControlNo + eol;
		;
		return IEA;

	}

	// Functional Group Response Header
	protected String createAK1(String functionalIdentifierCode,
			String groupControlNo) {
		String AK1 = "AK1" + separator + functionalIdentifierCode + separator
				+ groupControlNo + eol;
		lineCnt++;
		return AK1;
	}

	// Transaction Set Response Header
	protected String createAK2(String transactionsetIdentifierCode,
			String transactionsetControlNo) {
		String AK2 = "AK2" + separator + transactionsetIdentifierCode
				+ separator + transactionsetControlNo + eol;
		lineCnt++;
		return AK2;
	}

	// Data Segment Note
	protected String createAK3(String segmentIdCode,
			String segmentPositionInTransactionSet, String loopIdentifierCode,
			String segmentSyntaxErrorCode) {
		String AK3 = "AK3" + separator + segmentIdCode + separator
				+ segmentPositionInTransactionSet + separator
				+ loopIdentifierCode + separator + segmentSyntaxErrorCode + eol;
		lineCnt++;
		return AK3;
	}

	// Data Element Note
	protected String createAK4(String positionInSegment,
			String elementPositionInSegment, String dataElementReferenceNumber,
			String dataElementSyntaxErrorCode, String copyOfBadDataElement) {
		String AK4 = "AK4" + separator + positionInSegment + separator
				+ elementPositionInSegment + separator
				+ dataElementReferenceNumber + separator
				+ dataElementSyntaxErrorCode + separator + copyOfBadDataElement
				+ eol;
		lineCnt++;
		return AK4;
	}

	// Transaction Set Response Trailer
	protected String createAK5(String transactionSetAcknowledgmentCode,
			String transactionSetSyntaxErrorCode1,
			String transactionSetSyntaxErrorCode2,
			String transactionSetSyntaxErrorCode3,
			String transactionSetSyntaxErrorCode4,
			String transactionSetSyntaxErrorCode5) {
		String AK5 = "AK5" + separator + transactionSetAcknowledgmentCode
				+ separator + transactionSetSyntaxErrorCode1 + separator
				+ transactionSetSyntaxErrorCode2 + separator
				+ transactionSetSyntaxErrorCode3 + separator
				+ transactionSetSyntaxErrorCode4 + separator
				+ transactionSetSyntaxErrorCode5 + eol;
		lineCnt++;
		return AK5;
	}

	// Functional Group Response Trailer
	protected String createAK9(String functionalGroupAcknowledgeCode,
			String numberOfTransactionSetsIncluded,
			String numberOfReceivedTransactionSets,
			String numberOfAcceptedTransactionSets,
			String functionalGroupSyntaxErrorCode1,
			String functionalGroupSyntaxErrorCode2,
			String functionalGroupSyntaxErrorCode3,
			String functionalGroupSyntaxErrorCode4,
			String functionalGroupSyntaxErrorCode5) {
		String AK9 = "AK9" + separator + functionalGroupAcknowledgeCode
				+ separator + numberOfTransactionSetsIncluded + separator
				+ numberOfReceivedTransactionSets + separator
				+ numberOfAcceptedTransactionSets + separator
				+ functionalGroupSyntaxErrorCode1 + separator
				+ functionalGroupSyntaxErrorCode2 + separator
				+ functionalGroupSyntaxErrorCode3 + separator
				+ functionalGroupSyntaxErrorCode4 + separator
				+ functionalGroupSyntaxErrorCode5 + eol;
		lineCnt++;
		return AK9;
	}

	// this is for testing only
	public String[] createTest(String aFilename) {
		File folder = new File(Environment.getExternalStorageDirectory()
				+ "/artytheartist");
		// String aFilename = "204data";
		// aFilename = "204SPMD";
		// aFilename = "204SPSD";
		// aFilename = "204MPSD";
		// aFilename = "204LTL";
		File filename = new File(folder + "/" + aFilename + ".txt");
		// FastDateFormat newFormat =
		// FastDateFormat.getInstance("MMM dd, yyyy",Locale.getDefault());
		SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy",
				Locale.getDefault());

		Date cDate;
		String mTextString[] = { "", "", "", "", "" };
		// String mReply="";

		try {
			mTextString[0] = openEdiFiletoString(filename.toString());

			JSONObject edifile = parseEdiFiletoArrayList(filename.toString());
			Create990 create990 = new Create990();

			mTextString[1] = create990.create990(edifile, "A", "TESTMESSAGE",
					"Rick", "0012", "1403", "TL", "48", "MXMX");
			// Create FA
			Create997 create997 = new Create997();
			mTextString[2] = create997.create997(edifile, "A");

			Create210 create210 = new Create210();
			mTextString[3] = create210.create210(edifile, "A", "TESTMESSAGE",
					"Rick", "0012", "1403", "TL", "48", "MXMX");

			JSONArray jArrayStops = edifile.getJSONArray("STOPS");

			for (int i = 0; i < jArrayStops.length(); i++) {
				JSONObject jObj = jArrayStops.getJSONObject(i);
				cDate = ediDate.parse(jObj.optString("G6202"));

				mTextString[4] += Integer.toString(i + 1) + ": "
						+ jObj.getString("N102") + " " + jObj.getString("N301")
						+ " " + jObj.getString("N401") + " "
						+ jObj.getString("N402") + " " + jObj.getString("N403")
						+ "\n" + getDeliveryStatus(jObj.optString("G6201"))
						+ " " + newFormat.format(cDate) + " "
						+ jObj.optString("G6204") + " "
						+ jObj.optString("G6205") + "\n";
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mTextString;

	}

}
