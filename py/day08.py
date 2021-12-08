#!/usr/bin/env python3
# -*- coding:utf-8 -*-

from itertools import chain

def parse(path):
    result = []
    with open(path) as f:
        for line in f.readlines():
            sp, ov = line.strip().split(' | ')
            result.append({'signal_patterns': [set(p) for p in sp.split(' ')],
                             'output_values': [set(v) for v in ov.split(' ')]})
    return result

small_sample_input = parse('../resources/day08_ex_small.txt')
sample_input = parse('../resources/day08_ex.txt')
puzzle_input = parse('../resources/day08.txt')

def part_1(data):
    output_values = chain.from_iterable([entry['output_values'] for entry in data])
    return len([True for v in output_values if len(v) in [2,3,4,7]])

def decode_patterns(patterns):
    one    = [p for p in patterns if len(p) == 2][0]
    seven  = [p for p in patterns if len(p) == 3][0]
    four   = [p for p in patterns if len(p) == 4][0]
    eight  = [p for p in patterns if len(p) == 7][0]
    fivers = [p for p in patterns if len(p) == 5]
    sixers = [p for p in patterns if len(p) == 6]
    nine   = [p for p in sixers if four.issubset(p)][0]
    sixers.remove(nine)
    zero   = [p for p in sixers if one.issubset(p)][0]
    sixers.remove(zero)
    six    = sixers[0]
    three  = [p for p in fivers if one.issubset(p)][0]
    fivers.remove(three)
    five   = [p for p in fivers if p.issubset(six)][0]
    fivers.remove(five)
    two    = fivers[0]
    return [zero, one, two, three, four, five, six, seven, eight, nine]

def part_2(data):
    result = 0
    for entry in data:
        lut = decode_patterns(entry['signal_patterns'])
        digits = [str(lut.index(v)) for v in entry['output_values']]
        result += (int("".join(digits)))
    return result

print("Day 8\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
