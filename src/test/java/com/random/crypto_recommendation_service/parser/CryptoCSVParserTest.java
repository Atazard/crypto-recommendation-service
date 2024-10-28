package com.random.crypto_recommendation_service.parser;

import com.random.crypto_recommendation_service.dtos.CryptoDTO;
import com.random.crypto_recommendation_service.model.Crypto;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CryptoCSVParserTest {

    String TEST_CRYPTOS_DIRECTORY = "src/test/resources/csvs";

    @Test
    void testParseCryptoFile() {
        CryptoCSVParser cryptoCSVParser = new CryptoCSVParser(TEST_CRYPTOS_DIRECTORY);
        Path cryptoFilePath = Paths.get(TEST_CRYPTOS_DIRECTORY + "/random_values.csv");
        List<CryptoDTO> randomCryptos = cryptoCSVParser.parseCryptoFile(cryptoFilePath);

        assertEquals(5, randomCryptos.size());
    }

    @Test
    void testLoadAllCryptos() {
        CryptoCSVParser cryptoCSVParser = new CryptoCSVParser(TEST_CRYPTOS_DIRECTORY);
        List<Crypto> cryptos = cryptoCSVParser.loadAllCryptos();
        assertEquals(5, cryptos.size());
    }
}