1- Create a Database using the file provided
2- copy mysql driver jar into Tomcat lib folder
3- Modify server.xml file to add the following

<Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase"/>
      
      <Realm className="org.apache.catalina.realm.JDBCRealm"
                   driverName="com.mysql.jdbc.Driver"
                   connectionURL="jdbc:mysql://localhost/profilecontainer"
                   connectionName="root"
                   connectionPassword=""
                   userTable="users"
                   userNameCol="username"
                   userCredCol="password"
                   userRoleTable="user_role"
             roleNameCol="ROLE_NAME" />


4- To enable a user to use this service they have to be in websuer group.