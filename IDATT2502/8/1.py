import gym
import numpy as np 
import matplotlib.pyplot as plt
from math import radians
from sklearn.preprocessing import KBinsDiscretizer

learning_rate = 0.8
discount_factor = 1.0
episodes = 3000
bins = (8, 8)

env = gym.make("CartPole-v1", render_mode="rgb_array")
env.action_space.seed(69)

obs_min = (env.observation_space.low[2], -radians(50))
obs_max = (env.observation_space.high[2], radians(50))
print(obs_min, obs_max)

Q = np.zeros(bins + (env.action_space.n,))
print(Q)


# exploration and learning rate decrease over time
def get_exploration_rate(episodes, episode):
    return 1 - (episode / episodes)
def get_learning_rate(episodes, episode):
    return 1 - (episode / episodes)


def discretize_observation(pole_angle, pole_velocity):
    kbins = KBinsDiscretizer(n_bins=bins, encode='ordinal', strategy='uniform', subsample=200_000)
    kbins.fit([obs_min, obs_max])
    return tuple(map(int, kbins.transform([[pole_angle, pole_velocity]])[0]))


def bellman(old_value, immidate_reward, max_next_value, episode):
    lr = get_learning_rate(episodes, episode)
    return (1 - lr) * old_value + lr * (immidate_reward + discount_factor * max_next_value)


def get_next_action(episode, observation):
    if np.random.random() > get_exploration_rate(episodes, episode):
        # do best action based on Q table
        return np.argmax(Q[observation])
    else:
        # random action
        return env.action_space.sample()


rewards = []
for episode in range(episodes):
    # reset the environment to the initial state
    non_discretized_obs = env.reset()[0]
    # we only care about pole angel and pole pole_velocity
    _, _, pole_angel, pole_velocity = non_discretized_obs
    obs = discretize_observation(pole_angel, pole_velocity)

    finished = False
    episode_reward = 0
    while not finished:
        action = get_next_action(episode, obs)
        non_discretized_obs, reward, finished, _, _ = env.step(action)
        # unpack observations
        _, _, pole_angel, pole_velocity = non_discretized_obs
        new_obs = discretize_observation(pole_angel, pole_velocity)

        q_old = Q[obs][action]
        max_q_next = np.max(Q[new_obs])
        Q[obs][action] = bellman(q_old, reward, max_q_next, episode)

        obs = new_obs
        # one step is one reward
        episode_reward += 1

    if episode % 100 == 0:
        print(f"Episode: {episode}, reward: {episode_reward}")
    rewards.append(episode_reward)
    env.render()

env.close()

plt.figure()
plt.plot(range(0, episodes), rewards)
plt.title("Total reward per episode")
plt.xlabel("Episode")
plt.ylabel("Reward")
plt.show()
