package gov.cms.qpp.conversion.api.model;

import com.google.common.base.MoreObjects;
import java.time.Instant;

/**
 * Model to hold converted metadata to Unprocessed Cpc file data.
 */
public class UnprocessedCpcFileData {

	private final String fileId;
	private final String filename;
	private final String apm;
	private final Instant conversionDate;
	private final Boolean validationSuccess;
	private final Boolean test;

	/**
	 * Constructor to transform metadata into unprocessed cpc file data
	 *
	 * @param metadata object to be transformed
	 */
	public UnprocessedCpcFileData(Metadata metadata) {
		this.fileId = metadata.getUuid();
		this.filename = metadata.getFileName();
		this.apm = metadata.getApm();
		this.conversionDate = metadata.getCreatedDate();
		this.validationSuccess = metadata.getOverallStatus();
		this.test = metadata.getTest();
	}

	/**
	 * retrieves the uuid for file data
	 *
	 * @return fileId
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * retrieves the filename
	 *
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * retrieves the apm id
	 *
	 * @return apm
	 */
	public String getApm() {
		return apm;
	}

	/**
	 * retrieves the conversion date
	 *
	 * @return conversionDate formatted as a string
	 */
	public String getConversionDate() {
		return conversionDate.toString();
	}

	/**
	 * retrieves whether the validation was a success or not
	 *
	 * @return validationSuccess
	 */
	public Boolean getValidationSuccess() {
		return validationSuccess;
	}

	/**
	 * retrieves whether the submission was part of a test
	 *
	 * @return validationSuccess
	 */
	public Boolean getTest() {
		return test;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("fileId", fileId)
			.add("filename", "*could hold PII*")
			.add("apm", apm)
			.add("conversionDate", conversionDate)
			.add("validationSuccess", validationSuccess)
			.add("test", test)
			.toString();
	}
}
