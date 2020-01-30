# -*- coding: utf-8 -*-
import socket
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# addr = socket.gethostname()
addr = '192.168.137.174'
server.connect((addr,1000))

msg = server.recv(1024)
print(msg.decode("utf-8"))