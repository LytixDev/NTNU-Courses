<template>
  <div id="nav">
    <router-link :to="{ name: 'Calculator' }">Home</router-link> |
    <router-link :to="{ name: 'Feedback' }">Feedback</router-link> |
    <router-link :to="{ name: 'Login' }">Login</router-link>
    <div id="userLoginCard">
      <UserCard 
        :username="$store.state.username"/>
    </div>
  </div>
  <router-view />
</template>

<script>
import UserCard from "@/components/UserCard.vue"
export default {
  components: {
    UserCard,
  },
  watch: {
    "$store.state.loggedinUser": function() {
      console.log("yo");
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

#nav {
  padding: 30px;
  display: flex;
  justify-content: center;
}

#nav > * {
  margin: 0 10px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}
#nav a.router-link-exact-active {
  color: #42b983;
}
</style>
