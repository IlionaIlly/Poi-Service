package server_gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
//import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import dbconnect.DBInfo;

import property.Memory;

public class AdminForm extends JFrame implements ActionListener, WindowListener {
	
	//Access to memory
	private Memory m = Memory.getInstance();
	//private DBInfo connect = m;
	
	//Initialize Panels

	private JTabbedPane tabbedPane = new JTabbedPane();
	private JPanel panelTab1 = new JPanel();
	private JPanel requests = new JPanel ();
	private JPanel requests1 = new JPanel();
	private JPanel results = new JPanel();
	private JPanel refresh1 = new JPanel ();
	private JPanel panelTab2 = new JPanel();
	private JPanel panelTab2_North = new JPanel();
		
		
	private JTextField username = new JTextField("Type the username here", 20);
	private JButton search = new JButton("Search!");
	private JButton user_poi=new JButton("UserPoi");
	private JButton delete = new JButton("Delete!");
	private JButton deleteAll = new JButton("DeleteAll!");
	private JTextArea textArea = new JTextArea(25,45);

	private JScrollPane scroll= new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		
	private JLabel getset = new JLabel("Total count of users");
	private JTextField getset1 = new JTextField(" ", 50);
	private JButton done1 = new JButton("OK!");
			
	private JLabel set = new JLabel("Count of checkedin users"); 
	private JTextField set1 = new JTextField(" ", 50);
	private JButton done2 = new JButton("OK!");
		
	private JLabel coordinates = new JLabel("Users around this R");
	private JTextField coordinateX = new JTextField("Type Coordinate X", 10);
	private JTextField coordinateY = new JTextField("Type Coordinate Y", 10);
	private JButton done3 = new JButton("OK!");
		
	private JLabel coordinates1 = new JLabel("Pois around this R");
	private JTextField coordinateX1 = new JTextField("Type Coordinate X", 10);
	private JTextField coordinateY1 = new JTextField("Type Coordinate Y", 10);
	private JButton done4 = new JButton("OK!");
		
		
	private JTextField users = new JTextField("",10);
	private JLabel users1 = new JLabel("Users around this  R");
	
