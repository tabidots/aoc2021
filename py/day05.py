#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re
from collections import Counter
from itertools import repeat, chain

def inclusive_range(a, b):
    if a > b:
        rng = list(range(b, a + 1))
        rng.reverse()
        return rng
    else:
        return list(range(a, b + 1))


class Line:
    def __init__(self, x1, y1, x2, y2):
        self.is_diagonal = x1 != x2 and y1 != y2
        self.points = self.draw(x1, y1, x2, y2)

    def draw(self, x1, y1, x2, y2):
        if self.is_diagonal:
            return list(zip(inclusive_range(x1, x2), inclusive_range(y1, y2)))
        elif x1 == x2:
            return [(x1, y) for y in inclusive_range(y1, y2)]
        else:
            return [(x, y1) for x in inclusive_range(x1, x2)]


def parse(path):
    lines = []
    with open(path) as f:
        for line in f.readlines():
            coords = [int(x) for x in re.match(r'(\d+),(\d+) -> (\d+),(\d+)', line).group(1,2,3,4)]
            lines.append(Line(*coords))
    return lines


sample_input = parse('../resources/day05_ex.txt')
puzzle_input = parse('../resources/day05.txt')


def count_overlaps(data):
    grid = Counter()
    for line in data:
        for point in line.points:
            grid.update({point: 1})
    return len([freq for freq in grid.values() if freq > 1])


def part_1(data):
    return count_overlaps([line for line in data if not line.is_diagonal])

def part_2(data):
    return count_overlaps(data)


print("Day 5\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
