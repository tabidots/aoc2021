#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re
from math import ceil
from queue import Queue

def parse(path):
    with open(path) as f:
        return [line.strip() for line in f.readlines()]

example_1 = parse('../resources/day18_ex.txt')
example_2 = parse('../resources/day18_ex2.txt')
puzzle_input = parse('../resources/day18.txt')

def snailfish_add(a, b):
    if isinstance(a, list):
        a = str(a)
    if isinstance(b, list):
        b = str(b)
    sum = "".join(["[", a, ",", b, "]"])
    return snailfish_reduce(sum)

def snailfish_reduce(num):
    if isinstance(num, list):
        num = str(a)

    while True:
        pair_to_explode, pair_start, pair_end = None, 0, 0
        level = 0

        for i in range(len(num)):
            if num[i] == "[":
                if level == 4:
                    pair_to_explode = re.match(r"\[(\d+),\s?(\d+)\]", num[i:])
                    if pair_to_explode:
                        pair_start = i
                        inner_left, inner_right = [int(x) for x in pair_to_explode.group(1,2)]
                        pair_end = pair_start + pair_to_explode.end()
                        break
                level += 1
            elif num[i] == "]":
                level -= 1

        if pair_to_explode:
            outer_left = [m for m in re.finditer(r"\d+", num[:pair_start])]
            outer_right = [m for m in re.finditer(r"\d+", num[pair_end:])]
            if outer_right:
                target = outer_right[0]
                digit = int(target.group())
                num = num[:pair_end + target.start()] + str(inner_right + digit) + num[pair_end + target.end():]
            num = num[:pair_start] + "0" + num[pair_end:]
            if outer_left:
                target = outer_left[-1]
                digit = int(target.group())
                num = num[:target.start()] + str(digit + inner_left) + num[target.end():]
            continue

        numbers_to_split = [m for m in re.finditer(r"\d{2,}", num)]
        if numbers_to_split:
            target = numbers_to_split[0]
            digit = int(target.group())
            left, right = [str(x) for x in [digit // 2, int(ceil(digit / 2.0))]]
            num = num[:target.start()] + "[" + left + "," + right + "]" + num[target.end():]
        else:
            return num


class Node:
    def __init__(self, this, parent):
        self.parent    = parent
        self.magnitude = this if isinstance(this, int) else None
        self.children  = []
        if isinstance(this, list):
            for i, child in enumerate(this):
                self.children.append(Node(child, self))

    def is_pair(self):
        return True if self.children and len(self.children) == 2 and \
                       all([c.magnitude is not None for c in self.children]) else False

def get_magnitude(left, right):
    return 3 * left + 2 * right

def part_1(homework):
    solution = reduce(lambda a,b: snailfish_add(a, b), homework)
    tree = Node(eval(solution), None)
    node = tree
    while not tree.magnitude:
        if node.is_pair():
            node.magnitude = get_magnitude(*[c.magnitude for c in node.children])
            old_node = node
            node = node.parent
        elif node.children:
            for c in node.children:
                if c.magnitude is None:
                    node = c
    return tree.magnitude

def part_2(homework):
    max_magnitude = 0
    for left in homework:
        for right in homework:
            if left != right:
                max_magnitude = max(max_magnitude, part_1([left, right]))
    return max_magnitude

print("Day 18\n"
      "[Part 1] Examples: %d, %d; Puzzle: %d\n"
      "[Part 2] Examples: %d, %d; Puzzle: %d"
      %(part_1(example_1), part_1(example_2), part_1(puzzle_input),
        part_2(example_1), part_2(example_2), part_2(puzzle_input)))
