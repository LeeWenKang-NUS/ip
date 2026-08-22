package auto.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import auto.storage.Storage;
import auto.ui.Ui;

/** Provides isolated console streams and storage helpers for command tests. */
abstract class CommandTestSupport {
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUpConsole() {
        originalIn = System.in;
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(new byte[0]));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsole() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    protected Ui createUi() {
        return new Ui();
    }

    protected String output() {
        return output.toString(StandardCharsets.UTF_8);
    }

    protected Storage writableStorage(Path directory) {
        return new Storage(directory.resolve("tasks.txt").toString());
    }

    /** Uses an existing directory as the data file to force every save to fail. */
    protected Storage failingStorage(Path directory) {
        return new Storage(directory.toString());
    }
}
