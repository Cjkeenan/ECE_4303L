# -*- coding: utf-8 -*-
import socket
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# addr = socket.gethostname()
# addr = '192.168.137.174'
addr = '127.0.0.1'
port = 4000
server.connect((addr,port))

key = -1
while(True):
    msgReceived = server.recv(1024)
    print("Server >> ", msgReceived.decode("utf-8"))
    msgSent = input()
    server.send(msgSent.encode("utf-8"))
server.close()
print("Connection Terminated!")