package bsu.rpact.medionefrontend.pojo.other;

public class Country {
    private String name;
    private String code;
    private String region;
    private String flag;

    public Country(String name, String code, String region, String flag) {
        this.name = name;
        this.code = code;
        this.region = region;
        this.flag = flag;
    }

    public Country() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
