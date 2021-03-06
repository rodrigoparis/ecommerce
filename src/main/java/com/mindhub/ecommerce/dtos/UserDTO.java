package com.mindhub.ecommerce.dtos;

import com.mindhub.ecommerce.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String fantasyName;
    private String email;
    private String address;
    private String bankAccountNumber;
    private String imgUrl;
    private List<UserProductDTO> cart = new ArrayList();
    private List<UserProductDTO> historyCart = new ArrayList();
    private String role;

    public UserDTO() {
    }

    public UserDTO(User client) {
        this.fantasyName = client.getFirstName();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.imgUrl = client.getImgUrl();
        this.cart = client.getCurrentCart().stream().map(UserProductDTO::new).collect(Collectors.toList());
        this.historyCart = client.getShoppingHistory().stream().map(UserProductDTO::new).collect(Collectors.toList());
        this.email = client.getEmail();
        this.id = client.getId();
        this.address = client.getAddress();
        this.bankAccountNumber = client.getBankAccountNumber();
        this.role = client.getUserRole().toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFantasyName() {
        return fantasyName;
    }

    public void setFantasyName(String fantasyName) {
        this.fantasyName = fantasyName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<UserProductDTO> getCart() {
        return cart;
    }

    public void setCart(List<UserProductDTO> cart) {
        this.cart = cart;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public List<UserProductDTO> getHistoryCart() {
        return historyCart;
    }

    public void setHistoryCart(List<UserProductDTO> historyCart) {
        this.historyCart = historyCart;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
