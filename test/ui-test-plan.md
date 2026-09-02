# JavaFX UI test plan

Manual tests for Auto's JavaFX chat interface. Start a fresh application for
each test unless the test says otherwise.

Run the application with:

```powershell
.\gradlew.bat run
```

## TC-00 Show startup greeting

1. Start the application.

Expected: The first Auto chat bubble immediately shows the ASCII Auto banner,
`Hello! I'm Auto, your personal assistant.`, and `What can I do for you?`.
The banner uses a monospace font, so its characters remain aligned as drawn.
Saved tasks have already been loaded before this message appears. If the data
file could not be loaded fully, the same welcome bubble also contains the load
warning below the greeting.

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

## TC-08 Exit response

1. Send `bye`.

Expected: Auto displays `Bye. Hope to see you again soon!`. Closing or disabling
the window is outside the current MVP scope.
