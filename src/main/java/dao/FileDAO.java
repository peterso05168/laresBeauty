package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileDAO {
	private String imagesFolder;
	
	public void setImagesFolder(String imagesFolder) {
	    this.imagesFolder = imagesFolder;
	}

	@SuppressWarnings("resource")
	public String fileUpload(MultipartFile uploadedFile) throws Exception {
	    InputStream inputStream = null;
	    OutputStream outputStream = null;
	    MultipartFile file = uploadedFile;
	    String fileName = file.getOriginalFilename() + "_" + UUID.randomUUID();
	    File newFile = new File(imagesFolder + fileName);

        inputStream = file.getInputStream();

        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        outputStream = new FileOutputStream(newFile);
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
	    
	    return fileName;
	}
	
	public byte[] fileDownload(String fileName) throws IOException {
		InputStream targetStream = new FileInputStream(new File(imagesFolder + fileName));
		return IOUtils.toByteArray(targetStream);
	}
}