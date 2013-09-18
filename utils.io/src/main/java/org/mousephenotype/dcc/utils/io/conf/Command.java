package org.mousephenotype.dcc.utils.io.conf;

public interface Command<T> {

    public T execute(String line);
}
