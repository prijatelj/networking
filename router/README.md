COSC 450: Computer Networks Assignment #3: Link-State Router
=
Assignment by Dr. Donald L. Simon, simon@mathcs.duq.edu
___

Write a Java program that simulates a link-state router.

Simplifying Assumptions
-
To simplify the simulation we will assume that:
+ The IP address of your router is 127.0.0.1 (which is actually always the localhost and should not be used for anything else.)
+ Routers are connected only to other routers and to networks.
+ Only IP addresses will be used. The router will not have to translate IP addresses to MAC addresses
for transmission to other hosts.
+ Each interface is connected to just one router or one network.
+ Datagrams will be addressed to the network, not individual hosts.
+ A link which is broken will be denoted by having cost equal to Integer.MAX_VALUE (2147483647).
+ The use of IP packets will be simulated by reading data from standard input and writing data to
standard output.
+ All links are bi-directional, i.e., if there is a link from A to B of cost C, assume that there is a link from B to A of cost C, whether or not it is given.

Router Functions
-
The router will:
+ Initially be given a description of its peer routers (a peer router is one to which the router has a direct link) and its connected networks.
+ Inform its peer routers about its initial connections. A peer router should not be told about a connection to itself (it already knows that.)
+ Periodically receive advertisements from peer routers with information about links in the network. The advertisements will be sent when a router notes that a link has gone down (cost of the link equals Integer.MAX_VALUE), has come back up (cost is less than INTEGER.MAX_VALUE) or the cost of the link has changed (the new cost is different from the previous cost.)
+ Propagate changes in link information to its peer routers. Changes should only be set out to interfaces other than the interface upon which the change was received. A change is only propagated if the information is different from the information that the router already has.
+ Modify its network graph whenever changes in link information occur and run Dijsktra's algorithm to generate a shortest path tree for all known routers, with itself as the root.
+ Generateaforwardingtablegiventheshortestpathtree.
+ Receive datagrams from neighboring routers and networks and forward them to the appropriate next hop, either a peer router or a connected network.

Input
-
Instead of using IP to receive packets, the program will read packet information from standard input. The
format for the packet information is given below. Instead of sending packets via IP, the program will write the packet information to standard output. The format for this information is given below.
The input to the program consists of two parts, the initial configuration information and the information that is received in packets during the running of the router simulation. The two parts are separated by the line "0,0,0.0.0.0,0". Both parts consist of lines, each line containing comma-separated fields.
The lines in the initial configuration of the router contain information about the peer routers (IP address of the peer, cost of the link, and the interface to which the peer is connected) and information about connected networks (the network id of the network and interface to which the network is connected, and the cost of the link) The lines in this section are of the form:

FLAG,INTERFACE,IP ADDR,COST

where FLAG is either 0 for network or 1 for peer router, INTERFACE (a non-negative integer) is the interface to which the network or router is connected, IP ADDR (a four-tuple of integers, separated by periods, each between 0 and 255, inclusive) is the the network address or the IP address of the peer router, and COST (a non-negative integer) is the cost of the link (2147483647 for a broken link).
The lines in the simulation part are of two types: link advertisements and datagrams. Both types have the same form:

FLAG,INTERFACE,IP ADDR1,IP ADDR2,COST

In a link advertisement FLAG is always 0, INTERFACE is the interface upon which the advertisement was received, IP ADDR1 and IP ADDR2 are the endpoints of the link being advertised (either routers or networks), COST is a non-negative integer. If COST is Integer.MAX_VALUE, then the link has gone down. Otherwise, the link is active with the given cost. In a datagram, FLAG is always 1, INTERFACE is the interface upon which the datagram was received, IP ADDR1 is the source address, IP ADDR2 is the destination address, and COST is always 0.

Direct Link Changes
-
When the router gets an advertisement that notes a link is down and the link is directly connected to the router (which is noted because one of the endpoints of the link is the router itself), then the router should not send packets on that link until the link goes back up. When the router receives an advertisement that the link is back up, then the router can begin to use that link. The program can assume that the router won't receive any packets on that link until it gets the advertisement that the link is back up.

Output
-
Instead of sending out packets, the router will print the appropriate information to standard output. The format of the output is the same as the format as the input for the simulation part, except that INTERFACE will be the number of the outgoing interface. COST for datagrams is always 0. Note that the router will not try to send out a packet to a link (interface) that is current down.
If there is no route to a host X, then the router should print out the error message "No path to host: X" to standard output. "X" should be the same string as was in the input file.
