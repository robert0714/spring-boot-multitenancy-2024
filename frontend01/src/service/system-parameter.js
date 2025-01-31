import instance from '@/utils/interceptors'

//取得系統參數設置
const getSystemParam = async (companyId) => {
  const requestBody = {
    header: {},
    body: {
      type: 'READ',
      data: {
        companyId:companyId
         }
    }
  }
  return await instance
    .post('/systemParamSetting.r', requestBody)
    .then((response) => {
      return response.data.body
    })
    .catch((error) => {
      console.log(error)
    })
}

//更新系統參數設置
const updateSystemParam = async (updateData) => {
  const requestBody = {
    header: {},
    body: {
      type: 'UPDATE',
      data: updateData
    }
  }
  return await instance
    .post('/systemParamSetting.u', requestBody)
    .then((response) => {
      return response.data.body
    })
    .catch((error) => {
      console.log(error)
    })
}



export { getSystemParam, updateSystemParam }
