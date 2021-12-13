#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re

def parse(path):
    with open(path) as f:
        return [re.match(r"(\w+) (\d+)", x).group(1, 2) for x in f.readlines()]

sample_input = parse('../resources/day02_ex.txt')
puzzle_input = parse('../resources/day02.txt')

def solve(data, aim=None):
    horiz = 0
    depth = 0
    for command, unit in data:
        x = int(unit)
        if command == 'forward':
            horiz += x            # Part 1
            if aim is not None:   # Part 2
                depth += aim * x
        elif command == 'down':
            if aim is None:       # Part 1
                depth += x
            else:                 # Part 2
                aim += x
        else:
            if aim is None:       # Part 1
                depth -= x
            else:                 # Part 2
                aim -= x
    return horiz * depth

print("Day 2\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(solve(sample_input), solve(puzzle_input),
        solve(sample_input,aim=0), solve(puzzle_input,aim=0)))
