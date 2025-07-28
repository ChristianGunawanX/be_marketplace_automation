package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import utils.ConfigReader;

public class BaseTest {
    protected ConfigReader configReader;

    @BeforeSuite(alwaysRun = true)
    @Parameters("baseUri")
    public void setup(String baseUri) {
        RestAssured.baseURI = baseUri;

        String environment;
        if (baseUri.contains("testnet")) {
            environment = "testnet";
        } else if (baseUri.contains("data.pundi.ai")) {
            environment = "mainnet";
        } else if (baseUri.contains("8.222.154.96")) {
            environment = "mainnet";
        } else {
            throw new IllegalArgumentException("Unknown baseUri provided: " + baseUri);
        }

        configReader = new ConfigReader(environment);
    }
}
