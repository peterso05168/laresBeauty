package controller;

import dao.LoginDAO;
import dao.UserDAO;
import jsonobject.JSONObject;
import util.CommonUtil;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bean.UserAddress;
import bean.UserLocalAuth;

@RequestMapping(value = "user")

@RestController
public class UserController {

	private static final Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	UserDAO userDAO;

	@Autowired
	LoginDAO loginDAO;

	@RequestMapping(value = "get_user_address", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject getUserAddress(@RequestParam(value = "user_id") Integer userId) {
		logger.info("getUserAddress() started with userId = " + userId);
		JSONObject jsonObject = new JSONObject();
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
	public JSONObject deleteUserAddress(@RequestParam(value = "user_address_info_id") Integer userAddressInfoId) {
		logger.info("deleteUserAddress() started with userAddressInfoId = " + userAddressInfoId);
		JSONObject jsonObject = new JSONObject();
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
	public JSONObject changeDefaultUserAddress(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "user_address_info_id") Integer userAddressInfoId) {
		logger.info("changeDefaultUserAddress() started with userId = " + userId + ", userAddressInfoId = " + userAddressInfoId);
		JSONObject jsonObject = new JSONObject();
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
			jsonObject.setDetail("Fail to delete item due to : " + e.getMessage());
			logger.error("changeDefaultUserAddress() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}

	@RequestMapping(value = "changePassword", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONObject changePassword(@RequestParam(value = "user_id") Integer userId,
			@RequestParam(value = "password") String password, @RequestParam(value = "new_password") String newPassword)
			throws NoSuchAlgorithmException {
		logger.info("changePassword() started with userId = " + userId + ", password = " + password + ", newPassword = " + newPassword);
		JSONObject jsonObject = new JSONObject();
		List<UserLocalAuth> getUser = loginDAO.getLocalUserById(userId);
		String salt = getUser.get(0).getSalt();
		String username = getUser.get(0).getUsername();
		String hashedPassword = CommonUtil.SHA512Hashing(password, salt);
		List<UserLocalAuth> userLocalAuth = loginDAO.localAuth(username, hashedPassword);
		if (!CommonUtil.isNullOrEmpty(userLocalAuth) && userLocalAuth.size() == 1) {
			String newSalt = CommonUtil.getSalt();
			String newHashPassword = CommonUtil.SHA512Hashing(newPassword, newSalt);
			int successFlag = loginDAO.updateLocalUserPassword(userId, newHashPassword, newSalt);
			if (successFlag == 1) {
				jsonObject.setCode("S");
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("Failed to change password,please contact support");
			}
		} else {
			jsonObject.setCode("F");
			jsonObject.setDetail("Wrong Password");
		}
		return jsonObject;
	}
}