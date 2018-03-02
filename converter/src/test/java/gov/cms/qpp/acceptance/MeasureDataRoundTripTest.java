package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;

import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class MeasureDataRoundTripTest {

	private static String happy;
	private static String expected =
			"{\n  \"eligiblePopulation\" : 950,\n  \"performanceMet\" : 900,\n" +
			"  \"eligiblePopulationException\" : 50,\n  \"eligiblePopulationExclusion\" : 50\n}";

	@BeforeAll
	static void setup() throws IOException {
		happy = TestHelper.getFixture("measureDataHappy.xml");
	}

	@ParameterizedTest
	@EnumSource(value = SubPopulationLabel.class, mode = EnumSource.Mode.EXCLUDE, names = {"IPOP"})
	void decodeMeasureDataAsNode(SubPopulationLabel label) throws Exception {
		test(label);
	}

//	@Test
//	void decodeNumerMeasureDataAsNode() throws Exception {
//		test(SubPopulations.NUMER);//performanceMet
//	}
//
//	@Test
//	void decodeDenexMeasureDataAsNode() throws Exception {
//		test(SubPopulations.DENEX);//eligiblePopulationExclusion
//	}
//
//	@Test
//	void decodeDenexcepMeasureDataAsNode() throws Exception {
//		test(SubPopulations.DENEXCEP);//eligiblePopulationException
//	}

	private void test(SubPopulationLabel type) throws Exception {
		//setup
		String typeLabel = type.name();
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(happy));
		Node measure =  placeholder.findChildNode(n -> n.getValue(MEASURE_TYPE).equals(typeLabel));
		String message = String.format("Should have a %s measure", type);

		//when
		StringWriter sw = encode(placeholder);

		//then
		assertThat(measure).isNotNull();
		assertThat(measure.getChildNodes().get(0).getType())
				.isEquivalentAccordingToCompareTo(TemplateId.ACI_AGGREGATE_COUNT);
		assertThat(sw.toString())
				.isEqualTo(expected);
	}

	private StringWriter encode(Node placeholder) throws EncodeException {
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());
		List<Node> nodes = new ArrayList<>();
		nodes.add(placeholder);
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();
		encoder.encode(new BufferedWriter(sw));
		return sw;
	}
}
