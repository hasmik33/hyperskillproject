package budget;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
 
 
enum Category {
    FOOD(1),
    CLOTHES(2),
    ENTERTAINMENT(3),
    OTHER(4);
     int type;
 
    Category(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
    public static Category find(int type) {
        for (Category value: values()) {
            if (value.type == type) {
                 return value;
            }
        }
        return null;
    }
}
 
class Purchase implements Comparable<Purchase>{
    Category category;
    double price;
    String description;
    Purchase(String description, double price, Category category) {
        this.description=description;
        this.price = price;
        this.category = category;
    }
    public int compareTo(Purchase compareFruit) {
 
        double compareQuantity = compareFruit.price;
        //descending order
        return compareQuantity - this.price > 0? 1 : compareQuantity - this.price < 0? -1:0;
 
    }
    public String toString() {
        return description +" $"+price+" ("+category;
    }
}
public class Main {
    final static Scanner scanner = new Scanner(System.in);
    final static String fileName = "purchases.txt";
    final static String types ="Choose the type of purchase\n1) Food\n2) Clothes\n3) Entertainment\n4) Other";
    static double balance = 0;
    static ArrayList<Purchase> pList = new ArrayList<>();
    public static void print(String line) {
        System.out.print(line);
    }
 
    public static void println(String line) {
        System.out.println(line);
    }
 
    public static void printf(String line, Object... args) {
        System.out.printf(line, args);
    }
 
    public static String read() {
        return scanner.nextLine();
    }
    public static int menu() {
        print("Choose your action:\n" +
        "1) Add income\n"+
        "2) Add purchase\n"+
        "3) Show list of purchases\n"+
        "4) Balance\n5) Save\n6) Load\n7) Analyze (Sort)\n0) Exit\n");
       return readNumber();
    }
 
    public static void purchaseMenu(boolean all) {
        println(types);
        if(all) {
            println("5) All\n6) Back");
        } else {
            println("5) Back");
        }
    }
 
    private static void balanceShow() {
        printf("Balance: $%.2f\n", balance);
    }
 
    private static double printAllPurchases() {
        double total = 0;
        double cena = 0;
      //  println("All:");
        Collections.sort(pList);
        print(" ");
        for (Purchase purch : pList) {
            printf("%s $%.2f\n", purch.description, purch.price);
            cena = purch.price;
            total += cena;
        }
 
        return total;
 
    }
 
    private static double printPurchases(int type) {
        double total = 0;
 
        String catName = firstUpperCase(Category.find(type).toString());
        println(catName + ":");
        for (Purchase purch : pList) {
            double cena = 0;
            if (purch.category.getType() == type) {
                printf("%s $%.2f\n", purch.description, purch.price);
                cena = purch.price;
                total += cena;
            }
        }
        //println("");
        return total;
    }
 
    public static void main(String[] args) {
        boolean notFinished = true;
        while (notFinished) {
            int choice = menu();
            println("");
            if (choice == 0) {
                notFinished = false;
                println("Bye!");
                break;
            }
            switch (choice) {
                case 1:
                    addIncome();
                    println("Income was added!");
                    break;
                case 2:
                    addPurchase();
                    println("Purchase was added!");
                    break;
                case 3:
                    showList();
                    break;
                case 4:
                    balanceShow();
                    break;
                case 5:
                    Save();
                    println("Purchases were saved!");
 
                    break;
                case 6:
                    Load();
                    println("Purchases were loaded!");
                    break;
                case 7:
                    Analize();
                    break;
            }
            println("");
        } // while
    }
 
    private static void Analize() {
        while (true) {
            println("How do you want to sort?\n1) Sort all purchases\n" +
                    "2) Sort by type\n3) Sort certain type\n4) Back");
            int type = readNumber();
 
            if (type == 4) {
                return;
            }
            println("");
            if (pList.isEmpty() && (type == 1 /*|| type ==3*/) ) {
                println("Purchase list is empty!\n");
                continue;
            }
 
            if (type == 1) {
                printAllPurchases();
                println("");
            } else if(type == 2) {
                SortByType();
            } else if (type == 3) {
                SortType();
                println("");
            }
 
        }
    }
 
