import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

public class BookingClient extends JFrame {

	public static BookingInterface bookingInterface = null;
	
	/* 
	 *	Constructor is called when applications is launched.
	 */
	public BookingClient() {
		
		initialiseUI();
	}
	
	/* 
	 *	Constructor is called when user is returning to home page.
	 */
	public BookingClient(BookingInterface newBookingInterface) {
		
		bookingInterface = newBookingInterface;
		initialiseUI();
	}
	
	/*
	 *	Initialise all attributes to appear in Java Swing GUI.
	 */
	private void initialiseUI() {

		JMenuBar menuBar = new JMenuBar();	
		setMenuStyle(menuBar);
		
		ImageIcon logo = new ImageIcon("rmiLogo.png");
		JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);
		
		add(logoLabel);
		
		setFrameAttributes();
	}
	
	private void setFrameAttributes() {
	
		setTitle("RMI System");
		setSize(450, 450);
		setLocationRelativeTo(null);			
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/*
	 *	Set menu headers.	
	 */
	public void setMenuStyle(JMenuBar menuBar) {

		ImageIcon homeIcon = new ImageIcon("home.png");
		ImageIcon exitIcon = new ImageIcon("exit.png");
		
		JMenu file = new JMenu("File");
		JMenu services = new JMenu("Services");
		
		setFileMenuItem(file, homeIcon, exitIcon);
		setServicesMenuItem(services);

		menuBar.add(file);
		menuBar.add(services);

		setJMenuBar(menuBar);
	}
	
	/* 
	 *	Initialise functionality of menu headers.
	 */
	private void setFileMenuItem(JMenu file, ImageIcon homeIcon, ImageIcon exitIcon) {
		
		JMenuItem homeItem = new JMenuItem("Home", homeIcon);
		JMenuItem exitItem = new JMenuItem("Exit", exitIcon);
		
		homeItem.setToolTipText("Return to main menu");
		exitItem.setToolTipText("Exit application");
		
		homeItem.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent event) {
				
				setVisible(false);
				new BookingClient(bookingInterface);
			}
		});
		
		exitItem.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent event) {
				
				System.exit(0);
			}
		});
		
		file.add(homeItem);
		file.add(exitItem);
	}
	
	/*
	 *	Initialises and implements functionality of menu header items.
	 */	
	private void setServicesMenuItem(JMenu services) {
	
		JMenuItem menuListRooms = new JMenuItem("List All Rooms");
		JMenuItem menuCheckAvailability = new JMenuItem("Check Room Availability");
		JMenuItem menuBookRoom = new JMenuItem("Book a Room");
		JMenuItem menuRoomTimeTable = new JMenuItem("Display Room TimeTable");
		
		menuListRooms.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent event) {
				
				try {
					setVisible(false);
					new ClientRoomsList(bookingInterface);
				} catch (RemoteException e) {
					e.printStackTrace();
				}				
			}
		});
		
		menuCheckAvailability.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent event) {

				try {
					setVisible(false);
					new ClientAvailableRoomPrompt(bookingInterface);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		
		menuBookRoom.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent event) {

				try {
					setVisible(false);
					new ClientBookRoom(bookingInterface);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});

		menuRoomTimeTable.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent event) {

				try {
					setVisible(false);
					new ClientBookingTimeTable(bookingInterface);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		
		services.add(menuListRooms);
		services.add(menuCheckAvailability);
		services.add(menuBookRoom);
		services.add(menuRoomTimeTable);				
	}
		
	/*
	 *	Establishes connection with server via the interface using port 8888.
	 */
	private static void establishConnection(BookingInterface bookingInterface) {
						
		BookingClient bookingClient = new BookingClient();				
		
		try {
			String connectionName = setPortConnection();
			bookingInterface = (BookingInterface) Naming.lookup(connectionName);
			bookingInterface.initialiseRooms();
			
		} catch (Exception e) {
			bookingClient.setVisible(false);
			e.printStackTrace();
		}		
	}
	
	/*
	 *	RMISecurityManager implements a policy for RMI access.
	 */
	private static String setPortConnection() {
		
		System.setSecurityManager(new RMISecurityManager());
		return "rmi://localhost:8888/RMISystem";
	}

	public static void main(String [] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
		
			@Override
			public void run() {
		
				establishConnection(bookingInterface);
			}
		});
	}
}
