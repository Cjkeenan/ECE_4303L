// chatDlg.cpp : implementation file
//
//  modified 1/10/06 by Dr. Tim Lin
#include "stdafx.h"
#include "chat.h"
#include "chatDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog used for App About

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CAboutDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	//{{AFX_MSG(CAboutDlg)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
	//{{AFX_DATA_INIT(CAboutDlg)
	//}}AFX_DATA_INIT
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CAboutDlg)
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
	//{{AFX_MSG_MAP(CAboutDlg)
		// No message handlers
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CChatDlg dialog

CChatDlg::CChatDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CChatDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CChatDlg)
	m_iType = -1;
	m_strName = _T("");
	m_iPort = 0;
	m_strMessage = _T("What's up?");
	
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_strMessage = _T("Hello");
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CChatDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CChatDlg)
	DDX_Control(pDX, IDC_BCONNECT, m_ctlConnect);
	DDX_Control(pDX, IDC_LSENT, m_ctlSent);
	DDX_Control(pDX, IDC_LRECVD, m_ctlRecvd);
	DDX_Radio(pDX, IDC_RCLIENT, m_iType);
	DDX_Text(pDX, IDC_ESERVNAME, m_strName);
	DDX_Text(pDX, IDC_ESERVPORT, m_iPort);
	DDX_Text(pDX, IDC_EMSG, m_strMessage);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CChatDlg, CDialog)
	//{{AFX_MSG_MAP(CChatDlg)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDC_RCLIENT, OnRType)
	ON_BN_CLICKED(IDC_BCONNECT, OnBconnect)
	ON_BN_CLICKED(IDC_BCLOSE, OnBclose)
	ON_BN_CLICKED(IDC_RSERVER, OnRType)
	ON_BN_CLICKED(IDC_BSEND, OnBsend)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CChatDlg message handlers

BOOL CChatDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		CString strAboutMenu;
		strAboutMenu.LoadString(IDS_ABOUTBOX);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon
	
	// TODO: Add extra initialization here
	m_iType=0;
	m_strName="loopback";
	m_iPort=4000;
	m_ack = 0; // 1/10/06

	// Update the controls 

            UpdateData(FALSE); 

  

            // Set the Socket dialog pointers 

            m_sConnectSocket.SetParent(this); 

            m_sListenSocket.SetParent(this); 



	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CChatDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CChatDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, (WPARAM) dc.GetSafeHdc(), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// The system calls this to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CChatDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

void CChatDlg::OnRType() 
{
	// TODO: Add your control notification handler code here
	//Sync the controls with the variables 

    UpdateData(TRUE); 

  

    //Which mode are we in? 

    if (m_iType == 0)   //Set the appropriate text on the button 

                    m_ctlConnect.SetWindowText("C&onnect"); 

    else 

                    m_ctlConnect.SetWindowText("&Listen"); 


	
}

void CChatDlg::OnBconnect() 
{
	// TODO: Add your control notification handler code here
	 //Sync the variables with the controls 

    UpdateData(TRUE); 

  

    //Disabe the connection and type controls 

    GetDlgItem(IDC_BCONNECT) -> EnableWindow(FALSE); 

    GetDlgItem(IDC_ESERVNAME) -> EnableWindow(FALSE); 

    GetDlgItem(IDC_ESERVPORT) -> EnableWindow(FALSE); 

    GetDlgItem(IDC_STATICNAME) -> EnableWindow(FALSE); 

    GetDlgItem(IDC_STATICPORT) -> EnableWindow(FALSE); 

    GetDlgItem(IDC_RCLIENT) -> EnableWindow(FALSE); 

    GetDlgItem(IDC_RSERVER) -> EnableWindow(FALSE); 

    GetDlgItem(IDC_STATICTYPE) -> EnableWindow(FALSE); 

  

    //Are we running as client or server? 
	            if(m_iType == 0) 

            { 

                            // Client, create a default socket 

                            m_sConnectSocket.Create(); 

  

                            //Open the connection to the server 

                            m_sConnectSocket.Connect(m_strName, m_iPort); 

            } 

            else 

            { 

                            // Server, create a socket bound to the port specified 

                            m_sListenSocket.Create(m_iPort); 

                            

                            //Listen for connection requests 

                            m_sListenSocket.Listen(); 

            } 




	
}

