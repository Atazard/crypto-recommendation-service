package com.random.crypto_recommendation_service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
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

    //TODO: add normalaizedRangeInRange(from, to), minPriceInRange(from, to), maxPriceInRange(from, to), mormalaizedRange()
}
