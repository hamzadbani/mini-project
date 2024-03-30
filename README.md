# Mini-Project REST API with Spring Boot
***This mini-project aims to develop a series of APIs for user generation, importing users from a JSON file into a database, user authentication with JWT token generation, and accessing user profiles securely.***

## Objectives
The objectives of the mini-project are as follows:

Generate users with realistic data.
Import these users into a database while avoiding duplicates.
Authenticate users and generate JWT tokens.
Allow secure access to user profiles.
Endpoints
### 1. User Generation
Method: GET
URL: /api/users/generate
Content-Type: application/json
Secured: no
Parameters:
count: number
This first REST endpoint generates a JSON file containing the specified number of users with the following information:

First Name
Last Name
Date of Birth
City
Country
Avatar (URL of an image)
Company
Job Position
Mobile Number
Username
Email Address
Random Password (between 6 and 10 characters)
Role (admin or user)

### 2. Upload User File and Create Users in Database
Method: POST
URL: /api/users/batch
Content-Type: multipart/form-data
Secured: no
Parameters:
file: multipart-file
This second endpoint allows the upload of the previously generated JSON file. Users are added to a database while checking for duplicates based on email address and username. A JSON response is returned summarizing the total number of records, successfully imported records, and records that failed to import.

### 3. User Login + JWT Generation
Method: POST
URL: /api/auth
Content-Type: application/json
Request Body:
username: string
password: string
This third endpoint authenticates a user and generates a valid JWT token.

### 4. View My Profile
Method: GET
URL: /api/users/me
Secured: yes
This fourth endpoint allows securely accessing the profile of the associated user using the JWT token.

### 5. View User Profiles
Method: GET
URL: /api/users/{username}
Secured: yes
When the JWT token corresponds to a user with admin role, it is possible to view the profile of any other user.


**Java 17 and Spring Boot 3.2.** 

**H2 database.**

**The application start on port 9090.**

**Expose a Swagger endpoint.**

**Utilize java Faker library to generate users.**
