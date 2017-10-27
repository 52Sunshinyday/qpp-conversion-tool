package gov.cms.qpp.conversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConversionReportTest {
	private static Converter.ConversionReport report;
	private static Converter.ConversionReport errorReport;
	private static JsonWrapper wrapper;

	@BeforeClass
	public static void setup() {
		Converter converter = new Converter(
				new PathQrdaSource(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		wrapper = converter.transform();
		report = converter.getReport();

		Converter otherConverter = new Converter(
				new PathQrdaSource(Paths.get("../qrda-files/QRDA-III-without-required-measure.xml")));
		try {
			otherConverter.transform();
		} catch (TransformException ex) {
			//no worries
			errorReport = ex.getConversionReport();
		}
	}

	@Test
	public void testReportRetrieval() {
		assertThat(report).isNotNull();
	}

	@Test
	public void testGetEncoded() {
		assertThat(report.getEncoded().toString())
				.isEqualTo(wrapper.toString());
	}

	@Test
	public void validationErrorDetails() throws IOException {
		Converter converter = new Converter(
				new PathQrdaSource(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		Converter.ConversionReport aReport = converter.getReport();
		aReport.setRawValidationDetails("meep");
		String details = IOUtils.toString(aReport.streamRawValidationDetails(), "UTF-8");

		assertThat(details).isEqualTo("meep");
	}

	@Test
	public void emptyValidationErrorDetails() throws IOException {
		String details = IOUtils.toString(errorReport.streamRawValidationDetails(), "UTF-8");

		assertThat(details).isEmpty();
	}

	@Test
	public void getReportDetails() {
		assertThat(errorReport.getReportDetails())
				.isNotNull();
	}

	@Test(expected = EncodeException.class)
	public void getBadReportDetails() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
		ObjectMapper mockMapper = mock(ObjectMapper.class);
		when(mockMapper.writeValueAsBytes(any(AllErrors.class)))
				.thenThrow(new JsonMappingException("meep"));

		Converter converter = new Converter(
				new PathQrdaSource(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		Converter.ConversionReport badReport = converter.getReport();

		Field field = badReport.getClass().getDeclaredField("mapper");
		field.setAccessible(true);
		field.set(badReport, mockMapper);

		badReport.streamDetails();
	}

	@Test
	public void getGoodReportDetails() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
		assertThat(errorReport.streamDetails()).isNotNull();
	}

	@Test
	public void getErrorStream() throws IOException {
		Converter converter = new Converter(
				new PathQrdaSource(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		Converter.ConversionReport badReport = converter.getReport();
		Error error = new Error();
		error.setMessage("meep");
		AllErrors errors = new AllErrors();
		errors.addError(error);
		badReport.setReportDetails(errors);

		AllErrors echo = JsonHelper.readJson(badReport.streamDetails(), AllErrors.class);
		assertThat(echo.toString()).isEqualTo(errors.toString());
	}

	@Test
	public void getFilename() {
		assertThat(report.getFilename()).isEqualTo("valid-QRDA-III-latest.xml");
	}

	@Test
	public void getFileInput() {
		assertThat(report.getFileInput()).isNotNull();
	}
}
