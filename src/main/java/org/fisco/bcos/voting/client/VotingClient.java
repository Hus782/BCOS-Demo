package org.fisco.bcos.voting.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;
import org.fisco.bcos.voting.contract.Vote;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.net.URL;
import java.nio.file.Paths;

public class VotingClient {

    static Logger logger = LoggerFactory.getLogger(VotingClient.class);

    private BcosSDK bcosSDK;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;

    public void initialize() throws Exception {
        @SuppressWarnings("resource")
        ApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        bcosSDK = context.getBean(BcosSDK.class);
        client = bcosSDK.getClient(1);
        cryptoKeyPair = client.getCryptoSuite().createKeyPair();
        //client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
        String pemAccountFilePath = "conf/accounts/0xe4d66fcbcd5e292da8989270aa73ae2b19ceaa91.pem";
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        client.getCryptoSuite().loadAccount("pem", pemAccountFilePath, null);
       // logger.debug("create client for group1, account address is " + cryptoKeyPair.getAddress());
        //System.out.println("create client for group1, account address is " + cryptoKeyPair.getAddress());
        System.out.println("create client for group1, account address is " + client.getCryptoSuite().getCryptoKeyPair().getAddress());


    }

    public void deploy() {

        try {
            Vote vote = Vote.deploy(client, cryptoKeyPair);

            System.out.println(
                    "Deployed Vote successfully, contract address is " + vote.getContractAddress());

            saveAddress(vote.getContractAddress());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println("Deploying Vote contract failed, error message is  " + e.getMessage());
        }
    }
    public void getItemsCount() {
        try {
            String contractAddress = loadAddress();
            Vote vote = Vote.load(contractAddress, client, cryptoKeyPair);
            BigInteger size = vote.getItemsCount();
            //System.out.printf("Items size was %d \n", size);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

            System.out.printf(" getItemsCount() failed, error message is %s\n", e.getMessage());
        }
    }
    public void vote(BigInteger ID) {
        try{
            String contractAddress = loadAddress();
            Vote vote = Vote.load(contractAddress, client, cryptoKeyPair);
            System.out.printf("Submitting transaction \n");
            TransactionReceipt receipt = vote.vote(ID);
            Tuple1<String> res = vote.getVoteOutput(receipt);
            String msg = res.getValue1();
            if (!msg.isEmpty()){
                System.out.printf("%s \n", res.getValue1());
            }
            else{
                System.out.printf("You have voted already or Item ID is invalid! \n");
            }

        } catch (Exception e) {
            System.out.printf("Voting for item failed, error message is %s\n", e.getMessage());
        }
    }

    public void getItems() {
        try {
            String contractAddress = loadAddress();
            Vote vote = Vote.load(contractAddress, client, cryptoKeyPair);
            //BigInteger size = vote.getItem(ID);
            BigInteger size = vote.getItemsCount();//.add(BigInteger.ONE);
            //System.out.printf("Items size was %d \n", size);
            //Tuple2<Boolean, BigInteger> voters = vote.voters("0xe4d66fcbcd5e292da8989270aa73ae2b19ceaa91");
            //System.out.printf("voted: %s, vote: %s \n", voters.getValue1(), voters.getValue2().toString());
            System.out.printf("Voting items are: \n");

            BigInteger i ;
            for (i = BigInteger.valueOf(0);
                 i.compareTo(size) != 0;
                 i = i.add(BigInteger.ONE)) {

                Tuple3<String, BigInteger, BigInteger> result = vote.getItem(i);
                System.out.printf("Name: %s, ID: %s, Number of votes: %s \n", result.getValue1(), result.getValue2().toString(), result.getValue3().toString());
                //System.out.println(i);
            }


        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(" getItemsCount failed, error message is {}", e.getMessage());

            System.out.printf(" getItemsCount() failed, error message is %s\n", e.getMessage());
        }
    }
    public void getVoter(String address) {
        try {
            String contractAddress = loadAddress();
            Vote vote = Vote.load(contractAddress, client, cryptoKeyPair);
            Tuple2<Boolean, BigInteger> voter = vote.voters(client.getCryptoSuite().getCryptoKeyPair().getAddress());
            System.out.printf("Voter has voted=%s and voted for item %d \n", String.valueOf(voter.getValue1()),voter.getValue2());

        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(" getVoter() failed, error message is {}", e.getMessage());

            System.out.printf(" getVoter() failed, error message is %s\n", e.getMessage());
        }
    }

    public void saveAddress(String address) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.setProperty("address", address);
        final Resource contractResource = new ClassPathResource("contract.properties");
        FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
        prop.store(fileOutputStream, "contract address");
    }

    public String loadAddress() throws Exception {
        // load Asset contact address from contract.properties
        Properties prop = new Properties();
        final Resource contractResource = new ClassPathResource("contract.properties");
        prop.load(contractResource.getInputStream());

        String contractAddress = prop.getProperty("address");
        if (contractAddress == null || contractAddress.trim().equals("")) {
            throw new Exception(" load Vote contract address failed, please deploy it first. ");
        }
        logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println(
                "\t bash run.sh deploy");
        System.out.println(
                "\t bash run.sh vote :itemID");
        System.out.println(
                "\t bash run.sh vote 1");
        System.out.println(
                "\t bash run.sh getItems");
        System.out.println(
                "\t bash run.sh demo :itemID");
        System.out.println(
                "\t bash run.sh getVoter :addr");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
        Usage();
        }

        VotingClient client = new VotingClient();
        client.initialize();

        switch (args[0]) {
            case "deploy":
                client.deploy();
                break;
            case "vote":
                if (args.length < 2) {
                    Usage();
                }
                client.vote(new BigInteger(args[1]));
                break;
            case "getItems":
                client.getItems();
                break;
            case "getVoter":
                if (args.length < 2) {
                    Usage();
                }
                client.getVoter(args[1]);
                break;
            case "demo":
                if (args.length < 2) {
                    Usage();
                }
                client.deploy();
                client.getItems();
                client.vote(new BigInteger(args[1]));
                client.getItems();
                client.vote(new BigInteger(args[1]));
                client.getItems();

                break;
            default:
            {
                Usage();
            }
        }
        System.exit(0);
    }
}
