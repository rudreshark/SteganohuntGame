import java.util.*;
public class SteganohuntCustomRules {
    enum MessageType { CLEAN, OBFUSCATED, HIDDEN }
    static class Message {
        String content;
        MessageType type;
        String explanation;
        Message(String content, MessageType type, String explanation) {
            this.content = content;
            this.type = type;
            this.explanation = explanation;
        }
    }
    static Set<String> suspiciousKeywords = new HashSet<>();
    static Set<Character> suspiciousChars = new HashSet<>();
    static boolean checkLeetspeak = false;
    static boolean checkSpacing = false;
    static boolean checkCapitalPattern = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(" Build Your Detection Rules:");
        System.out.print("Enter keywords to flag (comma-separated): ");
        String[] keywords = scanner.nextLine().split(",");
        for (String word : keywords)
            suspiciousKeywords.add(word.trim().toLowerCase());

        System.out.print("Enter suspicious characters (e.g., @ $ %): ");
        String chars = scanner.nextLine();
        for (char c : chars.toCharArray()) {
            if (!Character.isWhitespace(c))
                suspiciousChars.add(c);
        }

        System.out.print("Enable Leetspeak detection? (yes/no): ");
        checkLeetspeak = scanner.nextLine().equalsIgnoreCase("yes");

        System.out.print("Enable extra spacing detection? (yes/no): ");
        checkSpacing = scanner.nextLine().equalsIgnoreCase("yes");

        System.out.print("Enable inconsistent capitalization detection? (yes/no): ");
        checkCapitalPattern = scanner.nextLine().equalsIgnoreCase("yes");

        List<Message> messages = Arrays.asList(
            new Message("Meet me at 5 PM. Bring the reports.", MessageType.CLEAN, "Normal meeting message."),
            new Message("Don't forget to b.r.i.n.g it.", MessageType.OBFUSCATED, "Split letters for emphasis."),
            new Message("D0n't f0rg3t the dr1v3!", MessageType.HIDDEN, "Leetspeak - numbers replacing letters."),
            new Message("Heres the location: park.", MessageType.OBFUSCATED, "Suspicious spacing."),
            new Message("cHecK tHe pAckeT sTruCture", MessageType.HIDDEN, "Capitalization pattern may spell something."),
            new Message("Just a reminder, see you soon!", MessageType.CLEAN, "Safe message."),
            new Message("R3m3mb3r, the f1l3 n4m3 is 'x2y9a'", MessageType.HIDDEN, "Leetspeak to hide file reference."),
            new Message("a a a a", MessageType.OBFUSCATED, "Weird repeated pattern.")
        );

        Collections.shuffle(messages);
        int score = 0;
        int round = 1;

        System.out.println("\n Game Start Use your own rules to analyze each message!\n");

        for (Message msg : messages) {
            System.out.println(" Round " + round + ":");
            System.out.println("Message: " + msg.content);

            List<String> systemFlags = analyzeMessage(msg.content);
            if (!systemFlags.isEmpty()) {
                System.out.println(" Detected Clues: " + String.join(", ", systemFlags));
            }

            System.out.print("Your classification (clean / obfuscated / hidden): ");
            String input = scanner.nextLine().trim().toLowerCase();
            MessageType userType;

            switch (input) {
                case "clean": userType = MessageType.CLEAN; break;
                case "obfuscated": userType = MessageType.OBFUSCATED; break;
                case "hidden": userType = MessageType.HIDDEN; break;
                default:
                    System.out.println(" Invalid input. Skipping.");
                    continue;
            }

            if (userType == msg.type) {
                System.out.println(" Correct! " + msg.explanation);
                score += 2;
            } else if ((userType == MessageType.OBFUSCATED && msg.type == MessageType.HIDDEN) ||
                       (userType == MessageType.HIDDEN && msg.type == MessageType.OBFUSCATED)) {
                System.out.println(" Close! It was: " + msg.type + " " + msg.explanation);
                score += 1;
            } else {
                System.out.println(" Wrong! It was: " + msg.type + " " + msg.explanation);
                score -= 1;
            }

            System.out.println("Current Score: " + score);
            System.out.println("--------------------------------------------------\n");
            round++;
        }

        System.out.println(" Game Over Final Score: " + score + "/" + (messages.size() * 2));
        scanner.close();
    }

    static List<String> analyzeMessage(String msg) {
        List<String> flags = new ArrayList<>();

        for (String word : suspiciousKeywords) {
            if (msg.toLowerCase().contains(word)) {
                flags.add("keyword: '" + word + "'");
            }
        }

        for (char c : msg.toCharArray()) {
            if (suspiciousChars.contains(c)) {
                flags.add("char: '" + c + "'");
            }
        }

        if (checkLeetspeak && msg.matches(".*[4301].*")) {
            flags.add("leetspeak pattern");
        }

        if (checkSpacing && msg.contains(" ")) {
            flags.add("excessive spacing");
        }

        if (checkCapitalPattern && !msg.equals(msg.toLowerCase()) && !msg.equals(msg.toUpperCase())) {
            if (msg.matches(".*[a-z][A-Z]|[A-Z][a-z].*")) {
                flags.add("odd capitalization");
            }
        }

        return flags;
    }
}
