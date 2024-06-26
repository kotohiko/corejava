package collecting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * @author Cay Horstmann
 * @version 1.01 2021-09-06
 */
public class DownstreamCollectors {

    private static final String CITIES_TXT = "cities.txt";

    public static Stream<City> readCities(String filename) throws IOException {
        return Files.lines(Path.of("v2ch01/src/main/resources/" + filename))
                .map(l -> l.split(", "))
                .map(a -> new City(a[0], a[1], Integer.parseInt(a[2])));
    }

    public static void main(String[] args) throws IOException {
        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Set<Locale>> countryToLocaleSet = locales.collect(groupingBy(
                Locale::getCountry, toSet()));
        System.out.println("countryToLocaleSet: " + countryToLocaleSet);

        locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Long> countryToLocaleCounts = locales.collect(groupingBy(
                Locale::getCountry, counting()));
        System.out.println("countryToLocaleCounts: " + countryToLocaleCounts);

        Stream<City> cities = readCities(CITIES_TXT);
        Map<String, Integer> stateToCityPopulation = cities.collect(groupingBy(
                City::state, summingInt(City::population)));
        System.out.println("stateToCityPopulation: " + stateToCityPopulation);

        cities = readCities(CITIES_TXT);
        Map<String, Optional<String>> stateToLongestCityName = cities
                .collect(groupingBy(City::state,
                        mapping(City::name, maxBy(Comparator.comparing(String::length)))));
        System.out.println("stateToLongestCityName: " + stateToLongestCityName);

        locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Set<String>> countryToLanguages = locales.collect(groupingBy(
                Locale::getDisplayCountry, mapping(Locale::getDisplayLanguage, toSet())));
        System.out.println("countryToLanguages: " + countryToLanguages);

        cities = readCities(CITIES_TXT);
        Map<String, IntSummaryStatistics> stateToCityPopulationSummary = cities
                .collect(groupingBy(City::state, summarizingInt(City::population)));
        System.out.println(stateToCityPopulationSummary.get("NY"));

        cities = readCities(CITIES_TXT);
        Map<String, String> stateToCityNames;

        cities = readCities(CITIES_TXT);
        stateToCityNames = cities.collect(groupingBy(City::state,
                mapping(City::name, joining(", "))));
        System.out.println("stateToCityNames: " + stateToCityNames);

        cities = readCities(CITIES_TXT);
        record Pair<S, T>(S first, T second) {
        }
        Pair<List<String>, Double> result = cities.filter(c -> c.state().equals("NV"))
                .collect(teeing(
                        mapping(City::name, toList()),
                        averagingDouble(City::population),
                        Pair::new));
        System.out.println(result);
    }

    public record City(String name, String state, int population) {
    }
}
