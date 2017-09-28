package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.APIException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

import dao.LoginDAO;
import dao.OrderDAO;
import dao.ShoppingDetailDAO;
import jsonobject.JSONResult;
import jsonobject.JSONShoppingDetailDTO;
import util.CommonUtil;

@RequestMapping(value = "payment")

@CrossOrigin
@Transactional("tjtJTransactionManager")
@RestController
public class PaymentController {
	
	private static final Logger logger = Logger.getLogger(PaymentController.class);

	@Autowired
	LoginDAO loginDAO;

	@Autowired
	OrderDAO orderDAO;

	@Autowired
	ShoppingDetailDAO shoppingDetailDAO;

	@RequestMapping(value = "/checkout")
	public JSONResult checkout(@RequestParam(value = "price") Integer price,
			@RequestParam(value = "stripeToken") String stripeToken, @RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "user_address_info_id") Integer userAddressInfoId,
			@RequestParam(value = "amend_detail") String[] productAmendDetail) {
		
			logger.info("checkout() start");
			JSONResult jsonResult = new JSONResult();
			
			try {
				chargeCustomer(price, stripeToken);
				addOrder(userId, userAddressInfoId, productAmendDetail);
				jsonResult.setCode("S");
			}catch (Exception e) {
				jsonResult.setCode("F");
				jsonResult.setDetail(e.getMessage());
			}
			
		return jsonResult;
	}

	private boolean chargeCustomer(int price, String token) throws Exception {
		logger.info("chargeCustomer() start");
		// Set your secret key: remember to change this to your live secret key in
		// production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_7SPSsD2SkPABEZlWdqgQaMVa";
		// Token is created using Stripe.js or Checkout!
		// Get the payment token ID submitted by the form:
		// String token = request.getParameter("stripeToken");

		// Create a Customer:
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("email", "ericqwerty0@gmail.com");
		customerParams.put("source", token);
		Customer customer;
		try {
			customer = Customer.create(customerParams);
		} catch (Exception e) {
			logger.error("chargeCustomer() failed with error: " + e.getMessage()); 
			throw e;
		}

		// Charge the Customer instead of the card:
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", price);
		chargeParams.put("currency", "hkd");
		chargeParams.put("customer", customer.getId());
		Charge charge;
		try {
			charge = Charge.create(chargeParams);
		} catch (APIException e) {
			logger.error("chargeCustomer() failed with error: " + e.getMessage()); 
			throw e;
		}
		if (charge.getPaid()) {
			logger.info("chargeCustomer() success");
			return true;
		}

		// // YOUR CODE: Save the customer ID and other info in a database for later.
		//
		// // YOUR CODE (LATER): When it's time to charge the customer again, retrieve
		// the customer ID.
		// Map<String, Object> chargeParams = new HashMap<String, Object>();
		// chargeParams.put("amount", 1500); // $15.00 this time
		// chargeParams.put("currency", "hkd");
		// chargeParams.put("customer", customerId);
		// Charge charge = Charge.create(chargeParams);
		return false;
	}

	private boolean addOrder(int userId, int userAddressInfoId, String[] amendDetail) throws Exception {
		logger.info("addOrder() start");
		ObjectMapper mapper = new ObjectMapper();

		int orderId;
		try {
			orderId = orderDAO.addOrder(userId, userAddressInfoId);
		} catch (Exception e) {
			logger.error("addOrder() failed with error: " + e.getMessage());
			throw e;
		}

		int successFlag = 0;

		try {
			if (!CommonUtil.isNullOrEmpty(amendDetail)) {
				if (!amendDetail[0].substring(amendDetail[0].length() - 1).equalsIgnoreCase("}")) {
					String modifiedStr = amendDetail[0] + ", " + amendDetail[1];
					JSONShoppingDetailDTO dto = mapper.readValue(modifiedStr, JSONShoppingDetailDTO.class);

					successFlag += shoppingDetailDAO.updateShoppingDetailStatus(userId, dto.getProduct_id(), "H", orderId);
				} else {
					for (int i = 0; i < amendDetail.length; i++) {
						JSONShoppingDetailDTO dto = mapper.readValue(amendDetail[i], JSONShoppingDetailDTO.class);

						successFlag += shoppingDetailDAO.updateShoppingDetailStatus(userId, dto.getProduct_id(), "H",
								orderId);
					}
				}
			}
		}catch (IOException e) {
			logger.error("addOrder() failed with error: " + e.getMessage());
			throw e;
		}
		
		try {
			// If update count = 0 -> it is from product detail-> payment directly
			if (successFlag == 0) {
				if (!amendDetail[0].substring(amendDetail[0].length() - 1).equalsIgnoreCase("}")) {
					String modifiedStr = amendDetail[0] + ", " + amendDetail[1];
					JSONShoppingDetailDTO dto = mapper.readValue(modifiedStr, JSONShoppingDetailDTO.class);

					successFlag += shoppingDetailDAO.addShoppingDetailWithOrder(userId, dto.getProduct_id(),
							dto.getProduct_quantity(), "H", orderId);
				} else {
					for (int i = 0; i < amendDetail.length; i++) {
						JSONShoppingDetailDTO dto = mapper.readValue(amendDetail[i], JSONShoppingDetailDTO.class);

						successFlag += shoppingDetailDAO.addShoppingDetailWithOrder(userId, dto.getProduct_id(),
								dto.getProduct_quantity(), "H", orderId);
					}
				}
			}
		}catch (IOException e) {
			logger.error("addOrder() failed with error: " + e.getMessage());
			throw e;
		}
		

		if (successFlag == 0) {
			logger.info("addOrder() failed with error: successFlag = 0");
			return false;
		} else {
			logger.info("addOrder() success");
			return true;
		}

	}
}
