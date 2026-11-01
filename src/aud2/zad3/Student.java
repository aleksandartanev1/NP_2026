package aud2.zad3;

public class Student {
    private final String index;
    private String name;
    private int grade;
    private int attendance;

    public Student(String index, String name, int grade, int attendance) {
        this.index = index;
        this.name = name;
        this.grade = grade;
        this.attendance = attendance;
    }

    @Override
    public String toString() {

    }
}
