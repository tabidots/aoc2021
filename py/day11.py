#!/usr/bin/env python3
# -*- coding:utf-8 -*-

def parse(path):
    with open(path) as f:
        return [[int(val) for val in line.strip()] for line in f]

sample_input = parse('../resources/day11_ex.txt')
puzzle_input = parse('../resources/day11.txt')

def neighbors(y, x):
    return [(y-1, x-1), (y-1, x), (y-1,x+1),
            (y  , x-1),           (y  ,x+1),
            (y+1, x-1), (y+1, x), (y+1,x+1)]

def step(state):
    grid, flashes = state['grid'], state['flashes']
    max_row = len(grid)
    max_col = len(grid[0])
    grid = [[x+1 for x in row] for row in grid]
    while True:
        # Find the first octopus that needs to flash and hasn't yet
        stack = [(y, x) for (y, row) in enumerate(grid)
                        for (x, _) in enumerate(row)
                        if grid[y][x] > 9]
        if not stack:
            break

        # Trigger the flash
        flash_y, flash_x = stack[0]
        to_get_flashed = [(y, x) for (y, x) in neighbors(flash_y, flash_x)
                          if 0 <= y < max_row and 0 <= x < max_col]
        for y, x in to_get_flashed:
            grid[y][x] += 1
        grid[flash_y][flash_x] = -1000
        flashes += 1

    # Reset all octopi that flashed
    grid = [[max(x, 0) for x in row] for row in grid]

    return {'grid': grid, 'flashes': flashes}

def part_1(data):
    state = {'grid': data, 'flashes': 0}
    for i in range(100):
        state = step(state)
    return state['flashes']

def part_2(data):
    state = {'grid': data, 'flashes': 0}
    steps = 0
    while True:
        state = step(state)
        steps += 1
        if all([x == 0 for row in state['grid'] for x in row]):
            return steps

print("Day 11\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
