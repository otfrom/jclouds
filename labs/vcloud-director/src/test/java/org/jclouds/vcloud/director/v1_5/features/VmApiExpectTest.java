/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;

/**
 * Allows us to test the {@link VmApi} via its side effects.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "VmApiExpectTest")
public class VmApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   
   private String vmId = "vm-d0e2b6b9-4381-4ddc-9572-cdfae54059be";
   private URI vmURI = URI.create(endpoint + vmId);
   
   @BeforeClass
   public void before() {
   }
   
   @Test(enabled = false)//TODO
   public void testGetVm() {
      VCloudDirectorApi api = orderedRequestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", vmId)
               .acceptMedia(VCloudDirectorMediaType.VM)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vm/vm.xml", VCloudDirectorMediaType.VM)
               .httpResponseBuilder().build());
      
      Vm expected = getVm();

      assertEquals(api.getVmApi().getVm(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVm() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId)
            .xmlFilePayload("/vm/modifyVm.xml", VCloudDirectorMediaType.VM)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vm/modifiedVm.xml", VCloudDirectorMediaType.VM)
            .httpResponseBuilder().build());
		         
		Vm modified = getVm();
		modified.setName("new-name");
		modified.setDescription("New Description");
		
		Task expected = modifyVmTask();
		
		assertEquals(api.getVmApi().modifyVm(vmURI, modified), expected);
   }

   @Test(enabled = false)
   public void testDeleteVm() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("DELETE", vmId)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vm/deleteVmTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = deleteVmTask();
		
		assertEquals(api.getVmApi().deleteVm(vmURI), expected);
   }

   @Test(enabled = false)
   public void testConsolidateVm() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/consolidate")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vm/consolidateVmTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = consolidateVmTask();
		
		assertEquals(api.getVmApi().consolidateVm(vmURI), expected);
   }

   @Test(enabled = false)
   public void testDeploy() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/deploy")
            .xmlFilePayload("/vm/deployParams.xml", VCloudDirectorMediaType.DEPLOY_VAPP_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/deployTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      DeployVAppParams params = DeployVAppParams.builder()
            .build();
		
		Task expected = deployTask();
		
		assertEquals(api.getVmApi().deploy(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testDiscardSuspendedState() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/discardSuspendedState")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/discardSuspendedStateTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = discardSuspendedStateTask();
		
		assertEquals(api.getVmApi().discardSuspendedState(vmURI), expected);
   }

   @Test(enabled = false)
   public void testInstallVMwareTools() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/installVMwareTools")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/installVMwareToolsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = installVMwareToolsTask();
		
		assertEquals(api.getVmApi().installVMwareTools(vmURI), expected);
   }

   @Test(enabled = false)
   public void testRelocate() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/relocate")
            .xmlFilePayload("/vApp/relocateParams.xml", VCloudDirectorMediaType.RELOCATE_VM_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/relocateTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
     
      RelocateParams params = RelocateParams.builder()
            .build();
		
		Task expected = relocateTask();
		
		assertEquals(api.getVmApi().relocateVm(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testUndeploy() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/undeploy")
            .xmlFilePayload("/vApp/undeployParams.xml", VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/undeployTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      UndeployVAppParams params = UndeployVAppParams.builder()
            .build();
		
		Task expected = undeployTask();
		
		assertEquals(api.getVmApi().undeploy(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testUpgradeHardwareVersion() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/upgradeHardwareVersion")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/upgradeHardwareVersionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = upgradeHardwareVersionTask();
		
		assertEquals(api.getVmApi().upgradeHardwareVersion(vmURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOff() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/powerOff")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/powerOffTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = powerOffTask();

      assertEquals(api.getVmApi().powerOff(vmURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/powerOn")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/powerOnTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = powerOnTask();

      assertEquals(api.getVmApi().powerOn(vmURI), expected);
   }

   @Test(enabled = false)
   public void testReboot() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/reboot")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/rebootTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = rebootTask();

      assertEquals(api.getVmApi().reboot(vmURI), expected);
   }

   @Test(enabled = false)
   public void testReset() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/reset")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/resetTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = resetTask();

      assertEquals(api.getVmApi().reset(vmURI), expected);
   }

   @Test(enabled = false)
   public void testShutdown() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/shutdown")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/shutdownTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = shutdownTask();

      assertEquals(api.getVmApi().shutdown(vmURI), expected);
   }

   @Test(enabled = false)
   public void testSuspend() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/suspend")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/suspend.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = suspendTask();
		
		assertEquals(api.getVmApi().suspend(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetGuestCustomizationSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/guestCustomizationSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getGuestCustomizationSection.xml", VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION)
            .httpResponseBuilder().build());
		
		GuestCustomizationSection expected = getGuestCustomizationSection();
		
		assertEquals(api.getVmApi().getGuestCustomizationSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyGuestCustomizationSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/guestCustomizationSection")
            .xmlFilePayload("/vApp/modifyGuestCustomizationSection.xml", VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyGuestCustomizationSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      GuestCustomizationSection section = getGuestCustomizationSection().toBuilder()
            .build();

      Task expected = modifyGuestCustomizationSectionTask();

      assertEquals(api.getVmApi().modifyGuestCustomizationSection(vmURI, section), expected);
   }

   @Test(enabled = false)
   public void testEjectMedia() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/media/action/ejectMedia")
            .xmlFilePayload("/vApp/ejectMediaParams.xml", VCloudDirectorMediaType.MEDIA_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/ejectMediaTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .build();

      Task expected = ejectMediaTask();

      assertEquals(api.getVmApi().ejectMedia(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testInsertMedia() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/media/action/insertMedia")
            .xmlFilePayload("/vApp/insertMediaParams.xml", VCloudDirectorMediaType.MEDIA_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/insertMediaTask.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());
      
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .build();

      Task expected = insertMediaTask();

      assertEquals(api.getVmApi().insertMedia(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkConnectionSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/networkConnectionSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getNetworkConnectionSection.xml", VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION)
            .httpResponseBuilder().build());

      NetworkConnectionSection expected = getNetworkConnectionSection();

         assertEquals(api.getVmApi().getNetworkConnectionSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyNetworkConnectionSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/networkConnectionSection")
            .xmlFilePayload("/vApp/modifyNetworkConnectionSection.xml", VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyNetworkConnectionSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		      
		NetworkConnectionSection section = getNetworkConnectionSection().toBuilder()
		      .build();
		
		Task expected = modifyNetworkConnectionSectionTask();
		
		assertEquals(api.getVmApi().modifyNetworkConnectionSection(vmURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetOperatingSystemSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/operatingSystemSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getOperatingSystemSection.xml", VCloudDirectorMediaType.OPERATING_SYSTEM_SECTION)
            .httpResponseBuilder().build());

		OperatingSystemSection expected = getOperatingSystemSection();
		
		assertEquals(api.getVmApi().getOperatingSystemSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyOperatingSystemSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/operatingSystemSection")
            .xmlFilePayload("/vApp/modifyOperatingSystemSection.xml", VCloudDirectorMediaType.OPERATING_SYSTEM_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyOperatingSystemSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		      
      OperatingSystemSection section = getOperatingSystemSection().toBuilder()
		      .build();
		
		Task expected = modifyOperatingSystemSectionTask();
		
		assertEquals(api.getVmApi().modifyOperatingSystemSection(vmURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetProductSections() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/productSections")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getProductSections.xml", VCloudDirectorMediaType.PRODUCT_SECTION_LIST)
            .httpResponseBuilder().build());

         ProductSectionList expected = getProductSections();

         assertEquals(api.getVmApi().getProductSections(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyProductSections() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/productSections")
            .xmlFilePayload("/vApp/modifyProductSections.xml", VCloudDirectorMediaType.PRODUCT_SECTION_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyProductSections.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         Task expected = modifyProductSectionsTask();

         assertEquals(api.getVmApi().modifyProductSections(vmURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetPendingQuestion() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/question")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getPendingQuestion.xml", VCloudDirectorMediaType.VM_PENDING_QUESTION)
            .httpResponseBuilder().build());

         VmPendingQuestion expected = getPendingQuestion();

         assertEquals(api.getVmApi().getPendingQuestion(vmURI), expected);
   }

   @Test(enabled = false)
   public void testAnswerQuestion() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/question/action/answer")
            .xmlFilePayload("/vApp/answerQuestion.xml", VCloudDirectorMediaType.VM_PENDING_ANSWER)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());

         VmQuestionAnswer answer = null; // = VmQuestionAnswer.builder();
//               .build;

         api.getVmApi().answerQuestion(vmURI, answer);
   }

   @Test(enabled = false)
   public void testGetRuntimeInfoSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/runtimeInfoSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getRuntimeInfoSection.xml", VCloudDirectorMediaType.RUNTIME_INFO_SECTION)
            .httpResponseBuilder().build());

      RuntimeInfoSection expected = getRuntimeInfoSection();

      assertEquals(api.getVmApi().getRuntimeInfoSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetScreenImage() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/screen")
            .acceptMedia(VCloudDirectorMediaType.ANY_IMAGE)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder()
            .headers(Multimaps.forMap(ImmutableMap.of("Content-Type", "image/png")))
            .message(new String(getScreenImage()))
            .build());
		
		byte[] expected = getScreenImage();
		
		assertEquals(api.getVmApi().getScreenImage(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetScreenTicket() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/screen/action/acquireTicket")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getScreenTicket.xml", VCloudDirectorMediaType.SCREEN_TICKET)
            .httpResponseBuilder().build());
		
		ScreenTicket expected = getScreenTicket();
		
		assertEquals(api.getVmApi().getScreenTicket(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSection.xml", VCloudDirectorMediaType.VIRTUAL_HARDWARE_SECTION)
            .httpResponseBuilder().build());

      VirtualHardwareSection expected = getVirtualHardwareSection();

		assertEquals(api.getVmApi().getVirtualHardwareSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSection.xml", VCloudDirectorMediaType.VIRTUAL_HARDWARE_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      VirtualHardwareSection section = getVirtualHardwareSection().toBuilder()
            .build();

		Task expected = modifyVirtualHardwareSectionTask();
		
		assertEquals(api.getVmApi().modifyVirtualHardwareSection(vmURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionCpu() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/cpu")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .httpResponseBuilder().build());

      RasdItem expected = getVirtualHardwareSectionCpu();

         assertEquals(api.getVmApi().getVirtualHardwareSectionCpu(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionCpu() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", vmId + "/virtualHardwareSection/cpu")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionCpuTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      RasdItem cpu = getVirtualHardwareSectionCpu(); // .toBuilder();
//               .build();

         Task expected = modifyVirtualHardwareSectionCpuTask();

         assertEquals(api.getVmApi().modifyVirtualHardwareSectionCpu(vmURI, cpu), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionDisks() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/disks")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionDisks();

         assertEquals(api.getVmApi().getVirtualHardwareSectionDisks(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionDisks() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection/disks")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionDisksTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList disks = getVirtualHardwareSectionDisks().toBuilder()
               .build();

         Task expected = modifyVirtualHardwareSectionDisksTask();

         assertEquals(api.getVmApi().modifyVirtualHardwareSectionDisks(vmURI, disks), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionMedia() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/media")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionMedia.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

      RasdItemsList expected = getVirtualHardwareSectionMedia();

      assertEquals(api.getVmApi().getVirtualHardwareSectionMedia(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionMemory() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/memory")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .httpResponseBuilder().build());

      RasdItem expected = getVirtualHardwareSectionMemory();

         assertEquals(api.getVmApi().getVirtualHardwareSectionMemory(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionMemory() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection/memory")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionMemoryTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      RasdItem memory = getVirtualHardwareSectionCpu(); // .toBuilder();
//               .build();

         Task expected = modifyVirtualHardwareSectionMemoryTask();

         assertEquals(api.getVmApi().modifyVirtualHardwareSectionMemory(vmURI, memory), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionNetworkCards() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/networkCards")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionNetworkCards();

         assertEquals(api.getVmApi().getVirtualHardwareSectionNetworkCards(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionNetworkCards() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection/networkCards")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionNetworkCardsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList networkCards = getVirtualHardwareSectionNetworkCards().toBuilder()
               .build();

         Task expected = modifyVirtualHardwareSectionNetworkCardsTask();

         assertEquals(api.getVmApi().modifyVirtualHardwareSectionNetworkCards(vmURI, networkCards), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionSerialPorts() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/serialPorts")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionSerialPorts();

         assertEquals(api.getVmApi().getVirtualHardwareSectionSerialPorts(vmURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionSerialPorts() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection/serialPorts")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionSerialPortsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList serialPorts = getVirtualHardwareSectionSerialPorts().toBuilder()
               .build();

         Task expected = modifyVirtualHardwareSectionSerialPortsTask();

         assertEquals(api.getVmApi().modifyVirtualHardwareSectionSerialPorts(vmURI, serialPorts), expected);
   }

   public static Vm getVm() {
      // FIXME Does not match XML
      Vm vm = Vm.builder()
            .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vm-d0e2b6b9-4381-4ddc-9572-cdfae54059be"))
//            .link(Link.builder()
//                     .href(URI.create())
//                     .build())
            .build();

//      <Link rel="power:powerOn" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/power/action/powerOn"/>
//      <Link rel="deploy" type="application/vnd.vmware.vcloud.deployVAppParams+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/deploy"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.vAppNetwork+xml" name="orgNet-cloudsoft-External" href="https://mycloud.greenhousedata.com/api/network/2a2e2da4-446a-4ebc-a086-06df7c9570f0"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.controlAccess+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/controlAccess/"/>
//      <Link rel="controlAccess" type="application/vnd.vmware.vcloud.controlAccess+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/controlAccess"/>
//      <Link rel="recompose" type="application/vnd.vmware.vcloud.recomposeVAppParams+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/recomposeVApp"/>
//      <Link rel="up" type="application/vnd.vmware.vcloud.vdc+xml" href="https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"/>
//      <Link rel="edit" type="application/vnd.vmware.vcloud.vApp+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be"/>
//      <Link rel="remove" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.owner+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/owner"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.metadata+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/metadata"/>
      
      return vm;
   }

   public static Task modifyVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task deleteVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task consolidateVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static ControlAccessParams controlAccessParams() {
      ControlAccessParams params = ControlAccessParams.builder()
            .build();

      return params;
   }

   public static Task deployTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task discardSuspendedStateTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task installVMwareToolsTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task recomposeVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task relocateTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task undeployTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task upgradeHardwareVersionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task powerOffTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task powerOnTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task rebootTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task resetTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task shutdownTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task suspendTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static ControlAccessParams getControlAccessParams() {
      ControlAccessParams params = ControlAccessParams.builder()
            .build();

      return params;
   }

   public static GuestCustomizationSection getGuestCustomizationSection() {
      GuestCustomizationSection section = GuestCustomizationSection.builder()
            .build();

      return section;
   }

   public static Task modifyGuestCustomizationSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static LeaseSettingsSection getLeaseSettingsSection() {
      LeaseSettingsSection section = LeaseSettingsSection.builder()
            .build();

      return section;
   }

   public static Task modifyLeaseSettingsSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task ejectMediaTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task insertMediaTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static NetworkConfigSection getNetworkConfigSection() {
      NetworkConfigSection section = NetworkConfigSection.builder()
            .build();

      return section;
   }

   public static Task modifyNetworkConfigSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static NetworkConnectionSection getNetworkConnectionSection() {
      NetworkConnectionSection section = NetworkConnectionSection.builder()
            .build();

      return section;
   }

   public static Task modifyNetworkConnectionSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static NetworkSection getNetworkSection() {
      NetworkSection section = NetworkSection.builder()
            .build();

      return section;
   }

   public static OperatingSystemSection getOperatingSystemSection() {
      OperatingSystemSection section = OperatingSystemSection.builder()
            .build();

      return section;
   }

   public static Task modifyOperatingSystemSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Owner getOwner() {
      Owner owner = Owner.builder()
            .build();

      return owner;
   }

   public static Task modifyOwnerTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static ProductSectionList getProductSections() {
      ProductSectionList sectionItems = ProductSectionList.builder()
            .build();

      return sectionItems;
   }

   public static Task modifyProductSectionsTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static VmPendingQuestion getPendingQuestion() {
      VmPendingQuestion question = VmPendingQuestion.builder()
            .build();

      return question;
   }

   public static VmQuestionAnswer answerQuestion() {
      VmQuestionAnswer answer = null; // = VmQuestionAnswer.builder() 
//            .build();

      return answer;
   }

   public static RuntimeInfoSection getRuntimeInfoSection() {
      RuntimeInfoSection section = RuntimeInfoSection.builder()
            .build();

      return section;
   }

   public static byte[] getScreenImage() {
      byte[] image = new byte[0];

      return image;
   }

   public static ScreenTicket getScreenTicket() {
      ScreenTicket ticket = null; // = ScreenTicket.builder();
//            .build();

      return ticket;
   }

   public static StartupSection getStartupSection() {
      StartupSection section = null; // = StartupSection.builder();
//            .build();

      return section;
   }

   public static Task modifyStartupSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static VirtualHardwareSection getVirtualHardwareSection() {
      VirtualHardwareSection section = VirtualHardwareSection.builder()
            .build();

      return section;
   }

   public static Task modifyVirtualHardwareSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItem getVirtualHardwareSectionCpu() {
      RasdItem cpu = RasdItem.builder()
            .build();

      return cpu;
   }

   public static Task modifyVirtualHardwareSectionCpuTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionDisks() {
      RasdItemsList disks = RasdItemsList.builder()
            .build();

      return disks;
   }

   public static Task modifyVirtualHardwareSectionDisksTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionMedia() {
      RasdItemsList media = RasdItemsList.builder()
            .build();

      return media;
   }

   public static RasdItem getVirtualHardwareSectionMemory() {
      RasdItem memory = RasdItem.builder()
            .build();

      return memory;
   }

   public static Task modifyVirtualHardwareSectionMemoryTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionNetworkCards() {
      RasdItemsList networkCards = RasdItemsList.builder()
            .build();

      return networkCards;
   }

   public static Task modifyVirtualHardwareSectionNetworkCardsTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionSerialPorts() {
      RasdItemsList serialPorts = RasdItemsList.builder()
            .build();

      return serialPorts;
   }

   public static Task modifyVirtualHardwareSectionSerialPortsTask() {
      return task("id", "name", "description", "status", "operation", "operationName", "startTime");
   }

   /** Used by other methods to create a custom {@link Task} object. */
   private static Task task(String taskId, String name, String description, String status, String operation, String operationName, String startTime) {
      Task task = Task.builder()
            .error(Error.builder().build())
            .org(Reference.builder().build())
            .owner(Reference.builder().build())
            .user(Reference.builder().build())
            .params(null)
            .progress(0)
            .status(status)
            .operation(operation)
            .operationName(operationName)
            .startTime(dateService.iso8601DateParse(startTime))
            .endTime(null)
            .expiryTime(null)
            .tasks(Sets.<Task>newLinkedHashSet())
            .description(description)
            .name(name)
            .id("urn:vcloud:" + taskId)
            .href(URI.create(endpoint + "/task/" + taskId))
            .links(Sets.<Link>newLinkedHashSet())
            .build();

      return task;
   }
}
