import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 *	All methods contained within the interface are made available to the Client.
 */
public interface BookingInterface extends Remote {
	
	public RoomList roomList() throws RemoteException;
	public void initialiseRooms() throws RemoteException;
	public int getDayValue(String day) throws RemoteException;
	public int getTimeValue(String time) throws RemoteException;
	public boolean checkRoomExists(String room) throws RemoteException;
	public int [][][] bookingTimeTable(String room) throws RemoteException;
	public String bookAvailableRoom(String room, int dayOfWeek, int timeOfDay) throws RemoteException;
	public String checkRoomAvailability(String room, int dayOfWeek, int timeOfDay) throws RemoteException;
}
