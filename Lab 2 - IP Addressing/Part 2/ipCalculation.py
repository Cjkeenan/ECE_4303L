import random

# Calculate IP Values
def ipCalc(ip):
    w1 = ip[1] * pow(2,8) + ip[0]
    w2 = ip[3] * pow(2,8) + ip[2]
    addr = ip[3] * pow(2,24) + ip[2] * pow(2,16) + ip[1] * pow(2,8) + ip[0]

    output = {
        "w1" : w1,
        "w2" : w2,
        "addr" : addr
    }

    return output

# Generate Random IPv4 address
def randomIP():
    ip0 = random.randint(0,255)
    ip1 = random.randint(0,255)
    ip2 = random.randint(0,255)
    ip3 = random.randint(0,255)
    
    return([ip0, ip1, ip2, ip3])

# Print IP Address and IP Calculations
def printIP(ip):
    print("IP:{0}".format(ip))
    print(ipCalc(ip))
    print()

while(True):
    response = input("Would you like to generate a random IP and get calculations?")
    if((response == "yes") | (response == "y")):
        printIP(randomIP())
    if((response == "close") | (response == "exit")):
        break
    else:
        ip = []
        ip.append(int(input("Please input the first IP value [0,255]: ")))
        ip.append(int(input("Please input the second IP value [0,255]: ")))
        ip.append(int(input("Please input the third IP value [0,255]: ")))
        ip.append(int(input("Please input the fourth IP value [0,255]: ")))
        # print(ip)
        printIP(ip)