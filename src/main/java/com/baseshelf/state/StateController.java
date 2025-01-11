package com.baseshelf.state;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("stores/{store-id}/state-code")
@RequiredArgsConstructor
public class StateController {
    private final StateRepository stateRepository;

    @Bean
    @Order(value = 8)
    public CommandLineRunner insertStateCodes(StateRepository stateRepository) {
        return args -> {
            List<StateCode> states = new ArrayList<>();

            states.add(StateCode.builder().stateCode("AN").stateName("Andaman and Nicobar Islands").tinNumber(35).build());
            states.add(StateCode.builder().stateCode("AP").stateName("Andhra Pradesh").tinNumber(28).build());
            states.add(StateCode.builder().stateCode("AD").stateName("Andhra Pradesh (New)").tinNumber(37).build());
            states.add(StateCode.builder().stateCode("AR").stateName("Arunachal Pradesh").tinNumber(12).build());
            states.add(StateCode.builder().stateCode("AS").stateName("Assam").tinNumber(18).build());
            states.add(StateCode.builder().stateCode("BH").stateName("Bihar").tinNumber(10).build());
            states.add(StateCode.builder().stateCode("CH").stateName("Chandigarh").tinNumber(4).build());
            states.add(StateCode.builder().stateCode("CT").stateName("Chattisgarh").tinNumber(22).build());
            states.add(StateCode.builder().stateCode("DN").stateName("Dadra and Nagar Haveli").tinNumber(26).build());
            states.add(StateCode.builder().stateCode("DD").stateName("Daman and Diu").tinNumber(25).build());
            states.add(StateCode.builder().stateCode("DL").stateName("Delhi").tinNumber(7).build());
            states.add(StateCode.builder().stateCode("GA").stateName("Goa").tinNumber(30).build());
            states.add(StateCode.builder().stateCode("GJ").stateName("Gujarat").tinNumber(24).build());
            states.add(StateCode.builder().stateCode("HR").stateName("Haryana").tinNumber(6).build());
            states.add(StateCode.builder().stateCode("HP").stateName("Himachal Pradesh").tinNumber(2).build());
            states.add(StateCode.builder().stateCode("JK").stateName("Jammu and Kashmir").tinNumber(1).build());
            states.add(StateCode.builder().stateCode("JH").stateName("Jharkhand").tinNumber(20).build());
            states.add(StateCode.builder().stateCode("KA").stateName("Karnataka").tinNumber(29).build());
            states.add(StateCode.builder().stateCode("KL").stateName("Kerala").tinNumber(32).build());
            states.add(StateCode.builder().stateCode("LD").stateName("Lakshadweep Islands").tinNumber(31).build());
            states.add(StateCode.builder().stateCode("MP").stateName("Madhya Pradesh").tinNumber(23).build());
            states.add(StateCode.builder().stateCode("MH").stateName("Maharashtra").tinNumber(27).build());
            states.add(StateCode.builder().stateCode("MN").stateName("Manipur").tinNumber(14).build());
            states.add(StateCode.builder().stateCode("ME").stateName("Meghalaya").tinNumber(17).build());
            states.add(StateCode.builder().stateCode("MI").stateName("Mizoram").tinNumber(15).build());
            states.add(StateCode.builder().stateCode("NL").stateName("Nagaland").tinNumber(13).build());
            states.add(StateCode.builder().stateCode("OR").stateName("Odisha").tinNumber(21).build());
            states.add(StateCode.builder().stateCode("PY").stateName("Pondicherry").tinNumber(34).build());
            states.add(StateCode.builder().stateCode("PB").stateName("Punjab").tinNumber(3).build());
            states.add(StateCode.builder().stateCode("RJ").stateName("Rajasthan").tinNumber(8).build());
            states.add(StateCode.builder().stateCode("SK").stateName("Sikkim").tinNumber(11).build());
            states.add(StateCode.builder().stateCode("TN").stateName("Tamil Nadu").tinNumber(33).build());
            states.add(StateCode.builder().stateCode("TS").stateName("Telangana").tinNumber(36).build());
            states.add(StateCode.builder().stateCode("TR").stateName("Tripura").tinNumber(16).build());
            states.add(StateCode.builder().stateCode("UP").stateName("Uttar Pradesh").tinNumber(9).build());
            states.add(StateCode.builder().stateCode("UT").stateName("Uttarakhand").tinNumber(5).build());
            states.add(StateCode.builder().stateCode("WB").stateName("West Bengal").tinNumber(19).build());

            stateRepository.saveAll(states);
            System.out.println("State codes inserted successfully.");
        };
    }

    @GetMapping("/all")
    public List<StateCode> getAllStateCode(){
        return stateRepository.findAll();
    }
}
