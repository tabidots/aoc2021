#!/usr/bin/env python3
# -*- coding:utf-8 -*-

from functools import reduce

def parse(path):
    with open(path) as f:
        return [line.strip() for line in f]

sample_input = parse('../resources/day10_ex.txt')
puzzle_input = parse('../resources/day10.txt')

openers = ["(", "[", "{", "<"]
pairs = {"(": ")", "[": "]", "{": "}", "<": ">"}
checker_scores = {")": 3, "]": 57, "}": 1197, ">": 25137}
ac_scores = {")": 1, "]": 2, "}": 3, ">": 4}

def categorize_lines(data):
    result = {'corrupt': [], 'incomplete': []}
    for line in data:
        corrupt = False
        stack = []
        for char in line:
            if char in openers:
                stack.append(char)
            elif char == pairs[stack[-1]]:
                stack.pop()
            else:
                corrupt = True
                result['corrupt'].append(checker_scores[char])
                break
        if not corrupt:
            result['incomplete'].append(stack)
    return result

def part_1(data):
    return sum(categorize_lines(data)['corrupt'])

def part_2(data):
    result = []
    for stack in categorize_lines(data)['incomplete']:
        completion = [pairs[char] for char in reversed(stack)]
        result.append(reduce(lambda a, b: (a * 5) + ac_scores[b], \
                             completion, 0))
    return sorted(result)[len(result) // 2]

print("Day 10\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
