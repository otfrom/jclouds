/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.keystone.v2_0.functions;

import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.openstack.keystone.v2_0.ServiceAsyncClient;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.ApiAccessKeyCredentials;
import org.jclouds.rest.AsyncClientFactory;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

public class AuthenticateApiAccessKeyCredentials implements Function<Credentials, Access> {
   private final AsyncClientFactory factory;

   // passing factory here to avoid a circular dependency on
   // OpenStackAuthAsyncClient resolving ServiceAsyncClient
   @Inject
   public AuthenticateApiAccessKeyCredentials(AsyncClientFactory factory) {
      this.factory = factory;
   }

   @Override
   public Access apply(Credentials input) {
      // TODO: tenantID may not be present
      Iterable<String> tenantIdUsernameOrAccessKey = Splitter.on(':').split(input.identity);
      String tenantId = Iterables.get(tenantIdUsernameOrAccessKey, 0);
      String usernameOrAccessKey = Iterables.get(tenantIdUsernameOrAccessKey, 1);
      String passwordOrSecretKey = input.credential;

      ServiceAsyncClient client = factory.create(ServiceAsyncClient.class);
      try {
         ApiAccessKeyCredentials apiAccessKeyCredentials = ApiAccessKeyCredentials.createWithAccessKeyAndSecretKey(
                  usernameOrAccessKey, passwordOrSecretKey);
         return client.authenticateTenantWithCredentials(tenantId, apiAccessKeyCredentials).get();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public String toString() {
      return "authenticateApiAccessKeyCredentials()";
   }
}