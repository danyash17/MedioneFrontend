package bsu.rpact.medionefrontend.pojo;

import java.sql.Timestamp;

public class OrderedVisitPojo {
    private Integer order;
    private Timestamp datetime;
    private String actor;
    private Integer actorId;

    public OrderedVisitPojo(Integer order, Timestamp datetime, String actor, Integer actorId) {
        this.order = order;
        this.datetime = datetime;
        this.actor = actor;
        this.actorId = actorId;
    }

    public OrderedVisitPojo() {
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public Integer getActorId() {
        return actorId;
    }

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }
}
