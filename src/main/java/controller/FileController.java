package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dao.FileDAO;
import jsonobject.JSONObject;
import util.CommonUtil;
 

@RequestMapping(value = "files")

@RestController
public class FileController {

	@Autowired  
	FileDAO fileDAO;
	
	//TESTED, PLEASE CHANGE THE LOCATION CONFIG BEFORE USING IT WHICH LOCATED IN springrest-servlet.xml
	@RequestMapping(value = "/upload_image", method = RequestMethod.POST)
	public JSONObject uploadFile(@RequestParam("file") MultipartFile file) {
		JSONObject jsonObject = new JSONObject();
		try {
			String uploadedPath = fileDAO.fileUpload(file);
			if (!CommonUtil.isNullOrEmpty(uploadedPath)) {
				jsonObject.setCode("S");
			}else {
				jsonObject.setCode("F");
				jsonObject.setDetail("Fail to upload file.");
			}
		}catch(Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail(e.getMessage());
		}
		
		return jsonObject;
	}
	
	@CrossOrigin
	@RequestMapping(value = "/download_image", method = RequestMethod.GET)
	public byte[] downloadFile(@RequestParam("file_name") String fileName) {
		try {
			return fileDAO.fileDownload(fileName);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}