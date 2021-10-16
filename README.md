# Welcome to the ADB Connector!
This repository contains a project that I made in the year 2017. It was made as
an addition to Android Studio, which had no possibility to connect to an
Android device using the network. The Android Debug Bridge however already had
that function, although one had to type in several commands on the command
line, so I created this project to automate this job.

### Android Debug Bridge commands
 - To list all devices that are connected to the ADB: ``adb devices -l``
 - To connect with a device over network: ``adb connect <IP address>``
 - To open a network port on the connected devices: ``adb tcpip <PORT>``

## Idea
The initial idea was to create a graphical user interface, which should show
what was going on. Also, it should wait until the user plugged in a device,
then it should open the port on that device and prompt the user to plug out his
device. This was necessary as the ADB closed all connections to a device when
it was plugged out. To keep the network connection, it was necessary to
reconnect to the device, therefor the idea to prompt the user to plug out his
device. Finally, it should establish the network connection to that device and
quit.

Because Smartphones are designed to save battery power, the network connection
using WLAN is lost after some time without traffic from the ADB, there should
also be the possibility to reconnect to a device without the need of plugging
it in again, as the network port on the Android device stayed open until the
next reboot.

## GUI-Design
The graphical user interface consists of a single window, which will stay the
top most window while open. It has no borders or decorations. It contains a
text label showing the status of the application, underneath it are two
buttons, one to quit the program, as well as one to skip to the connection
part.

## Approach
As the application is written in Java, the main class of the project is a
subclass of JFrame, and it also implements the ActionListener interface. The
strategy to do what it is supposed to, is to first get the path to the ``adb``,
as it is by default not on the shell path. After setting up the GUI, a timer is
started, which will call the ``actionPerformed(ActionEvent)`` method of the
main class. During execution, the timer will have different action commands. At
the beginning, the action command is set to look for attached devices. Ones the
specified device is found in the list returned by the ADB, the specified port
will be opened. After that, a new timer is started with a action command
indicating to watch the list of attached devices until the specified device
disappears. When this happens, the connection will be established using the
specified IP address. Finally, the timer is started with the command to check
wether the device is still connected. After ten seconds of checking, the
application quits itself.

The skip button directly starts the connection process, regardless wether the
device has been connected or may still be connected.

### Final notes
This project does not meet all best practices of development using Java.
However, I coded the project within a few days back in 2017.

© 2017 [mhahnFr](https://www.github.com/mhahnFr)
