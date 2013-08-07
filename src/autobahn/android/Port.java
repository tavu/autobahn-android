package autobahn.android;


public class Port {

    private String id;
    private String domain;
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Port() {
        name=null;
        id=null;
        name=null;
    }

    public String getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public String name() {
        if(name==null){
            return id;
        }
        return name;
    }

}
