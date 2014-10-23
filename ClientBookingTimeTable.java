import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

public class ClientBookingTimeTable extends JFrame {

    private JLabel roomNameLabel;
    private JComboBox roomNameBox;
	private JButton submitButton;
    private JPanel topPanel, bottomPanel;
	
	private CapturedPanel capturedPanel;
	
	private static BookingInterface bookingInterface = null;
																								
	private String [] daysOfWeek = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
	
	private RoomList roomList = new RoomList();
	
	/* 
	 *	Constructor establishes connection with server via the interface using port 8888.
	 */
	public ClientBookingTimeTable(BookingInterface newBookingInterface) throws RemoteException {

		try{
			bookingInterface = newBookingInterface;
			String connectionName = setPortConnection();
			bookingInterface = (BookingInterface) Naming.lookup(connectionName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		initialiseUI(bookingInterface);
	}
	
	/*
	 *	Initialise all attributes to appear in Java Swing GUI.
	 */
	private void initialiseUI(final BookingInterface bookingInterface) {

		roomList = new RoomList();
		
		try {
			roomList = bookingInterface.roomList();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		JMenuBar menuBar = new JMenuBar();
		setMenuStyle(menuBar);
		
		roomNameLabel = new JLabel("Room Name: ");
		submitButton = new JButton("Submit");
        roomNameBox = new JComboBox();
		
		submitButton.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent event) {

				capturedPanel = new CapturedPanel();
				
				String submitRoomName = roomNameBox.getSelectedItem().toString();
				
				retrieveRoomTimeTable(capturedPanel, submitRoomName);		

				add(capturedPanel, BorderLayout.SOUTH);
				
				submitButton.setEnabled(false);
			}
		});
		
        this.add(createCenterPanel(), "Center");
        this.add(new JPanel(), "West");
        this.add(new JPanel(), "East");
        this.setJMenuBar(menuBar);
		
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
	 *	RMISecurityManager implements a policy for RMI access.
	 */
	private static String setPortConnection() {
		
		System.setSecurityManager(new RMISecurityManager());
		return "rmi://localhost:8888/RMISystem";
	}
    
	/*
	 *	Populates combo boxes with rooms defined in Room_Capacity text file.
	 */
	@SuppressWarnings("unchecked")
	private void addExistingRoomNames(JComboBox roomNames) {
		
		RoomList roomList = new RoomList();		
			
		try {
			roomList = bookingInterface.roomList();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	
		for (int index = 0; index < 100; index++) {
				
			if (roomList.completeRoomList[index] == null) {	
				break;
			}
			
			roomNameBox.addItem(roomList.completeRoomList[index].substring(0, 4));
		}
	}
	
	/*
	 *	Displays the selected room's timetable to the PrintScream console within the Java Swing GUI.
	 */
	private void retrieveRoomTimeTable(CapturedPanel capturedPanel, String retrievedRoomName) {
		
		PrintStream printStream = System.out;
		System.setOut(new PrintStream(new CapturedText("", capturedPanel, printStream)));
		
		String roomName = retrievedRoomName;        
		int roomTimeTable[][][] = null;

		try {
			roomTimeTable = (int[][][]) bookingInterface.bookingTimeTable(roomName).clone();
		} catch (RemoteException e) {
			System.out.println("Error: retrieving room timetable failure.");
		}
		
		displayTimeSlot();
		
		System.out.println();
		int room = Integer.parseInt(roomName.replaceAll("[^0-9]", ""));
		
		displayTimeTable(roomTimeTable, room);
	}
		
	/*
	 *	Formats the timeslot associated with the timetable.
	 */
	private void displayTimeSlot() {
		
		System.out.format("%2s %-14s" ,"TIME", " ");
		
		for (int amIndex = 9; amIndex <= 12; amIndex++) {
			System.out.format("%4s", amIndex);
		}
		
		for (int pmIndex = 1; pmIndex <= 8; pmIndex++) {
			System.out.format("%5s", pmIndex);
		}
		
		System.out.println();
	}
	
	/*
	 *	Formats the timetable associated with the selected room.
	 */	
	private void displayTimeTable(int roomTimeTable[][][], int room) {
	
		for (int dayIndex = 0; dayIndex < 7; dayIndex++) {

			if (daysOfWeek[dayIndex].equals("Wednesday")) {
				System.out.format("%-12s", daysOfWeek[dayIndex]);
			} else if (daysOfWeek[dayIndex].equals("Friday")) { 
				System.out.format("%-20s", daysOfWeek[dayIndex]);
			} else {
				System.out.format("%-16s", daysOfWeek[dayIndex]);
			}
			
			for (int timeIndex = 0; timeIndex < 12; timeIndex++) {
				
				String roomNotation = Integer.toString(roomTimeTable[room][dayIndex][timeIndex]);
				
				/*
				 * 'X' indicates room has been booked.
				 * '-' indicates room is currently available.
				 */
				if (roomNotation.equals("1")) {
					System.out.printf("%6s", "X ");
				} else {
					System.out.printf("%6s", "- ");
				}
			}
			System.out.println();
        }
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
 	 *	Defines Swing layout coordinates
	 */
    private JPanel createCenterPanel() {
	
		addExistingRoomNames(roomNameBox);			
		
		topPanel = new JPanel();

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,15,15,0);
        topPanel.add(roomNameLabel,gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,35,15,0);
        topPanel.add(roomNameBox,gbc);

		gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,15,15,0);
        topPanel.add(submitButton,gbc);
		
        topPanel.setBorder(BorderFactory.createTitledBorder("Room"));

        topPanel.setPreferredSize(topPanel.getPreferredSize());
        topPanel.validate();

        return topPanel;
    }
	
    public static void main(String[]args) {
	
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new ClientBookingTimeTable(bookingInterface);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | RemoteException e) {
            e.printStackTrace();
        }
    }
}