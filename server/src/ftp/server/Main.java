package ftp.server;

public class Main {
    Server server;

    private void init(){
        server = new Server();
    }

    private void run(){
        server.RunServer();
    }

    public static void main(String[] args) {
        Main app = new Main();
        System.out.println("Server started");
        app.init();
        app.run();

    }
}
