; CLW file contains information for the MFC ClassWizard

[General Info]
Version=1
LastClass=CChatDlg
LastTemplate=CAsyncSocket
NewFileInclude1=#include "stdafx.h"
NewFileInclude2=#include "chat.h"

ClassCount=4
Class1=CChatApp
Class2=CChatDlg
Class3=CAboutDlg

ResourceCount=3
Resource1=IDD_ABOUTBOX
Resource2=IDR_MAINFRAME
Class4=CMySocket
Resource3=IDD_CHAT_DIALOG

[CLS:CChatApp]
Type=0
HeaderFile=chat.h
ImplementationFile=chat.cpp
Filter=N

[CLS:CChatDlg]
Type=0
HeaderFile=chatDlg.h
ImplementationFile=chatDlg.cpp
Filter=D
BaseClass=CDialog
VirtualFilter=dWC
LastObject=IDC_ESERVPORT

[CLS:CAboutDlg]
Type=0
HeaderFile=chatDlg.h
ImplementationFile=chatDlg.cpp
Filter=D

[DLG:IDD_ABOUTBOX]
Type=1
Class=CAboutDlg
ControlCount=4
Control1=IDC_STATIC,static,1342177283
Control2=IDC_STATIC,static,1342308480
Control3=IDC_STATIC,static,1342308352
Control4=IDOK,button,1342373889

[DLG:IDD_CHAT_DIALOG]
Type=1
Class=CChatDlg
ControlCount=16
Control1=IDC_STATICTYPE,button,1342177287
Control2=IDC_RCLIENT,button,1342308361
Control3=IDC_RSERVER,button,1342177289
Control4=IDC_STATICNAME,static,1342308352
Control5=IDC_ESERVNAME,edit,1350631552
Control6=IDC_STATICPORT,static,1342308352
Control7=IDC_ESERVPORT,edit,1350631552
Control8=IDC_BCONNECT,button,1342242816
Control9=IDC_BCLOSE,button,1476460544
Control10=IDC_STATICMSG,static,1476526080
Control11=IDC_EMSG,edit,1484849280
Control12=IDC_BSEND,button,1476460545
Control13=IDC_STATIC,static,1342308352
Control14=IDC_LSENT,listbox,1352679681
Control15=IDC_STATIC,static,1342308352
Control16=IDC_LRECVD,listbox,1352679681

[CLS:CMySocket]
Type=0
HeaderFile=MySocket.h
ImplementationFile=MySocket.cpp
BaseClass=CAsyncSocket
Filter=N

