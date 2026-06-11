package mesos.am30.server;

import java.io.IOException;

/**
 * Interface for Running IOException methods.
 * <br/>This Interface is used in order to give as a (lambda) parameter a method call which can throw an IOException.
 * <br/>This allows to use a given method without the explicit need of having to catch the IOException each time (the caller will handle it).
 */
@FunctionalInterface
public interface IORunnable {

    /**
     * Run a method from a (lambda) parameter.
     *
     * @throws IOException The method has thrown an IOException.
     */
    void run() throws IOException;
}