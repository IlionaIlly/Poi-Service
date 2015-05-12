package main;

import javax.xml.ws.Endpoint; 

import property.Memory;

import server.PoiWebServiceImpl;
import server_gui.AdminForm;

public class MyMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {     
        //http://0.0.0.0:9999/PoiService/PoiWebServiceImpl?WSDL  
    	
    	//Create memory
    	Memory m = Memory.getInstance();
    	
    	//Publish web service
    	m.publishEndpoint();
   
    	AdminForm f = new AdminForm();
        f.Show();
    	

    }
}