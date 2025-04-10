package stanism.marketplace.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class VippsService {
        /**
         * Base URL for Vipps API endpoints.
         */
        private final String baseUrl;

        /**
         * Merchant serial number for Vipps authentication.
         */
        private final String merchantSerialNumber;

        /**
         * Subscription key for Vipps API access.
         */
        private final String subscriptionKey;

        /**
         * Client ID for Vipps authentication.
         */
        private final String clientId;

        /**
         * Client secret for Vipps authentication.
         */
        private final String clientSecret;

        /**
         * RestTemplate instance for making HTTP requests.
         */
        private final RestTemplate restTemplate;

        public VippsService(
                        @Value("${vipps.base.url}") String baseUrl,
                        @Value("${vipps.merchant.serial.number}") String merchantSerialNumber,
                        @Value("${vipps.subscription.key}") String subscriptionKey,
                        @Value("${vipps.client.id}") String clientId,
                        @Value("${vipps.client.secret}") String clientSecret) {
                this.baseUrl = baseUrl;
                this.merchantSerialNumber = merchantSerialNumber;
                this.subscriptionKey = subscriptionKey;
                this.clientId = clientId;
                this.clientSecret = clientSecret;
                this.restTemplate = new RestTemplate();
        }

        private String getAccessToken() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("client_id", clientId);
                headers.set("client_secret", clientSecret);
                headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
                headers.set("Merchant-Serial-Number", merchantSerialNumber);
                System.out.println("clientId: " + clientId);
                System.out.println("clientSecret: " + clientSecret);
                System.out.println("subscriptionKey: " + subscriptionKey);
                System.out.println("merchantSerialNumber: " + merchantSerialNumber);
                // -H 'client_id: YOUR-CLIENT-ID' \
                // -H 'client_secret: YOUR-CLIENT-SECRET' \
                // -H 'Ocp-Apim-Subscription-Key: YOUR-SUBSCRIPTION-KEY' \
                // -H 'Merchant-Serial-Number: YOUR-MSN' \
                HttpEntity<String> entity = new HttpEntity<>("", headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                                baseUrl + "/accesstoken/get",
                                HttpMethod.POST,
                                entity,
                                Map.class);
                System.out.println("response: " + response);

                return (String) response.getBody().get("access_token");
        }

        public Map<String, Object> initiatePayment(String orderId, double amount, String description) {
                String accessToken = getAccessToken();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
                headers.set("Merchant-Serial-Number", merchantSerialNumber);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("merchantInfo", Map.of(
                                "merchantSerialNumber", merchantSerialNumber,
                                "callbackPrefix", "https://your-domain.com/api/vipps/callback",
                                "fallBack", "https://your-domain.com/order/" + orderId));

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("orderId", orderId);
                transaction.put("amount", (long) (amount * 100)); // Convert to øre
                transaction.put("transactionText", description);

                requestBody.put("transaction", transaction);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                return restTemplate.exchange(
                                baseUrl + "/ecomm/v2/payments",
                                HttpMethod.POST,
                                entity,
                                Map.class).getBody();
        }

        public Map<String, Object> refundPayment(String orderId, double amount) {
                String accessToken = getAccessToken();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
                headers.set("Merchant-Serial-Number", merchantSerialNumber);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("amount", Map.of(
                                "currency", "NOK",
                                "value", (long) (amount * 100) // Convert to øre
                ));

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                return restTemplate.exchange(
                                baseUrl + "/ecomm/v2/payments/" + orderId + "/refund",
                                HttpMethod.POST,
                                entity,
                                Map.class).getBody();
        }
}
