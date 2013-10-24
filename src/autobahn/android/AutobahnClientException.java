package autobahn.android;

import android.content.Context;

public class AutobahnClientException extends Exception {

    enum Error {
        INVALID_PARAM,
        UNKNOWN,
        NO_LOG_IN

    }

    public AutobahnClientException() {
		super();
        error=Error.UNKNOWN;
	}

	public AutobahnClientException(String error) {
		super(error);
	}

    public AutobahnClientException(Error e) {
        super();
        error=e;
    }

    private Error error=Error.UNKNOWN;

    public Error getError() {
        return error;
    }

    public String getVisibleMsg(Context context) {
        //TODO return a visible string for toast message
        return new String();

    }


}
