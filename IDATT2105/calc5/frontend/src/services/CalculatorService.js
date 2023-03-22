import axios from "axios"

const apiClient = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: false,
    headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
    }
})

export default {
    async postCalculatorExpr(expr, token) {
        const headers = {
            Authorization: "Bearer " + token
        }
        return await axios.post("http://localhost:8080/calculate", expr, {headers: headers})
    },

    async fetchCalculations(token) {
        const headers = {
            Authorization: "Bearer " + token
        }
        return await axios.get("http://localhost:8080/fetch-calculations", {headers: headers})
    }
}