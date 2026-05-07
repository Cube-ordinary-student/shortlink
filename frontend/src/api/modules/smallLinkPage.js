import http from '../axios'

export default {
  queryPage(data) {
    return http({ url: '/page', method: 'get', params: data })
  },
  addSmallLink(data) {
    return http({ url: '/create', method: 'post', data })
  },
  editSmallLink(data) {
    return http({ url: '/update', method: 'post', data })
  },
  toRecycleBin(data) {
    return http({ url: '/recycle-bin/save', method: 'post', data })
  },
  queryRecycleBin(data) {
    return http({ url: '/recycle-bin/page', method: 'get', params: data })
  },
  recoverLink(data) {
    return http({ url: '/recycle-bin/recover', method: 'post', data })
  },
  removeLink(data) {
    return http({ url: '/recycle-bin/remove', method: 'post', data })
  },
  queryLinkStats(data) {
    return http({ url: '/stats', method: 'get', params: data })
  }
}
