<template>
    <!-- Add new sale modal -->
    <div class="modal fade" id="new-sale-modal" role="dialog">
        <div class="modal-dialog rounded" role="document">
            <div class="modal-content">
                <div class="modal-header" @click="cancelSale()">
                    <h3>Define Sale</h3>
                    <button class="btn btn-close close"></button>
                </div>
                <div class="modal-body">
                    <v-date-picker v-model="v$.sale.dateTimeFrom.$model" :min-date='new Date()' mode="dateTime" is24hr>
                        <template v-slot="{ inputValue, inputEvents }">
                            <input class="px-2 py-1 border rounded focus:outline-none focus:border-blue-300" placeholder="Period date and time*" :value="inputValue" v-on="inputEvents" />
                            <div class="text-danger" v-if="v$.sale.dateTimeFrom.$error">Value is required and can't be before the expiration date </div>
                        </template>
                    </v-date-picker>

                    <input type="number" class="form-control" placeholder="Duration*" v-model="v$.sale.durationInHours.$model"/>
                    <div class="text-danger" v-if="v$.sale.durationInHours.$error">{{v$.sale.durationInHours.$errors[0].$message}} </div>

                    <input type="number" class="form-control" placeholder="Maximum number of persons*" v-model="v$.sale.maximumPersons.$model"/>
                    <div class="text-danger" v-if="v$.sale.maximumPersons.$error">{{v$.sale.maximumPersons.$errors[0].$message}} </div>

                    <input type="text" class="form-control" placeholder="Additional services included*" v-model="v$.sale.additionalServices.$model"/>
                    <div class="text-danger" v-if="v$.sale.additionalServices.$error">{{v$.sale.additionalServices.$errors[0].$message}} </div>

                    <input type="number" class="form-control" placeholder="Price*" v-model="v$.sale.price.$model"/>
                    <div class="text-danger" v-if="v$.sale.price.$error">{{v$.sale.price.$errors[0].$message}} </div>

                    <v-date-picker v-model="v$.sale.expireDateTime.$model" :min-date='new Date()' mode="dateTime" is24hr>
                        <template v-slot="{ inputValue, inputEvents }">
                            <input class="px-2 py-1 border rounded focus:outline-none focus:border-blue-300" placeholder="Sale expiration date and time*" :value="inputValue" v-on="inputEvents" />
                            <div class="text-danger" v-if="v$.sale.expireDateTime.$error">Value is required and can't be after the start date</div>
                        </template>
                    </v-date-picker>
                </div>
                <div class="modal-footer">
                    <div class="confirm-buttons">
                        <button class="btn submit-btn" @click="createSale()" :disabled="v$.sale.$invalid" >Submit</button>
                        <button class="btn cancel-btn" @click="cancelSale()">Cancel</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

     <!-- Define new unavailable period modal -->
    <div class="modal fade" id="unavailable-period-modal" role="dialog">
        <div class="modal-dialog rounded" role="document">
            <div class="modal-content">
                <div class="modal-header" @click="cancelUnavailablePeriod()">
                    <h3>Define Unavailable period</h3>
                    <button class="btn btn-close close"></button>
                </div>
                <div class="modal-body">
                    <v-date-picker v-model="v$.unavailablePeriod.fromDateTime.$model" :min-date='new Date()' mode="dateTime" is24hr>
                        <template v-slot="{ inputValue, inputEvents }">
                            <input class="px-2 py-1 border rounded focus:outline-none focus:border-blue-300" placeholder="Start period date and time*" :value="inputValue" v-on="inputEvents"/>
                            <div class="text-danger" v-if="v$.unavailablePeriod.fromDateTime.$error">Value is required and can't be before the end date </div>
                        </template>
                    </v-date-picker>

                    <v-date-picker v-model="v$.unavailablePeriod.toDateTime.$model" :min-date='new Date()' mode="dateTime" is24hr>
                        <template v-slot="{ inputValue, inputEvents }">
                            <input class="px-2 py-1 border rounded focus:outline-none focus:border-blue-300" placeholder="End period date and time*" :value="inputValue" v-on="inputEvents"/>
                            <div class="text-danger" v-if="v$.unavailablePeriod.toDateTime.$error">Value is required and can't be after the start date</div>
                        </template>
                    </v-date-picker>
                </div>
                <div class="modal-footer">
                    <div class="confirm-buttons">
                        <button class="btn submit-btn" @click="createUnavailablePeriod()" :disabled="v$.unavailablePeriod.$invalid">Submit</button>
                        <button class="btn cancel-btn" @click="cancelUnavailablePeriod()">Cancel</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <transition name="fade" appear v-if="userRoleIsClient()">
        <ClientReservation :entity="ship" :type="type" v-if="displayReservationModal" @close-modal='closeModal' @new-reservation="showReservation"/>
    </transition>
    <transition name="fade" appear v-else>
        <CreateReservation :entity="ship" :type="type" v-if="displayReservationModal" @close-modal='closeReservationModal' @new-reservation="showReservation"/>
    </transition>

    <div id="profile" v-if="ship">
        <AdventureCaption :adventureName="this.ship.name" :adventureId="this.entityId" :entityName="'ship'"
            @create-sale="openModalForCreatingSale()" @edit-entity="this.$emit('edit-ship', this.entityId)" @entity-deleted="this.$emit('entity-deleted', 2)"/>
        <div class="content">
            <div class="left-side">
                <ImageGallery :images="ship.images"  description="Photos of our ship"/> <hr/>
                <Sales :sales="ship.sales" v-if="userRole != ''" :adventure="ship" @sale-to-reservation="saleToReservation"/><br/>
                <div class="btn-wrap">
                    <h2>Schedule for this adventure</h2>
                    <button class="btn" v-if="userRole != '' && userRole != 'ROLE_CLIENT' && userRole != 'ROLE_ADMIN'" @click="openModalForDefineUnavailablePeriod()">Unavailable period&nbsp;&ensp;<i class="fas fa-calendar-check"></i> </button>
                    <button class="btn" v-if="userRole != 'ROLE_ADMIN'" @click="makeReservation()">Make a reservation&nbsp;&ensp;<i class="fas fa-calendar-check"></i> </button>
                </div>
                <Calendar class="calendar" :events="events"/>
                <p>In case of reservation cancellation, ship owner retains {{ ship.cancellationPercentage }}% of the price! </p><hr/>
                <PricelistTable :pricelistItem="ship.pricelistItems"/><hr/>
            </div>
            <div class="right-side">
                <ShipTextDescription :ship="ship" /><hr/>
                <Map :cottage="ship"/><br/><hr/>
            </div>
        </div>
    </div>
