<template>
  <div id="app">
    <div class="modal-overlay" @click="$emit('close-modal')"></div>
      <div class="modal-inner">
        <h1>Revision</h1>
        <textarea id="w3review" name="w3review" placeholder="Description ...*" rows="6" cols="50" class="mt-4" v-model="description"/>
        <p v-if="error">Field and rating must not be empty!</p>
        <h3>Enter a grade:</h3>
        <StarRating v-model:rating="rating" :show-rating="false" class="rating" :animate="true"/>
        <div class="form-group mt-4 button-div">
          <button type="button" class="btn log-btn p-2"    @click="submitRevision">Submit</button>
          <button type="button" class="btn cancel-btn p-2" @click="$emit('close-modal')">Cancel</button>
        </div>
      </div>
  </div>
</template>

<script>
  import StarRating from 'vue-star-rating'
  export default {
    props: ['showModal'],
    emits:['submit-revision'],
    components: {
      StarRating
    },
    data(){
      return{
          error : false,
          rating: 0,
          description: ''
      }
    },
    methods:{
      submitRevision(){
        if(this.description == '' || this.rating == 0) 
          this.error=true;
        else
          this.$emit('submit-revision',this.description,this.rating);
      }
    }
  }
</script>

<style scoped>
  *{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  #app {
    position: absolute;
    display: flex;
    justify-content: center;
    align-items: center;
    left:0;
    top:0;
    width: 100vw;
    min-height: 100vh;
    overflow-x: hidden;
  }
  .rating{
    display: flex;
    justify-content: center;
  }
  .modal-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 98;
    background-color: rgba(0, 0, 0, 0.3);
  }
  .modal-inner{
    flex-direction: column;
    justify-content: space-around;
    width: 30%;
    height: 60%;
    background-color: #8495e8;
    z-index: 1000;
    position: fixed;
    border-radius: 16px;
    padding: 25px;
  }
  .button-div{
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-around;
  }
  .log-btn{
    color:white;
    background: #0e0f40;
    width: 30%;
  }
  .cancel-btn{
    background: white;
    color: #0e0f40;
    width: 30%;
  }
  h3{
    display: flex;
    justify-content: flex-start;
    margin-left:3vw ;
    margin-top:2vh;
  }
  textarea{
    border-radius: 5px;
    border:none;
    height: 15vh;
    width: 100%;
  }
  p{
    color:#992107;
  }
</style>
