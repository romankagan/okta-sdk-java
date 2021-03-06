/*
 * Copyright 2018 Okta, Inc.
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
package com.okta.sdk.http;

/**
 * Provides a portion of a User Agent string.
 *
 * @since 1.1.0
 * @deprecated instead create a file named META-INF/okta/version.properties with a format of name=version
 */
@Deprecated
public interface UserAgentProvider {

    /**
     * Returns a portion of a User-Agent string used when making HTTP requests. It is recomended the result be in
     * the format of <code>&lt;lib-identifier&gt;/&lt;version&gt;</code>.
     *
     * @return a portion of a User-Agent string
     */
    String getUserAgent();
}
