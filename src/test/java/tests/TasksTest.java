package tests;

import base.BaseTest;
import endpoints.Endpoints;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.params.HttpProtocolParamBean;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.ClassImpl;

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
        String expectedMinRows = configReader.getProperty("size.minRows");
        String expectedMaxRows = configReader.getProperty("size.maxRows");

        Response response = Endpoints.getSize();
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);

        Assert.assertEquals(response.path("data.minRows"), expectedMinRows);
        Assert.assertEquals(response.path("data.maxRows"), expectedMaxRows);
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
        String expectedLicensesString = configReader.getProperty("licenses.list").replaceFirst("^licenses\\.list=", "").trim();
        List<String> expectedLicenses = Arrays.stream(expectedLicensesString.split("\\|"))
                .map(String::trim)
                .collect(Collectors.toList());

        Response response = Endpoints.getLicenses();
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);

        List<String> licenses = response.path("data.licenses");
        licenses = licenses.stream()
                .map(s -> s.trim())
                .collect(Collectors.toList());

        if (expectedLicenses.size() != licenses.size()) {
            System.out.println("DEBUGGING LIST SIZE MISMATCH:");
            System.out.println("Expected Size: " + expectedLicenses.size());
            System.out.println("Actual Size  : " + licenses.size());

            Set<String> expectedSet = new HashSet<>(expectedLicenses);
            Set<String> actualSet = new HashSet<>(licenses);

            // Find items that are in the actual API response but NOT in your properties file
            Set<String> extraItemsInResponse = new HashSet<>(actualSet);
            extraItemsInResponse.removeAll(expectedSet);
            if (!extraItemsInResponse.isEmpty()) {
                System.out.println("--> Extra items found in API response: " + extraItemsInResponse);
            }

            // Find items that are in your properties file but NOT in the actual API response
            Set<String> missingItemsFromResponse = new HashSet<>(expectedSet);
            missingItemsFromResponse.removeAll(actualSet);
            if (!missingItemsFromResponse.isEmpty()) {
                System.out.println("--> Missing items from API response: " + missingItemsFromResponse);
            }
            System.out.println("--- END DEBUGGING ---");
        }

        Assert.assertEqualsNoOrder(
                licenses.toArray(new String[0]),
                expectedLicenses.toArray(new String[0])
        );
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
        String expectedFormatsString = configReader.getProperty("formats.list");
        List<String> expectedFormats = Arrays.asList(expectedFormatsString.split(","));

        Response response = Endpoints.getFormats();
        response.then().log().body();
        List<String> actualFormats = response.path("data.formats");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEqualsNoOrder(
                actualFormats.toArray(new String[0]),
                expectedFormats.toArray(new String[0])
        );
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
        String page = configReader.getProperty("page");
        String limit = configReader.getProperty("limit");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", page);
        queryParams.put("limit", limit);

        Map<String, Object> payload = new HashMap<>();
        payload.put("responser", "0x66AB45A8d222Fb3a79f19e0231d3e5b4E27F4c25");
        payload.put("requester", "0xe50C136967C6e97BC7c53465a1f1E850A8eb020a");
        payload.put("tokenId", "0");

        Response response = Endpoints.filterDownloadedDatasets(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("data.page"), Integer.parseInt(page));
        Assert.assertEquals(response.path("data.limit"),limit);
        Assert.assertNotNull(response.path("data.data"));
        Assert.assertTrue(((List<?>) response.path("data.data")).isEmpty());
    }

    @Test(description = "Verify POST v1/downloadedDatasets/filter with Missing responser and requester")
    public void getDownloadedDatasetsMissingFields() {
        String page = configReader.getProperty("page");
        String limit = configReader.getProperty("limit");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", page);
        queryParams.put("limit", limit);

        Map<String, Object> payload = new HashMap<>();
        payload.put("tokenId", "280");

        Response response = Endpoints.filterDownloadedDatasets(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), 200);
        Assert.assertEquals((Integer) response.path("data.page"), Integer.parseInt(page));
        Assert.assertEquals(response.path("data.limit"), limit);
        Assert.assertNotNull(response.path("data.data"));
    }

    @Test(description = "Verify POST v1/usageRightMakerAsk/filter with Positive Case")
    public void getUsageRightMakerAskPositive() {
        String page = configReader.getProperty("page");
        String limit = configReader.getProperty("limit");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", page);
        queryParams.put("limit", limit);

        Map<String, Object> payload = new HashMap<>();
        payload.put("collectionAddress", "0xd5fE186dA67742eC77d48E9a600e355E1C0DC5f6");
        payload.put("itemId", "295");

        Response response = Endpoints.filterUsageRightMakerAsk(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("data.page"), Integer.parseInt(page));
        Assert.assertEquals((Integer) response.path("data.limit"), Integer.parseInt(limit));

        List<Map<String, Object>> dataList = response.path("data.data");
        for (Map<String, Object> item : dataList) {
            Assert.assertEquals(item.get("collectionAddress"), payload.get("collectionAddress"));
        }
    }

    @Test(description = "Verify POST v1/usageRightMakerAsk/filter with Wrong Collection Address")
    public void getUsageRightMakerAskWrongAddress() {
        String page = configReader.getProperty("page");
        String limit = configReader.getProperty("limit");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", page);
        queryParams.put("limit", limit);

        Map<String, Object> payload = new HashMap<>();
        payload.put("collectionAddress", "0");
        payload.put("itemId", "295");

        Response response = Endpoints.filterUsageRightMakerAsk(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("data.page"), Integer.parseInt(page));
        Assert.assertEquals((Integer) response.path("data.limit"), Integer.parseInt(limit));
        Assert.assertNotNull(response.path("data.data"));
        Assert.assertTrue(((List<?>) response.path("data.data")).isEmpty());
    }

    @Test(description = "Verify POST v1/usageRightMakerAsk/filter with Wrong Token Id")
    public void getUsageRightMakerAskWrongTokenId() {
        String page = configReader.getProperty("page");
        String limit = configReader.getProperty("limit");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", page);
        queryParams.put("limit", limit);

        Map<String, Object> payload = new HashMap<>();
        payload.put("collectionAddress", "0xd5fE186dA67742eC77d48E9a600e355E1C0DC5f6");
        payload.put("itemId", "1");

        Response response = Endpoints.filterUsageRightMakerAsk(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("data.page"), Integer.parseInt(page));
        Assert.assertEquals((Integer) response.path("data.limit"), Integer.parseInt(limit));
        Assert.assertNotNull(response.path("data.data"));
        Assert.assertTrue(((List<?>) response.path("data.data")).isEmpty());
    }

    @Test(description = "Verify POST v1/checkStatus")
    public void checkStatusOwnership() {
        String userAddress = configReader.getProperty("user");
        String page = configReader.getProperty("page");
        String limit = configReader.getProperty("limit");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", page);
        queryParams.put("limit", limit);

        Map<String, Object> payload = new HashMap<>();
        payload.put("user", userAddress);
        payload.put("filter", "ownership");

        Response response = Endpoints.checkStatus(queryParams, payload);
        response.then().log().body();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals((Integer) response.path("code"), 200);
        Assert.assertEquals((Integer) response.path("data.page"), Integer.parseInt(page));
        Assert.assertEquals((Integer) response.path("data.limit"), Integer.parseInt(limit));
        Assert.assertEquals(response.path("data.data[0].owner"), payload.get("user"));
    }
}
