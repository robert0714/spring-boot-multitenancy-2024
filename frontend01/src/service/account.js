import instance from '@/utils/interceptors'

//取得帳號資訊
const getAccount = async (data) => {
  const requestBody = {
    header: {},
    body: {
      type: 'READ',
      data: {
        userName: data,
      },
    }
  }
  return await instance
    .post('/vUserInfo', requestBody)
    .then((response) => {
      return response.data.body
    })
    .catch((error) => {
      console.log(error)
    })
}

//編輯帳號資訊
const editAccount = async (updateData) => {
  const requestBody = {
    header: {},
    body: {
      type: 'UPDATE',
      data: updateData
    }
  }
  return await instance
    .post('/vUserInfo', requestBody)
    .then((response) => {
      return response.data.body
    })
    .catch((error) => {
      console.log(error)
    })
}

//編輯mima
const editMima = async (updateData) => {
  const requestBody = {
    header: {},
    body: {
      type: 'UPDATE',
      data: updateData
    }
  }
  return await instance
    .post('/vUserInfo.changePassword', requestBody)
    .then((response) => {
      return response.data.body
    })
    .catch((error) => {
      console.log(error)
    })
}

export { getAccount, editAccount, editMima }
