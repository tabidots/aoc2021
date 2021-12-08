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

def iterate(f, n):
    """Works like Clojure (iterate f x)."""
    # https://codereview.stackexchange.com/questions/80524/functional-programming-approach-to-repeated-function-application
    if n == 1:
        return f
    return lambda x: f(iterate(f, n-1)(x))

def part_1(data):
    fish = iterate(elapse, 80)(parse(data))
    return sum(fish.values())

def part_2(data):
    fish = iterate(elapse, 256)(parse(data))
    return sum(fish.values())

print("Day 6\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
