import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import py4j.GatewayServer;

class Game {
    private static final int TOTAL_DECKS = 6; // Total number of decks in the shoe
    private static final int RESHUFFLE_THRESHOLD = TOTAL_DECKS * 26; // Half of the total cards
    private Deck deck;
    private List<Hand> playerHands;
    private Hand dealerHand;
    private Scanner scanner;
    private int wallet; // Player's money
    private int currentBet; // Amount bet on the current hand
    private GatewayServer gateway;

    public Game() {
        deck = new Deck(TOTAL_DECKS); // Initialize with 6 decks
        deck.shuffle(); // Shuffle only once at the beginning
        playerHands = new ArrayList<>();
        playerHands.add(new Hand());
        dealerHand = new Hand();
        scanner = new Scanner(System.in);
        wallet = 1000; // Player starts with $1000
        this.gateway = new GatewayServer(this);
        this.gateway.start();
    }

    public void play() {
        while (wallet >= 5 && deck.remainingCards() > RESHUFFLE_THRESHOLD) {
            resetHands(); // Reset hands for a new round
            placeBet();
            initialDeal();

            System.out.println("Dealer's face-up card: " + dealerHand.getCards().get(0) + " (" + dealerHand.getCards().get(0).getRank().getValue() + ")");

            for (int i = 0; i < playerHands.size(); i++) {
                Hand currentHand = playerHands.get(i);
                System.out.println("Playing hand " + (i + 1) + ": " + currentHand + " (" + currentHand.getDisplayValue() + ")");
                playerTurn(currentHand);
            }

            dealerTurn();
            determineWinner();

        }
        if (deck.remainingCards() <= RESHUFFLE_THRESHOLD) {
            System.out.println("The shoe is nearly empty. Game over!");
        }
        System.out.println("You don't have enough money to continue. Game over!");
    }

    private void placeBet() {
        while (true) {
            System.out.println("Your current wallet: $" + wallet);
            System.out.print("Place your bet (min $5, max $500): ");
            try {
                currentBet = Integer.parseInt(scanner.nextLine());
                if (currentBet >= 5 && currentBet <= 500 && currentBet <= wallet) {
                    wallet -= currentBet; // Deduct the bet from the wallet
                    break;
                } else {
                    System.out.println("Invalid bet amount. Please bet between $5 and $500, and ensure you have enough in your wallet.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void initialDeal() {
        for (Hand hand : playerHands) {
            hand.addCard(deck.draw());
            hand.addCard(deck.draw());
        }
        dealerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());
    }

    private void playerTurn(Hand currentHand) {
        while (true) {
            System.out.println("Your current hand: " + currentHand + " (" + currentHand.getDisplayValue() + ")");
            System.out.println("Do you want to (h)it, (s)tand, (d)ouble down, or s(p)lit?");
            String choice = scanner.nextLine().toLowerCase();
            switch (choice) {
                case "h":
                    currentHand.addCard(deck.draw());
                    if (currentHand.isBusted()) {
                        return;
                    }
                    break;
                case "s":
                    return;
                case "d":
                    currentHand.addCard(deck.draw());
                    currentHand.setDoubleDown(true); // Set the doubleDown flag
                    wallet -= currentBet; // Deduct the additional bet from the wallet
                    currentBet *= 2; // Double the current bet
                    System.out.println("Your hand after doubling down: " + currentHand);
                    return;
                case "p":
                    if (currentHand.canSplit() && playerHands.size() < 4) {
                        Hand newHand = new Hand();
                        newHand.addCard(currentHand.split());
                        newHand.addCard(deck.draw());
                        playerHands.add(newHand);
                        currentHand.addCard(deck.draw());

                        wallet -= currentBet; // Deduct the bet for the split hand from the wallet

                        System.out.println("Your hands after splitting: ");
                        for (int i = 0; i < playerHands.size(); i++) {
                            System.out.println("Hand " + (i + 1) + ": " + playerHands.get(i));
                        }
                    } else {
                        System.out.println("Cannot split this hand.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please choose again.");
                    break;
            }
        }
    }
    private void dealerTurn() {
        while (dealerHand.getValue() < 17) {
            dealerHand.addCard(deck.draw());
        }
    }

    private void determineWinner() {
        System.out.println("Dealer's hand value: " + dealerHand.getDisplayValue());
        for (int i = 0; i < playerHands.size(); i++) {
            Hand hand = playerHands.get(i);
            System.out.println("Your hand " + (i + 1) + " value: " + hand.getDisplayValue());
            checkHandOutcome(hand, "hand " + (i + 1));
        }
    }

    private void checkHandOutcome(Hand hand, String handName) {
        int payout = hand.isDoubleDown() ? currentBet * 2 : currentBet; // Calculate the payout based on double down

        if (hand.isBusted()) {
            System.out.println("Your " + handName + " busted! Dealer wins your bet of $" + payout + ".");
        } else if (dealerHand.isBusted()) {
            System.out.println("Dealer busted! Your " + handName + " wins. You gain $" + (2 * payout) + ".");
            wallet += 2 * payout;
        } else if (hand.getValue() > dealerHand.getValue()) {
            System.out.println("Your " + handName + " wins! You gain $" + (2 * payout) + ".");
            wallet += 2 * payout;
        } else if (hand.getValue() < dealerHand.getValue()) {
            System.out.println("Dealer wins against your " + handName + ". Dealer wins your bet of $" + payout + ".");
        } else {
            System.out.println("Your " + handName + " ties with the dealer. You get back your bet of $" + payout + ".");
            wallet += payout;
        }
    }

    private void resetHands() {
        playerHands.clear(); // Clear the list of player hands
        playerHands.add(new Hand()); // Add a new hand to the list

        dealerHand = new Hand(); // Create a new dealer hand
    }

}