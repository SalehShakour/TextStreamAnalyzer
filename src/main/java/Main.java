public class Main {

    public static void main(String[] args) {
        DataProcessor dataProcessor = new DataProcessor("sample.txt", "StopWords.txt");
        System.out.println(dataProcessor.charAverage());
        System.out.println(dataProcessor.evenOdd());

    }
}
