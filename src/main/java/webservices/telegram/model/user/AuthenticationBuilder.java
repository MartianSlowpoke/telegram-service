package webservices.telegram.model.user;

public class AuthenticationBuilder {
	
	protected Long userId;
	protected String email;
	protected String password;

	public AuthenticationBuilder() {

	}
	
	public AuthenticationBuilder userId(Long userId) {
		this.userId = userId;
		return this;
	}

	public AuthenticationBuilder email(String email) {
		this.email = email;
		return this;
	}

	public AuthenticationBuilder password(String password) {
		this.password = password;
		return this;
	}

	public Authentication build() {
		return new Authentication(this);
	}

}
