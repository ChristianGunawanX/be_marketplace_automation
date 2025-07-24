package tests;

import base.BaseTest;
import endpoints.Endpoints;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

public class TasksTest extends BaseTest {
    @Test(description = "GET /v1/tasks endpoint")
    public void getV1Tasks() {
        Response response = Endpoints.getTasks();
        response.then().log().body();

        List<Object> data = response.path("data");
        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertNotNull(data);
    }

    @Test(description = "GET /v1/size endpoint")
    public void getV1Size() {
        Response response = Endpoints.getSize();
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);

        Assert.assertEquals(response.path("data.minRows"), "<1K");
        Assert.assertEquals(response.path("data.maxRows"), "1M");
    }

    @Test(description = "GET /v1/libraries endpoint")
    public void getV1Libraries() {
        Response response = Endpoints.getLibraries();
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);

        List<Object> libraries = response.path("data.libraries");
        Assert.assertNotNull(libraries);
    }

    @Test(description = "GET /v1/licenses endpoint")
    public void getV1Licenses() {
        List<String> expectedLicenses = Arrays.asList(
                "agpl-3.0", "apache-2.0", "apple-amlr", "apple-ascl", "bsd-3-clause-clear",
                "cc", "cc-by-4.0", "cc-by-nc-4.0", "cc-by-sa-4.0", "cdla-sharing-1.0",
                "creativeml-openrail-m", "epl-1.0", "gpl", "gpl-2.0", "gpl-3.0",
                "llama2", "mit", "openrail", "other", "pddl", "unlicense"
        );

        Response response = Endpoints.getLicenses();
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        List<String> licenses = response.path("data.licenses");
        Assert.assertNotNull(licenses);
        Assert.assertEquals(licenses, expectedLicenses);
    }

    @Test(description = "GET /v1/languages endpoint")
    public void getV1Languages() {
        List<String> expectedLanguage = Arrays.asList("English", "French");

        Response response = Endpoints.getLanguages();
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);

        List<String> languages = response.path("data.languages");

        Assert.assertNotNull(languages);
        Assert.assertEquals(languages.size(), 2);
        Assert.assertEquals(languages, expectedLanguage);
    }

    @Test(description = "Verify GET /v1/modalities endpoint")
    public void getV1Modalities() {
        List<String> expectedModalities = Arrays.asList("audio", "image", "text");
        Response response = Endpoints.getModalities();
        response.then().log().body();
        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        List<String> actualModalities = response.path("data.modalities");
        Assert.assertEquals(actualModalities, expectedModalities);
    }

    @Test(description = "Verify GET /v1/formats endpoint")
    public void getV1Formats() {
        List<String> expectedFormats = Arrays.asList("csv", "jsonl", "parquet", "txt", "zip");

        Response response = Endpoints.getFormats();
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals(response.path("data.formats"), expectedFormats);
    }

    @Test(description = "Verify GET /v1/queryPaymentTokenList endpoint")
    public void getQueryPaymentTokenList() {
        List<String> expectedSymbols = Arrays.asList("PUNDIAI", "BNB", "USDT");
        Response response = Endpoints.queryPaymentTokenList();
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals(response.path("msg"), "success");

        List<String> actualSymbols = response.path("data.symbol");
        Assert.assertTrue(actualSymbols.containsAll(expectedSymbols));

        List<Object> decimalsList = response.path("data.decimals");
        for (Object decimal : decimalsList) {
            Assert.assertTrue(decimal instanceof Integer);
        }

        List<String> addresses = response.path("data.contractAddress");
        for (String address : addresses) {
            Assert.assertTrue(address.startsWith("0x"));
        }
    }

    @Test(description = "Verify POST v1/datasets/filter with a single filter")
    public void filterDatasetsWithSingleFilter() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("page", 1);
        payload.put("limit", 20);
        payload.put("name", "");
        payload.put("modality", Arrays.asList("text"));
        payload.put("sort", "recentlyCreated");

        Response response = Endpoints.filterDatasets(payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertTrue(response.path("data.total") instanceof Integer);
        Assert.assertEquals(response.path("data.page"), payload.get("page"));
        Assert.assertTrue(response.path("data.pages") instanceof Integer);
        Assert.assertEquals(response.path("data.limit"), payload.get("limit"));
        Assert.assertNotNull(response.path("data.data"));
    }

    @Test(description = "Verify POST v1/datasets/filter with a multiple filter")
    public void filterDatasetsWithMultipleFilters() {
        // 1. Create the request payload using a Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("page", 1);
        payload.put("limit", 20);
        payload.put("name", "");
        payload.put("modality", Arrays.asList("text"));
        payload.put("language", Arrays.asList("English"));
        payload.put("sort", "recentlyCreated");

        Response response = Endpoints.filterDatasets(payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), 200);
        Assert.assertTrue(response.path("data.total") instanceof Integer);
        Assert.assertEquals(response.path("data.page"), payload.get("page"));
        Assert.assertTrue(response.path("data.pages") instanceof Integer);
        Assert.assertEquals(response.path("data.limit"), payload.get("limit"));
        Assert.assertNotNull(response.path("data.data"));
    }
}
