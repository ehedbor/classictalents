import difflib
import re
from typing import List


def _unique(seq):
    already_seen = set()
    return [x for x in seq if not (x in already_seen or already_seen.add(x))]


def _split_text(text: str) -> List[str]:
    text = re.sub(' +', ' ', text)
    words = []

    token = ''
    last_was_space = False
    for i, char in enumerate(text):
        if char == ' ':
            if not last_was_space:
                last_was_space = True
                words.append(token)
                token = char
            else:
                token += char
        elif char == '%':
            last_was_space = False
            words.append(token)
            words.append(char)
            token = ''
        else:
            if last_was_space:
                last_was_space = False
                words.append(token)
                token = char
            else:
                token += char

    if len(token) > 0:
        words.append(token)

    return words


def _get_common_words(words):
    # determine the base string first
    base_words = words[0].copy()
    no_removals_in_a_row = 0
    i = 0
    while no_removals_in_a_row < len(words):
        i = (i + 1) % len(words)
        no_removals_in_a_row += 1

        removals = list()
        pos = 0
        for diff in difflib.ndiff(base_words, words[i]):
            if diff[0] in ['?', '+']:
                continue
            if diff[0] == '-':
                removals.append(pos)
                no_removals_in_a_row = 0
            pos += 1

        for pos in reversed(_unique(removals)):
            del base_words[pos]

    return base_words


def _get_additions(words, common_words):
    # at first, find the additions
    additions = {}
    for text_idx, words in enumerate(words):
        for pos, diff in enumerate(difflib.ndiff(common_words, words)):
            op = diff[0]
            substr = diff[2:]
            if op == '+':
                if pos not in additions.keys():
                    additions[pos] = {}
                additions[pos][text_idx] = substr

    # merge neighboring additions
    additions_list = list(additions.items())

    i = 0
    while i < len(additions_list) - 1:
        pos, changes = additions_list[i]
        next_pos, next_changes = additions_list[i + 1]

        base_i = i
        base_pos = pos
        while i < len(additions_list) - 1 and next_pos == pos + 1:
            if any([(None if not c else c[-1]) == '.' for c in changes.values()]):
                break

            combined_changes = {}
            for j in range(len(words)):
                combined_changes[j] = changes.get(j, '') + next_changes.get(j, '')

            pos = next_pos
            changes = combined_changes
            i += 1
            if i < len(additions_list) - 1:
                next_pos, next_changes = additions_list[i + 1]

        for _ in range(base_i, i):
            del additions_list[base_i]

        additions_list[base_i] = (base_pos, changes)
        i += 1

    return additions_list


def _get_merged_words(number_of_options, common_words, additions):
    common_words = common_words.copy()
    for (pos, changes) in additions:
        format_str = "{:|" if len(additions) == 1 else "{0:|"
        for index in range(number_of_options):
            change = changes.get(index, "")
            format_str += change.replace(',', '\\,')
            if index != number_of_options - 1:
                format_str += ","
        format_str += "}"

        common_words.insert(pos, format_str)

    return common_words


def merge_talent_desc(text: List[str]) -> str:
    words = [_split_text(t) for t in text]
    common_words = _get_common_words(words)
    additions = _get_additions(words, common_words)
    merged = _get_merged_words(len(words), common_words, additions)
    return ''.join(merged)
