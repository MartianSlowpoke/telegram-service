package webservices.telegram.websocket;

import webservices.telegram.exception.UserDaoException;
import webservices.telegram.model.user.User;

public interface UserStateConnectionListener {

	public void onOpen(User user) throws IllegalArgumentException, UserDaoException;

	public void onClose(User user) throws UserDaoException;

}
