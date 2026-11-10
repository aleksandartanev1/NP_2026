import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

class InvalidProductException extends Exception {
    public InvalidProductException(String line) {
        super("Invalid product: " + line);
    }
}

class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String category) {
        super("Category " + category + " not found");
    }
}

abstract class Product implements Comparable<Product> {
    private int id;
    private String name;
    private String category;
    private double basePrice;

    public Product(int id, String name, String category, double basePrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public abstract double getFinalPrice();

    public abstract char getTypeCode();

    public static Product createProduct(String line) throws InvalidProductException {
        String[] parts = line.split(";", -1);
        if (parts.length != 6) {
            throw new InvalidProductException(line);
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        String type = parts[0];
        String name = parts[2];
        String category = parts[3];
        int id;
        double basePrice;
        try {
            id = Integer.parseInt(parts[1]);
            basePrice = Double.parseDouble(parts[4]);
        } catch (NumberFormatException e) {
            throw new InvalidProductException(line);
        }
        if (basePrice < 0) {
            throw new InvalidProductException(line);
        }

        if (type.equals("P")) {
            double weight;
            try {
                weight = Double.parseDouble(parts[5]);
            } catch (NumberFormatException e) {
                throw new InvalidProductException(line);
            }
            if (weight <= 0) {
                throw new InvalidProductException(line);
            }
            return new PhysicalProduct(id, name, category, basePrice, weight);
        } else if (type.equals("D")) {
            int sizeMb;
            try {
                sizeMb = Integer.parseInt(parts[5]);
            } catch (NumberFormatException e) {
                throw new InvalidProductException(line);
            }
            if (sizeMb <= 0) {
                throw new InvalidProductException(line);
            }
            return new DigitalProduct(id, name, category, basePrice, sizeMb);
        } else {
            throw new InvalidProductException(line);
        }
    }

    @Override
    public int compareTo(Product other) {
        int res = Double.compare(this.getFinalPrice(), other.getFinalPrice());
        if (res != 0) {
            return res;
        }
        res = this.name.compareTo(other.name);
        if (res != 0) {
            return res;
        }
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return String.format("%c %-5d %-15s %-12s %10.2f", getTypeCode(), id, name, category, getFinalPrice());
    }
}

class PhysicalProduct extends Product {
    private double weight;

    public PhysicalProduct(int id, String name, String category, double basePrice, double weight) {
        super(id, name, category, basePrice);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public double getShippingCost() {
        if (weight <= 1) {
            return 60;
        }
        return 60 + 30 * (weight - 1);
    }

    @Override
    public double getFinalPrice() {
        return getBasePrice() + getShippingCost();
    }

    @Override
    public char getTypeCode() {
        return 'P';
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %.2f kg", weight);
    }
}

class DigitalProduct extends Product {
    private int sizeMb;

    public DigitalProduct(int id, String name, String category, double basePrice, int sizeMb) {
        super(id, name, category, basePrice);
        this.sizeMb = sizeMb;
    }

    public int getSizeMb() {
        return sizeMb;
    }

    @Override
    public double getFinalPrice() {
        return getBasePrice() * 0.9;
    }

    @Override
    public char getTypeCode() {
        return 'D';
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %d MB", sizeMb);
    }
}

class Store {
    private List<Product> products;
    private int invalidLines;

    public Store() {
        products = new ArrayList<>();
        invalidLines = 0;
    }

    public int readProducts(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        int count = 0;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                products.add(Product.createProduct(line));
                count++;
            } catch (InvalidProductException e) {
                invalidLines++;
            }
        }
        return count;
    }

    public int getInvalidLines() {
        return invalidLines;
    }

    public void printSorted(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        List<Product> sorted = new ArrayList<>(products);
        Collections.sort(sorted);
        for (Product p : sorted) {
            pw.println(p);
        }
        pw.flush();
    }

    public void printByCategory(OutputStream os, String category) throws CategoryNotFoundException {
        List<Product> filtered = products.stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(Collectors.toList());
        if (filtered.isEmpty()) {
            throw new CategoryNotFoundException(category);
        }

        Comparator<Product> comparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Product::getFinalPrice, Comparator.reverseOrder())
                .thenComparing(Product::getId);
        filtered.sort(comparator);

        PrintWriter pw = new PrintWriter(os);
        for (Product p : filtered) {
            pw.println(p);
        }
        pw.flush();
    }

    public void printStatistics(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        if (products.isEmpty()) {
            pw.println("No products");
            pw.flush();
            return;
        }

        int physical = 0;
        int digital = 0;
        double sum = 0;
        for (Product p : products) {
            if (p.getTypeCode() == 'P') {
                physical++;
            } else {
                digital++;
            }
            sum += p.getFinalPrice();
        }

        pw.println("Total: " + products.size() + " (physical: " + physical + ", digital: " + digital + ")");
        pw.println("Cheapest: " + Collections.min(products));
        pw.println("Most expensive: " + Collections.max(products));
        pw.println(String.format("Average price: %.2f", sum / products.size()));
        pw.flush();
    }
}

public class StoreTest {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder productLines = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.trim().equals("COMMANDS")) {
            productLines.append(line).append("\n");
        }

        Store store = new Store();
        int read = store.readProducts(
                new ByteArrayInputStream(productLines.toString().getBytes(StandardCharsets.UTF_8)));
        System.out.println("Read products: " + read);
        System.out.println("Invalid lines: " + store.getInvalidLines());

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            System.out.println("=== " + line + " ===");
            switch (parts[0]) {
                case "SORTED":
                    store.printSorted(System.out);
                    break;
                case "CATEGORY":
                    try {
                        store.printByCategory(System.out, parts[1]);
                    } catch (CategoryNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "STATS":
                    store.printStatistics(System.out);
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }
}