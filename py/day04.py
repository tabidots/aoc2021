#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import re
from itertools import chain

# https://stackoverflow.com/questions/6560354/how-would-i-create-a-custom-list-class-in-python
class Board(list):
    def __init__(self, *args, **kwargs):
        super(Board, self).__init__(args[0])

    def cols(self):
        return [[row[i] for row in self] for i in range(len(self[0]))]

    def has_bingo(self, called):
        if any([all([x in called for x in row]) for row in self]) or \
           any([all([x in called for x in col]) for col in self.cols()]):
           return True
        return False

    def score(self, called):
        uncalled = [x for x in chain.from_iterable(self) if x not in called]
        return sum(uncalled) * called[-1]


def parse(path):
    calls, boards = [], []
    cur_board = Board([])
    with open(path) as f:
        for line in f.readlines():
            if ',' in line:
                call_list = [int(x) for x in line.split(',')]
            elif re.search(r'\d+', line):
                cur_board.append([int(x) for x in re.findall(r'\d+', line)])
                if len(cur_board) == 5:
                    boards.append(cur_board)
                    cur_board = Board([])
    return {'call_list': call_list, 'boards': boards}


sample_input = parse('../resources/day04_ex.txt')
puzzle_input = parse('../resources/day04.txt')


def part_1(data):
    called = []
    for call in data['call_list']:
        called.append(call)
        for board in data['boards']:
            if board.has_bingo(called):
                return board.score(called)
    return None


def part_2(data):
    called, winning_scores, remaining = [], [], data['boards']
    for call in data['call_list']:
        called.append(call)
        winning_scores.extend([board.score(called) for board in remaining if board.has_bingo(called)])
        remaining = [board for board in remaining if not board.has_bingo(called)]
    return winning_scores[-1]


print("Day 4\n"
      "[Part 1] Example: %d, Puzzle: %d\n"
      "[Part 2] Example: %d, Puzzle: %d"
      %(part_1(sample_input), part_1(puzzle_input),
        part_2(sample_input), part_2(puzzle_input)))
