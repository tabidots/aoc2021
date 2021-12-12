#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re
from collections import defaultdict

def parse(path):
    system = defaultdict(lambda: [])
    with open(path) as f:
        for line in f:
            head, tail = re.split(r'\W+', line.strip())
            system[head].append(tail)
            system[tail].append(head)
    system = {head: [t for t in system[head] if t != 'start']
              for head in system if head != 'end'}
    return system

sample_1 = parse('../resources/day12_ex1.txt')
sample_2 = parse('../resources/day12_ex2.txt')
sample_3 = parse('../resources/day12_ex3.txt')
puzzle_input = parse('../resources/day12.txt')

def is_small_cave(cave):
    return re.match(r"^[a-z]{1,2}$", cave)

def not_distinct(lst):
    return len(lst) != len(set(lst))

def solve(system, mode='part_1'):
    cur_paths = [['start']]
    new_paths = []
    while True:
        if all([path[-1] == 'end' for path in cur_paths]):
            return len(cur_paths)
        for path in cur_paths:

            # successful path
            if path[-1] == 'end':
                new_paths.append(path)
                continue

            visited_small_caves = [c for c in path if is_small_cave(c)]
            if mode == 'part_1' or (mode == 'part_2' and
                                    not_distinct(visited_small_caves)):
                choices = [c for c in system[path[-1]]
                           if c not in visited_small_caves]
            else:
                choices = system[path[-1]]

            # dead end
            if path[-1] != 'end' and not choices:
                continue
            new_paths.extend([path + [c] for c in choices])
        cur_paths = new_paths
        new_paths = []

print("Day 12\n"
      "[Part 1] Examples: %d, %d, %d; Puzzle: %d\n"
      "[Part 2] Examples: %d, %d, %d; Puzzle: %d"
      %(solve(sample_1), solve(sample_2), solve(sample_3), solve(puzzle_input),
        solve(sample_1, mode='part_2'), solve(sample_2, mode='part_2'),
        solve(sample_3, mode='part_2'), solve(puzzle_input, mode='part_2')))
