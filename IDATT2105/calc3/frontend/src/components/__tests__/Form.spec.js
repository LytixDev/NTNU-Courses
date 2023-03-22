import Form from "@/components/Form/Form.vue";
import { mount } from "@vue/test-utils";
import { describe, test, expect, beforeEach } from "vitest";
import { createStore } from 'vuex'

describe("Form", () => {
  let store;

  beforeEach(() => {
    store = createStore({
      state: {
        feedback: {
          name: "",
          email: "",
          message: "",
        },
      },
    })
  })

  test("Form displays name, email and message inputs", () => {
    const wrapper = mount(Form, {
      global: {
        plugins: [store]
      }
    });
    const nameInput = wrapper.find('[data-testid="form-input-name"]');
    const emailInput = wrapper.find('[data-testid="form-input-email"]');
    const messageInput = wrapper.find('[data-testid="form-input-message"]');
    expect(nameInput.exists()).toBe(true);
    expect(emailInput.exists()).toBe(true);
    expect(messageInput.exists()).toBe(true);
  });
});