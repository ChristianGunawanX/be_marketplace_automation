package tests;

import base.BaseTest;
import endpoints.Endpoints;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.params.HttpProtocolParamBean;
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

    @Test(description = "Verify POST v1/downloadedDatasets/filter with Positive Case")
    public void getDownloadedDatasetsPositive() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 1);
        queryParams.put("limit", 50);

        Map<String, Object> payload = new HashMap<>();
        payload.put("responser", "0x66AB45A8d222Fb3a79f19e0231d3e5b4E27F4c25");
        payload.put("requester", "0xe50C136967C6e97BC7c53465a1f1E850A8eb020a");
        payload.put("tokenId", "280");

        Response response = Endpoints.filterDownloadedDatasets(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals(response.path("data.page"), queryParams.get("page"));
        Assert.assertEquals(response.path("data.limit"), String.valueOf(queryParams.get("limit")));

        List<Map<String, Object>> dataList = response.path("data.data");
        for (Map<String, Object> item : dataList) {
            Assert.assertEquals(item.get("requester"), payload.get("requester"));
            List<Map<String, Object>> responsesList = (List<Map<String, Object>>) item.get("responses");
            for (Map<String, Object> responseItem : responsesList) {
                Assert.assertEquals(responseItem.get("responser"), payload.get("responser"));
            }
        }
    }

    @Test(description = "Verify POST v1/downloadedDatasets/filter with Wrong tokenId Case")
    public void getDownloadedDatasetsWrongTokenId() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 1);
        queryParams.put("limit", 50);

        Map<String, Object> payload = new HashMap<>();
        payload.put("responser", "0x66AB45A8d222Fb3a79f19e0231d3e5b4E27F4c25");
        payload.put("requester", "0xe50C136967C6e97BC7c53465a1f1E850A8eb020a");
        payload.put("tokenId", "0");

        Response response = Endpoints.filterDownloadedDatasets(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals(response.path("data.page"), queryParams.get("page"));
        Assert.assertEquals(response.path("data.limit"), String.valueOf(queryParams.get("limit")));
        Assert.assertNotNull(response.path("data.data"));
        Assert.assertTrue(((List<?>) response.path("data.data")).isEmpty());
    }

    @Test(description = "Verify POST v1/downloadedDatasets/filter with Missing responser and requester")
    public void getDownloadedDatasetsMissingFields() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 1);
        queryParams.put("limit", 50);

        Map<String, Object> payload = new HashMap<>();
        payload.put("tokenId", "280");

        Response response = Endpoints.filterDownloadedDatasets(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), 200);
        Assert.assertEquals(response.path("data.page"), queryParams.get("page"));
        Assert.assertEquals(response.path("data.limit"), String.valueOf(queryParams.get("limit")));
        Assert.assertNotNull(response.path("data.data"));
    }

    @Test(description = "Verify POST v1/usageRightMakerAsk/filter with Positive Case")
    public void getUsageRightMakerAskPositive() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 1);
        queryParams.put("limit", 50);

        Map<String, Object> payload = new HashMap<>();
        payload.put("collectionAddress", "0xd5fE186dA67742eC77d48E9a600e355E1C0DC5f6");
        payload.put("itemId", "295");

        Response response = Endpoints.filterUsageRightMakerAsk(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals(response.path("data.page"), queryParams.get("page"));
        Assert.assertEquals(response.path("data.limit"), queryParams.get("limit"));

        List<Map<String, Object>> dataList = response.path("data.data");
        for (Map<String, Object> item : dataList) {
            Assert.assertEquals(item.get("collectionAddress"), payload.get("collectionAddress"));
        }
    }

    @Test(description = "Verify POST v1/usageRightMakerAsk/filter with Wrong Collection Address")
    public void getUsageRightMakerAskWrongAddress() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 1);
        queryParams.put("limit", 50);

        Map<String, Object> payload = new HashMap<>();
        payload.put("collectionAddress", "0");
        payload.put("itemId", "295");

        Response response = Endpoints.filterUsageRightMakerAsk(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals(response.path("data.page"), queryParams.get("page"));
        Assert.assertEquals(response.path("data.limit"), queryParams.get("limit"));
        Assert.assertNotNull(response.path("data.data"));
        Assert.assertTrue(((List<?>) response.path("data.data")).isEmpty());
    }

    @Test(description = "Verify POST v1/usageRightMakerAsk/filter with Wrong Token Id")
    public void getUsageRightMakerAskWrongTokenId() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 1);
        queryParams.put("limit", 50);

        Map<String, Object> payload = new HashMap<>();
        payload.put("collectionAddress", "0xd5fE186dA67742eC77d48E9a600e355E1C0DC5f6");
        payload.put("itemId", "1");

        Response response = Endpoints.filterUsageRightMakerAsk(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals(response.path("data.page"), queryParams.get("page"));
        Assert.assertEquals(response.path("data.limit"), queryParams.get("limit"));
        Assert.assertNotNull(response.path("data.data"));
        Assert.assertTrue(((List<?>) response.path("data.data")).isEmpty());
    }

    @Test(description = "Verify POST v1/checkStatus")
    public void checkStatusOwnership() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 1);
        queryParams.put("limit", 50);

        Map<String, Object> payload = new HashMap<>();
        payload.put("user", "0xe50C136967C6e97BC7c53465a1f1E850A8eb020a");
        payload.put("filter", "ownership");

        Response response = Endpoints.checkStatus(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), 200);
        Assert.assertEquals(response.path("data.page"), queryParams.get("page"));
        Assert.assertEquals(response.path("data.limit"), queryParams.get("limit"));
        Assert.assertEquals(response.path("data.data[0].owner"), payload.get("user"));
    }
}
