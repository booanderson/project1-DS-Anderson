import java.rmi.Remote;
import java.rmi.RemoteException;

interface ChatServer extends Remote {
    void sendMessage(String message) throws RemoteException;
    void receiveMessage(String message) throws RemoteException;
}

