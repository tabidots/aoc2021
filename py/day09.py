#!/usr/bin/env python3
# -*- coding:utf-8 -*-

from operator import mul
from functools import reduce

def parse(path):
    with open(path) as f:
        return [[int(val) for val in line.strip()] for line in f]

sample_input = parse('../resources/day09_ex.txt')
puzzle_input = parse('../resources/day09.txt')

def low_points(heatmap):
    result = []
    max_row = len(heatmap)
    max_col = len(heatmap[0])
    for row in range(max_row):
        for col in range(max_col):
            neighbors = [(y, x) for (y, x) in
                         [(row-1,col), (row+1,col), (row,col-1), (row,col+1)]
                         if 0 <= y < max_row and 0 <= x < max_col]
            if all([heatmap[row][col] < heatmap[y][x] for (y, x) in neighbors]):
                result.append((row, col))
    return result

def part_1(heatmap):
  return sum([heatmap[y][x] + 1 for (y, x) in low_points(heatmap)])

def part_2(heatmap):
    basins = []
    max_row = len(heatmap)
    max_col = len(heatmap[0])
    for coords in low_points(heatmap):
        basin = {coords}
        neighbors = []
        while True:
            neighbors = [(y, x)
                         for (row, col) in basin
                         for (y, x) in [(row-1,col), (row+1,col), (row,col-1), (row,col+1)]
                         if 0 <= y < max_row and 0 <= x < max_col
                         and heatmap[y][x] < 9
                         and (y, x) not in basin]
            if not neighbors:
                break
            basin.update(neighbors)
        basins.append(basin)
    basins = sorted([len(b) for b in basins], reverse=True)
    return reduce(mul, basins[0:3])

print("Day 9\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
