# JavaFX UI test plan

Manual tests for Auto's JavaFX chat interface. Start a fresh application for
each test unless the test says otherwise.

Run the application with:

```powershell
.\gradlew.bat run
```

## TC-00 Show startup greeting

1. Start the application.

Expected window icons on Windows: The coffee-cup image from
`src/main/resources/images/auto-icon.png` appears in the top-left title bar
and on the running application's taskbar button. Minimize and restore the
window; both icons remain the coffee cup. Repeat after launching the packaged
JAR with `java -jar build/libs/auto.jar` to check that the icon is bundled.

Expected: The first Auto chat bubble immediately shows the coffee-cup application icon above
`Hello! I'm Auto, your personal assistant.`, and `What can I do for you?`.
The icon fits within 96 by 96 pixels, keeps its original proportions, and
has no ASCII banner. Check this greeting in both Gradle and packaged JAR launches.
Saved tasks have already been loaded before this message appears. If the data
file could not be loaded fully, the same welcome bubble also contains the load
warning below the greeting. Deadlines due today and events spanning today
appear below any warning under `Reminders for today (MMM dd yyyy):`.

## TC-01 Send with Enter

1. Type `todo read book` in the input field.
2. Press Enter.

Expected: The chat shows the user command followed by Auto's confirmation that
the todo was added. Each bubble shows its sender on a separately styled line
above the message. The user bubble is aligned right and Auto's bubble is aligned
left. The input field is cleared.

## TC-02 Send with the button

1. Type `list` in the input field.
2. Click **Send**.

Expected: Auto displays the task-list heading and the numbered `read book`
task. The input field is cleared.

## TC-03 Reject blank input

1. Leave the input field empty or enter only spaces.
2. Press Enter or click **Send**.

Expected: No chat messages are added.

## TC-04 Display an invalid command

1. Send `borrow book`.

Expected: Auto displays `Ohhh Noooo... I don't understand you!` and the
application remains usable.

## TC-05 Preserve state between messages

1. Send `todo write essay`.
2. Send `mark 1`.
3. Send `list`.

Expected: The list shows task 1 as completed: `[T][X] write essay`.

## TC-06 Persist state between launches

1. Add a task and close the application.
2. Start the application again.
3. Send `list`.

Expected: The previously added task is restored from `data/auto.txt`.

## TC-07 Keep the newest message visible

1. Send enough commands to fill the chat history beyond the window height.

Expected: The history scrolls automatically so the newest response remains
visible.

Scroll up using the wheel or scrollbar. Expected: A `↓ Latest message` button
appears at the bottom-right of the history. Click it: the history scrolls to
the bottom, the button disappears, and the input field receives focus.
Scroll up again and manually scroll to the bottom: the button disappears.
While scrolled up, send `list`: the history automatically jumps to the latest
reply and the button disappears. Subsequent commands also scroll to their
latest replies, even if you manually scroll up between commands.
With a short history that fits entirely in the window, the button is hidden.
Enlarge the window until the history fits: the button disappears.
The button must also be reachable with Tab and activatable with Space.

## TC-08 Exit response

1. Send `bye`.

Expected: Auto processes the farewell and immediately closes the application
window. The Gradle `run` task also finishes rather than remaining active in the
IDE. The farewell bubble may close before it is visibly rendered.

## TC-09 Show today's reminders on each launch

1. Using today's local date in dd/MM/yyyy format, add a todo, a deadline due
   today, and events starting today, ending today, and spanning today.
2. Add deadlines and events entirely before or after today.
3. Mark the deadline due today as completed, then close and reopen Auto.

Expected: The first welcome bubble includes `Reminders for today (MMM dd yyyy):`
with today's date. It lists the deadline and all three events, using their
original list numbers and normal task formatting, including the deadline's
`[X]` marker. The todo and tasks on other dates do not appear in reminders.
Reopening the application displays the reminders again without changing tasks.

## TC-10 Omit reminders when nothing occurs today

1. Start with an empty saved task list, or only todos and tasks on other dates.
2. Open Auto.

Expected: The normal welcome message appears without a reminders heading.

## TC-11 Show reminders alongside recovery warnings

1. Save a deadline due today, then close Auto.
2. Append a malformed line such as `not a task` to the saved data file.
3. Reopen Auto.

Expected: The welcome bubble shows the existing invalid-data warning, then
a blank line and today's reminders, including the recovered deadline.
