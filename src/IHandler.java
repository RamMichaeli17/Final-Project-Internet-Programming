import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Functional interface that gives the server the ability to distinguish between different tasks according to the suitable entity
 * This function is a 'void' type because we are injecting the output through the 'OutputStream'
 */
public interface IHandler {
    public abstract void handle(InputStream fromClient, OutputStream toClient) throws IOException, ClassNotFoundException;
}
