# BeeBot

## Setup
### Docker
A Dockerfile is included. Run "mvn package" to compile the jar and execute the Dockerfile.

### MongoDB
Use the environment variable to set the mongodb server uri

    spring.data.mongodb.uri=mongodb+srv://<username>>:<password>@<server>/<database>
Or set the variables independently
    
    spring.data.mongodb.port=27017
    spring.data.mongodb.database=<database>
    spring.data.mongodb.username=<username>
    spring.data.mongodb.password=<password>

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