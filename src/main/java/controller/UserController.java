package controller;

import dao.LoginDAO;
import dao.UserDAO;
import jsonobject.JSONResult;
import util.CommonUtil;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import bean.UserAddress;
import bean.UserLocalAuth;

@RequestMapping(value = "user")

@CrossOrigin
@Transactional("tjtJTransactionManager")
@RestController
public class UserController {

	private static final Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	UserDAO userDAO;

	@Autowired
	LoginDAO loginDAO;

	@RequestMapping(value = "add_user_address", method = RequestMethod.POST)
	public JSONResult addUserAddress(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "recipient_name") String recipientName,
			@RequestParam(value = "recipient_tel") String recipientTel,
			@RequestParam(value = "recipient_address") String recipientAddress) {

		logger.info("addUserAddress() started with userId = " + userId + ", recipientName = " + recipientName
				+ ", recipientTel = " + recipientTel + ", recipientAddress = " + recipientAddress);
		JSONResult jsonObject = new JSONResult();

		try {

			int successFlag = 0;

			successFlag += userDAO.addAddress(userId, recipientName, recipientTel, recipientAddress);

			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Add address failed.");
				logger.error("addUserAddress() failed");
			} else {
				jsonObject.setCode("S");
				logger.info("addUserAddress() success");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Add address failed due to : " + e.getMessage());
			logger.error("addUserAddress() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}
	
	@RequestMapping(value = "update_user_address", method = RequestMethod.POST)
	public JSONResult updateUserAddress(@RequestParam(value = "user_address_info_id") Integer userAddressInfoId,
			@RequestParam(value = "recipient_name") String recipientName,
			@RequestParam(value = "recipient_tel") String recipientTel,
			@RequestParam(value = "recipient_address") String recipientAddress) {

		logger.info("updateUserAddress() started with userAddressInfoId = " + userAddressInfoId + ", recipientName = " + recipientName
				+ ", recipientTel = " + recipientTel + ", recipientAddress = " + recipientAddress);
		JSONResult jsonObject = new JSONResult();

		try {

			int successFlag = 0;

			successFlag += userDAO.updateAddress(userAddressInfoId, recipientName, recipientTel, recipientAddress);

			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Add address failed.");
				logger.error("updateUserAddress() failed");
			} else {
				jsonObject.setCode("S");
				logger.info("updateUserAddress() success");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Add address failed due to : " + e.getMessage());
			logger.error("updateUserAddress() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}
	
	@RequestMapping(value = "get_user_address", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResult getUserAddress(@RequestParam(value = "user_id") Integer userId) {
		logger.info("getUserAddress() started with userId = " + userId);
		JSONResult jsonObject = new JSONResult();
		try {
			List<UserAddress> userAddressList = userDAO.getUserAddress(userId);
			if (!CommonUtil.isNullOrEmpty(userAddressList)) {
				jsonObject.setCode("S");
				jsonObject.setData(userAddressList);
				logger.info("getUserAddress() success with return value = " + userAddressList);
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("No result is found.");
				logger.error("getUserAddress() failed with error: no result is found");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured : " + e.getMessage());
			logger.error("getUserAddress() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "delete_user_address", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResult deleteUserAddress(@RequestParam(value = "user_address_info_id") Integer userAddressInfoId) {
		logger.info("deleteUserAddress() started with userAddressInfoId = " + userAddressInfoId);
		JSONResult jsonObject = new JSONResult();
		int successFlag = 0;
		try {
			successFlag = userDAO.deleteUserAddress(userAddressInfoId);
			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Delete item failed, possible due to wrong user_id or user_address_info_id.");
				logger.error("deleteUserAddress() failed with error: wrong user_id or user_address_info_id");
			} else {
				jsonObject.setCode("S");
				logger.info("deleteUserAddress() success");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to delete item due to : " + e.getMessage());
			logger.error("deleteUserAddress() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "change_default_user_address", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResult changeDefaultUserAddress(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "user_address_info_id") Integer userAddressInfoId) {
		logger.info("changeDefaultUserAddress() started with userId = " + userId + ", userAddressInfoId = " + userAddressInfoId);
		JSONResult jsonObject = new JSONResult();
		int successFlag = 0;
		try {
			successFlag = userDAO.changeDefaultUserAddress(userId, userAddressInfoId);
			if (successFlag == 0) {
				jsonObject.setCode("F");
				jsonObject.setDetail("Delete item failed, possible due to wrong user_id or user_address_info_id.");
				logger.error("changeDefaultUserAddress() failed with error: wrong user_id or user_address_info_id"); 
			} else {
				jsonObject.setCode("S");
				logger.info("changeDefaultUserAddress() success");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Fail to change item due to : " + e.getMessage());
			logger.error("changeDefaultUserAddress() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "change_password", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResult changePassword(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "password") String password, @RequestParam(value = "new_password") String newPassword)
			throws NoSuchAlgorithmException {
		logger.info("changePassword() started with userId = " + userId + ", password = " + password + ", newPassword = " + newPassword);
		JSONResult jsonObject = new JSONResult();
		List<UserLocalAuth> getUser = loginDAO.getLocalUserById(userId);
		//if user is not null
		String salt = getUser.get(0).getSalt();
		String username = getUser.get(0).getUsername();
		String hashedPassword = CommonUtil.SHA512Hashing(password, salt);
		List<UserLocalAuth> userLocalAuth = loginDAO.localAuth(username, hashedPassword);
		if (!CommonUtil.isNullOrEmpty(userLocalAuth) && userLocalAuth.size() == 1) {
			String newSalt = CommonUtil.randomCharSalt();
			String newHashPassword = CommonUtil.SHA512Hashing(newPassword, newSalt);
			int successFlag = loginDAO.updateLocalUserPassword(userId, newHashPassword, newSalt);
			if (successFlag == 1) {
				jsonObject.setCode("S");
				logger.info("changePassword() success");
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("Failed to change password, please contact support");
				logger.error("changePassword() failed with error: unknown");
			}
		} else {
			jsonObject.setCode("F");
			jsonObject.setDetail("Wrong Password");
			logger.error("changePassword() failed with error: wrong password entered");
		}
		return jsonObject;
	}
}