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
- **Test cases share no state.** The runner removes `data/test_auto.txt` before each
  fresh process, so a case that needs existing tasks must add them itself as
  part of its input.
- A case can provide an optional **Initial data** block to populate
  `data/test_auto.txt` before the program starts.
- A case can override the Java storage property with a **Data path:** line to
  exercise path-level failures without touching production data.
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

**Aim:** Verify that `list` on a fresh session prints its header and nothing
else, with no task entries and no error. The header is unconditional, so an
empty list is the header alone between the dividers.

**Input**

```text
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
1. [T][ ] borrow book
2. [T][X] return book
=======================================================
=======================================================
Nice! I've marked this task as not done yet
  [T][ ] return book
=======================================================
=======================================================
Here are the tasks in your list:
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
Here are the tasks in your list:
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
deadline return book /by 08/06/2026
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [D][ ] return book (by: Jun 08 2026)
Now you have 1 tasks in the list.
=======================================================
=======================================================
Here are the tasks in your list:
1. [D][ ] return book (by: Jun 08 2026)
=======================================================
{{FAREWELL}}
```

### TC-08 Add an event

**Aim:** Verify that `event <description> /from <start> /to <end>` creates a task
tagged `[E]`, splits the description and both times at the two separators, and
renders them as `(from: <start> to: <end>)`.

**Input**

```text
event project meeting /from 06/08/2026 /to 07/08/2026
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
Now you have 1 tasks in the list.
=======================================================
=======================================================
Here are the tasks in your list:
1. [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
=======================================================
{{FAREWELL}}
```

### TC-09 Mix task types and count them

**Aim:** Verify that the running count in the acknowledgement grows across
different task types, and that a `list` renders each type with its own tag.

**Input**

```text
todo read book
deadline return book /by 08/06/2026
event project meeting /from 06/08/2026 /to 07/08/2026
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
  [D][ ] return book (by: Jun 08 2026)
Now you have 2 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
Now you have 3 tasks in the list.
=======================================================
=======================================================
Here are the tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Jun 08 2026)
3. [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
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
Here are the tasks in your list:
1. [T][ ] read book
=======================================================
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
=======================================================
Here are the tasks in your list:
1. [T][ ] read book
=======================================================
=======================================================
Got it. I've added this task:
  [T][ ] write essay
Now you have 2 tasks in the list.
=======================================================
=======================================================
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
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
deadline  return book  /by   08/06/2026
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
  [D][ ] return book (by: Jun 08 2026)
Now you have 2 tasks in the list.
=======================================================
=======================================================
Here are the tasks in your list:
1. [T][ ]    read book
2. [D][ ] return book (by: Jun 08 2026)
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
Here are the tasks in your list:
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

### TC-24 Delete removes a task and renumbers the ones after it

**Aim:** Verify that `delete` takes the task out of the list, reports it with the
running count already decremented, and that the tasks after it move up so a
later `list` numbers them contiguously from 1. The deleted task is marked done
first, so the acknowledgement shows a status the task genuinely had.

**Input**

```text
todo read book
todo write essay
todo file report
mark 2
delete 2
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
  [T][ ] write essay
Now you have 2 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [T][ ] file report
Now you have 3 tasks in the list.
=======================================================
=======================================================
Nice! I've marked this task as done
  [T][X] write essay
=======================================================
=======================================================
Roger! I've deleted this task:
  [T][X] write essay
Now you have 2 tasks in the list.
=======================================================
=======================================================
Here are the tasks in your list:
1. [T][ ] read book
2. [T][ ] file report
=======================================================
{{FAREWELL}}
```

### TC-25 Deleting the only task empties the list

**Aim:** Verify the lower boundary of the count: deleting the last remaining
task reports `0 tasks` and leaves a list that prints its header and no entries,
rather than going negative or leaving a stale entry behind.

**Input**

```text
todo read book
mark 1
delete 1
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
Roger! I've deleted this task:
  [T][X] read book
