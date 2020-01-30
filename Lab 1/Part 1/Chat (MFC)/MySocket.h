#if !defined(AFX_MYSOCKET_H__92842DD6_F3DA_4426_A6F2_83E9D3735335__INCLUDED_)
#define AFX_MYSOCKET_H__92842DD6_F3DA_4426_A6F2_83E9D3735335__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// MySocket.h : header file
//



/////////////////////////////////////////////////////////////////////////////
// CMySocket command target

class CMySocket : public CAsyncSocket
{
// Attributes
public:

// Operations
public:
	CMySocket();
	virtual ~CMySocket();

// Overrides
public:
	void SetParent(CDialog* pWnd);
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CMySocket)
	//}}AFX_VIRTUAL

	// Generated message map functions
	//{{AFX_MSG(CMySocket)
		// NOTE - the ClassWizard will add and remove member functions here.
	//}}AFX_MSG

// Implementation
protected:
	virtual void OnSend(int nErrorCode);
	virtual void OnReceive(int nErrorCode);
	virtual void OnClose(int nErrorCode);
	virtual void OnConnect(int nErrorCode);
	virtual void OnAccept(int nErrorCode);
private:
	CDialog* m_pWnd;
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_MYSOCKET_H__92842DD6_F3DA_4426_A6F2_83E9D3735335__INCLUDED_)
