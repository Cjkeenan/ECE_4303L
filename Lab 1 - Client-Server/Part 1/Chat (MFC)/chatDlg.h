// chatDlg.h : header file
//

#if !defined(AFX_CHATDLG_H__F8353C6C_7172_461A_8D4E_C2DFE48A4093__INCLUDED_)
#define AFX_CHATDLG_H__F8353C6C_7172_461A_8D4E_C2DFE48A4093__INCLUDED_

#include "MySocket.h"	// Added by ClassView
#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CChatDlg dialog

class CChatDlg : public CDialog
{
// Construction
public:
	void OnClose();
	void OnReceive();
	void OnSend();
	void OnConnect();
	void OnAccept();
	CChatDlg(CWnd* pParent = NULL);	// standard constructor
	int m_iPort;
	int m_ack;

// Dialog Data
	//{{AFX_DATA(CChatDlg)
	enum { IDD = IDD_CHAT_DIALOG };
	CButton	m_ctlConnect;
	CListBox	m_ctlSent;
	CListBox	m_ctlRecvd;
	int		m_iType;
	CString	m_strName;
//	int		m_iPort;
	CString	m_strMessage;
//	int m_ack; // added 1/10/06
	//}}AFX_DATA
//	int		m_iPort;	

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CChatDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CChatDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg void OnRType();
	afx_msg void OnBconnect();
	afx_msg void OnBclose();
	afx_msg void OnBsend();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
private:
	CMySocket m_sConnectSocket;
	CMySocket m_sListenSocket;
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_CHATDLG_H__F8353C6C_7172_461A_8D4E_C2DFE48A4093__INCLUDED_)
