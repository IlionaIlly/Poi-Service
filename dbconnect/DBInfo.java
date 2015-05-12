package dbconnect;

import java.sql.Connection;
//import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.ws.Endpoint;

import property.Memory;
import server.PoiWebServiceImpl;

public class DBInfo {
	private Connection con;
	private double R;
	private int T;
	private String url;
	private String dbUsername;
	private String dbPassword;
	
	private PreparedStatement insertUser;
	private PreparedStatement deleteUser;
	private PreparedStatement userExists;
	private PreparedStatement selectUserByName;
	private PreparedStatement deleteAllUsers;
	private PreparedStatement insertPoi;
	private PreparedStatement PoiExistsWithinRangeByName;
	private PreparedStatement selectPoiByTypeName;
	private PreparedStatement poiExists;
	private PreparedStatement poiExistsWithinRange;
	private PreparedStatement selectAllPoi;
	private PreparedStatement allGetandSet;
	private PreparedStatement allSet;
	private PreparedStatement PoiNoOfUsersSet;
	private PreparedStatement AllPoiNoOfUsersGet;
	private PreparedStatement AllPoiWithinRange;
	private PreparedStatement selectAllUsers;
	private PreparedStatement validatePassword;
	private PreparedStatement insertSetPoiTM;
	private PreparedStatement insertGet;
	private PreparedStatement insertSet;
	private PreparedStatement CountAllPoiWithinRange;
	private PreparedStatement usersInfo;
	private PreparedStatement userInfo;
	private PreparedStatement allUsersPois;
	private PreparedStatement PoiTotalSet;
	private PreparedStatement selectTypebyId;
	private PreparedStatement checkCoordinates;
	
	
	
	public DBInfo(String url, String dbUsername, String dbPassword, String R, String T) {
		this.url = url;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.R = Double.parseDouble(R);
		this.T = Integer.parseInt(T);
		
	}
	
	public int stopConnection() {
		try {
			
			con.close();
			return 1;
		}
		catch(Exception e) {
			System.out.println("Error in stopConnection in DBInfo " + e);
			return 0;
		}
	}
	
