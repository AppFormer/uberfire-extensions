JBoss Wildfly / EAP provider for Users and groups management services
=====================================================================

Introduction
------------
The classes from this package contains the users and groups management services implementations for JBoss Wildfly / EAP.                               
 
There exist two JBoss Wildfly / EAP's service implementations, use any of the following ones that fits better in your environment:                       

**JBoss Wildfly / EAP provider based on property files**                  

This provider supports realm types based on properties files (the default ones in JBoss server), such as `application-users.properties` or `application-roles.properties`.                                 

* The users management services implementations are provided by class `org.uberfire.ext.security.management.wildfly.properties.WildflyUserManagementService`.              
* The concrete users management service implementation is provided by class `org.uberfire.ext.security.management.wildfly.properties.WildflyUserPropertiesManager`, that **binds the Wildfly / EAP users for the given realm into application users**.                   
* The concrete roles management service implementation is provided by class `org.uberfire.ext.security.management.wildfly.properties.WildflyGroupPropertiesManager`, that **binds the Wildfly / EAP roles for the given realm into application groups**.                   

**JBoss Wildfly / EAP provider based on property files and CLI**                   

This provider supports realm types based on properties files, as the above one, but instead of specifying the file system paths for the users and roles property files to manage, 
 you can just provide JBoss Wildfly native interface connection attributes, and this implementation will use CLI commands (use of CLI remote Java API) to discover the paths of the realm property files for you.                              

* The users management services implementations are provided by class `org.uberfire.ext.security.management.wildfly.cli.WildflyCLIUserManagementService`.              
* The concrete users management service implementation is provided by class `org.uberfire.ext.security.management.wildfly.cli.WildflyUserPropertiesCLIManager`, that **binds the Wildfly / EAP users for the given realm into application users**.                   
* The concrete roles management service implementation is provided by class `org.uberfire.ext.security.management.wildfly.cli.WildflyGroupPropertiesCLIManager`, that **binds the Wildfly / EAP roles for the given realm into application groups or roles**.                   

Installation notes
------------------

If you are deploying the application in a Wildfly 8.X or an EAP 6.4, make sure you add the Wildfly / EAP controller and domain base module dependencies (provided by the server) into your application's classpath, 
 by creating file or adding the following module dependencies in *jboss-deployment-descriptor.xml*:                                   

        <jboss-deployment-structure>
            <deployment>
                <dependencies>
                    <module name="org.jboss.as.controller-client"/>
                    <module name="org.jboss.as.domain-management"/>
                    <module name="org.jboss.sasl"/>
                    <module name="org.jboss.msc"/>
                    <module name="org.jboss.dmr"/>
                </dependencies>
            </deployment>
        </jboss-deployment-structure>

And ensure you are excluding all Wildfly / EAP controller and domain libraries, if any, from your web applications classpath, as are provided by the container.                   

Usage
-----

**Using the Wildfly / EAP provider based on property files**

To use this provider implementation for the users and groups management services, please choose one of the following options:               

a) Specify the concrete provider to use by adding a properties file named `security-management.properties` in your web application root classpath. 
(e.g. `src/main/resources/security-management.properties`), with the following keys and your concrete provider name as value:                               

    org.uberfire.ext.security.management.api.userManagementServices=WildflyUserManagementService

b) Specify the following Java system properties at container startup:        
 
    -Dorg.uberfire.ext.security.management.api.userManagementServices=WildflyUserManagementService

In order to use any existing users/roles properties files from a JBoss Wildfly / EAP instance, the following system properties are required to be present at startup:                 

* `org.uberfire.ext.security.management.wildfly.properties.realm` - The name of the realm to use. Property is not mandatory. Defaults to `ApplicationRealm`.                  
* `org.uberfire.ext.security.management.wildfly.properties.users-file-path` - The absolute file path for the users properties file to manage. Property is mandatory. Defaults to `./standalone/configuration/application-users.properties`.                        
* `org.uberfire.ext.security.management.wildfly.properties.groups-file-path` - The absolute file path for the groups properties file to manage. Property is mandatory. Defaults to `./standalone/configuration/application-roles.properties`.                        

**Using Wildfly / EAP provider based on property files and CLI**

To use this provider implementation for the users and groups management services, please choose one of the following options:               

a) Specify the concrete provider to use by adding a properties file named `security-management.properties` in your web application root classpath. 
(e.g. `src/main/resources/security-management.properties`), with the following keys and your concrete provider name as value:                               

    org.uberfire.ext.security.management.api.userManagementServices=WildflyCLIUserManagementService


b) Specify the following Java system properties at container startup:        
        
    -Dorg.uberfire.ext.security.management.api.userManagementServices=WildflyCLIUserManagementService                                                                                   

In order to use any existing users/roles properties files from a JBoss Wildfly / EAP instance, the following system properties are required to be present at startup:                 

* `org.uberfire.ext.security.management.wildfly.cli.host` - The native administration interface host. Property is not mandatory. Defaults to `localhost`.                                       
* `org.uberfire.ext.security.management.wildfly.cli.port` - The native administration interface port. Property is not mandatory. Defaults to `9990`.                                       
* `org.uberfire.ext.security.management.wildfly.cli.user` - The native administration interface username. Property is not mandatory. No default value provided. Only use it if you need to specify the administration credentials for managing the server instance.                                                      
* `org.uberfire.ext.security.management.wildfly.cli.password` - The native administration interface user's password. Property is not mandatory. No default value provided. Only use it if you need to specify the administration credentials for managing the server instance.                                                                                 
* `org.uberfire.ext.security.management.wildfly.cli.realm` - The realm used by the application's security context. Property is not mandatory. Default value is `ApplicationRealm`.                                         

Provider capabilities
---------------------
The Wildfly / EAP provider for users and groups management services provides the following features:                   

**User service capabilities**
* User search - Can search or list users. Search by `username`.          
* Read user - Can read a user            
* Create user - Can add new users            
* Update user - Can update a user            
* Delete user - Can delete a user            
* Group assignment - Can manage groups for a user            
* Role assignment - Can manage roles for a user             
* Change password - Can change user's password            

**Group service capabilities**
* Group search - Can search or list groups. Search by `name` attribute.             
* Read group - Can read a group            
* Create group - Can add new groups            
* Delete group - Can delete a group            

Notes
-----
* Java7+
* This implementation has been tested for JBoss Wildfly version `8.1.0.Final` and `8.2.0.Final` and for JBoss EAP version `6.4`.                
