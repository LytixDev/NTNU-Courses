from grid_world import GridWorld, GridWorldAction
from typing import Tuple
import numpy as np



# exploration and learning rate decrease over time
def get_rate(episodes, episode):
    return 1 - (episode / episodes)


def bellman(old_value, immidate_reward, max_next_value, discount_factor, episode, episodes):
    lr = get_rate(episodes, episode)
    return old_value + lr * (immidate_reward + discount_factor * max_next_value - old_value)


def get_next_action(env, Q, episode, episodes, x, y):
    # random action
    if np.random.random() < get_rate(episodes, episode):
        return env.random_action()

    # move based on Q table
    return GridWorldAction(np.argmax(Q[x][y]))


def gw_q_solve(verbose=False, env=None, Q=None) -> Tuple[GridWorld, np.ndarray]:
    if env is None:
        env = GridWorld(rows=20, cols=20)
    episodes = 200
    discount_factor = 0.9

    if Q is None:
        Q = np.zeros((env.rows, env.cols, len(env.action_space())))
    if verbose:
        print(Q)

    for episode in range(episodes):
        x, y = env.reset()
        finished = False
        count = 0

        while not finished:
            action = get_next_action(env, Q, episode, episodes, x, y)
            (new_x, new_y), reward, finished = env.step(action)

            current_value = Q[x][y][action.value]
            max_next_value = np.max(Q[new_x][new_y])
            Q[x][y][action.value] = bellman(current_value, reward, max_next_value, discount_factor, episode, episodes)
            x, y = new_x, new_y
            count += 1

        if verbose:
            print(f"Episode {episode} done in {count} moves")
            
    print(f"Ran {episodes} episodes ")
    if verbose:
        print(f"goal: x:{env.goal_x}, y:{env.goal_y}")
        print("\t\t".join(action.name for action in env.action_space()))
        print(Q)

    return env, Q


if __name__ == "__main__":
    gw_q_solve(verbose=True)
