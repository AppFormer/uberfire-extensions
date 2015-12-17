/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.wildfly.properties;

import org.apache.commons.io.FileUtils;
import org.jboss.errai.security.shared.api.Group;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.BaseTest;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;
import org.uberfire.ext.security.management.wildfly.properties.WildflyGroupPropertiesManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WildflyGroupsPropertiesManagerTest extends BaseTest {

    protected static final String GROUPS_FILE = "org/uberfire/ext/security/management/wildfly/application-roles.properties";
    protected String groupsFilePath;
    
    @Spy
    private WildflyGroupPropertiesManager groupsPropertiesManager = new WildflyGroupPropertiesManager();

    private static File elHome;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void initWorkspace() throws Exception {
        elHome = tempFolder.newFolder("uf-extensions-security-management-wildfly");
    }
    
    @Before
    public void setup() throws Exception {
        URL templateURL = Thread.currentThread().getContextClassLoader().getResource(GROUPS_FILE);
        File templateFile = new File(templateURL.getFile());
        FileUtils.cleanDirectory(elHome);
        FileUtils.copyFileToDirectory(templateFile, elHome);
        this.groupsFilePath = new File(elHome, templateFile.getName()).getAbsolutePath();
        doReturn(groupsFilePath).when(groupsPropertiesManager).getGroupsFilePath();
        groupsPropertiesManager.initialize(userSystemManager);
    }

    @After
    public void finishIt() throws Exception {
        groupsPropertiesManager.destroy();
    }

    @Test
    public void testCapabilities() {
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_SEARCH_GROUPS), CapabilityStatus.ENABLED);
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_READ_GROUP), CapabilityStatus.ENABLED);
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_ADD_GROUP), CapabilityStatus.UNSUPPORTED);
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_DELETE_GROUP), CapabilityStatus.UNSUPPORTED);
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_UPDATE_GROUP), CapabilityStatus.UNSUPPORTED);
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPageZero() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 0, 5);
        AbstractEntityManager.SearchResponse<Group> response = groupsPropertiesManager.search(request);
    }
    
    @Test
    public void testSearchAll() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 1, 5);
        AbstractEntityManager.SearchResponse<Group> response = groupsPropertiesManager.search(request);
        assertNotNull(response);
        List<Group> groups = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, 5);
        assertTrue(!hasNextPage);
        assertEquals(groups.size(), 5);
        List<Group> expectedGroups = createGroupList("ADMIN", "admin", "role3", "role2", "role1");
        assertEquals(new HashSet<Group>(expectedGroups), new HashSet<Group>(groups));
    }
    
    @Test
    public void testGroupsForUser() {
        Set<Group> groups = groupsPropertiesManager.getGroupsForUser("admin");
        assertGroupsForUser(groups, new String[]{"admin", "ADMIN"});
        groups = groupsPropertiesManager.getGroupsForUser("user1");
        assertGroupsForUser(groups, new String[]{"role1"});
        groups = groupsPropertiesManager.getGroupsForUser("user2");
        assertGroupsForUser(groups, new String[]{"role1", "role2"});
        groups = groupsPropertiesManager.getGroupsForUser("user3");
        assertGroupsForUser(groups, new String[]{"role3"});
    }

    @Test
    public void testGet() {
        assertGet("admin");
        assertGet("role1");
        assertGet("role2");
        assertGet("role3");
        assertGet("ADMIN");
    }

    @Test(expected = UnsupportedServiceCapabilityException.class)
    public void testCreateGroup() {
        Group group = mock(Group.class);
        when(group.getName()).thenReturn("role10");
        groupsPropertiesManager.create(group);
    }

    @Test(expected = UnsupportedServiceCapabilityException.class)
    public void testUpdateGroup() {
        Group group = mock(Group.class);
        when(group.getName()).thenReturn("role10");
        groupsPropertiesManager.update(group);
    }

    @Test(expected = UnsupportedServiceCapabilityException.class)
    public void testDeleteGroup() {
        groupsPropertiesManager.delete("role3");
    }

    private List<Group> createGroupList(String... names) {
        if (names != null) {
            List<Group> result = new ArrayList<Group>(names.length);
            for (int x = 0; x < names.length; x++) {
                String name = names[x];
                Group g = SecurityManagementUtils.createGroup(name);
                result.add(g);
            }
            return result;
        }
        return null;
    }
    
    private void assertGet(String name) {
        Group group = groupsPropertiesManager.get(name);
        assertNotNull(group);
        assertEquals(group.getName(), name);
    }

    private void assertGroupsForUser(Set<Group> groupsSet, String[] groups) {
        assertNotNull(groupsSet);
        assertEquals(groupsSet.size(), groups.length);
        int x = 0;
        for (Group g : groupsSet) {
            String gName = groups[x];
            assertEquals(g.getName(), gName);
            x++;
        }
    }
    
    
    
}