Now you have 0 tasks in the list.
=======================================================
=======================================================
Here are the tasks in your list:
=======================================================
{{FAREWELL}}
```

### TC-26 Delete rejects a bad task number without touching the list

**Aim:** Verify that `delete` guards its argument exactly as `mark` does — zero,
past the end, and non-numeric are each reported with their own message, and
`delete` with no argument at all is unrecognised rather than a delete of
nothing. The closing `list` proves none of the four rejections removed anything.

**Input**

```text
todo read book
delete 0
delete 9
delete abc
delete
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
Ohhh Noooo... there is no task 9!
=======================================================
=======================================================
Ohhh Noooo... 'abc' is not a task number!
=======================================================
=======================================================
Ohhh Noooo... I don't understand you!
=======================================================
=======================================================
Here are the tasks in your list:
1. [T][ ] read book
=======================================================
{{FAREWELL}}
```

### TC-27 Deleting an unfinished task does not report it as done

**Aim:** Verify that the acknowledgement shows the task's real status, so a task
that was never marked is shown as not done when it is deleted.

**Input**

```text
todo read book
delete 1
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
Roger! I've deleted this task:
  [T][ ] read book
Now you have 0 tasks in the list.
=======================================================
=======================================================
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
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
Here are the tasks in your list:
=======================================================
{{FAREWELL}}
```

### TC-22 Reject an event with no /to

**Aim:** Verify that an `event` with a `/from` but no `/to` is reported, since
the end time is not optional, and that nothing is added.

**Input**

```text
event project meeting /from 06/08/2026
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
Here are the tasks in your list:
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
Here are the tasks in your list:
=======================================================
{{FAREWELL}}
```

### TC-29 Load tasks from the data file

**Aim:** Verify that startup restores todo, deadline, and event tasks together
with their saved completion statuses from `data/test_auto.txt`.

**Initial data**

```text
T | 1 | cmVhZCBib29r
D | 0 | cmV0dXJuIGJvb2s= | SnVuIDA2IDIwMjY=
E | 1 | cHJvamVjdCBtZWV0aW5n | QXVnIDA2IDIwMjY= | QXVnIDA3IDIwMjY=
```

**Input**

```text
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Jun 06 2026)
3. [E][X] project meeting (from: Aug 06 2026 to: Aug 07 2026)
=======================================================
{{FAREWELL}}
```

### TC-28 Save after every task-list change

**Aim:** Exercise adding each task type, marking, unmarking, and deleting so the
resulting task list can be verified in `data/test_auto.txt` after the session.

**Input**

```text
todo read book
deadline return book /by 06/06/2026
event project meeting /from 06/08/2026 /to 07/08/2026
mark 1
unmark 1
delete 2
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
  [D][ ] return book (by: Jun 06 2026)
Now you have 2 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
Now you have 3 tasks in the list.
=======================================================
=======================================================
Nice! I've marked this task as done
  [T][X] read book
=======================================================
=======================================================
Nice! I've marked this task as not done yet
  [T][ ] read book
=======================================================
=======================================================
Roger! I've deleted this task:
  [D][ ] return book (by: Jun 06 2026)
Now you have 2 tasks in the list.
=======================================================
{{FAREWELL}}
```

### TC-30 Recover valid tasks from a partially corrupted file

**Aim:** Verify that blank and malformed records are skipped with one warning,
while valid durable-format records retain separators, Unicode, and status.

**Initial data**

```text
T | 1 | cmVhZCB8IGJvb2sgKGJ5OiBsYXRlcik=
not a task

D | 0 | cmV0dXJuIGJvb2s= | SnVuIDA2IDIwMjY=
E | 2 | 6aG555uuIG1lZXRpbmc= | QXVnIDA2IDIwMjY= | QXVnIDA3IDIwMjY=
Z | 0 | dW5rbm93bg==
```

**Input**

```text
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Warning: 3 invalid data line(s) were skipped: line 2 is invalid and was skipped, line 5 is invalid and was skipped, line 6 is invalid and was skipped.
=======================================================
=======================================================
Here are the tasks in your list:
1. [T][X] read | book (by: later)
2. [D][ ] return book (by: Jun 06 2026)
=======================================================
{{FAREWELL}}
```

### TC-31 Recover from load and save path failures

**Aim:** Verify that a directory used as the data file causes friendly load and
save errors, rolls back the attempted addition, and prints no stack trace.

**Data path:** `data`

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
Sorry, I couldn't load your saved tasks. Starting with an empty task list.
=======================================================
=======================================================
Sorry, I couldn't save your tasks. Your latest change was not applied.
=======================================================
=======================================================
Here are the tasks in your list:
=======================================================
{{FAREWELL}}
```

### TC-32 Reject a deadline date in the wrong format

**Aim:** Verify that deadline dates must use `dd/MM/yyyy`, and that a rejected
deadline is not added to the task list.

