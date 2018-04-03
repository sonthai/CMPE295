# CMPE295
### Set up dev environment
- Download and install [Vagrant](https://www.vagrantup.com/downloads.html)
- How to use vagrant
  + Start vagrant: vagrant up
  + Reload provision: vagrant provision OR vagrant reload --provision (provision script is under dev-vm/.provision directory)
  + Destroy vm: vagrant destroy


### Available Rest APIs

#### User API
- Login 

POST /user/login

	Body: {"email": "", "password": ""}
	
- Register 

POST /user/register

	Body: {"email": "", "password": ""}
	
- Update password

POST /user/update_password

	Body: {"email": "", "old_password": "", "new_password": ""}
	
#### Product API
- Get info of one product by name

POST /product/info

	Body: {"name": ""}
	
where name is the image name of one product

- Get info of multiple products

POST /product/list

	Body: {"name": ""}
	
where name is the comma separated image names of products

#### Data Processing API  
- Process recommendation: 

POST /customer/processData

	Body: {"id": "", 
	       "image": "" , 
	       "email": "" (Note: only mobile needs to send user_id over network)
	      }
	      
- Get Product recommendation (This is only for Camera)

GET /customer/recommend

  	Body {"quantity": (number)}
	
where quantity will indicate how many result return.
If the quantity is higher than the available images, it will return all the available products
else it will return the number products requested
		   
