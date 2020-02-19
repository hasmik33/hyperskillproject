package flashcards;


import java.io.*;
import java.util.*;

/*implementing singleton design pattern*/
class Journal {
    private static ArrayList<String> log = new ArrayList<>();
    public static ArrayList<String> getLog() {
        return log;
    }
    public static void printLog() {
    }
}

class loggingOut extends PrintStream {
    public loggingOut()
    {
        super(System.out);
    }

    public void println(String message) {
        super.println(message);
        Journal.getLog().add(message + "\n");
    }

    public PrintStream printf(String format,Object... args) {
        String message = String.format(format, args);
        super.print(message);
        Journal.getLog().add(message);
        return this;
    }
}

class Flashcard {
    String definition;
    int errors;
    public Flashcard(String definition) {
        this.definition = definition;
        errors = 0;
    }
    public void addError() {
        errors++;
    }

    public String getDefinition() {
        return definition;
    }

    public int getError() {
        return errors;
    }

    public void setError(int errors) {
        this.errors = errors;
    }

    public void resetErrors(){
        errors = 0;
    }
}
public class flashcards7 {
    private static LinkedHashMap <String, Flashcard> flashcards;
    private static Scanner scanner;
    private static loggingOut myOut = new loggingOut();

    public static String keyByValue(String value) {
        for (var entry : flashcards.entrySet()) {
            if (entry.getValue().getDefinition().equals(value)) {
                return entry.getKey();
            } // if
        } //for
        return null;
    }

    public static String getUserInput() {
        String message = scanner.nextLine();
        Journal.getLog().add(message + "\n");
        return message;
    }

    public static void add() {
        String term;
        String definition;

        myOut.println("The card:");

        if (flashcards.containsKey(term = getUserInput())) {
            myOut.printf("The card \"%s\" already exists.\n", term);
            return;
        }
        myOut.println("The definition of the card:");

        definition = getUserInput();

        if (keyByValue(definition) != null) {
            myOut.printf("The definition \"%s\" already exists.\n", definition);
            return;
        }
        Flashcard fc = new Flashcard(definition);
        flashcards.put(term, fc);
        myOut.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
    }

    public static void remove() {
        String term;

        myOut.println("The card:");

        if (!flashcards.containsKey(term = getUserInput())) {
            myOut.printf("Can't remove \"%s\": there is no such card\n", term);
        } else {
            flashcards.remove(term);
            myOut.printf("The card \"%s\" has been removed.\n", term);
        }
    }

    public static void ask() {
        myOut.println("How many times to ask?");
        int n = Integer.parseInt(getUserInput());
        Random generator = new Random();
        Object[]  entries = flashcards.keySet().toArray();
        if (entries.length == 0) {
            return;
        }
        for (int i = 0; i < n; i++) {
            String randomKey = (String) entries[generator.nextInt(entries.length)];

            myOut.printf("Print the definition of \"%s\":\n", randomKey);
            String answer = getUserInput();
            Flashcard fc = flashcards.get(randomKey);
            if (fc.getDefinition().equals(answer)) {
                myOut.println("Correct answer.");
            } else {
                String rightTerm = keyByValue(answer);
                fc.addError();
                if (rightTerm != null) {
                    myOut.printf("Wrong answer. (The correct one is \"%s\", you've just written the definition of \"%s\" card.)\n\n",fc.getDefinition(), rightTerm);
                } else {
                    myOut.printf("Wrong answer. The correct one is \"%s\".\n",fc.getDefinition());
                } // else
            } // else
        } // for
    } //ask

    public static void importFromFile(String pathToFile) throws UnsupportedEncodingException {
        String term = "";
        String definition = "";
        String err = "";

        java.io.File file = new File(pathToFile);
        int count = 0;
        //   PrintStream ps = new PrintStream(System.out, true, "cp866");

        try (Scanner fscanner = new Scanner(file)) {

            while (fscanner.hasNext()) {
                fscanner.useDelimiter("[^A-Za-z]+");
                term = fscanner.nextLine();
                definition = fscanner.next();
                fscanner.useDelimiter("[A-Za-z]+");
                err = fscanner.next();

                char c = err.charAt(0);
                int error = c;
                Flashcard fc = new Flashcard(definition);
                fc.setError(error);
                flashcards.put(term, fc);
                count++;
            } //while
            myOut.printf("%d cards have been loaded.\n\n", count);
        } //try
        catch (FileNotFoundException e) {
            myOut.println("File not found.");
        }

    }


