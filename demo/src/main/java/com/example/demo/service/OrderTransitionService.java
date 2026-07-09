package com.example.demo.service;

import com.example.demo.enums.Status;
import com.example.demo.exception.InvalidTransitionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OrderTransitionService {
    private static final Map<Status,List<Status>> allowed_Transitions= Map.of(
            Status.ORDERED, List.of(Status.CANCELLED,Status.IN_PRODUCTION),
            Status.IN_PRODUCTION,List.of(Status.CANCELLED,Status.READY),
            Status.READY,List.of(Status.DELIVERED),
            Status.CANCELLED,List.of(),
            Status.DELIVERED,List.of()
    );

    public void validate(Status current,Status next){
        List<Status> allowed=allowed_Transitions.getOrDefault(current,List.of());
        if(!allowed.contains(next)){
            throw new InvalidTransitionException("InValid Transaction: "+current+" -> "+next+". Allowed from"+current+": "+allowed);
        }
        log.info("State Transition Validated :{} -> {}",current,next);
    }

}
