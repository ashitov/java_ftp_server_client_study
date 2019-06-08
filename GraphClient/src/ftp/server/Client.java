package ftp.server;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    //Client vars
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public Client(String ip, int port){
        // init client
        try {
            InetAddress ip_address = InetAddress.getByName(ip);
            socket = new Socket(ip_address, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception x) {System.out.println("Error while esteblishing connect");}
    }

    public void ClientClose(){
        //correctly close all connections
        try {
            ois.close();
            oos.close();
            socket.close();
        } catch (Exception x) {System.out.println("Error while closing connection");}
    }

    public String ClientAskDir(){
        try{

            Packet packet = new Packet();
            packet.SetHeader("list_dir");
            oos.writeObject(packet);
            oos.flush();
            Packet resp = (Packet) ois.readObject();
            return resp.str_data;

        } catch (Exception x) {return ("Error while getting dirlist");}
    }

    public void SendHandshake(){
        try {
            Packet packet = new Packet();
            packet.SetHeader("auth");
            oos.writeObject(packet);
            oos.flush();
        } catch (Exception x) {System.out.println("Error while sending handshake");}
    }

    public void GetFile(String filename){
        try{
            Packet packet = new Packet();
            packet.SetHeader("get_file");
            packet.AddStr(filename);
            oos.writeObject(packet);
            oos.flush();
            Packet resp = (Packet) ois.readObject();
            FileOutputStream stream = new FileOutputStream("c:\\test_resive\\"+resp.str_data);
            while (resp.header.equals("end")== false){
                String head = resp.header;
                if(resp.header.equals("end")==false) {
                    if(resp.header.equals("file_packet")){
                        int count = Integer.parseInt(resp.str_data);
                        if(count==1024){
                            stream.write(resp.byte_data);
                        }
                        else{
                            byte[] output_byte = Arrays.copyOfRange(resp.byte_data, 0, count);
                            stream.write(output_byte);
                        }
                    }
                    resp = (Packet) ois.readObject();
                }
                else{
                    System.out.println("+_+_+_+_+_+_+_");
                }
            }
            stream.close();
            oos.reset();
        } catch (Exception x){}
    }
}
