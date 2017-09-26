package controller;

import dao.ShoppingDetailDAO;
import dto.ShoppingDetailDTO;
import jsonobject.JSONObject;
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
	public JSONObject addShoppingDetail(HttpServletRequest request) {
		logger.info("addShoppingDetail() started");
		JSONObject jsonObject = new JSONObject();
		int successFlag = 0;
		int userId = Integer.valueOf(request.getParameter("user_id"));
		String productId = request.getParameter("product_detail[product_id]");
		String productQuantity = request.getParameter("product_detail[product_quantity]");
		Map<String, String[]> params = request.getParameterMap();
		try {
			JSONShoppingDetailDTO dto = new JSONShoppingDetailDTO();
			dto.setProduct_id(Integer.valueOf(productId));
			dto.setProduct_quantity(Integer.valueOf(productQuantity));
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
	public JSONObject getShoppingDetail(@RequestParam(value = "user_id") Integer userId) {
		logger.info("getShoppingDetail() started with userId = " + userId);
		JSONObject jsonObject = new JSONObject();
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
	public JSONObject updateShoppingDetail(HttpServletRequest request) {
		logger.info("updateShoppingDetail() started");
		JSONObject jsonObject = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		int successFlag = 0;
		Map<String, String[]> params = request.getParameterMap();
		try {
//			JSONShoppingDetailDTO[] dtoList = mapper.readValue(productAmendDetail, JSONShoppingDetailDTO[].class);
//			for (int i = 0; i < dtoList.length; i++) {
//				successFlag += shoppingDetailDAO.updateShoppingDetailQuantity(userId, dtoList[i].getProduct_id(),
//						dtoList[i].getProduct_quantity());
//			}
//			if (successFlag == 0) {
//				jsonObject.setCode("F");
//				jsonObject.setDetail("Update shopping cart failed, possible due to wrong user_id or product_id");
//				logger.error("updateShoppingDetail() failed with error: wrong user_id or product_id");
//			} else {
//				jsonObject.setCode("S");
//				logger.info("updateShoppingDetail() finished");
//			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to update shopping detail due to : " + e.getMessage());
			logger.error("updateShoppingDetail() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "delete_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject deleteShoppingDetail(HttpServletRequest request) {
		logger.info("deleteShoppingDetail() started");
		JSONObject jsonObject = new JSONObject();
		
		int successFlag = 0;
		Map<String, String[]> params = request.getParameterMap();
		
		try {
			int userId = 0;
			for (Entry<String, String[]> entry : params.entrySet()) {
			    System.out.println(entry.getKey() + "/" + entry.getValue());
			    if (entry.getKey().equals("user_id")) {
			    		userId = Integer.valueOf(entry.getValue()[0]);
			    		continue;
			    }
			    successFlag += shoppingDetailDAO.deleteShoppingDetail(userId, Integer.valueOf(entry.getValue()[0]));
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