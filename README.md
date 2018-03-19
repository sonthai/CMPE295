# CMPE295
### Set up dev environment
- Download and install [Vagrant](https://www.vagrantup.com/downloads.html)
- How to use vagrant
  + Start vagrant: vagrant up
  + Reload provision: vagrant provision OR vagrant reload --provision (provision script is under dev-vm/.provision directory)
  + Destroy vm: vagrant destroy

### Install Apache Cassandra in local machine
##### Note: You only need to install if you run the app in the standalone mode. If you use vagrant to deploy the api, you will not need install Apache Cassandra reparately.

### Available Rest APIs
- Login  GET /user/login
	Body: {"user_name": "", "user_password": ""}
- Register POST /user/register
	Body: {"user_name": "", "user_password": "", "user_email": ""}
  
- Process recommendation: POST /customer/processData
	Body: {"id": "", 
		   "image": "" , 
		   "user_id": "" (Note: only mobile needs to send user_id over network
		  }
		   
