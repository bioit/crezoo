package org.tgdb.exceptions;

import org.tgdb.frame.ActionException;

public class ApplicationException extends ActionException {
    
    public ApplicationException(String message) {
        super(message);
    }   
    
    public ApplicationException(String message, Exception e)  {
        super(message);
        this.initCause(e);
    }   
}
