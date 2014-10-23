import java.io.Serializable;

class AvailableRoom implements Serializable {

	public String roomName;
	
	/*
	 *	Each available room object contains an array representing the following:
	 *		- 1000	=> Room numbers that can be stored, ranging between 0 - 1000
	 *		- 7 	=> Days of the week
	 *	    - 12	=> Hours of the day, ranging between 9am - 8pm
	 */
	public int [][][] timeSlot = new int[1000][7][12];
	
	/*
	 *  All rooms are made available upon initialisation.
	 */
	public AvailableRoom(String newRoomName) {
		
		this.roomName = newRoomName;
		
		int room = extractRoomNameIntegerValue(roomName);
		
		for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
			
			for (int hourIndex = 0; hourIndex < 12; hourIndex++) {
				
				this.timeSlot[room][dayIndex][hourIndex] = 0;
			}
		}
	}
		
	/*
	 *	A value of 1 indicates that a room is not available for booking at that specific day and time.
	 */
	public boolean roomIsAvailable(String newRoomName, int dayOfWeek, int timeOfDay) {

		int room = extractRoomNameIntegerValue(newRoomName);
		
		if (timeSlot[room][dayOfWeek][timeOfDay] != 1) {
			return true;
 		} else {
 			return false;
 		}
	}
	
	/*
	 * 	A value of 1 is set to indicate that the room has been booked at that specific day and time.
	 */
	public void bookRoom(String newRoomName, int dayOfWeek, int timeOfDay) {
	
		int room = extractRoomNameIntegerValue(newRoomName);
		this.timeSlot[room][dayOfWeek][timeOfDay] = 1;
	}
	
	/*
	 *  Strips all characters from string name and returns name in integer format.
	 */
	private int extractRoomNameIntegerValue(String newRoomName) {
	
		return Integer.parseInt(newRoomName.replaceAll("[^0-9]", ""));
	}
}
