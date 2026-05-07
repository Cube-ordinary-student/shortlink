import { createStore } from 'vuex'

const store = createStore({
  state() {
    return {
      domain: 's.lanyue.com'
    }
  },
  mutations: {
    SET_DOMAIN(state, domain) {
      state.domain = domain
    }
  },
  actions: {
    setDomain({ commit }, domain) {
      commit('SET_DOMAIN', domain)
    }
  },
  getters: {
    getDomain: (state) => state.domain
  }
})

export default store
