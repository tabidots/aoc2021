#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re

def parse(path):
    with open(path) as f:
        return [re.match(r"(\w+) (\d+)", x).group(1, 2) for x in f.readlines()]

sample_input = parse('../resources/day02_ex.txt')
puzzle_input = parse('../resources/day02.txt')

def part_1(data):
    horiz = 0
    depth = 0
    for command, unit in data:
        x = int(unit)
        if command == 'forward':
            horiz += x
        elif command == 'down':
            depth += x
        else:
            depth -= x
    return horiz * depth

def part_2(data):
    horiz = 0
    depth = 0
    aim = 0
    for command, unit in data:
        x = int(unit)
        if command == 'forward':
            horiz += x
            depth += aim * x
        elif command == 'down':
            aim += x
        else:
            aim -= x
    return horiz * depth

print("Day 2\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
