package aud4.zad1;

public class Book implements Comparable<Book> {
    private String title;
    private String category;
    private Float price;

    public Book(String title, String category, Float price) {
        this.title = title;
        this.category = category;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("Title: %s, Category: %s, Price: %.2f", title, category, price);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Override
    public int compareTo(Book o) {
        if (this.title.equals(o.title)) {
            return Float.compare(this.price, o.price);
        }
        return this.title.compareTo(o.title);
    }
}