void CChatDlg::OnBclose() 
{
	// TODO: Add your control notification handler code here
	//Call the OnClose function 

    OnClose(); 


	
}

void CChatDlg::OnBsend() 
{
// TODO: Add your control notification handler code here 

  

            int iLen, iSent; 

  

            //Sync the controls with the variables 
			if (m_ack != 1)
				UpdateData(TRUE); 
			else
				m_strMessage =_T("Got you!");

  

            //Is there a message to be sent? 

            if(m_strMessage !="") 

            { 

                            //Get the length of the message 

                            iLen = m_strMessage.GetLength(); 

  

                            //Send the message 

                            iSent = m_sConnectSocket.Send(LPCTSTR(m_strMessage), iLen); 


//							m_strMessage = _T("Really sent?");
//							UpdateData (FALSE);
  

                            //Were we able to send it? 

                            if(iSent == SOCKET_ERROR) 

                            { 

                            } 

                            else 

                            { 

                                            //Add the message to the list box 

                                            m_ctlSent.AddString(m_strMessage); 

  

                                            //Sync the variable with the controls 

                                            UpdateData(FALSE); 

											// Two lines added on 9/26/03 to clear the box after sending

											m_strMessage = _T ("");

												UpdateData (FALSE);

                            } 

            } 


	
}

void CChatDlg::OnAccept()
{
	               //Accept the connection request 

                m_sListenSocket.Accept(m_sConnectSocket); 

  

                //Enable the text and message controls 

                GetDlgItem(IDC_EMSG) -> EnableWindow(TRUE); 

                GetDlgItem(IDC_BSEND) -> EnableWindow(TRUE); 

                GetDlgItem(IDC_STATICMSG) -> EnableWindow(TRUE); 



}

void CChatDlg::OnConnect()
{
	   //Enable the text and message controls 

            GetDlgItem(IDC_EMSG) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_BSEND) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_STATICMSG) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_BCLOSE) -> EnableWindow(TRUE); 



}

void CChatDlg::OnSend()
{
	m_strMessage = _T("I am here");
	UpdateData (FALSE);
}

void CChatDlg::OnReceive()
{
	char *pBuf = new char[1025]; 

            int iBufSize = 1024; 

            int iRcvd; 

			int ack = 0; 
            CString strRecvd; 

  

            //Receive the message 

            iRcvd = m_sConnectSocket.Receive(pBuf,iBufSize); 

  

            //Did we recive any thing? 

            if (iRcvd == SOCKET_ERROR) 

            { 

            } 

            else 

            { 

                            // Truncate the end of the message 

                            pBuf[iRcvd] = NULL; 

  

                            // Copu the message to a CString 

                            strRecvd = pBuf; 

  

                            //Add the message to the received list box 

                            m_ctlRecvd.AddString(strRecvd); 

							// add code to test received string

							if (!strcmp (strRecvd, "Answer me")) // 1/10/06 added
							{
								m_ack = 1;  // send acknowledgement
								OnBsend ();
							}
  

                            //Sync the variables with the controls 

                            UpdateData(FALSE); 

            } 



}

void CChatDlg::OnClose()
{
	 // Close the connected socket 

            m_sConnectSocket.Close(); 

            

            //Disable the message sending controls 

            GetDlgItem(IDC_EMSG) -> EnableWindow(FALSE); 

            GetDlgItem(IDC_BSEND) -> EnableWindow(FALSE); 

            GetDlgItem(IDC_STATICMSG) -> EnableWindow(FALSE); 

            GetDlgItem(IDC_BCLOSE) -> EnableWindow(FALSE); 

  

            //Are we running in Client mode? 

            if(m_iType == 0) 

            { 

                            //Yes, so enable the connection configuration controls 

            GetDlgItem(IDC_BCONNECT) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_ESERVNAME) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_ESERVPORT) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_STATICNAME) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_STATICPORT) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_RCLIENT) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_RSERVER) -> EnableWindow(TRUE); 

            GetDlgItem(IDC_STATICTYPE) -> EnableWindow(TRUE); 

            } 



}
