package com.mindhub.ecommerce.models.products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindhub.ecommerce.models.ClientProduct;
import com.mindhub.ecommerce.models.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "idsGenerator")
//    @TableGenerator(name = "idsGenerator.product", table = "ProducstIdsGenerator",
//            pkColumnName = "id", pkColumnValue = "Product", valueColumnName = "productsIds")
    @Column(name = "id", unique = true, nullable = false)
    private Long productId;
    private Integer points;
    private Double price;
    private String disscountCode;
    private String address;
    private String name;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id")
    private User agency;

    @OneToMany(mappedBy = "product")
    private Set<ClientProduct> clientProducts = new HashSet();

    //TODO:Revisar si nos conviene hacer una tabla intermedia productClient por la relación ManyToMany
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "client_id")
//    private Client productClient;


    public Product() {
    }

    public Product(Integer points, Double price, String disscountCode, String address, User agency, String name) {
        this.points = points;
        this.price = price;
        this.disscountCode = disscountCode;
        this.address = address;
        this.agency = agency;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDisscountCode() {
        return disscountCode;
    }

    public void setDisscountCode(String disscountCode) {
        this.disscountCode = disscountCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonIgnore
    public User getAgency() {
        return agency;
    }


    public void setAgency(User agency) {
        this.agency = agency;
    }

    public Set<ClientProduct> getClientProducts() {
        return clientProducts;
    }

    public void setClientProducts(Set<ClientProduct> clientProducts) {
        this.clientProducts = clientProducts;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", points=" + points +
                ", price=" + price +
                ", disscountCode='" + disscountCode + '\'' +
                '}';
    }
}
