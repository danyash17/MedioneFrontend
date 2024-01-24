package bsu.rpact.medionefrontend.pojo;

public class OrderedDoctorSpecialityPojo {
    private Integer order;
    private String speciality;

    public OrderedDoctorSpecialityPojo(Integer order, String speciality) {
        this.order = order;
        this.speciality = speciality;
    }

    public OrderedDoctorSpecialityPojo() {
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
