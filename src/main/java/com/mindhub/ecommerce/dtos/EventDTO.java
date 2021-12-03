package com.mindhub.ecommerce.dtos;

import com.mindhub.ecommerce.models.products.Event;
import com.mindhub.ecommerce.models.users.Agency;
import com.mindhub.ecommerce.models.users.Client;

public class EventDTO {
    private Long productId;
    private Integer points;
    private Double price;
    private String disscountCode;
    private String address;
    private Agency agency;
    private Client client;
    private String artist;
    private Integer maxCapacity;
    private boolean vip;

    public EventDTO() {}
    public EventDTO(Event event) {
        this.productId=event.getProductId();
        this.points=event.getPoints();
        this.price=event.getPrice();
        this.disscountCode=event.getDisscountCode();
        this.address=event.getAddress();
        this.agency=event.getAgency();
        this.client=event.getClient();
        this.artist=event.getArtist();
        this.maxCapacity=event.getMaxCapacity();
        this.vip=event.isVip();
    }

    public Long getProductId() {return productId;}
    public void setProductId(Long productId) {this.productId = productId;}

    public Integer getPoints() {return points;}
    public void setPoints(Integer points) {this.points = points;}

    public Double getPrice() {return price;}
    public void setPrice(Double price) {this.price = price;}

    public String getDisscountCode() {return disscountCode;}
    public void setDisscountCode(String disscountCode) {this.disscountCode = disscountCode;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public Agency getAgency() {return agency;}
    public void setAgency(Agency agency) {this.agency = agency;}

    public Client getClient() {return client;}
    public void setClient(Client client) {this.client = client;}

    public String getArtist() {return artist;}
    public void setArtist(String artist) {this.artist = artist;}

    public Integer getMaxCapacity() {return maxCapacity;}
    public void setMaxCapacity(Integer maxCapacity) {this.maxCapacity = maxCapacity;}

    public boolean isVip() {return vip;}
    public void setVip(boolean vip) {this.vip = vip;}
}
