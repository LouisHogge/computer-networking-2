# Introduction to Computer Networking: Second part of the assignment

## Project Description
For this project, you are going to implement a simple pair of client-server able to perform DNS tunneling. You already completed a client prototype in the project first part, now in the second part, you will implement a DNS server.

## How to Use the Project
The server can be launched using the following command: *java Server \<owned domain name\>*, where "owned domain name" is a valid domain name under the responsability of your server. Thus, the server will only successfully reply to queries addressing this domain and its subdomains. 

For example:
```bash
java Server tnl.test
```
