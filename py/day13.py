#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re

def parse(path):
    result = {'grid': [], 'folds': []}
    with open(path) as f:
        for line in f.readlines():
            if not line.strip():
                continue
            elif re.match(r"fold", line):
                axis, crease = re.search(r"([xy])=(\d+)", line).group(1, 2)
                result['folds'].append({'axis': axis, 'crease': int(crease)})
            else:
                coords = tuple(int(x) for x in re.match(r"(\d+),(\d+)", line).group(1, 2))
                result['grid'].append(coords)
    return result

sample_input = parse('../resources/day13_ex.txt')
puzzle_input = parse('../resources/day13.txt')

def fold(grid, axis, crease):
    new_grid = []
    for x, y in grid:
        if axis == 'y' and y > crease:
            new_grid.append((x, y - 2*abs(y - crease)))
        elif axis == 'x' and x > crease:
            new_grid.append((x - 2*abs(x - crease), y))
        else:
            new_grid.append((x, y))
    return new_grid

def display(grid):
    width = max([x for x, y in grid])
    height = max([y for x, y in grid])
    for y in range(height + 1):
        print(".".join(["#" if (x, y) in grid else " " for x in range(width + 1)]))

def part_1(data):
    return len(set(fold(data['grid'], **data['folds'][0])))

def part_2(data):
    grid = data['grid']
    for f in data['folds']:
        grid = fold(grid, **f)
    display(grid)

print("Day 11\n"
      "[Part 1] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input)))
print("[Part 2] Example:\n")
part_2(sample_input)
print("\n[Part 2] Puzzle:\n")
part_2(puzzle_input)
