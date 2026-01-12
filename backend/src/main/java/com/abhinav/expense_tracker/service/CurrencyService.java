package com.abhinav.expense_tracker.service;


import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
public class CurrencyService {
    private final RestTemplate rest=new RestTemplate();
    @Value("${currency.api.key}")
    String accessKey;

    public double convert(String from,String to,double amount){
        String url = String.format("https://v6.exchangerate-api.com/v6/%s/pair/%s/%s/%s", 
                                   accessKey, from, to, amount);
        Map res = rest.getForObject(url, Map.class);
        if(res!=null && "success".equals(res.get("result"))){
            Object val = res.get("conversion_result");
            if(val instanceof Number){
                return ((Number) val).doubleValue();
            }
        }
        return amount;
    }
}
