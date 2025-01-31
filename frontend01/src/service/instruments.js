import instance from '@/utils/interceptors'
function getInstrumentsUrl() {
  // 依照subdomain取找正確的oauth2 server 
   
  const host = window.location.origin;     
  return host+`/instrumentsEdge`;   
}

//取得instruments
const getInstruments = async () => { 
    let  headerValuse = {
        "Content-Type": "application/json; charset=utf-8",
        Accept: "application/json" 
    };

    return await instance
      .get('/instruments',  {
        headers: headerValuse
    })
      .then((response) => {
        console.log(response.data);
        return response.data  ;
      })
      .catch((error) => {
        console.log(error)
      })
  }

  const getInstrumentsV2 = async () => { 
    const url = getInstrumentsUrl() ;
    const token = JSON.parse(sessionStorage.getItem('user'))?.token;
    let  headerValuse = {
        "Content-Type": "application/json; charset=utf-8",
        "Authorization" : `Bearer ${token}`,
        // "Host" : `${url}`,
        Accept: "application/json" 
    };

    return await fetch(url,  {
      method: "GET",
      headers: headerValuse
    })
       
  }


export { getInstruments ,getInstrumentsV2 }