	/**
	* Starts connection with the database 
	*/
	public int startConnection() {

		//Start connection with database
		try {
			con = DriverManager.getConnection(url, dbUsername, dbPassword);
			
			//User table
			validatePassword = con.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
			selectAllUsers = con.prepareStatement("SELECT * FROM users ORDER BY USERNAME");
			selectUserByName = con.prepareStatement("SELECT id FROM users WHERE username = ?");
			insertUser = con.prepareStatement("INSERT INTO users(`username`, `password`) VALUES(?, ?)");
			deleteUser = con.prepareStatement("DELETE FROM users WHERE username = ?");
			userExists = con.prepareStatement("SELECT * FROM users WHERE username = ?");
			deleteAllUsers = con.prepareStatement("DELETE FROM users");
			
			//POI table
			//selectPoiIdByLoc = con.prepareStatement("SELECT id FROM poi WHERE geolocationX = ? AND geolocationY = ?");
			checkCoordinates = con.prepareStatement("SELECT id FROM poi WHERE geolocationX = ? AND geolocationY = ?");
			insertPoi = con.prepareStatement("INSERT INTO poi(`name`, `geolocationX`, `geolocationY`, `typeID`, `createdBY`) " +
					"VALUES(?, ?, ?, ?, ?)");
			//deletePoi = con.prepareStatement("DELETE FROM poi WHERE name = ?");
			selectPoiByTypeName = con.prepareStatement("SELECT id FROM poitype WHERE typeDesc = ?");
			selectTypebyId = con.prepareStatement("SELECT typeDesc FROM poitype WHERE id = ?");
			poiExists = con.prepareStatement("SELECT * FROM poi WHERE name = ? AND geolocationX = ? AND geolocationY = ?");
			poiExistsWithinRange = con.prepareStatement("SELECT * FROM poi WHERE name = ? " +
					"AND ((geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?))");
			selectAllPoi = con.prepareStatement("SELECT * FROM poi");
			insertSetPoiTM = con.prepareStatement("INSERT INTO getset(poiID, userID, getTM, statistics) " +
		    		"VALUES((select id from poi where geolocationX = ? and geolocationY = ?)," +
		    		"(select id from users where username = ?), (select createdTM from poi where geolocationX = ? and geolocationY = ?), 'SET')");
		    insertGet = con.prepareStatement("INSERT INTO getset(`poiID`, `userID`) VALUES(?,?)");
		    insertSet = con.prepareStatement("INSERT INTO getset(poiID, userID, statistics) " +
		    		"VALUES( ?, ?, 'SET')");
		    
		    
		    //Statistics
		    
		    //3.Σε μια περίοδο (πχ μια ημέρα) πόσες φορές κάνανε setMonitorData & getMapData όλοι οι χρήστες
		    allGetandSet = con.prepareStatement("SELECT COUNT(*) FROM getset WHERE (statistics = 'GET' OR statistics ='SET')" +
		    		"AND getTM BETWEEN ? AND ? ");
		    
		    //Όμοια με το 3 μόνο για setMonitorData
            allSet = con.prepareStatement("SELECT COUNT(*) FROM getset WHERE statistics = 'SET'" +
            		"AND getTM BETWEEN ? AND ? ");	
            
            //Για κάθε ένα POI που βρίσκεται στη γεωγραφική περιοχή, πόσοι χρήστες το έχουν δηλώσει
		    PoiNoOfUsersSet = con.prepareStatement("SELECT COUNT(*) FROM poi INNER JOIN getset ON poi.ID = getset.poiID " +
		    										"WHERE statistics = 'SET' AND geolocationX = ? AND geolocationY = ?");
		    
		    //Πόσοι χρήστες έχουν ζητήσει δεδομένα (getMapData) για τη συγκεκριμένη γεωγραφική περιοχή
		    AllPoiNoOfUsersGet = con.prepareStatement("SELECT COUNT(*) FROM poi INNER JOIN getset ON poi.ID = getset.poiID " +
		    	" WHERE statistics='GET' AND (geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?)");
		    
		    //Ποσοι POI βρίσκονται σε μια συγκεκριμένη περιοχή
		    CountAllPoiWithinRange = con.prepareStatement("SELECT COUNT(*) FROM poi " +
		    		"WHERE (geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?)");
		    
		    //Ποιοι POI βρίσκονται σε μια συγκεκριμένη περιοχή
		    AllPoiWithinRange = con.prepareStatement("SELECT * FROM poi " +
			    	"WHERE (geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?)");
		    
		    PoiTotalSet = con.prepareStatement("select p.name,count(*) from poi p, getset g where p.id=g.poiID and statistics='SET' " +
		    		"AND (geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?) group by p.name");
		  

		    //πληροφορίες για ολους τους χρήστες:
		    //username, password
		    //(πόσα set εκανε ο χρήστης)/(ολα τα set ολων των χρηστών),
		    //(πόσα get εκανε ο χρήστης)/(ολα τα get ολων των χρηστών)
		    usersInfo = con.prepareStatement("SELECT users.username, users.password, " +
		    		"(SELECT COUNT(*) FROM getset WHERE users.id = getset.userid AND getset.statistics = 'SET')/ " +
		    		"(SELECT COUNT(*) FROM getset WHERE statistics = 'SET') AS TotalSet, " +
		    		"(SELECT COUNT(*) FROM getset WHERE users.id = getset.userid AND getset.statistics = 'GET')/ " +
		    		"(SELECT COUNT(*) FROM getset WHERE statistics = 'GET') AS TotalGet " +
		    		"FROM users " +
		    		"GROUP BY username ORDER BY username ASC");
		    
		    //info of a specific user 
		    userInfo = con.prepareStatement("SELECT users.username, users.password, " +
		    		"(SELECT COUNT(*) FROM getset WHERE users.id = getset.userid AND getset.statistics = 'SET')/ " +
		    		"(SELECT COUNT(*) FROM getset WHERE statistics = 'SET') AS TotalSet, " +
		    		"(SELECT COUNT(*) FROM getset WHERE users.id = getset.userid AND getset.statistics = 'GET')/ " +
		    		"(SELECT COUNT(*) FROM getset WHERE statistics = 'GET') AS TotalGet " +
		    		"FROM users " +
		    		"WHERE username = ? ");
		    
		    //show user's pois SET
		    allUsersPois = con.prepareStatement("select p.name, p.geolocationX, p.geolocationY, p.createdTM " +
		    		"from poi p, getset g, users u " +
		    		"where p.id = g.poiID and p.createdTM = g.getTM and u.id = g.userid and u.username = ? " +
		    		"group by p.name");
			return 1;    
		    
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	* Select all users
	* allUsers = con.prepareStatement("SELECT * FROM users ORDER BY USERNAME");
	* @return all users from users table
	*/
	public ResultSet selectAllUsers() {
		try {
			ResultSet set = selectAllUsers.executeQuery();
			return set;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	* Select a users
	* userExists = con.prepareStatement("SELECT * FROM users WHERE username = ?");
	* @return a user from users table
	*/
	public ResultSet selectUser(String username) {
		try {
			userExists.setString(1, username);	
			ResultSet set = userExists.executeQuery();
			return set;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}
	

	/**
	* Release Resources
	* @return true if the Resources were Released
	*/
	public boolean ReleaseResources() {
		System.out.println("Resources released! ");
		try {
			 insertUser.close();
			 deleteUser.close();
			 userExists.close();
			 selectUserByName.close();
			 deleteAllUsers.close();
		   	 insertPoi.close();
			 PoiExistsWithinRangeByName.close();
		     selectPoiByTypeName.close();
			 poiExists.close();
			 poiExistsWithinRange.close();
			 selectAllPoi.close();
			 allGetandSet.close();
			 allSet.close();
			 PoiNoOfUsersSet.close();
			 AllPoiNoOfUsersGet.close();
			 AllPoiWithinRange.close();
			 selectAllUsers.close();
			 validatePassword.close();
			 insertSetPoiTM.close();
			 insertGet.close();
			 insertSet.close();
			 CountAllPoiWithinRange.close();
			 usersInfo.close();
			 userInfo.close();
			 allUsersPois.close();
			 PoiTotalSet.close();
			 selectTypebyId.close();
			 checkCoordinates.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
	}

	
	/**
	* Inserts a user into users table
	* insertUser = con.prepareStatement("INSERT INTO users(username, password) VALUES(?, ?)");
	* @return successful insert
	*/
	public int insertUser(String username, String password) {
		try {
			insertUser.setString(1, username);				
			insertUser.setString(2, password);
			return insertUser.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	
	/**
	* Delete a user from users table
	* deleteUser = con.prepareStatement("DELETE FROM users WHERE username = ?");
	* @return successful delete
	*/
	public int deleteUser(String username) {
		try {
			deleteUser.setString(1, username);
			System.out.println( username + "\t" + "was deleted sucsessfully!");
			return deleteUser.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	* Delete all users from users table
	* deleteAllUsers = con.prepareStatement("DELETE FROM users");
	* @return successful delete
	*/
	public int deleteAllUsers() {
		try {
			return deleteAllUsers.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	* Check if there is any other user with the same username
	* userExists = con.prepareStatement("SELECT * FROM users WHERE username = ?");
	* @return true if there username already exists
	*/
	public boolean checkUsername(String username) {
		try {
			userExists.setString(1, username);				// one based ...
			ResultSet set = userExists.executeQuery();
			if (set.next() == false) {
				set.close();
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean checkCoordinates(Double x, Double y) {
		try {
			checkCoordinates.setDouble(1, x);				
			checkCoordinates.setDouble(2, y);
			ResultSet set = checkCoordinates.executeQuery();
			if (set.next() == false) {
				set.close();
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	* Check if there is any other POI with the same name within the range R
	* poiExistsWithinRange = con.prepareStatement("SELECT * FROM poi WHERE name = ? " +
	*	"AND ((geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?))");
	* @return true if there is a POI with the same name within the range R
	*/
	public boolean poiExistsWithinRange(String poiname, Double x, Double y) {
		try {
			poiExistsWithinRange.setString(1, poiname);	
			poiExistsWithinRange.setDouble(2, x-R);				
			poiExistsWithinRange.setDouble(3, x+R);				
			poiExistsWithinRange.setDouble(4, y-R);
			poiExistsWithinRange.setDouble(5, y+R);				
			ResultSet set = poiExistsWithinRange.executeQuery();
			if (set.next() == true) {
				set.close();
				return true;
			} else {
				System.out.println("There are pois within range R!!");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	* POI with the same name but different coordinates within the range R
	* 
	* poiExistsWithinRange = con.prepareStatement("SELECT * FROM poi WHERE name = ? " +
	*	"AND ((geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?))");
	*
	* insertGet = 
	* @return POi with the same name but different coordinates
	*/
	public boolean PoiExistsWithinRangeByName(String poiname, Double x, Double y, String username) {
		try {
			poiExistsWithinRange.setString(1, poiname);	
			poiExistsWithinRange.setDouble(2, x-R);				
			poiExistsWithinRange.setDouble(3, x+R);				
			poiExistsWithinRange.setDouble(4, y-R);
			poiExistsWithinRange.setDouble(5, y+R);		
			//System.out.println("EDW!!!!!!!!!!!!!!");
			ResultSet set = poiExistsWithinRange.executeQuery();
			int creatorId = selectUserByName(username);
			while(set.next()){
				//Double geolocationX = set.getDouble("geolocationX");
				//Double geolocationY = set.getDouble("geolocationX");	
				//int id = selectPoiIdByLoc(poiname);
				int id = set.getInt("id");			
				insertSet(id, creatorId);
				set.close();
				System.out.println("POI EXISTS! But still count it in users GETs!");
				return true;
			} 
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	* Check the log in details
	* validatePassword = con.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
	* @return true if the log in details are right
	*/
	public boolean checkUsernamePassword(String username, String password) {
		try {
			validatePassword.setString(1, username);				// one based ...
			validatePassword.setString(2, password);
			ResultSet set = validatePassword.executeQuery();
			if (set.next() == false) {
				set.close();
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	* Insert a new POI
	* //insertPoi = con.prepareStatement("INSERT INTO poi(name, geolocationX, geolocationY, typeID, createdBY) VALUES(?, ?, ?, ?, ?)");
	* @return POI was inserted
	*/
	public int insertPoi(String name, double geolocationX, double geolocationY, int typeID, int createdBY) { 
		try {
			insertPoi.setString(1, name);				
			insertPoi.setDouble(2, geolocationX);
			insertPoi.setDouble(3, geolocationY);
			insertPoi.setInt(4, typeID);
			insertPoi.setInt(5, createdBY);
			return insertPoi.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	* Return the id of the typeDesc given
	* selectPoiByTypeName = con.prepareStatement("SELECT id FROM poitype WHERE typeDesc = ?");
	* @return the id of the typeDesc
	*/
	public int selectPoiByTypeName(String type){
		try {
			int result = 0;
			selectPoiByTypeName.setString(1, type);
			ResultSet set = selectPoiByTypeName.executeQuery();
			while(set.next()){
				result = set.getInt(1);
			}			
			System.out.println("The id of the type i need is: " + result);
			set.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	

	/**
	* Shows if the POI exists in the database 
	* poiExists = con.prepareStatement("SELECT * FROM poi WHERE name = ? AND geolocationX = ? AND geolocationY = ?");
	* @return true if it does
	*/
	public boolean poiExists(String name, Double x, Double y) {
		try {
			poiExists.setString(1, name);
			poiExists.setDouble(2, x);
			poiExists.setDouble(3, y);
			ResultSet set = poiExists.executeQuery();
			if(set.next() == false){
			   set.close();
			   return false;
			}else {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false;
	}
	
	
	/**
	* Select all POIs
	* selectAllPoi = con.prepareStatement("SELECT * FROM poi");
	* @return executed successfully
	*/
	public ResultSet selectAllPoi() {
		try {
			ResultSet set = selectAllPoi.executeQuery();
			return set;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	* Return id of the POI given by name
	* selectUserByName = con.prepareStatement("SELECT id FROM users WHERE username = ?");
	* @return id
	*/
	public int selectUserByName(String name) {
		try {
			int result = 0;
			selectUserByName.setString(1, name);
			ResultSet set = selectUserByName.executeQuery();
			while(set.next())
				result = set.getInt(1);		
			System.out.println("The id of the user is: " + result);
			set.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	* Return No of users get and set in last period T
	* allGetandSet = con.prepareStatement("SELECT COUNT(*) FROM getset WHERE (statistics = 'GET' OR statistics ='SET')" +
	* 		"AND getTM BETWEEN ? AND ? ");
	* @return No of users
	*/
	public int allGetandSet() {
		try {
			int records = 0;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			String endTime = dateFormat.format(cal.getTime()); 	 //current Date and Time
			System.out.println(endTime); 
			cal.add(Calendar.SECOND, -T);
			String beginTime = dateFormat.format(cal.getTime());
			System.out.println(beginTime);
			allGetandSet.setString(1, beginTime);
			allGetandSet.setString(2, endTime);
		    ResultSet set = allGetandSet.executeQuery();
		    while(set.next())
				records = set.getInt(1);
			set.close();
			System.out.println("The number of users who did get&setMonitorData: " + records);
			return records;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
    	

	/**
	* Return No of users set in last period T
	* allSet = con.prepareStatement("SELECT COUNT(*) FROM getset WHERE statistics = 'SET' AND getTM BETWEEN ? AND ? ");	
	* @return No of users
	*/
	public int allSet() {
		try {
			int records = 0;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			String endTime = dateFormat.format(cal.getTime()); 	 //current Date and Time
			System.out.println(endTime); 
			cal.add(Calendar.SECOND, -T);
			String beginTime = dateFormat.format(cal.getTime());
			System.out.println(beginTime);
			allSet.setString(1, beginTime);
			allSet.setString(2, endTime);
		    ResultSet set = allSet.executeQuery();
			while(set.next())
				records = set.getInt(1);
			set.close();
			System.out.println("The number of users who did setMonitorData: " + records);
			return records;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	* Insert new SET data about a specific POI into getset table (getTM is taken from poi table, its when the POI was created)
	* insertSetPoiTM = con.prepareStatement("INSERT INTO getset(poiID, userID, getTM, statistics) " +
    *		"VALUES((select id from poi where geolocationX = ? and geolocationY = ?)," +
    *		"(select id from users where username = ?), (select createdTM from poi where geolocationX = ? and geolocationY = ?), 'SET')");
	* @return successful getset table update
	*/
	public int insertSetPoiTM(Double x, Double y, String username) {
		try {
			insertSetPoiTM.setDouble(1, x);				
			insertSetPoiTM.setDouble(2, y);
			insertSetPoiTM.setString(3, username);
			insertSetPoiTM.setDouble(4, x);
			insertSetPoiTM.setDouble(5, y);
			return insertSetPoiTM.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	* Insert new GET data about a specific POI into getset table 
	* insertGet = con.prepareStatement("INSERT INTO getset(poiID, userID) " +
    *		"VALUES(?,?)");
	* @return successful getset table update
	*/
	public int insertGet(int poiID, int userID) {
		try {
			insertGet.setDouble(1, poiID);				
			insertGet.setDouble(2, userID);
			//insertGet.setString(3, username);
			return insertGet.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	* Insert new SET data about a specific POI into getset table (SET a POI that does exist within the range R)
	* insertSet = con.prepareStatement("INSERT INTO getset(poiID, userID, statistics) " +
    *		"VALUES(?, ?, 'SET')");
	* @return successful getset table update
	*/
	public int insertSet(int poiID, int userID) {
		try {
			insertSet.setDouble(1, poiID);				
			insertSet.setDouble(2, userID);
			//insertGet.setString(3, username);
			return insertSet.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	* No of users SET POIs within the range R
	* PoiNoOfUsersSet = con.prepareStatement("SELECT COUNT(*) FROM poi INNER JOIN getset ON poi.ID = getset.poiID " +
	*		"WHERE statistics = 'SET' AND geolocationX = ? AND geolocationY = ?");
	* @return No of users
	*/
	public int PoiNoOfUsersSet(double geolocationX, double geolocationY) {
		try {	
			int records = 0;
			PoiNoOfUsersSet.setDouble(1, geolocationX);
			PoiNoOfUsersSet.setDouble(2, geolocationY);
			ResultSet set = PoiNoOfUsersSet.executeQuery();
			while(set.next())
				records = set.getInt(1);
			System.out.println("No of users set in this location: "+ records + "\n");
			set.close();
			return records;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}	
    
	/**
	* No of users GET POIs within the range R
	* AllPoiNoOfUsersGet = con.prepareStatement("SELECT COUNT(*) FROM poi INNER JOIN getset ON poi.ID = getset.poiID " +
	*		" WHERE statistics='GET' AND (geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?)");
	* @return No of users
	*/
	public int AllPoiNoOfUsersGet(double geolocationX, double geolocationY) {
		try {
			int records = 0;
			AllPoiNoOfUsersGet.setDouble(1, geolocationX-R);
			AllPoiNoOfUsersGet.setDouble(2, geolocationX+R);
			AllPoiNoOfUsersGet.setDouble(3, geolocationY-R);
			AllPoiNoOfUsersGet.setDouble(4, geolocationY+R);
			ResultSet set = AllPoiNoOfUsersGet.executeQuery();
			while(set.next())
				records = set.getInt(1);
			System.out.println("No of users getset is: "+ records + "\n");
			set.close();
			return records;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	* Select all POIs within the range R and update getset table with new GETs that the user did
	* Used in getMapData
	* AllPoiWithinRange = con.prepareStatement("SELECT * FROM poi " +
	*		"WHERE (geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?)");
	* @return All POIs within the range R
	*/
	public String AllPoiWithinRange(double x, double y, String username) {
		try {
			String result = "";
			AllPoiWithinRange.setDouble(1, x-R);
			AllPoiWithinRange.setDouble(2, x+R);
			AllPoiWithinRange.setDouble(3, y-R);
			AllPoiWithinRange.setDouble(4, y+R);  
			ResultSet set = AllPoiWithinRange.executeQuery();
			int userID = selectUserByName(username);
			while(set.next()){
				String name = set.getString("name");
				double geolocationX = set.getDouble("geolocationX");
				double geolocationY = set.getDouble("geolocationY");
				int poiID = set.getInt("id");
				String type = selectTypebyId(poiID);
				result = result + geolocationX + "#" + geolocationY + "#" + type + "#" + name + "$";
				if (geolocationX != x && geolocationY != y)
					insertGet(poiID,userID);
			} 
			set.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	* Count all POIs within the range R
	* CountAllPoiWithinRange = con.prepareStatement("SELECT COUNT(*) FROM poi " +
    *		"WHERE (geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?)");
	* @return No of POIs
	*/
	public int CountAllPoiWithinRange(double geolocationX, double geolocationY) {
		try {
			int records = 0;
			CountAllPoiWithinRange.setDouble(1, geolocationX-R);
			CountAllPoiWithinRange.setDouble(2, geolocationX+R);
			CountAllPoiWithinRange.setDouble(3, geolocationY-R);
			CountAllPoiWithinRange.setDouble(4, geolocationY+R);
			ResultSet set = CountAllPoiWithinRange.executeQuery();
			while (set.next()) 
				  records = set.getInt(1);
			System.out.println("No of POIs within range is: "+ records + "\n");
			set.close();
			return records;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	* Show info of all the users in db
	* username, password
	* (no of set of each user)/(no of set of all the users),
	* (no of get of each user)/(no of get of all the users)
	* @return ResultSet 
	*/
	public ResultSet usersInfo() {
		try {
			ResultSet set = usersInfo.executeQuery();
			return set;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	 
	}
	
	/**
	* Show info of one spicific user 
	* username, password 
	* (no of set of the user)/(no of set of all the users),
	* (no of get of the user)/(no of get of all the users)
	* @return ResultSet 
	*/
	public ResultSet userInfo(String username) {
		try {
			userInfo.setString(1, username);
			ResultSet set = userInfo.executeQuery();
			return set;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	 
	}
	

	/**
	* Show all the pois set by a spesific user 
	*  allUsersPois = con.prepareStatement("SELECT p.name, p.geolocationX, p.geolocationY, g.getTM, g.statistics " +
	*	    		"FROM users u, poi p, getset g " +
	*	    		"WHERE u.username = ? AND u.id = g.userid AND g.poiid = p.id AND p.createdTM = g.getTM " +
	*	    		"GROUP BY name ORDER BY name ASC");
	* @return ResultSet 
	*/
	public String allUsersPois(String username) {
		try {
			String result = "";
			allUsersPois.setString(1, username);
			ResultSet set = allUsersPois.executeQuery();
			while(set.next()){
				String name = set.getString("name");
				Double geolocationX = set.getDouble("geolocationX");
				Double geolocationY = set.getDouble("geolocationY");
				String createdTM = set.getString("createdTM");
				result = result + name + " : " + geolocationX + " : " + geolocationY + " : " + createdTM + "\n";
			} 
			set.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	 
	}
	 
	
	/**
	* Show all the pois with their number of SET 
	*  PoiTotalSet = con.prepareStatement("select p.name,count(*) from poi p, getset g where p.id = g.poiID and statistics='SET' " +
	*	    		"AND (geolocationX BETWEEN ? AND ?) AND (geolocationY BETWEEN ? AND ?) group by p.name");
	* @return ResultSet 
	*/
	public String PoiTotalSet(Double geolocationX, Double geolocationY) {
		try {
			String result = "";
			PoiTotalSet.setDouble(1, geolocationX-R);
			PoiTotalSet.setDouble(2, geolocationX+R);
			PoiTotalSet.setDouble(3, geolocationY-R);
			PoiTotalSet.setDouble(4, geolocationY+R);
			ResultSet set = PoiTotalSet.executeQuery();
			while(set.next()){
				String name = set.getString("name");
				int TotalSet = set.getInt("count(*)");
				result = result + name + " : " + TotalSet + "\n";
			} 
			set.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	 
	}
	
	/**
	* Gets an id of a type 
	*  selectTypebyId = con.prepareStatement("SELECT typeDesc FROM poitype WHERE id = ?");
	* @return Poi Type as a string
	*/
	public String selectTypebyId(int Id) {
		try {
			String result = null;
			selectTypebyId.setInt(1, Id);
			ResultSet set = selectTypebyId.executeQuery();
			while(set.next())
				result = set.getString(1);
			set.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	 
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}