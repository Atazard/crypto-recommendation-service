package com.random.crypto_recommendation_service.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CryptoDTO {

    @CsvBindByName(column = "timestamp")
    private String timestamp;

    @CsvBindByName(column = "symbol")
    private String symbol;

    @CsvBindByName(column = "price")
    private String price;
}
