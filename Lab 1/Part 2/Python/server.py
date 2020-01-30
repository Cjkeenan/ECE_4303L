# -*- coding: utf-8 -*-
"""
Created on Wed Jan 29 23:26:54 2020

@author: eunsu
"""

import socket
s= socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostname(),1000))
s.listen(5)

while True:
    clientsocket,address =s.accept()
    print(f"Connection from {address} has ben established!")
    clientsocket.send(bytes("Welcome to the server!", "utf-8"))