package com.edilibrary;


	import java.util.Date;
	import org.json.JSONException;
	import org.json.JSONObject;


	public class Create204 extends EdiSupport {

		// Response to a Load Tender
		public String create204(JSONObject JSONShipment, String shipmentStatus,
				String message, String driverName, String driverNo,
				String equipmentNumber, String equipmentCode,
				String equipmentLength, String equipmentType) throws JSONException {
			String X12_204 = "";
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
			String responsibleAgencyCode = JSONShipment.getString("GS07");
			String industryIdentifierCode = JSONShipment.getString("GS08");
			// ST

			String transactionsetControlNo = JSONShipment.getString("ST02");
			// B1
			String standardCarrierAlphaCode = JSONShipment.getString("B202");
			String shipmentId = JSONShipment.getString("B204");

			// String shipmentDate=JSONShipment.getString("B203");
			X12_204 += createISA(authorizationQualifier, authorizationInformation,
					securityInformationQualifier, securityInformation,
					interChangeInformationQualifier, interChangeSenderId,
					interChangeIdQualifier, interChangeReceiverId, interChangeDate,
					interChangeTime, repetitionSeparator,
					interChangeControlVersionNumber, interChangeControlNumber,
					acknowledgementRequested, usageIndicator,
					componentElementSeparator);
			X12_204 += createGS(functionalIdentifierCode, applicationSendersCode,
					applicationReceiversCode, cDate, cTime, groupControlNo,
					responsibleAgencyCode, industryIdentifierCode);
			X12_204 += createST(transactionsetControlNo, "990");
			X12_204 += createB1(standardCarrierAlphaCode, shipmentId,
					shipmentStatus);
			X12_204 += createN9(shipmentId);
			// X12_204 += createG62(shipmentId);
			X12_204 += createN7(equipmentNumber, equipmentCode, equipmentLength,
					equipmentType);
			X12_204 += createK1(driverName, driverNo);

			X12_204 += createSE(transactionsetControlNo);
			X12_204 += createGE(transactionsetControlNo);
			X12_204 += createIEA(transactionsetControlNo);

			return X12_204;

		}

	}
