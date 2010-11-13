<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="arx" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>

    <h1>Details for project '<jsp:getProperty name="project" property="name"/>'</h1>
    
    <arx:window name="adm.prj.main" state="expanded" title="Project details" workflow="AdminViewProject">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=AdminEditProject&pid=<jsp:getProperty name="project" property="pid"/>">
                <arx:img name="edit"/></a>
            </arx:hide>
        </menu>
        <body>
            <table class="block">
                <tr class="block">
                    <td class="block">
                        <b>Name</b><br> <jsp:getProperty name="project" property="name"/>
                    </td>
                    <td class="block">
                        <b>Status</b><br><jsp:getProperty name='project' property='status'/>
                    </td> 
                </tr>
                <tr>
                    <td class="block" colspan="2">
                        <b>Comment</b><br><jsp:getProperty name='project' property='comm'/>
                    </td>           
                </tr>
            </table>
        </body>
    </arx:window>
     
        
    <br>
    
    <arx:window name="admin.users" title="Users assigned to project" workflow="AdminViewProject">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=AdminAssignUser&pid=<jsp:getProperty name="project" property="pid"/>"><arx:img name="assign" title="Assign a user to this project"/></a>
            </arx:hide>
        </menu>
        <body>
            <table class="block_data">
                <tr>
                    <th class="data" width="45%">User</th>
                    <th class="data" width="45%">Role</th>
                    <arx:hide privilege="PROJECT_ADM">
                        <th class="data" width="10%">Unassign</th>
                    </arx:hide>
                </tr>
                <arx:iterate-collection collection="users">
                <tr class="data #?alt#">
                    <td width="45%"><a info="View user details" href="Controller?workflow=AdminViewUser&id=#:getId#">#:getName#</a></td>
                    <td width="45%">#:getRoleName#</td>
                    <arx:hide privilege="PROJECT_ADM">
                        <td width="10%"><a href="Controller?workflow=AdminUnAssignUser&pid=<jsp:getProperty name="project" property="pid"/>&id=#:getId#"><arx:img name="unassign" title="Unassign from project"/></a></td>
                    </arx:hide>
                </tr>
                </arx:iterate-collection>
            </table>
        </body>
    </arx:window>
    
    
    <br>
    
    <arx:window name="admin.roles" title="Roles in this project" workflow="AdminViewProject">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=AdminCreateRole&pid=<jsp:getProperty name="project" property="pid"/>"><arx:img name="add" title="Add a role to this project"/></a>
            </arx:hide>
        </menu>
        <body>
            <table class="block_data">
                <tr>
                    <th class="data" width="25%">Name</th>
                    <th class="data" width="55%">Comment</th>
                    <arx:hide privilege="PROJECT_ADM">                            
                        <th class="data" width="10%">Edit</th>
                        <th class="data" width="10%">Remove</th>
                    </arx:hide>
                </tr>
                <arx:iterate-collection collection="roles">
                <tr class="data #?alt#">
                    <td width="25%">#:getName#</td>
                    <td width="55%">#:getComm#</td>
                    <arx:hide privilege="PROJECT_ADM">
                        <td width="10%"><a href="Controller?workflow=EditRole&rid=#:getRid#"><arx:img name="edit"/></a></td>
                        <td width="10%"><a href="Controller?workflow=AdminRemoveRole&rid=#:getRid#"><arx:img name="delete" title="Delete from project"/></a></td>
                    </arx:hide>
                </tr>
                </arx:iterate-collection>
            </table>
        </body>
    </arx:window>
    
    <br>
    
    <arx:window name="admin.species" title="Species in this project" workflow="AdminViewProject">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=AdminAssignSpecies&pid=<jsp:getProperty name="project" property="pid"/>"><arx:img name="assign" title="Assign a species to this project"/></a>
            </arx:hide>
        </menu>
        <body>
            <table class="block_data">
                <tr>
                    <th class="data" width="25%">Name</th>
                    <th class="data" width="65%">Comment</th>
                    <th class="data" width="10%">Unassign</th>
                </tr>
                <arx:iterate-collection collection="species">
                <tr class="data #?alt#">
                    <td width="25%">#:getName#</td>
                    <td width="65%">#:getComm#</td>
                    <td width="10%"><a href="Controller?workflow=AdminUnAssignSpecies&pid=<jsp:getProperty name="project" property="pid"/>&sid=#:getSid#" onClick="return confirm('Unassign species?')"><arx:img name="unassign" title="Unassign from project"/></a></td>
                </tr>
                </arx:iterate-collection>
            </table>
        </body>
    </arx:window>
    
    <br>
    
    <arx:window name="admin.su" title="Sampling units in this project" workflow="AdminViewProject">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=AdminCreateSamplingUnit&pid=<jsp:getProperty name="project" property="pid"/>"><arx:img name="assign" title="Assign a sampling unit to this project"/></a>
            </arx:hide>
        </menu>
        <body>
            <table class="block_data">
                <tr>
                        <th class="data" width="25%">Name</th>
                        <th class="data" width="65%">Comment</th>
                        <th class="data" width="10%">Unassign</th>
                </tr>
                <arx:iterate-collection collection="sus">
                <tr class="data #?alt#">
                        <td width="25%">#:getName#</td>
                        <td width="65%">#:getComm#</td>
                        <td width="10%"><a href="Controller?workflow=AdminXXX"><arx:img name="unassign" title="Unassign sampling unit"/></a></td>
                </tr>
                </arx:iterate-collection>
            </table>
        </body>
    </arx:window>
    
    
    <br>
    
    <arx:window name="admin.res" title="Resource categories for project" workflow="AdminViewProject">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=AdminCreateResourceCategory&pid=<jsp:getProperty name="project" property="pid"/>"><arx:img name="add" title="Add a resource category to this project"/></a>
            </arx:hide>
        </menu>
        <body>
            <table class="block_data">
                <tr>
                    <th class="data" width="20%">Name</th>
                    <th class="data" width="10%">Resources</th>
                    <th class="data" width="50%">Comment</th>
                    <arx:hide privilege="PROJECT_ADM">
                        <th class="data" width="10%">Edit</th>
                        <th class="data" width="10%">Remove</th>
                    </arx:hide>
                </tr>
                <arx:iterate-collection collection="categories">
                <tr class="data #?alt#">
                    <td width="20%">#:getCatName#</td>
                    <td width="10%">#:getNumberOfResources#</td>
                    <td width="50%">#:getComm#</td>
                    <arx:hide privilege="PROJECT_ADM">
                        <td width="10%"><a href="Controller?workflow=AdminEditResourceCategory&catId=#:getCatId#&pid=<jsp:getProperty name="project" property="pid"/>"><arx:img name="edit"/></a></td>
                        <td width="10%"><a href="Controller?workflow=AdminRemoveResourceCategory&catId=#:getCatId#&pid=<jsp:getProperty name="project" property="pid"/>" onClick="return confirm('Remove resource category?')"><arx:img name="delete" title="Remove from project"/></a></td>
                    </arx:hide>
                </tr>
                </arx:iterate-collection>
            </table>
        </body>
    </arx:window>
    
    
    <form action="Controller" method="post">
        <br>
        <arx:back/>
    </form>
    </body>
</html>
