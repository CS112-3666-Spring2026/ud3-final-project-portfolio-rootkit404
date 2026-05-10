package war.models;

/**
 * PlayingCardTester.java - Represents a class to test all attributes of PlayingCard.java
 */
public class PlayingCardTester {

    public static void main(String[] args) {
        System.out.println("--- PlayingCard Tests ---\n");

        // ----------------------------------------------------
        // Test 1: Full Constructor & toString() (Face Down)
        // ----------------------------------------------------
        System.out.println("1. Testing Full Constructor & toString() [Face Down]:");
        PlayingCard card1 = new PlayingCard(Suit.HEARTS, Rank.QUEEN, false);
        System.out.println("Expected: [Hidden Card]");
        System.out.println("Actual:   " + card1.toString() + "\n");

        // ----------------------------------------------------
        // Test 2: Full Constructor & toString() (Face Up)
        // ----------------------------------------------------
        System.out.println("2. Testing Full Constructor & toString() [Face Up]:");
        PlayingCard card2 = new PlayingCard(Suit.SPADES, Rank.ACE, true);
        System.out.println("Expected: ACE of SPADES");
        System.out.println("Actual:   " + card2.toString() + "\n");

        // ----------------------------------------------------
        // Test 3: Default Constructor
        // ----------------------------------------------------
        System.out.println("3. Testing Default Constructor:");
        PlayingCard defaultCard = new PlayingCard();
        System.out.println("Expected: [Hidden Card] (ACE of SPADES face down)");
        System.out.println("Actual:   " + defaultCard.toString());
        System.out.println("Default suit (Expected SPADES):  " + defaultCard.getSuit());
        System.out.println("Default rank (Expected ACE):     " + defaultCard.getRank());
        System.out.println("Default faceUp (Expected false): " + defaultCard.isFaceUp() + "\n");

        // ----------------------------------------------------
        // Test 4: Copy Constructor
        // ----------------------------------------------------
        System.out.println("4. Testing Copy Constructor:");
        PlayingCard card1Copy = new PlayingCard(card1);
        System.out.println("Original: " + card1.toString());
        System.out.println("Copy:     " + card1Copy.toString());
        System.out.println("Expected: both identical [Hidden Card]");
        System.out.println("Suits match (Expected true):   " + (card1.getSuit() == card1Copy.getSuit()));
        System.out.println("Ranks match (Expected true):   " + (card1.getRank() == card1Copy.getRank()));
        System.out.println("FaceUp match (Expected true):  " + (card1.isFaceUp() == card1Copy.isFaceUp()) + "\n");

        System.out.println("4.5 Testing Null Copy Constructor:");

        // Test: passing null should throw IllegalArgumentException
        try {
            PlayingCard badCopy = new PlayingCard(null);
            System.out.println("FAIL: No exception was thrown. This should not print.");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: IllegalArgumentException caught.");
        } catch (Exception e) {
            System.out.println("FAIL: Wrong exception type thrown: " + e.getClass().getSimpleName());
            /*
            Program survives exception and continues executing, so the GUI won't
            just vanish if this edge case somehow gets triggered.
             */
        }

        // Test: program is still alive after the exception
        System.out.println("PASS: Program continued running after the exception.\n");


        // ----------------------------------------------------
        // Test 5: Getters
        // ----------------------------------------------------
        System.out.println("5. Testing Getters:");
        System.out.println("Card2 Suit  (Expected SPADES):  " + card2.getSuit());
        System.out.println("Card2 Rank  (Expected ACE):     " + card2.getRank());
        System.out.println("Card2 FaceUp (Expected true):   " + card2.isFaceUp() + "\n");

        // ----------------------------------------------------
        // Test 6: Setters
        // ----------------------------------------------------
        System.out.println("6. Testing Setters:");
        card1.setSuit(Suit.DIAMONDS);
        card1.setRank(Rank.NINE);
        card1.setFaceUp(true);
        System.out.println("Expected after setters: NINE of DIAMONDS");
        System.out.println("Actual:                 " + card1.toString() + "\n");

        // ----------------------------------------------------
        // Test 7: flipCard() Method
        // ----------------------------------------------------
        System.out.println("7. Testing flipCard():");
        System.out.println("Before flip - faceUp (Expected true): " + card1.isFaceUp());
        card1.flipCard();
        System.out.println("After flip  - faceUp (Expected false): " + card1.isFaceUp());
        System.out.println("toString after flip (Expected [Hidden Card]): " + card1.toString());
        card1.flipCard();
        System.out.println("Flip again  - faceUp (Expected true): " + card1.isFaceUp());
        System.out.println("toString after re-flip (Expected NINE of DIAMONDS): " + card1.toString() + "\n");

        // ----------------------------------------------------
        // Test 8: equals() Method
        // ----------------------------------------------------
        System.out.println("8. Testing equals() Method:");

        PlayingCard cardA = new PlayingCard(Suit.CLUBS, Rank.TEN, false);
        PlayingCard cardB = new PlayingCard(Suit.CLUBS, Rank.TEN, false);
        PlayingCard cardC = new PlayingCard(Suit.CLUBS, Rank.JACK, false);
        PlayingCard cardD = new PlayingCard(Suit.CLUBS, Rank.TEN, true); // same suit/rank, different faceUp

        System.out.println("Same object (Expected true):         " + cardA.equals(cardA));
        System.out.println("Exact copies (Expected true):        " + cardA.equals(cardB));
        System.out.println("Different rank (Expected false):     " + cardA.equals(cardC));
        System.out.println("Same rank/suit, diff faceUp (Expected false): " + cardA.equals(cardD));
        System.out.println("Compare to null (Expected false):    " + cardA.equals(null));
        System.out.println("Compare to String (Expected false):  " + cardA.equals("TEN of CLUBS") + "\n");

        // ----------------------------------------------------
        // Test 9: beats() Method (Comparison Logic)
        // ----------------------------------------------------
        System.out.println("9. Testing beats() Method [War Comparison]:");

        PlayingCard ace   = new PlayingCard(Suit.HEARTS, Rank.ACE, true);
        PlayingCard king  = new PlayingCard(Suit.SPADES, Rank.KING, true);
        PlayingCard two   = new PlayingCard(Suit.CLUBS, Rank.TWO, true);
        PlayingCard ace2  = new PlayingCard(Suit.DIAMONDS, Rank.ACE, true); // equal rank

        System.out.println("ACE beats KING (Expected true):   " + ace.beats(king));
        System.out.println("KING beats ACE (Expected false):  " + king.beats(ace));
        System.out.println("KING beats TWO (Expected true):   " + king.beats(two));
        System.out.println("TWO beats KING (Expected false):  " + two.beats(king));
        System.out.println("ACE beats ACE (Expected false):   " + ace.beats(ace2));
        System.out.println("Equal cards - neither beats:      " + (!ace.beats(ace2) && !ace2.beats(ace)) + "\n");

        // ----------------------------------------------------
        System.out.println("--- Tests Completed ---");
    }
}