import java.util.ArrayList;
import java.util.List;

class Hand {
    private List<Card> cards;
    private boolean doubleDown = false;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getValue() {
        int value = 0;
        int aces = 0;
        for (Card card : cards) {
            value += card.getRank().getValue();
            if (card.getRank() == Rank.ACE) {
                aces++;
            }
        }
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        return value;
    }

    public boolean isBusted() {
        return getValue() > 21;
    }

    @Override
    public String toString() {
        return cards.toString();
    }

    public boolean canSplit() {
        if (cards.size() != 2) {
            return false;
        }
        Card card1 = cards.get(0);
        Card card2 = cards.get(1);
        return (card1.getRank().getValue() == card2.getRank().getValue()) || // Both cards have the same value
                (card1.getRank().getValue() == 10 && card2.getRank().getValue() == 10); // Both cards are 10-value cards
    }

    public Card split() {
        return cards.remove(1); // Remove and return the second card
    }

    public String getDisplayValue() {
        int value = getValue();
        if (hasAce()) { // If there's an Ace and the value is 11 or less, it's a soft hand
            if(value + 10 > 21)
                return String.valueOf(value);
            return (value + 10) + " or " + value; // Display both possible values
        }
        return String.valueOf(value);
    }

    public void setDoubleDown(boolean doubleDown) {
        this.doubleDown = doubleDown;
    }

    public boolean isDoubleDown() {
        return doubleDown;
    }

    private boolean hasAce() {
        for (Card card : cards) {
            if (card.getRank() == Rank.ACE) {
                return true;
            }
        }
        return false;
    }
}