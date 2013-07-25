package autobahn.android;


public class Port {

    private String id;
    private String domain;
    private String name;

    public Port(String id,String domain,String name) {
        this.id=id;
        this.domain=domain;
        name=name.trim();
        if(name.length()==0 ) {
            this.name=null;
        }
        else {
            this.name=name;
        }
    }

    public Port(String id,String domain) {
        this.id=id;
        this.domain=domain;
        this.name=null;
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
