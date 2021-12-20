#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re

lut = {"#": 1, ".": 0}

def parse(path):
    algo, image = [], []
    with open(path) as f:
        for line in f.readlines():
            if len(line.strip()) == 512:
                algo = [lut[c] for c in re.findall(r"\S", line)]
            elif line.strip():
                image.append([lut[c] for c in line.strip()])
    return {'algo': algo, 'image': image}

sample_input = parse('../resources/day20_ex.txt')
puzzle_input = parse('../resources/day20.txt')

def square(y, x):
    return [(y-1,x-1), (y-1,x), (y-1,x+1),
            (y,x-1),    (y,x),    (y,x+1),
            (y+1,x-1), (y+1,x), (y+1,x+1)]

def get_index(image, y, x, dummy=0):
    height, width = len(image), len(image[0])
    bits = [image[y][x] if 0 <= y < height and 0 <= x < width else dummy
            for (y, x) in square(y, x)]
    return int("".join([str(b) for b in bits]), 2)

def solve(iterations, algo, image):
    for i in range(iterations):
        dummy = 0 if algo[0] == 0 else i % 2
        height, width = len(image), len(image[0])
        image = [[algo[get_index(image, y, x, dummy)] for x in range(-1, width+1)]
                                                      for y in range(-1, height+1)]
    return sum([sum([1 if x == 1 else 0 for x in row]) for row in image])

print("Day 15\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(solve(2, **sample_input), solve(2, **puzzle_input),
        solve(50, **sample_input), solve(50, **puzzle_input)))
