package com.random.crypto_recommendation_service.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CryptoTest {

    Crypto crypto;

    @BeforeEach
    void setUp() {
        crypto = new Crypto("BTC");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAddEntryInHistory() {
        assertEquals(0, crypto.getHistory().size());

        crypto.addEntryInHistory("1", "1.1");
        assertEquals(1, crypto.getHistory().size());
        assertEquals(new Timestamp(1), crypto.getHistory().firstKey());
        assertEquals(new BigDecimal("1.1"), crypto.getHistory().firstEntry().getValue());

        crypto.addEntryInHistory(new Timestamp(2), new BigDecimal("2.2"));
        assertEquals(2, crypto.getHistory().size());
        assertEquals(new Timestamp(2), crypto.getHistory().lastKey());
        assertEquals(new BigDecimal("2.2"), crypto.getHistory().lastEntry().getValue());
    }

    @Test
    void testOldestEntry() {
        Optional<Map.Entry<Timestamp, BigDecimal>> oldestEntry = crypto.oldestEntry();
        assertFalse(oldestEntry.isPresent());

        crypto.addEntryInHistory("1", "1.1");

        Timestamp expectedTimestamp = new Timestamp(1);
        BigDecimal expectedValue = new BigDecimal("1.1");

        oldestEntry = crypto.oldestEntry();
        assertTrue(oldestEntry.isPresent());
        assertEquals(expectedTimestamp, oldestEntry.get().getKey());
        assertEquals(expectedValue, oldestEntry.get().getValue());

        crypto.addEntryInHistory("2", "2.2");
        crypto.addEntryInHistory("3", "3.3");

        oldestEntry = crypto.oldestEntry();
        assertTrue(oldestEntry.isPresent());
        assertEquals(expectedTimestamp, oldestEntry.get().getKey());
        assertEquals(expectedValue, oldestEntry.get().getValue());
    }

    @Test
    void testLatestEntry() {
        Optional<Map.Entry<Timestamp, BigDecimal>> latestEntry = crypto.latestEntry();
        assertFalse(latestEntry.isPresent());

        crypto.addEntryInHistory("1", "1.1");

        Timestamp expectedTimestamp = new Timestamp(1);
        BigDecimal expectedValue = new BigDecimal("1.1");

        latestEntry = crypto.latestEntry();
        assertTrue(latestEntry.isPresent());
        assertEquals(expectedTimestamp, latestEntry.get().getKey());
        assertEquals(expectedValue, latestEntry.get().getValue());

        expectedTimestamp = new Timestamp(3);
        expectedValue = new BigDecimal("3.3");

        crypto.addEntryInHistory("2", "2.2");
        crypto.addEntryInHistory("3", "3.3");

        latestEntry = crypto.latestEntry();
        assertTrue(latestEntry.isPresent());
        assertEquals(expectedTimestamp, latestEntry.get().getKey());
        assertEquals(expectedValue, latestEntry.get().getValue());
    }

    @Test
    void testOldestEntryInRange() {
        Optional<Map.Entry<Timestamp, BigDecimal>> oldestEntryInRange = crypto.oldestEntryInRange("1", "2");
        assertFalse(oldestEntryInRange.isPresent());

        crypto.addEntryInHistory("1", "1.1");

        Timestamp expectedTimestamp = new Timestamp(1);
        BigDecimal expectedValue = new BigDecimal("1.1");

        oldestEntryInRange = crypto.oldestEntryInRange(expectedTimestamp, expectedTimestamp);
        assertTrue(oldestEntryInRange.isPresent());
        assertEquals(expectedTimestamp, oldestEntryInRange.get().getKey());
        assertEquals(expectedValue, oldestEntryInRange.get().getValue());

        oldestEntryInRange = crypto.oldestEntryInRange("1", "2");
        assertTrue(oldestEntryInRange.isPresent());
        assertEquals(expectedTimestamp, oldestEntryInRange.get().getKey());
        assertEquals(expectedValue, oldestEntryInRange.get().getValue());

        oldestEntryInRange = crypto.oldestEntryInRange("2", "1");
        assertFalse(oldestEntryInRange.isPresent());

        crypto.addEntryInHistory("2", "2.2");
        crypto.addEntryInHistory("3", "3.3");

        expectedTimestamp = new Timestamp(2);
        expectedValue = new BigDecimal("2.2");

        oldestEntryInRange = crypto.oldestEntryInRange("2", "3");
        assertTrue(oldestEntryInRange.isPresent());
        assertEquals(expectedTimestamp, oldestEntryInRange.get().getKey());
        assertEquals(expectedValue, oldestEntryInRange.get().getValue());
    }

    @Test
    void TestLatestEntryInRange() {
        Optional<Map.Entry<Timestamp, BigDecimal>> latestEntryInRange = crypto.latestEntryInRange("1", "2");
        assertFalse(latestEntryInRange.isPresent());

        crypto.addEntryInHistory("1", "1.1");

        Timestamp expectedTimestamp = new Timestamp(1);
        BigDecimal expectedValue = new BigDecimal("1.1");

        latestEntryInRange = crypto.latestEntryInRange(expectedTimestamp, expectedTimestamp);
        assertTrue(latestEntryInRange.isPresent());
        assertEquals(expectedTimestamp, latestEntryInRange.get().getKey());
        assertEquals(expectedValue, latestEntryInRange.get().getValue());

        latestEntryInRange = crypto.oldestEntryInRange("1", "2");
        assertTrue(latestEntryInRange.isPresent());
        assertEquals(expectedTimestamp, latestEntryInRange.get().getKey());
        assertEquals(expectedValue, latestEntryInRange.get().getValue());

        latestEntryInRange = crypto.oldestEntryInRange("2", "1");
        assertFalse(latestEntryInRange.isPresent());

        crypto.addEntryInHistory("2", "2.2");
        crypto.addEntryInHistory("3", "3.3");

        expectedTimestamp = new Timestamp(3);
        expectedValue = new BigDecimal("3.3");

        latestEntryInRange = crypto.latestEntryInRange("2", "3");
        assertTrue(latestEntryInRange.isPresent());
        assertEquals(expectedTimestamp, latestEntryInRange.get().getKey());
        assertEquals(expectedValue, latestEntryInRange.get().getValue());
    }
}