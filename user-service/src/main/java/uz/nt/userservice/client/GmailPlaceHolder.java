package uz.nt.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shared.libs.dto.ResponseDto;

@FeignClient(url = "http://localhost:8888",name = "gmail-service")
public interface GmailPlaceHolder {
    @PostMapping("/send")
    ResponseDto<String> sendToGmailAndGetVerifyCode(@RequestParam String gmail,@RequestParam Integer code);
}
