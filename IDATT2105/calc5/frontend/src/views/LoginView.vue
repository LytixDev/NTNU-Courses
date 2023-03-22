<template>
    <h1>Login</h1>
    <form @submit.prevent="submit">
      <BaseInput
        id="Username"
        v-model="username"
        label="Brukernavn"
        type="text"
        :error="usernameError"
        data-testid="form-input-username"
      />

      <BaseInput
        id="Password"
        v-model="password"
        label="Passord"
        type="password"
        :error="passwordError"
        data-testid="form-input-password"
      />

    <input 
      id="submit-button"
      :class="submitVisible ? 'visible' : 'hide'"
      type="submit"
      value="Send inn">

    </form>

    <p v-if="submitMessage" id="submit-message">
          {{ submitMessage }}
    </p>
</template>

<script>
import BaseInput from '@/components/Form/BaseInput.vue';
import { useField, useForm } from "vee-validate"
import { object as yupObject, string as yupString } from "yup";
import store from '@/store/index.js';
import LoginService from "@/services/LoginService"
import { ref } from 'vue';

export default {
  name: 'Login',
  components: {
    BaseInput,
  },

  setup() {
    let submitMessage = ref("")

    const schema = yupObject({
      username: yupString()
                .required("Username is required")
                .min(3, "Username must be at least 3 characters long"),
      password: yupString()
                .required("Password is required")
                //.min(8, "Password must be at least 8 characters long")
                //.test("isValidPass", "must contain at least one uppercase, lowercase and number", (value) => {
                //    const hasUpperCase = /[A-Z]/.test(value);
                //    const hasLowerCase = /[a-z]/.test(value);
                //    const hasNumber = /[0-9]/.test(value);
                //    return (hasUpperCase && hasLowerCase && hasNumber);
                //}),
      
    });

    const { handleSubmit } = useForm({
      validationSchema: schema
    })

    const username = useField("username")
    const password = useField("password")

    const submit = handleSubmit(async values => {
        await LoginService.postLoginForm(values)
            .then((response) => {
                const token = response.data.token;
                store.dispatch("login", {...values, token})
                submitMessage.value = "Login successful"
            })
            .catch((error) => {
                submitMessage.value = "Login unsuccessful"
            })
    })

    return {
      username: username.value,
      usernameError: username.errorMessage,
      password: password.value,
      passwordError: password.errorMessage,
      submit,
      submitMessage,
      schema
    }
  },
  computed: {
    submitVisible() {
      return this.schema.isValidSync({
        username: this.username,
        password: this.password,
      });
    }
  }

}
</script>


<style scoped>

.hide {
  opacity: 50%;
  color: grey;
  pointer-events: none;
  cursor: not-allowed;
}

.visible {
    background-color: greenyellow;
}
</style>