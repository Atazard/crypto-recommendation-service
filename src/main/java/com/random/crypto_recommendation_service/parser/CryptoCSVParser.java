package com.random.crypto_recommendation_service.parser;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.random.crypto_recommendation_service.dtos.CryptoDTO;
import com.random.crypto_recommendation_service.model.Crypto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CryptoCSVParser {

    private final String csvFolderPath;

    public List<Crypto> loadAllCryptos() {
        List<Crypto> cryptos = new ArrayList<>();

        Path path = Paths.get(csvFolderPath);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path file : directoryStream) {
                List<CryptoDTO> cryptoDTOs = parseCryptoFile(file);
                for (CryptoDTO cryptoDTO : cryptoDTOs) {
                    Crypto crypto = new Crypto(cryptoDTO.getSymbol());

                    if (cryptos.contains(crypto)) {
                        crypto = cryptos.get(cryptos.indexOf(crypto));
                        crypto.addEntryInHistory(cryptoDTO.getTimestamp(), cryptoDTO.getPrice());
                    } else {
                        crypto.addEntryInHistory(cryptoDTO.getTimestamp(), cryptoDTO.getPrice());
                        cryptos.add(crypto);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed loading CSV files from folder: {}", path, e);
        }

        return cryptos;
    }

    List<CryptoDTO> parseCryptoFile(Path cryptoFilePath) {

        List<CryptoDTO> cryptoDTOs = new ArrayList<>();

        try {
            CsvToBean<CryptoDTO> csvToBean = new CsvToBeanBuilder<CryptoDTO>(Files.newBufferedReader(cryptoFilePath))
                    .withType(CryptoDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            cryptoDTOs = csvToBean.parse();
        } catch (IOException e) {
            log.error("Failed to parse CSV file: {}", cryptoFilePath, e);
        }

        return cryptoDTOs;
    }
}
