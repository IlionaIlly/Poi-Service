package server;

import javax.jws.WebMethod;

//import javax.jws.WebParam;
import javax.jws.WebService;

//import dbconnect.DBInfo;

import property.Memory;

@WebService(endpointInterface="server.PoiWebService") 
public class PoiWebServiceImpl implements PoiWebService {
	
	private Memory m = Memory.getInstance();
	//private DBInfo connect = m.getDatabase();
 
	@Override
    @WebMethod 
    public String registerUser(String usernamePswRep){
		String[] words = usernamePswRep.split("#");
		String username = words[0];
		String password1 = words[1];
		String password2 = words[2];
		
		if (password1.equals(password2)) {
			if (m.checkUsername(username)) {
				return "ERROR: Already registered";
			} else {
				if (m.insertUser(username, password1) == 0) {
					return "ERROR: Database insert failed" ;
				} else {
					return "SUCCESS: Successful registration";
				}
			}
		}
		return "ERROR: Passwords do not match";
	}
	
	public String setMonitorData(String usernamePsw, String newEntry){
		String[] firstStr = usernamePsw.split("#");
		String username = firstStr[0];
		String password = firstStr[1];
		String[] secondStr = newEntry.split("#");
		Double geolocationX = Double.parseDouble(secondStr[0]);
		Double geolocationY = Double.parseDouble(secondStr[1]);
		String PoiType = secondStr[2];
		String PoiName = secondStr[3];
		
		System.out.println("SetMonitorData... Username: " + username + "  passowrd: " + password );
		if (!m.checkUsernamePassword(username, password)) {
			return "ERROR: Authentication error";
		} else {
			//if there is any poi within the range
			if (m.CountAllPoiWithinRange(geolocationX, geolocationY) == 0) {
				 m.insertPoi(PoiName, geolocationX, geolocationY, m.selectPoiByTypeName(PoiType), m.selectUserByName(username));
				 m.insertSetPoiTM(geolocationX, geolocationY, username);
				 return "Succesfull POI insertion1";
			} 
			//if poi exists within the range 
			else if (m.poiExistsWithinRange(PoiName, geolocationX, geolocationY) == true){
				//the same poi
				if(m.poiExists(PoiName, geolocationX, geolocationY) == true){
					return "This POI already exists1!";
				}
				//has different coordinates
				else {
					m.PoiExistsWithinRangeByName(PoiName, geolocationX, geolocationY, username);
					return "This POI already exists2!";
				}
			}
			//poi doesnt exist among the other pois in this range
			else {	
				if (m.checkCoordinates(geolocationX, geolocationY) == true)
					return "Acsess denied! Poi was not inserted and SET was not counted!";
				m.insertPoi(PoiName, geolocationX, geolocationY, m.selectPoiByTypeName(PoiType), m.selectUserByName(username));
				m.insertSetPoiTM(geolocationX, geolocationY, username);
				return "Succesfull POI insertion2";
			}
		}
	}
	
	public String getMapData(String usernamePsw, String position){
		String[] words = usernamePsw.split("#");
		String username = words[0];
		String password = words[1];
		String[] secondStr = position.split("#");
		Double geolocationX = Double.parseDouble(secondStr[0]);
		Double geolocationY = Double.parseDouble(secondStr[1]);
			
		if (!m.checkUsernamePassword(username, password)) {
			return "ERROR: Authentication error";
		} else {
			//if there is any poi within the range
			if (m.CountAllPoiWithinRange(geolocationX, geolocationY) == 0)
				return "No POIs round this range in database!";
			else {
				//show me pois and then put every poi around the the R to getset table as GET
				return m.AllPoiWithinRange(geolocationX, geolocationY, username);
			}
		}
	}
	
}