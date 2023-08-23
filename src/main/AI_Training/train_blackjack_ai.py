import numpy as np
from py4j.java_gateway import JavaGateway
from tensorflow import keras
from keras import layers

# Connect to the Java Gateway
gateway = JavaGateway()
game = gateway.entry_point

# Define the neural network model
model = keras.Sequential([
    layers.Dense(32, activation='relu', input_shape=(5,)),  # 5 inputs: dealer's card, player's two cards, wallet, current bet
    layers.Dense(32, activation='relu'),
    layers.Dense(4, activation='softmax')  # 4 outputs: hit, stand, double down, split
])
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

# Training loop
for episode in range(5000):
    state = game.getInitialState()  # Get the initial state from the game
    done = False
    while not done:
        # Predict the action using the model
        action_probabilities = model.predict(np.array([state]))[0]
        action = np.argmax(action_probabilities)

        # Take the action in the game
        next_state, reward, done = game.step(action)

        # Train the model using the reward
        target = reward + np.max(model.predict(np.array([next_state]))[0])
        with tf.GradientTape() as tape:
            predictions = model(np.array([state]))
            loss = keras.losses.mean_squared_error(target, predictions)
        grads = tape.gradient(loss, model.trainable_variables)
        optimizer.apply_gradients(zip(grads, model.trainable_variables))

        state = next_state

    # Reset the game for the next episode
    game.reset()

model.save('blackjack_model.h5')
