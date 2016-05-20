This is a simple backup management system for windows machines in a LAN network. The system works with a server that manages all the backups and their lifecycles. Every machine has their own backups, and the central server determines which machine is going to run their backups. It provides a number of slots of backups (configurable), so only a limited number of backups can run at the same time (even if they are from different machines)

## Quick Start

There are three different modules in this project. 

The first module is the central server. It has a REST API to communicate with the other modules, manages the backups and provides role based security. Timeout is the time it takes for a backup to execute. When timeout is reached, the slot is freed 

To start it, run:

	./gradlew clean build
	java -jar server/build/libs/server-1.0-SNAPSHOT.jar -Dtimeout={time-in-milliseconds}

The server uses a MySQL database. To change the properties, open the file in server/src/main/resources/application.yml and customize the url, username and password attributes. There is no need to do anything to initialize the database. Hibernate is generating the DDL, and for default a Admin user is persisted

The second module is the dashboard. It is a simple CLI app that helps the admin to manage the backups. It connects with the central server.

To start it, run:

	./gradlew clean build
	java -jar dashboard/build/libs/dashboard-1.0-SNAPSHOT.jar -Dserver.address={http://server.com}

The server is http://localhost:8080 by default

Finally, the last module is the machine, where each machine will have one installed. Like the dashboard, it have to connect with the central server. So you will need to customize the server address, username and client

To start it, run:
	
	./gradlew clean build
	java -jar machine/build/libs/machine-1.0-SNAPSHOT.jar -Dserver.address={http://server.com} -Dserver.username={username} -Dserver.password={password}



## Instructions to use the dashboard

The dashboard is a Shell application. It has a helper built in for each command.

To show a list of available commands, run:

	help

To do any action, it is necessary to login first. For default, the server creates a admin user with username=admin and password=admin. To login, run:

	login --u admin --p admin

Once the admin user is logged, it can create, show, enable, disable, delete backups and create and list machines. The process is very straightforward. To show the list of parameters to create a backup for example, run:

	help backup create

The format to send dates is yyyy-MM-dd'T'HH:mm:ss.SSSZ
The format for the source and destination parameters is smb://{domain}/{path}