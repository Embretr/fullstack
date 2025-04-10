package stanism.marketplace.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import stanism.marketplace.service.VippsService;

import java.util.Map;

@RestController
@RequestMapping("/api/vipps")
public class VippsController {

    /**
     * Service for handling Vipps payment operations.
     */
    private final VippsService vippsService;

    public VippsController(VippsService vippsService) {
        this.vippsService = vippsService;
    }

    @PostMapping("/payment")
    public Map<String, Object> initiatePayment(
            @RequestParam String orderId,
            @RequestParam double amount,
            @RequestParam String description) {
        return vippsService.initiatePayment(orderId, amount, description);
    }

    @PostMapping("/refund")
    public Map<String, Object> refundPayment(
            @RequestParam String orderId,
            @RequestParam double amount) {
        return vippsService.refundPayment(orderId, amount);
    }

    @PostMapping("/callback")
    public void handleCallback(@RequestBody Map<String, Object> callbackData) {
        // Handle Vipps callback
        // This is where you'll update your order status based on the payment result
        String orderId = (String) callbackData.get("orderId");
        String status = (String) callbackData.get("status");
        // Update your order status based on the payment result
    }
}