    public static void exportFile(String pathToFile) {

        java.io.File file = new File(pathToFile);
        int count = 0;
        try (FileWriter writer = new FileWriter(file)) {
            for (var entry : flashcards.entrySet()) {
                writer.write(entry.getKey() + "\n");
                Flashcard fc = entry.getValue();
                writer.write(fc.getDefinition());
                writer.write(fc.getError());
                count++;
            }
            writer.close();
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
        myOut.printf("%d cards have been saved\n",count);
    }

    public static void exportLog() {
        String pathToFile = "";

        myOut.println("File name");
        pathToFile = getUserInput();
        java.io.File file = new File(pathToFile);
        try (FileWriter writer = new FileWriter(file)) {
            for (String entry : Journal.getLog()) {
                writer.write(entry);
            }
            writer.close();
        } catch (IOException e) {
            myOut.printf("An exception occurs %s", e.getMessage());
        }
        myOut.println("The log has been saved.");
    }

    public static void resetStats() {
        for (var entry : flashcards.entrySet()) {

            Flashcard fc = entry.getValue();
            fc.resetErrors();
        }
        myOut.println("Card statistics has been reset.\n");
    }

    public static void hardestCard() {
        int max = 0;
        ArrayList<Flashcard> hardest = new ArrayList<>();
        for (var entry : flashcards.entrySet()) {

            Flashcard fc = entry.getValue();

            int errors = fc.getError();
            if (errors > max) {
                max = errors;
                hardest.clear();
                hardest.add(fc);
            } else if (errors == max && max > 0) {
                hardest.add(fc);
            }
        }
        if (max == 0) {
            myOut.println("There are no cards with errors.\n\n");
        } else if (hardest.size() == 1) {
            myOut.printf("The hardest card is \"%s\". You have %d errors answering it.\n\n", keyByValue(hardest.get(0).getDefinition()), max);
        }else {
            StringBuilder sb = new StringBuilder("\"" + keyByValue(hardest.get(0).getDefinition()) +"\"");
            for(int i = 1; i < hardest.size(); i++) {
                sb.append(", \"" + keyByValue(hardest.get(i).getDefinition()) + "\"");
            }
            myOut.printf("The hardest cards are %s. You have %d errors answering it.\n\n", sb.toString(), max);
        }

    }

    public static void main(String[] args) throws UnsupportedEncodingException  {
        flashcards = new LinkedHashMap <>();
        scanner = new Scanner(System.in, "cp866");

// 7 phase
/* arguments command-line 
To read an initial cards set from an external file, you should pass the argument -import and follow it with the file name. 
If the argument is present, the first line of your program output should be 10 cards have been loaded. 
(hereinafter, replace 10 with the number of cards). If such argument is not set, the set of cards should be initially empty.

*/
		for (int i=0; i < args.length; i++) {
			if ("-import".equals(args[i])) {
				importFromFile(args[i + 1]);
			}
		}
        while (true) {
            myOut.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String action = getUserInput();
            switch (action) {
                case "add":
                    add();
                    break;
                case "remove":
                    remove();
                    break;
                case "ask":
                    ask();
                    break;
                case "import":
					myOut.println("File name");
                    importFromFile(getUserInput());
                    break;
                case "export":
					myOut.println("File name");
                    exportFile(getUserInput());
                    break;
                case "log":
                    exportLog();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                default:
                    break;
            }
            if ("exit".equals(action)) {
				for (int i=0; i < args.length; i++) {

					if ("-export".equals(args[i])) {
				exportFile(args[i + 1]);
			}
		}
                myOut.println("Bye bye!");
                break;
            }
        }
    }
}

