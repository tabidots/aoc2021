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

def sum_partitions(lst, chunk_size, step=-1):
    start = 0
    end = chunk_size
    sums = []
    if step == -1:
        step = chunk_size
    while start <= len(lst):
        if len(lst[start:end]) < chunk_size:
            return sums
        sums.append(sum(lst[start:end]))
        start += step
        end = start + chunk_size

def part_2(data):
    return part_1(sum_partitions(data, 3, 1))

print("Day 1\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
