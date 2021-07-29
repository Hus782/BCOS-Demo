# BCOS-Demo
## A simple voting demo app using FISCO BCOS. 
Contract is written in solidity, client side is using the JAVA SDK.

### To run the project 
1. Follow the steps in [搭建第一个区块链网络](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)
2. Clone the repository inside the ~fisco directory
3. Clear old certificates and add the new ones
    ```
    rm -r voting-app/src/test/resources/conf/*
    cp -r nodes/127.0.0.1/sdk/* voting-app/src/test/resources/conf
    ```
4. Build the project
```
cd voting-app
./gradlew build
```
5. Run the project
```
cd dist
type bash run.sh to see the usage
```

##### Supported commands are:
1.	deploy – used to deploy the contract
2.	vote – used to vote for a voting item, needs an ID argument
3.	getItems – get a list of all the voting items 
4.	demo – performs all the above mentioned operations, needs an ID argument

##### Notes:
Every time the init() function is called a new client with a new address is created for some reason.

