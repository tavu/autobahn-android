package autobahn.android;

public class AutobahnClientException extends Exception {
    public AutobahnClientException() {
        super();
    }

    public AutobahnClientException(String error) {
        super(error);
        status=0;
    }

    public AutobahnClientException(int status,String error) {
        super(error);
        this.status=status;
    }

    public String getMessage() {
        String ret=new String();

        if(status!=0 && status != 200) {
            ret+="Error:"+status;
        }

        String s=super.getMessage();

        if(s!=null) {
            if(!ret.isEmpty()) {
                ret += System.getProperty("line.separator");
            }
            ret+=s;
        }
        if(ret.isEmpty()) {
            ret=null;
        }

        return ret;
    }

    private int status;
}
