<template>
{{ this.msg }}
</template>


<script>
export default {
    name: "UserCard",
    props: ["username"],
    computed: {
        msg() {
            if (this.username == "" || this.username == null || this.username == undefined)
                return "Not logged in"
            else
                return "Logged in as " + this.username
        }
    }
}
</script>