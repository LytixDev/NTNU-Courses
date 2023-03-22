<template>
  <h3>Kontaktskjema</h3>
  <Form />

  {{ $store.state.feedbackSubmissions }}
</template>

<script>
import Form from "@/components/Form/Form.vue"

export default {
  name: 'FeedbackView',
  components: {
    Form,
  }
}
</script>