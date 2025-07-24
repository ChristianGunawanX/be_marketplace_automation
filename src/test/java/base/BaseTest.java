package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    @BeforeSuite(alwaysRun = true)
    public void setup() {
        RestAssured.baseURI = "https://testnet-label-ixer7wzhr8xfd9ws.pundix.com";
    }
}
