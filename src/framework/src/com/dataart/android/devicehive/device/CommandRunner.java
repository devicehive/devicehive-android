package com.dataart.android.devicehive.device;

import com.dataart.android.devicehive.Command;

/**
 * Common interface for objects able to execute commands. Usually these are
 * {@link Device} and {@link Equipment}.
 */
public interface CommandRunner {
	/**
	 * Check whether receiver of the command should execute command on some
	 * other thread, not on the main (UI) thread.
	 * 
	 * @param command
	 *            Command to be executed.
	 * @return true if {@link #runCommand(Command)} should be called
	 *         asynchronously, otherwise returns false.
	 */
	boolean shouldRunCommandAsynchronously(final Command command);

	/**
	 * Execute given command. Cab be called either on the main thread or some
	 * other thread. It depends on the value that is returned by
	 * {@link #shouldRunCommandAsynchronously(Command)} method.
	 * 
	 * @param command
	 *            Command to be executed.
	 * @return {@link CommandResult} object describing command execution result
	 *         and status.
	 */
	CommandResult runCommand(final Command command);
}
