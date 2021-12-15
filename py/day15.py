#!/usr/bin/env python3
# -*- coding:utf-8 -*-

from queue import PriorityQueue

def parse(path):
    with open(path) as f:
        return [[int(val) for val in line.strip()] for line in f]

sample_input = parse('../resources/day15_ex.txt')
puzzle_input = parse('../resources/day15.txt')

def get_neighbors(y, x):
    return [(y-1, x), (y+1, x), (y, x+1), (y, x-1)]

def solve(matrix):
    last_row = len(matrix) - 1
    last_col = len(matrix[0]) - 1
    goal = (last_row, last_col)

    stack, closed_set = PriorityQueue(), {(0, 0), }
    stack.put((0, (0, 0)))

    while stack:
        cur_cost, cur_coords = stack.get()
        if cur_coords == goal:
            return cur_cost
        neighbors = [(y, x) for (y, x) in get_neighbors(*cur_coords)
                     if 0 <= y <= last_row and 0 <= x <= last_col
                     and (y, x) not in closed_set]
        for neighbor in neighbors:
            y, x = neighbor
            cost = cur_cost + matrix[y][x]
            stack.put((cost, neighbor))
            closed_set.add(neighbor)

def expand(matrix):
    for row in matrix:
        copy = row
        for i in range(4):
            copy = [x+1 if x < 9 else 1 for x in copy]
            row.extend(copy)
    copy = matrix
    for i in range(4):
        copy = [[x+1 if x < 9 else 1 for x in row] for row in copy]
        matrix.extend(copy)
    return matrix

print("Day 15\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(solve(sample_input), solve(puzzle_input),
        solve(expand(sample_input)), solve(expand(puzzle_input))))
