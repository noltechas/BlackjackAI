import random

from src.main.python.Deck import Deck
from src.main.python.Hand import Hand


class Game:

    TOTAL_DECKS = 6  # Total number of decks in the shoe
    RESHUFFLE_THRESHOLD = TOTAL_DECKS * 26  # Half of the total cards

    def __init__(self):
        self.deck = Deck(self.TOTAL_DECKS)  # Initialize with 6 decks
        self.deck.shuffle()  # Shuffle only once at the beginning
        self.player_hands = [Hand()]
        self.dealer_hand = Hand()
        self.wallet = 1000  # Player starts with $1000

    def play(self):
        while self.wallet >= 5 and len(self.deck.cards) > self.RESHUFFLE_THRESHOLD:
            self.place_bet()
            self.initial_deal()

            print(f"Dealer's face-up card: {self.dealer_hand.cards[0]} ({self.dealer_hand.cards[0].rank.value})")

            for i, hand in enumerate(self.player_hands):
                print(f"Playing hand {i + 1}: {hand} ({hand.get_display_value()})")
                self.player_turn(hand)

            self.dealer_turn()
            self.determine_winner()

            print(f"Your current wallet: ${self.wallet}")

        if len(self.deck.cards) <= self.RESHUFFLE_THRESHOLD:
            print("The shoe is nearly empty. Game over!")
        else:
            print("You don't have enough money to continue. Game over!")