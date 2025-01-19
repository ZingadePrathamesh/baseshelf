package com.baseshelf.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StateService {
    private final StateRepository stateRepository;

    public List<StateCode> getAllStateCode(){
        return stateRepository.findAll();
    }

    public boolean stateCodeExists(StateCode stateCode){
        Long id = stateCode.getId();
        if(stateRepository.existsById(id))
            return true;
        else
            throw new StateCodeNotFoundException("State Code with id: "+ id + " does not exist!");
    }

    public StateCode getById(Long id){
        Optional<StateCode> stateCodeOpt = stateRepository.findById(id);
        return stateCodeOpt.orElseThrow(()->new StateCodeNotFoundException("State Code with id: "+ id + " does not exist!"));
    }
}
