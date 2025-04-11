package stanism.marketplace.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class for handling Vipps payment integration.
 * This service provides functionality to interact with the Vipps payment API,
 * including initiating payments and processing refunds.
 */
@Service
public class VippsService {

        /** Logger instance for logging service operations. */
        private static final Logger LOGGER = LoggerFactory.getLogger(VippsService.class);

        /** RestTemplate instance for making HTTP requests to the Vipps API. */
        private final RestTemplate restTemplate;

        /** Environment instance for accessing application properties. */
        private final Environment environment;

        /**
         * Constructs a new VippsService with the specified environment.
         * Initializes the RestTemplate and logs the configured properties.
         *
         * @param environment
         *                the Spring environment containing Vipps configuration
         */
        public VippsService(Environment environment) {
                this.environment = environment;
                this.restTemplate = new RestTemplate();
                LOGGER.info("VippsService initialized with properties:");
                LOGGER.info("Base URL: {}", getBaseUrl());
                LOGGER.info("Merchant Serial Number: {}", getMerchantSerialNumber());
                LOGGER.info("Client ID: {}", getClientId());
                LOGGER.info("Subscription Key: {}", getSubscriptionKey());
        }

        /**
         * Retrieves the Vipps API base URL from environment properties.
         *
         * @return the Vipps API base URL
         */
        private String getBaseUrl() {
                return environment.getProperty("VIPPS_BASE_URL");
        }

        /**
         * Retrieves the merchant serial number from environment properties.
         *
         * @return the merchant serial number
         */
        private String getMerchantSerialNumber() {
                return environment.getProperty("VIPPS_MERCHANT_SERIAL_NUMBER");
        }

        /**
         * Retrieves the subscription key from environment properties.
         *
         * @return the subscription key
         */
        private String getSubscriptionKey() {
                return environment.getProperty("VIPPS_SUBSCRIPTION_KEY");
        }

        /**
         * Retrieves the client ID from environment properties.
         *
         * @return the client ID
         */
        private String getClientId() {
                return environment.getProperty("VIPPS_CLIENT_ID");
        }

        /**
         * Retrieves the client secret from environment properties.
         *
         * @return the client secret
         */
        private String getClientSecret() {
                return environment.getProperty("VIPPS_CLIENT_SECRET");
        }

        /**
         * Retrieves an access token from the Vipps API.
         * This token is required for authenticated API requests.
         *
         * @return the access token
         * @throws Exception
         *                 if there is an error retrieving the access token
         */
        private String getAccessToken() {
                String baseUrl = getBaseUrl();
                String fullUrl = baseUrl + "/accesstoken/get";
                LOGGER.info("Attempting to get access token from URL: {}", fullUrl);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("client_id", getClientId());
                headers.set("client_secret", getClientSecret());
                headers.set("Ocp-Apim-Subscription-Key", getSubscriptionKey());
                headers.set("Merchant-Serial-Number", getMerchantSerialNumber());

                HttpEntity<String> entity = new HttpEntity<>("", headers);

                try {
                        ResponseEntity<Map> response = restTemplate.exchange(
                                        fullUrl,
                                        HttpMethod.POST,
                                        entity,
                                        Map.class);
                        return (String) response.getBody().get("access_token");
                } catch (Exception e) {
                        LOGGER.error("Error getting access token from URL: {}", fullUrl, e);
                        throw e;
                }
        }

        /**
         * Initiates a payment through the Vipps API.
         *
         * @param orderId
         *                the unique identifier for the order
         * @param amount
         *                the payment amount in NOK
         * @param description
         *                the description of the payment
         * @return a Map containing the payment response from Vipps
         */
        public Map<String, Object> initiatePayment(String orderId, double amount, String description) {
                String accessToken = getAccessToken();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("Ocp-Apim-Subscription-Key", getSubscriptionKey());
                headers.set("Merchant-Serial-Number", getMerchantSerialNumber());

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("merchantInfo", Map.of(
                                "merchantSerialNumber", getMerchantSerialNumber(),
                                "callbackPrefix", "https://your-domain.com/api/vipps/callback",
                                "fallBack", "https://your-domain.com/order/" + orderId));

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("orderId", orderId);
                transaction.put("amount", (long) (amount * 100)); // Convert to øre
                transaction.put("transactionText", description);

                requestBody.put("transaction", transaction);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                LOGGER.info("Request body: {}", getBaseUrl() + "/ecomm/v2/payments");

                return restTemplate.exchange(
                                getBaseUrl() + "/ecomm/v2/payments",
                                HttpMethod.POST,
                                entity,
                                Map.class).getBody();
        }

        /**
         * Processes a refund for a previously completed payment.
         *
         * @param orderId
         *                the unique identifier of the order to refund
         * @param amount
         *                the amount to refund in NOK
         * @return a Map containing the refund response from Vipps
         */
        public Map<String, Object> refundPayment(String orderId, double amount) {
                String accessToken = getAccessToken();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("Ocp-Apim-Subscription-Key", getSubscriptionKey());
                headers.set("Merchant-Serial-Number", getMerchantSerialNumber());

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("amount", Map.of(
                                "currency", "NOK",
                                "value", (long) (amount * 100) // Convert to øre
                ));

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                return restTemplate.exchange(
                                getBaseUrl() + "/ecomm/v2/payments/" + orderId + "/refund",
                                HttpMethod.POST,
                                entity,
                                Map.class).getBody();
        }
}
