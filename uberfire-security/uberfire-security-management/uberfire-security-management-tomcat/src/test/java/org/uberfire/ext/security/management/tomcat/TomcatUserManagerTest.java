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

package org.uberfire.ext.security.management.tomcat;

import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.commons.io.FileUtils;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.BaseTest;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This tests create temporary working copy of the "tomcat-users.xml" file as the tests are run using the real tomcat admin api for realm management. 
 */
@RunWith(MockitoJUnitRunner.class)
public class TomcatUserManagerTest extends BaseTest {

    protected static final String USERS_FILE_PATH = "org/uberfire/ext/security/management/tomcat/";
    protected static final String USERS_FILE_NAME = "tomcat-users.xml";

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    private static File elHome;
    
    @Spy
    private TomcatUserManager usersManager = new TomcatUserManager();

    @BeforeClass
    public static void initWorkspace() throws Exception {
        elHome = tempFolder.newFolder("uf-extensions-security-management-tomcat");
    }

    @Before
    public void setup() throws Exception {
        URL templateURL = Thread.currentThread().getContextClassLoader().getResource(USERS_FILE_PATH + USERS_FILE_NAME);
        File templateFile = new File(templateURL.getFile());
        FileUtils.cleanDirectory(elHome);
        FileUtils.copyFileToDirectory(templateFile, elHome);
        String full = new File(elHome, templateFile.getName()).getAbsolutePath();
        String path = full.substring(0, full.lastIndexOf(File.separator));
        String name = full.substring(full.lastIndexOf(File.separator) + 1, full.length());
        Map<String, String> props = new HashMap<String, String>(1);
        props.put("org.uberfire.ext.security.management.tomcat.users-file-path", path);
        props.put("org.uberfire.ext.security.management.tomcat.users-file-name", name);
        System.setProperty(BaseTomcatManager.CATALINA_BASE_PROPERTY, "");
        usersManager.loadConfig(new ConfigProperties(props));
        usersManager.initialize(userSystemManager);
    }
    
    @After
    public void finishIt() throws Exception {
        usersManager.destroy();
    }

    @Test
    public void testCapabilities() {
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_SEARCH_USERS), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_READ_USER), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_UPDATE_USER), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_ADD_USER), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_DELETE_USER), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_MANAGE_ATTRIBUTES), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_ASSIGN_GROUPS), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_CHANGE_PASSWORD), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_ASSIGN_ROLES), CapabilityStatus.UNSUPPORTED);
    }

    @Test
    public void testAttributes() {
        final Collection<UserManager.UserAttribute> USER_ATTRIBUTES = Arrays.asList(BaseTomcatManager.USER_FULLNAME);
        Collection<UserManager.UserAttribute> attributes = usersManager.getAttributes();
        assertEquals(attributes,USER_ATTRIBUTES);
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPageZero() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 0, 5);
        AbstractEntityManager.SearchResponse<User> response = usersManager.search(request);
    }
    
    @Test
    public void testSearchAll() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 1, 5);
        AbstractEntityManager.SearchResponse<User> response = usersManager.search(request);
        assertNotNull(response);
        List<User> users = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, 4);
        assertTrue(!hasNextPage);
        assertEquals(users.size(), 4);
        Set<User> expectedUsers = new HashSet<User>(4);
        expectedUsers.add(create("admin"));
        expectedUsers.add(create("user1"));
        expectedUsers.add(create("user2"));
        expectedUsers.add(create("user3"));
        assertThat(new HashSet<User>(users), is(expectedUsers));
    }

    @Test
    public void testGetAdmin() {
        User user = usersManager.get("admin");
        assertUser(user, "admin");
    }

    @Test
    public void testGetUser1() {
        User user = usersManager.get("user1");
        assertUser(user, "user1");
    }

    @Test
    public void testGetUser2() {
        User user = usersManager.get("user2");
        assertUser(user, "user2");
    }

    @Test
    public void testGetUser3() {
        User user = usersManager.get("user3");
        assertUser(user, "user3");
    }

    @Test
    public void testCreateUser() {
        User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user4");
        User userCreated = usersManager.create(user);
        assertUser(userCreated, "user4");
    }

    @Test
    public void testUpdateUser() {
        User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user1");
        Map<String, String> properties = new HashMap<String, String>(1);
        properties.put(BaseTomcatManager.ATTRIBUTE_USER_FULLNAME, "user1 Full Name");
        when(user.getProperty(BaseTomcatManager.ATTRIBUTE_USER_FULLNAME)).thenReturn("user1 Full Name");
        when(user.getProperties()).thenReturn(properties);
        User userUpdated = usersManager.update(user);
        assertNotNull(userUpdated);
        assertEquals("user1 Full Name", userUpdated.getProperty(BaseTomcatManager.ATTRIBUTE_USER_FULLNAME));
    }

    @Test( expected = UserNotFoundException.class )
    public void testDeleteUser() {
        usersManager.delete("user1");
        usersManager.get("user1");
    }

    @Test
    public void testAssignGroups() {
        Collection<String> groups = new ArrayList<String>();
        groups.add("role1");
        groups.add("role3");
        usersManager.assignGroups("user1", groups);
        Set<Group> result = usersManager.get("user1").getGroups();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testChangePassword() {
        usersManager.changePassword("user1", "newUser1Password");
        MemoryUserDatabase database = usersManager.getDatabase();
        org.apache.catalina.User catalinaUser = usersManager.getUser(database, "user1");
        assertEquals("newUser1Password", catalinaUser.getPassword());
    }
    
    private User create(String username) {
        return new UserImpl(username);
    }
    
    private void assertUser(User user, String username) {
        assertNotNull(user);
        assertEquals(user.getIdentifier(), username);
    }
    
}
