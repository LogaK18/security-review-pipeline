public class VulnerableApp {
    public static void main(String[] args) {
        // Security Hotspot: hardcoded password
        String apiKey = "mySuperSecretKey123"; 
        System.out.println("Using API key: " + apiKey);
    }
}
