import random
from datetime import datetime
from enum import Enum
from typing import *



class GridWorldAction(Enum):
    UP = 0
    DOWN = 1
    LEFT = 2
    RIGHT = 3


# Simple GridWorld.
# Finding the goal gives 1.0 reward and terminates the simulation
class GridWorld:
    def __init__(self, rows: int = 10, cols: int = 10, seed: int = 1012349991):
        self.rows = rows
        self.cols = cols
        self.x = 0
        self.y = 0
        self.walls = []

        random.seed(seed)
        self.goal_x = random.randint(0, self.rows - 1)
        self.goal_y = random.randint(0, self.cols - 1)
        random.seed(datetime.now().timestamp())

    def obs(self) -> Tuple[float, float]:
        return self.x, self.y

    def reset(self) -> Tuple[float, float]:
        self.x = 0
        self.y = 0
        return self.obs()

    def step(self, action: GridWorldAction) -> Tuple[Tuple, float, bool]:
        if action == GridWorldAction.UP:
            self.y -= 1
        elif action == GridWorldAction.DOWN:
            self.y += 1
        elif action == GridWorldAction.LEFT:
            self.x -= 1
        elif action == GridWorldAction.RIGHT:
            self.x += 1

        # clamp coordinates to grid world
        # TODO: should we punish by giving negative reward ?
        if self.x >= self.rows or self.x < 0 or self.y >= self.cols or self.y < 0:
            self.x = min(max(self.x, 0), self.rows - 1)
            self.y = min(max(self.y, 0), self.cols - 1)
            return self.obs(), 0, False

        # punish colliding with wall
        for wall_x, wall_y in self.walls:
            if self.x == wall_x and self.y == wall_y:
                # revert action
                if action == GridWorldAction.UP:
                    self.y += 1
                elif action == GridWorldAction.DOWN:
                    self.y -= 1
                elif action == GridWorldAction.LEFT:
                    self.x += 1
                elif action == GridWorldAction.RIGHT:
                    self.x -= 1
                return self.obs(), -1, False

        # check if x, and y match goal 
        if self.x == self.goal_x and self.y == self.goal_y:
            return self.obs(), 1, True
        return self.obs(), 0, False

    def action_space(self) -> List[GridWorldAction]:
        return [GridWorldAction.UP, GridWorldAction.DOWN, GridWorldAction.LEFT, GridWorldAction.RIGHT]

    def random_action(self) -> GridWorldAction:
        return random.choice(self.action_space())

    def add_wall(self, x, y):
        print(f"Added wall x:{x}, y:{y}")
        self.walls.append((x, y))
    
    def rem_wall(self, x, y):
        print(f"Removed wall x:{x}, y:{y}")
        self.walls.remove((x, y))
