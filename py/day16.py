#!/usr/bin/env python3
# -*- coding:utf-8 -*-

from math import prod

lut = {'0': "0000", '1': "0001", '2': "0010", '3': "0011", '4': "0100",
       '5': "0101", '6': "0110", '7': "0111", '8': "1000", '9': "1001",
       'A': "1010", 'B': "1011", 'C': "1100", 'D': "1101", 'E': "1110",
       'F': "1111"}

def hex_to_bin(hex):
    return "".join([lut[char] for char in hex])

with open('../resources/day16_ex.txt') as f:
    examples = [hex_to_bin(line.strip()) for line in f.readlines()]

with open('../resources/day16.txt') as f:
    puzzle_input = hex_to_bin(f.readline().strip())

class Packet:
    def __init__(self, data, cursor):
        self.version        = int(data[cursor:cursor+3], 2)
        self.packet_type    = int(data[cursor+3:cursor+6], 2)
        self.end            = None
        self.packets        = []
        self.target_end     = None
        self.target_packets = None
        self.value          = None
        if self.packet_type == 4:
            self.cursor = cursor + 6
            return None
        length_type = int(data[cursor+6])
        if length_type == 0:
            self.cursor       = cursor + 22
            self.target_end   = self.cursor + int(data[cursor+7:cursor+22], 2)
        elif length_type == 1:
            self.cursor          = cursor + 18
            self.target_packets  = int(data[cursor+7:cursor+18], 2)

    def __repr__(self):
        result = {    'version': self.version,
                  'packet type': self.packet_type}
        if self.value:
            result['value'] = self.value
        if self.packets:
            result['packets'] = self.packets
        return repr(result)

    def get_literal_value(self, data):
        assert self.packet_type == 4
        cursor, bytes = self.cursor, ""
        while True:
            head = int(data[cursor])
            bytes += data[cursor+1:cursor+5]
            if head == 0:
                self.value = int(bytes, 2)
                self.end = cursor + 5
                break
            cursor += 5

    def evaluate(self):
        assert self.packets and not self.value
        values = [p.value for p in self.packets]
        operations = {
            0: sum(values),
            1: prod(values),
            2: min(values),
            3: max(values)
        }
        if len(values) >= 2:
            operations.update({
                5: 1 if values[0] > values[1] else 0,
                6: 1 if values[0] < values[1] else 0,
                7: 1 if values[0] == values[1] else 0
            })
        self.value = operations[self.packet_type]


def get_packets(data, top_level=False, cursor=0, target_packets=None, target_end=None):
    packets = []
    while True:
        if (target_packets == len(packets)) or (cursor == target_end):
            return packets if top_level else (packets, cursor)

        try: # Creation of new packet will fail if not enough bits left
            current = Packet(data, cursor)
        except:
            return packets if top_level else (packets, cursor)

        if current.packet_type == 4:
            current.get_literal_value(data)
            packets.append(current)
            cursor = current.end
            continue

        sub_packets, cursor = get_packets(data, cursor=current.cursor,
                                          target_packets=current.target_packets,
                                          target_end=current.target_end)
        current.packets.extend(sub_packets)
        current.evaluate()
        packets.append(current)

def part_1(data):
    packets = get_packets(data, top_level=True)
    version_sum = 0
    while packets:
        new_packets = []
        for packet in packets:
            version_sum += packet.version
            new_packets.extend(packet.packets)
        packets = new_packets
    return version_sum

def part_2(data):
    return get_packets(data, top_level=True)[0].value

print("Day 16\n"
      "[Part 1] Examples: {p1e}, Puzzle: {p1s}\n"
      "[Part 2] Examples: {p2e}, Puzzle: {p2s}".format(
        p1e=[part_1(x) for x in examples],
        p1s=part_1(puzzle_input),
        p2e=[part_2(x) for x in examples],
        p2s=part_2(puzzle_input)))
