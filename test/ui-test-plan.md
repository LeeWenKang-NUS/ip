# UI test plan

Manual-style tests for the `Auto` command line interface, driven automatically by
the `test-ui` skill. Each test case starts a **fresh** instance of the program,
feeds its input lines to standard input, and compares the console output with
the expected output.

Run them with:

```bash
python .claude/skills/test-ui/scripts/run-ui-tests.py
```

## Conventions

- **Every test case must end with `bye`.** The program only leaves its input
  loop on that command; without it the run hangs until the harness times out.
- **Test cases share no state.** The task list lives in memory only, so a case
  that needs existing tasks must add them itself as part of its input.
- **Expected output is the whole console session**, including the greeting and
  the farewell. Those two fixed blocks are written once under
  *Common blocks* below and referenced as `{{GREETING}}` and `{{FAREWELL}}`, so
  a change to the banner only has to be made in one place.
- **Comparison ignores cosmetic whitespace.** Line endings are normalised, and
  trailing spaces on each line and blank lines at the very start and end are
  ignored. Everything else must match exactly.
- Test cases run **in the order listed, and every one of them runs** even if an
  earlier case failed. Failures are reported together at the end, so one broken
  behaviour cannot hide the state of the rest of the plan.
- **Expected output describes desired behaviour, not current behaviour.** A
  case that crashes the program is a failing case, never a passing one. The
  cases under *Invalid input handling, not implemented yet* are written this way
  on purpose and are expected to fail until the code catches up.
- **Avoid depending on trailing whitespace in an input block.** It is invisible
  in an editor and a formatter may strip it, silently turning the input into a
  different command. Use interior spaces where padding needs testing.

## Common blocks

### {{GREETING}}

```text
=======================================================
    _         _
   / \  _   _| |_ ___
  / _ \| | | | __/ _ \
 / ___ \ |_| | || (_) |
/_/   \_\__,_|\__\___/

Hello! I'm Auto, your personal assistant.
What can I do for you?
=======================================================
```

### {{FAREWELL}}

```text
=======================================================
Bye. Hope to see you again soon!
=======================================================
```

## Test cases

### TC-01 Greet and exit

**Aim:** Verify that the program shows the greeting on startup and exits cleanly
when given `bye`, with no other output.

**Input**

```text
bye
```

**Expected output**

```text
{{GREETING}}
{{FAREWELL}}
```

### TC-02 List an empty task list

**Aim:** Verify that `list` on a fresh session prints only the divider lines,
with no task entries and no error.

**Input**

```text
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
=======================================================
{{FAREWELL}}
```

### TC-03 Reject unrecognised input without storing it

**Aim:** Verify that input matching no known command is refused with an error
message and **not** added to the task list. The `list` afterwards proves nothing
was stored.

**Input**

```text
borrow book
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
=======================================================
=======================================================
{{FAREWELL}}
```

### TC-04 List numbers tasks from one

**Aim:** Verify that `list` numbers tasks starting at 1, in the order they were
added, and shows each task as not done.

**Input**

```text
todo borrow book
todo return book
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
=======================================================
=======================================================
1. [T][ ] borrow book
2. [T][ ] return book
=======================================================
{{FAREWELL}}
```

### TC-05 Mark and unmark a task

**Aim:** Verify that `mark` sets the task's status icon to `X` and `unmark`
clears it, that the change is reflected in a later `list`, and that the
one-based index in the command selects the right task.

**Input**

```text
todo borrow book
todo return book
mark 2
list
unmark 2
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
=======================================================
=======================================================
Nice! I've marked this task as done
  [T][X] return book
=======================================================
=======================================================
1. [T][ ] borrow book
2. [T][X] return book
=======================================================
=======================================================
Nice! I've marked this task as not done yet
  [T][ ] return book
=======================================================
=======================================================
1. [T][ ] borrow book
2. [T][ ] return book
=======================================================
{{FAREWELL}}
```

### TC-06 Add a todo

