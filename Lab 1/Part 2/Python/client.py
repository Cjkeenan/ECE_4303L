# -*- coding: utf-8 -*-
"""
Created on Wed Jan 29 23:31:10 2020

@author: eunsu
"""

import socket
s= socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((socket.gethostname(),1000))

msg = s.recv(1024)
print(msg.decode("uft-8"))