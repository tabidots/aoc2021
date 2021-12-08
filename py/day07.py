#!/usr/bin/env python3
# -*- coding:utf-8 -*-

from functools import lru_cache

def parse(path):
    with open(path) as f:
        return [int(x) for x in f.read().split(",")]

sample_input = parse('../resources/day07_ex.txt')
puzzle_input = parse('../resources/day07.txt')

def total_basic_cost_at(destination, data):
    return sum([abs(position - destination) for position in data])

def part_1(data):
    return min([total_basic_cost_at(d, data) for d in range(max(data) + 1)])

# Had to browse the Reddit thread for the hint: Use triangular numbers.
@lru_cache
def triangular(n):
    return n * (n + 1) // 2

def dynamic_cost(position, destination):
    return triangular(abs(position - destination))

def total_dynamic_costs(data):
    for destination in range(max(data) + 1):
        yield sum([dynamic_cost(position, destination) for position in data])

def part_2(data):
    return min(total_dynamic_costs(data))

print("Day 7\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
