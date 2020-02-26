# -*- coding: utf-8 -*-
import socket
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
addr = ''
port = 4000
server.bind((addr,port))
server.listen(5)
clientsocket,address = server.accept()
print("Connection from {0} has ben established!".format(address))
clientsocket.send(bytes("Welcome to the server!", "utf-8"))

while True:
    msg = clientsocket.recv(1024).decode("utf-8")
    if(msg != ''):
        print(msg)
        clientsocket.send(bytes("Message Received!", "utf-8"))

    if(msg == 'shutdown'):
        clientsocket.send(bytes("Message Received!", "utf-8"))
        print("Server Shutting Down!")
        clientsocket.close()
        server.close()
        break