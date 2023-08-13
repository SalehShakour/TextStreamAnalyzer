import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DataProcessor {
    private String fileName;
    private String stopWordsFileName;
    private List<String> extractedSentences;
    private List<String> stopWords;

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

        String content = new String(bytes);
        List<String> stringList = Stream.of(content.split("\\.")).toList();


        Function<String, String> blank = s -> s
                .replaceAll("\r?\n", " ")
                .replaceAll(" {2,}", " ")
                .replaceAll("^ | $", "");

        return stringList.stream()
                .map(blank)
                .toList();
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

    public int sentencesCounter() {
        return this.extractedSentences.size();
    }

}
