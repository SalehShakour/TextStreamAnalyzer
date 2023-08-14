import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataProcessor {
    private String fileName;
    private String stopWordsFileName;
    private List<String> extractedSentences;
    private List<String> stopWords;
    private String totalContent;

    public DataProcessor(String fileName, String stopWordsFileName) {
        this.fileName = fileName;
        this.stopWordsFileName = stopWordsFileName;
        this.extractedSentences = this.extractSentences();
        this.stopWords = this.extractStopWord();
    }

    public List<String> extractSentences() {
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        totalContent = new String(bytes);
        List<String> stringList = Stream.of(totalContent.split("\\.")).toList();


        Function<String, String> blank = s -> s
                .replaceAll("\r?\n", " ")
                .replaceAll("[^a-zA-Z0-9\\s]", " ")
                .replaceAll(" {2,}", " ")
                .replaceAll("^ | $", "");

        List<String> result = stringList.stream()
                .map(blank)
                .toList();
        totalContent = String.join(" ", result);
        return result;
    }

    public List<String> extractStopWord() {
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(Paths.get(stopWordsFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String content = new String(bytes);
        return Stream.of(content.split("\r?\n")).toList();
    }

    public List<String> withoutStopWord() {
        List<String> wordCounts = extractedSentences.stream()
                .map(sentence -> Arrays.stream(sentence.split(" ")).filter(word -> !stopWords.contains(word)).collect(Collectors.joining(" "))).toList();

        return wordCounts;
    }

    public void printDocDetails() {
        System.out.println("all sentences: " + extractedSentences.size());
        System.out.println("sentence words without stop word:\n" + withoutStopWord().stream().map(x -> x.split(" ").length).toList());
        System.out.println("all sentences word without stop word: " + withoutStopWord().stream().map(x -> x.split(" ").length).reduce(0, Integer::sum));
    }

    public Map<String ,List<Long>> repeatCounter() {
        //Map<String, Long> map = Arrays.stream(totalContent.split(" ")).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Set<String> setOdWords = Arrays.stream(totalContent.split(" ")).collect(Collectors.toSet());
        Map<String ,List<Long>> ref = setOdWords.stream().collect(Collectors.toMap(Function.identity(), e -> new ArrayList<>()));
        IntStream.range(0,extractedSentences.size()).mapToObj(i -> new AbstractMap.SimpleEntry<>(i, extractedSentences.get(i)))
                .forEach(entry -> {
                    int index = entry.getKey();
                    String element = entry.getValue();
                    Arrays.stream(element.split(" ")).forEach(x -> ref.get(x).add((long) index));
                });

        return ref;
    }
    public int repeatCounter(String key) {
        Map<String ,List<Long>> map = repeatCounter();
        return map.get(key).size();
    }

    public Map<String, List<String>> firstWordMap(){
        Map<String, List<String>> result = Arrays.stream(totalContent.split(" ")).collect(Collectors.toSet()).stream().collect(Collectors.toMap(Function.identity(), e -> new ArrayList<>()));
        Consumer<String> consumer = x -> result.get(x.split(" ")[0]).add(x);
        extractedSentences.forEach(consumer);
        return result;
    }

    public double charAverage(){
        double allSen = extractedSentences.size();
        double charSum = (double) extractedSentences.stream().map(x -> x.split(" ").length).reduce(0, Integer::sum);
        return charSum/allSen;
    }


}
