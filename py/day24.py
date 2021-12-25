#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re

with open("../resources/day24.txt") as f:
    alu_steps, block = [], []
    for i, line in enumerate(f.readlines()):
        if re.match("inp w", line):
            if block:
                alu_steps.append(block)
            block = []
        if i % 18 in [4, 5, 15]:
            val = re.search(r"-?\d+", line)
            block.append(int(val.group()))
    alu_steps.append(block)

def alu_func(z, w, a, b, c):
    x = 0 if w == z % 26 + b else 1
    return z // a * (25 * x + 1) + x * (w + c)

def inverse_alu_func(zprime, a, b, c, mode="max"):
    result = {}
    for w in range(1, 10):
        match = [extra + (a * zprime) for extra in range(0, a)]
        not_match = [extra + (a * (zprime - (w + c))) // 26 for extra in range(0, a)]
        candidates = set([z for z in match + not_match if z > 0])
        for z in candidates:
            if zprime == alu_func(z, w, a, b, c):
                if z in result:
                    result[z] = max(result[z], w) if mode == "max" else min(result[z], w)
                else:
                    result[z] = w
    return result

def table(mode="max"):
    result = {}
    zprimes = [0]
    for digit in range(13, 0, -1):
        group = {}
        for zp in zprimes:
            for z, w in inverse_alu_func(zp, *alu_steps[digit], mode=mode).items():
                if z in group:
                    group[z] = max(group[z], w) if mode == "max" else min(group[z], w)
                else:
                    group[z] = w
        result[digit] = group
        zprimes = group.keys()
    return result

def solve(mode="max"):
    the_table = table(mode=mode)
    candidates = []
    for seed in range(1, 10):
        result = [seed]
        z = 0
        for i, step in enumerate(alu_steps):
            z = alu_func(z, result[-1], *step)
            try:
                result.append(the_table[i+1][z])
            except KeyError:
                break
        if len(result) == 14:
            candidates.append(int("".join([str(x) for x in result])))
    return max(candidates) if mode == "max" else min(candidates)

print("Day 24\n"
      "[Part 1] %d\n"
      "[Part 2] %d"
      %(solve(), solve(mode="min")))