	private JTextArea poisArea = new JTextArea ();
	private JScrollPane scroll1= new JScrollPane(poisArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	private JLabel pois1 = new JLabel("Checked in pois around this R");
	private JButton refresh = new JButton("REFRESH");
		
	private final int T;
	private Timer timer = new Timer();
		
		
	
	public AdminForm() {
		
		//Start connection with database
		m.startConnection();
		
		setLocation(50,50);
		setSize(600,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		T = Integer.parseInt(m.getT());
		
		//our two tabs
		add(tabbedPane);
		tabbedPane.addTab("Statistics", null, panelTab1, "Does nothing");
		tabbedPane.addTab("User info", null, panelTab2, "Does nothing");
			    	
		//this is tab2 and its components 
		panelTab2.setLayout(new BorderLayout());
		panelTab2.add(panelTab2_North, BorderLayout.NORTH);
		panelTab2_North.setLayout(new FlowLayout());
		panelTab2_North.add(username);
		panelTab2_North.add(search);
		panelTab2_North.add(user_poi);
		panelTab2_North.add(delete);
		panelTab2_North.add(deleteAll);
				
				
		textArea.setLineWrap(true);
		textArea.setBackground(Color.PINK);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		scroll.setAutoscrolls(true);
		panelTab2.add(scroll,BorderLayout.EAST);
		
		//this is tab 1 and its components
		panelTab1.setLayout(new GridLayout(4,1,5,5));
		panelTab1.add(requests);
		requests.setLayout(new GridLayout(2,3));
		requests.add(getset);
		requests.add(getset1);
		requests.add(done1);            //for the first ok 
		
		requests.add(set);
		requests.add(set1);
		requests.add(done2);             //for the second ok 
		
		panelTab1.add(requests1);
		requests1.setLayout(new GridLayout(2,4));
		requests1.add(coordinates);
		requests1.add(coordinateX);
		requests1.add(coordinateY);
		requests1.add(done3);              // fort the third ok 
	
		requests1.add(coordinates1);
		requests1.add(coordinateX1);
		requests1.add(coordinateY1);
		requests1.add(done4);              //for the fourth ok 
		
		panelTab1.add(results);             //panel for the results and refresh button 
		results.setLayout(new GridLayout(2,2));
		results.add(users1);
		results.add(pois1);
		results.add(users);
		poisArea.setLineWrap(true);
		poisArea.setBackground(Color.PINK);
		poisArea.setWrapStyleWord(true);
		poisArea.setEditable(false);
		scroll1.setAutoscrolls(true);
		results.add(scroll1,BorderLayout.EAST);
		
		
		panelTab1.add(refresh1);    //refresh 
		refresh1.setLayout(new FlowLayout());
		refresh1.add(refresh);
		
				
		// add event listeners ...
		delete.addActionListener(this);
		deleteAll.addActionListener(this);
		search.addActionListener(this);
		done1.addActionListener(this);
		done2.addActionListener(this);
		done3.addActionListener(this);
		done4.addActionListener(this);
		refresh.addActionListener(this);
		user_poi.addActionListener(this);
		this.addWindowListener(this);
		textArea.setLineWrap(true);
		textArea.setBackground(Color.PINK);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		scroll.setAutoscrolls(true);
		panelTab2.add(scroll,BorderLayout.EAST);
		this.addWindowListener(this);		
				
		UpdateGUITask task = new UpdateGUITask(T);
				
		timer.schedule(task, T*1000, T*1000);    // update gui results for T period 
	}
			
	public void Show() {
		this.setVisible(true);       // make the frame visible 
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Search users profile by their name, if text field is empty then show all users
		if (e.getSource() == search) {
			String usr = username.getText();
			ResultSet set;
			if(usr.trim().equals("")){
				set = m.usersInfo();
			} else {
				set = Memory.getInstance().userInfo(usr);
			}
		
			try {
				textArea.setText("");
				while (set.next()) {
					String u = set.getString("username");
					String p = set.getString("password");
					String s = set.getString("TotalSet");
					String g = set.getString("TotalGet");
					
					String temp = u + " : " + p + " : " + s + " : " + g;
					textArea.setText(textArea.getText() + temp + "\n");
				}
				if (textArea.getText().equals("")) {
					textArea.setText("No user found");
				}
				set.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//show the pois user checked in
		if (e.getSource() == user_poi) {
			try {
				String usr = username.getText();
				String result = Memory.getInstance().allUsersPois(usr);
				textArea.setText("");
				textArea.setText(result);
				
				
				if (textArea.getText().equals("")) {
					textArea.setText("No pois found");
				}
				//set.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//delete a user by his name
		if (e.getSource() == delete) {
			if (username.getText().trim().length() > 0) {
				String u = username.getText().toLowerCase();
				
				int apantisi = JOptionPane.showConfirmDialog(this, "This will delete user " + u + ". Are you sure you want to do it? ");
				if (apantisi == JOptionPane.YES_OPTION) {
					try {
						int diagraftikan = m.deleteUser(u);
						JOptionPane.showMessageDialog(this, String.valueOf(diagraftikan) + " users deleted. ");
						
					} catch (Exception ex) {
					
					}
				}		
			}
		}
		//shows the count of gets and sets by users
		if (e.getSource() == done1) {
			try {
				int counter = m.allGetandSet();
				getset1.setText(String.valueOf(counter));
				
		    } catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex.toString());
			}
		}
		//shows the number of sets by users
		if (e.getSource() == done2) {
			try {
				int counter = Memory.getInstance().allSet();
				set1.setText(String.valueOf(counter));
				
		    } catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex.toString());
			}
		}
		
		//shows the number of users who did getmap data
		if (e.getSource() == done3) {
			try {
				double x,y;
				x = Double.parseDouble(coordinateX.getText());   
				y = Double.parseDouble(coordinateY.getText());
				int counter1 = m.AllPoiNoOfUsersGet(x, y);
				//int counter1 = m.PoiNoOfUsersSet(x, y);
				users.setText(String.valueOf(counter1));
		    	
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex.toString());
			}
		}
		
		//show a list of pois in the area and the total set for them
		if (e.getSource() == done4) {
			try {
			     
				double x,y;
				x = Double.parseDouble(coordinateX1.getText());   
				y = Double.parseDouble(coordinateY1.getText());
				String counter= m.PoiTotalSet(x, y);
			    poisArea.setText(counter);
				
					} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex.toString());
			}
		}
		
		if (e.getSource() == deleteAll) {
			System.out.println("yo");
			int apantisi = JOptionPane.showConfirmDialog(this, "This will delete all users. Are you sure you want to do it? ");
			if (apantisi == JOptionPane.YES_OPTION) {
				try {
					
					int diagraftikan = m.deleteAllUsers();
				    JOptionPane.showMessageDialog(this, String.valueOf(diagraftikan) + " users deleted. ");
			
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, ex.toString());
					ex.printStackTrace();				
				}
			}
		}
		
		if (e.getSource() == refresh) {			
			
		}
	}	
	
	//refresh the two first fields by T
	public void Refresh() {
		int k = m.allSet();
		int l = m.allGetandSet();
		
		set1.setText(String.valueOf(k));
		getset1.setText(String.valueOf(l));
	}
	
	public static void main(String[] args) {
        AdminForm f = new AdminForm();
        f.Show();
    	
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try
		{
			//Clean up the resources
			m.ReleaseResources();
			//Stop connection with database
			m.stopConnection();
			//Unpublish web service
			System.out.println(m);
			m.unpublishEndpoint();
			System.out.println("D");
			
			System.exit(0);
			timer.cancel();
		}
		catch(Exception ex)
		{
			//System.out.println("Error in windoClosing " + ex);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class UpdateGUITask extends TimerTask {
		int T;

		public UpdateGUITask(int T) {
			this.T = T;
		}
		
		@Override
		public void run() {
			Refresh();	
		}
		
	}
}
