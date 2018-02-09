
Distributed Editor
==================

Distributed application that allows simultaneous editing of text from multiple editors running in different processes (they can run on different machines). 

This is a project I developed during my master studies. I picked it for two reasons. First, I really enjoyed developing it, as it involved dealing with many interesting challenges, like parallel communication over the network, synchronizing operations and object oriented design. The second reason is that it is strongly related to my recent education and work background, which are based on developing distributed systems. 

Implementation Idea
===================

The editor makes use of a server that synchronizes editing commands issued by multiple editor instances. There are 2 types of commands:

1. insert character x at position y ==> inserts a character at the specified position

2. delete character at position y ==> deletes the character at the specified position

To synchronize commands, the server makes use of a queue. Whenever a new command is received from a client, the server adds it to the queue. While the queue is not empty, the server removes one command at a time and dispatches it to all editor instances. All the network operations are synchronous to make sure that all editors perform the commands in the same order.

For the user interface, a JFrame object with a text area is used. When a character is typed or deleted, the change is displayed to the user only after the command has been  acknowledged by the server to make sure that consistency among editors is preserved.

Usage
=====

Building the project requires Apache Ant. 

1. To compile it, use the command: ant compile

2. To run unit and integration tests, use the command: ant test

3. To start a session with 3 editors on the local machine, use the command: ant run

4. To manually start the server, use the command: java -cp bin server.CentralServer -p [port]

5. To manually start a client, use the command: java -cp bin editor.DistributedEditor -p [port] -s [server_ip]:[server_port]

NOTES:
	* The server and the clients should be started with different ports.
	* The server and the clients can run on different machines.