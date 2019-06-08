package ftp.server;

import java.io.Console;
import java.io.Serializable;

public class Packet implements Serializable {
    String header = null;
    int byte_size = 0;
    int str_size = 0;
    byte[] byte_data = null;
    String str_data = null;

    public void SetHeader(String header_str){header = header_str;}

    public void AddStr(String data_input){
        int len = data_input.length();
        str_size = len;
        str_data = data_input;
    }

    public void AddByte(byte[] data_input){
        byte_size = data_input.length;
        byte_data = data_input;
    }

    public void ClearPacket(){
        header = null;
        byte_size = 0;
        str_size = 0;
        byte_data = null;
        str_data = null;
    }

    public void print_packet(){
        System.out.println("===---===---===");
        System.out.println("Header: "+this.header);
        System.out.println("Str_data: "+this.str_data);
        System.out.println("Str_length: "+this.str_size);
        System.out.println("Byte_data: "+this.byte_data);
        System.out.println("Byte_size: "+this.byte_size);
        System.out.println("===---===---===");
    }
}
