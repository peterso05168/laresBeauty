package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dao.OrderDAO;
import dao.ShoppingDetailDAO;
import jsonobject.JSONResult;

@RequestMapping(value = "order")

@RestController
public class OrderController {

	@Autowired
	ShoppingDetailDAO shoppingDetailDAO;

	@Autowired
	OrderDAO orderDAO;

	@Deprecated
	@Transactional("tjtJTransactionManager") // This is for transaction control if one insert or update failed, it roll backs.
	@RequestMapping(value = "add_order", method = RequestMethod.POST)
	public JSONResult addOrder(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "user_address_info_id") Integer userAddressInfoId,
			@RequestParam(value = "shopping_detail") String shoppingDetail) {

		return null;
	}

}