    private static void SortType() {
 
        println(types);
        int type = readNumber();
        println("");
        int count = 0;
        for (Purchase purch : pList) {
            if (purch.category.getType() == type) {
                count++;
            }
        }
        if(count == 0) {
            println("list is empty!\n");
            return;
        }
        printPurchases(type);
    }
 
    public static String firstUpperCase(String word){
        if(word == null || word.isEmpty()) return "";//или return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
 
    private static void SortByType() {
 
        double[] subtotals = new double[]{0,0,0,0};
        HashMap<Double, Category> sortPrice = new HashMap<>();
        for (Purchase purch : pList) {
            int type = purch.category.getType() - 1;
                    //printf("%s $%.2f\n", purch.description, purch.price);
            subtotals[type] += purch.price;
        }
        for (Category cat: Category.values()) {
          //  printf("%s - $%.2f\n",firstUpperCase(cat.toString()), subtotals[cat.getType()-1]);
        sortPrice.put(subtotals[cat.getType()-1], cat);
        }
        //List<Double> catByPrice = new ArrayList<Double>(subtotals);
        Arrays.sort(subtotals);
        for(int i=3; i>=0;i--) {
            double price = subtotals[i];
            Category cat = sortPrice.get(price);
            printf("%s - $%.2f\n",firstUpperCase(cat.toString()), price);
        }
        println("");
    }
 
    private static void Load() {
        File file = new File(fileName);
        String line="";
        try (Scanner fscanner = new Scanner(file)) {
            line = fscanner.nextLine();
            int count = Integer.parseInt(line);
            for (int i = 0; i < count; i++) {
                String description = fscanner.nextLine();
                line =  fscanner.nextLine();
                double price = Double.parseDouble(line);
                line =  fscanner.nextLine();
                Purchase purch = new Purchase(description, price, Category.valueOf(line));
                pList.add(purch);
            }
            line =  fscanner.nextLine();
            balance = Double.parseDouble(line);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }
 
    private static void Save() {
        File file = new File(fileName);
        try {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println(pList.size());
            for (Purchase purch : pList) {
                printWriter.println(purch.description);
                printWriter.println(purch.price);
                printWriter.println(purch.category);
            }
            printWriter.println(balance);
            printWriter.close();
 
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }
 
    private static void addPurchase() {
        while (true) {
            purchaseMenu(false);
            int type = readNumber();
            if (type == 5) {
                return;
            }
            println("\nEnter purchase name:");
            String description = read();
            println("Enter its price");
            double purchased = readRealNumber();
            balance -= purchased;
            Purchase purch = new Purchase(description, purchased, Category.find(type));
            pList.add(purch);
            println("");
        }
    }
 
    private static void showList() {
        while (true) {
            if (pList.isEmpty()) {
                println("Purchase list is empty");
                return;
            } else {
                purchaseMenu(true);
                int type = readNumber();
                if (type == 6) {
                    return;
                }
                double total = 0;
                println("");
                if (type == 5) {
                    total = printAllPurchases();
                    println("Total: $" + total+"\n");
 
                } else {
                    total = printPurchases(type);
                    printf("Total sum: $ %.2f\n\n",  total);
                }
            }
        }
    }
 
    private static void addIncome() {
        println("Enter income:");
        int added = readNumber();
        balance += added;
    }
 
    private static int readNumber() {
        try
        {
            int i = Integer.parseInt(read());
            return i;
        }
        catch (NumberFormatException nfe)
        {
            println("NumberFormatException: " + nfe.getMessage());
            return 0;
        }
    }
    private static double readRealNumber() {
        double d = 0;
        try
        {
            d = Double.parseDouble(read());
            return d;
        }
        catch (NumberFormatException nfe)
        {
            println("NumberFormatException: " + nfe.getMessage());
            return 0;
        }
    }
}
