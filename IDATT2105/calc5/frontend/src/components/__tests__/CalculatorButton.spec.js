import { describe, it, expect, test } from 'vitest'

import { mount } from '@vue/test-utils'
import CalculatorButton from '../Calculator/CalculatorButton.vue'

describe("CalcualtorButton", () => {
    test("Button shows given value", () => {
        const wrapper = mount(CalculatorButton, { props: { value: "1"} })
        expect(wrapper.text()).toBe("1")
    })
})