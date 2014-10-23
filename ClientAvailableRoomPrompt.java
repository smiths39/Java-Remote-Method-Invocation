import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

public class ClientAvailableRoomPrompt extends JFrame {

    private JLabel roomNameLabel, dayOfWeekLabel, timeOfDayLabel, resultLabel;
    private JComboBox roomNameBox, dayOfWeekBox, timeOfDayBox;
	private JButton validateButton;
    private JPanel centerPanel;
	
	private static BookingInterface bookingInterface = null;
	
	private String [] daysOfWeek = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
	private String [] timeOfDay = { "9am", "10am", "11am", "12am", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm" };
	private String resultLabelText = "Result: ";
	
	/* 
	 *	Constructor establishes connection with server via the interface using port 8888.
	 */
    public ClientAvailableRoomPrompt(BookingInterface newBookingInterface) throws RemoteException {

		try{
			bookingInterface = newBookingInterface;
			String connectionName = setPortConnection();
			bookingInterface = (BookingInterface) Naming.lookup(connectionName);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		initialiseUI(bookingInterface);
	}

	/*
	 *	Initialise all attributes to appear in Java Swing GUI.
	 */
	private void initialiseUI(final BookingInterface bookingInterface) {

		JMenuBar menuBar = new JMenuBar();
		setMenuStyle(menuBar);
		
		initialiseFrameVariables();

		// When submit button is clicked, selected items are used to check availability of desired room.
		validateButton.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent event) {

				String roomStatus = null;
				String submitRoomName = roomNameBox.getSelectedItem().toString();
				String submitDayOfWeek = dayOfWeekBox.getSelectedItem().toString();
				String submitTimeOfDay = timeOfDayBox.getSelectedItem().toString();
				
				try {
					int dayValue = bookingInterface.getDayValue(submitDayOfWeek);
					int timeValue = bookingInterface.getTimeValue(submitTimeOfDay);
					roomStatus = bookingInterface.checkRoomAvailability(submitRoomName, dayValue, timeValue);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				resultLabel.setText(resultLabelText + roomStatus);
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
	
	private void initialiseFrameVariables() {
		
		roomNameLabel = new JLabel("Room Name: ");
        dayOfWeekLabel = new JLabel("Day: ");
        timeOfDayLabel = new JLabel("Time: ");
        resultLabel = new JLabel(resultLabelText);

        roomNameBox = new JComboBox();
        dayOfWeekBox = new JComboBox();
        timeOfDayBox = new JComboBox();
      
		validateButton = new JButton("Submit Values");
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
	 *	Populates combo boxes with day and time values specified in array.
	 */
	@SuppressWarnings("unchecked")
	private void addComboItems(JComboBox combo, String[] strArray) {
		
		for (int index = 0; index < strArray.length; index++) {
			
			combo.addItem(strArray[index]);
		}
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
 	 *	Defines Swing layout coordinates
	 */
    private JPanel createCenterPanel() {
	
		addExistingRoomNames(roomNameBox);			
		addComboItems(dayOfWeekBox, daysOfWeek);
		addComboItems(timeOfDayBox, timeOfDay);			
        
		centerPanel = new JPanel();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        centerPanel.setLayout(gbl);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,15,15,0);
        centerPanel.add(roomNameLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,35,15,0);
        centerPanel.add(roomNameBox,gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,15,15,0);
        centerPanel.add(dayOfWeekLabel,gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,35,15,0);
        centerPanel.add(dayOfWeekBox,gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,15,15,0);
        centerPanel.add(timeOfDayLabel,gbc);

        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,35,15,0);
        centerPanel.add(timeOfDayBox,gbc);

		gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,15,15,0);
        centerPanel.add(validateButton,gbc);
		
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(50,0,0,0);
		gbc.gridwidth = 6;
        centerPanel.add(resultLabel,gbc);

        centerPanel.setBorder(BorderFactory.createTitledBorder("Available Rooms"));

        centerPanel.setPreferredSize(centerPanel.getPreferredSize());
        centerPanel.validate();

        return centerPanel;
    }

    /*
	 *	RMISecurityManager implements a policy for RMI access.
	 */
	private static String setPortConnection() {
		
		System.setSecurityManager(new RMISecurityManager());
		return "rmi://localhost:8888/RMISystem";
	}
	
    public static void main(String[]args) {
	
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new ClientAvailableRoomPrompt(bookingInterface);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | RemoteException e) {
            e.printStackTrace();
        }
    }
}