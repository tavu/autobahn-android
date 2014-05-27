package autobahn.android.utils;

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

    public AutobahnClientException(Error e, String str) {
        super(str);
        error = e;
    }

    public Error getError() {
        return error;
    }


    enum Error {
        INVALID_PARAM,
        UNKNOWN,
        NO_LOG_IN,
        STATUS_ERR
    }


}
