### Application Description

The application consists of a Client and a Server. An Interface is used to specify the methods defined within the Server, and which the corresponding Client uses during runtime. A text file is used to specify a list of rooms and their corresponding capacities.
Additional classes included are RoomList and the AvailableRoom class. During runtime, the client creates an AvailableRoom object associated for each room specified in the text file. A 3D array is used for storage purposes and represents the room’s name, the day of the week and the time of the day (between 9am and 8pm). Upon initialisation, each object’s array is populated with a 0 to indicate the room is not currently booked. If a booking is made during runtime, the value within the object array will be swapped with a 1 to indicate the room is booked.
The application is launched in the form of a Java Swing GUI. The user may select the desired operations listed under ‘services’ on the menu bar. Upon the selection of an operation, a method corresponding to that selected operation is called by the Client, which was made available via the Server and specified in the Interface, which carries out the appropriate execution to complete the operation. The result is returned to the Client and displayed within the GUI interface.


### Client Classes

There are multiple Client classes associated with the application, with each class representing a separate GUI frame. The GUI interface is populated with predefined data ranges such as days of the week and times of the day, with the exception of room names being processed at runtime. Services provided by the interface include the following operations:

1. Displaying a list of all rooms and their capacities managed by the RMI system.
2. Checking the availability of a room via the user’s selection of the room name, day
and time. All optional selections come in the form of a dropdown list.
3. Booking an available room via the user’s selection of the room name, day and time. If the room is already currently booked, a message will be displayed as to why the
booking was unsuccessful.
4. Printing the timetable of a selected room. The appearance of an X’s within a column
associated with a particular time indicates that the room is booked at that time.
￼￼
### Server Class

The Server class contains the implementations of methods to perform the operations listed above.

1. Displaying a list of all available rooms requires the use of the RoomList class, as this class hold an array of all the available rooms currently stored. The object is returned to the client, which extracts the values and displays them via the interface.
2. For checking the availability of a room and booking a room, the Server returns a boolean verification to the client indicating if a room is available for booking. In the case of booking a room, all values are passed into a 3D array within the AvailableRoom class.
3. Upon the request of a room’s timetable, the Server passes the 3D array to the client. The contents of the array are extracted and displayed in a formatted style within the text area in the Swing UI.

### AvailableRoom Class

This class is primarily responsible for performing booking and verification operations on rooms. A room is booked by receiving a specified room name (which is trimmed to extract integers within the room), a day and a time. A Client sends a request to the Server in order to retrieve room data. The Server class calls this class to retrieve the data requested, with the Server class acting as the ‘Middle Man’.


### RoomList Class

The sole purpose of this class is to store an array of the submitted rooms, specified within the text file. Upon the request of the listing of all rooms and their capacity to be displayed, the object is passed to the Client. The information is extracted and displayed within the text area of the user interface.
Building Instructions (Windows)


The following are instructions to be followed to successfully build the application:

# Terminal 1

1. javac *.java
2. rmic BookingServer
3. start rmiregistry 8888
4. java –Djava.security.policy=connect.policy BookingServer

# Terminal 2

1. java –Djava.security.policy=connect.policy BookingClient
￼￼￼
## Please Note:

port 8888 was selected at random and all socket permissions are granted within the connect.policy file
