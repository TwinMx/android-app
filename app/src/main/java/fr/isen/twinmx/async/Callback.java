package fr.isen.twinmx.async;

/**
 * Created by Clement on 05/01/2017.
 */

public interface Callback<T>  {
    void run(T data);
}
