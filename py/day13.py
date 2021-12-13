#!/usr/bin/env python3
# -*- coding:utf-8 -*-

def parse(path):
    with open(path) as f:
        return [[int(val) for val in line.strip()] for line in f]

sample_input = parse('../resources/day13_ex.txt')
puzzle_input = parse('../resources/day13.txt')

# print("Day 13\n"
#       "[Part 1] Example: %d, Puzzle: %d\n"
#       "[Part 2] Example: %d, Puzzle: %d"
#       %(part_1(sample_input), part_1(puzzle_input),
#         part_2(sample_input), part_2(puzzle_input)))
