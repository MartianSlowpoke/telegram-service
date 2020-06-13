package webservices.telegram.exception;

public enum DaoIllegalException {

	NOT_FOUND_BY_USER_ID {
		@Override
		public String toString() {
			return "a user doesn't exist";
		}
	},

	NO_USERS {
		@Override
		public String toString() {
			return "there are not registrated users in the system";
		}
	},

	NOT_FOUND_BY_CREDENTIALS {
		@Override
		public String toString() {
			return "a user doesn't exist with such credentials";
		}
	},

	EMPTY_CREDENTIALS {
		@Override
		public String toString() {
			return "the credentials you have given are not completed";
		}
	};

	public static DaoIllegalException fromMessage(String message) {
		for (DaoIllegalException exception : DaoIllegalException.values()) {
			if (exception.toString().equals(message))
				return exception;
		}
		throw new IllegalArgumentException("there is DaoException with such message : " + message);
	}

}
