package fr.isen.twinmx.database.exceptions;

/**
 * Created by Clement on 05/01/2017.
 */

public class MotoRepositoryException extends RepositoryException {
    public MotoRepositoryException(String message, Exception ex) {
        super(message, ex);
    }
}
