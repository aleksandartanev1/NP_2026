package aud2.zad1;

public class Test {
    public static void main(String[] args) {
        MessageProvider anon = new MessageProvider() {
            @Override
            public String getMessage() {
                return "Ovoa e anon";
            }
        };
        MessageProvider anonlambda = () -> "Ovoa e lambda";
        MessageProvider an = new MessageProviderImpl();

        System.out.println(an.getMessage());
        System.out.println(anon.getMessage());
        System.out.println(anonlambda.getMessage());



        Operation Multiplication = new Operation() {
            @Override
            public int apply(int a, int b) {
                return a * b;
            }
        };
        System.out.println(Multiplication.apply(2, 5));


        Operation Division = (int a, int b) -> {
            if (b != 0)
                return a / b;
            return a;
        };
        System.out.println(Division.apply(10, 2));


    }
}
