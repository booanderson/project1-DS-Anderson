/*usage
 * 1. compile programs with javac clients.java servers.java
 * 2. run java ChatClient "name" in 3 seperate terminals
 * 3. follow code instructions
 */


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

class ChatClient extends UnicastRemoteObject implements ChatServer {
    private String name;

    protected ChatClient(String name) throws RemoteException {
        this.name = name;
    }

    public void sendMessage(String message) throws RemoteException {
        System.out.println(name + " received: " + message);
    }

    public void receiveMessage(String message) throws RemoteException {
        System.out.println("Message from " + name + ": " + message);
    }

    public void startClient() {
        try {
            //try binding the clients rmi instance to the registry 
            Naming.rebind(name, this);
            System.out.println("Client " + name + " ready.");

            // Reading input to send messages to other clients
	    //creating scanner object
            Scanner scanner = new Scanner(System.in);
            while (true) {
		//getting input from user to send
                System.out.print("Enter name of receiver: ");
                String recipientName = scanner.nextLine();
                if (recipientName.equalsIgnoreCase("exit")) break;

                System.out.print("Enter message: ");
                String message = scanner.nextLine();

                //try looking up recipient in regsitry
                try {
                    ChatServer recipient = (ChatServer) Naming.lookup(recipientName);
                    recipient.receiveMessage(name + ": " + message);
                } catch (Exception e) {
                    System.out.println("Failed to send message. " + e.getMessage());
                }
            }

            scanner.close();
        } catch (Exception e) {
            System.out.println("ChatClient exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String clientName = args[0];
        try {
            //create the regsitry if it doesn't already exist. 
	    //this will allow for only the client commands to be run from terminal 
            try {
                Registry registry = LocateRegistry.getRegistry(1099);
                //just in case of exception
		registry.list();
            } catch (RemoteException e) {
                //if regsitry isn't already running
                LocateRegistry.createRegistry(1099);
                System.out.println("RMI registry created on port 1099");
            }

            //starting the client
            ChatClient client = new ChatClient(clientName);
            client.startClient();
        } catch (Exception e) {
    	    //just in case there was an error starting the client
            System.out.println("Error starting client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

