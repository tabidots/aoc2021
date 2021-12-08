#!/usr/bin/env python3
# -*- coding:utf-8 -*-

from collections import Counter

def parse(path):
    with open(path) as f:
        return [x.strip() for x in f.readlines()]

sample_input = parse('../resources/day03_ex.txt')
puzzle_input = parse('../resources/day03.txt')

def most_least_common_bit_at(data, index):
    res = ""
    c = Counter()
    for entry in data:
        c.update({entry[index]: 1})
    return (max(c, key=c.get), min(c, key=c.get))

def part_1(data):
    gam_eps = [most_least_common_bit_at(data, i) for i in range(len(data[0]))]
    gamma = "".join([x[0] for x in gam_eps])
    epsilon = "".join([x[1] for x in gam_eps])
    return int(gamma, 2) * int(epsilon, 2)

def oxygen_rating(data):
    oxygen_list = data
    i = 0
    while True:
        if len(oxygen_list) == 1:
            return int(oxygen_list[0], 2)
        (mcb, lcb) = most_least_common_bit_at(oxygen_list, i)
        if mcb == lcb:
            oxygen_list = [entry for entry in oxygen_list if entry[i] == '1']
        else:
            oxygen_list = [entry for entry in oxygen_list if entry[i] == mcb]
        i += 1

def co2_rating(data):
    co2_list = data
    i = 0
    while True:
        if len(co2_list) == 1:
            return int(co2_list[0], 2)
        (mcb, lcb) = most_least_common_bit_at(co2_list, i)
        if mcb == lcb:
            co2_list = [entry for entry in co2_list if entry[i] == '0']
        else:
            co2_list = [entry for entry in co2_list if entry[i] == lcb]
        i += 1

def part_2(data):
    return oxygen_rating(data) * co2_rating(data)

print("Day 3\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
