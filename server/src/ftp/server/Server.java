package ftp.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;


class Server {
    //Class vars
    ServerSocket server_socket;
    String folder_path;
    int port;

    //Class init
    Server(){
        try {
            FileOutputStream fos = new FileOutputStream("test.conf");
            FileInputStream fis = new FileInputStream("set.conf");
            Properties cfg = new Properties();
            Properties t = new Properties();
            cfg.load(fis);
            t.setProperty("ye", "11");
            t.store(fos, null);

            String s = cfg.getProperty("port");
            port = Integer.parseInt(s);
            folder_path = cfg.getProperty("path");
            System.out.println(folder_path);
        } catch (IOException e) {System.err.println("Error! No cfg file");}

        try{
            //Class vars
            server_socket = new ServerSocket(port);
        }
        catch (Exception x) {System.out.println("Error while starting server");}
    }

    private String GetFolderFiles(){
        try {
            String res = "";
            File folder = new File(folder_path);
            for (File fe : folder.listFiles()) {
                res = res + fe.getName();
                res = res + ",";
            }
            int str_length = res.length();
            res = res.substring(0, str_length - 1);
            return res;
        } catch (Exception x) {System.out.println("Error while getting file list");}
        return null;
    }

    private void ReadFileFragment(File filename, long start, Integer count, Packet _packet){
        byte[] res = new byte[count];
        try {
            RandomAccessFile raf = new RandomAccessFile(filename, "r");
            raf.seek(start);
            Integer i = 0;
            while(i < count && start+i<raf.length()){
                byte b = raf.readByte();
                res[i] = b;
                i++;
            }
            raf.close();
            _packet.AddByte(res);
            _packet.AddStr(i.toString());
        } catch (Exception x){x.printStackTrace();}
    }

    private void SendPacket(ObjectOutputStream oos, Packet packet){
        try {
            oos.writeObject(packet);
            oos.flush();
            oos.reset();
        } catch (Exception x) {System.out.println("Erroe while sending packet");}
    }

    void RunServer(){
        try {
            Socket socket = server_socket.accept();
            boolean end_trigger = false;
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            int x1 = 0;
            while(!end_trigger || x1 < 100){
                x1 = x1+1;
                oos.flush();
                try{
                    Packet packet = (Packet)ois.readObject();
                    Packet resp_packet = new Packet();
                    switch (packet.header){
                        case "auth":
                            resp_packet.SetHeader("auth");
                            resp_packet.AddStr("OK");
                            SendPacket(oos, resp_packet);
                            ois.reset();

                        case "stop":
                            ois.close();
                            oos.close();
                            end_trigger = true;

                        case "list_dir":
                            resp_packet.SetHeader("list_dir");
                            resp_packet.AddStr(GetFolderFiles());
                            SendPacket(oos, resp_packet);
                            ois.reset();

                        case "get_file":
                            String file_name = packet.str_data;
                            File filename = new File(folder_path+file_name);
                            packet.SetHeader("get_file");
                            SendPacket(oos, packet);
                            packet.ClearPacket();
                            Integer counter = 0;
                            long file_length = filename.length();
                            System.out.println(file_length);
                            while (file_length-counter>=1024) {
                                System.out.println(counter);
                                packet.SetHeader("file_packet");
                                ReadFileFragment(filename, counter, 1024, packet);
                                SendPacket(oos, packet);
                                packet.ClearPacket();
                                counter = counter+1024;
                            }
                            packet.SetHeader("file_packet");
                            ReadFileFragment(filename, counter, 1024, packet);
                            SendPacket(oos, packet);
                            packet.ClearPacket();
                            packet.SetHeader("end");
                            SendPacket(oos, packet);
                            packet.ClearPacket();
                            ois.reset();
                }
              } catch (Exception x) {end_trigger = true;}
            }
        } catch (Exception x) {System.out.println("Error while runnng server");}
    }
}
