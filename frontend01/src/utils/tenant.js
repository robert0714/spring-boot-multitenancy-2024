// utils/tenant.js
export const getTenantFromSubdomain = () => {
    const hostname = window.location.hostname;
    const isIp = validateIPaddress(hostname);
    if (isIp) {
      return null;
    }
    const subdomain = hostname.split('.')[0];
    return subdomain;
  };
  const validateIPaddress = (ipaddress) =>  {  
    if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(ipaddress)) {  
      return (true)  
    }  
    console.log("You have entered an invalid IP address!")  
    return (false)  
  }  