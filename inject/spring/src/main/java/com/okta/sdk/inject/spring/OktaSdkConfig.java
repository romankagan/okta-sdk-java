/*
 * Copyright 2017 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.inject.spring;

import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.cache.CacheManager;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.client.ClientConfiguration;
import com.okta.sdk.client.Clients;
import com.okta.sdk.inject.spring.cache.SpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Configure Okta's management SDK, and expose it as a Bean.
 *
 * @since 0.3.0
 */
@Configuration
@Conditional(OktaSdkConfig.OktaApiTokenCondition.class)
@ConditionalOnClass(Client.class)
public class OktaSdkConfig {

    private final ClientConfiguration clientConfiguration;
    private final org.springframework.cache.CacheManager springCacheManager;

    public OktaSdkConfig(ClientConfiguration clientConfiguration,
                         @Autowired(required = false) org.springframework.cache.CacheManager springCacheManager) {

        this.clientConfiguration = clientConfiguration;
        this.springCacheManager = springCacheManager;
    }

    @Bean
    protected Client oktaSdkClient() {
        ClientBuilder builder = Clients.builder().withConfiguration(clientConfiguration)
                .setCacheManager(oktaSdkCacheManager());

        return builder.build();
    }

    private CacheManager oktaSdkCacheManager() {
        return (springCacheManager != null) ?
             new SpringCacheManager(springCacheManager) : null;
    }

    static class OktaClientPropertiesConfiguration {

        @Bean
        @ConfigurationProperties("okta.client")
        protected ClientConfiguration oktaSdkConfig() {
            return new ClientConfiguration();
        }
    }

    /**
     * Spring Boot conditional based on the existance of the {code}okta.client.token{code} property.
     */
    static class OktaApiTokenCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

            ConditionMessage.Builder message = ConditionMessage.forCondition("Okta Api Token Condition");
            RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(context.getEnvironment(), "okta.client.");
            String tokenValue = resolver.getProperty("token");
            if (StringUtils.hasText(tokenValue)) {
                return ConditionOutcome.match(message.foundExactly("provided API token"));
            }
            return ConditionOutcome.noMatch(message.didNotFind("provided API token").atAll());
        }
    }
}