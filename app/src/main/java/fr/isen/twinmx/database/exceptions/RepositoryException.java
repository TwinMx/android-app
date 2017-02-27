package fr.isen.twinmx.database.exceptions;

/**
 * Created by Clement on 05/01/2017.
 */

public class RepositoryException extends Exception {

    private Exception parentException;

    public RepositoryException(String message, Exception parentException) {
        super(message);
        this.parentException = parentException;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (this.parentException != null)
            this.parentException.printStackTrace();
    }



}
