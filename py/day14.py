#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re
from collections import Counter

def parse(path):
    result = {'polymer': Counter(), 'last_char': "", 'pairs': [], 'repls': []}
    with open(path) as f:
        for line in f:
            line = line.strip()
            if re.search(r"->", line):
                a, c, b = re.findall(r"\w", line)
                result['pairs'].append(a + c)
                result['repls'].append((a + b, b + c))
            elif re.match(r"\w+", line):
                result['last_char'] = line[-1]
                for i in range(len(line) - 1):
                    result['polymer'].update({line[i:i+2]: 1})
    return result

sample_input = parse('../resources/day14_ex.txt')
puzzle_input = parse('../resources/day14.txt')

def step(polymer, last_char, pairs, repls):
    result = Counter()
    for pair, count in polymer.items():
        if pair in pairs:
            idx = pairs.index(pair)
            repl = repls[idx]
            result.update({repl[0]: count, repl[1]: count})
        else:
            result.update({pair: count})
    return {'polymer': result, 'last_char': last_char, 'pairs': pairs, 'repls': repls}

def solve(data, steps):
    result = data
    for i in range(steps):
        result = step(**result)
    letters = Counter()
    for pair, count in result['polymer'].items():
        letters.update({pair[0]: count})
    letters.update({data['last_char']: 1})
    counts = letters.values()
    return max(counts) - min(counts)

print("Day 14\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(solve(sample_input, 10), solve(puzzle_input, 10),
        solve(sample_input, 40), solve(puzzle_input, 40)))
