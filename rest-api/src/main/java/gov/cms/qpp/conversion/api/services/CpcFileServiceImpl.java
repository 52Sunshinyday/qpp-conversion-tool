package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for handling Cpc File meta data
 */
@Service
public class CpcFileServiceImpl implements CpcFileService {

	@Autowired
	private DbService dbService;

	@Autowired
	private StorageService storageService;

	/**
	 * Calls the DbService for unprocessed metadata to transform into UnprocessedCpcFileData
	 *
	 * @return List of {@link UnprocessedCpcFileData}
	 */
	@Override
	public List<UnprocessedCpcFileData> getUnprocessedCpcPlusFiles() {
		List<Metadata> metadata = dbService.getUnprocessedCpcPlusMetaData();

		return transformMetaDataToUnprocessedCpcFileData(metadata);
	}

	/**
	 * Retrieves the file location id and uses it to retrieve the file
	 *
	 * @param fileId {@link Metadata} identifier
	 * @return file returned as an {@link InputStream}
	 */
	public InputStream getFileById(String fileId) {
		String fileLocationId = dbService.getFileSubmissionLocationId(fileId);

		return storageService.getFileByLocationId(fileLocationId);
	}

	/**
	 * Service to transform a {@link Metadata} list into the {@link UnprocessedCpcFileData}
	 *
	 * @param metadataList object to hold the list of {@link Metadata} from DynamoDb
	 * @return transformed list of {@link UnprocessedCpcFileData}
	 */
	private List<UnprocessedCpcFileData> transformMetaDataToUnprocessedCpcFileData(List<Metadata> metadataList) {
		return metadataList.stream().map(UnprocessedCpcFileData::new).collect(Collectors.toList());
	}

	/**
	 * Process to ensure the file is an unprocessed cpc+ file and marks the file as processed
	 *
	 * @param fileId
	 */
	public String processFileById(String fileId) {
		String message = "The given id is invalid and was not find the cpc+ data";
		Metadata metadata = dbService.getMetadataById(fileId);
		if (metadata != null && metadata.getCpc() != null && !metadata.getCpcProcessed()) {
			metadata.setCpcProcessed(true);
			dbService.write(metadata);
			message = "The data will be set as processed!";
		}

		return message;
	}
}
