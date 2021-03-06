package com.mindhub.ecommerce.controllers;


import com.itextpdf.layout.Document;
import com.mindhub.ecommerce.dtos.*;
import com.mindhub.ecommerce.email.EmailServiceImpl;
import com.mindhub.ecommerce.enums.Pension;
import com.mindhub.ecommerce.enums.UserRole;
import com.mindhub.ecommerce.models.*;
import com.mindhub.ecommerce.repositories.ProductRepository;
import com.mindhub.ecommerce.repositories.SalesRepository;
import com.mindhub.ecommerce.repositories.UserRepository;
import com.mindhub.ecommerce.services.PDFServiceImpl;
import com.mindhub.ecommerce.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PDFServiceImpl pdfServiceImpl;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private SalesRepository salesRepo;
    private Object UserProductDTO;

    @GetMapping("/agencies")
    public Set<UserDTO> getAgencies() {
        return userService.getAgencies();
    }

    @GetMapping("/clients")
    public Set<UserDTO> getClients() {
        return userService.getClients();
    }

    @PostMapping("/agencies/new")
    public ResponseEntity<String> createAgency(@RequestParam String email, @RequestParam String password, @RequestParam String imgUrl, @RequestParam String address, @RequestParam String fantasyName) {
        if (email.isBlank() || password.isBlank() || imgUrl.isBlank() || address.isBlank()) {
            return new ResponseEntity<>("No parameter can be blank", HttpStatus.FORBIDDEN);
        }

        if (userService.createAgency(fantasyName, email, password, imgUrl, address)) {
            return new ResponseEntity<String>("Agency created succesfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Something went wrong, please contact our help desk", HttpStatus.CONFLICT);
    }

    @PostMapping("/clients/new")
    public ResponseEntity<String> createClient(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password, @RequestParam String role) {
        if (userRepo.existsByEmail(email)) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            return new ResponseEntity<>("No parameter can be blank", HttpStatus.FORBIDDEN);
        }

        if (userService.createUser(firstName, lastName, email, password, role)) {
            return new ResponseEntity<String>("Client created succesfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Something went wrong, please contact our help desk", HttpStatus.CONFLICT);
    }

    @GetMapping("/clients/current")
    public UserDTO getCurrentClient(Authentication auth) {
        try {
            return new UserDTO(userRepo.findByEmail(auth.getName()).orElse(null));

        } catch (NullPointerException nullPointerException) {
            return null;
        }
    }

    @GetMapping("/clients/{id}")
    public UserDTO getClient(@PathVariable Long id) {
        return userService.getClientById(id);
    }

    @PostMapping("/clients/current/addToCart/event")
    public ResponseEntity<UserProductDTO> addEventToCart(Authentication auth, @RequestParam Long
            eventId, @RequestParam Boolean isVip, @RequestParam Integer attendants) {
        User user = userRepo.findByEmail(auth.getName()).orElse(null);
        Product product = productRepo.findById(eventId).orElse(null);
        if (!(product instanceof Event)) {
            return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.BAD_REQUEST);
        }

        Event event = (Event) product;
        if (user == null) {
            return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.NOT_FOUND);

        }
        UserProductDTO userProductDTO = userService.addEventToClientCart(user, event, isVip, attendants);

        if (userProductDTO != null) {
            return new ResponseEntity<UserProductDTO>(userProductDTO, HttpStatus.CREATED);
        }

        //TODO
        return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.CREATED);
    }

    @PostMapping("/clients/current/addToCart/hotel")
    public ResponseEntity<UserProductDTO> addHotelToCart(Authentication auth, @RequestParam Long hotelId, @RequestParam Integer nights, @RequestParam Integer passangers) {

        User user = userRepo.findByEmail(auth.getName()).orElse(null);
        Product product = productRepo.findById(hotelId).orElse(null);
        if (!(product instanceof Hotel)) {
            return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.BAD_REQUEST);
        }

        Hotel hotel = (Hotel) product;
        if (user == null) {
            return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.NOT_FOUND);

        }
        if (hotel == null) {
            return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.NOT_FOUND);
        }
        UserProductDTO userProductDTO = userService.addHotelToClientCart(user, hotel, nights, passangers);
        //TODO
        if (userProductDTO != null) {
            return new ResponseEntity<UserProductDTO>(userProductDTO, HttpStatus.CREATED);
        }
        return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.CREATED);


    }

    @PostMapping("/clients/current/addToCart/ticket")
    public ResponseEntity<UserProductDTO> addTicketToCart(Authentication auth, @RequestParam Long ticketId, @RequestParam String clase, @RequestParam Integer passengers) {

        User user = userRepo.findByEmail(auth.getName()).orElse(null);
        Product product = productRepo.findById(ticketId).orElse(null);

        if (!(product instanceof Ticket)) {
            return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.BAD_REQUEST);
        }

        Ticket ticket = (Ticket) product;

        if (user == null) {
            return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.NOT_FOUND);
        }
        if (ticket == null) {
            return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.NOT_FOUND);
        }
        UserProductDTO userProductDTO = userService.addTicketToClientCart(user, ticket, clase, passengers);

        if (userProductDTO != null) {
            return new ResponseEntity<UserProductDTO>(userProductDTO, HttpStatus.CREATED);
        }

        return new ResponseEntity<UserProductDTO>((UserProductDTO) null, HttpStatus.CREATED);

        //TODO

    }

    @PostMapping("/clients/current/add1toCart")//sirve para agregar de a 1 producto
    public ResponseEntity<String> add1ProductToCart(Authentication authentication, @RequestParam Long userProductId) {
        User user = userRepo.findByEmail(authentication.getName()).orElse(null);
        UserProduct productToAdd = salesRepo.findById(userProductId).orElse(null);

        if (user == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        if (productToAdd == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }

        if (userService.add1ProductToCart(user, productToAdd)) {
            return new ResponseEntity<>("Agregado con exito", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/clients/current/removeFromCart")  //sirve para eliminar producto de 1 en 1
    public ResponseEntity<String> removeProductFromCart(Authentication auth, @RequestParam Long userProductId) {

        User user = userRepo.findByEmail(auth.getName()).orElse(null);
        UserProduct productToRemove = salesRepo.findById(userProductId).orElse(null);

        if (user == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        if (productToRemove == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }

        if (userService.removeProductFromCart(user, productToRemove)) {
            return new ResponseEntity<String>("Product successfully removed", HttpStatus.CREATED);
        }

        return new ResponseEntity<String>("Product removal unsuccesful", HttpStatus.CREATED);


    }

    @PostMapping("/clients/current/finalRemoveFromCart")  //sirve para eliminar definitivamente todo un producto
    public ResponseEntity<String> finalRemoveFromCart(Authentication auth, @RequestParam Long userProductId) {

        User user = userRepo.findByEmail(auth.getName()).orElse(null);
        UserProduct productToRemove = salesRepo.findById(userProductId).orElse(null);

        if (user == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        if (productToRemove == null) {
            return new ResponseEntity<String>("Product not found", HttpStatus.NOT_FOUND);
        }

        if (userService.finalRemoveProductFromCart(user, productToRemove)) {
            return new ResponseEntity<String>("Product successfully removed", HttpStatus.CREATED);
        }

        return new ResponseEntity<String>("Product removal unsuccesful", HttpStatus.CREATED);


    }

    @PostMapping("/clients/current/sendInvoice")
    public ResponseEntity<String> sendInvoice(Authentication auth, HttpServletResponse response) throws IOException {

        User user = userRepo.findByEmail(auth.getName()).orElse(null);

        if (user == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }

        Set<UserProductDTO> shoppingBag = user.getCurrentCart().stream().map(UserProductDTO::new).collect(Collectors.toSet());

        ByteArrayOutputStream outPutStream = pdfServiceImpl.generatePDF(response, user, shoppingBag);
        byte[] bytes = outPutStream.toByteArray();

        if (userService.sendInvoice(user, bytes)) {
            return new ResponseEntity<String>("Invoice succesfully sent", HttpStatus.CREATED);
        }

        return new ResponseEntity<String>("Something wrong happened", HttpStatus.BAD_REQUEST);


    }

    @PatchMapping("/clients/current/updatePicture")
    public ResponseEntity<String> modifyUserDetails(Authentication auth, @RequestParam String imgUrl) {
        //TODO MODIFY CLIENT
        User user = userRepo.findByEmail(auth.getName()).orElse(null);
        if (user != null) {
            if (userService.updatePic(user, imgUrl))
                return new ResponseEntity<String>("We just updated your picture", HttpStatus.OK);
        }


        return new ResponseEntity<String>("Something went wrong", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/clients/current/endPurchase")
    public ResponseEntity<String> endPurchase(Authentication auth, HttpServletResponse response) throws IOException {
        User user = userRepo.findByEmail(auth.getName()).orElse(null);

        if (user == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        Set<UserProductDTO> shoppingBag = user.getCurrentCart().stream().map(UserProductDTO::new).collect(Collectors.toSet());

        ByteArrayOutputStream outPutStream = pdfServiceImpl.generatePDF(response, user, shoppingBag);
        byte[] bytes = outPutStream.toByteArray();

        userService.sendInvoice(user, bytes);

        if (userService.createHistoryCart(user)) {
            return new ResponseEntity<String>("Shopping history successful", HttpStatus.OK);

        }

        return new ResponseEntity<String>("Something wrong happened", HttpStatus.NOT_FOUND);

    }

    @PostMapping("/help")
    public ResponseEntity<String> helpMessage(String firstName, String lastName, String email, String country, String comment) {

        if (userService.sendHelpMesagge(firstName, lastName, email, country, comment)) {
            return new ResponseEntity<String>("Thanks for your contact, we will contact you as soon as possible", HttpStatus.OK);

        }
        return new ResponseEntity<String>("Something wrong happened", HttpStatus.NOT_FOUND);

    }


}
