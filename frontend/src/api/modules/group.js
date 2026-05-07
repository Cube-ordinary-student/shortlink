import http from '../axios'

export default {
  queryGroup() {
    return http({ url: '/group/page', method: 'get' })
  },
  addGroup(data) {
    return http({ url: '/group/create', method: 'post', data })
  },
  editGroup(data) {
    return http({ url: '/group/update', method: 'post', data })
  },
  removeGroup(data) {
    return http({ url: '/group/delete', method: 'post', data })
  }
}
