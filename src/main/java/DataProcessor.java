import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DataProcessor {
    private String fileName;

    public DataProcessor(String fileName) {
        this.fileName = fileName;
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

    public int sentencesCounter(){
        return extractSentences().size();
    }

}
