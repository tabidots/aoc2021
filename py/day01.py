#!/usr/bin/env python3
# -*- coding:utf-8 -*-

sample_input = [199,200,208,210,200,207,240,269,260,263]

with open('../resources/day01.txt') as f:
    puzzle_input = [int(x) for x in f.readlines()]

def part_1(data):
    winners = 0
    for a, b in zip(data, data[1:]):
        if b > a:
            winners += 1
    return winners

def part_2(data):
    winners = 0
    for i, _ in enumerate(data):
        if i + 3 == len(data):
            return winners
        if sum(data[i+1:i+4]) > sum(data[i:i+3]):
            winners += 1

print("Day 1\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
