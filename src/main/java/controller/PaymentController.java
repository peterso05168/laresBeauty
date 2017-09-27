package controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

import dao.LoginDAO;
import jsonobject.JSONResult;

@RequestMapping(value = "payment")

@CrossOrigin
@RestController
public class PaymentController {

	@Autowired
	LoginDAO loginDAO;

	@RequestMapping(value = "/checkout")
	public JSONResult checkout(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "price") String price, @RequestParam(value = "stripeToken") String stripeToken)
			throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		// Set your secret key: remember to change this to your live secret key in
		// production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = "sk_test_7SPSsD2SkPABEZlWdqgQaMVa";
		JSONResult chargeObject = new JSONResult();
		// Token is created using Stripe.js or Checkout!
		// Get the payment token ID submitted by the form:
		// String token = request.getParameter("stripeToken");

		// Create a Customer:
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("email", "ericqwerty0@gmail.com");
		customerParams.put("source", stripeToken);
		Customer customer = Customer.create(customerParams);

		// Charge the Customer instead of the card:
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", price);
		chargeParams.put("currency", "hkd");
		chargeParams.put("customer", customer.getId());
		Charge charge = Charge.create(chargeParams);
		if (charge.getPaid()) {
			// charge is success and access orderDao
			chargeObject.setCode("s");
			;
			chargeObject.setDetail("charge success");
		} else {
			chargeObject.setCode("f");
			chargeObject
					.setDetail("charge failed.Error: " + charge.getFailureCode() + " " + charge.getFailureMessage());
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
		return chargeObject;
	}
}
