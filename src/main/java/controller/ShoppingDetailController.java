package controller;

import dao.ShoppingDetailDAO;
import jsonobject.JSONObject;
import jsonobject.JSONShoppingDetailDTO;
import util.CommonUtil;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import bean.ShoppingDetail;

@RequestMapping(value = "shop")

@RestController
public class ShoppingDetailController {

	@Autowired
	ShoppingDetailDAO shoppingDetailDAO;

	@RequestMapping(value = "add_to_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject addShoppingDetail(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "product_detail") String productDetail) {

		JSONObject jsonObject = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		int successFlag = 0;
		try {
			JSONShoppingDetailDTO dto = mapper.readValue(productDetail, JSONShoppingDetailDTO.class);
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
			} else {
				jsonObject.setCode("S");
			}

		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to insert to shopping detail due to : " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "get_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject getShoppingDetail(@RequestParam(value = "user_id") Integer userId) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<ShoppingDetail> shoppingDetailList = shoppingDetailDAO.getShoppingDetail(userId);
			if (!CommonUtil.isNullOrEmpty(shoppingDetailList)) {
				jsonObject.setCode("S");
				jsonObject.setData(shoppingDetailList);
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("No result is found.");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured : " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "update_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject updateShoppingDetail(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "product_amend_detail") String productAmendDetail) {
		JSONObject jsonObject = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		int successFlag = 0;
		try {
			JSONShoppingDetailDTO[] dtoList = mapper.readValue(productAmendDetail, JSONShoppingDetailDTO[].class);
			for (int i = 0; i < dtoList.length; i++) {
				successFlag += shoppingDetailDAO.updateShoppingDetailQuantity(userId, dtoList[i].getProduct_id(),
						dtoList[i].getProduct_quantity());
			}
			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Update shopping cart failed, possible due to wrong user_id or wrong product_id.");
			} else {
				jsonObject.setCode("S");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to update shopping detail due to : " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "delete_shopping_detail", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject deleteShoppingDetail(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "product_delete_detail") String productDeleteDetail) {
		JSONObject jsonObject = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		int successFlag = 0;
		try {
			JSONShoppingDetailDTO[] dtoList = mapper.readValue(productDeleteDetail, JSONShoppingDetailDTO[].class);
			for (int i = 0; i < dtoList.length; i++) {
				successFlag += shoppingDetailDAO.deleteShoppingDetail(userId, dtoList[i].getProduct_id());
			}
			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Delete item failed, possible due to wrong user_id or wrong product_id.");
			} else {
				jsonObject.setCode("S");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to delete item due to : " + e.getMessage());
		}

		return jsonObject;
	}

}