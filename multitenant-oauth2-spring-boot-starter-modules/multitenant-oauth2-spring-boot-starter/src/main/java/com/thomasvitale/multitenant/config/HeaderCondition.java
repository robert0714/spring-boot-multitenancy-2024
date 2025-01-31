package com.thomasvitale.multitenant.config;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Condition for creating header-based beans.
 */
public class HeaderCondition extends AllNestedConditions {

    HeaderCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(prefix = "spring.security.oauth2.resourceserver.multitenant.header", name = "enabled",
            havingValue = "true")
    static class OnEnabled { }

     

}
