import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TreeTester {
    public static void main(String[] args) {
        File words = new File("words.txt");
        LazyBST<String> dictionary = new LazyBST<>();

        // try-with-resources: Scanner will be closed automatically
        try (Scanner myReader = new Scanner(words)) {
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                dictionary.insert(data);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
        try (Scanner input = new Scanner(System.in)) {
            OUTER: while (true) {
                System.out.println("Press A to add a word to the dictionary");
                System.out.println("Press R to remove a word from the dictionary");
                System.out.println("Press S to spellcheck a document");
                System.out.println("Press Q to quit");
                String userChoice = input.nextLine().toLowerCase();
                switch (userChoice) {
                    case "q":
                        break OUTER;
                    case "a":
                        // Add
                        System.out.println("Enter the word you want to add:");
                        userChoice = input.nextLine();
                        if (userChoice.length() > 0) {
                            dictionary.insert(userChoice);
                        }
                        break;
                    case "r":
                        // Remove
                        System.out.println("Enter the word you want to remove:");
                        userChoice = input.nextLine();
                        if (userChoice.length() > 0) {
                            if (dictionary.remove(userChoice) == null) {
                                System.out.println("That word is not in the dictionary");
                            }
                        }
                        break;
                    case "s":
                        // Spellcheck
                        System.out.println("Enter the filename");
                        userChoice = input.nextLine();
                        File doc = new File(userChoice);
                        try (Scanner myReader = new Scanner(doc)) {
                            while (myReader.hasNextLine()) {
                                String data = myReader.next().replaceAll("[-+.^:,]", "");
                                if (dictionary.find(data) == null) {
                                    System.out.println(
                                            data + " is not in the dictionary. Would you like to add it? (y/n)");
                                    String addWord = input.nextLine().toLowerCase();
                                    if (addWord.equals("y") && data.length() > 0) {
                                        dictionary.insert(data);
                                    }
                                }
                            }
                        } catch (FileNotFoundException e) {
                            System.out.println("File does not exist");
                        }
                        break;
                    default:
                        System.out.println("Choose a valid option");
                        break;
                }
                System.out.println("---------------------");
            }
        }
    }
}
