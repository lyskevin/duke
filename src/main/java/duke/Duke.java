package duke;

import duke.commands.Command;
import duke.commands.CommandResult;

import duke.data.TaskList;

import duke.exceptions.DukeException;

import duke.parser.Parser;

import duke.storage.Storage;

import duke.ui.TextUi;

/**
 * Implements the Duke chat bot.
 * @author Lim Yong Shen, Kevin
 */
public class Duke {

    private static final String DATA_FILE_PATH = "src/main/java/duke/data/tasks.txt";

    private Storage storage;
    private TaskList tasks;
    private TextUi ui;

    /**
     * Constructs a new Duke chat bot with the specified data file name.
     * @param fileName The specified data file name.
     */
    private Duke(String fileName) {
        ui = new TextUi();
        storage = new Storage(fileName);
        try {
            tasks = storage.load();
        } catch (DukeException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Constructs a Duke instance (used for the Launcher class).
     */
    public Duke() {

    }

    /**
     * Processes the specified input and returns the appropriate message.
     * @param input The specified input.
     * @return The appropriate message.
     */
    private String processInput(String input) {
        Command command = getCommand(input);
        assert tasks != null;

        command.setTaskListToExecuteOn(tasks);
        CommandResult commandResult = command.execute();
        try {
            storage.save(tasks);
        } catch (DukeException e) {
            return e.getMessage();
        }
        return commandResult.getMessage();
    }

    /**
     * Parses the specified input and returns the appropriate command.
     * @param input The specified input.
     * @return The appropriate command.
     */
    private Command getCommand(String input) {
        return Parser.parse(input);
    }

    /**
     * Returns Duke's response based on the specified input.
     * @param input The specified input.
     * @return Duke's response based on the specified input.
     */
    public String execute(String input) {
        return new Duke(DATA_FILE_PATH).processInput(input);
    }

}
