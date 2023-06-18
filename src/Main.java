import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            System.out.println(text.substring(0, 100) + " -> " + maxSize);
        }
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");

        int countThreads = 30;
        ExecutorService threadPool = Executors.newFixedThreadPool(countThreads);

        List<Future> futureTasks = new ArrayList<>();
        long startTsWithTreads = System.currentTimeMillis();

        for (int t = 0; t < countThreads; t++) {
            Callable<Integer> myCallable = () -> {
                String letters = "aab";
                int length = 30_000;
                Random random = new Random();
                StringBuilder textBuilder = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    textBuilder.append(letters.charAt(random.nextInt(letters.length())));
                }
                String text = textBuilder.toString();

                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            Future<Integer> futureTask = threadPool.submit(myCallable);
            futureTasks.add(futureTask);
        }

        int max = 0;
        for (Future futureTask : futureTasks) {
            int maxOfThread = (Integer)futureTask.get();
            if(maxOfThread > max){
                max = maxOfThread;
            }
        }
        threadPool.shutdown();

        long endTsWithTreads = System.currentTimeMillis();
        System.out.println("Time: " + (endTsWithTreads - startTsWithTreads) + "ms");
        System.out.println("Максимальный интервал значений среди всех строк: " + max);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}