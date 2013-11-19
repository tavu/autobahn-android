package autobahn.android;

import android.content.Context;

public class AutobahnClientException extends Exception {

	private Error error = Error.UNKNOWN;
	private int status;

	public AutobahnClientException() {
		super();
		error = Error.UNKNOWN;
		status = -1;
	}

	public AutobahnClientException(String error) {
		super(error);
	}

	public AutobahnClientException(Error e) {
		super();
		error = e;
	}

	public AutobahnClientException(int status) {
		super();
		error = Error.STATUS_ERR;
		this.status = status;
	}

	int getStatus() {
		return status;
	}

	public Error getError() {
		return error;
	}

	public String getVisibleMsg(Context context) {
		//TODO return a visible string for toast message
		return new String();
	}

	enum Error {
		INVALID_PARAM,
		UNKNOWN,
		NO_LOG_IN,
		STATUS_ERR

	}


}
