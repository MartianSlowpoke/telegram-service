package webservices.telegram.controller;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttribute;

import webservices.telegram.dao.user.UserDAO;
import webservices.telegram.dto.user.UserRegistrationRequest;
import webservices.telegram.exception.UserDaoException;
import webservices.telegram.model.user.Authentication;
import webservices.telegram.model.user.User;
import webservices.telegram.model.user.UserPhoto;

@Controller("user-controller")
@RequestMapping("users")
public class UserController {

	private final UserDAO userDAO;

	public UserController(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@RequestMapping(method = RequestMethod.GET, value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public User getUserById(@PathVariable("id") Long id) throws IllegalArgumentException, UserDaoException {
		User user = userDAO.get(id);
		return user;
	}

	@RequestMapping(method = RequestMethod.GET, value = "me", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public User getMeUser(@SessionAttribute("user") User user) throws IllegalArgumentException, UserDaoException {
		User me = userDAO.get(user.getId());
		return me;
	}

	@RequestMapping(method = RequestMethod.GET, value = "{id}/photo", produces = { MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE })
	@ResponseBody
	public UserPhoto getUserPhoto(@PathVariable("id") Long userId) throws IllegalArgumentException, UserDaoException {
		UserPhoto photo = userDAO.getPhoto(userId);
		return photo;
	}

	@RequestMapping(method = RequestMethod.GET, value = "me/auth", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Authentication getUserAuth(@SessionAttribute("user") User user)
			throws IllegalArgumentException, UserDaoException {
		Authentication authentication = userDAO.getAuth(user.getId());
		return authentication;
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/", "" }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ResponseStatus(code = HttpStatus.CREATED)
	public User registration(HttpServletRequest request, @RequestBody UserRegistrationRequest user)
			throws IllegalArgumentException, UserDaoException {
		user.getUser().setCreatedAt(Instant.now());
		userDAO.add(user.getUser(), user.getAuth());
		request.getSession(true).setAttribute("user", user.getUser());
		return user.getUser();
	}

	@RequestMapping(method = RequestMethod.PUT, value = "me", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public User updateInfo(@SessionAttribute(name = "user", required = true) User initiator, @RequestBody User updated)
			throws IllegalArgumentException, UserDaoException {
		updated.setId(initiator.getId());
		userDAO.update(updated);
		return userDAO.get(initiator.getId());
	}

	@RequestMapping(method = RequestMethod.PUT, value = "me/auth", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void updateAuth(@SessionAttribute(name = "user", required = true) User initiator,
			@RequestBody Authentication updatedAuth) throws IllegalArgumentException, UserDaoException {
		updatedAuth.setUserId(initiator.getId());
		userDAO.update(updatedAuth);
	}

}
