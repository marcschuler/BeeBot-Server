# BeeBot
![Build](https://github.com/marcschuler/BEE-Teamspeak-Bot-Server/workflows/Maven%20Build/badge.svg)
## Setup
### Docker
A Dockerfile is included. Run "mvn package" to compile the jar and execute the Dockerfile.

### Port
The default port is 8080 and can be changed with the ``server.port`` property

### MongoDB
Use the environment variable to set the mongodb server uri

    spring.data.mongodb.uri=mongodb+srv://<username>>:<password>@<server>/<database>


### I just want to run the BeeBot Server
``java -Dspring.data.mongodb.uri=mongodb://USERNAME:PASSWORD@localhost:27017/DATABASE -jar beebot-2.1.0.jar``

Please replace username, password and DATABASE. Create a user in mongodb first.
## Client
The client is available at https://beebot.karlthebee.de
### Encryption
If you run the Client from a HTTPS site (as above) you should run the BeeBot Server over SSL
One option is to use an Apache server with LetsEncrypt.

``ProxyPass /beebot http://127.0.0.1:8080/``

## Login
At first startup the server will generate a (SECRET) login token. The token will be saved on file "token.txt" and on
 stdout
 on startup. You have to use the token to login in the client.
 To generate a new token, delete the token.txt file and restart the server
 
 
 
 ## Dynamic Names
 The Bot allows to use dynamic names. For example create an private channel worker with the following name
 >Channel of %client_name%
 
 When a user joins, the Bot will create a channel called _Channel of karlthebee_, because it replaces everything
  inside % signs with actual data
 
 #### Client
 client_name : The name of the client
 
 client_id : The client id (int)
 
 client_ip : The client ip
 
 client_uid : The clients unique id
 
 
 #### Channel
 channel_name : The channel name
 
 channel_id : The channels id (int)
 
 #### General
 \n : A new line
 
 \t : A tab
