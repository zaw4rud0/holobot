package dev.zawarudo.holo.exceptions;

import net.dv8tion.jda.api.Permission;

/**
 * An exception that is thrown when the bot is missing a perm to perform an action
 */
public class MissingPermissionException extends Exception {

	private final Permission[] missingPermissions;

	public MissingPermissionException(Permission... missingPermissions) {
		this.missingPermissions = missingPermissions;
	}

	public Permission[] getMissingPermissions() {
		return missingPermissions;
	}
}