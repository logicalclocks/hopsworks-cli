package io.hops.cli.action;

import java.io.IOException;

public interface IHopsworksAction {
    
    /**
     * Send http request.
     * @return http status code.
     */
    int execute() throws Exception;
}