**Input**

```text
deadline return book /by 2026-06-06
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... '2026-06-06' is not a valid date! Use dd/MM/yyyy.
=======================================================
=======================================================
Here are the tasks in your list:
=======================================================
{{FAREWELL}}
```

### TC-33 Reject an impossible event date

**Aim:** Verify that strict date parsing rejects impossible calendar dates and
does not add a partially parsed event.

**Input**

```text
event project meeting /from 30/02/2026 /to 01/03/2026
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... '30/02/2026' is not a valid date! Use dd/MM/yyyy.
=======================================================
=======================================================
Here are the tasks in your list:
=======================================================
{{FAREWELL}}
```

### TC-34 Find tasks occurring on a date

**Aim:** Verify that `occur <date>` shows deadlines due exactly on the date and
events whose inclusive date range contains it, excludes todos and non-matches,
and retains the tasks' original list numbers.

**Input**

```text
todo prepare notes
deadline submit report /by 08/06/2026
event conference /from 07/06/2026 /to 09/06/2026
deadline return book /by 10/06/2026
occur 08/06/2026
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] prepare notes
Now you have 1 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [D][ ] submit report (by: Jun 08 2026)
Now you have 2 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [E][ ] conference (from: Jun 07 2026 to: Jun 09 2026)
Now you have 3 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [D][ ] return book (by: Jun 10 2026)
Now you have 4 tasks in the list.
=======================================================
=======================================================
Here are the tasks occurring on Jun 08 2026:
2. [D][ ] submit report (by: Jun 08 2026)
3. [E][ ] conference (from: Jun 07 2026 to: Jun 09 2026)
=======================================================
{{FAREWELL}}
```

### TC-35 Find no tasks occurring on a date

**Aim:** Verify that `occur` prints an empty result cleanly when no deadline or
event occurs on the requested date.

**Input**

```text
todo prepare notes
deadline submit report /by 08/06/2026
occur 09/06/2026
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] prepare notes
Now you have 1 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [D][ ] submit report (by: Jun 08 2026)
Now you have 2 tasks in the list.
=======================================================
=======================================================
Here are the tasks occurring on Jun 09 2026:
=======================================================
{{FAREWELL}}
```

### TC-36 Reject an invalid occur date

**Aim:** Verify that `occur` uses the same strict `dd/MM/yyyy` validation as
deadline and event commands and that the program continues after an error.

**Input**

```text
occur 31/02/2026
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... '31/02/2026' is not a valid date! Use dd/MM/yyyy.
=======================================================
{{FAREWELL}}
```

### TC-37 Reject an event whose end precedes its start

**Aim:** Verify that an event with a `/to` date before its `/from` date is
rejected, is not added to the task list, and does not terminate the program.

**Input**

```text
event e /from 22/08/2026 /to 23/08/2021
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... an event's /to date cannot be before its /from date!
=======================================================
=======================================================
Here are the tasks in your list:
=======================================================
{{FAREWELL}}
```

### TC-38 Find tasks by description

**Aim:** Verify that `find <keyword>` performs a case-insensitive substring
search, preserves list order and original task numbers, and excludes non-matches.

**Input**

```text
todo read book
deadline submit essay /by 08/06/2026
event BOOK launch /from 09/06/2026 /to 10/06/2026
find book
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
  [D][ ] submit essay (by: Jun 08 2026)
Now you have 2 tasks in the list.
=======================================================
=======================================================
Got it. I've added this task:
  [E][ ] BOOK launch (from: Jun 09 2026 to: Jun 10 2026)
Now you have 3 tasks in the list.
=======================================================
=======================================================
Here are the matching tasks in your list:
1. [T][ ] read book
3. [E][ ] BOOK launch (from: Jun 09 2026 to: Jun 10 2026)
=======================================================
{{FAREWELL}}
```

### TC-39 Find no matching tasks

**Aim:** Verify that `find` prints an empty result cleanly when no task
description contains the keyword.

**Input**

```text
todo read book
find essay
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
Here are the matching tasks in your list:
=======================================================
{{FAREWELL}}
```

### TC-40 Reject find without a keyword

**Aim:** Verify that `find` requires a non-blank keyword and that the program
continues after reporting the error.

**Input**

```text
find
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Ohhh Noooo... a find command needs a keyword!
=======================================================
{{FAREWELL}}
```
