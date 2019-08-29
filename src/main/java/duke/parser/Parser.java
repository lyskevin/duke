package duke.parser;

import duke.Duke;
import duke.commands.Command;
import duke.commands.DeadlineCommand;
import duke.commands.DeleteCommand;
import duke.commands.DoneCommand;
import duke.commands.EventCommand;
import duke.commands.ExitCommand;
import duke.commands.IncorrectCommand;
import duke.commands.ListCommand;
import duke.commands.TodoCommand;
import duke.data.tasks.Deadline;
import duke.data.tasks.Event;
import duke.data.tasks.Todo;
import duke.exceptions.DukeException;

import java.util.regex.Pattern;
import java.text.ParseException;

public class Parser {

    private static final String BASIC_COMMAND_FORMAT = "[\\w\\/\\:\\-\\s]+";
    private static final int COMMAND_WORD_INDEX = 0;
    private static final int ARGUMENTS_INDEX = 1;
    private static final int INPUT_STRING_SPLIT_LIMIT = 2;

    /**
     * Parses the specified input and returns the appropriate command.
     * @param input The specified input.
     * @return The appropriate command based on the specified input.
     */
    public static Command parse(String input) {
        if (!Pattern.matches(BASIC_COMMAND_FORMAT, input)) {
            return prepareIncorrectCommand();
        }
        String commandWord = getCommandWord(input);
        Command command;
        switch (commandWord) {
        case DeadlineCommand.COMMAND_WORD:
            command = prepareDeadlineCommand(input);
            break;
        case DeleteCommand.COMMAND_WORD:
            command = prepareDeleteCommand(input);
            break;
        case DoneCommand.COMMAND_WORD:
            command = prepareDoneCommand(input);
            break;
        case EventCommand.COMMAND_WORD:
            command = prepareEventCommand(input);
            break;
        case ExitCommand.COMMAND_WORD:
            command = prepareExitCommand();
            break;
        case ListCommand.COMMAND_WORD:
            command = prepareListCommand();
            break;
        case TodoCommand.COMMAND_WORD:
            command = prepareTodoCommand(input);
            break;
        default:
            command = prepareIncorrectCommand();
            break;
        }
        return command;
    }

    private static String getArguments(String input) throws ArrayIndexOutOfBoundsException {
        try {
            return input.split(" ", INPUT_STRING_SPLIT_LIMIT)[ARGUMENTS_INDEX];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw e;
        }
    }

    /**
     * Returns the command word in the specified input.
     * @param input The specified input.
     * @return The command word in the specified input.
     */
    private static String getCommandWord(String input) {
        return input.split(" ", INPUT_STRING_SPLIT_LIMIT)[COMMAND_WORD_INDEX];
    }

    /**
     * Prepares and returns a deadline command based on the specified input.
     * @param input The specified arguments.
     * @return A deadline command based on the specified input.
     */
    private static Command prepareDeadlineCommand(String input) {
        try {
            String arguments = getArguments(input);
            String[] deadlineInformation = arguments.split(" /by ");
            Deadline deadline = new Deadline(deadlineInformation[0], deadlineInformation[1]);
            return new DeadlineCommand(deadline);
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            String errorMessage = "deadline command format: deadline <description> /by <date>\n";
            return new IncorrectCommand(errorMessage);
        } catch (ParseException | IllegalArgumentException e) {
            String errorMessage = "Please enter a valid date format.\n"
                    + "Date and time: dd/mm/YYYY HH:mm:ss\n";
            return new IncorrectCommand(errorMessage);
        }
    }

    /**
     * Prepares and returns a delete command based on the specified input.
     * @param input The specified input.
     * @return A delete command based on the specified input.
     */
    private static Command prepareDeleteCommand(String input) {
        try {
            String arguments = getArguments(input);
            String[] taskInformation = arguments.split(" ");
            if (taskInformation.length > 1) {
                throw new IllegalArgumentException();
            }
            int taskNumber = Integer.parseInt(taskInformation[0]);
            return new DeleteCommand(taskNumber);
        } catch (ArrayIndexOutOfBoundsException
                | IllegalArgumentException e) {
            String errorMessage = "delete command format: delete <number>";
            return new IncorrectCommand(errorMessage);
        } catch (IndexOutOfBoundsException e) {
            String errorMessage = "Please enter a valid task number for the delete command.\n"
                    + "The delete command will not work if there are 0 stored tasks.\n";
            return new IncorrectCommand(errorMessage);
        }
    }

    /**
     * Prepares and returns a done command based on the specified input.
     * @param input The specified input.
     * @return A done command based on the specified input.
     */
    private static Command prepareDoneCommand(String input) {
        try {
            String arguments = getArguments(input);
            String[] taskInformation = arguments.split(" ");
            if (taskInformation.length > 1) {
                throw new IllegalArgumentException();
            }
            int taskNumber = Integer.parseInt(taskInformation[0]);
            return new DoneCommand(taskNumber);
        } catch (ArrayIndexOutOfBoundsException
                | IllegalArgumentException e) {
            String errorMessage = "done command format: done <number>\n";
            return new IncorrectCommand(errorMessage);
        } catch (IndexOutOfBoundsException e) {
            String errorMessage = "Please enter a valid task number for the done command.\n"
                    + "The done command will not work if there are 0 stored tasks.";
            return new IncorrectCommand(errorMessage);
        }
    }

    /**
     * Prepares and returns an exit command.
     * @return An exit command.
     */
    private static Command prepareExitCommand() {
        return new ExitCommand();
    }

    /**
     * Prepares and returns an event command based on the specified input.
     * @param input The specified input.
     * @return An event command based on the specified input.
     */
    private static Command prepareEventCommand(String input) {
        try {
            String arguments = getArguments(input);
            String[] taskInformation = arguments.split(" /at ");
            Event event = new Event(taskInformation[0], taskInformation[1]);
            return new EventCommand(event);
        } catch (ArrayIndexOutOfBoundsException
                | StringIndexOutOfBoundsException e) {
            String errorMessage = "event command format: event <description> /at <dateAndTime>\n";
            return new IncorrectCommand(errorMessage);
        } catch (ParseException e) {
            String errorMessage = "Please enter a valid date format.\n"
                    + "Date and time: dd/mm/YYYY HH:mm:ss\n";
            return new IncorrectCommand(errorMessage);
        }
    }

    /**
     * Prepares and returns an incorrect command with the default error message.
     * @return An incorrect command with the default error message.
     */
    private static Command prepareIncorrectCommand() {
        return new IncorrectCommand();
    }

    /**
     * Prepares and returns a list command based on the specified arguments.
     * @return A list command based on the specified arguments.
     */
    private static Command prepareListCommand() {
        return new ListCommand();
    }

    /**
     * Prepares and returns a todo command based on the specified input.
     * @param input The specified input.
     * @return A todo command based on the specified input.
     */
    private static Command prepareTodoCommand(String input) {
        try {
            String arguments = getArguments(input);
            Todo todo = new Todo(arguments);
            return new TodoCommand(todo);
        } catch (StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException e) {
            String errorMessage = "todo command format: todo <description>\n";
            return new IncorrectCommand(errorMessage);
        }
    }

}