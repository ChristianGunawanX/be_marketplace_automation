package endpoints;

import api.ApiManager;
import io.restassured.response.Response;

public class Endpoints {
    private static final String TASKS_PATH = "/dev/data-marketplace/bsc/api/v1/tasks";
    private static final String SIZE_PATH = "/dev/data-marketplace/bsc/api/v1/size";
    private static final String LIBRARIES_PATH = "/dev/data-marketplace/bsc/api/v1/libraries"; 
    private static final String LICENSES_PATH = "/dev/data-marketplace/bsc/api/v1/licenses";
    private static final String LANGUAGES_PATH = "/dev/data-marketplace/bsc/api/v1/languages"; 
    private static final String MODALITIES_PATH = "/dev/data-marketplace/bsc/api/v1/modalities";
    private static final String FORMATS_PATH = "/dev/data-marketplace/bsc/api/v1/formats"; 
    private static final String PAYMENT_TOKEN_PATH = "/dev/data-marketplace/bsc/api/v1/queryPaymentTokenList"; 
    private static final String DATASETS_FILTER_PATH = "/dev/data-marketplace/bsc/api/v1/datasets/filter"; 

    public static Response getTasks() {
        return ApiManager.get(TASKS_PATH);
    }

    public static Response getSize() {
        return ApiManager.get(SIZE_PATH);
    }

    public static Response getLibraries() {
        return ApiManager.get(LIBRARIES_PATH);
    }

    public static Response getLicenses() {
        return ApiManager.get(LICENSES_PATH);
    }

    public static Response getLanguages() {
        return ApiManager.get(LANGUAGES_PATH);
    }

    public static Response getModalities() {
        return ApiManager.get(MODALITIES_PATH);
    }

    public static Response getFormats() {
        return ApiManager.get(FORMATS_PATH);
    }

    public static Response queryPaymentTokenList() {
        return ApiManager.get(PAYMENT_TOKEN_PATH);
    }

    public static Response filterDatasets(Object payload) {
        return ApiManager.post(DATASETS_FILTER_PATH, payload);
    }
}
