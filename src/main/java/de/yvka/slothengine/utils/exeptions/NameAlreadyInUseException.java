package de.yvka.slothengine.utils.exeptions;

/**
 * A Exception which indicates that a name is already in use.
 */
public class NameAlreadyInUseException extends Exception {

	public NameAlreadyInUseException(String name) {
		super("The name '" + name + "' is already in use" );
	}
}
