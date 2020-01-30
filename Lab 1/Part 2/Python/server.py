# -*- coding: utf-8 -*-
import socket
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
addr = ''
server.bind((addr,1000))
server.listen(5)

while True:
    clientsocket,address = server.accept()
    print(f"Connection from {address} has ben established!")
    clientsocket.send(bytes("Welcome to the server!", "utf-8"))