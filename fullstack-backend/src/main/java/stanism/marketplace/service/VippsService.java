package stanism.marketplace.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stanism.marketplace.config.VippsProperties;

import java.util.HashMap;
import java.util.Map;

@Service
public class VippsService {
        /** Configuration properties for Vipps integration. */
        private final VippsProperties vippsProperties;

        /** RestTemplate instance for making HTTP requests. */
        private final RestTemplate restTemplate;

        public VippsService(VippsProperties vippsProperties) {
                this.vippsProperties = vippsProperties;
                this.restTemplate = new RestTemplate();
        }

        private String getAccessToken() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("client_id", vippsProperties.getClientId());
                headers.set("client_secret", vippsProperties.getClientSecret());
                headers.set("Ocp-Apim-Subscription-Key", vippsProperties.getSubscriptionKey());
                headers.set("Merchant-Serial-Number", vippsProperties.getMerchantSerialNumber());

                HttpEntity<String> entity = new HttpEntity<>("", headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                                vippsProperties.getBaseUrl() + "/accesstoken/get",
                                HttpMethod.POST,
                                entity,
                                Map.class);

                return (String) response.getBody().get("access_token");
        }

        public Map<String, Object> initiatePayment(String orderId, double amount, String description) {
                String accessToken = getAccessToken();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("Ocp-Apim-Subscription-Key", vippsProperties.getSubscriptionKey());
                headers.set("Merchant-Serial-Number", vippsProperties.getMerchantSerialNumber());

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("merchantInfo", Map.of(
                                "merchantSerialNumber", vippsProperties.getMerchantSerialNumber(),
                                "callbackPrefix", "https://your-domain.com/api/vipps/callback",
                                "fallBack", "https://your-domain.com/order/" + orderId));

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("orderId", orderId);
                transaction.put("amount", (long) (amount * 100)); // Convert to øre
                transaction.put("transactionText", description);

                requestBody.put("transaction", transaction);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                return restTemplate.exchange(
                                vippsProperties.getBaseUrl() + "/ecomm/v2/payments",
                                HttpMethod.POST,
                                entity,
                                Map.class).getBody();
        }

        public Map<String, Object> refundPayment(String orderId, double amount) {
                String accessToken = getAccessToken();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("Ocp-Apim-Subscription-Key", vippsProperties.getSubscriptionKey());
                headers.set("Merchant-Serial-Number", vippsProperties.getMerchantSerialNumber());

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("amount", Map.of(
                                "currency", "NOK",
                                "value", (long) (amount * 100) // Convert to øre
                ));

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                return restTemplate.exchange(
                                vippsProperties.getBaseUrl() + "/ecomm/v2/payments/" + orderId + "/refund",
                                HttpMethod.POST,
                                entity,
                                Map.class).getBody();
        }
}
