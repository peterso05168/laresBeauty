package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import dao.OrderDAO;
import dao.ShoppingDetailDAO;
import jsonobject.JSONResult;
import jsonobject.JSONShoppingDetailDTO;

@RequestMapping(value = "order")

@RestController
public class OrderController {

	@Autowired
	ShoppingDetailDAO shoppingDetailDAO;

	@Autowired
	OrderDAO orderDAO;

	@Transactional("tjtJTransactionManager") // This is for transaction control if one insert or update failed, it roll
												// backs.
	@RequestMapping(value = "add_order", method = RequestMethod.POST)
	public JSONResult addOrder(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "user_address_info_id") Integer userAddressInfoId,
			@RequestParam(value = "shopping_detail") String shoppingDetail) {

		JSONResult jsonObject = new JSONResult();
		ObjectMapper mapper = new ObjectMapper();
		try {
			int orderId = orderDAO.addOrder(userId, userAddressInfoId);

			int successFlag = 0;

			JSONShoppingDetailDTO[] dtoList = mapper.readValue(shoppingDetail, JSONShoppingDetailDTO[].class);
			// Assume it is from shopping cart
			for (int i = 0; i < dtoList.length; i++) {
				successFlag += shoppingDetailDAO.updateShoppingDetailStatus(userId, dtoList[i].getProduct_id(), "P",
						orderId);
			}

			// If update count = 0 -> it is from product detail-> payment directly
			if (successFlag == 0) {
				for (int i = 0; i < dtoList.length; i++) {
					successFlag += shoppingDetailDAO.addShoppingDetailWithOrder(userId, dtoList[i].getProduct_id(),
							dtoList[i].getProduct_quantity(), "P", orderId);
				}
			}

			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Add item failed.");
			} else {
				jsonObject.setCode("S");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Add item failed due to : " + e.getMessage());
		}

		return jsonObject;
	}

}