</template>

<script>
    import AdventureCaption from "@/components/adventure/AdventureCaption.vue"
    import ImageGallery from "@/components/ImageGallery.vue"
    import Calendar from "@/components/Calendar.vue"
    import PricelistTable from "@/components/entities/PricelistTable.vue"  
    import ShipTextDescription from "@/components/ship/ShipTextDescription.vue"
    import Sales from "@/components/adventure/Sales.vue"
    import Map from "@/components/entities/ShowLocationOnMap.vue"
    import CreateReservation from "@/components/cottage/CreateReservation.vue"
    import ClientReservation from "@/components/client/ClientReservation.vue"
    import axios from 'axios'
    import server from '../../server/index'
    import useValidate from '@vuelidate/core'
    import {required, numeric} from '@vuelidate/validators'
    import moment from "moment"

    export default {
        props:['entityId'],
        emits:['edit-ship', 'entity-deleted'],
        components: {
            AdventureCaption,
            ImageGallery,
            Calendar,
            PricelistTable,
            ShipTextDescription,
            Map,
            Sales,
            CreateReservation,
            ClientReservation
        },
        data() {
            return {
                id: this.entityId,
                displayReservationModal : false,
                type: 'Ship',
                ship: {
                    name: '',
                    description: '',
                    cancellationPercentage: undefined,
                    images: [],
                    allowedBehavior: [],
                    unallowedBehavior: [],
                    address: {
                        streetName: "Bulevar Cara Lazara",
                        streetNumber: "171",
                        postalCode: "21000",
                        city: "Novi Sad",
                        country:  "Serbia",
                        longitude: 19,
                        latitude: 45
                    },
                    pricelistItems: [
                        {
                            service:'Standard offer',
                            price: 3000
                        }
                    ],
                    type: '',
                    length: undefined,
                    engineNumber: undefined,
                    enginePower: undefined,
                    maxSpeed: undefined,
                    capacity: undefined,
                    navigationEquipment: [],
                    fishingEquipment: [],
                    shipOwner:{
                        email: ''
                    }
                },
                sale: {
                    dateTimeFrom : '',
                    durationInHours: '',
                    maximumPersons: '',
                    expireDateTime: '',
                    additionalServices: '',
                    price: ''
               },
                unavailablePeriod:{
                    fromDateTime: '',
                    toDateTime: '',
                    entityName : ''
               }, 
               events: []
            } 
        },
        computed: {
            userRole(){
                return this.$store.getters.getRole;
            },
            state(){
                return this.$store.getters.getState;
            },
            token(){
                 return this.$store.getters.getToken;
            }
        },
        created() {
            this.fetchData()
        },
        setup() {
            return { v$: useValidate() }
        },
        validations() {
            return {
                sale: {
                    dateTimeFrom : { 
                        required, 
                        minValue(val, { expireDateTime }) {
                            return new Date(val) > new Date(expireDateTime);
                        }
                    },
                    durationInHours: { required, numeric },
                    maximumPersons: { required, numeric },
                    expireDateTime: { 
                        required, 
                        minValue(val) {
                            return new Date(val) > new Date();
                        },
                        maxValue(val, {dateTimeFrom}){
                            return new Date(dateTimeFrom) > new Date(val);
                        }
                    },
                    price: { required, numeric },
                    additionalServices: {  }
                },
                unavailablePeriod: {
                    fromDateTime : { 
                        required,
                        minValue(val) {
                            return new Date(val) > new Date();
                        },
                    },
                    toDateTime: { 
                        required, 
                        minValue(val) {
                            return new Date(val) > new Date();
                        },
                        maxValue(val, {fromDateTime}){
                            return new Date(fromDateTime) < new Date(val);
                        }
                    }
                },
            }
        },
        methods: {
            fetchData: function(){
                axios
                .get(`${server.baseUrl}/ship/getOne/` + this.entityId)
                .then(response => {
                    this.ship = response.data;
                    for(let sale of this.ship.sales) {
                        var endTime = new Date(sale.dateTimeFrom);
                        endTime.setHours(parseInt(endTime.getHours()) + parseInt(sale.durationInHours));
                        this.events.push({
                            start : new Date(sale.dateTimeFrom), 
                            end : endTime,
                            title : 'SALE',
                            content: `<p style="font-size: 12px; color="light-gray;"> Expires on ${this.dateFormat(sale.expireDateTime)} </p>`,
                            class: 'calendar-sale'
                         })
                    }

                    for(let period of this.ship.unavailablePeriods) {
                        this.events.push({
                            start : new Date(period.fromDateTime), 
                            end : new Date(period.toDateTime),
                            title : period.message,
                            class: 'calendar-unavailable'
                        })
                    }

                    axios.get(`${server.baseUrl}/reservation/entity/${this.ship.id}`)
                    .then((res) => {
                        for(let reservation of res.data) {
                            var endTime = new Date(reservation.dateTime);
                            endTime.setHours(parseInt(endTime.getHours()) + parseInt(reservation.durationInHours));
                            this.events.push({
                                start : new Date(reservation.dateTime), 
                                end : endTime,
                                title : 'BOOKED',
                                class: 'calendar-booked'
                            })
                        }
                    })
                })
            },
            openModalForCreatingSale(){
                window.$('#new-sale-modal').modal('show');
            },
             openModalForDefineUnavailablePeriod: function(){
                window.$('#unavailable-period-modal').modal('show');
            },
            cancelUnavailablePeriod: function() {
                this.unavailablePeriod = { fromDateTime: '', toDateTime: '' }
                window.$('#unavailable-period-modal').modal('hide');
            },
            createUnavailablePeriod: function(){
                const headers = {
                    'Content-Type': 'application/json;charset=UTF-8',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${this.token}`
                }

                this.unavailablePeriod.entityId = this.id;

                axios.post(`${server.baseUrl}/ship/defineUnavailablePeriod`, this.unavailablePeriod, { headers: headers })
                .then((response) => {
                   
                    this.unavailablePeriod = { fromDateTime: '', toDateTime: '' }
                    window.$('#unavailable-period-modal').modal('hide');

                    this.events.push({
                        start : new Date(response.data.fromDateTime), 
                        end : new Date(response.data.toDateTime),
                        title : response.data.message,
                        class: 'calendar-unavailable'
                    });

                    this.$swal({
                        position: 'top-end',
                        icon: 'success',
                        title: "Unavailable period saved!",
                        showConfirmButton: false,
                        timer: 2000
                    })
                })
                .catch(error => {
                     this.$swal({
                        position: 'top-end',
                        icon: 'info',
                        title: error.response.data.message,
                        showConfirmButton: false,
                        timer: 2000
                    })
                })

            },
            createSale: function() { 
                const headers = {
                    'Content-Type': 'application/json;charset=UTF-8',
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${this.token}`
                }

                axios.post(`${server.baseUrl}/sale/${this.id}`, this.sale, { headers: headers })

                .then((response) => {
                    this.ship.sales.push(response.data);

                    var endTime = new Date(this.sale.dateTimeFrom);
                    endTime.setHours(parseInt(endTime.getHours()) + parseInt(this.sale.durationInHours));

                    this.events.push({
                        start : new Date(this.sale.dateTimeFrom), 
                        end : endTime,
                        title : 'SALE',
                        content: `<p style="font-size: 12px; color="light-gray;"> Expires on ${this.dateFormat(this.sale.expireDateTime)} </p>`,
                        class: 'calendar-sale'
                    })

                    window.$('#new-sale-modal').modal('hide');

                    this.$swal({
                        position: 'top-end',
                        icon: 'success',
                        title: 'New sale created!',
                        showConfirmButton: false,
                        timer: 2000
                    })

                    this.sale = { dateTimeFrom : '', durationInHours: undefined, maximumPersons: undefined, expireDateTime: '', additionalServices: '', price: undefined }
                })
                .catch(error => {
                    this.$swal({
                        position: 'top-end',
                        icon: 'info',
                        title: error.response.data.message,
                        showConfirmButton: false,
                        timer: 2000
                    })
                 })
             },
            cancelSale: function() {
                this.sale = { dateTimeFrom : '', durationInHours: '', maximumPersons: '', expireDateTime: '', additionalServices: '', price: '' }
                window.$('#new-sale-modal').modal('hide');
            },
            closeReservationModal: function(){
                this.displayReservationModal = false;
                document.getElementById('appContainer').style.overflow = 'unset';
                document.getElementById('appContainer').style.height='unset';
            },
            editEntity: function() {},
            makeReservation: function() {
                this.displayReservationModal = true;
                document.getElementById('appContainer').style.overflow ='hidden';
                document.getElementById('appContainer').style.height='100vh';
            },  
            closeModal: function(){
                this.displayReservationModal = false;
                document.getElementById('appContainer').style.overflow = 'unset';
                document.getElementById('appContainer').style.height='unset';
            },
            dateFormat(value) {
                return moment(value).format("DD.MM.YYYY. HH:mm");
            },
            userRoleIsClient(){
                if(this.userRole == "ROLE_CLIENT") return true;
                else return false;
            },
            saleToReservation(reservation) {
                for(let e of this.events) {
                    var endTime = new Date(reservation.dateTime);
                    endTime.setHours(parseInt(endTime.getHours()) + parseInt(reservation.durationInHours));
                    
                    if(e.start.getTime() === new Date(reservation.dateTime).getTime() && e.end.getTime() === endTime.getTime()) {
                        e.title = "BOOKED";
                        e.class = "calendar-booked";
                        e.content = "";
                        break;
                    }
                }
            },
            showReservation(reservation) {
                this.events.push({
                    start : new Date(reservation.dateFrom), 
                    end : new Date(reservation.dateTo),
                    title : 'BOOKED',
                    content: "",
                    class: 'calendar-booked'
                })
                this.displayReservationModal = false;
                document.getElementById('appContainer').style.overflow = 'unset';
                document.getElementById('appContainer').style.height='unset';
            }
        }
    }

</script>

<style scoped>
    #profile {
        padding-top: 50px;
        padding-bottom: 50px;
        margin-left: 15%;
        margin-right: 15%;
    }
    hr {
        margin-top: 30px;
        margin-bottom: 30px;
    }
    h2 {
        font-size: 25px;
    }
    .content {
        display: flex;
        margin-top: 50px;
        justify-content: space-between;
    }
    .left-side {
        width: 63%;
    }
    .right-side {
        width: 30%;
    }
    .btn-wrap {
        display: flex;
        justify-content: space-between;
    }
    .btn-wrap-period {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
    }
    .btn {
        background-color: #2c3e50;
        color: white;
    }

   .btn-close{ 
        background-color: transparent;
    }
    .cancel-btn {
        background-color: white;
        color: #2c3e50;
        border-color: #cfd3d8;
        margin-left: 10px;
    }
    input {
        width: 465px;
        margin-bottom: 25px;
    }
    .text-danger {
        margin-top: -20px;
        margin-bottom: 5px;
        text-align: left;
        font-size: 13px;
    }

    .calendar {
        margin-top: 20px;
        height: 100vh;
        margin-bottom: 20px;
    }
</style>