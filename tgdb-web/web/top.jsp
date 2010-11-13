<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>TgDb : top_frame</title>
    <link rel="stylesheet" type="text/css" href="test.css" />
  </head>
  <body>
  <%
    org.tgdb.project.projectmanager.ProjectDTO project;
    project = (org.tgdb.project.projectmanager.ProjectDTO)session.getAttribute("project.projectdto");
    
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
  %>
  <form method="post" action="Controller?workflow=SearchKeywordFast" target="page">
  <table width="100%"  border="0" cellpadding="0" cellspacing="0">
  <tr valign="top">
    <td height="78px" width="500px" background="images/top_pic.jpg">&nbsp;</td>
    <td height="78px" background="images/top_back.jpg">&nbsp;</td>
  </tr>
  <tr>
    <td height="30px" bgcolor="#000000" style="color:#FFFFFF" colspan="2">
	&nbsp;&nbsp;<a target="page" href="Controller?workflow=ViewModels" class="tgdblink">transgenic mice </a>|
	<a target="page" href="Controller?workflow=ViewGenes" class="tgdblink"> transgene index </a>| search
        <input type="text" name="fast_search_key">
    </td>
  </tr>
  </table>
  </form>
  </body>
</html>
