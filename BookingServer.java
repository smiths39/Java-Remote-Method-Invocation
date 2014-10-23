import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class BookingServer extends UnicastRemoteObject implements BookingInterface {

	private int dayOfWeek, timeOfDay, selectedRoom;
	
	public String [] availableRoomList = new String[1000];
	public AvailableRoom [] availableRoom = new AvailableRoom[1000];
	
	public String input = new String();
	public RoomList roomList = new RoomList();
	
	public BookingServer() throws RemoteException {}
	
	/*
	 *	This is called by the Client upon initialisation of the application.
	 *  Input read in, via the Room_Capacity text file, creates an object for the corresponding room entitled within the file.
	 */
	@Override
	public void initialiseRooms() throws RemoteException {
		
		String bookingRecord = null, room = null;
		int counter = 0;
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Room_Capacity.txt"));
			
			while ((bookingRecord = bufferedReader.readLine()) != null) {
				
				room = bookingRecord.substring(0, bookingRecord.lastIndexOf(" ", bookingRecord.length()));
				availableRoom[counter] = new AvailableRoom(room);
				
				counter++;
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 *	The submitted room is compared with the name of each existing room object.
	 */
	public int compareExistingRooms(String room) {
	
		for (int index = 0; index < availableRoom.length; index++) {
			
			if (availableRoom[index].roomName.equals(room) == false) {
				
				return index;
			}
		}
		
		return -1;
	}
	
	/*
	 *	The submitted room is cross-examined for existence.
	 *
	 *	If the submitted room has not been booked for a specific day and time, corresponding to the requested day and time,
	 *  an appropriate message will be returned, indicating the room's availability.
	 */
	@Override
	public String checkRoomAvailability(String room, int dayOfWeek, int timeOfDay) throws RemoteException {
		
		int roomNumber = compareExistingRooms(room);
		
		if (checkRoomExists(room) == false) {
			return "Room does not exist";
		}
		
		if (availableRoom[roomNumber].roomIsAvailable(room, dayOfWeek, timeOfDay)) {
			return "Room can be booked";
		} else {
			return "Room cannot be booked";
		}
	}
	
	/*
	 *	The submitted room is cross-examined for existence.
	 *
	 *	If the submitted room has not been booked, the submitted room will be booked with the selected day and time.
	 *  An appropriate message will be returned, indicating the room's booking status.
	 */
	@Override
	public String bookAvailableRoom(String room, int dayOfWeek, int timeOfDay) throws RemoteException {
		
		int roomNumber = compareExistingRooms(room);
		
		if (checkRoomExists(room) == false) {
			return "Room does not exist";
		}
		
		if (availableRoom[roomNumber].roomIsAvailable(room, dayOfWeek, timeOfDay)) {
			
			availableRoom[roomNumber].bookRoom(room, dayOfWeek, timeOfDay);
			return "Room has been booked";
		} else {
			
			return "Room is already booked"; 
		}
	}

	/*
	 *	The timetable associated with the submitted room is calculated, with the resulting array displaying the room's timetable.
	 */
	@Override
	public int[][][] bookingTimeTable(String room) throws RemoteException {

		for (int index = 0; index < availableRoom.length; index++) {
			
			if (availableRoom[index].roomName.equals(room) == false) {
	
				return availableRoom[index].timeSlot;
			} 
		}
		
		return availableRoom[availableRoom.length - 1].timeSlot;
	}

	/*
	 *	This method ensures the submitted room exists within the application's corresponding text file.
	 */
	@Override
	public boolean checkRoomExists(String room) throws RemoteException {
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Room_Capacity.txt"));
			Scanner scanner = new Scanner(bufferedReader);
	
			while (scanner.hasNext()) {
				
				int trimRoom = Integer.parseInt(room.replaceAll("[^0-9]", ""));
				int trimLine = Integer.parseInt(scanner.next().replaceAll("[^0-9]", ""));				

				if (trimRoom == trimLine) {
					return true;
				}
			}
			bufferedReader.close();
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/*
	 *	This method produces the list of rooms and their corresponding capacity, as described in the application's text file.
	 *	The returned list of rooms can be retrieved by the Client. 
	 */
	@Override
	public RoomList roomList() throws RemoteException {
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Room_Capacity.txt"));
			
			if ((input = bufferedReader.readLine()) != null) {
				
				roomList.completeRoomList[0] = input;
				
				for (int index = 1; index < availableRoomList.length; index++) {
					
					if ((input = bufferedReader.readLine()) != null) {
					
						roomList.completeRoomList[index] = input;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return roomList;
	}
	
	/*
	 *	Return integer value corresponding to selected day.
	 */
	@Override
	public int getDayValue(String day) throws RemoteException {
		
		if (day.equals("Monday")) {
			return 0;
		} else if (day.equals("Tuesday")) {
			return 1;
		} else if (day.equals("Wednesday")) {
			return 2;
		} else if (day.equals("Thursday")) {
			return 3;
		} else if (day.equals("Friday")) {
			return 4;
		} else if (day.equals("Saturday")) {
			return 5;
		} else if (day.equals("Sunday")) {
			return 6;
		} 
		
		return -1;
	}
	
	/*
	 *	Extract integer value from selected time and returns corresponding value.
	 */
	@Override
	public int getTimeValue(String time) throws RemoteException {

		int timeValue = 0;
		
		if (time.length() == 3) {
			timeValue = Integer.parseInt(time.substring(0, 1));
		} else {
			timeValue = Integer.parseInt(time.substring(0, 2));
		}
		
		if (timeValue == 9) {
			return 0;
		} else if (timeValue == 10) {
			return 1;
		} else if (timeValue == 11) {
			return 2;
		} else if (timeValue == 12) {
			return 3;
		} else if (timeValue == 1) {
			return 4;
		} else if (timeValue == 2) {
			return 5;
		} else if (timeValue == 3) {
			return 6;
		} else if (timeValue == 4) {
			return 7;
		} else if (timeValue == 5) {
			return 8;
		} else if (timeValue == 6) {
			return 9;
		} else if (timeValue == 7) {
			return 10;
		} else if (timeValue == 8) {
			return 11;
		} 
		
		return -1;
	}
	
	public static void main(String [] args) {
		
		try {
			BookingServer bookingServer = new BookingServer();
			String rmiName = "rmi://localhost:8888/RMISystem";
			Naming.bind(rmiName, bookingServer);
			
			System.out.println(rmiName + " is successfully running...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}