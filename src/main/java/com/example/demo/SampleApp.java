import java.util.*;
import java.io.*;

public class SampleApp {
    public static void main(String[] args) {
        String password = "hardcodedSecret"; // Security issue: hardcoded password
        System.out.println("Hello, World!");

        // Bug: possible NullPointerException
        String name = null;
        System.out.println(name.toLowerCase());

        // Code smell: duplicated logic
        for (int i = 0; i < 5; i++) {
            System.out.println("Count: " + i);
        }
        for (int i = 0; i < 5; i++) {
            System.out.println("Count: " + i);
        }
    }
}
