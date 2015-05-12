package server;
  
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.DOCUMENT)
public interface PoiWebService 
{      
    @WebMethod 
    public String registerUser(String usernamePswRep);
    public String setMonitorData(String usernamePsw, String newEntry);
    public String getMapData(String usernamePsw, String position);
}