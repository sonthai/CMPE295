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

  	Body {"quantity": (number), "email": "" (for mobile case)}
	
where quantity will indicate how many result returned. If it is not specified, it will return 5 results by default.
If the quantity is higher than the available images, it will return all the available products
else it will return the number of products being requested

	Response: 

	      {
		    "responseCode": "OK",
		    
		    "responseMsg": "Retrieve result from engine",
		    
		    "data": [
		    
			{
			    "id": 251,
			    "image": "aero_womentop_1.jpg",
			    "brand": 12,
			    "price": 12,
			    "productName": "Long Sleeve Heathered Henley "
			},
			
			{
			    "id": 154,
			    "image": "levi_slim_fit_v-neck_tees.jpg",
			    "brand": 19,
			    "price": 10,
			    "productName": "Slim Fit V-neck Tees"
			}
			
		    ]
		    
		}
	
		   
