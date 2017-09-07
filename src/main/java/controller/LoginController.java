package controller;

import dao.ProductDAO;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bean.Post;

@RestController
public class LoginController {
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public void login(HttpServletResponse response) throws IOException {
		response.sendRedirect("http://localhost:8080/laresBeauty/posts");
	}
}
