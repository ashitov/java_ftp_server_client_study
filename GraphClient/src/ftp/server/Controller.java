package ftp.server;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


public class Controller {

    private Client client;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="file_list"
    private ListView<String> file_list; // Value injected by FXMLLoader

    @FXML // fx:id="connect_button"
    private Button connect_button; // Value injected by FXMLLoader

    @FXML // fx:id="server_ip"
    private TextField server_ip; // Value injected by FXMLLoader

    @FXML // fx:id="get_file_button"
    private Button get_file_button; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        connect_button.setOnAction(event -> {
            connect();
        });
        get_file_button.setOnAction(event -> {
            get_file();
        });
    }

    void connect(){
        String ip = server_ip.getText();
        String[] ip_port = ip.split(":");
        this.client = new Client(ip_port[0], Integer.parseInt(ip_port[1]));
        String list = this.client.ClientAskDir();
        String[] files = list.split(",");
        for (String s: files){
            file_list.getItems().add(s);
        }
    }

    void get_file(){
        String selected = file_list.getSelectionModel().getSelectedItem();
        if(selected != ""){
            this.client.GetFile(selected);
        }
    }
}
