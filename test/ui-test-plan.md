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
- Test cases run **in the order listed**, and the session stops at the first
  failure.

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

### TC-03 Add unrecognised input as a plain task

**Aim:** Verify that input matching no known command is stored as a plain task
and acknowledged with `Added: <text>`.

**Input**

```text
borrow book
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Added: borrow book
=======================================================
{{FAREWELL}}
```

### TC-04 List numbers tasks from one

**Aim:** Verify that `list` numbers tasks starting at 1, in the order they were
added, and shows each task as not done.

**Input**

```text
borrow book
return book
list
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Added: borrow book
=======================================================
=======================================================
Added: return book
=======================================================
=======================================================
1. [ ] borrow book
2. [ ] return book
=======================================================
{{FAREWELL}}
```

### TC-05 Mark and unmark a task

**Aim:** Verify that `mark` sets the task's status icon to `X` and `unmark`
clears it, that the change is reflected in a later `list`, and that the
one-based index in the command selects the right task.

**Input**

```text
borrow book
return book
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
Added: borrow book
=======================================================
=======================================================
Added: return book
=======================================================
=======================================================
Nice! I've marked this task as done
  [X] return book
=======================================================
=======================================================
1. [ ] borrow book
2. [X] return book
=======================================================
=======================================================
Nice! I've marked this task as not done yet
  [ ] return book
=======================================================
=======================================================
1. [ ] borrow book
2. [ ] return book
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
Now you have 1 tasks in the list
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
Now you have 1 tasks in the list
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
Now you have 1 tasks in the list
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
Now you have 1 tasks in the list
=======================================================
=======================================================
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list
=======================================================
=======================================================
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list
=======================================================
=======================================================
1. [T][ ] read book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
=======================================================
{{FAREWELL}}
```
