package com.edilibrary;


import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class Create997 extends EdiSupport {

	// Response to a Load Tender
	public String create997(JSONObject JSONShipment, String functionalGroupAcknowledgeCode) throws JSONException {
		String X12_997 = "";
		Date date = new Date();
		String cDate = ediDate.format(date);
		String cTime = ediTime.format(date);
		String authorizationQualifier = formatElement(
				JSONShipment.getString("ISA01"), 2, 2);
		String authorizationInformation = JSONShipment.getString("ISA02");
		String securityInformationQualifier = formatElement(
				JSONShipment.getString("ISA03"), 2, 2);
		String securityInformation = formatElement(
				JSONShipment.getString("ISA04"), 10, 10);
		String interChangeInformationQualifier = formatElement(
				JSONShipment.getString("ISA05"), 2, 2);
		String interChangeSenderId = formatElement(
				JSONShipment.getString("ISA08"), 15, 15);
		String interChangeIdQualifier = formatElement(
				JSONShipment.getString("ISA07"), 2, 2);
		String interChangeReceiverId = formatElement(
				JSONShipment.getString("ISA06"), 15, 15);
		String interChangeDate = formatElement(shortDate.format(date), 6, 6);
		String interChangeTime = formatElement(cTime, 4, 4);
		String repetitionSeparator = "^";
		String interChangeControlVersionNumber = formatElement(
				JSONShipment.getString("ISA12"), 5, 5);
		String interChangeControlNumber = formatElement(
				JSONShipment.getString("ISA13"), 9, 9);
		String acknowledgementRequested = formatElement(
				JSONShipment.getString("ISA14"), 1, 1);
		String usageIndicator = formatElement(JSONShipment.getString("ISA15"),
				1, 1);
		String componentElementSeparator = formatElement(
				JSONShipment.getString("ISA16"), 1, 1);
		// GS
		String functionalIdentifierCode = formatElement(
				JSONShipment.getString("GS01"), 2, 2);
		String applicationSendersCode = JSONShipment.getString("GS03");
		String applicationReceiversCode = JSONShipment.getString("GS02");
		String groupControlNo = formatElement(JSONShipment.getString("GS06"),
				5, 5);// Note 5 digits
		String responsibleAgencyCode = formatElement(
				JSONShipment.getString("GS07"), 1, 2);
		String industryIdentifierCode = formatElement(
				JSONShipment.getString("GS08"), 1, 2);
		// ST
		String transactionsetControlNo = formatElement(
				JSONShipment.getString("ST02"), 4, 9);
		;
		// AK9
		String functionalGroupSyntaxErrorCode1 = formatElement(null, 0, 0), functionalGroupSyntaxErrorCode2 = formatElement(
				null, 0, 0), functionalGroupSyntaxErrorCode3 = formatElement(
				null, 0, 0), functionalGroupSyntaxErrorCode4 = formatElement(
				null, 0, 0), functionalGroupSyntaxErrorCode5 = formatElement(
				null, 0, 0);
		functionalGroupAcknowledgeCode = formatElement(functionalGroupAcknowledgeCode, 1, 1);
		String numberOfTransactionSetsIncluded = formatElement(
				JSONShipment.getString("SE02"), 1, 6);
		String numberOfReceivedTransactionSets = formatElement(
				JSONShipment.getString("SE02"), 1, 6);
		String numberOfAcceptedTransactionSets = formatElement(
				JSONShipment.getString("SE02"), 1, 6);

		X12_997 += createISA(authorizationQualifier, authorizationInformation,
				securityInformationQualifier, securityInformation,
				interChangeInformationQualifier, interChangeSenderId,
				interChangeIdQualifier, interChangeReceiverId, interChangeDate,
				interChangeTime, repetitionSeparator,
				interChangeControlVersionNumber, interChangeControlNumber,
				acknowledgementRequested, usageIndicator,
				componentElementSeparator);
		X12_997 += createGS(functionalIdentifierCode, applicationSendersCode,
				applicationReceiversCode, cDate, cTime, groupControlNo,
				responsibleAgencyCode, industryIdentifierCode);
		X12_997 += createST(transactionsetControlNo, "997");
		X12_997 += createAK1(functionalIdentifierCode, groupControlNo);
		X12_997 += createAK9(functionalGroupAcknowledgeCode,
				numberOfTransactionSetsIncluded,
				numberOfReceivedTransactionSets,
				numberOfAcceptedTransactionSets,
				functionalGroupSyntaxErrorCode1,
				functionalGroupSyntaxErrorCode2,
				functionalGroupSyntaxErrorCode3,
				functionalGroupSyntaxErrorCode4,
				functionalGroupSyntaxErrorCode5);

		X12_997 += createSE(transactionsetControlNo);
		X12_997 += createGE(transactionsetControlNo);
		X12_997 += createIEA(transactionsetControlNo);

		return X12_997;

	}

}
