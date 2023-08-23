import random
from Suit import Suit
from Rank import Rank
from Card import Card

class Deck:

    def __init__(self, num_decks=1):
        self.cards = []
        for _ in range(num_decks):
            for suit in Suit:
                for rank in Rank:
                    self.cards.append(Card(rank, suit))
        random.shuffle(self.cards)

    def draw(self):
        return self.cards.pop()

    def shuffle(self):
        random.shuffle(self.cards)

    def remaining_cards(self):
        return len(self.cards)