# permission-app

### How to run the app
1. First, install docker and docker-compose in your enviroment.
2. Then, clone the repo.
3. Inside the repo root folder run docker-compose up --build (make sure you dont have nothing running on ports 8080, 6379. 27017).
4. The desired function is a GET rest endpoint http://localhost:8080/api/permissions/v1/{email}, call it from the browser
or from where you desire, passing the desired email. (Unfortunately swagger is not available due to a springfox 2.92 malfunction with springboot 2.2.0);
5. The sample data file is located in the src/main/resources folder, in case any change is applied, just don't forget to run command 3 again.
