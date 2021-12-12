#!/usr/bin/env python3
# -*- coding:utf-8 -*-

from collections import Counter

sample_input = [3,4,3,1,2]

with open('../resources/day06.txt') as f:
    puzzle_input = [int(x) for x in f.read().split(',')]

def parse(data):
    c = Counter()
    for timer in data:
        c.update({timer: 1})
    return c

def elapse(fish):
    c_new = Counter()
    for timer in fish:
        num_fish = fish[timer]
        if timer == 0:
            c_new.update({6: num_fish, 8: num_fish})
        else:
            c_new.update({timer - 1: num_fish})
    return c_new

def solve(data, iters):
    fish = parse(data)
    for i in range(iters):
        fish = elapse(fish)
    return sum(fish.values())

print("Day 6\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(solve(sample_input, 80), solve(puzzle_input, 80),
        solve(sample_input, 256), solve(puzzle_input, 256)))