**Aim:** Verify that `todo` creates a task tagged `[T]`, that the acknowledgement
shows the task in its full rendered form rather than the raw text, and that the
command word is not stored as part of the task name.

**Input**

```text
todo read book
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
{{FAREWELL}}
```

### TC-07 Add a deadline

**Aim:** Verify that `deadline <description> /by <date>` creates a task tagged
`[D]`, splits the description from the date at `/by`, and renders the date as
`(by: <date>)`.

**Input**

```text
deadline return book /by Sunday
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
=======================================================
=======================================================
1. [D][ ] return book (by: Sunday)
=======================================================
{{FAREWELL}}
```

### TC-08 Add an event

**Aim:** Verify that `event <description> /from <start> /to <end>` creates a task
tagged `[E]`, splits the description and both times at the two separators, and
renders them as `(from: <start> to: <end>)`.

**Input**

```text
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
=======================================================
=======================================================
1. [E][ ] project meeting (from: Mon 2pm to: 4pm)
=======================================================
{{FAREWELL}}
```

### TC-09 Mix task types and count them

**Aim:** Verify that the running count in the acknowledgement grows across
different task types, and that a `list` renders each type with its own tag.

**Input**

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
=======================================================
=======================================================
1. [T][ ] read book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
=======================================================
{{FAREWELL}}
```

### TC-10 A command word with no argument is rejected

**Aim:** Verify what happens when a known command word is typed without its
argument. `mark` does not match the `mark ` prefix, so it reaches the catch-all
branch and is refused. Interleaving `list` before and after shows the list is
unchanged, and the following `todo` shows the task count was not inflated.

**Input**

```text
todo read book
list
mark
list
todo write essay
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
=======================================================
Got it. I've added this task:
  [T][ ] write essay
Now you have 2 tasks in the list.
=======================================================
=======================================================
1. [T][ ] read book
2. [T][ ] write essay
=======================================================
{{FAREWELL}}
```

### TC-11 Command words are case sensitive

**Aim:** Verify that command matching is exact and case sensitive, so `List`
does not list and `BYE` does not exit. Both are refused as unrecognised input
and the session continues until the lowercase `bye`.

**Input**

```text
todo read book
List
BYE
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
{{FAREWELL}}
```

### TC-12 An empty input line is rejected

**Aim:** Verify that pressing Enter on an empty line is treated as unrecognised
input and refused, rather than creating a task with a blank description. The
surrounding `todo` and `list` show the real task is untouched.

**Input**

```text
todo read book

list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
{{FAREWELL}}
```

### TC-13 Marking twice and unmarking twice are idempotent

**Aim:** Verify that repeating `mark` on an already done task, and `unmark` on
an already not-done task, keeps the status correct rather than toggling it. Each
repeat prints the same acknowledgement, and the interleaved `list` confirms the
stored status matches what was printed.

**Input**

```text
todo read book
mark 1
mark 1
list
unmark 1
unmark 1
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Nice! I've marked this task as done
  [T][X] read book
=======================================================
=======================================================
Nice! I've marked this task as done
  [T][X] read book
=======================================================
=======================================================
1. [T][X] read book
=======================================================
=======================================================
Nice! I've marked this task as not done yet
  [T][ ] read book
=======================================================
=======================================================
Nice! I've marked this task as not done yet
  [T][ ] read book
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
{{FAREWELL}}
```

### TC-14 Surplus spaces are kept in a todo but trimmed in a deadline

**Aim:** Verify the inconsistent whitespace handling between the two commands:
`todo` stores everything after `todo ` verbatim, including leading spaces, while
`deadline` trims both halves around `/by`. This is current behaviour, not a
deliberate design.

**Input**

```text
todo    read book
deadline  return book  /by   Sunday
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ]    read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
=======================================================
=======================================================
1. [T][ ]    read book
2. [D][ ] return book (by: Sunday)
=======================================================
{{FAREWELL}}
```

### TC-15 A rejected command leaves the indices untouched

**Aim:** Verify that a mistyped command does not disturb the task list, so
index-based commands keep addressing the task the user means. The mistyped
`unmark` between the two `todo` commands takes up no position, so the second
task is at index 2 and `mark 2` selects it. This is the regression guard for the
old catch-all behaviour, where the junk entry pushed it to index 3.

**Input**

```text
todo read book
unmark
todo write essay
mark 2
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
=======================================================
Got it. I've added this task:
  [T][ ] write essay
