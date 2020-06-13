package webservices.telegram.dao.user;

import java.util.Collection;

import webservices.telegram.exception.UserDaoException;
import webservices.telegram.exception.user.UserNotFoundException;
import webservices.telegram.model.user.Authentication;
import webservices.telegram.model.user.User;
import webservices.telegram.model.user.UserPhoto;

public interface UserDAO {

	public void add(User user, Authentication authentication) throws UserDaoException, IllegalArgumentException;

	public User logIn(Authentication authentication) throws UserDaoException, IllegalArgumentException;

	public User get(Long id) throws UserDaoException, UserNotFoundException;

	public User get(String login) throws UserDaoException, IllegalArgumentException;

	public Authentication getAuth(Long userId) throws UserDaoException, IllegalArgumentException;

	public Collection<User> getAll(String loginMatch) throws UserDaoException, IllegalArgumentException;

	public Collection<User> getAll() throws UserDaoException, IllegalArgumentException;

	public UserPhoto getPhoto(Long userId) throws UserDaoException, IllegalArgumentException;

	public boolean existEmail(String email) throws UserDaoException, IllegalArgumentException;

	public boolean existLogin(String login) throws UserDaoException, IllegalArgumentException;

	public void update(Authentication authentication) throws UserDaoException, IllegalArgumentException;

	public void update(User user) throws UserDaoException, IllegalArgumentException;

	public void updateOnlineStatus(User user) throws UserDaoException, IllegalArgumentException;

	public void delete(User user) throws UserDaoException, IllegalArgumentException;

}
