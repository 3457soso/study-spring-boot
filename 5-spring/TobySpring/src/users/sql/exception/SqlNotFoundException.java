package users.sql.exception;

public class SqlNotFoundException extends RuntimeException {
    public SqlNotFoundException(String message){
        super(message);
    }

    public SqlNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}