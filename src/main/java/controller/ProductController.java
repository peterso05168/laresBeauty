package controller;

import bean.Product;
import dao.FileDAO;
import dao.ProductDAO;
import dto.ProductDTO;
import jsonobject.JSONObject;
import jsonobject.JSONProduct;
import jsonobject.JSONProductDTO;
import jsonobject.JSONProductDataList;
import util.CommonUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping(value = "product")

@CrossOrigin
@RestController
public class ProductController {

	private static final Logger logger = Logger.getLogger(ProductController.class);

	@Autowired
	ProductDAO productDAO;

	@Autowired
	FileDAO fileDAO;

	// FOR TESTING PURPOSE
	@RequestMapping(value = "featured", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONObject getFeaturedProducts() {
		return null;
	}

	@RequestMapping(value = "get_product", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONProduct getCategoryProducts(
			@RequestParam(value = "product_status", defaultValue = "A") String productStatus,
			@RequestParam(value = "product_type", defaultValue = "F") String productType) {

		logger.info("getCategoryProducts() started with productStatus = " + productStatus + ", productType = "
				+ productType);

		JSONProduct jsonObject = new JSONProduct();

		try {
			List<Product> categoryProductList = productDAO.getCategoryProducts(productStatus, productType);
			if (!CommonUtil.isNullOrEmpty(categoryProductList)) {
				jsonObject.setCode("S");

				JSONProductDataList jsonProductDataList = new JSONProductDataList();
				List<ProductDTO> productTypeF = new ArrayList<ProductDTO>();
				List<ProductDTO> productTypeS = new ArrayList<ProductDTO>();

				for (Product product : categoryProductList) {
					if (product.getProductType().equals("S")) {
						productTypeS.add(toDto(product));
					}
					if (product.getProductType().equals("F")) {
						productTypeF.add(toDto(product));
					}
				}
				jsonProductDataList.setProductTypeF(productTypeF);
				jsonProductDataList.setProductTypeS(productTypeS);
				jsonObject.setData(jsonProductDataList);
				logger.info("getCategoryProducts() success with return value = " + jsonProductDataList);
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("No result is found.");
				logger.error("getCategoryProducts() failed with error: no result is found.");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured: " + e.getMessage());
			logger.error("getCategoryProducts() failed with error: " + e.getMessage());
		}
		return jsonObject;
	}

	@RequestMapping(value = "get_product_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject getProductsDetail(@RequestParam(value = "product_id") Integer productId) {

		logger.info("getProductsDetail() started with productId = " + productId);

		JSONObject jsonObject = new JSONObject();
		try {
			List<Product> productDetail = productDAO.getProductDetail(productId);
			if (!CommonUtil.isNullOrEmpty(productDetail)) {
				jsonObject.setCode("S");
				jsonObject.setData(productDetail);
				logger.info("getProductsDetail() success with return value = " + productDetail.toString());
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("No result is found.");
				logger.error("getProductsDetail() failed with error: no result is found");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured: " + e.getMessage());
			logger.error("getProductsDetail() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "delete_product", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject deleteProducts(@RequestParam(value = "product_id") String productId) {
		logger.info("deleteProducts() started with productId = " + productId);
		JSONObject jsonObject = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		int successFlag = 0;
		try {
			JSONProductDTO[] dtoList = mapper.readValue(productId, JSONProductDTO[].class);
			for (int i = 0; i < dtoList.length; i++) {
				successFlag += productDAO.deleteProduct(dtoList[i].getProduct_id());
			}
			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Delete item failed, possible due to wrong product_id.");
				logger.error("deleteProducts() failed with error: wrong product_id");
			} else {
				jsonObject.setCode("S");
				logger.info("deleteProducts() success");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to delete item due to : " + e.getMessage());
			logger.error("deleteProducts() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "search_product_by_title", method = RequestMethod.POST)
	public JSONObject searchProductByTitle(@RequestParam(value = "product_title") String productTitle) {
		logger.info("searchProductByTitle() started with productTitle = " + productTitle);
		JSONObject jsonObject = new JSONObject();
		try {
			List<Product> productList = productDAO.searchProductByTitle(productTitle);
			if (!CommonUtil.isNullOrEmpty(productList)) {
				jsonObject.setCode("S");
				jsonObject.setData(productList);
				logger.info("searchProductByTitle() success with return value = " + productList);
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("No result is found.");
				logger.error("searchProductByTitle() failed with error: no result is found");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured: " + e.getMessage());
			logger.error("searchProductByTitle() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "add_product", method = RequestMethod.POST)
	public JSONObject addProducts(@RequestParam(value = "product_title") String productTitle,
			@RequestParam(value = "product_desc") String productDesc,
			@RequestParam(value = "product_price") Double productPrice,
			@RequestParam(value = "product_type") String productType,
			@RequestParam("product_img") MultipartFile[] productImg) {

		logger.info("addProducts() started with productTitle = " + productTitle + ", productDesc = " + productDesc
				+ ", productPrice = " + productPrice + ", productType = " + productType + ", productImg = "
				+ productImg);
		JSONObject jsonObject = new JSONObject();

		try {
			String fileName = new String(fileDAO.fileUpload(productImg[0]));
			String fileName2 = new String(fileDAO.fileUpload(productImg[1]));
			String fileName3 = new String(fileDAO.fileUpload(productImg[2]));

			int successFlag = 0;

			successFlag += productDAO.addProduct(productTitle, productDesc, productPrice, productType, fileName,
					fileName2, fileName3);

			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Add item failed.");
				logger.error("addProducts() failed");
			} else {
				jsonObject.setCode("S");
				logger.info("addProducts() success");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Add item failed due to : " + e.getMessage());
			logger.error("addProducts() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "edit_product", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject editProducts(@RequestParam(value = "product_id") Integer productId,
			@RequestParam(value = "product_title") String productTitle,
			@RequestParam(value = "product_desc") String productDesc,
			@RequestParam(value = "product_price") Double productPrice,
			@RequestParam(value = "product_type") String productType) {
		logger.info("editProducts() started with productId = " + productId + ", productTitle = " + productTitle
				+ ", productDesc = " + productDesc + ", productPrice = " + productPrice + ", productType = "
				+ productType);
		JSONObject jsonObject = new JSONObject();
		int successFlag = 0;
		try {
			successFlag += productDAO.editProduct(productId, productTitle, productDesc, productPrice, productType);

			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Delete item failed, possible due to wrong product_id.");
				logger.error("editProducts() failed with error: wrong productId");
			} else {
				jsonObject.setCode("S");
				logger.info("editProducts() success");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to delete item due to : " + e.getMessage());
			logger.error("editProducts() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	public ProductDTO toDto(Product product) {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setProductId(product.getProductId());
		productDTO.setProductDesc(product.getProductDesc());
		productDTO.setProductStatus(product.getProductStatus());
		productDTO.setProductTitle(product.getProductTitle());
		productDTO.setProductType(product.getProductType());
		productDTO.setProductImg(product.getProductImg());
		List<String> detailImgs = new ArrayList<String>();
		detailImgs.add(product.getProductImg());
		detailImgs.add(product.getProductImg2());
		detailImgs.add(product.getProductImg3());
		productDTO.setDetailImgs(detailImgs);
		productDTO.setProductPrice(new DecimalFormat("0.00").format(product.getProductPrice()));
		return productDTO;
	}

}