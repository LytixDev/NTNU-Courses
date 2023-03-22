import { describe, expect, test } from 'vitest'

import { mount } from '@vue/test-utils'
import Display from '../Calculator/Display.vue'

describe("CalcualtorButton", () => {
    test("Button shows given value", () => {
        const wrapper = mount(Display, { props: { value: "30"} })
        expect(wrapper.text()).toBe("30")
    })
})