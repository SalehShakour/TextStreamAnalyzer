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
    private final String fileName;
    private final String stopWordsFileName;
    private final List<String> extractedSentences;
    private final List<String> stopWords;
    private String totalContent;

    /**
     * Constructs a new DataProcessor object with the given file names for the main data file and stop words file.
     * It extracts sentences from the main data file and stop words from the stop words file.
     *
     * @param fileName The name of the main data file
     * @param stopWordsFileName The name of the stop words file
     */
    public DataProcessor(String fileName, String stopWordsFileName) {
        this.fileName = fileName;
        this.stopWordsFileName = stopWordsFileName;
        this.extractedSentences = this.extractSentences();
        this.stopWords = this.extractStopWord();
    }

    /**
     * Extracts sentences from a file and returns them as a list of strings.
     *
     * @return The list of extracted sentences
     * @throws RuntimeException if there is an error reading the file
     */
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

    /**
     * Extracts stop words from a file and returns them as a list of strings.
     *
     * @return A list of stop words
     * @throws RuntimeException if there is an error reading the file
     */
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

    /**
     * Returns a list of sentences with stop words removed.
     *
     * @return A list of sentences without stop words.
     */
    public List<String> withoutStopWord() {
        List<String> wordCounts = extractedSentences.stream()
                .map(sentence -> Arrays.stream(sentence.split(" ")).filter(word -> !stopWords.contains(word)).collect(Collectors.joining(" "))).toList();

        return wordCounts;
    }

    /**
     * Prints the details of the document.
     * This includes the total number of sentences, the number of words in each sentence without stop words,
     * and the total number of words in all sentences without stop words.
     */
    public void printDocDetails() {
        System.out.println("all sentences: " + extractedSentences.size());
        System.out.println("sentence words without stop word:\n" + withoutStopWord().stream().map(x -> x.split(" ").length).toList());
        System.out.println("all sentences word without stop word: " + withoutStopWord().stream().map(x -> x.split(" ").length).reduce(0, Integer::sum));
    }

    /**
     * Counts the number of times each word appears in the total content and returns a map
     * where the keys are the unique words and the values are lists of indices indicating
     * the positions of the word in the extracted sentences.
     *
     * @return A map where the keys are words and the values are lists of indices.
     */
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
    /**
     * Returns the number of occurrences of a given key in the map.
     *
     * @param key The key to count occurrences for
     * @return The number of occurrences of the key in the map
     */
    public int repeatCounter(String key) {
        Map<String ,List<Long>> map = repeatCounter();
        return map.get(key).size();
    }

    /**
     * Creates a map where the keys are the first words of the sentences in the extractedSentences list,
     * and the values are lists of sentences that start with the corresponding first word.
     *
     * @return A map with first words as keys and lists of sentences as values.
     */
    public Map<String, List<String>> firstWordMap(){
        Map<String, List<String>> result = Arrays.stream(totalContent.split(" ")).collect(Collectors.toSet()).stream().collect(Collectors.toMap(Function.identity(), e -> new ArrayList<>()));
        Consumer<String> consumer = x -> result.get(x.split(" ")[0]).add(x);
        extractedSentences.forEach(consumer);
        return result;
    }

    /**
     * Calculates the average length of characters in the extracted sentences.
     *
     * @return The average length of characters in the extracted sentences, or 0 if there are no sentences.
     */
    public double charAverage(){
        return extractedSentences.stream().mapToInt(String::length).average().orElse(0);
    }

    /**
     * Calculates the sum of the lengths of sentences in the extractedSentences list,
     * where the length is negated if the sentence has an odd number of words.
     *
     * @return The sum of the lengths of sentences, with odd-length sentences negated.
     */
    public int evenOdd(){
        return extractedSentences.stream().map(x -> x.split(" ").length).map(x -> x % 2 == 0 ? x : -1 * x).reduce(0, Integer::sum);
    }


}
