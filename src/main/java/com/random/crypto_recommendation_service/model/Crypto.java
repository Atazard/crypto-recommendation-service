package com.random.crypto_recommendation_service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class Crypto {

    private final String symbol;
    private final TreeMap<Timestamp, BigDecimal> history = new TreeMap<>();
    // http://www.javapractices.com/topic/TopicAction.do?Id=13
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public void addEntryInHistory(Timestamp timestamp, BigDecimal price) {
        history.put(timestamp, price);
    }

    public void addEntryInHistory(String timestamp, String price) {
//        history.put(new Timestamp(Long.parseLong(timestamp)), new BigDecimal(price));
        addEntryInHistory(new Timestamp(Long.parseLong(timestamp)), new BigDecimal(price));
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> oldestEntry() {
//        return history.entrySet().stream().findFirst();
        return history.isEmpty() ? Optional.empty() : oldestEntryInRange(history.firstKey(), history.lastKey());
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> latestEntry() {
//        return history.isEmpty() ? Optional.empty() : Optional.of(history.lastEntry());
        return history.isEmpty() ? Optional.empty() : latestEntryInRange(history.firstKey(), history.lastKey());
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> oldestEntryInRange(Timestamp from, Timestamp to) {
        return historyInRange(from, to).findFirst();
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> oldestEntryInRange(String from, String to) {
//        return historyInRange(new Timestamp(Long.parseLong(from)), new Timestamp(Long.parseLong(to))).findFirst();
        return oldestEntryInRange(new Timestamp(Long.parseLong(from)), new Timestamp(Long.parseLong(to)));
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> latestEntryInRange(Timestamp from, Timestamp to) {
        return historyInRange(from, to).max(Map.Entry.comparingByKey());
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> latestEntryInRange(String from, String to) {
//        return historyInRange(new Timestamp(Long.parseLong(from)), new Timestamp(Long.parseLong(to))).max(Map.Entry.comparingByKey());
        return latestEntryInRange(new Timestamp(Long.parseLong(from)), new Timestamp(Long.parseLong(to)));
    }

    private Stream<Map.Entry<Timestamp, BigDecimal>> historyInRange(Timestamp from, Timestamp to) {
        if (from.after(to)) {
            //how should we handle this? probably some exception and return a relative message as response
            return Stream.empty();
        }

        // before() and after() are not inclusive, so we need a little trick
        Timestamp fromInclusive = new Timestamp(from.getTime()-1);
        Timestamp toInclusive = new Timestamp(to.getTime()+1);

        return history.entrySet().stream().filter(e -> e.getKey().after(fromInclusive) && e.getKey().before(toInclusive));
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> minPriceEntry() {
        return history.isEmpty() ? Optional.empty() : minPriceEntryInRange(history.firstKey(), history.lastKey());
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> minPriceEntryInRange(Timestamp from, Timestamp to) {
        return historyInRange(from, to).min(Map.Entry.comparingByValue());
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> minPriceEntryInRange(String from, String to) {
        return minPriceEntryInRange(new Timestamp(Long.parseLong(from)), new Timestamp(Long.parseLong(to)));
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> maxPriceEntry() {
        return history.isEmpty() ? Optional.empty() : maxPriceEntryInRange(history.firstKey(), history.lastKey());
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> maxPriceEntryInRange(Timestamp from, Timestamp to) {
        return historyInRange(from, to).max(Map.Entry.comparingByValue());
    }

    public Optional<Map.Entry<Timestamp, BigDecimal>> maxPriceEntryInRange(String from, String to) {
        return maxPriceEntryInRange(new Timestamp(Long.parseLong(from)), new Timestamp(Long.parseLong(to)));
    }

    public Optional<BigDecimal> normalizedRange() {
        return history.isEmpty() ? Optional.empty() : normalizedRangeInRange(history.firstKey(), history.lastKey());
    }

    public Optional<BigDecimal> normalizedRangeInRange(Timestamp from, Timestamp to) {
        Optional<Map.Entry<Timestamp, BigDecimal>> minPriceEntryInRange = minPriceEntryInRange(from, to);
        Optional<Map.Entry<Timestamp, BigDecimal>> maxPriceEntryInRange = maxPriceEntryInRange(from, to);

        if (minPriceEntryInRange.isEmpty() || maxPriceEntryInRange.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal minPriceValueInRange = minPriceEntryInRange.get().getValue();
        BigDecimal maxPriceValueInRange = maxPriceEntryInRange.get().getValue();

        if (minPriceValueInRange.compareTo(BigDecimal.ZERO) == 0 || minPriceValueInRange.compareTo(maxPriceValueInRange) > 0) {
            return Optional.empty();
        }

        return Optional.of(maxPriceValueInRange.subtract(minPriceValueInRange).divide(minPriceValueInRange, ROUNDING_MODE));
    }

    public Optional<BigDecimal> normalizedRangeInRange(String from, String to) {
        return normalizedRangeInRange(new Timestamp(Long.parseLong(from)), new Timestamp(Long.parseLong(to)));
    }
}
