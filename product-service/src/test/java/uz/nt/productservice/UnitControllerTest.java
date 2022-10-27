package uz.nt.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.netty.handler.codec.string.LineSeparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import shared.libs.dto.JWTResponseDto;
import shared.libs.dto.ResponseDto;
import shared.libs.dto.UnitDto;
import uz.nt.productservice.dto.LoginDto;
import uz.nt.productservice.feign.UserFeign;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
//@RequiredArgsConstructor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserFeign userFeign;

    private static String token;
    private static ObjectMapper objectMapper;
    private static JsonMapper jsonMapper;


    @PostConstruct
    public void injecting(){
        objectMapper = new ObjectMapper();
        jsonMapper = new JsonMapper();

        log.info("Annotation \"PostConstruct\" is worked. Objects are injected.");
    }

    @Test
    @Order(1)
    public void token(){
        LoginDto loginDto = LoginDto.builder()
                .username("sardorbroo").password("password").build();

        ResponseDto<JWTResponseDto> responseDto = userFeign.token(loginDto);

        Assertions.assertNotNull(responseDto);
        Assertions.assertNotNull(responseDto.getResponseData());
        Assertions.assertEquals(true, responseDto.getSuccess());

        token = responseDto.getResponseData().getToken();
    }


    @Test
    @Order(3)
    public void addNewUnit(){
        UnitDto unitDto = UnitDto.builder()
                .name("Litr").shortName("L")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String entity;

        try {
            entity = objectMapper.writeValueAsString(unitDto);
        } catch (JsonProcessingException e) {
            log.error("[Error while converting DTO to String] Message: {} | Cause: {}",
                    e.getMessage(), e.getCause());

            throw new RuntimeException(e);
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                new MockHttpServletRequestBuilder(HttpMethod.POST, "http://localhost:8003/api/unit");
                .post("/unit")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .accept("application/json")
                .content(entity);
        String response;

        try {
            response = mvc
                    .perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("responseData")))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

        } catch (Exception e) {
            log.error("[Error while testing controller with MockMvc.perform()] Message: {} | Cause: {}",
                    e.getMessage(), e.getCause());

            throw new RuntimeException(e);
        }

//        JsonMapper jsonMapper = new JsonMapper();
        ObjectReader reader = jsonMapper.readerFor(new TypeReference<ResponseDto<UnitDto>>() {});

        ResponseDto<UnitDto> data;

        try {
            data = reader.readValue(response);
            Assertions.assertNotNull(data);
            Assertions.assertEquals(true, data.getSuccess());
            Assertions.assertNotNull(data.getResponseData());


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    public void checkPaginationController(){
        Map<String, List<String>> map = Map.of(
                "page", List.of(String.valueOf(0)),
                "size", List.of(String.valueOf(10))
        );

        MultiValueMap<String, String> params = new MultiValueMapAdapter<>(map);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/unit/pagination")
                                .params(params)
                                .header("Authorization", "Bearer " + token)
                                .accept("application/json");

        String response;
        try {
            response = mvc
                    .perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ObjectReader objectReader = jsonMapper.readerFor(new TypeReference<ResponseDto<Page<UnitDto>>>() {});

        ResponseDto<Page<UnitDto>> responseDto = null;

        try {
            responseDto = objectReader.readValue(response);

            Assertions.assertNotNull(responseDto);
            Assertions.assertNotNull(responseDto.getResponseData());
            Assertions.assertEquals(true, responseDto.getSuccess());
            Assertions.assertInstanceOf(PageRequest.class, responseDto.getResponseData());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
