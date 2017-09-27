package controller;

import dao.ShoppingDetailDAO;
import dto.ShoppingDetailDTO;
import jsonobject.JSONProductDTO;
import jsonobject.JSONResult;
import jsonobject.JSONShoppingDetailDTO;
import util.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import bean.ShoppingDetail;

@RequestMapping(value = "shop")

@CrossOrigin
@RestController
public class ShoppingDetailController {

	private static final Logger logger = Logger.getLogger(ShoppingDetailController.class);
	
	@Autowired
	ShoppingDetailDAO shoppingDetailDAO;

	@RequestMapping(value = "add_to_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResult addShoppingDetail(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "amend_detail") String productAmendDetail) {
		logger.info("addShoppingDetail() started");
		JSONResult jsonObject = new JSONResult();
		ObjectMapper mapper = new ObjectMapper();
		int successFlag = 0;
		

		try {
			JSONShoppingDetailDTO dto = null;
			dto = mapper.readValue(productAmendDetail, JSONShoppingDetailDTO.class);
	
			if (CommonUtil.isNullOrEmpty(dto)) {
				throw new Exception("amend_detail is null");
			}
			
			List<ShoppingDetail> checkIfAlreadyExisted = shoppingDetailDAO.checkShoppingDetail(userId,
					dto.getProduct_id());
			if (checkIfAlreadyExisted.size() > 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Item already exist in shopping cart.");
				return jsonObject;
			}

			successFlag = shoppingDetailDAO.addShoppingDetail(userId, dto.getProduct_id(), dto.getProduct_quantity());
			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Fail to insert to shopping detail due to unknown reason");
				logger.error("addShoppingDetail() failed");
			} else {
				jsonObject.setCode("S");
				logger.info("addShoppingDetail() success");
			}

		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to insert to shopping detail due to : " + e.getMessage());
			logger.error("addShoppingDetail() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "get_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResult getShoppingDetail(@RequestParam(value = "user_id") Integer userId) {
		logger.info("getShoppingDetail() started with userId = " + userId);
		JSONResult jsonObject = new JSONResult();
		try {
			List<ShoppingDetail> shoppingDetailList = shoppingDetailDAO.getShoppingDetail(userId);
			List<ShoppingDetailDTO> shoppingDetailDTOlst = new ArrayList<ShoppingDetailDTO>();
			if (!CommonUtil.isNullOrEmpty(shoppingDetailList)) {				
				for (ShoppingDetail shoppingDetail : shoppingDetailList) {
					shoppingDetailDTOlst.add(toDto(shoppingDetail));
				}
				jsonObject.setCode("S");
				jsonObject.setData(shoppingDetailDTOlst);
				logger.info("getShoppingDetail() started with return value = " + shoppingDetailDTOlst);
			} else {
				jsonObject.setCode("S");
				jsonObject.setDetail("No result is found.");
				jsonObject.setData(shoppingDetailDTOlst);
				logger.error("getShoppingDetail() failed with error: no result is found");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured : " + e.getMessage());
			logger.error("getShoppingDetail() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "update_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResult updateShoppingDetail(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "amend_detail") String[] productAmendDetailArray) {
		logger.info("updateShoppingDetail() started");
		JSONResult jsonObject = new JSONResult();
		ObjectMapper mapper = new ObjectMapper();
		int successFlag = 0;
		try {
			if (!CommonUtil.isNullOrEmpty(productAmendDetailArray)) {
				if (!productAmendDetailArray[0].substring(productAmendDetailArray[0].length() - 1).equalsIgnoreCase("}")) {
					String modifiedStr = productAmendDetailArray[0] + ", " + productAmendDetailArray[1];
					JSONShoppingDetailDTO dto = mapper.readValue(modifiedStr, JSONShoppingDetailDTO.class);
					
					successFlag += shoppingDetailDAO.updateShoppingDetailQuantity(userId, dto.getProduct_id(),
							dto.getProduct_quantity());
				}else {
					for (int i = 0; i < productAmendDetailArray.length; i++) {
						JSONShoppingDetailDTO dto = mapper.readValue(productAmendDetailArray[i], JSONShoppingDetailDTO.class);
						
						successFlag += shoppingDetailDAO.updateShoppingDetailQuantity(userId, dto.getProduct_id(),
								dto.getProduct_quantity());
					}
				}				
			}
			
			
			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Update shopping cart failed, possible due to wrong user_id or product_id");
				logger.error("updateShoppingDetail() failed with error: wrong user_id or product_id");
			} else {
				jsonObject.setCode("S");
				logger.info("updateShoppingDetail() finished");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to update shopping detail due to : " + e.getMessage());
			logger.error("updateShoppingDetail() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "delete_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResult deleteShoppingDetail(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "amend_detail") String[] productAmendDetail) {
		logger.info("deleteShoppingDetail() started");
		JSONResult jsonObject = new JSONResult();
		ObjectMapper mapper = new ObjectMapper();
		int successFlag = 0;
		
		try {
			if (!CommonUtil.isNullOrEmpty(productAmendDetail)) {
				if (!productAmendDetail[0].substring(productAmendDetail[0].length() - 1).equalsIgnoreCase("}")) {
					String modifiedStr = productAmendDetail[0] + ", " + productAmendDetail[1];
					JSONProductDTO dto = mapper.readValue(modifiedStr, JSONProductDTO.class);
					
					successFlag += shoppingDetailDAO.deleteShoppingDetail(userId, dto.getProduct_id());
				}else {
					for (int i = 0; i < productAmendDetail.length; i++) {
						JSONProductDTO dto = mapper.readValue(productAmendDetail[i], JSONProductDTO.class);
						
						successFlag += shoppingDetailDAO.deleteShoppingDetail(userId, dto.getProduct_id());
					}
				}				
			}
			
			
			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Delete item failed, possible due to wrong user_id or product_id.");
				logger.error("deleteShoppingDetail() failed with error: wrong user_id or product_id");
			} else {
				jsonObject.setCode("S");
				logger.info("deleteShoppingDetail() success");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to delete item due to : " + e.getMessage());
			logger.error("deleteShoppingDetail() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}
	
	public ShoppingDetailDTO toDto(ShoppingDetail shoppingDetail) {
		ShoppingDetailDTO shoppingDetailDTO = new ShoppingDetailDTO();
		shoppingDetailDTO.setChecked(true);
		shoppingDetailDTO.setProductId(shoppingDetail.getProductId());
		shoppingDetailDTO.setProductImg(shoppingDetail.getProductImg());
		shoppingDetailDTO.setProductTitle(shoppingDetail.getProductTitle());
		shoppingDetailDTO.setProductPrice((shoppingDetail.getProductPrice()));
		shoppingDetailDTO.setProductQuantity(shoppingDetail.getProductQuantity());
		return shoppingDetailDTO;
	}

}