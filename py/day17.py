#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re

def parse(path):
    with open(path) as f:
        left, right, bottom, top = [int(d) for d in re.findall(r"-?\d+", f.readline())]
        return {'left': left, 'right': right, 'bottom': bottom, 'top': top}

sample_input = parse('../resources/day17_ex.txt')
puzzle_input = parse('../resources/day17.txt')

class Probe:
    def __init__(self, xv, yv):
        self.x       = 0
        self.y       = 0
        self.xv      = xv
        self.yv      = yv
        self.max_y   = 0
        self.success = None

    def step(self):
        self.x     += self.xv
        self.y     += self.yv
        self.max_y = max(self.y, self.max_y)
        self.yv    -= 1
        if self.xv > 0:
            self.xv -= 1

    def launch(self, left, right, bottom, top):
        while self.success is None:
            self.step()
            if (left <= self.x <= right) and (bottom <= self.y <= top):
                self.success = True
            elif (self.y < bottom):
                self.success = False
            elif (self.x < left) or (self.y > top):
                pass
            else:
                self.success = False

def solve(left, right, bottom, top):
    max_y, count = 0, 0
    for xv in range(1, right + 1):
        for yv in range(bottom, (bottom * -1) + 1):
            probe = Probe(xv, yv)
            probe.launch(left, right, bottom, top)
            if probe.success == True:
                max_y = max(max_y, probe.max_y)
                count += 1
    return (max_y, count)

print("Day 17\n"
      "[Part 1] Example: {example[0]}, Puzzle: {solution[0]}\n"
      "[Part 2] Example: {example[1]}, Puzzle: {solution[1]}".format
      (example=solve(**sample_input), solution=solve(**puzzle_input)))
