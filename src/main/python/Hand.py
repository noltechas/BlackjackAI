class Hand:

    def __init__(self):
        self.cards = []

    def add_card(self, card):
        self.cards.append(card)

    def split(self):
        if len(self.cards) == 2 and self.can_split():
            return self.cards.pop()
        return None

    def can_split(self):
        return self.cards[0].rank.value == self.cards[1].rank.value

    def is_busted(self):
        return self.get_value() > 21

    def get_value(self):
        value = sum(card.rank.value for card in self.cards)
        num_aces = sum(1 for card in self.cards if card.rank == Rank.ACE)
        while value <= 11 and num_aces:
            value += 10
            num_aces -= 1
        return value

    def get_display_value(self):
        value = self.get_value()
        if any(card.rank == Rank.ACE for card in self.cards) and value <= 11:
            return f'{value}/{value + 10}'
        return str(value)

    def __str__(self):
        return ', '.join(str(card) for card in self.cards)