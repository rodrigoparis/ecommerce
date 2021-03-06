
const app = Vue.createApp({
  data() {
    return {
      hotels: [],
      tickets: [],
      events: [],
      hotelsCantTres: [],
      ticketsCantTres: [],
      eventsCantTres: [],
      productToFilter: "",
      filtrado: "",
      filtradoHotel: "",
      filtradoEvent: "",
      filtradoTicket: "",
      includes:"",

      clase: "",
      passengers: 1,
      ticket: true,

      event: true,
      vip: true,
      personAmount: 1,

      hotel: true,
      parking: false,
      concierge: false,
      beds: 1,
      nights: 1,

      
      cart: [],
      currentUser: [],

      email: "",
      password: "",
      firstName: "",
      lastName: "",
      roleUser: "",
      isPasswordVisible: false,
      isAdmin: false,
      isClient: false

    }
  },
  created() {
    axios.get("/api/clients/current")
      .then(response => {
        console.log(response.data)
        this.currentUser = response.data
        if (response.data.role == "CLIENT") {
          this.isClient = true;
          this.isAdmin = false;
        } else if (response.data.role == "AGENCY") {
          this.isClient = false;
          this.isAdmin = true;
        }
      })

    axios.get('/api/products/hotels')
      .then(response => {
        console.log(response.data)
        this.hotels = response.data
        filtradoParaCard(response.data)
      })
      .catch(error => {
        return error.message;
      })
    axios.get('/api/products/tickets')
      .then(response => {
        console.log(response.data)
        this.tickets = response.data
        filtradoParaCard(response.data)
      })
      .catch(error => {
        return error.message;

      })

    axios.get('/api/products/events')
      .then(response => {
        console.log(response.data)
        this.events = response.data
        filtradoParaCard(response.data)
      })
      .catch(error => {
        return error.message;
      })
    if (sessionStorage.getItem('cart')) {
      try {
        this.cart = JSON.parse(sessionStorage.getItem('cart'));
      } catch (e) {
        sessionStorage.removeItem('cart');
      }
      console.log(this.cart)
    };

  },
  computed: {
    showPassword() {
      if (this.isPasswordVisible) {
        return "text";
      } else {
        return "password";
      }
    },
    filteredHotels() {
      return this.hotels.filter(hotel => hotel.description.toLowerCase().includes(this.filtradoHotel.toLowerCase())).slice(0, 3)
    },
    filteredEvents() {
      return this.events.filter(hotel => hotel.description.toLowerCase().includes(this.filtradoEvent.toLowerCase())).slice(0, 3)
    },
    filteredTickets() {
      return this.tickets.filter(hotel => hotel.description.toLowerCase().includes(this.filtradoTicket.toLowerCase())).slice(0, 3)
    },
    filteredRef() {
      return `#${this.productToFilter}`;
    }
  },
  methods: {
    hotelShow(product) {
      this.hotel = product;
      console.log(product);
    },
    haveParking(hotel) {
      if (hotel == true) {
        return "Yes"
      } else {
        return "No"
      }
    },
    savingCart() {
      const parsed = JSON.stringify(this.cart);
      sessionStorage.setItem('cart', parsed);
    },
    flyShow(product) {
      this.ticket = product;
    },
    activityShow(product) {
      this.ticket = product;
      console.log(this.ticket)
    },
    addTicket(ticket) {
      if (!(this.isAdmin || this.isClient)) {
        swal("Please Log in or Sign Up to proceed with your purchase!", {
          title: "It seems that you are not logged in",
          buttons: ["Maybe next time!", "I want to Log In!"],
          icon: "info"
        }).then(res => {
          if (res) {
            this.$refs.loginModal.click();
          }
        })
      } else {
        axios.post("/api/clients/current/addToCart/ticket", `ticketId=${ticket.productId}&clase=${this.clase}&passengers=${this.passengers}`)
          .then(resp => {
            ticket = resp.data;
            console.log(ticket);
            if (ticket.stock <= 0) {
              alert("No stock");
            } else {
              ticket.stock--;
              console.log(this.cart);
              if (this.cart.some(prod => prod.id == ticket.id)) {
                let id = this.cart.findIndex(prod => prod.id == ticket.id);
                //actualizar cantidad en ese producto
                this.cart[id].quantity = ticket.quantity;
              } else {
                ticket.quantity = this.passengers;
                ticket.clase = this.clase;
                ticket.subtotal = ticket.quantity * ticket.price;
                this.cart.push(ticket);
                console.log(this.cart);
              }
              this.savingCart();
              swal(`We just added your ticket to your cart!`, {
                buttons: ["Great!", "Take me to my cart"],
                icon: "success"
            })
                .then(res => {
                    if (res) {
                        window.location.href = "./cart.html"
                    }
                })
            }
          })
          .catch(err => console.log(err));
      }
    },
    addEvent(ticket) {
      if (!(this.isAdmin || this.isClient)) {
        swal("Please Log in or Sign Up to proceed with your purchase!", {
          title: "It seems that you are not logged in",
          buttons: ["Maybe next time!", "I want to Log In!"],
          icon: "info"
        }).then(res => {
          if (res) {
            this.$refs.loginModal.click();
          }
        })
      } else {
        axios.post("/api/clients/current/addToCart/event", `eventId=${this.ticket.productId}&isVip=${true}&attendants=${this.personAmount}`)
          .then(resp => {
            ticket = resp.data;
            console.log(ticket);
            if (ticket.stock <= 0) {
              alert("No stock");
            } else {
              ticket.stock--;
              console.log(this.cart);
              if (this.cart.some(prod => prod.id == ticket.id)) {
                let id = this.cart.findIndex(prod => prod.id == ticket.id);
                console.log(id);
                //actualizar cantidad en ese producto
                this.cart[id].quantity = this.cart[id].quantity + this.personAmount;
              } else {
                this.event.quantity = this.personAmount;
                ticket.subtotal = this.event.quantity * this.event.price;
                this.cart.push(ticket);
                console.log(this.cart);
              }
              this.savingCart();
              swal(`We just added your event to your cart!`, {
                buttons: ["Great!", "Take me to my cart"],
                icon: "success"
            })
                .then(res => {
                    if (res) {
                        window.location.href = "./cart.html"
                    }
                })
            }
          })
          .catch(err => console.log(err));
      }
    },
    addHotel(hotel) {
      if (!(this.isAdmin || this.isClient)) {
        swal("Please Log in or Sign Up to proceed with your purchase!", {
          title: "It seems that you are not logged in",
          buttons: ["Maybe next time!", "I want to Log In!"],
          icon: "info"
        }).then(res => {
          if (res) {
            this.$refs.loginModal.click();
          }
        })
      } else {
        axios.post("/api/clients/current/addToCart/hotel", `hotelId=${this.hotel.productId}&nights=${this.nights}&passangers=${this.beds}`)
          .then(resp => {
            hotel = resp.data;
            console.log(hotel);
            if (hotel.stock <= 0) {
              alert("No stock");
            } else {
              hotel.stock--;
              console.log(this.cart);
              if (this.cart.some(prod => prod.id == hotel.id)) {
                let id = this.cart.findIndex(prod => prod.id == hotel.id);
                //actualizar cantidad en ese producto
                this.cart[id].quantity += this.beds * this.nights;
              } else {
                hotel.quantity = this.beds * this.nights;
                hotel.clase = this.clase;
                hotel.subtotal = hotel.quantity * hotel.price;
                this.cart.push(hotel);
                console.log(this.cart);
              }
              this.savingCart();
              swal(`We just added your hotel to your cart!`, {
                buttons: ["Great!", "Take me to my cart"],
                icon: "success"
            })
                .then(res => {
                    if (res) {
                        window.location.href = "./cart.html"
                    }
                })
            }
          })
          .catch(err => console.log(err));
      }
    },
    totalProductsInCart() {
      const array = this.cart
      function reducer(previous, current, index, array) {
        const product = array[index];
        const returns = previous + (parseInt(product.quantity));
        return returns;
      }
      return array.reduce(reducer, 0);
    },
    totalPriceCalc() {
      const array = this.cart
      function reducer(previous, current, index, array) {
        const product = array[index];
        const returns = previous + (product.finalPrice * product.quantity);
        return returns;
      }
      return array.reduce(reducer, 0);
    },
    login(e) {
      if (e) {
        e.preventDefault()
      }
      if (!this.validEmail(this.email)) {
        this.validMailLogIn = false
        return
      } else {
        this.validMailLogIn = true
      }

      axios.post('/api/login', `email=${this.email}&password=${this.password}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
        .then(response => {
          window.location.reload()
        })
        .catch(error => {
                   swal({
                      title: "Something went wrong!",
                      text: "Wrong email or password, please check your credentials.",
                      buttons: "Got it!",
                      icon: "error"
                    })
        })
    },
    logout() {
      axios.post('/api/logout')
        .then(response => {
          sessionStorage.removeItem('cart'); //alerta de exito
          console.log("loged out!");
          this.isClient = false;
          this.isAdmin = false;
          window.location.reload();

        })
        .catch(error => {
          console.log('Error', error.message);
        })
    },
    filter() {
      if (this.productToFilter == "events") {
        this.filtradoEvent = this.filtrado;
      } else if (this.productToFilter == "tickets") {
        this.filtradoTicket = this.filtrado;
      } else if (this.productToFilter == "hotels") {
        this.filtradoHotel = this.filtrado;
      }
    },
    validEmail(email) {
      var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    },
    removeOne(prod) {
      console.log(prod);
      console.log(typeof prod.id)
      let findProd = this.cart.findIndex(product => product.id == prod.id);
      console.log(findProd);
      axios.post("/api/clients/current/removeFromCart", `userProductId=${prod.id}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
        .then(resp => {
          console.log(findProd)
          this.cart[findProd].quantity--;
          if (this.cart[findProd].quantity <= 0) { this.cart.splice(findProd, 1) }
          this.savingCart();
          if (this.cart.length == 0) {
            sessionStorage.removeItem('cart');
          }
        }).catch(err => console.log(err))
    },
    removeAll(prod) {
      axios.post('/api/clients/current/finalRemoveFromCart', `userProductId=${prod.id}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
        .then(resp => {
          if (this.cart.some(product => product.id == prod.id)) {
            let index = this.cart.findIndex(product => product.id == prod.id);
            console.log(index);
            this.cart.splice(index, 1);
            this.savingCart();
            if (this.cart.length == 0) {
              sessionStorage.removeItem('cart');
            }
          }
        }).catch(err => console.log(err))
    },
    addProduct(prod) {
      axios.post("/api/clients/current/add1toCart", `userProductId=${prod.id}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
        .then(resp => {
          let id = this.cart.findIndex(product => product.id == prod.id);
          console.log(id)
          console.log(this.cart)
          this.cart[id].quantity++;
          this.savingCart();
          swal(`We just added your product to your cart!`, {
            buttons: ["Great!", "Take me to my cart"],
            icon: "success"
        })
            .then(res => {
                if (res) {
                    window.location.href = "./cart.html"
                }
            })
        })
    },
    signUp() {
      axios.post('/api/clients/new', `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}&role=${this.roleUser}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
        .then(response => {
          swal({
            title: "Done!",
            text: "We will redirect you...",
            buttons: "Got it!",
            icon: "success"
          }).then(res => {
            if (res) {
              axios.post('/api/login', `email=${this.email}&password=${this.password}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                  console.log(response)
                  window.location.reload()
                })
                .catch(error => {
                  console.log(error.response.status)
                  console.log(error.response.data)
                })
            }
          })
        })
        .catch(error => {
          swal({
            title: "Mmm...",
            text: `${error.response.data}`,
            buttons: "Got it!",
            icon: "info"
          })

        })
    },
  }
})

app.mount("#app")


