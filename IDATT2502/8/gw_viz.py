import pygame
import numpy as np

from gw_q_solve import gw_q_solve

env, Q = gw_q_solve()

# pygame constants
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
FPS = 60
WIDTH = HEIGHT = 800 
ROWS = COLS = env.rows
PIXEL_SIZE = WIDTH // ROWS

ENV_START = (0, 0)
ENV_GOAL = (env.goal_x, env.goal_y)

# grid of max rewards
max_reward_grid = np.zeros((ROWS, COLS))

def updatet_max_reward_grid():
    for x, row in enumerate(Q):
        for y, col in enumerate(row):
            max_reward_grid[x][y] = np.max(col)


def get_shade_of_gray(reward):
    return tuple([255 * (1 - reward) for _ in range(3)])


def draw_grid(win, grid):
    for x, row in enumerate(grid):
        for y, reward in enumerate(row):
            color = get_shade_of_gray(reward)
            pygame.draw.rect(win, color, (x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE))

    # draw walls as red
    for x, y in env.walls:
        pygame.draw.rect(win, (255, 0, 0), (x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE))
    # draw start pos blue
    pygame.draw.rect(win, (0, 0, 255), (ENV_START[0] * PIXEL_SIZE, ENV_START[1] * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE))
    # draw goal pos green
    pygame.draw.rect(win, (0, 255, 0), (ENV_GOAL[0] * PIXEL_SIZE, ENV_GOAL[1] * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE))


def draw(win, grid):
    draw_grid(win, grid)
    pygame.display.update()


def get_pixel_coord(pos):
    x, y = pos
    col = y // PIXEL_SIZE
    row = x // PIXEL_SIZE
    return row, col


def main():
    global env
    global Q
    pygame.init()
    WIN = pygame.display.set_mode((WIDTH, HEIGHT))
    pygame.display.set_caption("Paint")
    
    running = True
    clock = pygame.time.Clock()
    # main event loop
    while running:
        clock.tick(FPS)
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False

            if pygame.mouse.get_pressed()[0]:
                pos = pygame.mouse.get_pos()
                x, y = get_pixel_coord(pos)
                walls = env.walls
                if (x, y) in walls:
                    env.rem_wall(x, y)
                else:
                    env.add_wall(x, y)

                env, Q = gw_q_solve(env=env)
                updatet_max_reward_grid()


        draw(WIN, max_reward_grid)
    
    # take a screenshot of the surface before exiting
    pygame.image.save(WIN, "viz.png")
    pygame.quit()


if __name__ == "__main__":
    main()
