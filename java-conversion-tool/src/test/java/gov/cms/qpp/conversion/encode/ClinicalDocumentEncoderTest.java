package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gov.cms.qpp.conversion.io.ByteCounterOutputStream;
import gov.cms.qpp.conversion.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ClinicalDocumentEncoderTest {

	private static final String EXPECTED_CLINICAL_DOC_FORMAT = "{\n  \"programName\" : \"mips\"," + "\n  \"entityType\" : \"individual\","
			+ "\n  \"taxpayerIdentificationNumber\" : \"123456789\","
			+ "\n  \"nationalProviderIdentifier\" : \"2567891421\"," + "\n  \"performanceYear\" : 2017,"
			+ "\n  \"measurementSets\" : [ " + "{\n    \"category\" : \"aci\",\n    \"measurements\" : [ "
			+ "{\n      \"measureId\" : \"ACI-PEA-1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_EP_1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 500,\n        \"denominator\" : 700\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_CCTPE_3\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    } ],"
			+ "\n    \"source\" : \"provider\"," + "\n    \"performanceStart\" : \"2017-01-01\","
			+ "\n    \"performanceEnd\" : \"2017-12-31\"" + "\n  } ]\n}";
	
	private static final String EXPECTED_NO_REPORTING = "{\n  \"programName\" : \"mips\"," + "\n  \"entityType\" : \"individual\","
			+ "\n  \"taxpayerIdentificationNumber\" : \"123456789\","
			+ "\n  \"nationalProviderIdentifier\" : \"2567891421\","
			+ "\n  \"measurementSets\" : [ " + "{\n    \"category\" : \"aci\",\n    \"measurements\" : [ "
			+ "{\n      \"measureId\" : \"ACI-PEA-1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_EP_1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 500,\n        \"denominator\" : 700\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_CCTPE_3\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    } ],"
			+ "\n    \"source\" : \"provider\"\n  } ]\n}";

	private static final String EXPECTED_NO_ACI = "{\n  \"programName\" : \"mips\"," + "\n  \"entityType\" : \"individual\","
			+ "\n  \"taxpayerIdentificationNumber\" : \"123456789\","
			+ "\n  \"nationalProviderIdentifier\" : \"2567891421\"," + "\n  \"performanceYear\" : 2017\n}";

	private Node aciSectionNode;
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private Node aciProportionMeasureNode2;
	private Node aciProportionNumeratorNode2;
	private Node aciProportionDenominatorNode2;
	private Node numeratorValueNode2;
	private Node denominatorValueNode2;
	private Node aciProportionMeasureNode3;
	private Node aciProportionNumeratorNode3;
	private Node aciProportionDenominatorNode3;
	private Node numeratorValueNode3;
	private Node denominatorValueNode3;
	private Node reportingParametersActNode;
	private Node reportingParametersSectionNode;
	private Node clinicalDocumentNode;
	private List<Node> nodes;

	@Before
	public void createNode() {

		numeratorValueNode = new Node();
		numeratorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorValueNode.putValue("aggregateCount", "400");

		numeratorValueNode2 = new Node();
		numeratorValueNode2.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorValueNode2.putValue("aggregateCount", "500");

		numeratorValueNode3 = new Node();
		numeratorValueNode3.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorValueNode3.putValue("aggregateCount", "400");

		denominatorValueNode = new Node();
		denominatorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		denominatorValueNode.putValue("aggregateCount", "600");

		denominatorValueNode2 = new Node();
		denominatorValueNode2.setId("2.16.840.1.113883.10.20.27.3.3");
		denominatorValueNode2.putValue("aggregateCount", "700");

		denominatorValueNode3 = new Node();
		denominatorValueNode3.setId("2.16.840.1.113883.10.20.27.3.3");
		denominatorValueNode3.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setId("2.16.840.1.113883.10.20.27.3.32");
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionDenominatorNode2 = new Node();
		aciProportionDenominatorNode2.setId("2.16.840.1.113883.10.20.27.3.32");
		aciProportionDenominatorNode2.addChildNode(denominatorValueNode2);

		aciProportionDenominatorNode3 = new Node();
		aciProportionDenominatorNode3.setId("2.16.840.1.113883.10.20.27.3.32");
		aciProportionDenominatorNode3.addChildNode(denominatorValueNode3);

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionNumeratorNode2 = new Node();
		aciProportionNumeratorNode2.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode2.addChildNode(numeratorValueNode2);

		aciProportionNumeratorNode3 = new Node();
		aciProportionNumeratorNode3.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode3.addChildNode(numeratorValueNode3);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciProportionMeasureNode2 = new Node();
		aciProportionMeasureNode2.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode2.addChildNode(aciProportionNumeratorNode2);
		aciProportionMeasureNode2.addChildNode(aciProportionDenominatorNode2);
		aciProportionMeasureNode2.putValue("measureId", "ACI_EP_1");

		aciProportionMeasureNode3 = new Node();
		aciProportionMeasureNode3.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode3.addChildNode(aciProportionNumeratorNode3);
		aciProportionMeasureNode3.addChildNode(aciProportionDenominatorNode3);
		aciProportionMeasureNode3.putValue("measureId", "ACI_CCTPE_3");

		aciSectionNode = new Node();
		aciSectionNode.setId("2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);
		aciSectionNode.addChildNode(aciProportionMeasureNode2);
		aciSectionNode.addChildNode(aciProportionMeasureNode3);

		reportingParametersActNode = new Node();
		reportingParametersActNode.setId("2.16.840.1.113883.10.20.27.3.23");
		reportingParametersActNode.putValue("performanceStart", "20170101");
		reportingParametersActNode.putValue("performanceEnd", "20171231");

		reportingParametersSectionNode = new Node();
		reportingParametersSectionNode.setId("2.16.840.1.113883.10.20.27.2.6");
		reportingParametersSectionNode.addChildNode(reportingParametersActNode);


		clinicalDocumentNode = new Node();
		clinicalDocumentNode.setId("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.addChildNode(reportingParametersSectionNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);

		nodes = new ArrayList<>();
		nodes.add(clinicalDocumentNode);
	}
	
	@Test
	public void testInternalEncode() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map clinicalDocMap = ((Map)testJsonWrapper.getObject());

		assertThat("Must have a correct program name", clinicalDocMap.get("programName"), is("mips"));

		assertThat("Must have a correct entityType", clinicalDocMap.get("entityType"), is("individual"));

		assertThat("Must have a correct taxpayerIdentificationNumber",
				clinicalDocMap.get("taxpayerIdentificationNumber"), is("123456789"));

		assertThat("Must have a correct nationalProviderIdentifier",
				clinicalDocMap.get("nationalProviderIdentifier"), is("2567891421"));

		assertThat("Must have a correct performanceYear", clinicalDocMap.get("performanceYear"), is(2017));

		assertThat("Must have correct json formatting", testJsonWrapper.toString(),
				is(EXPECTED_CLINICAL_DOC_FORMAT));
	}

	@Test
	public void testInternalEncoderWithoutReporting() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(reportingParametersSectionNode);

		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		assertThat("Must return a Clinical Document without reporting parameter section", testJsonWrapper.toString(),
				is(EXPECTED_NO_REPORTING));
	}

	@Test
	public void testInternalEncodeWithoutMeasures() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);

		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		assertThat("Must return a Clinical Document without measurement section", testJsonWrapper.toString(),
				is(EXPECTED_NO_ACI));
	}

	@Test
	public void testInvalidEncoder() {
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();

		Registry<String, JsonOutputEncoder> invalidRegistry = new Registry<String, JsonOutputEncoder>(Encoder.class) {
			@Override
			protected Class<?> getAnnotatedClass(String className) throws ClassNotFoundException {
				if ("gov.cms.qpp.conversion.encode.AciSectionEncoder".equals(className)) {
					System.setErr(new PrintStream(new ByteCounterOutputStream()));
					throw new ClassNotFoundException();
				}
				return Class.forName(className);
			}
		};
		boolean exception = false;
		Registry<String, JsonOutputEncoder> validRegistry = QppOutputEncoder.encoders;
		QppOutputEncoder.encoders = invalidRegistry;

		try {
			clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);
		} catch (EncodeException e) {
			exception = true;
		}

		QppOutputEncoder.encoders = validRegistry;
		assertThat("Expecting Encode Exception", exception, is(true));

	}

}