Now you have 2 tasks in the list.
=======================================================
=======================================================
Nice! I've marked this task as done
  [T][X] write essay
=======================================================
=======================================================
1. [T][ ] read book
2. [T][X] write essay
=======================================================
{{FAREWELL}}
```

### TC-16 Anything after bye does not exit

**Aim:** Verify that exiting requires the input to equal `bye` exactly, so
`bye now` is refused as unrecognised and the loop continues until a real `bye`
arrives. A trailing space after `bye` behaves the same way but is not tested
here, because trailing whitespace in an input block is invisible and easily
stripped.

**Input**

```text
bye now
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
{{FAREWELL}}
```

## Invalid input handling, not implemented yet

**These cases describe what `Auto` should do, not what it does now.** Today an
invalid task number or a missing `/by`, `/from` or `/to` throws an uncaught
exception and kills the program, so **every case in this section fails**, and
the runner reports it as a failure rather than passing quietly on a crash.

They are the specification for the next change: make each of these commands
report the problem and carry on. Each case ends with `list` and `bye` precisely
to prove the program is still alive afterwards and that nothing half-built was
added to the task list.

When the error handling is in place, this section passes and moves up into
*Test cases*.

### TC-17 Reject a task number of zero

**Aim:** Verify the off-by-one boundary is caught: `mark 0` is below the first
valid index, so it should be reported rather than turned into list index -1.
The task list must be unchanged afterwards.

**Input**

```text
todo read book
mark 0
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Ohhh Noooo... there is no task 0!
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
{{FAREWELL}}
```

### TC-18 Reject a task number past the end of the list

**Aim:** Verify that an index above the task count is reported too, naming the
number the user asked for, and that the existing task is untouched.

**Input**

```text
todo read book
mark 99
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Ohhh Noooo... there is no task 99!
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
{{FAREWELL}}
```

### TC-19 Reject a task number that is not a number

**Aim:** Verify that a non-numeric argument is reported as such rather than
parsed blindly. `unmark` shares this code path and needs the same guard.

**Input**

```text
todo read book
mark abc
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
=======================================================
Ohhh Noooo... 'abc' is not a task number!
=======================================================
=======================================================
1. [T][ ] read book
=======================================================
{{FAREWELL}}
```

### TC-20 Reject marking a task when the list is empty

**Aim:** Verify that `mark` in a session with no tasks is reported like any
other out-of-range number, instead of dying before the user can type anything
else.

**Input**

```text
mark 1
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... there is no task 1!
=======================================================
=======================================================
=======================================================
{{FAREWELL}}
```

### TC-21 Reject a deadline with no /by

**Aim:** Verify that a `deadline` missing its `/by` separator is reported and
that no half-built task is added to the list.

**Input**

```text
deadline return book
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... a deadline needs a /by date!
=======================================================
=======================================================
=======================================================
{{FAREWELL}}
```

### TC-22 Reject an event with no /to

**Aim:** Verify that an `event` with a `/from` but no `/to` is reported, since
the end time is not optional, and that nothing is added.

**Input**

```text
event project meeting /from Mon 2pm
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... an event needs a /from and a /to time!
=======================================================
=======================================================
=======================================================
{{FAREWELL}}
```

### TC-23 Reject an event with no /from

**Aim:** Verify that an `event` missing both separators is reported with the
same message as a missing `/to`, so neither separator is optional.

**Input**

```text
event project meeting
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... an event needs a /from and a /to time!
=======================================================
=======================================================
=======================================================
{{FAREWELL}}
```
