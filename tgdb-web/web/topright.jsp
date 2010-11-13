<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>TgDb [top_right_frame]</title>
    <link rel="stylesheet" type="text/css" href="test.css" />
  </head>
  <body>
  <%
    org.tgdb.project.projectmanager.ProjectDTO project;
    project = (org.tgdb.project.projectmanager.ProjectDTO)session.getAttribute("project.projectdto");
    
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
  %>
  <form method="post" action="Controller?workflow=begin" target="page">
  <table width="100%"  border="0" cellpadding="0" cellspacing="0">
  <tr valign="top">
    <td background="images/top_back.jpg" height="78" style="color: rgb(255, 255, 255); font-weight: bold;" align="left" valign="middle">
        TgDb beta version 0.7d.2c is still being heavily curated.<br><br>
        TOTAL MOUSE STRAINS HOSTED: 185<br>
        PARTIALLY CURATED & PUBLICLY AVAILABLE STRAINS: 18<br>
    </td>
  </tr>
  <tr>
    <td align="right" style="color: rgb(255, 255, 255);" bgcolor="#000000" height="30">
        usr:<input name="usr" maxlength="19" size="19" type="text">
        &nbsp;pwd:<input name="pwd" maxlength="19" size="19" class="logit" type="password">&nbsp;
        |&nbsp;<input id="buttonclean2" type="submit" value="login" title="login">&nbsp;&nbsp;
    </td>
  </tr>
  </table>
  </form>
  </body>
</html>
