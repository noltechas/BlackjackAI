from src.main.python.Rank import Rank


class Card:

    def __init__(self, rank, suit):
        self.rank = rank
        self.suit = suit

    def __str__(self):
        return f'{self.rank} of {self.suit}'

    @property
    def value(self):
        return self.rank.value

    def is_face_card(self):
        return self.rank in [Rank.JACK, Rank.QUEEN, Rank.KING]

    def is_ace(self):
        return self.rank == Rank.ACE