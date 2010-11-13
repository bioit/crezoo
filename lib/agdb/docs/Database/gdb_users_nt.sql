--
-- This script creates all DB users
--
-- This script must be run as SYSTEM
--
--  2000-03-16	ANNY	First version
--
--  2000-10-06	ROCA	changed from SysAdm/SysUser => GdbAdm/GdbUser
--
-- GdbAdm
--

drop user GdbAdm cascade ;

create user GdbAdm identified by "GdbAdm"
      default tablespace USER_DATA
      temporary tablespace TEMPORARY_DATA 
      quota unlimited on USER_DATA
      quota unlimited on TEMPORARY_DATA ;

grant Create Procedure,
      Create Sequence,
      Create Session,
      Create Public Synonym,
      Create Table,
      Create View,
      Drop Public Synonym,
      Execute Any Procedure to GdbAdm ;

--
-- GdbUser
--

drop user GdbUser cascade ;


create user GdbUser identified by "GdbUser"
      default tablespace USER_DATA
      temporary tablespace TEMPORARY_DATA 
      quota unlimited on USER_DATA
      quota unlimited on TEMPORARY_DATA ;

grant Create Session,
      Execute Any Procedure,
      Select Any Sequence,
      Delete Any Table,
      Insert Any Table,
      Lock Any Table,
      Select Any Table,
      Update Any Table to GdbUser ;



commit ;
/

