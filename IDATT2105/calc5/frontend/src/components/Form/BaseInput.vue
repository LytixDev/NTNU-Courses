<template>
    <div class="base-input">
        <label v-if="label">{{ label }}</label>
        <input
            v-bind="$attrs"
            :placeholder="label"
            class="field"
            :value="modelValue"
            @input="$emit('update:modelValue', $event.target.value)"
            :style="{ borderColor: error ? 'red' : '' }"
            :class="isLarge ? 'large' : ''"
        >
        <div v-if="error" id="error-message">
            {{ error }}
        </div>
    </div>
</template>

<script>
export default {
    name: "BaseInput",
    props: {
        label: {
            type: String,
            default: ""
        },
        error: {
            type: String,
            default: ""
        },
        isLarge: {
            type: Boolean,
            default: false
        },
        modelValue: {
            type: [String, Number],
            default: ""
        }
    }
}

</script>

<style scoped>
#error-message {
    color: red;
}

.base-input {
    padding: 10px;
}

label {
    padding: 20px;
}

.large {
    height: 200px;
}
</style>