package controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dao.FileDAO;
import jsonobject.JSONResult;
import util.CommonUtil;

@RequestMapping(value = "files")

@RestController
public class FileController {

	private static final Logger logger = Logger.getLogger(FileController.class);

	@Autowired
	FileDAO fileDAO;

	// TESTED, PLEASE CHANGE THE LOCATION CONFIG BEFORE USING IT WHICH LOCATED IN
	// springrest-servlet.xml
	@RequestMapping(value = "/upload_image", method = RequestMethod.POST)
	public JSONResult uploadFile(@RequestParam("file") MultipartFile file) {
		logger.info("uploadFile() started");
		JSONResult jsonObject = new JSONResult();
		try {
			String uploadedPath = fileDAO.fileUpload(file);
			if (!CommonUtil.isNullOrEmpty(uploadedPath)) {
				jsonObject.setCode("S");
				logger.info("uploadFile() success");
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("Fail to upload file.");
				logger.error("uploadFile() failed");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail(e.getMessage());
			logger.error("uploadFile() failed due to error: " + e.getMessage());
		}

		return jsonObject;
	}

	@CrossOrigin
	@RequestMapping(value = "/download_image", method = RequestMethod.GET)
	public byte[] downloadFile(@RequestParam("file_name") String fileName) {
		logger.info("downloadFile() started with fileName = " + fileName);
		try {
			if (CommonUtil.isNullOrEmpty(fileName) || fileName.equals("undefined")) {
				logger.error("downloadFile() failed with error: fileName is null or undefined");
				throw new Exception("fileName is null or undefined");
			}
			byte[] byteArray = fileDAO.fileDownload(fileName);
			if (!CommonUtil.isNullOrEmpty(byteArray)) {
				logger.info("downloadFile() success");
			}else {
				logger.error("downloadFile() failed, byte array is null");
			}
			return byteArray;
		} catch (Exception e) {
			logger.error("downloadFile() failed with error: " + e.getMessage());
			return null;
		}
	}

}