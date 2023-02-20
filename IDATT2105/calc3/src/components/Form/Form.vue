<template>
  <form class="review-form" @submit.prevent="submit">
    <fieldset>
      <legend>Informasjon om deg</legend>
      <BaseInput
        id="Name"
        v-model="name"
        label="Navn"
        type="text"
        :error="nameError"
        data-testid="form-input-name"
      />

      <BaseInput
        id="Email"
        v-model="email"
        label="E-post"
        type="email"
        :error="emailError"
        data-testid="form-input-email"
      />
    </fieldset>

    <fieldset>
      <BaseInput
        id="Message"
        v-model="message"
        label="Melding"
        type="text"
        :error="messageError"
        data-testid="form-input-message"
      />
    </fieldset>
    <input 
      id="submit-button"
      :class="{ hide: !submitVisible }"
      type="submit"
      value="Send inn">
  </form>

  <p v-if="submitMessage" id="submit-message">
        {{ submitMessage }}
  </p>

</template>

<script>
  import { ref } from "vue"
  import { useField, useForm } from "vee-validate"
  import store from '@/store/index.js';
  import FeedbackService from "@/services/FeedbackService"
  import { object as yupObject, string as yupString } from "yup";

  import BaseInput from '@/components/Form/BaseInput.vue';
  import BaseSelect from '@/components/Form/BaseSelect.vue';
  import BaseCheckbox from '@/components/Form/BaseCheckbox.vue';

export default {
  name: 'Form',
  components: {
    BaseInput,
    BaseSelect,
    BaseCheckbox
  },

  setup() {
    let submitMessage = ref("")

    const schema = yupObject({
      name: yupString().required("Name is required"),
      email: yupString()
          .required("Email is required")
          .email("Not a valid email"),
      message: yupString().required("Message is required"),
    });

    const { handleSubmit } = useForm({
      validationSchema: schema
    })

    const name = useField("name")
    const message = useField("message")
    const email = useField("email")

    const submit = handleSubmit(async values => {
      await FeedbackService.postFeedbackForm(values)
          .then(() => {
            store.dispatch("saveFeedbackSubmission", values)
            //alert("Form successfully submitted!")
            submitMessage.value = "Skjemaet er sendt inn. Takk."
          })
          .catch((error) => {
              if (error.code == "ERR_NETWORK")
                submitMessage.value = "Beklager, serveren vår er nede. Skjemaet ble ikke sendt inn."
              else
                submitMessage.value = "Beklager, men en ukjent feil oppstod. Skjemaet ble ikke sendt inn."
          })
    })

    return {
      email: email.value,
      emailError: email.errorMessage,
      name: name.value,
      nameError: name.errorMessage,
      message: message.value,
      messageError: message.errorMessage,
      submitMessage,
      submit,
      schema
    }
  },
  computed: {
    submitVisible() {
      return this.schema.isValidSync({
        name: this.name,
        email: this.email,
        message: this.message
      });
    }
  }
}
</script>

<style scoped>
body {
  margin: auto;
}

.review-form > input {
  margin-top: 20px;
}

fieldset {
  border: 0;
  padding: 10px;
  margin: 20px;
}

legend {
  font-size: 28px;
  font-weight: 700;
  margin-top: 20px;
}

.hide {
  opacity: 50%;
  color: grey;
  pointer-events: none;
  cursor: not-allowed;
}

</style>