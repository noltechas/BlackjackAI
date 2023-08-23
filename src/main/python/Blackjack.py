
class Blackjack:

    def __init__(self):
        from src.main.python.Game import Game
        self.game = Game()

    def play(self):
        self.game.play()


if __name__ == '__main__':
    blackjack = Blackjack()
    blackjack.play()