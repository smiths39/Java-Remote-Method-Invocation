import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ClientRoomsList extends JFrame {
	
	private static RoomList roomList = null;
	private static BookingInterface bookingInterface = null;
	
	/* 
	 *	Constructor establishes connection with server via the interface using port 8888.
	 */
	public ClientRoomsList(BookingInterface newBookingInterface) throws RemoteException {
		
		try {
			bookingInterface = newBookingInterface;
			String connectionName = setPortConnection();	
			bookingInterface = (BookingInterface) Naming.lookup(connectionName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initialiseUI(roomList, bookingInterface);	
	} 
	
	/*
	 *	RMISecurityManager implements a policy for RMI access.
	 */
	private static String setPortConnection() {
		
		System.setSecurityManager(new RMISecurityManager());
		return "rmi://localhost:8888/RMISystem";
	}
	
	/*
	 *	Initialise all attributes to appear in Java Swing GUI.
	 */
	private void initialiseUI(RoomList roomList, BookingInterface bookingInterface) {
		
		roomList = new RoomList();
		
		try {
			roomList = bookingInterface.roomList();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		JLabel listTitle = new JLabel("Available Rooms");
		listTitle.setFont(new Font("Arial", Font.BOLD, 13));
		listTitle.setHorizontalAlignment(SwingConstants.CENTER);
			
		JMenuBar menuBar = new JMenuBar();
		setMenuStyle(menuBar);

		CapturedPanel capturedPanel = new CapturedPanel();		
		retrieveRoomList(roomList, capturedPanel);

		this.add(listTitle);
		this.setJMenuBar(menuBar);
		this.add(capturedPanel, BorderLayout.EAST);
		
		setFrameAttributes();
	}
	
	private void setFrameAttributes() {
		
		this.setTitle("RMI System");	
		this.setResizable(false);
		this.setSize(360, 360);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	 *	The method retrieves and displays all room's and their corresponding capacity's defined within the Room_Capacity text file.
	 */	
	private void retrieveRoomList(RoomList roomList, CapturedPanel capturedPanel) {

		PrintStream printStream = System.out;
		System.setOut(new PrintStream(new CapturedText("", capturedPanel, printStream)));
				
		System.out.println("ROOM  |  CAPACITY");
		System.out.println("=================");
		
		for (int index = 0; index < 100; index++) {
			
        	if (roomList.completeRoomList[index] == null) {	
        		break;
        	}
        	
			System.out.println(roomList.completeRoomList[index]);
        }	
	}
		
	public static void main(String [] args) { 
	
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new ClientRoomsList(bookingInterface);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | RemoteException e) {
			e.printStackTrace();
		}
	}
}