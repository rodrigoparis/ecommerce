const app = Vue.createApp({

    data() {
        return {
            clients: [],
        }
    },

    created() {
        axios.get('/api/clients')
        .then(response => {
            console.log(response.data)
            this.clients = response.data
        })
        .catch(error => {
            return error.message;
        })
    },
    methods: {
        
    }, 

})

const verAppVue = app.mount("#app") 
