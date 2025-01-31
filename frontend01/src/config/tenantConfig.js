// config/tenantConfig.js
import defaultConfig from './defaultConfig';
import tenant1Config from './tenant1Config';
import tenant2Config from './tenant2Config';

export const getTenantConfig = (tenant) => {
  switch (tenant) {
    case 'beans':
      return tenant1Config;
    case 'dukes':
      return tenant2Config;
    default:
      return defaultConfig;
  }
};

