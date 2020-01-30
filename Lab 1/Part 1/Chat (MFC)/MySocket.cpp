// MySocket.cpp : implementation file
//

#include "stdafx.h"
#include "chat.h"
#include "chatDlg.h"
// #include "MySocket.h"
// #include "chatDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CMySocket

CMySocket::CMySocket()
{
}

CMySocket::~CMySocket()
{
}


// Do not edit the following lines, which are needed by ClassWizard.
#if 0
BEGIN_MESSAGE_MAP(CMySocket, CAsyncSocket)
	//{{AFX_MSG_MAP(CMySocket)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()
#endif	// 0

/////////////////////////////////////////////////////////////////////////////
// CMySocket member functions

void CMySocket::SetParent(CDialog *pWnd)
{

	 // Set the member pointer 

        m_pWnd = pWnd; 


}

void CMySocket::OnAccept(int nErrorCode)
{
	 // Were there any errors? 

    if (nErrorCode == 0) 

                    ((CChatDlg*)m_pWnd)-> OnAccept(); 


}

void CMySocket::OnConnect(int nErrorCode)
{
	// Were there any errors? 

    if (nErrorCode == 0) 

                    ((CChatDlg*)m_pWnd)-> OnConnect(); 


}

void CMySocket::OnClose(int nErrorCode)
{
	 // Were there any errors? 

    if (nErrorCode == 0) 

                    ((CChatDlg*)m_pWnd)-> OnClose(); 

}

void CMySocket::OnReceive(int nErrorCode)
{
	 // Were there any errors? 

    if (nErrorCode == 0) 

                    ((CChatDlg*)m_pWnd)-> OnReceive(); 



}

void CMySocket::OnSend(int nErrorCode)
{
	// Were there any errors? 

    if (nErrorCode == 0) 

                    ((CChatDlg*)m_pWnd)-> OnSend(); 

//	m_iPort = 0;
//	m_strMessage = _T("");

}
