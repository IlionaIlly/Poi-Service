package property;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.ws.Endpoint;

import server.PoiWebServiceImpl;

import dbconnect.DBInfo;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Memory {
	private static Memory memory;		// (1)
	private String dbName;
	private String dbIp;
	private String dbPort;
	private String dbUsername;
	private String dbPassword;
	private String R;
	private String T;
	private String url;
	private String IP;
	private int PORT;
	
	
	private Endpoint endpoint;
	private DBInfo dbInfo;
	private Properties prop = new Properties();

	// (2)
	private Memory(){
		try{
			//Load property file from project folder
			prop.load(new FileInputStream("settings.properties"));
		} catch (IOException ex) {
			System.err.println("IO Exception occured while loading property file");
		}
		dbName = prop.getProperty("dbName");
		dbIp = prop.getProperty("dbIP");
		dbPort = prop.getProperty("dbPort");
		dbUsername = prop.getProperty("dbUsername");
		dbPassword = prop.getProperty("dbPassword");
		IP = prop.getProperty("IP");
		PORT = Integer.parseInt(prop.getProperty("PORT"));
		R = prop.getProperty("R");
		T = prop.getProperty("T");
		
		url = "jdbc:mysql://" + dbIp + ":" + dbPort + "/" + dbName;

		dbInfo = new DBInfo(url, dbUsername, dbPassword, R, T);	
		
	}
	
	private final ReentrantReadWriteLock fLock = new ReentrantReadWriteLock();
	private final Lock fReadLock = fLock.readLock();
	private final Lock fWriteLock = fLock.writeLock();
	
	public int stopConnection() {
		return dbInfo.stopConnection(); 
	}
	
	public int startConnection() {
		return dbInfo.startConnection();
	}
	
	public ResultSet selectAllUsers(){
		ResultSet s;
		fReadLock.lock();
		s=dbInfo.selectAllUsers();
		fReadLock.unlock();
	    return s;
	    
	}
	
	public ResultSet selectUser(String username){
		ResultSet s;
		fReadLock.lock();
		s= dbInfo.selectUser(username);
		fReadLock.unlock();
		return s;
	}
	
	public boolean ReleaseResources() {
		boolean s;
		fReadLock.lock();
		s= dbInfo.ReleaseResources();
		fReadLock.unlock();
		return s;
	}
	
	public int insertUser(String username, String password) {
		int s;
		fWriteLock.lock();
		s = dbInfo.insertUser(username, password);
		fWriteLock.unlock();
		return s;
				
	}
	
	public int deleteUser(String username) {
		int s;
		fWriteLock.lock();
		s = dbInfo.deleteUser(username);
		fWriteLock.unlock();
		return s;
	}
	
	public int deleteAllUsers() {
		int s;
		fWriteLock.lock();
		s = dbInfo.deleteAllUsers();
		fWriteLock.unlock();
		return s;
	}
	
	public boolean checkUsername(String username) {
		boolean s;
		fReadLock.lock();
		s= dbInfo.checkUsername(username);
		fReadLock.unlock();
		return s;
	}
	
	public boolean poiExistsWithinRange(String poiname, Double x, Double y) {
		boolean s;
		fReadLock.lock();
		s = dbInfo.poiExistsWithinRange(poiname, x, y);
		fReadLock.unlock();
		return s;
	}
	
	public boolean PoiExistsWithinRangeByName(String poiname, Double x, Double y, String username) {
		boolean s;
		 fReadLock.lock();
		 s= dbInfo.PoiExistsWithinRangeByName(poiname, x, y, username);
		 fReadLock.unlock();
		 return s;
	}
	
	public boolean checkUsernamePassword(String username, String password) {
		boolean s;
		fReadLock.lock();
		s= dbInfo.checkUsernamePassword(username, password);
		fReadLock.unlock();
		return s;
	}
	
	public int insertPoi(String name, double geolocationX, double geolocationY, int typeID, int createdBY) { 
		int s ;
		fWriteLock.lock();
		s= dbInfo.insertPoi(name, geolocationX, geolocationY, typeID, createdBY);
		fWriteLock.unlock();
		return s;
	}
	
	public int selectPoiByTypeName(String type) {
		int s;
		fReadLock.lock();
		s= dbInfo.selectPoiByTypeName(type);
		fReadLock.unlock();
		return s;
	}
	
	public boolean poiExists(String name, Double x, Double y) {
		boolean s;
		fReadLock.lock();
		s = dbInfo.poiExists(name, x, y);
		fReadLock.unlock();
		return s;
	}
	
	
	public ResultSet selectAllPoi() {
		ResultSet s;
		fReadLock.lock();
		s=dbInfo.selectAllPoi();
		fReadLock.unlock();
		return s;
	}
	
	public int selectUserByName(String name) {
		int s;
		fReadLock.lock();
		s = dbInfo.selectUserByName(name);
		fReadLock.unlock();
		return s;
	}
	
	public int allGetandSet() {
		return dbInfo.allGetandSet();
	}
	
	public int allSet() {
		int s;
		fReadLock.lock();
		s = dbInfo.allSet();
		fReadLock.unlock();
		return s;
	}
	
	public int insertSetPoiTM(Double x, Double y, String username) {
		int s;
		fWriteLock.lock();
		s = dbInfo.insertSetPoiTM(x, y, username);
		fWriteLock.unlock();
		return s;
	}
	
	public int insertGet(int  poiID, int userID) {
		int s;
		fWriteLock.lock();
		s = dbInfo.insertGet(poiID,userID);
		fWriteLock.unlock();
		return s;
	}
	
	public int insertSet(int poiID, int userID) {
		int s;
		fWriteLock.lock();
		s= dbInfo.insertSet(poiID, userID);
		fWriteLock.unlock();
		return s;
	}
	
	public int PoiNoOfUsersSet(double geolocationX, double geolocationY) {
		int s;
		fReadLock.lock();
		s = dbInfo.PoiNoOfUsersSet(geolocationX, geolocationY);
		fReadLock.unlock();
		return s;
 	}
	
	public int AllPoiNoOfUsersGet(double geolocationX, double geolocationY) {
		int s;
		fReadLock.lock();
		s = dbInfo.AllPoiNoOfUsersGet(geolocationX, geolocationY);
		fReadLock.unlock();
		return s;
	}
	
	public String AllPoiWithinRange(double x, double y, String username) {
		String s;
		fReadLock.lock();
		s = dbInfo.AllPoiWithinRange(x, y, username);
		fReadLock.unlock();
		return s;
	}
	
	public int CountAllPoiWithinRange(double geolocationX, double geolocationY) {
		int s;
		fReadLock.lock();
		s = dbInfo.CountAllPoiWithinRange(geolocationX, geolocationY);
		fReadLock.unlock();
		return s;
	}
	
	public ResultSet usersInfo() {
		ResultSet s;
		fReadLock.lock();
		s =  dbInfo.usersInfo();
		fReadLock.unlock();
		return s;
	}
	
	public ResultSet userInfo(String username) {
		ResultSet s;
		fReadLock.lock();
		s = dbInfo.userInfo(username);
		fReadLock.unlock();
		return s;
	}
	
	public String allUsersPois(String username) {
		String s;
		fReadLock.lock();
		s = dbInfo.allUsersPois(username);
		fReadLock.unlock();
		return s;
	}
	
	public boolean checkCoordinates(Double x, Double y) {
		Boolean s;
		fReadLock.lock();
		s = dbInfo.checkCoordinates(x,y);
		fReadLock.unlock();
		return s;
	}
	
    public String selectTypebyId(int Id) {
        String s;
        fReadLock.lock();
        s = dbInfo.selectTypebyId(Id);
		return s;
	}
	
	 public String PoiTotalSet(Double geolocationX, Double geolocationY){
		 String s;
		 fReadLock.lock();
		 s=dbInfo.PoiTotalSet(geolocationX, geolocationY);
		 fReadLock.unlock();
		 return s;
	 }
	
	
	
	public String toString() {
		String s = 	"dbName: " + dbName + "\n" + 
					"dbIP: " + dbIp + "\n" + 
					"dbPort: " + dbPort + "\n" + 
					"dbUsername: " + dbUsername + "\n" + 
					"dbPassword: " + dbPassword + "\n" + 
					"R: " + R + "\n" + 
					"T: " + T + "\n" + 
					"url: " + url + "\n";
		return s;
	}
	
	// (3)
	public static Memory getInstance() {
		if (memory == null) {
			synchronized (Memory.class) { // (4)
				 if (memory == null) {
					 memory = new Memory();
				 }
			}
		}
		return memory;
	}


	public String getdbName() {
		return dbName;
	}


	public void setdbName(String dbName) {
		this.dbName = dbName;
	}


	public String getdbIp() {
		return dbIp;
	}


	public void setdbIp(String dbIp) {
		this.dbIp = dbIp;
	}

	public String getdbPort() {
		return dbPort;
	}

	public void setdbPort(String dbPort) {
		this.dbPort = dbPort;
	}
	
	public String getdbUsername() {
		return dbUsername;
	}
	
	public void setdbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}
	
	public String getdbPassword() {
		return dbPassword;
	}
	
	public void setdbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	public String getR() {
		return R;
	}
	
	public void setR(String dbR) {
		this.R = dbR;
	}
	
	public String getT() {
		return T;
	}
	
	public void setT(String dbT) {
		this.T = dbT;
	}
	
	//public synchronized DBInfo getDatabase() {
	//	return dbInfo;
	//}
	
	public void publishEndpoint(){
		//Publish web service
    	endpoint = Endpoint.publish("http://"+  IP + ":" + PORT +"/PoiService/", new PoiWebServiceImpl()); 	
    	
    	System.out.println("The web service is published!");
	}
	
	public void unpublishEndpoint(){
		//Unpublish web service
		endpoint.stop();
		
		System.out.println("The web service is unpublished!");
	}
	
	// (5)
	@Override
	protected Object clone() throws CloneNotSupportedException {
		System.out.println("Clone not supported for singleton object");
		return null;
	}

	
	
}
