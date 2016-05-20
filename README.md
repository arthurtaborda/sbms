This is a simple backup management system for windows machines in a LAN network. The system works with a server that manages all the backups and their lifecycles. Every machine has their own backups, and the central server determines which machine is going to run their backups. It provides a number of slots of backups (configurable), so only a limited number of backups can run at the same time (even if they are from different machines)

## Quick Start

There are three different modules in this project. 

The first module is the central server. It has a REST API to communicate with the other modules, manages the backups and provides role based security. Timeout is the time it takes for a backup to execute. When timeout is reached, the backup 

To start it, run:

	./gradlew clean build
	java -jar build/libs/api-1.0-SNAPSHOT.jar -Dtimeout={time-in-milliseconds}

This module uses a MySQL database. To change the properties, open the file in crossover-api/src/main/resources/application.yml and customize the url, username and password attributes. There is no need to do anything to initialize the database. Hibernate is generating the DDL, and for default a Admin user is persisted

The second module is the dashboard. It is a simple CLI app that helps the admin to manage the backups. It connects with the central server.

To start it, run:

	./gradlew clean build
	java -jar build/libs/dashboard-1.0-SNAPSHOT.jar -Dserver.address={http://server.com}

The server is http://localhost:8080 by default

Finally, the last module is the client, where each machine will have one installed. Like the dashboard, it have to connect with the central server. So you will need to customize the server address, username and client

To start it, run:
	
	./gradlew clean build
	java -jar build/libs/client-1.0-SNAPSHOT.jar -Dserver.address={http://server.com} -Dserver.username={username} -Dserver.password={password}



## Opinion about the project

This was made as a challenge from crossover. It was an interesting challenge, with many things to think upfront about code design and architecture decisions. Some requirements were not very clear, so some of them needed some improvisation. For example, it was not clear what was the type of the source directory for the backup. It says that it is necessary to provide a user and a password for it, so I assumed it is on a protected directory on the network. The protocol used for both source and destination was SMB (since it says the machines are windows based). Another not so clear requirement is about the queue. It is not specifying if the queue must be in the server or in the machine clients.


## Things that are missing

It wasn't possible to implement a web app with AngularJS/ReactJS simply because I don't know them very well yet. So I decided to do the admin app as a CLI. It is fairly simple to use.
Another thing that is missing is integration test. I made many unit tests, and in some few cases had to use Mockito to mock database repository classes, but I didn't do any integration tests simply because I had no time. I could not do the demo too because I can't record my voice with my computer, I don't know why. I will do if you accept it after I send the files