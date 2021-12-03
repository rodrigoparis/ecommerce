package com.mindhub.ecommerce.dtos;

import com.mindhub.ecommerce.models.products.Event;
import com.mindhub.ecommerce.models.products.Hotel;
import com.mindhub.ecommerce.models.products.Product;
import com.mindhub.ecommerce.models.products.Ticket;
import com.mindhub.ecommerce.models.ClientProduct;

public class ClientProductDTO {
    private long id;
    private String productName;
    private double productPrice;
    private Integer points;
    private String disscountCode;
    private String address;
    private EventDTO eventDTO;
    private HotelDTO hotelDTO;
    private TicketDTO ticketDTO;


    public ClientProductDTO() {}
    public ClientProductDTO(ClientProduct clientProduct) {
        Product product = clientProduct.getProduct();

        this.id= product.getProductId();
        this.productName=product.getName();
        this.productPrice=product.getPrice();
        this.points = product.getPoints();
        this.disscountCode = product.getDisscountCode();
        this.address = product.getAddress();

        if (product instanceof Event){
            this.eventDTO = new EventDTO((Event) product);
        } else if( product instanceof Hotel) {
            this.hotelDTO  = new HotelDTO((Hotel) product);
        } else if (product instanceof Ticket){
            this.ticketDTO = new TicketDTO((Ticket) product);
        }

    }
}
