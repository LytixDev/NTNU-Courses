import { createStore } from "vuex"
import FeedbackService from "@/services/FeedbackService"

export default createStore({
    state: {
        feedbackSubmissions: [],
        username: null,
        token: null,
    },
    mutations: {
        ADD_FEEDBACK_SUBMISSION(state, subm) {
            state.feedbackSubmissions.push(subm)
        },
        LOGIN(state, data) {
            state.username = data.username
            state.token = data.token
            console.log(state.username, state.token)
        }
    },
    actions: {
        createFeedbackSubmission({ commit }, submission) {
            FeedbackService.postFeedbackForm(submission)
                .then(() => {
                //this.$store.commit("ADD_CONTACT_SUBMISSION", submission)
                    commit("ADD_FEEDBACK_SUBMISSION", submission)
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        saveFeedbackSubmission({ commit }, subm) {
            commit("ADD_FEEDBACK_SUBMISSION", subm)
        },
        login({ commit }, data) {
            commit("LOGIN", data)
        }
    },
    modules: {}
})