
<HTML><HEAD><TITLE>Arexis GDB Login page</TITLE></HEAD>
<BODY BACKGROUND="<%=application.getInitParameter("rootPath")%>/images/dna70w.gif" 
BGCOLOR="oldlace" ALIGN = "CENTER">
<BR><BR><BR><CENTER>
<%
if (!"https".equals(request.getScheme()))
{
    out.println(application.getInitParameter("loginPath"));
    response.sendRedirect(application.getInitParameter("loginPath"));
}


%>
<H1>AREXIS</H1>
<H1>Genetic Database Tool</H1>
<H2>Version <%=application.getInitParameter("version")%></H2>
</CENTER><BR><BR><BR><BR>
<FORM ACTION="<%=application.getInitParameter("rootPath")%>/loginAction" NAME="FORM1" method="post">
<TABLE align=center BORDER="0" cellspacing=2 cellpadding=2>
<TR><TD>User:</TD><TD><INPUT NAME="uid" ></TD>
<TR><TD>Password:</TD><TD><INPUT type="password" NAME="pwd" >
</TD><TR><TD></TD><TD></TD></TR><TR><TD></TD>
<TD align=center>
<INPUT TYPE="submit" VALUE="  Login  ">
</TD></TR></TABLE></FORM><BR><BR><BR>
<CENTER><H3>Developed by Arexis AB</H3></CENTER>
<CENTER><P>For questions, please contact <a href="mailto:<%=application.getInitParameter("webmaster")%>"><%=application.getInitParameter("webmaster")%></a></CENTER>
<!-- <a href="mailto:nils.bertilsson@arexis.com">This is not a real one</a> -->
<SCRIPT type="text/javascript"><!--  document.FORM1.uid.focus()//--></script>
</BODY></HTML>
