---------------------------------------------------------------
--
-- This script creates the GDBP package body
--
-- This script must be run as GdbAdm
--
--  2000-10-07	TOBJ	First version
--  2000-12-20	TOBJ	Modified Update_Filter to not
--						make any changes to the database if 
--						the new data is the same as the old.
--  2001-01-09  TOBJ	Added procs for samples.
--	2001-01-14	TOBJ	Added procedures for unified mappings
--	2001-02-08	TOBJ	Modified procs for library markers to
--						handle the columns p1 and p2.
--	2001-02-09	TOBJ	Added checks for identifying names
--						such as marker name. They can no more 
--						contains whitespces
--	2001-02-12	TOBJ	Modified the procedure check_individual
--						to also make sure that the parants are
--						enabled individuals.
--  2001-02-12	TOBJ	Modified Delete_Sampling_Unit to only 
--						remove the link from the relational 
--						table r_prj_su if the sampling unit 
--						to be deleted also is used from at least  
--						one additional project. 
--
----------------------------------------------------------------
CREATE OR REPLACE package body GDBP as

    DEBUG_LEVEL_DEBUG CONSTANT NUMBER  :=  5;
    DEBUG_LEVEL_NORM  CONSTANT NUMBER  :=  3;
    DEBUG_LEVEL_LOW   CONSTANT NUMBER  :=  1;
    DEBUG_LEVEL NUMBER  :=  DEBUG_LEVEL_DEBUG;

	NO_DATA_FOUND_MESS 		CONSTANT VARCHAR2(128) := 'Internal error (A query didn not return any data)' ;
	TOO_MANY_ROWS_MESS 		CONSTANT VARCHAR2(128) := 'Internal error (A query return more than one row)' ;
    DUP_VAL_ON_INDEX_MESS 	CONSTANT VARCHAR2(128) := 'Internal error (Duplicated value on index)' ;
    NULL_NOT_ALLOWED_MESS	CONSTANT VARCHAR2(128) := 'Internal error (At least one parameter was null, that must not be null)' ;
    CHECK_VIOLATED_MESS		CONSTANT VARCHAR2(128) := 'Internal error (A parameter was out of bounds)' ;
    INTEG_VIOLATED_MESS		CONSTANT VARCHAR2(128) := 'Internal error (An integrety check was violated)' ;
    OTHERS_MESS				CONSTANT VARCHAR2(128) := 'Internal error (Unknown error)' ;


----------------------------------------

  procedure Create_Sampling_Unit (
    p_pid     in      PROJECTS.PID%TYPE,
    p_suid    in out  SAMPLING_UNITS.SUID%TYPE,
    p_name    in      SAMPLING_UNITS.NAME%TYPE,
    p_comm	  in      SAMPLING_UNITS.COMM%TYPE,
	p_status  in	  SAMPLING_UNITS.STATUS%TYPE,
    p_sid     in      SPECIES.SID%TYPE,
    p_id      in      USERS.ID%TYPE,
    p_message in out  VARCHAR2) IS

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

	l_ok          BOOLEAN := true;
	l_sp_project  NUMBER := 0;

  	begin
      p_message := '';
      p_suid := 0;
  -- Check if this species exist and if project includes it
      begin
	    if l_ok then
		  select count(pid) into l_sp_project from R_Prj_Spc
		    where pid = p_pid and
				  sid = p_sid ;
		  if l_sp_project < 1 then
		    p_message := 'This projects does not include this species.';
			l_ok := false;
		  end if;
	    end if ;
	  exception
	    when others then
		  p_message := 'Failed to determine if this species is included in this project. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end ;
  -- Find the suid
      begin
	    if l_ok then
          select Sampling_Units_Seq.Nextval into p_suid from Dual;
		end if;
      exception
		when others
	  	  then p_message := 'Internal error (Unable to step up Sampling_Units_Seq) ' ||
		  	   			 	'ORACLE error: ' || SQLERRM ;
		  l_ok := false;
	  end ;
  -- Check the value of status
  	 if l_ok and p_status not in ('E', 'D') then
	 	p_message := 'Illegal value of status (' || p_status || ')';
		l_ok := false;
	 end if;
  -- Create the sampling unit
  	  begin
	    if l_ok then
          insert into Sampling_Units values
            (p_suid, p_name, p_comm, p_status, p_sid, p_id, SYSDATE);
		end if ;
	  exception
	    when others
		  then p_message := 'Internal error (Unable to create the sampling unit [' ||
		  		  	   	 	p_name || '] ' ||
		  	   			 	'ORACLE error: ' || SQLERRM ;

		  l_ok := false;
	  end ;
  -- Create the link to this project
      begin
	    if l_ok then
          insert into R_Prj_SU values
            	   		(p_pid, p_suid);
		end if ;
	  exception
	    when others
		  then p_message := 'Internal error (Unable to create link to this project ' ||
		  		  		  	'(pid=' || p_pid || ') ' ||
		  	   			 	'ORACLE error: ' || SQLERRM ;
			   l_ok := false;
	  end ;
    exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Update_Sampling_Unit (
    p_suid	  in	  SAMPLING_UNITS.Suid%TYPE,
    p_name	  in	  SAMPLING_UNITS.Name%TYPE,
    p_comm	  in	  SAMPLING_UNITS.Comm%TYPE,
	p_status  in	  SAMPLING_UNITS.STATUS%TYPE,
    p_id	  in	  USERS.Id%TYPE,
    p_message in out  varchar2) is

    l_suid	  SAMPLING_UNITS_LOG.Suid%TYPE;
    l_name	  SAMPLING_UNITS_LOG.Name%TYPE;
    l_comm	  SAMPLING_UNITS_LOG.Comm%TYPE;
	l_status  SAMPLING_UNITS_LOG.STATUS%TYPE;
    l_id	  SAMPLING_UNITS_LOG.ID%TYPE;
    l_ts	  SAMPLING_UNITS_LOG.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '' ;
  -- log the old data
	begin
      select suid, name, comm, status, id, ts into
		   l_suid, l_name, l_comm, l_status, l_id, l_ts
        from Sampling_Units
        where suid = p_suid;
      insert into Sampling_Units_Log values
  	        (l_suid, l_name, l_comm, l_status, l_id, l_ts);
	exception
	  when others
	    then p_message := 'Unable to log old data. ' ||
			 		   	  'ORACLE error: ' || SQLERRM ;
			 l_ok := false;
	end ;
  -- Check the new data
    if l_ok then
  	  if length(p_name) > 20 then
	    p_message := 'Name (' || p_name || ') exceeds 20 characters';
	    l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  elsif p_status not in ('E', 'D') then
	    p_message := 'Illegal value of status (' || p_status || ')';
		l_ok := false;
	  end if;
	end if;
  -- update the sampling unit

    begin
	  if l_ok then
        update Sampling_Units set
            name = p_name, comm = p_comm, status = p_status,
			id = p_id, ts = SYSDATE
          where suid = p_suid;
	  end if ;
	exception
	  when others
	    then p_message := 'Failed to update the sampling unit [' ||
			 		   	  p_name || '] ' ||
						  ' ORACLE error: ' || SQLERRM;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Delete_Sampling_Unit (
    p_pid		in      R_PRJ_SU.PID%TYPE,
    p_suid		in		SAMPLING_UNITS.SUID%TYPE,
    p_message	in out  varchar2) is

	l_temp		NUMBER;
  begin
    p_message := '';

	select count(pid) into l_temp
		   from r_prj_su
		   where suid = p_suid;
	-- If this sampling unit is part of more than
	-- pone project, it should be physicly deleted.
	-- Otherwise, we should simple remove the link
	-- to this project.
	if l_temp = 1 then
	  delete from genotypes      where suid = p_suid;
      delete from phenotypes     where suid = p_suid;
      delete from variables      where suid = p_suid;
      delete from variable_sets  where suid = p_suid;
      delete from markers        where suid = p_suid;
      delete from marker_sets    where suid = p_suid;
      delete from groupings      where suid = p_suid;
      delete from individuals    where suid = p_suid;
      delete from sampling_units where suid = p_suid;
	else
	  delete from R_PRJ_SU where
	  pid = p_pid and
	  suid = p_suid;
	end if;

  exception
    when others
      then p_message := 'Failed to delete the sampling unit. ' ||
	  	   			 	'ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Individual (
    p_iid	     in out	   INDIVIDUALS.IID%TYPE,
    p_identity	 in		   INDIVIDUALS.IDENTITY%TYPE,
    p_alias		 in		   INDIVIDUALS.ALIAS%TYPE,
    p_father	 in		   INDIVIDUALS.FATHER%TYPE,
    p_mother	 in		   INDIVIDUALS.MOTHER%TYPE,
    p_sex		 in		   INDIVIDUALS.SEX%TYPE,
    p_birth_date in		   INDIVIDUALS.BIRTH_DATE%TYPE,
	p_status	 in		   INDIVIDUALS.STATUS%TYPE,
    p_comm		 in		   INDIVIDUALS.COMM%TYPE,
    p_suid		 in		   INDIVIDUALS.SUID%TYPE,
    p_id		 in		   INDIVIDUALS.ID%TYPE,
    p_message	 in out    varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;
	l_temp NUMBER;
  begin
    p_message := '';
    p_iid := 0;
  -- Find next iid
  	begin
	  if l_ok then
        select Individuals_Seq.Nextval into p_iid from Dual;
	  end if ;
	exception
	  when others
	    then p_message := 'Failed to create individual [' || p_identity ||
			 		   	  '] (Unable to step up Individuals_Seq) ' ||
			 		   	  'ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;
  -- Check the parameters
    if l_ok then
	  if length(p_identity) > 11 then
	    p_message := 'Name exceeds 1 characters';
		l_ok := false;
	  elsif length(p_alias) > 11 then
	    p_message := 'Alias exceeds 11 characters';
		l_ok := false;
	  elsif p_sex not in ('F', 'M', 'U') then
	    p_message := 'Illegal value of sex';
		l_ok := false;
	  elsif p_status not in ('E', 'D') then
	    p_message := 'Illegal value of status';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 charcters';
		l_ok := false;
	  elsif p_father is not null then
	    select count(iid) into l_temp from individuals where
	      suid = p_suid and iid=p_father;
	    if l_temp != 1 then
	      p_message := 'Father does not exist';
		  l_ok := false;
		end if;
	  elsif p_mother is not null then
		select count(iid) into l_temp from individuals where
		  suid=p_suid and iid=p_mother;
		if l_temp != 1 then
		  p_message := 'Mother does not exist';
		  l_ok := false;
		end if;
	  end if;
	end if;

  -- Insert the new individual
    begin
	  if l_ok then
        insert into Individuals(iid, identity, alias, father, mother,
			   					sex, birth_date, suid, comm, id, ts, status)
				    values(
      		   p_iid, p_identity, p_alias, p_father, p_mother,
       		   p_sex, p_birth_date, p_suid, p_comm, p_id, SYSDATE, p_status);
	  end if ;
	exception
	  when DUP_VAL_ON_INDEX then
	    p_message := 'The individual [' || p_identity || '] already exist in this sampling unit.';
		l_ok := false;
	  when others then
	    p_message := 'Failed to create the individual [' || p_identity ||
			 		   	  '] (Unable to insert row into Individuals) ' ||
			 		   	  'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Set_Parents (
    p_suid	    in	    INDIVIDUALS.SUID%TYPE,
    p_identity	in		INDIVIDUALS.IDENTITY%TYPE,
    p_fidentity	in		INDIVIDUALS.IDENTITY%TYPE,
    p_midentity	in		INDIVIDUALS.IDENTITY%TYPE,
    p_message	in out  varchar2) is

    l_iid int;
    l_fid int;
    l_mid int;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find iid for the individual
    begin
	  if l_ok then
	    select iid into l_iid from Individuals
      	  where suid = p_suid and identity = p_identity;
	  end if ;
	exception
	  when others
	    then p_message := 'The individual [' || p_identity ||
			 		   	  '] does not exist';
			 l_ok := false;
	end ;
  -- Find the father's iid
    begin
	  if l_ok then
	    if p_fidentity is not null then
          select iid into l_fid from Individuals
            where suid = p_suid and identity = p_fidentity;
		end if ;
	  end if ;
	exception
	  when others
	    then p_message := 'Failed to find father [' || p_fidentity || '] in this sampling unit. ' ||
			 		   	  'ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;
  -- Find the mother's iid
    begin
	  if l_ok then
	    if p_midentity is not null then
          select iid into l_mid from Individuals
            where suid = p_suid and identity = p_midentity;
		end if;
	  end if;
	exception
	  when others
	    then p_message := 'Failed to find mother [' || p_midentity || '] in this sampling unit. ' ||
			 		   	  'ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;
  -- Update the table
    begin
	  if l_ok then
        update Individuals set
          father = l_fid, mother = l_mid
          where iid = l_iid;
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to update the individual [' || p_identity || '] ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

procedure Create_Or_Update_Individual (
    p_iid	     in out	   INDIVIDUALS.IID%TYPE,
    p_identity	         in	   INDIVIDUALS.IDENTITY%TYPE,
    p_alias		 in	   INDIVIDUALS.ALIAS%TYPE,
    p_father	         in        INDIVIDUALS.FATHER%TYPE,
    p_mother	         in	   INDIVIDUALS.MOTHER%TYPE,
    p_sex		 in        INDIVIDUALS.SEX%TYPE,
    p_birth_date         in	   INDIVIDUALS.BIRTH_DATE%TYPE,
    p_status	         in	   INDIVIDUALS.STATUS%TYPE,
    p_comm		 in	   INDIVIDUALS.COMM%TYPE,
    p_suid		 in	   INDIVIDUALS.SUID%TYPE,
    p_id		 in	   INDIVIDUALS.ID%TYPE,
    p_message	 in out    varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);



	l_ok   				BOOLEAN := true;
	l_identity 	 		INDIVIDUALS.IDENTITY%TYPE;
	l_temp 				NUMBER;


	begin
	p_message := '';
    p_iid := 0;
	-- Trim idenity from white spcases
	l_identity := ltrim(rtrim(p_identity) );

  -- Check the parameters
  begin
    if l_ok then
	  if instr(l_identity, ' ') > 0 then
	  	 p_message := 'Identity contains white spaces';
		 l_ok := false;
	  elsif length(l_identity) > 11 then
	    p_message := 'Name exceeds 1 characters';
		l_ok := false;
	  elsif length(p_alias) > 11 then
	    p_message := 'Alias exceeds 11 characters';
		l_ok := false;
	  elsif p_sex not in ('F', 'M', 'U') then
	    p_message := 'Illegal value of sex';
		l_ok := false;
	  elsif p_status not in ('E', 'D') then
	    p_message := 'Illegal value of status';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 charcters';
		l_ok := false;
      end if;
	end if;
  end;
  -- Is this an update or a new?

	if l_ok then
	   select count(IDENTITY) into l_temp from INDIVIDUALS where IDENTITY=l_identity AND suid = p_suid;

	   if l_temp != 1 then
		-- the individual does not exist
		Create_Individual(p_iid, l_identity, p_alias, p_father, p_mother, p_sex, p_birth_date, p_status,
                                  p_comm, p_suid, p_id, p_message);
		else
		-- the individual exists
		select IID into p_iid from INDIVIDUALS where IDENTITY=l_identity AND suid = p_suid;
		Update_Individual(p_iid, l_identity, p_alias, p_father, p_mother, p_sex, p_birth_date, p_status,
                                  p_comm, p_id, p_message);
		end if;
	end if;


    exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
end;

----------------------------------------

  procedure Update_Individual (
    p_iid	     in  	INDIVIDUALS.IID%TYPE,
    p_identity	 in		INDIVIDUALS.IDENTITY%TYPE,
    p_alias		 in		INDIVIDUALS.ALIAS%TYPE,
    p_father	 in		INDIVIDUALS.FATHER%TYPE,
    p_mother	 in		INDIVIDUALS.MOTHER%TYPE,
    p_sex		 in		INDIVIDUALS.SEX%TYPE,
    p_birth_date in		INDIVIDUALS.BIRTH_DATE%TYPE,
	p_status	 in		INDIVIDUALS.STATUS%TYPE,
    p_comm		 in		INDIVIDUALS.COMM%TYPE,
    p_id		 in		USERS.ID%TYPE,
    p_message	 in out varchar2) is

    l_new_identity INDIVIDUALS.IDENTITY%TYPE;

    l_iid	      INDIVIDUALS.IID%TYPE;
    l_identity	  INDIVIDUALS.IDENTITY%TYPE;
    l_alias		  INDIVIDUALS.ALIAS%TYPE;
    l_father	  INDIVIDUALS.FATHER%TYPE;
    l_mother	  INDIVIDUALS.MOTHER%TYPE;
    l_sex		  INDIVIDUALS.SEX%TYPE;
    l_birth_date  INDIVIDUALS.BIRTH_DATE%TYPE;
	l_status	  INDIVIDUALS.STATUS%TYPE;
    l_comm		  INDIVIDUALS.COMM%TYPE;
    l_id		  USERS.ID%TYPE;
    l_ts		  INDIVIDUALS.TS%TYPE;

	l_ok BOOLEAN := true;
	l_temp NUMBER;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
  	l_new_identity := ltrim(rtrim(p_identity));
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
        select iid, identity, alias, father, mother, sex,
    		   birth_date, status, comm, id, ts
  	   	  into l_iid, l_identity, l_alias, l_father, l_mother,
                  	l_sex, l_birth_date, l_status,
					l_comm, l_id, l_ts
      	  from Individuals
          where iid = p_iid;
        insert into individuals_Log values(
	      	 l_iid, l_identity, l_alias, l_father, l_mother,
			 l_sex, l_birth_date, l_status, l_comm, l_id, l_ts);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to log the data for [' || p_identity ||
				  	 '] ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Check the new data
    if l_ok then
	  if length(l_new_identity) > 11 then
	    p_message := 'Name exceeds 1 characters';
		l_ok := false;
	  elsif instr(l_new_identity, ' ') > 0 then
	  	p_message := 'Identity contains whitespaces';
		l_ok := false;
	  elsif length(p_alias) > 11 then
	    p_message := 'Alias exceeds 11 characters';
		l_ok := false;
	  elsif p_sex not in ('F', 'M', 'U') then
	    p_message := 'Illegal value of sex';
		l_ok := false;
	  elsif p_status not in ('E', 'D') then
	    p_message := 'Illegal value of status';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 charcters';
		l_ok := false;


	 -- new
	 l_ok := false;
	  elsif p_father is not null then
	    select count(iid) into l_temp from individuals where
	       iid=p_father;
	    if l_temp != 1 then
	      p_message := 'Father does not exist';
		  l_ok := false;
		end if;
	  elsif p_mother is not null then
		select count(iid) into l_temp from individuals where
		  iid=p_mother;
		if l_temp != 1 then
		  p_message := 'Mother does not exist';
		  l_ok := false;
		end if;
	  end if;
	end if;

	 -- old

	  /*else
	    select count(iid) into l_temp from individuals
			   where iid=p_father;
	    if l_temp != 1 then
	      p_message := 'Father does not exist';
		  l_ok := false;
		else
		  select count(iid) into l_temp from individuals
		  		 where iid=p_mother;
		  if l_temp != 1 then
		    p_message := 'Mother does not exist';
			l_ok := false;
		  end if;
		end if;
	  end if;
	end if;
	*/

  -- Update the individual
    begin
	  if l_ok then
        update Individuals set
          identity = l_new_identity, alias = p_alias, father = p_father,
          mother = p_mother, sex = p_sex, birth_date = p_birth_date,
          status = p_status, comm = p_comm, id = p_id, ts = SYSDATE
          where iid = p_iid;
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to update the individual [' || p_identity ||
				  	 '] ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Update_Individual (
    p_suid	     in		INDIVIDUALS.SUID%TYPE,
    p_identity	 in		INDIVIDUALS.IDENTITY%TYPE,
    p_alias		 in		INDIVIDUALS.ALIAS%TYPE,
    p_father	 in		INDIVIDUALS.IDENTITY%TYPE,
    p_mother	 in		INDIVIDUALS.IDENTITY%TYPE,
    p_sex		 in		INDIVIDUALS.SEX%TYPE,
    p_birth_date in		INDIVIDUALS.BIRTH_DATE%TYPE,
	p_status	 in		INDIVIDUALS.STATUS%TYPE,
    p_comm		 in		INDIVIDUALS.COMM%TYPE,
    p_id		 in		USERS.ID%TYPE,
    p_message	 in out varchar2) is

	l_new_identity INDIVIDUALS.IDENTITY%TYPE;

    l_iid	      INDIVIDUALS.IID%TYPE;
    l_identity	  INDIVIDUALS.IDENTITY%TYPE;
    l_alias		  INDIVIDUALS.ALIAS%TYPE;
    l_father	  INDIVIDUALS.FATHER%TYPE;
    l_mother	  INDIVIDUALS.MOTHER%TYPE;
    l_sex		  INDIVIDUALS.SEX%TYPE;
    l_birth_date  INDIVIDUALS.BIRTH_DATE%TYPE;
	l_status	  INDIVIDUALS.STATUS%TYPE;
    l_comm		  INDIVIDUALS.COMM%TYPE;
    l_id		  USERS.ID%TYPE;
    l_ts		  INDIVIDUALS.TS%TYPE;
	l_nfid		  INDIVIDUALS.FATHER%TYPE;
	l_nmid		  INDIVIDUALS.MOTHER%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    l_new_identity := ltrim(rtrim(p_identity));
    p_message := '';
  -- Find iid
    begin
	  if l_ok then
	    select iid into l_iid from Individuals where
		  suid = p_suid and
		  identity = l_new_identity;
	  end if;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'The individual [' || l_new_identity || '] ' ||
				 	'does not exist in this sampling unit.';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find the individual [' || l_new_identity || '] ' ||
					 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Find father identity, if any
    begin
	  if l_ok and p_father is not null and length(p_father) > 0 then
	    select iid into l_nfid from Individuals
		  Where suid = p_suid
		    and identity = p_father;
	  else
	    l_nfid := null;
	  end if;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'Father [' || p_father || '] does not exist.';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find father [' || p_father || '] ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Find mother identity, if any
    begin
	  if l_ok and p_mother is not null and length(p_mother) > 0 then
	    select iid into l_nmid from Individuals
		  Where suid = p_suid
		    and identity = p_mother;
	  else
	    l_nmid := null;
	  end if;
	exception
	  when NO_DATA_FOUND then
	  	p_message := 'The mother [' || p_mother || '] does not exist.';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find mother [' || p_mother || '] ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Log the old data
    begin
	  if l_ok then
        select identity, alias, father, mother, sex,
    		   birth_date, status, comm, id, ts
  	   	  into l_identity, l_alias, l_father, l_mother,
               l_sex, l_birth_date, l_status, l_comm, l_id, l_ts
      	  from Individuals
          where iid = l_iid;
        insert into individuals_Log values(
	      	 l_iid, l_identity, l_alias, l_father, l_mother,
			 l_sex, l_birth_date, l_status, l_comm, l_id, l_ts);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to log the data for [' || p_identity ||
				  	 '] ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the individual
    begin
	  if l_ok then
        update Individuals set
          identity = l_new_identity, alias = p_alias, father = l_nfid,
          mother = l_nmid, sex = p_sex, birth_date = p_birth_date,
		  status = p_status, comm = p_comm, id = p_id, ts = SYSDATE
          where iid = l_iid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the individual [' || p_identity ||
				  	 '] ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Delete_Individual (
    p_iid	  in	  INDIVIDUALS.IID%TYPE,
    p_message in out  varchar2) is

  begin
    p_message := '';
  -- Delete all genotype logs
    delete from genotypes_log where iid=p_iid;
  -- Delete all genotypes
    delete from genotypes where iid=p_iid;
  -- Delete all phenotype logs
    delete from phenotypes_log where iid=p_iid;
  -- Delete all phenotypes
    delete from phenotypes where iid=p_iid;
  -- Delete all r_ind_grp (delete cascade)

  -- delete all individual logs
    delete from individuals_log where iid=p_iid;
  -- Delete the individual !!!
    delete from Individuals
      where iid = p_iid;
  exception
    when others
      then p_message := 'Failed to delete the individual ' ||
	  	   			 	'ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Check_Individual (
    p_iid	  in	  INDIVIDUALS.IID%TYPE,
    p_status in out   int) is

    l_bd 	     INDIVIDUALS.BIRTH_DATE%TYPE;
    l_father  	 INDIVIDUALS.IDENTITY%TYPE;
    l_fsex	  	 INDIVIDUALS.SEX%TYPE;
	l_fstatus 	 INDIVIDUALS.STATUS%TYPE;
    l_fbd 	  	 INDIVIDUALS.BIRTH_DATE%TYPE;

    l_mother  	 INDIVIDUALS.IDENTITY%TYPE;
    l_msex	  	 INDIVIDUALS.SEX%TYPE;
	l_mstatus	 INDIVIDUALS.STATUS%TYPE;
    l_mbd 	  	 INDIVIDUALS.BIRTH_DATE%TYPE;

  begin
    p_status := 0;
    select father, mother, birth_date into l_father, l_mother, l_bd
      from Individuals
      where iid = p_iid;
    if l_father is not null then
      select sex, status, birth_date into l_fsex, l_fstatus, l_fbd
        from Individuals
        where iid = l_father;
      if l_fsex != 'M' then
        p_status := p_status + 1;
      end if;
	  if l_fstatus != 'E' then
	    p_status := p_status + 16;
	  end if;
      if l_fbd is not null and l_bd is not null and l_fbd >= l_bd then
        p_status := p_status + 2;
      end if;
    end if;

    if l_mother is not null then
      select sex, status, birth_date into l_msex, l_mstatus, l_mbd
        from Individuals
        where iid = l_mother;
      if l_msex != 'F' then
        p_status := p_status + 4;
      end if;
	  if l_mstatus != 'E' then
	    p_status := p_status + 32;
	  end if;
      if l_mbd is not null and l_bd is not null and l_mbd >= l_bd then
        p_status := p_status + 8;
      end if;
    end if;
  exception
    when others
      then p_status := 99;
  end;

----------------------------------------
  procedure Create_Grouping (
    p_gsid	    in out	GROUPINGS.GSID%TYPE,
    p_name		in		GROUPINGS.NAME%TYPE,
    p_comm		in		GROUPINGS.COMM%TYPE,
    p_suid		in		GROUPINGS.SUID%TYPE,
    p_id		in		GROUPINGS.ID%TYPE,
    p_message	in out  varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

	l_ok         BOOLEAN;
	l_temp       NUMBER;
	l_new_name	 GROUPINGS.NAME%TYPE;

  begin
    l_new_name := ltrim(rtrim(p_name));
    l_ok := true;
    p_message := '';
    p_gsid := 0;
  -- Check the data
    if l_ok then
	  if length(l_new_name) > 20 then
	    p_message := 'Name exceeds 20 charcters';
		l_ok := false;
	  elsif instr(l_new_name, ' ') > 0 then
	    p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  else
	    select count(suid) into l_temp from sampling_units where suid=p_suid;
		if l_temp != 1 then
		  p_message := 'The sampling unit does not exist';
		  l_ok := false;
		end if;
	  end if;
	end if;
  -- Create the grouping
  if l_ok then
    select Groupings_Seq.Nextval into p_gsid from Dual;
    insert into Groupings values
      (p_gsid, l_new_name, p_comm, p_suid, p_id, SYSDATE);
  end if;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

procedure Copy_Grouping (
    p_togsid	   in out	GROUPINGS.GSID%TYPE,
    p_toname	   in		GROUPINGS.NAME%TYPE,
    p_tocomm	   in		GROUPINGS.COMM%TYPE,
    p_fromgsid	   in		GROUPINGS.GSID%TYPE,
    p_id	   in		GROUPINGS.ID%TYPE,
    p_suid	   in		GROUPINGS.SUID%TYPE,
    p_message  in out  	varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;
	l_temp NUMBER;
	l_new_toname    GROUPINGS.NAME%TYPE;
  begin
    l_new_toname := ltrim(rtrim(p_toname));
    p_message := '';
    p_togsid := 0;
  -- Check the parameters
    if l_ok then
	  if length(l_new_toname) > 20 then
	    p_message := 'Name exceeds 20 characters';
		l_ok := false;
	  elsif instr(l_new_toname, ' ') > 0 then
	    p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif length(p_tocomm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  end if;
	end if;
  -- Create the new grouping
    if l_ok then
      select Groupings_Seq.Nextval into p_togsid from Dual;
      insert into Groupings values(p_togsid, l_new_toname, p_tocomm, p_suid,p_id, SYSDATE);

 -- copy all groups
    Declare
	 cursor c_groups IS
         select NAME,COMM,GID from GROUPS where GSID=p_fromgsid ;

	l_name GROUPS.NAME%TYPE;
	l_comm GROUPS.COMM%TYPE;
	l_togid  GROUPS.GID%TYPE;
    l_fromgid  GROUPS.GID%TYPE;
	l_message  varchar2(100);

   BEGIN
      open c_groups;
      LOOP
        FETCH  c_groups INTO l_name, l_comm, l_fromgid;
	IF c_groups%FOUND then
	  Copy_Group(l_togid, l_name, l_comm,p_fromgsid, l_fromgid, p_togsid,p_id,l_message);
	ELSE
	 CLOSE c_groups;
         EXIT;
       END IF;
      END LOOP;
  END;
 END IF;



  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;


  end;

----------------------------------------

  procedure Create_Or_Update_Grouping (
    p_gsid	    in out	 GROUPINGS.GSID%TYPE,
    p_name		in		 GROUPINGS.NAME%TYPE,
    p_comm		in		 GROUPINGS.COMM%TYPE,
    p_suid		in		 GROUPINGS.SUID%TYPE,
    p_id		in		 GROUPINGS.ID%TYPE,
    p_message	in out   varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN;
	l_temp NUMBER;
	l_new_name  GROUPINGS.NAME%TYPE;

  begin
    l_new_name := ltrim(rtrim(p_name));
    l_ok := true;
    p_message := '';
    p_gsid := 0;
  -- Check the data
    if l_ok then
	  if length(l_new_name) > 20 then
	    p_message := 'Name exceeds 20 charcters';
		l_ok := false;
	  elsif instr(l_new_name, ' ') > 0 then
	    p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  else
	    select count(suid) into l_temp from sampling_units where suid=p_suid;
		if l_temp != 1 then
		  p_message := 'The sampling unit does not exist';
		  l_ok := false;
		end if;
	  end if;
	end if;
  -- Is this an update or a new?
    begin
	  if l_ok then
        select gsid into p_gsid from Groupings
          where suid = p_suid
            and name = p_name;
	  end if;
    exception
      when NO_DATA_FOUND then
		null;
    end;
    if l_ok and p_gsid > 0 then
      Update_Grouping(p_gsid, l_new_name, p_comm, p_id, p_message);
    elsif l_ok then
      select Groupings_Seq.Nextval into p_gsid from Dual;
      insert into Groupings values
        (p_gsid, l_new_name, p_comm, p_suid, p_id, SYSDATE);
    end if;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Update_Grouping (
    p_gsid	  in	  GROUPINGS.GSID%TYPE,
    p_name	  in	  GROUPINGS.NAME%TYPE,
    p_comm	  in	  GROUPINGS.COMM%TYPE,
    p_id	  in	  GROUPINGS.ID%TYPE,
    p_message in out  varchar2) is

    l_gsid	  GROUPINGS.GSID%TYPE;
    l_name	  GROUPINGS.NAME%TYPE;
    l_comm	  GROUPINGS.COMM%TYPE;
    l_id	  GROUPINGS.SUID%TYPE;
    l_ts	  GROUPINGS.TS%TYPE;

	l_ok BOOLEAN := true;
	l_temp NUMBER;
	l_new_name  GROUPINGS.NAME%TYPE;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
	l_new_name := ltrim(rtrim(p_name));
  -- Check the data
    if l_ok then
	  if length(l_new_name) > 20 then
	    p_message := 'Name exceeds 20 charcters';
		l_ok := false;
	  elsif instr(l_new_name, ' ') > 0 then
	    p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  end if;
	end if;
  -- Copy the old data to the log table
    begin
	  if l_ok then
        select gsid, name, comm, id, ts
	        into l_gsid, l_name, l_comm, l_id, l_ts
            from Groupings
            where gsid = p_gsid;
        insert into Groupings_Log values
            (l_gsid, l_name, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others
	    then p_message := 'Failed to log the old data. ' ||
			 		   	  'ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;
  -- Update the table
    begin
	  if l_ok then
        update Groupings set
          name = l_new_name, comm = p_comm, id = p_id, ts = SYSDATE
          where gsid = p_gsid;
	  end if ;
	exception
	  when others
	    then p_message := 'Failed to update the grouping [' || p_name ||
			 		   	  '] ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Delete_Grouping (
    p_gsid	  in	  GROUPINGS.GSID%TYPE,
    p_message in out  varchar2) is

	CURSOR c_gids is select gid from groups where gsid = p_gsid;
	l_ok   BOOLEAN := true;
	l_gid  GROUPS.GID%TYPE;

  begin
    p_message := '';

  -- Delete all R_Ind_grp (delete cascade gid, gsid)
  -- Delete all group logs
    if l_ok then
	  open c_gids;
	  loop
	    fetch c_gids into l_gid;
		if c_gids%FOUND then
		  delete from groups_log where gid=l_gid;
		else
		  close c_gids;
		  exit;
		end if;
	  end loop;
	end if;
  -- Delete all groups
    if l_ok then
	  delete from groups where gsid = p_gsid;
	end if;
  -- Delete all grouping logs
    if l_ok then
	  delete from groupings_log where gsid = p_gsid;
	end if;
  -- Delete the grouping
    if l_ok then
      delete from Groupings where gsid = p_gsid;
	end if;
  exception
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Group (
    p_gid	   in out	GROUPS.GID%TYPE,
    p_name	   in		GROUPS.NAME%TYPE,
    p_comm	   in		GROUPS.COMM%TYPE,
    p_gsid	   in		GROUPS.GSID%TYPE,
    p_id	   in		GROUPS.ID%TYPE,
    p_message  in out  	varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;
	l_temp NUMBER;
	l_new_name GROUPS.NAME%TYPE;
  begin
    p_message := '';
    p_gid := 0;
	l_new_name := ltrim(rtrim(p_name));

  -- Check the parameters
    if l_ok then
	  if length(l_new_name) > 20 then
	    p_message := 'Name exceeds 20 characters';
		l_ok := false;
	  elsif instr(l_new_name,  ' ') > 0 then
	    p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  else
	    select count(gsid) into l_temp from groupings where gsid = p_gsid;
		if l_temp != 1 then
		  p_message := 'The grouping does not exist';
		  l_ok := false;
		end if;
	  end if;
	end if;
  -- Create the group
    if l_ok then
      select Groups_Seq.Nextval into p_gid from Dual;
      insert into Groups values(
	    p_gid, l_new_name, p_comm, p_gsid, p_id, SYSDATE);
	end if;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

procedure Copy_Group (
    p_togid	   in out	GROUPS.GID%TYPE,
    p_toname	   in		GROUPS.NAME%TYPE,
    p_tocomm	   in		GROUPS.COMM%TYPE,
    p_fromgsid	   in		GROUPS.GSID%TYPE,
    p_fromgid	   in		GROUPS.GID%TYPE,
    p_togsid	   in		GROUPS.GSID%TYPE,
    p_id	   in		GROUPS.ID%TYPE,
    p_message  in out  	varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;
	l_temp NUMBER;
	l_new_toname GROUPS.NAME%TYPE;
  begin
    p_message := '';
    p_togid := 0;
	l_new_toname := ltrim(rtrim(p_toname));

  -- Check the parameters
    if l_ok then
	  if length(l_new_toname) > 20 then
	    p_message := 'Name exceeds 20 characters';
		l_ok := false;
	  elsif instr(l_new_toname, ' ') > 0 then
	    p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif length(p_tocomm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  else
	    select count(gsid) into l_temp from groupings where gsid = p_togsid;
		if l_temp != 1 then
		  p_message := 'The grouping does not exist';
		  l_ok := false;
		end if;
	  end if;
	end if;
  -- Create the group
    if l_ok then
      select Groups_Seq.Nextval into p_togid from Dual;
      insert into Groups values(
	    p_togid, l_new_toname, p_tocomm, p_togsid, p_id, SYSDATE);
	end if;

 -- copy data (modify relation table)

    if l_ok then
    Declare
	cursor c_gid_iid IS
         select IID from R_IND_GRP where GID=p_fromgid;

	tmpIID INDIVIDUALS.IID%TYPE;

    BEGIN
      open c_gid_iid;
      LOOP
        FETCH c_gid_iid INTO tmpIID;
		IF c_gid_iid%FOUND then
	 	   INSERT INTO R_IND_GRP(IID,GID,ID,TS) VALUES(tmpIID, p_togid, p_id, sysdate);
		ELSE
	 		CLOSE c_gid_iid;
            EXIT;
        END IF;
      END LOOP;
	END;
  END IF;

  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;


  end;

----------------------------------------

  procedure Create_Or_Update_Group (
    p_gid	   in out	GROUPS.GID%TYPE,
    p_name	   in		GROUPS.NAME%TYPE,
    p_comm	   in		GROUPS.COMM%TYPE,
    p_gsid	   in		GROUPS.GSID%TYPE,
    p_id	   in		GROUPS.ID%TYPE,
    p_message  in out  	varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

	l_ok            BOOLEAN := true;
	l_temp 			NUMBER;
	l_new_name 		GROUPS.NAME%TYPE;
  begin
    p_message := '';
    p_gid := 0;
	l_new_name := ltrim(rtrim(p_name));
  -- Check the parameters
    if l_ok then
	  if length(l_new_name) > 20 then
	    p_message := 'Name exceeds 20 characters';
		l_ok := false;
	  elsif instr(l_new_name, ' ') > 0 then
	    p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  else
	    select count(gsid) into l_temp from groupings where gsid = p_gsid;
		if l_temp != 1 then
		  p_message := 'The grouping does not exist';
		  l_ok := false;
		end if;
	  end if;
	end if;
  -- Is this a new group?
    if l_ok then
      begin
        select gid into p_gid from Groups
          where gsid = p_gsid
            and name = p_name;
      exception
        when NO_DATA_FOUND
          then null;
      end;
      if p_gid > 0 then
        Update_Group(p_gid, l_new_name, p_comm, p_id, p_message);
      else
        select Groups_Seq.Nextval into p_gid from Dual;
        insert into Groups values
          (p_gid, l_new_name, p_comm, p_gsid, p_id, SYSDATE);
      end if;
	end if;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Update_Group (
    p_gid	  in	  GROUPS.GID%TYPE,
    p_name	  in	  GROUPS.NAME%TYPE,
    p_comm	  in	  GROUPS.COMM%TYPE,
    p_id	  in	  GROUPS.ID%TYPE,
    p_message in out  varchar2) is

    l_gid	      GROUPS.GID%TYPE;
    l_name		  GROUPS.NAME%TYPE;
    l_comm		  GROUPS.COMM%TYPE;
    l_id		  GROUPS.ID%TYPE;
    l_ts		  GROUPS.TS%TYPE;

	l_new_name	  GROUPS.NAME%TYPE;
	l_ok 		  BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
	l_new_name := ltrim(rtrim(p_name));
  -- Check the parameters
    if l_ok then
	  if length(l_new_name) > 20 then
	    p_message := 'Name exceeds 20 characters';
		l_ok := false;
	  elsif instr(l_new_name, ' ') > 0 then
	    p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
--	  else
--	    select count(gsid) into l_temp from groupings where gsid = p_gsid;
--		if l_temp != 1 then
--		  p_message := 'The grouping does not exist';
--		  l_ok := false;
--		end if;
	  end if;
	end if;
  -- Log the old data
    begin
      if l_ok then
        select gid, name, comm, id, ts
	      into l_gid, l_name, l_comm, l_id, l_ts
          from Groups
          where gid = p_gid;
        insert into Groups_Log values(
	      l_gid, l_name, l_comm, l_id, l_ts);
	  end if;
  	exception
	  when others
	    then p_message := 'Failed to log the old data for the group [' || p_name ||
	    		   	   	  '] ORACLE error: ' || SQLERRM;
	   					  l_ok := false;
    end ;
  -- Update the group
    begin
	  if l_ok then
        update Groups set
        name = l_new_name, comm = p_comm, id = p_id, ts = SYSDATE
        where gid = p_gid;
	  end if ;
	exception
	  when others
	    then p_message := 'Failed to update the group [' || p_name ||
			 		   	  '] ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Delete_Group (
    p_gid	    in	    GROUPS.GID%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;
  begin
    p_message := '';
  -- Delete all R_Ind_Grp (delete cascade gid)
  -- Delete all group logs
    delete from groups_log where gid = p_gid;
    delete from Groups where gid = p_gid;
  exception
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Group_Link (
    p_gsid	    in	    GROUPINGS.GSID%TYPE,
    p_identity	in		INDIVIDUALS.IDENTITY%TYPE,
    p_grp_name	in		GROUPS.NAME%TYPE,
    p_id		in		R_IND_GRP.ID%TYPE,
    p_message	in out  varchar2) is

    l_suid 	GROUPINGS.SUID%TYPE;
    l_iid 	INDIVIDUALS.IID%TYPE;
    l_gid 	GROUPS.GID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find suid
    begin
	  if l_ok then
        select suid into l_suid from Groupings
          where gsid = p_gsid;
	  end if ;
	exception
	  when others
	    then p_message := 'Grouping does not exist ' ||
			 		   	  'ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;
  -- Find iid
    begin
	  if l_ok then
        select iid into l_iid from Individuals
          where suid = l_suid and identity = p_identity;
	  end if ;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'The individual [' || p_identity ||
		       		   	  '] does not exist in this sampling unit.';
		l_ok := false;
	  when others then
	    p_message := 'Failed to retrieve identity fo individual [' || p_identity ||
		       		   	  ']. ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- check if the group already exists
    begin
	  if l_ok then
        select gid into l_gid from Groups
          where gsid = p_gsid and name = p_grp_name;
	  end if;
    exception
      when NO_DATA_FOUND then
	    if l_ok then
		  Create_Group(l_gid, p_grp_name, null,
                       p_gsid, p_id, p_message);
		end if ;
    end;
  -- Create the link
    begin
	  if l_ok then
        insert into R_Ind_Grp values(l_iid, l_gid, p_id, SYSDATE);
	  end if ;
	exception
	  when others
	    then p_message := 'Failed to create the individual - group link ' ||
			 		   	  'ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

  procedure Create_Group_Link_Thr_Alias (
    p_gsid	    in	    GROUPINGS.GSID%TYPE,
    p_alias		in		INDIVIDUALS.ALIAS%TYPE,
    p_grp_name	in		GROUPS.NAME%TYPE,
    p_id		in		R_IND_GRP.ID%TYPE,
    p_message	in out  varchar2) is

    l_identity 	INDIVIDUALS.IDENTITY%TYPE;
	l_suid		INDIVIDUALS.SUID%TYPE;

	l_ok      BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';

  -- Find suid
    begin
	  if l_ok then
        select suid into l_suid from Groupings
          where gsid = p_gsid;
	  end if ;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'The grouping does not exist';
		l_ok := false;
	  when others then
	    p_message := 'Grouping does not exist ' ||
			 		   	  'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;

  -- Find identity
    begin
	  if l_ok then
        select identity into l_identity from Individuals
          where suid = l_suid and alias = p_alias;
	  end if ;
	exception
	  when TOO_MANY_ROWS then
	  	p_message := 'There are more than one individual in this sampling unit whith ' ||
				  	 'the alias [' || p_alias || '].';
		l_ok := false;
	  when NO_DATA_FOUND then
	    p_message := 'The individual [' || p_alias ||
		       		   	  '] does not exist in this sampling unit.';
			 l_ok := false;
	  when others
	    then p_message := 'Failed to retrieve identity fo individual [' || p_alias ||
		       		   	  ']. ORACLE error: ' || SQLERRM;
			 l_ok := false;
	end ;

	if l_ok then
	  Create_Group_Link(p_gsid, l_identity, p_grp_name, p_id, p_message);
	end if;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;


----------------------------------------

  procedure Create_Group_Link (
    p_gid	    in	    GROUPS.GID%TYPE,
    p_iid		in		INDIVIDUALS.IID%TYPE,
    p_id		in		R_IND_GRP.ID%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Create the link
    begin
	  if l_ok then
        insert into R_Ind_Grp values(p_iid, p_gid, p_id, SYSDATE);
	  end if ;
	exception
	  when others then null;
	    -- It's possible that the errors was caused by the fact
		-- that this individual already is a member of the group.
		-- In this case we do not wish to enoy the user with a
		-- annoying error message. This is why we do nothing!
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Delete_Group_Link (
    p_iid	   in	   INDIVIDUALS.IID%TYPE,
    p_gid	   in	   GROUPS.GID%TYPE,
    p_message  in out  varchar2) is

  begin
    p_message := '';
    delete from R_Ind_Grp
      where iid = p_iid and gid = p_gid;
  exception
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Phenotype (
    p_suid	     in	     PHENOTYPES.SUID%TYPE,
    p_identity	 in		 INDIVIDUALS.IDENTITY%TYPE,
    p_name		 in		 VARIABLES.NAME%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) is

    l_vid VARiABLES.VID%TYPE;
    l_iid PHENOTYPES.IID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find vid for this variable
    begin
	  if l_ok then
        select vid into l_vid from Variables
          where suid = p_suid and name = p_name;
	  end if ;
	exception
	  when others then
	    p_message := 'The variable [' || p_name || '] does not exist ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Find iid for this individual
    begin
	  if l_ok then
        select iid into l_iid from Individuals
          where suid = p_suid and identity = p_identity;
	  end if ;
	exception
	  when others then
	    p_message := 'The individual [' || p_identity || '] does not exist in this sampling unit. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Insert the new phenotype
    begin
	  if l_ok then
        insert into Phenotypes values(
		  l_vid, l_iid, p_suid, p_value, p_date, p_reference, p_id, SYSDATE, p_comm);
	end if;
  exception
    when others then
	  p_message := 'Failed to create the new phenotype for variable [' || p_name || '] and ' ||
	  			   'individual [' || p_identity || '] ORACLE error: ' || SQLERRM;
	  l_ok := false;
  end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

  procedure Create_Phenotype_Thr_Alias (
    p_suid	     in	     PHENOTYPES.SUID%TYPE,
    p_alias		 in		 INDIVIDUALS.ALIAS%TYPE,
    p_name		 in		 VARIABLES.NAME%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) is

	l_identity	 INDIVIDUALS.IDENTITY%TYPE;
	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find identity for this individual
    begin
	  if l_ok then
        select identity into l_identity from Individuals
          where suid = p_suid and alias = p_alias;
	  end if ;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'The individual [' || p_alias || '] does not exist.';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find individual [' || p_alias || '] in this sampling unit. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Call Create_Phenotype
    if l_ok then
	  Create_Phenotype(p_suid, l_identity, p_name, p_value,
	  				   p_date, p_reference, p_comm, p_id, p_message);
	end if;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_Phenotype (
    p_iid		 in		 PHENOTYPES.IID%TYPE,
    p_vid		 in		 PHENOTYPES.VID%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) is

    l_vid 		 VARiABLES.VID%TYPE;
    l_iid 		 PHENOTYPES.IID%TYPE;
	l_suid		 PHENOTYPES.SUID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Check if iid exists and find suid
    begin
	  if l_ok then
        select suid into l_suid from Individuals
          where iid = p_iid ;
	  end if ;
	exception
	  when others then
	    p_message := 'The individual does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Check if vid exists
    begin
	  if l_ok then
        select vid into l_vid from Variables
          where vid = p_vid and
		  		suid = l_suid ;
	  end if ;
	exception
	  when others then
	    p_message := 'The variable does not exist for this sampling unit ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Insert the new phenotype
    begin
	  if l_ok then
        insert into Phenotypes values(
		  p_vid, p_iid, l_suid, p_value, p_date, p_reference, p_id, SYSDATE, p_comm);
	end if;
  exception
    when others then
	  p_message := 'Failed to create the new phenotype. ' ||
	  			   'ORACLE error: ' || SQLERRM;
	  l_ok := false;
  end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------


  procedure Update_Phenotype (
    p_vid	      in	  PHENOTYPES.VID%TYPE,
    p_iid		  in	  PHENOTYPES.IID%TYPE,
    p_value		  in	  PHENOTYPES.VALUE%TYPE,
    p_date		  in	  PHENOTYPES.DATE_%TYPE,
    p_reference	  in	  PHENOTYPES.REFERENCE%TYPE,
    p_comm		  in	  PHENOTYPES.COMM%TYPE,
    p_id		  in	  PHENOTYPES.ID%TYPE,
    p_message	  in out  varchar2) is

    l_vid		  PHENOTYPES_LOG.VID%TYPE;
    l_iid		  PHENOTYPES_LOG.IID%TYPE;
    l_value		  PHENOTYPES_LOG.VALUE%TYPE;
    l_date 		  PHENOTYPES_LOG.DATE_%TYPE;
    l_reference	  PHENOTYPES_LOG.REFERENCE%TYPE;
    l_comm		  PHENOTYPES_LOG.COMM%TYPE;
    l_id		  PHENOTYPES_LOG.ID%TYPE;
    l_ts		  PHENOTYPES_LOG.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
        select vid, iid, value, date_, reference, id, ts, comm
	      into l_vid, l_iid, l_value, l_date,
		  	   l_reference, l_id, l_ts, l_comm
          from Phenotypes
          where vid = p_vid
            and iid = p_iid;
       insert into Phenotypes_Log values(
	     l_vid, l_iid, l_value, l_date, l_reference, l_comm, l_id, l_ts);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to log the old data from the phenotype. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the phenotype
    begin
	  if l_ok then
        update Phenotypes set
            value = p_value,
		    date_ = p_date,
		    reference = p_reference,
            id = p_id, ts = SYSDATE,
			comm = p_comm
          where vid = p_vid
            and iid = p_iid;
	  end if ;
    exception
	  when others then
	    p_message := 'Unable to update the phenotype. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------


 procedure Create_Or_Update_Phenotype (
    p_suid	     in	     PHENOTYPES.SUID%TYPE,
    p_identity	 in		 INDIVIDUALS.IDENTITY%TYPE,
    p_vname		 in		 VARIABLES.NAME%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) is

    l_vid VARiABLES.VID%TYPE;
    l_iid PHENOTYPES.IID%TYPE;

	l_ok BOOLEAN := true;
    l_count NUMBER := 0;
    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find vid for this variable
    begin
	  if l_ok then
        select vid into l_vid from Variables
          where suid = p_suid and name = p_vname;
	  end if ;
	exception
	  when others then
	    p_message := 'The variable [' || p_vname || '] does not exist ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Find iid for this individual
    begin
	  if l_ok then
        select iid into l_iid from Individuals
          where suid = p_suid and identity = p_identity;
	  end if ;
	exception
	  when others then
	    p_message := 'The individual [' || p_identity || '] does not exist in this sampling unit. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;


  -- check if Phenotype exists
    begin
	if l_ok then
    select count(VID) into l_count from PHENOTYPES where VID= l_vid and IID = l_iid;

	 if l_count = 0 then
	  Create_Phenotype(p_suid,p_identity,p_vname,p_value,p_date,p_reference,p_comm,p_id,p_message);
	 else
	  Update_Phenotype(p_vname,p_identity,p_value,p_date,p_reference,p_comm,p_suid,p_id,p_message);
	 end if;
	end if;
	exception
    when others then
	  p_message := 'Failed to create  Or Update the new phenotype for variable [' || p_vname || '] and ' ||
	  			   'individual [' || p_identity || '] ORACLE error: ' || SQLERRM;
	  l_ok := false;
  end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

-----------------------------------
 procedure Create_Or_Update_P_Thr_Alias (
    p_suid	     in	     PHENOTYPES.SUID%TYPE,
    p_alias	 	 in		 INDIVIDUALS.ALIAS%TYPE,
    p_vname		 in		 VARIABLES.NAME%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) is

	l_identity	 INDIVIDUALS.IDENTITY%TYPE;
	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find identity
    begin
	  if l_ok then
        select identity into l_identity from Individuals
          where suid = p_suid and alias = p_alias;
	  end if ;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'The indifvidual [' || p_alias || '] does not exist in the sampling unit.';
		l_ok := false;
	  when others then
	    p_message := 'Failed to find the individual [' || p_alias || '] in this sampling unit. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
	if l_ok then
	  Create_Or_Update_Phenotype(p_suid, l_identity, p_vname, p_value,
	  							 p_date, p_reference, p_comm, p_id, p_message);
	end if;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

-----------------------------------

  procedure Update_Phenotype (
    p_vname		  in	  VARIABLES.NAME%TYPE,
    p_identity    in	  INDIVIDUALS.IDENTITY%TYPE,
    p_value		  in	  PHENOTYPES.VALUE%TYPE,
    p_date		  in	  PHENOTYPES.DATE_%TYPE,
    p_reference	  in	  PHENOTYPES.REFERENCE%TYPE,
    p_comm		  in	  PHENOTYPES.COMM%TYPE,
	p_suid		  in	  PHENOTYPES.SUID%TYPE,
    p_id		  in	  PHENOTYPES.ID%TYPE,
    p_message	  in out  varchar2) is

    l_vid		  PHENOTYPES_LOG.VID%TYPE;
    l_iid		  PHENOTYPES_LOG.IID%TYPE;
    l_value		  PHENOTYPES_LOG.VALUE%TYPE;
    l_date 		  PHENOTYPES_LOG.DATE_%TYPE;
    l_reference	  PHENOTYPES_LOG.REFERENCE%TYPE;
    l_comm		  PHENOTYPES_LOG.COMM%TYPE;
    l_id		  PHENOTYPES_LOG.ID%TYPE;
    l_ts		  PHENOTYPES_LOG.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find the vid
    begin
	  if l_ok then
	    select vid into l_vid from Variables where
		  suid = p_suid and
		  name = p_vname;
	  end if;
	exception
	  when others then
	    p_message := 'The variable [' || p_vname || '] does not exist for ' ||
				  	 'this sampling unit. ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Find the iid
    begin
	  if l_ok then
	    select iid into l_iid from Individuals where
		  suid = p_suid and
		  identity = p_identity;
	  end if;
	exception
	  when others then
	    p_message := 'The individual [' || p_identity || '] does not exist ' ||
				  	 'in this sampling unit. ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Log the old data
    begin
	  if l_ok then
        select value, date_, reference, id, ts, comm
	      into l_value, l_date,
		  	   l_reference, l_id, l_ts, l_comm
          from Phenotypes
          where vid = l_vid
            and iid = l_iid;
       insert into Phenotypes_Log values(
	     l_vid, l_iid, l_value, l_date, l_reference, l_comm, l_id, l_ts);
	  end if ;
	exception
	  when NO_DATA_FOUND then
	    -- This is most likely caused by the lack of an
		-- existing phenotype
		p_message := 'The phenotype does not exist for the individual [' ||
				  	 p_identity || '] and the variable [' || p_vname || '].';
		l_ok := false;
	  when others then
	    p_message := 'Failed to log the old data from the phenotype. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the phenotype
    begin
	  if l_ok then
        update Phenotypes set
            value = p_value,
		    date_ = p_date,
		    reference = p_reference,
            id = p_id, ts = SYSDATE,
			comm = p_comm
          where vid = l_vid
            and iid = l_iid;
	  end if ;
    exception
	  when others then
	    p_message := 'Unable to update the phenotype. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;


----------------------------------------

  procedure Update_Phenotype_Thr_Alias (
    p_vname		  in	  VARIABLES.NAME%TYPE,
    p_alias    	  in	  INDIVIDUALS.ALIAS%TYPE,
    p_value		  in	  PHENOTYPES.VALUE%TYPE,
    p_date		  in	  PHENOTYPES.DATE_%TYPE,
    p_reference	  in	  PHENOTYPES.REFERENCE%TYPE,
    p_comm		  in	  PHENOTYPES.COMM%TYPE,
	p_suid		  in	  PHENOTYPES.SUID%TYPE,
    p_id		  in	  PHENOTYPES.ID%TYPE,
    p_message	  in out  varchar2) is

	l_identity	  INDIVIDUALS.IDENTITY%TYPE;
	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find identity
    begin
	  if l_ok then
	    select identity into l_identity from Individuals where
		  suid = p_suid and
		  alias = p_alias;
	  end if;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'The individual [' || p_alias || '] does not exist in this sampling unit.';
		l_ok := false;
	  when others then
	    p_message := 'Failed to find the individual [' || p_alias || '] ' ||
				  	 'in this sampling unit. ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  if l_ok then
    Update_Phenotype(p_vname, l_identity, p_value, p_date, p_reference, p_comm, p_suid, p_id, p_message);
  end if;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;


----------------------------------------
  procedure Delete_Phenotype (
    p_vid	    in	    PHENOTYPES.VID%TYPE,
    p_iid	    in		PHENOTYPES.IID%TYPE,
    p_message	in out  varchar2) is

  -- Delete all phenotype logs

  begin
    p_message := '';
	delete from phenotypes_log where vid = p_vid and iid = p_iid;
    delete from Phenotypes where vid = p_vid and iid = p_iid;
  exception
    when others then
	  p_message := OTHERS_MESS ||
				  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Genotype (
    p_suid	     in	     GENOTYPES.SUID%TYPE,
    p_identity	 in		 INDIVIDUALS.IDENTITY%TYPE,
    p_marker	 in		 MARKERS.NAME%TYPE,
    p_a1name	 in		 ALLELES.NAME%TYPE,
    p_a2name	 in		 ALLELES.NAME%TYPE,
    p_raw1		 in		 GENOTYPES.RAW1%TYPE,
    p_raw2		 in		 GENOTYPES.RAW2%TYPE,
    p_reference	 in		 GENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 GENOTYPES.COMM%TYPE,
	p_level		 in		 GENOTYPES.LEVEL_%TYPE,
    p_id		 in		 GENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) is

    l_cid   CHROMOSOMES.CID%TYPE;
    l_mid 	MARKERS.MID%TYPE;
    l_aid1 	ALLELES.AID%TYPE;
    l_aid2 	ALLELES.AID%TYPE;
    l_iid 	INDIVIDUALS.IID%TYPE;
	l_level	GENOTYPES.LEVEL_%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find level
    begin
      if p_level is null or p_level < 0 or p_level > 10 then
	    l_level := 1;
	  else
	    l_level := p_level;
	  end if ;
	exception
	  when others then
	    l_level := 1;
	end ;
  -- Find the mid for this marker
    begin
	  if l_ok then
        select m.mid into l_mid from Markers m
          where m.suid = p_suid
            and m.name = p_marker;
	  end if;
	exception
	  when others then
	    p_message := ' The marker [' || p_marker || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Find aid1 for allele 1
    begin
	  if l_ok then
	    if p_a1name is not null then
          select aid into l_aid1
            from Alleles
            where name = p_a1name
            and mid = l_mid;
		else
		  l_aid1 := null;
		end if;
	  end if ;
    exception
      when NO_DATA_FOUND then
	    p_message := 'Allele [' || p_a1name ||
				  	 '] does not exist for marker [' || p_marker || '].';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find allele [' || p_a1name ||
				  	 '] for marker [' || p_marker || '] ' ||
					 'ORACLE error: ' || SQLERRM;
		l_ok := false;
    end;
  -- Find aid2 for allele 2
    begin
	  if l_ok then
	    if p_a2name is not null then
          select aid into l_aid2
            from Alleles
            where name = p_a2name
              and mid = l_mid;
		else
		  l_aid2 := null;
		end if;
	  end if ;
    exception
      when NO_DATA_FOUND then
	    p_message := 'Allele [' || p_a1name ||
				  	 '] does not exist for marker [' || p_marker || '].';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find allele [' || p_a2name ||
				  	 '] for marker [' || p_marker || '] ' ||
					 'ORACLE error: ' || SQLERRM;
	    l_ok := false;
    end;
  -- Find iid for the individual
    begin
	  if l_ok then
        select iid into l_iid from Individuals
          where suid = p_suid and identity = p_identity;
	  end if ;
	exception
	  when others then
	    p_message := 'Individual [' || p_identity || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Insert the new genoptype into the table
    begin
	  if l_ok then
        insert into Genotypes
		  	  (mid, iid, aid1, aid2, suid, level_, raw1, raw2, reference, id, ts, comm)
		values(l_mid, l_iid, l_aid1, l_aid2, p_suid, l_level,
		  p_raw1, p_raw2, p_reference, p_id, SYSDATE, p_comm);
  	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the new genotype. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Genotype_Through_Alias (
    p_suid	     in	     GENOTYPES.SUID%TYPE,
    p_alias		 in		 INDIVIDUALS.ALIAS%TYPE,
    p_marker	 in		 MARKERS.NAME%TYPE,
    p_a1name	 in		 ALLELES.NAME%TYPE,
    p_a2name	 in		 ALLELES.NAME%TYPE,
    p_raw1		 in		 GENOTYPES.RAW1%TYPE,
    p_raw2		 in		 GENOTYPES.RAW2%TYPE,
    p_reference	 in		 GENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 GENOTYPES.COMM%TYPE,
	p_level		 in		 GENOTYPES.LEVEL_%TYPE,
    p_id		 in		 GENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) is

    l_cid   CHROMOSOMES.CID%TYPE;
    l_mid 	MARKERS.MID%TYPE;
    l_aid1 	ALLELES.AID%TYPE;
    l_aid2 	ALLELES.AID%TYPE;
    l_iid 	INDIVIDUALS.IID%TYPE;
	l_level	GENOTYPES.LEVEL_%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find level
    begin
      if p_level is null or p_level < 0 or p_level > 10 then
	    l_level := 1;
	  else
	    l_level := p_level;
	  end if ;
	exception
	  when others then
	    l_level := 1;
	end ;
  -- Find the mid for this marker
    begin
	  if l_ok then
        select m.mid into l_mid from Markers m
          where m.suid = p_suid
            and m.name = p_marker;
	  end if;
	exception
	  when others then
	    p_message := ' The marker [' || p_marker || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Find aid1 for allele 1
    begin
	  if l_ok then
	    if p_a1name is not null then
          select aid into l_aid1
            from Alleles
            where name = p_a1name
            and mid = l_mid;
		else
		  l_aid1 := null;
		end if;
	  end if ;
    exception
      when NO_DATA_FOUND then
	    p_message := 'Allele [' || p_a1name ||
				  	 '] does not exist for marker [' || p_marker || '].';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find allele [' || p_a1name ||
				  	 '] for marker [' || p_marker || '] ' ||
					 'ORACLE error: ' || SQLERRM;
		l_ok := false;
    end;
  -- Find aid2 for allele 2
    begin
	  if l_ok then
	    if p_a2name is not null then
          select aid into l_aid2
            from Alleles
            where name = p_a2name
              and mid = l_mid;
		else
		  l_aid2 := null;
		end if;
	  end if ;
    exception
      when NO_DATA_FOUND then
	    p_message := 'Allele [' || p_a2name ||
				  	 '] does not exist for marker [' || p_marker || '].';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find allele [' || p_a2name ||
				  	 '] for marker [' || p_marker || '] ' ||
					 'ORACLE error: ' || SQLERRM;
	    l_ok := false;
    end;
  -- Find iid for the individual
    begin
	  if l_ok then
        select iid into l_iid from Individuals
          where suid = p_suid and alias = p_alias;
	  end if ;
	exception
	  when TOO_MANY_ROWS then
	    p_message := 'There are more than one individual in this sampling ' ||
				  	 'unit with the alias [' || p_alias || '].';
		l_ok := false;
	  when others then
	    p_message := 'Individual [' || p_alias || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Insert the new genoptype into the table
    begin
	  if l_ok then
        insert into Genotypes
		  	  (mid, iid, aid1, aid2, suid, level_, raw1, raw2, reference, id, ts, comm)
		values(l_mid, l_iid, l_aid1, l_aid2, p_suid, l_level,
		  p_raw1, p_raw2, p_reference, p_id, SYSDATE, p_comm);
  	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the new genotype. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Genotype (
    p_iid		 in		 GENOTYPES.IID%TYPE,
    p_mid		 in		 GENOTYPES.MID%TYPE,
    p_aid1		 in		 GENOTYPES.AID1%TYPE,
    p_aid2		 in		 GENOTYPES.AID2%TYPE,
    p_raw1		 in		 GENOTYPES.RAW1%TYPE,
    p_raw2		 in		 GENOTYPES.RAW2%TYPE,
    p_reference	 in		 GENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 GENOTYPES.COMM%TYPE,
	p_level		 in		 GENOTYPES.LEVEL_%TYPE,
    p_id		 in		 GENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) is

	l_suid	GENOTYPES.SUID%TYPE;
	l_level	GENOTYPES.LEVEL_%TYPE;
	l_temp NUMBER;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find suid
    begin
	  if l_ok then
	    select suid into l_suid from Individuals where
		  iid = p_iid;
	  end if;
	exception
	  when others then
	    p_message := 'The individual does not exist. ORACLE error: ' ||
				  	 SQLERRM;
		l_ok := false;
	end ;
  -- Find level
    begin
      if p_level is null or p_level < 0 or p_level > 10 then
	    l_level := 1;
	  else
	    l_level := p_level;
	  end if ;
	exception
	  when others then
	    l_level := 1;
	end ;
  -- Check the allele id's
    begin
	  if l_ok and p_aid1 is not null then
	    select count(aid) into l_temp
		  from alleles where
		  aid = p_aid1 and
		  mid = p_mid;
		if l_temp <> 1 then
		  p_message := 'The allele 1 does not exist. ORACLE error: ' || SQLERRM;
		  l_ok := false;
		end if;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to check if the allele 1 exists. ORALCE error: ' ||
		 		  	 SQLERRM;
					 l_ok := false;
	end;
    begin
	  if l_ok and p_aid2 is not null then
	    select count(aid) into l_temp
		  from alleles where
		  aid = p_aid2 and
		  mid = p_mid ;
		if l_temp <> 1 then
		  p_message := 'The allele 2 does not exist. ORACLE error: ' || SQLERRM;
		  l_ok := false;
		end if;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to check if the allele 2 exists. ORALCE error: ' ||
		 		  	 SQLERRM;
					 l_ok := false;
	end;
  -- Insert the new genoptype into the table
    begin
	  if l_ok then
        insert into Genotypes values(
		  p_mid, p_iid, p_aid1, p_aid2, l_suid, l_level,
		  p_raw1, p_raw2, p_reference, p_id, SYSDATE, p_comm);
  	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the new genotype. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_Or_Update_Genotype (
    p_suid	     in	     GENOTYPES.SUID%TYPE,
    p_identity	 in		 INDIVIDUALS.IDENTITY%TYPE,
    p_marker	 in		 MARKERS.NAME%TYPE,
    p_a1name	 in		 ALLELES.NAME%TYPE,
    p_a2name	 in		 ALLELES.NAME%TYPE,
    p_raw1		 in		 GENOTYPES.RAW1%TYPE,
    p_raw2		 in		 GENOTYPES.RAW2%TYPE,
    p_reference	 in		 GENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 GENOTYPES.COMM%TYPE,
	p_level		 in		 GENOTYPES.LEVEL_%TYPE,
    p_id		 in		 GENOTYPES.ID%TYPE,
	p_diff		 out	 int,
	p_old_a1name out	 ALLELES.NAME%TYPE,
	p_old_a2name out	 ALLELES.NAME%TYPE,
	p_old_raw1	 out	 GENOTYPES.RAW1%TYPE,
	p_old_raw2	 out	 GENOTYPES.RAW2%TYPE,
	p_old_ref	 out	 GENOTYPES.REFERENCE%TYPE,
	p_old_comm	 out	 GENOTYPES.COMM%TYPE,
	p_old_level	 out	 GENOTYPES.LEVEL_%TYPE,
	p_old_usr	 out	 USERS.USR%TYPE,
    p_message	 in out  varchar2) is

    l_mid 	MARKERS.MID%TYPE;
    l_iid 	INDIVIDUALS.IID%TYPE;
	l_level	GENOTYPES.LEVEL_%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
	p_old_a1name := '';
	p_old_a2name := '';
	p_old_raw1 := '';
	p_old_raw2 := '';
	p_old_ref := '';
	p_old_comm := '';
	p_old_level := 0;
	p_old_usr := '';
  -- Find level
    begin
      if p_level is null or p_level < 0 or p_level > 10 then
	    l_level := 1;
	  else
	    l_level := p_level;
	  end if ;
	exception
	  when others then
	    l_level := 1;
	end ;
  -- Find the mid for this marker
    begin
	  if l_ok then
        select m.mid into l_mid from Markers m
          where m.suid = p_suid
            and m.name = p_marker;
	  end if;
	exception
	  when others then
	    p_message := ' The marker [' || p_marker || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Find iid for the individual
    begin
	  if l_ok then
        select iid into l_iid from Individuals
          where suid = p_suid and identity = p_identity;
	  end if ;
	exception
	  when others then
	    p_message := 'Individual [' || p_identity || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Determine whether this genotype exist or not
    begin
	  if l_ok then
	    select a1name, a2name, raw1, raw2, reference, comm, level_, usr
		into p_old_a1name, p_old_a2name, p_old_raw1, p_old_raw2, p_old_ref,
			 p_old_comm, p_old_level, p_old_usr
		from v_genotypes_3
		where
			 mid=l_mid and
			 iid=l_iid ;
	  end if;
	exception
	  when NO_DATA_FOUND then
	    -- The genotype didn't exist!!!
		p_diff := 0;
		Create_Genotype(p_suid, p_identity, p_marker, p_a1name, p_a2name,
						p_raw1, p_raw2, p_reference, p_comm, l_level, p_id, p_message);
		-- Just to prevent the code belove to be executed
		l_ok := false;
	end;

  --
  -- The code belove will only be executed if the genotype already existed.
  -- If it didn't exist we have alreade set l_ok := false, which prevents
  -- any futher execution.
  --
    begin
      if l_ok then
	--    if p_a1name != p_old_a1name and
	--	   p_a1name != p_old_a2name then
		   -- The new data differs from the old
		   -- ==> we better not update this
	--	   p_diff := 1;
	--	   l_ok := false;
	--	else
	--	  if p_a2name != p_old_a1name and
	--	   p_a2name != p_old_a2name then
		   -- The new data differs from the old
		   -- ==> we better not update this
	--	   p_diff := 1;
	--	   l_ok := false;

	--	   else

	-- We've got a match! The previous stored
	-- genotype has an identical allele constalation.
	-- We update the genotype to store this information,
	-- that is, the log will show that this value has been
	-- measured more than once!
	p_diff := 0;


	-- The alleles are here the same as before, but if they now come
	---in different order we "sort" them back the way they were.
	if p_a1name != p_old_a1name then


		Update_Genotype(p_identity, p_marker, p_suid, p_a2name, p_a1name,
				  p_raw1, p_raw2, p_reference, p_comm, l_level,
						  p_id, p_message);

	-- here note the order of p_a1name and p_a2name
	 else
		Update_Genotype(p_identity, p_marker, p_suid, p_a1name, p_a2name,
		  				  p_raw1, p_raw2, p_reference, p_comm, l_level,
						  p_id, p_message);

	end if;

	--end if;
	--end if;
	  end if;
	exception
	  when others then
	    l_ok := false;
	    p_message := 'Failed to update the  genotype with the same allele values ' ||
				  	 ' for individual [' || p_identity || '] and marker [' ||
					 p_marker || '] ORACLE error: ' || SQLERRM;
	end;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
 procedure Update_Genotype_File (
    p_suid	     in	     GENOTYPES.SUID%TYPE,
    p_identity	 in		 INDIVIDUALS.IDENTITY%TYPE,
    p_marker	 in		 MARKERS.NAME%TYPE,
    p_a1name	 in		 ALLELES.NAME%TYPE,
    p_a2name	 in		 ALLELES.NAME%TYPE,
    p_raw1		 in		 GENOTYPES.RAW1%TYPE,
    p_raw2		 in		 GENOTYPES.RAW2%TYPE,
    p_reference	 in		 GENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 GENOTYPES.COMM%TYPE,
	p_level		 in		 GENOTYPES.LEVEL_%TYPE,
    p_id		 in		 GENOTYPES.ID%TYPE,
	p_diff		 out	 int,
	p_old_a1name out	 ALLELES.NAME%TYPE,
	p_old_a2name out	 ALLELES.NAME%TYPE,
	p_old_raw1	 out	 GENOTYPES.RAW1%TYPE,
	p_old_raw2	 out	 GENOTYPES.RAW2%TYPE,
	p_old_ref	 out	 GENOTYPES.REFERENCE%TYPE,
	p_old_comm	 out	 GENOTYPES.COMM%TYPE,
	p_old_level	 out	 GENOTYPES.LEVEL_%TYPE,
	p_old_usr	 out	 USERS.USR%TYPE,
    p_message	 in out  varchar2) is

    l_mid 	MARKERS.MID%TYPE;
    l_iid 	INDIVIDUALS.IID%TYPE;
	l_level	GENOTYPES.LEVEL_%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
	p_old_a1name := '';
	p_old_a2name := '';
	p_old_raw1 := '';
	p_old_raw2 := '';
	p_old_ref := '';
	p_old_comm := '';
	p_old_level := 0;
	p_old_usr := '';
  -- Find level
    begin
      if p_level is null or p_level < 0 or p_level > 10 then
	    l_level := 1;
	  else
	    l_level := p_level;
	  end if ;
	exception
	  when others then
	    l_level := 1;
	end ;
  -- Find the mid for this marker
    begin
	  if l_ok then
        select m.mid into l_mid from Markers m
          where m.suid = p_suid
            and m.name = p_marker;
	  end if;
	exception
	  when others then
	    p_message := ' The marker [' || p_marker || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Find iid for the individual
    begin
	  if l_ok then
        select iid into l_iid from Individuals
          where suid = p_suid and identity = p_identity;
	  end if ;
	exception
	  when others then
	    p_message := 'Individual [' || p_identity || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Determine whether this genotype exist or not
    begin
	  if l_ok then
	    select a1name, a2name, raw1, raw2, reference, comm, level_, usr
		into p_old_a1name, p_old_a2name, p_old_raw1, p_old_raw2, p_old_ref,
			 p_old_comm, p_old_level, p_old_usr
		from v_genotypes_3
		where
			 mid=l_mid and
			 iid=l_iid ;
	  end if;
	exception
	  when NO_DATA_FOUND then
	    -- The genotype didn't exist!!! = > Error
		    p_message := 'The genotype for individual [' ||
				  	 p_identity || '] and marker [' ||
					 p_marker || '] does not exist.';
		-- Just to prevent the code belove to be executed
		l_ok := false;
	end;

  --
  -- The code below will only be executed if the genotype already existed.
  -- If it didn't exist we have alreade set l_ok := false, which prevents
  -- any futher execution.
  --
    begin
      if l_ok then
	  --  if p_a1name != p_old_a1name and
	--	   p_a1name != p_old_a2name then
		   -- The new data differs from the old
		   -- ==> we better not update this
	--	   p_diff := 1;
	--	   l_ok := false;
	--	else
	--	  if p_a2name != p_old_a1name and
	--	   p_a2name != p_old_a2name then
		   -- The new data differs from the old
		   -- ==> we better not update this
	--	   p_diff := 1;
	--	   l_ok := false;

	---	   else

		  -- We've got a match! The previous stored
		  -- genotype has an identical allele constalation.
		  -- We update the genotype to store this information,
		  -- that is, the log will show that this value has been
		  -- measured more than ones!
	 p_diff := 0;


		  -- The alleles are here the same as before, but if they now come
		  ---in different order we "sort" them back the way they were.
	if p_a1name != p_old_a1name then


		Update_Genotype(p_identity, p_marker, p_suid, p_a2name, p_a1name,
 				  p_raw1, p_raw2, p_reference, p_comm, l_level,
				 p_id, p_message);

	-- here note the order of p_a1name and p_a2name
	else
	 	Update_Genotype(p_identity, p_marker, p_suid, p_a1name, p_a2name,
		  				  p_raw1, p_raw2, p_reference, p_comm, l_level,
						  p_id, p_message);

		  end if;
--
--		 end if;
--	   end if;
	  end if;
	exception
	  when others then
	    l_ok := false;
	    p_message := 'Failed to update the  genotype with the same allele values ' ||
				  	 ' for individual [' || p_identity || '] and marker [' ||
					 p_marker || '] ORACLE error: ' || SQLERRM;
	end;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Or_Update_G_Thr_Alias (
    p_suid	     in	     GENOTYPES.SUID%TYPE,
    p_alias		 in		 INDIVIDUALS.ALIAS%TYPE,
    p_marker	 in		 MARKERS.NAME%TYPE,
    p_a1name	 in		 ALLELES.NAME%TYPE,
    p_a2name	 in		 ALLELES.NAME%TYPE,
    p_raw1		 in		 GENOTYPES.RAW1%TYPE,
    p_raw2		 in		 GENOTYPES.RAW2%TYPE,
    p_reference	 in		 GENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 GENOTYPES.COMM%TYPE,
	p_level		 in		 GENOTYPES.LEVEL_%TYPE,
    p_id		 in		 GENOTYPES.ID%TYPE,
	p_diff		 out	 int,
	p_old_a1name out	 ALLELES.NAME%TYPE,
	p_old_a2name out	 ALLELES.NAME%TYPE,
	p_old_raw1	 out	 GENOTYPES.RAW1%TYPE,
	p_old_raw2	 out	 GENOTYPES.RAW2%TYPE,
	p_old_ref	 out	 GENOTYPES.REFERENCE%TYPE,
	p_old_comm	 out	 GENOTYPES.COMM%TYPE,
	p_old_level	 out	 GENOTYPES.LEVEL_%TYPE,
	p_old_usr	 out	 USERS.USR%TYPE,
    p_message	 in out  varchar2) is

    l_identity 	INDIVIDUALS.IDENTITY%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find iid for the individual
    begin
	  if l_ok then
        select identity into l_identity from Individuals
          where suid = p_suid and alias = p_alias;
	  end if ;
	exception
	  when TOO_MANY_ROWS then
	    p_message := 'There are more than one individual in this sampling unit ' ||
				  	 'with alias [' || p_alias || '].';
		l_ok := false;
	  when others then
	    p_message := 'Individual [' || p_alias || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;

    begin
      if l_ok then
	    Create_Or_Update_Genotype(p_suid, l_identity, p_marker, p_a1name, p_a2name,
								  p_raw1, p_raw2, p_reference, p_comm, p_level, p_id,
								  p_diff, p_old_a1name, p_old_a2name, p_old_raw1, p_old_raw2,
								  p_old_ref, p_old_comm, p_old_level, p_old_usr, p_message);
	  end if;
	exception
	  when others then
	    l_ok := false;
	    p_message := 'Failed to call PL/SQL procedure CREATE_OR_UPDATE_GENOTYPE(...) for ' ||
				  	 'identity [' || l_identity || '] and marker [' ||
					 p_marker || '] ORACLE error: ' || SQLERRM;
	end;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------


  procedure Update_Genotype (
    p_mid	        in	       GENOTYPES.MID%TYPE,
    p_iid			in		   GENOTYPES.IID%TYPE,
    p_a1name		in		   ALLELES.NAME%TYPE,
    p_a2name		in		   ALLELES.NAME%TYPE,
    p_raw1			in		   GENOTYPES.RAW1%TYPE,
    p_raw2			in		   GENOTYPES.RAW2%TYPE,
    p_reference		in		   GENOTYPES.REFERENCE%TYPE,
    p_comm			in		   GENOTYPES.COMM%TYPE,
	p_level			in		   GENOTYPES.LEVEL_%TYPE,
    p_id			in		   GENOTYPES.ID%TYPE,
    p_message		in out     varchar2) is


    l_mid	        GENOTYPES.MID%TYPE;
    l_iid			GENOTYPES.IID%TYPE;
    l_aid1o			GENOTYPES.AID1%TYPE;
    l_aid2o			GENOTYPES.AID2%TYPE;
    l_aid1n			GENOTYPES.AID1%TYPE;
    l_aid2n			GENOTYPES.AID2%TYPE;
    l_raw1			GENOTYPES.RAW1%TYPE;
    l_raw2			GENOTYPES.RAW2%TYPE;
    l_reference		GENOTYPES.REFERENCE%TYPE;
    l_comm			GENOTYPES.COMM%TYPE;
	l_olevel		GENOTYPES.LEVEL_%TYPE;
	l_nlevel		GENOTYPES.LEVEL_%TYPE;
    l_id			GENOTYPES.ID%TYPE;
    l_ts			GENOTYPES.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Check the level parameter
    begin
      if p_level is null or p_level < 0 or p_level > 10 then
  	    l_nlevel := 1; -- Temporary solution
  	  else
  	    l_nlevel := p_level;
	  end if;
	exception
	  when others then
	    l_nlevel := 1;
	end ;
  -- Log the old data
    begin
	  if l_ok then
        select mid, iid, aid1, aid2, level_, raw1, raw2, reference, id, ts, comm
	      into l_mid, l_iid, l_aid1o, l_aid2o, l_olevel,
	  	       l_raw1, l_raw2, l_reference, l_id, l_ts, l_comm
          from Genotypes
          where mid = p_mid
            and iid = p_iid;
        insert into Genotypes_Log values(
		  l_mid, l_iid, l_aid1o, l_aid2o, l_olevel,
		  l_raw1, l_raw2, l_reference, l_comm, l_id, l_ts);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Find aid for allele 1
    begin
	  if l_ok then
	    if p_a1name is not null then
          select aid into l_aid1n
            from Alleles
            where name = p_a1name
               and mid = l_mid;
		else
		  l_aid1n := null;
		end if;
	  end if;
    exception
      when NO_DATA_FOUND then
	    p_message := 'Allele [' || p_a1name ||
				  	 '] does not exist.';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find the allele 1 [' || p_a1name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
    end;
  -- Find aid for allele 2
    begin
	  if l_ok then
	    if p_a2name is not null then
          select aid into l_aid2n
            from Alleles
            where name = p_a2name
               and mid = l_mid;
		else
		  l_aid2n := null;
		end if;
	  end if;
    exception
      when NO_DATA_FOUND then
	    p_message := 'Allele [' || p_a2name ||
				  	 '] does not exist.';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find the allele 2 [' || p_a1name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
    end;
  -- Update the table
    begin
	  if l_ok then
        update Genotypes set
          aid1 = l_aid1n, aid2 = l_aid2n, level_ = l_nlevel, raw1 = p_raw1,
		  raw2 = p_raw2, reference = p_reference, id = p_id, ts = SYSDATE,
		  comm = p_comm
          where mid = p_mid
            and iid = p_iid;
	  end if ; -- End of if l_ok then
	exception
	  when others then
	    p_message := 'Failed to update the genotypes. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Update_Genotype (
    p_mid	        in	       GENOTYPES.MID%TYPE,
    p_iid			in		   GENOTYPES.IID%TYPE,
    p_aid1			in		   GENOTYPES.AID1%TYPE,
    p_aid2			in		   GENOTYPES.AID2%TYPE,
    p_raw1			in		   GENOTYPES.RAW1%TYPE,
    p_raw2			in		   GENOTYPES.RAW2%TYPE,
    p_reference		in		   GENOTYPES.REFERENCE%TYPE,
    p_comm			in		   GENOTYPES.COMM%TYPE,
	p_level			in		   GENOTYPES.LEVEL_%TYPE,
    p_id			in		   GENOTYPES.ID%TYPE,
    p_message		in out     varchar2) is

    l_aid1o			GENOTYPES.AID1%TYPE;
    l_aid2o			GENOTYPES.AID2%TYPE;
    l_raw1			GENOTYPES.RAW1%TYPE;
    l_raw2			GENOTYPES.RAW2%TYPE;
    l_reference		GENOTYPES.REFERENCE%TYPE;
    l_comm			GENOTYPES.COMM%TYPE;
	l_olevel		GENOTYPES.LEVEL_%TYPE;
	l_nlevel		GENOTYPES.LEVEL_%TYPE;
    l_id			GENOTYPES.ID%TYPE;
    l_ts			GENOTYPES.TS%TYPE;

	l_temp			NUMBER;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Check the level parameter
    begin
      if p_level is null or p_level < 0 or p_level > 10 then
  	    l_nlevel := 1; -- Temporary solution
  	  else
  	    l_nlevel := p_level;
	  end if;
	exception
	  when others then
	    l_nlevel := 1;
	end ;
  -- Check alleles
    begin
	  if l_ok and p_aid1 is not null then
  	    select count(aid) into l_temp from Alleles where
	      aid = p_aid1 and
		  mid = p_mid;
	    if l_temp <> 1 then
		  p_message := 'The allele 1 does not exist.';
		  l_ok := false;
		end if;
	  end if;
	  if l_ok and p_aid2 is not null then
	    select count(aid) into l_temp from Alleles where
		  aid = p_aid2 and
		  mid = p_mid;
		if l_temp <> 1 then
		   p_message := 'The allele 2 does not exist.';
		   l_ok := false;
		end if;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to check the alleles. ORACLE error: ' ||
				  	 SQLERRM;
		l_ok := false;
	end ;
  -- Log the old data
    begin
	  if l_ok then
        select aid1, aid2, level_, raw1, raw2, reference, id, ts, comm
	      into l_aid1o, l_aid2o, l_olevel,
	  	       l_raw1, l_raw2, l_reference, l_id, l_ts, l_comm
          from Genotypes
          where mid = p_mid
            and iid = p_iid;
        insert into Genotypes_Log values(
		  p_mid, p_iid, l_aid1o, l_aid2o, l_olevel,
		  l_raw1, l_raw2, l_reference, l_comm, l_id, l_ts);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the table
    begin
	  if l_ok then
        update Genotypes set
          aid1 = p_aid1, aid2 = p_aid2, level_ = l_nlevel, raw1 = p_raw1,
		  raw2 = p_raw2, reference = p_reference, id = p_id, ts = SYSDATE,
		  comm = p_comm
          where mid = p_mid
            and iid = p_iid;
	  end if ; -- End of if l_ok then
	exception
	  when others then
	    p_message := 'Failed to update the genotypes. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Update_Genotype (
    p_identity		in		   INDIVIDUALS.IDENTITY%TYPE,
	p_marker		in		   MARKERS.NAME%TYPE,
	p_suid			in		   GENOTYPES.SUID%TYPE,
    p_a1name		in		   ALLELES.NAME%TYPE,
    p_a2name		in		   ALLELES.NAME%TYPE,
    p_raw1			in		   GENOTYPES.RAW1%TYPE,
    p_raw2			in		   GENOTYPES.RAW2%TYPE,
    p_reference		in		   GENOTYPES.REFERENCE%TYPE,
    p_comm			in		   GENOTYPES.COMM%TYPE,
	p_level			in		   GENOTYPES.LEVEL_%TYPE,
    p_id			in		   GENOTYPES.ID%TYPE,
    p_message		in out     varchar2) is

    l_mid	        GENOTYPES.MID%TYPE;
    l_iid			GENOTYPES.IID%TYPE;
    l_aid1o			GENOTYPES.AID1%TYPE;
    l_aid2o			GENOTYPES.AID2%TYPE;
    l_aid1n			GENOTYPES.AID1%TYPE;
    l_aid2n			GENOTYPES.AID2%TYPE;
    l_raw1			GENOTYPES.RAW1%TYPE;
    l_raw2			GENOTYPES.RAW2%TYPE;
    l_reference		GENOTYPES.REFERENCE%TYPE;
    l_comm			GENOTYPES.COMM%TYPE;
	l_olevel		GENOTYPES.LEVEL_%TYPE;
	l_nlevel		GENOTYPES.LEVEL_%TYPE;
    l_id			GENOTYPES.ID%TYPE;
    l_ts			GENOTYPES.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Check the level parameter
    begin
      if p_level is null or p_level < 0 or p_level > 10 then
	    l_nlevel := 1; -- Temporary solution
	  else
	    l_nlevel := p_level;
	  end if;
	exception
	  when others then
	    l_nlevel := 1;
	end;
  -- Find iid
    begin
	  if l_ok then
	    select iid into l_iid from Individuals where
	      identity = p_identity and
		  suid = p_suid;
	  end if;
	exception
	  when others then
	    p_message := 'The identity [' || p_identity || '] does not exist in this ' ||
				  	 'sampling unit. ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Find mid
    begin
	  if l_ok then
	    select mid into l_mid from Markers where
		  suid = p_suid and
		  name = p_marker;
	  end if;
	exception
	  when others then
	    p_message := 'The marker [' || p_marker || '] does not exist for this ' ||
				  	 'sampling unit. ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Log the old data
    begin
	  if l_ok then
        select aid1, aid2, level_, raw1, raw2, reference, id, ts, comm
	      into l_aid1o, l_aid2o, l_olevel,
	  	       l_raw1, l_raw2, l_reference, l_id, l_ts, l_comm
          from Genotypes
          where mid = l_mid
            and iid = l_iid;
        insert into Genotypes_Log values(
		  l_mid, l_iid, l_aid1o, l_aid2o, l_olevel,
		  l_raw1, l_raw2, l_reference, l_comm, l_id, l_ts);
	  end if ;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'The genotype for individual [' ||
				  	 p_identity || '] and marker [' ||
					 p_marker || '] does not exist.';
		l_ok := false;
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Find aid for allele 1
    begin
	  if l_ok then
	    if p_a1name is not null then
          select aid into l_aid1n
            from Alleles
            where name = p_a1name
              and mid = l_mid;
		else
		  l_aid1n := null;
		end if;
	  end if;
    exception
      when NO_DATA_FOUND then
	    p_message := 'Allele [' || p_a1name ||
				  	 '] does not exist for the marker [' || p_marker || '].';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find the allele 1 [' || p_a1name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
    end;
  -- Find aid for allele 2
    begin
	  if l_ok then
	    if p_a2name is not null then
          select aid into l_aid2n
            from Alleles
            where name = p_a2name
              and mid = l_mid;
		else
		  l_aid2n := null;
		end if;
	  end if;
    exception
      when NO_DATA_FOUND then
	    p_message := 'Allele [' || p_a2name ||
				  	 '] does not exist for marker [' || p_marker || '].';
		l_ok := false;
	  when others then
	    p_message := 'Unable to find the allele 2 [' || p_a2name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
    end;
  -- Update the table
    begin
	  if l_ok then
        update Genotypes set
          aid1 = l_aid1n, aid2 = l_aid2n, level_ = l_nlevel, raw1 = p_raw1,
		  raw2 = p_raw2, reference = p_reference, id = p_id, ts = SYSDATE,
		  comm = p_comm
          where mid = l_mid
            and iid = l_iid;
	  end if ; -- End of if l_ok then
	exception
	  when others then
	    p_message := 'Failed to update the genotypes. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;


  procedure Update_Genotype_Through_Alias (
    p_alias			in		   INDIVIDUALS.ALIAS%TYPE,
	p_marker		in		   MARKERS.NAME%TYPE,
	p_suid			in		   GENOTYPES.SUID%TYPE,
    p_a1name		in		   ALLELES.NAME%TYPE,
    p_a2name		in		   ALLELES.NAME%TYPE,
    p_raw1			in		   GENOTYPES.RAW1%TYPE,
    p_raw2			in		   GENOTYPES.RAW2%TYPE,
    p_reference		in		   GENOTYPES.REFERENCE%TYPE,
    p_comm			in		   GENOTYPES.COMM%TYPE,
	p_level			in		   GENOTYPES.LEVEL_%TYPE,
    p_id			in		   GENOTYPES.ID%TYPE,
    p_message		in out     varchar2) is

    l_identity		INDIVIDUALS.IDENTITY%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find identity
    begin
	  if l_ok then
	    select identity into l_identity from Individuals where
	      alias = p_alias and
		  suid = p_suid;
	  end if;
	exception
	  when TOO_MANY_ROWS then
	    p_message := 'There are more than one individual in this samplinhg unit ' ||
				  	 'widt alias [' || p_alias || '].';
		l_ok := false;
	  when others then
	    p_message := 'The alias [' || p_alias || '] does not exist in this ' ||
				  	 'sampling unit. ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;

    begin
	  if l_ok then
	    Update_Genotype(l_identity, p_marker, p_suid, p_a1name, p_a2name,
						p_raw1, p_raw2, p_reference, p_comm, p_level,
						p_id, p_message);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to call UPDATE_GENOTYPES(...) for the individual with ' ||
				  	 'alias [' || p_alias || '] and marker [' || p_marker || '] ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;


----------------------------------------
procedure Update_G_Thr_Ali_File (
    p_suid	     in	     GENOTYPES.SUID%TYPE,
    p_alias		 in		 INDIVIDUALS.ALIAS%TYPE,
    p_marker	 in		 MARKERS.NAME%TYPE,
    p_a1name	 in		 ALLELES.NAME%TYPE,
    p_a2name	 in		 ALLELES.NAME%TYPE,
    p_raw1		 in		 GENOTYPES.RAW1%TYPE,
    p_raw2		 in		 GENOTYPES.RAW2%TYPE,
    p_reference	 in		 GENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 GENOTYPES.COMM%TYPE,
	p_level		 in		 GENOTYPES.LEVEL_%TYPE,
    p_id		 in		 GENOTYPES.ID%TYPE,
	p_diff		 out	 int,
	p_old_a1name out	 ALLELES.NAME%TYPE,
	p_old_a2name out	 ALLELES.NAME%TYPE,
	p_old_raw1	 out	 GENOTYPES.RAW1%TYPE,
	p_old_raw2	 out	 GENOTYPES.RAW2%TYPE,
	p_old_ref	 out	 GENOTYPES.REFERENCE%TYPE,
	p_old_comm	 out	 GENOTYPES.COMM%TYPE,
	p_old_level	 out	 GENOTYPES.LEVEL_%TYPE,
	p_old_usr	 out	 USERS.USR%TYPE,
    p_message	 in out  varchar2) is

    l_identity 	INDIVIDUALS.IDENTITY%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find iid for the individual
    begin
	  if l_ok then
        select identity into l_identity from Individuals
          where suid = p_suid and alias = p_alias;
	  end if ;
	exception
	  when TOO_MANY_ROWS then
	    p_message := 'There are more than one individual in this sampling unit ' ||
				  	 'with alias [' || p_alias || '].';
		l_ok := false;
	  when others then
	    p_message := 'Individual [' || p_alias || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;

    begin
      if l_ok then
	    Update_Genotype_File(p_suid, l_identity, p_marker, p_a1name, p_a2name,
								  p_raw1, p_raw2, p_reference, p_comm, p_level, p_id,
								  p_diff, p_old_a1name, p_old_a2name, p_old_raw1, p_old_raw2,
								  p_old_ref, p_old_comm, p_old_level, p_old_usr, p_message);
	  end if;
	exception
	  when others then
	    l_ok := false;
	    p_message := 'Failed to call PL/SQL procedure CREATE_OR_UPDATE_GENOTYPE(...) for ' ||
				  	 'identity [' || l_identity || '] and marker [' ||
					 p_marker || '] ORACLE error: ' || SQLERRM;
	end;
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Delete_Genotype (
    p_mid	   in	   GENOTYPES.MID%TYPE,
    p_iid	   in	   GENOTYPES.IID%TYPE,
    p_message  in out  varchar2)  is

  begin
    p_message := '';
	delete from genotypes_log where mid = p_mid and iid = p_iid;
    delete from Genotypes where mid = p_mid and iid = p_iid;
  exception
    when others then
	  p_message := OTHERS_MESS ||
				  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_Filter (
    p_pid	      in	    FILTERS.PID%TYPE,
    p_fid		  in out	FILTERS.FID%TYPE,
    p_name		  in		FILTERS.NAME%TYPE,
    p_expression  in		FILTERS.EXPRESSION%TYPE,
    p_comm		  in		FILTERS.COMM%TYPE,
    p_sid		  in		FILTERS.SID%TYPE,
    p_id		  in		FILTERS.ID%TYPE,
    p_message	  in out  	varchar2) is

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
    p_fid := 0;
    select Filters_Seq.Nextval into p_fid from Dual;
    insert into Filters values
      (p_fid, p_name, p_expression, p_comm, p_pid, p_sid, p_id, SYSDATE);
  exception
      when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Update_Filter (
    p_fid	      in	  FILTERS.FID%TYPE,
    p_name		  in	  FILTERS.NAME%TYPE,
    p_expression  in	  FILTERS.EXPRESSION%TYPE,
    p_comm		  in	  FILTERS.COMM%TYPE,
    p_sid		  in	  FILTERS.SID%TYPE,
    p_id		  in	  FILTERS.ID%TYPE,
    p_message	  in out  varchar2) is

    l_fid		   FILTERS.FID%TYPE;
    l_name		   FILTERS.NAME%TYPE;
    l_expression   FILTERS.EXPRESSION%TYPE;
    l_comm		   FILTERS.COMM%TYPE;
    l_sid		   FILTERS.SID%TYPE;
    l_id		   FILTERS.ID%TYPE;
    l_ts		   FILTERS.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
        select fid, name, expression, comm, sid, id, ts
	      into l_fid, l_name, l_expression, l_comm, l_sid, l_id, l_ts
          from Filters
          where fid = p_fid;
      -- Check if the new data is the same as the old.
	  -- In that case we do nothing!
	  	if l_name = p_name and l_comm = p_comm and
		   l_expression = p_expression and l_sid = p_sid then
		   return;
		end if;
        insert into Filters_Log values(
		  l_fid, l_name, l_expression, l_comm, l_sid, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Update the filter
    begin
	  if l_ok then
        update Filters set
          name = p_name, expression = p_expression, comm = p_comm, sid = p_sid,
          id = p_id, ts = SYSDATE
          where fid = p_fid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the filter. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Delete_Filter (
    p_fid	    in	    FILTERS.FID%TYPE,
    p_message	in out  varchar2)  is

  begin
    p_message := '';
	delete from filters_log
	  where fid = p_fid;
    delete from Filters
      where fid = p_fid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  PROCEDURE Create_Marker (
    p_mid	    IN OUT	  MARKERS.MID%TYPE,
    p_name		IN		  MARKERS.NAME%TYPE,
    p_alias		IN		  MARKERS.ALIAS%TYPE,
    p_comm		IN		  MARKERS.COMM%TYPE,
    p_suid		IN		  MARKERS.SUID%TYPE,
	p_p1		IN		  MARKERS.P1%TYPE,
	p_p2		IN		  MARKERS.P2%TYPE,
	p_position	IN		  MARKERS.POSITION%TYPE,
    p_cname		IN		  CHROMOSOMES.NAME%TYPE,
    p_id		IN 		  MARKERS.ID%TYPE,
    p_message	IN OUT    varchar2) IS

    l_cid 		MARKERS.CID%TYPE;
	l_new_name	MARKERS.NAME%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
	l_new_name := ltrim(rtrim(p_name));
    p_mid := 0;
  -- Check the parameters
    if length(l_new_name) > 20 then
	  p_message := 'Name exceeds 20 characters';
	  l_ok := false;
	elsif instr(l_new_name, ' ') > 0 then
	  p_message := 'Name contains white spaces';
	  l_ok := false;
	elsif p_alias is not null and length(p_alias) > 20 then
	  p_message := 'Alias exceeds 20 characters';
	  l_ok := false;
	elsif p_comm is not null and length(p_comm) > 256 then
	  p_message := 'Comment exceeds 256 characters';
	  l_ok := false;
	elsif p_p1 is not null and length(p_p1) > 40 then
	  p_message := 'Primer 1 exceeds 40 characters';
	  l_ok := false;
	elsif p_p2 is not null and length(p_p2) > 40 then
	  p_message := 'Primer 2 exceeds 40 characters';
	  l_ok := false;
	end if;

  -- Find cid for the chromosome
	begin
	  if l_ok then
        select c.cid into l_cid
		  from Chromosomes c, Sampling_Units s
          where s.SUID = p_suid and
		  		s.SID = c.SID and
		  		c.name = p_cname ;
	  end if ;
	exception
	  when others then
	    p_message := 'Unable to find chromosome [' || p_cname || '] for this species. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the new marker
    begin
	  if l_ok then
	    select Markers_Seq.NEXTVAL into p_mid from Dual;
        insert into Markers Values(
		  p_mid, upper(l_new_name), p_alias, p_comm, p_suid,
		  l_cid, p_p1, p_p2, p_position, p_id, SYSDATE);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the marker [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Create_Marker (
    p_mid	    IN OUT	  MARKERS.MID%TYPE,
    p_name		IN		  MARKERS.NAME%TYPE,
    p_alias		IN		  MARKERS.ALIAS%TYPE,
    p_comm		IN		  MARKERS.COMM%TYPE,
    p_suid		IN		  MARKERS.SUID%TYPE,
    p_cid		IN		  MARKERS.CID%TYPE,
	p_p1		IN		  MARKERS.P1%TYPE,
	p_p2		IN		  MARKERS.P2%TYPE,
	p_position	IN		  MARKERS.POSITION%TYPE,
    p_id		IN 		  MARKERS.ID%TYPE,
    p_message	IN OUT    varchar2) IS

	l_temp 		NUMBER;
	l_ok 		BOOLEAN := true;
	l_new_name  MARKERS.NAME%TYPE;


    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
	l_new_name := ltrim(rtrim(p_name));
    p_mid := 0;
  -- Check the parameters
    if length(l_new_name) > 20 then
	  p_message := 'Name exceeds 20 characters';
	  l_ok := false;
	elsif instr(l_new_name, ' ') > 0 then
	  p_message := 'Name contains white spaces';
	  l_ok := false;
	elsif p_alias is not null and length(p_alias) > 20 then
	  p_message := 'Alias exceeds 20 characters';
	  l_ok := false;
	elsif p_comm is not null and length(p_comm) > 256 then
	  p_message := 'Comment exceeds 256 characters';
	  l_ok := false;
	elsif p_p1 is not null and length(p_p1) > 40 then
	  p_message := 'Primer 1 exceeds 40 characters';
	  l_ok := false;
	elsif p_p2 is not null and length(p_p2) > 40 then
	  p_message := 'Primer 2 exceeds 40 characters';
	  l_ok := false;
	end if;
  -- Check if cid exists
	begin
	  if l_ok then
        select cid into l_temp
		  from Chromosomes
          where cid = p_cid ;
	  end if ;
	exception
	  when others then
	    p_message := 'The chromosome does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Check if suid exists
	begin
	  if l_ok then
        select suid into l_temp
		  from Sampling_Units
          where suid = p_suid ;
	  end if ;
	exception
	  when others then
	    p_message := 'The sampling unit does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the new marker
    begin
	  if l_ok then
	    select Markers_Seq.NEXTVAL into p_mid from Dual;
        insert into Markers Values(
		  p_mid, upper(l_new_name), p_alias, p_comm, p_suid,
		  p_cid, p_p1, p_p2, p_position, p_id, SYSDATE);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the marker [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Update_Marker (
    p_mid	    IN 	     MARKERS.MID%TYPE,
    p_name		IN		 MARKERS.NAME%TYPE,
    p_alias		IN		 MARKERS.ALIAS%TYPE,
    p_comm		IN		 MARKERS.COMM%TYPE,
	p_p1		IN		 MARKERS.P1%TYPE,
	p_p2		IN		 MARKERS.P2%TYPE,
	p_position	IN		 MARKERS.POSITION%TYPE,
	p_cid		IN		 MARKERS.CID%TYPE,
    p_id		IN 		 MARKERS.ID%TYPE,
    p_message	IN OUT   varchar2) IS

    l_name  	MARKERS.NAME%TYPE;
    l_alias 	MARKERS.ALIAS%TYPE;
    l_comm 		MARKERS.COMM%TYPE;
	l_p1		MARKERS.P1%TYPE;
	l_p2		MARKERS.P2%TYPE;
	l_position  MARKERS.POSITION%TYPE;
	l_cid		MARKERS.CID%TYPE;
    l_id 		MARKERS.ID%TYPE;
    l_ts 		MARKERS.TS%TYPE;

	l_ok        BOOLEAN := true;
	l_new_name  MARKERS.NAME%TYPE;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
	l_new_name := ltrim(rtrim(p_name));
  -- Check the parameters
    if length(l_new_name) > 20 then
	  p_message := 'Name exceeds 20 characters';
	  l_ok := false;
	elsif instr(l_new_name, ' ') > 0 then
	  p_message := 'Name contains white spaces';
	  l_ok := false;
	elsif p_alias is not null and length(p_alias) > 20 then
	  p_message := 'Alias exceeds 20 characters';
	  l_ok := false;
	elsif p_comm is not null and length(p_comm) > 256 then
	  p_message := 'Comment exceeds 256 characters';
	  l_ok := false;
	elsif p_p1 is not null and length(p_p1) > 40 then
	  p_message := 'Primer 1 exceeds 40 characters';
	  l_ok := false;
	elsif p_p2 is not null and length(p_p2) > 40 then
	  p_message := 'Primer 2 exceeds 40 characters';
	  l_ok := false;
	end if;
  -- Log the old data
    begin
	  if l_ok then
        select name, alias, comm, p1, p2,
			   position, cid, id, ts
		  INTO
          	   l_name, l_alias, l_comm, l_p1,
			   l_p2, l_position, l_cid, l_id, l_ts
		  FROM
          	  Markers where
      	      mid = p_mid;
        insert into Markers_Log Values(
		  p_mid, l_name, l_alias, l_comm,
		  l_p1, l_p2, l_position, l_cid, l_id, l_ts);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the marker
    begin
	  if l_ok then
        update Markers set name = upper(l_new_name), alias = p_alias,
          comm = p_comm, id = p_id, ts = SYSDATE,
		  p1 = p_p1, p2 = p_p2, position = p_position, cid = p_cid
		  where mid = p_mid;
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to update the marker [' || p_name || ']. ' ||
		 		  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_Marker (
    p_mid	    in	    MARKERS.MID%TYPE,
    p_message	in out  varchar)  is

  begin
    p_message := '';
  -- Delete all R_UAid_Aid (delete cascade aid, uaid)
  -- Delte R_UMid_Mid  (delete cascade umid, mid)
  -- Delete all alleles log
  -- Delete all alleles
  -- Delete all genotype logs
  -- Delete all genotypes
  -- Delete Positions (delete cascade msid, mid)

	delete from alleles_log where aid in (select aid from alleles where mid=p_mid);
	delete from alleles where mid = p_mid;
	delete from genotypes_log where mid = p_mid;
	delete from genotypes where mid = p_mid;
    delete from Markers where mid = p_mid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  PROCEDURE Copy_Library_Marker (
    p_mid	    IN OUT	  MARKERS.MID%TYPE,
    p_lmid		IN		  L_MARKERS.LMID%TYPE,
    p_suid		IN		  MARKERS.SUID%TYPE,
	p_id		IN		  MARKERS.ID%TYPE,
    p_message	IN OUT    varchar2) IS

	l_ok 		BOOLEAN := true;
	CURSOR 		c_laids is
		select laid from l_alleles where lmid=p_lmid;

	l_laid		L_ALLELES.LAID%TYPE;
	l_aid		ALLELES.AID%TYPE;
	l_cid		MARKERS.CID%TYPE;
	l_mname		MARKERS.NAME%TYPE;
	l_aname		ALLELES.NAME%TYPE;
	l_alias		MARKERS.ALIAS%TYPE;
	l_comm		MARKERS.COMM%TYPE;
	l_p1		MARKERS.P1%TYPE;
	l_p2		MARKERS.P2%TYPE;
	l_position	MARKERS.POSITION%TYPE;


    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
    p_mid := 0;
  -- Read data from the library marker
    begin
	  if l_ok then
	    select name, alias, comm, cid, p1, p2, position into
			   l_mname, l_alias, l_comm, l_cid, l_p1, l_p2, l_position
			   from l_markers where lmid=p_lmid;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to read the data from the library marker. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;
  -- Get the unique marker id for the new marker
    begin
	  if l_ok then
	    select markers_seq.nextval into p_mid from dual;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to increment the marker id. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;
  -- Create the new marker
    begin
	  if l_ok then
	    insert into markers (
		  mid, name, alias, comm, suid, cid, p1, p2, position, id, ts)
		  values(
		  p_mid, l_mname, l_alias, l_comm, p_suid, l_cid,
		  l_p1, l_p2, l_position, p_id, sysdate);
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to create the new marker. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;
  -- Copy all library alleles to the new marker
    begin
	  if l_ok then
        open c_laids;
	    loop
	      fetch c_laids into l_laid;
	      if c_laids%FOUND then
		    -- Read library allele data
		    select name, comm into l_aname, l_comm
		   		  from l_alleles where laid=l_laid;
			-- Find allele id
			select alleles_seq.nextval into l_aid from dual;
			-- Create the new allele
		    insert into alleles values(
				   l_aid, l_aname, l_comm, p_mid, p_id, sysdate);
	      else
	        close c_laids;
		    exit;
	      end if;
	    end loop;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to copy library alleles. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;

  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  PROCEDURE Create_Marker_Set (
    p_msid	   IN OUT	MARKER_SETS.MSID%TYPE,
    p_name	   IN		MARKER_SETS.NAME%TYPE,
    p_comm	   IN		MARKER_SETS.COMM%TYPE,
    p_suid	   IN		MARKER_SETS.SUID%TYPE,
    p_id	   IN 		MARKER_SETS.ID%TYPE,
    p_message  IN OUT   varchar2) IS

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok  BOOLEAN := true;

  BEGIN
    p_message := '';
    p_msid := 0;
  -- Check the parameters
    if l_ok then
	  if length(p_name) > 20 then
	    p_message := 'Name exceeds 20 characters';
		l_ok := false;
	  elsif p_comm is not null and length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 charcters';
		l_ok := false;
	  end if;
	end if;
	if l_ok then
      select Marker_Sets_Seq.nextval into p_msid from Dual;
      insert into MARKER_SETS values(
	   p_msid, p_name, p_comm, p_suid,
       p_id, SYSDATE);
    end if;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;
----------------------------------------
  PROCEDURE Update_Marker_Set (
    p_msid	   IN 	   MARKER_SETS.MSID%TYPE,
    p_name	   IN	   MARKER_SETS.NAME%TYPE,
    p_comm	   IN	   MARKER_SETS.COMM%TYPE,
    p_id	   IN 	   MARKER_SETS.ID%TYPE,
    p_message  IN OUT  varchar2) IS

    l_name 	  MARKER_SETS.NAME%TYPE;
    l_comm 	  MARKER_SETS.COMM%TYPE;
    l_id 	  MARKER_SETS.ID%TYPE;
    l_ts 	  MARKER_SETS.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Check the parameters
    if l_ok then
	  if length(p_name) > 20 then
	    p_message := 'Name exceeds 20 characters';
		l_ok := false;
	  elsif p_comm is not null and length(p_comm) > 256 then
	    p_message := 'Comment exceeds 256 characters';
		l_ok := false;
	  end if;
	end if;
  -- Log the old data
    begin
	  if l_ok then
	    select name, comm, id, ts into
          l_name, l_comm, l_id, l_ts from
          Marker_Sets where
          msid = p_msid;
        insert into Marker_Sets_Log values(
		  p_msid, l_name, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the marker set
    begin
	  if l_ok then
	    update Marker_Sets set name = p_name,
          comm = p_comm, id = p_id, ts = SYSDATE
          where msid = p_msid;
	  end if;
    exception
	  when others then
	    p_message := 'Failed to update the marker set [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_Marker_Set (
    p_msid	    in	    MARKER_SETS.MSID%TYPE,
    p_message	in out  varchar2)  is


  begin
    p_message := '';
  -- Delete positions (delete cascade msid, mid)
    delete from Marker_sets_log where msid = p_msid;
    delete from Marker_Sets  where msid = p_msid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_Marker_Set_Link (
    p_msid	    in	     MARKER_SETS.MSID%TYPE,
    p_mark_name	in		 MARKERS.NAME%TYPE,
    p_position	in		 POSITIONS.VALUE%TYPE,
    p_suid		in		 MARKERS.SUID%TYPE,
    p_id		in		 POSITIONS.ID%TYPE,
    p_message	in out   varchar2) is

    l_mid 		MARKERS.MID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find the mid for the marker
    begin
	  if l_ok then
	    select mid into l_mid from Markers
      	where suid = p_suid and
			  name = p_mark_name ;
	  end if;
	exception
	  when NO_DATA_FOUND then
	    p_message := 'The marker [' || p_mark_name || '] does not exist.';
		l_ok := false;
	  when others then
	    p_message := 'Failed to find the marker [' || p_mark_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the link
    begin
	  if l_ok then
        insert into Positions values(
		  p_msid, l_mid, p_position, p_id, SYSDATE);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the position-link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Create_Marker_Set_Link (
    p_msid	    in	     MARKER_SETS.MSID%TYPE,
    p_mid  		in		 MARKERS.MID%TYPE,
    p_position	in		 POSITIONS.VALUE%TYPE,
    p_id		in		 POSITIONS.ID%TYPE,
    p_message	in out   varchar2) is


	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Create the link
    begin
	  if l_ok then
        insert into Positions values(
		  p_msid, p_mid, p_position, p_id, SYSDATE);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the position-link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Update_Marker_Set_Link (
    p_msid	    in	     POSITIONS.MSID%TYPE,
    p_mid		in		 POSITIONS.MID%TYPE,
    p_position	in		 POSITIONS.VALUE%TYPE,
    p_id		in		 POSITIONS.ID%TYPE,
    p_message	in out   varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Update the link
    begin
	  if l_ok then
        update Positions set value = p_position,
			   			 	 id = p_id,
							 ts = sysdate
		  where msid = p_msid and mid = p_mid;
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to update the position-link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Delete_Marker_Set_Link (
    p_msid	   in	  POSITIONS.MSID%TYPE,
    p_mid	   in	  POSITIONS.MID%TYPE,
    p_message  in out varchar2) is

  begin
    p_message := '';
    delete from Positions
      where msid = p_msid and mid = p_mid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_Allele (
    p_aid	    in out	ALLELES.AID%TYPE,
    p_name	    in		ALLELES.NAME%TYPE,
    p_comm		in		ALLELES.COMM%TYPE,
    p_mname		in		MARKERS.NAME%TYPE,
    p_suid		in		MARKERS.SUID%TYPE,
    p_id	    in		ALLELES.ID%TYPE,
    p_message	in out  varchar2) is

    l_mid int;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
    p_aid := 0;
  -- Find the mid for the marker
    begin
	  if l_ok then
	    select mid into l_mid from Markers
          where suid = p_suid and
		     	name = p_mname;
	  end if ;
	exception
	  when others then
	    p_message := 'The marker [' || p_mname || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the allele
    begin
	  if l_ok then
        select Alleles_Seq.Nextval into p_aid from Dual;
        insert into Alleles values(
		  p_aid, p_name, p_comm, l_mid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_Allele (
    p_aid	    in out	ALLELES.AID%TYPE,
    p_name	    in		ALLELES.NAME%TYPE,
    p_comm		in		ALLELES.COMM%TYPE,
    p_mid		in		ALLELES.MID%TYPE,
    p_id	    in		ALLELES.ID%TYPE,
    p_message	in out  varchar2) is

    l_temp		NUMBER;
	l_ok 		BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
    p_aid := 0;
  -- Check if mid exists
    begin
	  if l_ok then
	    select mid into l_temp from Markers
          where mid = p_mid ;
	  end if ;
	exception
	  when others then
	    p_message := 'The marker does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the allele
    begin
	  if l_ok then
        select Alleles_Seq.Nextval into p_aid from Dual;
        insert into Alleles values(
		  p_aid, p_name, p_comm, p_mid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Update_Allele (
    p_aid	    in	    ALLELES.AID%TYPE,
    p_name		in		ALLELES.NAME%TYPE,
    p_comm		in		ALLELES.COMM%TYPE,
    p_id		in		ALLELES.ID%TYPE,
    p_message	in out  varchar2) is

    l_aid		ALLELES.AID%TYPE;
    l_name		ALLELES.NAME%TYPE;
    l_comm		ALLELES.COMM%TYPE;
    l_id		ALLELES.ID%TYPE;
    l_ts		ALLELES.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
        select aid, name, comm, id, ts into l_aid, l_name, l_comm, l_id, l_ts
          from Alleles
          where aid = p_aid;
        insert into Alleles_Log values(l_aid, l_name, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Update the allele
    begin
	  if l_ok then
        update Alleles set
          name = p_name, comm = p_comm, id = p_id, ts = SYSDATE
          where aid = p_aid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Delete_Allele (
    p_aid	   in	   ALLELES.AID%TYPE,
    p_message  in out  varchar2) is

	l_mid	   GENOTYPES.MID%TYPE;
	l_ok	   BOOLEAN := true;
  begin
    p_message := '';
  -- Update genotypes or abort !?!
  -- Well, let's update all the involved genotypes with null
    begin
    -- I belive that we gain a lot of performance by first
	-- retieving the mid. There is an index on the mid for
	-- genotypes.
	  if l_ok then
	    select mid into l_mid from alleles where aid=p_aid;
  	    update genotypes set
	      aid1=decode(aid1, p_aid, null, aid1),
	      aid2=decode(aid2, p_aid, null, aid2)
	      where
	        mid=l_mid and (aid1 = p_aid or aid2 = p_aid);
	  end if;
    exception
	  when others then
	    p_message := 'Failed to update the genotypes refering to this allele.' ||
				  	 ' ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
	if l_ok then
	  delete from alleles_log where aid = p_aid;
	  delete from alleles where aid = p_aid;
	end if;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  PROCEDURE Create_L_Marker (
    p_lmid	    IN OUT	  L_MARKERS.LMID%TYPE,
    p_name		IN		  L_MARKERS.NAME%TYPE,
    p_alias		IN		  L_MARKERS.ALIAS%TYPE,
    p_comm		IN		  L_MARKERS.COMM%TYPE,
    p_sid		IN		  L_MARKERS.SID%TYPE,
	p_p1		IN		  L_MARKERS.P1%TYPE,
	p_p2		IN		  L_MARKERS.P2%TYPE,
	p_position	IN		  L_MARKERS.POSITION%TYPE,
    p_cname		IN		  CHROMOSOMES.NAME%TYPE,
    p_message	IN OUT    varchar2) IS

    l_cid 		L_MARKERS.CID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
    p_lmid := 0;
  -- Check the parameters
    if length(p_name) > 20 then
	  p_message := 'Name exceeds 20 characters';
	  l_ok := false;
	elsif p_alias is not null and length(p_alias) > 20 then
	  p_message := 'Alias exceeds 20 characters';
	  l_ok := false;
	elsif p_comm is not null and length(p_comm) > 256 then
	  p_message := 'Comment exceeds 256 characters';
	  l_ok := false;
	elsif p_p1 is not null and length(p_p1) > 40 then
	  p_message := 'Primer 1 exceeds 40 characters.';
	  l_ok := false;
	elsif p_p2 is not null and length(p_p2) > 40 then
	  p_message := 'Primer 2 exceeds 40 charcters.';
	  l_ok := false;
	end if;

  -- Find cid for the chromosome
	begin
	  if l_ok then
        select c.cid into l_cid
		  from Chromosomes c
          where c.SID = p_sid and
		  		c.name = p_cname ;
	  end if ;
	exception
	  when others then
	    p_message := 'Unable to find chromosome [' || p_cname || '] for this species. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the new marker
    begin
	  if l_ok then
	    select L_Markers_Seq.NEXTVAL into p_lmid from Dual;
        insert into L_Markers(
		  lmid, name, alias, comm, sid, cid, p1, p2, position)
		  Values(
		  p_lmid, upper(p_name), p_alias, p_comm, p_sid,
		  l_cid, p_p1, p_p2, p_position);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the library marker [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Create_L_Marker (
    p_lmid	    IN OUT	  L_MARKERS.LMID%TYPE,
    p_name		IN		  L_MARKERS.NAME%TYPE,
    p_alias		IN		  L_MARKERS.ALIAS%TYPE,
    p_comm		IN		  L_MARKERS.COMM%TYPE,
    p_sid		IN		  L_MARKERS.SID%TYPE,
    p_cid		IN		  L_MARKERS.CID%TYPE,
	p_p1		IN		  L_MARKERS.P1%TYPE,
	p_p2		IN		  L_MARKERS.P2%TYPE,
	p_position	IN		  L_MARKERS.POSITION%TYPE,
    p_message	IN OUT    varchar2) IS

	l_temp 		NUMBER;
	l_ok 		BOOLEAN := true;


    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
    p_lmid := 0;
  -- Check the parameters
    if length(p_name) > 20 then
	  p_message := 'Name exceeds 20 characters';
	  l_ok := false;
	elsif p_alias is not null and length(p_alias) > 20 then
	  p_message := 'Alias exceeds 20 characters';
	  l_ok := false;
	elsif p_comm is not null and length(p_comm) > 256 then
	  p_message := 'Comment exceeds 256 characters';
	  l_ok := false;
	elsif p_p1 is not null and length(p_p1) > 40 then
	  p_message := 'Primer 1 exceeds 40 characters.';
	  l_ok := false;
	elsif p_p2 is not null and length(p_p2) > 40 then
	  p_message := 'Primer 2 exceeds 40 charcters.';
	  l_ok := false;
	end if;
  -- Check if cid exists
	begin
	  if l_ok then
        select cid into l_temp
		  from Chromosomes
          where cid = p_cid ;
	  end if ;
	exception
	  when others then
	    p_message := 'The chromosome does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Check if sid exists
	begin
	  if l_ok then
        select sid into l_temp
		  from Species
          where sid = p_sid ;
	  end if ;
	exception
	  when others then
	    p_message := 'The species does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the new marker
    begin
	  if l_ok then
	    select L_Markers_Seq.NEXTVAL into p_lmid from Dual;
        insert into L_Markers (
		  lmid, name, alias, comm, sid, cid, p1, p2, position)
		Values(
		  p_lmid, upper(p_name), p_alias, p_comm, p_sid,
		  p_cid, p_p1, p_p2, p_position);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the library marker [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Update_L_Marker (
    p_lmid	    IN 	     L_MARKERS.LMID%TYPE,
    p_name		IN		 L_MARKERS.NAME%TYPE,
    p_alias		IN		 L_MARKERS.ALIAS%TYPE,
    p_comm		IN		 L_MARKERS.COMM%TYPE,
	p_p1		IN		 L_MARKERS.P1%TYPE,
	p_p2		IN		 L_MARKERS.P2%TYPE,
	p_position	IN		 L_MARKERS.POSITION%TYPE,
	p_cid		IN		 L_MARKERS.CID%TYPE,
    p_message	IN OUT   varchar2) IS


	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Check the parameters
    if length(p_name) > 20 then
	  p_message := 'Name exceeds 20 characters';
	  l_ok := false;
	elsif p_alias is not null and length(p_alias) > 20 then
	  p_message := 'Alias exceeds 20 characters';
	  l_ok := false;
	elsif p_comm is not null and length(p_comm) > 256 then
	  p_message := 'Comment exceeds 256 characters';
	  l_ok := false;
	elsif p_p1 is not null and length(p_p1) > 40 then
	  p_message := 'Primer 1 exceeds 40 characters.';
	  l_ok := false;
	elsif p_p2 is not null and length(p_p2) > 40 then
	  p_message := 'Primer 2 exceeds 40 charcters.';
	  l_ok := false;
	end if;
  -- Update the libary marker
    begin
	  if l_ok then
        update L_Markers set name = upper(p_name), alias = p_alias,
          comm = p_comm, p1 = p_p1, p2 = p_p2,
		  position = p_position, cid = p_cid
		  where lmid = p_lmid;
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to update the library marker [' || p_name || ']. ' ||
		 		  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Copy_Marker (
    p_lmid	    IN OUT	  L_MARKERS.LMID%TYPE,
    p_mid		IN		  MARKERS.MID%TYPE,
    p_message	IN OUT    varchar2) IS

	cursor c_aids is select aid from alleles where mid=p_mid;

	l_ok 	   BOOLEAN := true;

	l_aid	   ALLELES.AID%TYPE;

	l_laid	   L_ALLELES.LAID%TYPE;
	l_aname	   L_ALLELES.NAME%TYPE;
	l_acomm	   L_ALLELES.COMM%TYPE;

	l_name	   L_MARKERS.NAME%TYPE;
	l_alias	   L_MARKERS.ALIAS%TYPE;
	l_comm	   L_MARKERS.COMM%TYPE;
	l_sid	   L_MARKERS.SID%TYPE;
	l_cid	   L_MARKERS.CID%TYPE;
	l_p1	   L_MARKERS.P1%TYPE;
	l_p2	   L_MARKERS.P2%TYPE;
	l_position L_MARKERS.POSITION%TYPE;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
    p_lmid := 0;
  -- Read data from markers
    begin
  	  if l_ok then
	    select m.name, m.alias, m.comm, c.sid, m.cid, m.p1, m.p2, m.position
		  into l_name, l_alias, l_comm, l_sid, l_cid, l_p1, l_p2, l_position
		  from markers m, chromosomes c
		  where
		    m.cid = c.cid and
			m.mid = p_mid;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to read data from table. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;
  -- Find lmid
    begin
	  if l_ok then
	    select l_markers_seq.nextval into p_lmid from dual;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to increment sequence. ' ||
				 	'ORACLE error: ' || SQLERRM;
	end;
  -- Create the library marker
    begin
	  if l_ok then
	    insert into L_Markers (
		  lmid, name, alias, comm, sid, cid, p1, p2, position)
		values (
		  p_lmid, l_name, l_alias, l_comm, l_sid, l_cid, l_p1, l_p2, l_position);
	  end if;
	exception
	  when others then
	    l_ok := false;
	  	p_message := 'Failed to insert row into table. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;
  -- Copy all alleles
    begin
	  if l_ok then
	    open c_aids;
	    loop
  	      fetch c_aids into l_aid;
	      if c_aids%FOUND then
		    select name, comm into l_aname, l_acomm
			  from alleles where aid = l_aid;
			select l_alleles_seq.nextval into l_laid from dual;
			insert into l_alleles values (
			  l_laid, l_aname, l_acomm, p_lmid);
	      else
	        close c_aids;
		    exit;
	      end if;
	    end loop;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to copy alleles. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;

  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_L_Marker (
    p_lmid	    in	    L_MARKERS.LMID%TYPE,
    p_message	in out  varchar)  is

  begin
    p_message := '';

	delete from l_alleles where lmid = p_lmid;
	delete from l_markers where lmid = p_lmid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_L_Allele (
    p_laid	    in out	L_ALLELES.LAID%TYPE,
    p_name	    in		L_ALLELES.NAME%TYPE,
    p_comm		in		L_ALLELES.COMM%TYPE,
    p_mname		in		L_MARKERS.NAME%TYPE,
    p_sid		in		L_MARKERS.SID%TYPE,
    p_message	in out  varchar2) is

    l_lmid int;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
    p_laid := 0;
  -- Find the lmid for the library marker
    begin
	  if l_ok then
	    select lmid into l_lmid from L_Markers
          where sid = p_sid and
		     	name = p_mname;
	  end if ;
	exception
	  when others then
	    p_message := 'The library marker [' || p_mname || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the library allele
    begin
	  if l_ok then
        select L_Alleles_Seq.Nextval into p_laid from Dual;
        insert into L_Alleles values(
		  p_laid, p_name, p_comm, l_lmid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the library allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_L_Allele (
    p_laid	    in out	L_ALLELES.LAID%TYPE,
    p_name	    in		L_ALLELES.NAME%TYPE,
    p_comm		in		L_ALLELES.COMM%TYPE,
    p_lmid		in		L_ALLELES.LMID%TYPE,
    p_message	in out  varchar2) is

    l_temp		NUMBER;
	l_ok 		BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
    p_laid := 0;
  -- Check if lmid exists
    begin
	  if l_ok then
	    select lmid into l_temp from L_Markers
          where lmid = p_lmid ;
	  end if ;
	exception
	  when others then
	    p_message := 'The library marker does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the library allele
    begin
	  if l_ok then
        select L_Alleles_Seq.Nextval into p_laid from Dual;
        insert into L_Alleles values(
		  p_laid, p_name, p_comm, p_lmid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the library allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Update_L_Allele (
    p_laid	    in	    L_ALLELES.LAID%TYPE,
    p_name		in		L_ALLELES.NAME%TYPE,
    p_comm		in		L_ALLELES.COMM%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Update the library allele
    begin
	  if l_ok then
        update L_Alleles set
          name = p_name, comm = p_comm
          where laid = p_laid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the library allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Delete_L_Allele (
    p_laid	   in	   L_ALLELES.LAID%TYPE,
    p_message  in out  varchar2) is

	l_ok	   BOOLEAN := true;
  begin
    p_message := '';
	if l_ok then
	  delete from l_alleles where laid = p_laid;
	end if;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  PROCEDURE Create_Variable (
    p_vid	   IN OUT	VARIABLES.VID%TYPE,
    p_name	   IN		VARIABLES.NAME%TYPE,
    p_type	   IN		VARIABLES.TYPE%TYPE,
    p_unit	   IN		VARIABLES.UNIT%TYPE,
    p_comm	   IN		VARIABLES.COMM%TYPE,
    p_suid	   IN		VARIABLES.SUID%TYPE,
    p_id	   IN 		VARIABLES.ID%TYPE,
    p_message  IN OUT  	varchar2) IS

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_new_name   VARIABLES.NAME%TYPE;
	l_ok 		 BOOLEAN := true;

  BEGIN
    p_message := '';
	l_new_name := ltrim(rtrim(p_name));
    p_vid := 0;
	-- Check the parameters
    if l_ok then
	  if length(l_new_name) > 20 then
		p_message := 'Name exceeds 20 charcters';
		l_ok := false;
	  elsif instr(l_new_name, ' ') > 0 then
		p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif p_type not in('E', 'N') then
		p_message := 'Unknown type [' || p_type || ']';
		l_ok := false;
	  elsif length(p_unit) > 10 then
		p_message := 'Unit exceeds 10 charcters';
		l_ok := false;
	  elsif length(p_comm) > 256 then
		p_message := 'Comments exceeds 256 charcters';
		l_ok := false;
	  end if;
	end if;
	-- Find variable id
	begin
	  if l_ok then
	    select Variables_Seq.nextval into p_vid from dual;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to increment variable id. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
	-- Create the variable
	begin
	  if l_ok then
	    insert into Variables Values
      	  (p_vid, l_new_name, p_type, p_unit,
           p_comm, p_suid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to insert into table. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
    end;

  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Update_Variable (
    p_vid	    IN 	    VARIABLES.VID%TYPE,
    p_name		IN		VARIABLES.NAME%TYPE,
    p_type		IN		VARIABLES.TYPE%TYPE,
    p_unit		IN		VARIABLES.UNIT%TYPE,
    p_comm		IN		VARIABLES.COMM%TYPE,
    p_id		IN 		VARIABLES.ID%TYPE,
    p_message	IN OUT  varchar2) IS

    l_name 		VARIABLES.NAME%TYPE;
    l_type 		VARIABLES.UNIT%TYPE;
    l_unit 		VARIABLES.UNIT%TYPE;
    l_comm 		VARIABLES.COMM%TYPE;
    l_id 		VARIABLES.ID%TYPE;
    l_ts 		VARIABLES.TS%TYPE;

	l_new_name     VARIABLES.NAME%TYPE;
	l_ok 		   BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
	l_new_name := ltrim(rtrim(p_name));
	-- Check the parameters
    if l_ok then
	  if length(l_new_name) > 20 then
		p_message := 'Name exceeds 20 charcters';
		l_ok := false;
	  elsif instr(l_new_name, ' ') > 0 then
		p_message := 'Name contains white spaces';
		l_ok := false;
	  elsif p_type not in('E', 'N') then
		p_message := 'Unknown type [' || p_type || ']';
		l_ok := false;
	  elsif length(p_unit) > 10 then
		p_message := 'Unit exceeds 10 charcters';
		l_ok := false;
	  elsif length(p_comm) > 256 then
		p_message := 'Comments exceeds 256 charcters';
		l_ok := false;
	  end if;
	end if;

  -- Log the old data
    begin
	  if l_ok then
        select name, type, unit, comm, id, ts INTO
          l_name, l_type, l_unit, l_comm, l_id, l_ts FROM
          Variables where
          vid = p_vid;
	    insert into Variables_Log Values(
		  p_vid, l_name, l_type, l_unit, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the variable
    begin
	  if l_ok then
        update Variables set name = l_new_name, type = p_type,
          unit = p_unit, comm = p_comm, id = p_id, ts = SYSDATE
          where vid = p_vid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the variable [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_Variable (
    p_vid	  	in	    VARIABLES.VID%TYPE,
    p_message   in out  varchar2)  is

  begin
    p_message := '';
  -- Delete Phenotype logs
    delete from phenotypes_log where vid = p_vid;
  -- delete Phenotypes
    delete from phenotypes where vid = p_vid;
  -- Delete variables logs
    delete from variables_log where vid = p_vid;
  -- Delete R_UVid_Vid (delete cascade uvid, vid)
  -- Delete R_Var_Set (delete cascade vsid, vid)

    delete from Variables where vid = p_vid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  PROCEDURE Create_Variable_Set (
    p_vsid	    IN OUT	  VARIABLE_SETS.VSID%TYPE,
    p_name		IN		  VARIABLE_SETS.NAME%TYPE,
    p_comm		IN		  VARIABLE_SETS.COMM%TYPE,
    p_suid		IN		  VARIABLE_SETS.SUID%TYPE,
    p_id		IN 		  VARIABLE_SETS.ID%TYPE,
    p_message	IN OUT    varchar2) IS

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

  BEGIN
    p_message := '';
    p_vsid := 0;
    SELECT Variable_Sets_Seq.NEXTVAL INTO p_vsid FROM Dual;
    INSERT INTO Variable_SETS VALUES
      (p_vsid, p_name, p_comm, p_suid,
       p_id, SYSDATE);

  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;
----------------------------------------
  PROCEDURE Update_Variable_Set (
    p_vsid	    IN 	    VARIABLE_SETS.VSID%TYPE,
    p_name		IN		VARIABLE_SETS.NAME%TYPE,
    p_comm		IN		VARIABLE_SETS.COMM%TYPE,
    p_id		IN 		VARIABLE_SETS.ID%TYPE,
    p_message	IN OUT  varchar2) IS

    l_name 		VARIABLE_SETS.NAME%TYPE;
    l_comm 		VARIABLE_SETS.COMM%TYPE;
    l_id 		VARIABLE_SETS.ID%TYPE;
    l_ts 		VARIABLE_SETS.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
	    select name, comm, id, ts INTO
          l_name, l_comm, l_id, l_ts FROM
          Variable_Sets where
          vsid = p_vsid;
        insert into Variable_Sets_Log Values(
		  p_vsid, l_name, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
 	end;
  -- Update the variable set
    begin
	  if l_ok then
	    update Variable_Sets set name = p_name,
          comm = p_comm, id = p_id, ts = SYSDATE
          where vsid = p_vsid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the variable set. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_Variable_Set (
    p_vsid	    in	    VARIABLE_SETS.VSID%TYPE,
    p_message	in out  varchar2)  is

  begin
    p_message := '';
  -- Delete R_Var_Set (delete cascade vsid, vid)
  -- Delete Variable set logs
    delete from variable_sets_log where vsid = p_vsid;
    delete from Variable_Sets where vsid = p_vsid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_Variable_Set_Link (
    p_vsid	    in	    R_VAR_SET.VSID%TYPE,
    p_var_name	in		VARIABLES.NAME%TYPE,
    p_suid		in		VARIABLES.SUID%TYPE,
    p_id		in		R_VAR_SET.ID%TYPE,
    p_message	in out  varchar2) is

    l_vid 		R_VAR_SET.VID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find the vid for the variable
    begin
	  if l_ok then
	    select vid into l_vid from Variables
          where suid = p_suid and
		  		name = p_var_name ;
	  end if;
	exception
	  when others then
	    p_message := 'The variable [' || p_var_name || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Create the link
    begin
	  if l_ok then
        insert into R_Var_Set values(p_vsid, l_vid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the variable set link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_Variable_Set_Link (
    p_vsid	    in	    R_VAR_SET.VSID%TYPE,
    p_vid		in		VARIABLES.VID%TYPE,
    p_id		in		R_VAR_SET.ID%TYPE,
    p_message	in out  varchar2) is


	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Create the link
    begin
	  if l_ok then
        insert into R_Var_Set values(p_vsid, p_vid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the variable set link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Delete_Variable_Set_Link (
    p_vsid	    in	    R_VAR_SET.VSID%TYPE,
    p_vid		in		R_VAR_SET.VID%TYPE,
    p_message	in out  varchar2) is

  begin
    p_message := '';
    delete from R_Var_Set
      where vsid = p_vsid and vid = p_vid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  PROCEDURE Create_Chromosome (
    p_cid	   IN OUT	CHROMOSOMES.CID%TYPE,
    p_name	   IN		CHROMOSOMES.NAME%TYPE,
    p_comm	   IN		CHROMOSOMES.COMM%TYPE,
    p_sid	   IN		CHROMOSOMES.SID%TYPE,
    p_message  IN OUT   varchar2) IS

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

  BEGIN
    p_message := '';
    p_cid := 0;

    select Chromosomes_Seq.nextval into p_cid from dual;

    insert into Chromosomes Values
      (p_cid, p_name, p_comm, p_sid);

  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Update_Chromosome (
    p_cid	   IN 	    CHROMOSOMES.CID%TYPE,
    p_name	   IN	  	CHROMOSOMES.NAME%TYPE,
    p_comm	   IN 	  	CHROMOSOMES.COMM%TYPE,
    p_message  IN OUT 	varchar2) IS

    l_name 	   CHROMOSOMES.NAME%TYPE;
    l_comm 	   CHROMOSOMES.COMM%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Update the chromosome
    begin
	  if l_ok then
        update Chromosomes set
		  name = p_name, comm = p_comm
          where cid = p_cid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the chromosome. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_Chromosome (
    p_cid	    in	    CHROMOSOMES.CID%TYPE,
    p_message	in out  varchar2)  is

	cursor c_lmids is select lmid from l_markers where cid=p_cid;
	cursor c_umids is select umid from u_markers where cid=p_cid;
	cursor c_mids is select mid from markers where cid=p_cid;

	l_lmid L_MARKERS.LMID%TYPE;
	l_umid U_MARKERS.UMID%TYPE;
	l_mid  MARKERS.MID%TYPE;
  begin
    p_message := '';
 -- Delete all L_Alleles (are taken care of by l_markers)
 -- delete all L_Markers (are taken care of by l_markers)
 -- Delete all u allele logs (are taken care of by u_markers)
 -- Delete all R_Uaid_Aid (delete cascade uaid, aid)
 -- Delete all u alleles (are taken care of by u_markers)
 -- Delete all u_positions (delete cascade umid, umsid)
 -- Delete all u_markers log (are taken care of by u_markers)
 -- Delete all R_UMid_Mid (delete cascade umid, mid)
 -- Delete all Allele logs (are taken care of by markers)
 -- Delete / Update genotypes (are taken care of by markers)
 -- Delete all alleles (are taken care of markers)
 -- Delete all positions (are taken care of by markers)
 -- Delete all marker logs (are taken care of by markers)
 -- Delete all markers (are taken care of bu markers)
    open c_lmids;
	loop
	  fetch c_lmids into l_lmid;
	  if c_lmids%FOUND then
	    Delete_L_Marker(l_lmid, p_message);
	  else
	    close c_lmids;
		exit;
	  end if;
	end loop;
	open c_umids;
	loop
      fetch c_umids into l_umid;
	  if c_umids%FOUND then
	    Delete_U_Marker(l_umid, p_message);
	  else
	    close c_umids;
		exit;
	  end if;
	end loop;
	open c_mids;
	loop
	  fetch c_mids into l_mid;
	  if c_mids%FOUND then
	    Delete_Marker(l_mid, p_message);
	  else
	    close c_mids;
		exit;
	  end if;
	end loop;
    delete from Chromosomes where cid = p_cid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
-- Unified objects ---------------------
----------------------------------------
  PROCEDURE Create_U_Marker (
    p_umid	    IN OUT	  U_MARKERS.UMID%TYPE,
    p_name		IN		  U_MARKERS.NAME%TYPE,
    p_alias		IN		  U_MARKERS.ALIAS%TYPE,
    p_comm		IN		  U_MARKERS.COMM%TYPE,
	p_position	IN		  U_MARKERS.POSITION%TYPE,
	p_pid		IN		  U_MARKERS.PID%TYPE,
    p_sid		IN		  U_MARKERS.SID%TYPE,
    p_cname		IN		  CHROMOSOMES.NAME%TYPE,
    p_id		IN 		  U_MARKERS.ID%TYPE,
    p_message	IN OUT    varchar2) IS

    l_cid 		MARKERS.CID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
    p_umid := 0;
  -- Find cid for the chromosome
	begin
	  if l_ok then
        select cid into l_cid
		  from Chromosomes
          where SID = p_sid and
		  		name = p_cname ;
	  end if ;
	exception
	  when others then
	    p_message := 'Unable to find chromosome [' || p_cname || '] for this species. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the new marker
    begin
	  if l_ok then
	    select U_Markers_Seq.nextval into p_umid from Dual;
        insert into U_Markers Values(
		  p_umid, upper(p_name), p_alias, p_comm, p_position,
		  p_pid, p_sid, l_cid, p_id, SYSDATE);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the unified marker [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Create_U_Marker (
    p_umid	    IN OUT	  U_MARKERS.UMID%TYPE,
    p_name		IN		  U_MARKERS.NAME%TYPE,
    p_alias		IN		  U_MARKERS.ALIAS%TYPE,
    p_comm		IN		  U_MARKERS.COMM%TYPE,
	p_position	IN		  U_MARKERS.POSITION%TYPE,
	p_pid		IN		  U_MARKERS.PID%TYPE,
    p_cid		IN		  U_MARKERS.CID%TYPE,
    p_id		IN 		  U_MARKERS.ID%TYPE,
    p_message	IN OUT    varchar2) IS

    l_sid 		MARKERS.CID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
    p_umid := 0;
  -- Find Sid
    begin
	  if l_ok then
	    select sid into l_sid from chromosomes where cid=p_cid;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Unable to read from chromosemes. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end ;
  -- Create the new marker
    begin
	  if l_ok then
	    select U_Markers_Seq.nextval into p_umid from Dual;
        insert into U_Markers Values(
		  p_umid, upper(p_name), p_alias, p_comm, p_position,
		  p_pid, l_sid, p_cid, p_id, SYSDATE);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the unified marker [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Update_U_Marker (
    p_umid	    IN 	     U_MARKERS.UMID%TYPE,
    p_name		IN		 U_MARKERS.NAME%TYPE,
    p_alias		IN		 U_MARKERS.ALIAS%TYPE,
    p_comm		IN		 U_MARKERS.COMM%TYPE,
	p_position  IN		 U_MARKERS.POSITION%TYPE,
	p_cid		IN		 U_MARKERS.CID%TYPE,
    p_id		IN 		 U_MARKERS.ID%TYPE,
    p_message	IN OUT   varchar2) IS

    l_name  	U_MARKERS.NAME%TYPE;
    l_alias 	U_MARKERS.ALIAS%TYPE;
    l_comm 		U_MARKERS.COMM%TYPE;
	l_position	U_MARKERS.POSITION%TYPE;
	l_cid		U_MARKERS.CID%TYPE;
    l_id 		U_MARKERS.ID%TYPE;
    l_ts 		U_MARKERS.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
        select name, alias, comm, position, cid, id, ts INTO
          l_name, l_alias, l_comm, l_position, l_cid, l_id, l_ts FROM
          U_Markers where
      	    umid = p_umid;
        insert into U_Markers_Log Values(
		  p_umid, l_name, l_alias, l_comm, l_position, l_cid, l_id, l_ts);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the marker
    begin
	  if l_ok then
        update U_Markers set name = upper(p_name), alias = p_alias, comm = p_comm,
			   position = p_position, cid = p_cid, id = p_id, ts = SYSDATE
		  where umid = p_umid;
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to update the unified marker [' || p_name || ']. ' ||
		 		  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_U_Marker (
    p_umid	    in	    U_MARKERS.UMID%TYPE,
    p_message	in out  varchar)  is

  begin
    p_message := '';
	delete from u_alleles_log where uaid in (select uaid from u_alleles where umid=p_umid);
	delete from u_alleles where umid=p_umid;
    delete from u_Markers where umid = p_umid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  PROCEDURE Create_U_Marker_Set (
    p_umsid	   IN OUT	U_MARKER_SETS.UMSID%TYPE,
    p_name	   IN		U_MARKER_SETS.NAME%TYPE,
    p_comm	   IN		U_MARKER_SETS.COMM%TYPE,
	p_pid	   IN		U_MARKER_SETS.PID%TYPE,
    p_sid	   IN		U_MARKER_SETS.SID%TYPE,
    p_id	   IN 		U_MARKER_SETS.ID%TYPE,
    p_message  IN OUT   varchar2) IS

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
    p_umsid := 0;
    SELECT U_Marker_Sets_Seq.NEXTVAL INTO p_umsid FROM Dual;
    INSERT INTO U_MARKER_SETS VALUES
      (p_umsid, p_name, p_comm, p_pid,
	  p_sid, p_id, SYSDATE);

  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;
----------------------------------------
  PROCEDURE Update_U_Marker_Set (
    p_umsid	   IN 	   U_MARKER_SETS.UMSID%TYPE,
    p_name	   IN	   U_MARKER_SETS.NAME%TYPE,
    p_comm	   IN	   U_MARKER_SETS.COMM%TYPE,
    p_id	   IN 	   U_MARKER_SETS.ID%TYPE,
    p_message  IN OUT  varchar2) IS

    l_name 	  U_MARKER_SETS.NAME%TYPE;
    l_comm 	  U_MARKER_SETS.COMM%TYPE;
    l_id 	  U_MARKER_SETS.ID%TYPE;
    l_ts 	  U_MARKER_SETS.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
	    select name, comm, id, ts INTO
          l_name, l_comm, l_id, l_ts FROM
          U_Marker_Sets where
          umsid = p_umsid;
        insert into U_Marker_Sets_Log Values(
		  p_umsid, l_name, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the marker set
    begin
	  if l_ok then
	    update U_Marker_Sets set name = p_name,
          comm = p_comm, id = p_id, ts = SYSDATE
          where umsid = p_umsid;
	  end if;
    exception
	  when others then
	    p_message := 'Failed to update the unified marker set [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_U_Marker_Set (
    p_umsid	    in	    U_MARKER_SETS.UMSID%TYPE,
    p_message	in out  varchar2)  is


  begin
    p_message := '';
	delete from u_marker_sets_log where umsid = p_umsid;
    delete from u_marker_sets where umsid = p_umsid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_U_Marker_Set_Link (
    p_umsid	        in	     U_MARKER_SETS.UMSID%TYPE,
    p_u_mark_name	in		 U_MARKERS.NAME%TYPE,
    p_u_position	in		 U_POSITIONS.VALUE%TYPE,
	p_pid			in		 U_MARKER_SETS.PID%TYPE,
    p_sid			in		 U_MARKERS.SID%TYPE,
    p_id			in		 U_POSITIONS.ID%TYPE,
    p_message	 	in out   varchar2) is

    l_umid 		U_MARKERS.UMID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find the umid for the unified marker
    begin
	  if l_ok then
	    select umid into l_umid from U_Markers
      	where pid = p_pid and
			  sid = p_sid and
			  name = p_u_mark_name ;
	  end if;
	exception
	  when NO_DATA_FOUND then
	  	p_message := 'The unfified marker [' || p_u_mark_name || '] does not exist.';
		l_ok := false;
	  when others then
	    p_message := 'Failed to retrieve id for the unified marker [' || p_u_mark_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the link
    begin
	  if l_ok then
        insert into U_Positions values(
		  p_umsid, l_umid, p_u_position, p_id, SYSDATE);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the unified position-link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_U_Marker_Set_Link (
    p_umsid	        in	     U_POSITIONS.UMSID%TYPE,
    p_umid			in		 U_POSITIONS.UMID%TYPE,
    p_u_position	in		 U_POSITIONS.VALUE%TYPE,
    p_id			in		 U_POSITIONS.ID%TYPE,
    p_message	 	in out   varchar2) is


	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Create the link
    begin
	  if l_ok then
        insert into U_Positions values(
		  p_umsid, p_umid, p_u_position, p_id, SYSDATE);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the unified position-link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Update_U_Marker_Set_Link (
    p_umsid	    in	     U_POSITIONS.UMSID%TYPE,
    p_umid		in		 U_POSITIONS.UMID%TYPE,
    p_position	in		 U_POSITIONS.VALUE%TYPE,
    p_id		in		 U_POSITIONS.ID%TYPE,
    p_message	in out   varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Update the link
    begin
	  if l_ok then
        update U_Positions set value = p_position,
			   			   	   		   id = p_id,
									   ts = sysdate
		  where umsid = p_umsid and umid = p_umid;
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to update the unified position-link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Delete_U_Marker_Set_Link (
    p_umsid	   in	  U_POSITIONS.UMSID%TYPE,
    p_umid	   in	  U_POSITIONS.UMID%TYPE,
    p_message  in out varchar2) is

  begin
    p_message := '';
    delete from U_Positions
      where umsid = p_umsid and umid = p_umid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_U_Allele (
    p_uaid	    in out	U_ALLELES.UAID%TYPE,
    p_name	    in		U_ALLELES.NAME%TYPE,
    p_comm		in		U_ALLELES.COMM%TYPE,
    p_umname	in		U_MARKERS.NAME%TYPE,
    p_sid		in		U_MARKERS.SID%TYPE,
    p_id	    in		U_ALLELES.ID%TYPE,
    p_message	in out  varchar2) is

    l_umid int;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
    p_uaid := 0;
  -- Find the umid for the unified marker
    begin
	  if l_ok then
	    select umid into l_umid from U_Markers
          where sid = p_sid and
		     	name = p_umname;
	  end if ;
	exception
	  when others then
	    p_message := 'The unified marker [' || p_umname || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the allele
    begin
	  if l_ok then
        select U_Alleles_Seq.Nextval into p_uaid from Dual;
        insert into U_Alleles values(
		  p_uaid, p_name, p_comm, l_umid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the unified allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_U_Allele (
    p_uaid	    in out	U_ALLELES.UAID%TYPE,
    p_name	    in		U_ALLELES.NAME%TYPE,
    p_comm		in		U_ALLELES.COMM%TYPE,
    p_umid		in		U_MARKERS.UMID%TYPE,
    p_id	    in		U_ALLELES.ID%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
    p_uaid := 0;
  -- Create the allele
    begin
	  if l_ok then
        select U_Alleles_Seq.Nextval into p_uaid from Dual;
        insert into U_Alleles values(
		  p_uaid, p_name, p_comm, p_umid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the unified allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Update_U_Allele (
    p_uaid	    in	    U_ALLELES.UAID%TYPE,
    p_name		in		U_ALLELES.NAME%TYPE,
    p_comm		in		U_ALLELES.COMM%TYPE,
    p_id		in		U_ALLELES.ID%TYPE,
    p_message	in out  varchar2) is

    l_uaid		U_ALLELES.UAID%TYPE;
    l_name		U_ALLELES.NAME%TYPE;
    l_comm		U_ALLELES.COMM%TYPE;
    l_id		U_ALLELES.ID%TYPE;
    l_ts		U_ALLELES.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
        select uaid, name, comm, id, ts into l_uaid, l_name, l_comm, l_id, l_ts
          from U_Alleles
          where uaid = p_uaid;
        insert into U_Alleles_Log values(l_uaid, l_name, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Update the allele
    begin
	  if l_ok then
        update U_Alleles set
          name = p_name, comm = p_comm, id = p_id, ts = SYSDATE
          where uaid = p_uaid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the unified allele. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Delete_U_Allele (
    p_uaid	   in	   U_ALLELES.UAID%TYPE,
    p_message  in out  varchar2) is

  begin
    p_message := '';
	delete from u_alleles_log where uaid = p_uaid;
    delete from U_Alleles where uaid = p_uaid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  PROCEDURE Create_U_Marker_Mapping (
	p_pid		IN		  R_UMID_MID.PID%TYPE,
	p_umid		IN		  R_UMID_MID.UMID%TYPE,
	p_mid		IN		  R_UMID_MID.MID%TYPE,
    p_message	IN OUT    varchar2) IS

    l_suid 		R_UMID_MID.SUID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Find Suid
    begin
	  if l_ok then
	    select suid into l_suid from Markers where mid=p_mid;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Unable to read from markers. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end ;
  -- Create the mapping link
    begin
	  if l_ok then
        insert into R_UMID_MID Values(
		  p_pid, l_suid, p_umid, p_mid, sysdate);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the mapping link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  procedure Create_U_Marker_Mapping (
	p_umid 		 IN OUT		U_Markers.UMID%TYPE,
	p_mid		 IN OUT		Markers.MID%TYPE,
	p_pid 		 IN			R_UMID_MID.PID%TYPE,
	p_umname	 in			U_MARKERS.NAME%TYPE,
	p_suname	 in			Sampling_Units.NAME%TYPE,
	p_mname		 in			Markers.name%TYPE,
	p_message	 in out		varchar2) is

	l_suid		 Sampling_units.SUID%TYPE := 0;
	l_ok		 boolean := true;
	l_old_mid    R_UMID_MID.MID%TYPE;

  	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);
  BEGIN
  	p_message := '';
	-- Find umid for the unified marker.
	begin
	  if l_ok then
	    select umid into p_umid from U_Markers
		  where pid = p_pid and
		  		name=p_umname;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'The unified marker [' || p_umname || '] does not ' ||
				  	 'exist for this project.';
	end;
  -- Find suid for the sampling unit
    begin
	  if l_ok then
	    select suid into l_suid
		  from sampling_units
		  where name=p_suname;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'The sampling unit [' || p_suname || '] does not ' ||
				  	 'exist.';
	end ;
  -- Find mid for the marker
    begin
	  if l_ok then
	    select mid into p_mid from markers
		  where suid=l_suid and
		  		name = p_mname;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'The marker [' || p_mname || '] does not exist for ' ||
				  	 'the sampling unit [' || p_suname || '].';
	end;
  -- Delete the old mapping between these markers.
    begin
      if l_ok then
	    select MID into l_old_mid from r_umid_mid where umid= p_umid and
		suid = l_suid;

		delete from r_umid_mid
	  	   where umid=p_umid and
		      	 mid = l_old_mid;

		  delete from r_uaid_aid where
	  	   uaid in (select uaid from u_alleles where umid=p_umid) and
		   aid in (select aid from alleles where mid=l_old_mid);

	  end if;
	exception
	  when others then
	  null;
	end;
  -- Create the new mapping
    begin
	  if l_ok then
	    insert into r_umid_mid values(
		  p_pid, l_suid, p_umid, p_mid, sysdate);
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to create the new mapping. ' ||
				  	 'ORACEL error: ' || SQLERRM;
	end;

end;
----------------------------------------
  procedure Delete_U_Marker_Mapping (
    p_umid	    in	    R_UMID_MID.UMID%TYPE,
	p_mid		in		R_UMID_MID.MID%TYPE,
    p_message	in out  varchar)  is

  begin
    p_message := '';
	delete from r_uaid_aid where umid=p_umid;
	delete from r_umid_mid where umid=p_umid and mid=p_mid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  PROCEDURE Create_U_Allele_Mapping (
	p_pid		IN		  R_UAID_AID.PID%TYPE,
	p_uaid		IN		  R_UAID_AID.UAID%TYPE,
	p_aid		IN		  R_UAID_AID.AID%TYPE,
    p_message	IN OUT    varchar2) IS

    l_umid 		R_UAID_AID.UMID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Find umid
    begin
	  if l_ok then
	    select umid into l_umid from U_Alleles where uaid=p_uaid;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Unable to read from unified alleles. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end ;
  -- Create the mapping link
    begin
	  if l_ok then
        insert into R_UAID_AID Values(
		  p_pid, l_umid, p_aid, p_uaid, sysdate);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the mapping link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  procedure Create_U_Allele_Mapping (
	p_pid 		  IN		R_UAID_AID.PID%TYPE,
	p_mid		  IN		ALLELES.MID%TYPE,
	p_aname		  IN		ALLELES.NAME%TYPE,
	p_umid		  IN		U_ALLELES.UMID%TYPE,
	p_uaname	  IN		U_ALLELES.NAME%TYPE,
	p_message	 in out		varchar2) is

	l_uaid		 U_ALLELES.UAID%TYPE := 0;
	l_aid		 ALLELES.AID%TYPE := 0;
	l_ok		 boolean := true;

  	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);
  BEGIN
  	p_message := '';
	-- Find uaid for the unified allele
	begin
	  if l_ok then
	    select uaid into l_uaid from U_ALLELES
		  where umid=p_umid and
		  		name=p_uaname;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'The unified allele [' || p_uaname || '] does not ' ||
				  	 'exist for this unified marker.';
	end;
  -- Find aid for the allele
    begin
	  if l_ok then
	    select aid into l_aid from alleles
		  where mid=p_mid and
		  		name=p_aname;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'The allele [' || p_aname || '] does not exist for ' ||
				  	 'this marker.';
	end;
  -- Delete the old mapping between these alleles
    begin
      if l_ok then
	    delete from r_uaid_aid where
	  	   aid=l_aid and
		   uaid=l_uaid;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to remove old mappings for alleles. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;
  -- Create the new mapping
    begin
	  if l_ok then
	    insert into r_uaid_aid values(
		  p_pid, p_umid, l_aid, l_uaid, sysdate);
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to create the new mapping. ' ||
				  	 'ORACEL error: ' || SQLERRM;
	end;

end;

----------------------------------------

  procedure Delete_U_Allele_Mapping (
    p_pid	    in	    R_UAID_AID.PID%TYPE,
	p_aid		in		R_UAID_AID.AID%TYPE,
    p_message	in out  varchar)  is

  begin
    p_message := '';
	delete from r_uaid_aid
	  where
		   pid=p_pid and
		   aid=p_aid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  PROCEDURE Create_U_Variable (
    p_uvid	   IN OUT	U_VARIABLES.UVID%TYPE,
    p_name	   IN		U_VARIABLES.NAME%TYPE,
    p_type	   IN		U_VARIABLES.TYPE%TYPE,
    p_unit	   IN		U_VARIABLES.UNIT%TYPE,
    p_comm	   IN		U_VARIABLES.COMM%TYPE,
	p_pid	   IN		U_VARIABLES.PID%TYPE,
    p_sid	   IN		U_VARIABLES.SID%TYPE,
    p_id	   IN 		U_VARIABLES.ID%TYPE,
    p_message  IN OUT  	varchar2) IS

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

  BEGIN
    p_message := '';
    p_uvid := 0;

    select U_Variables_Seq.nextval into p_uvid from dual;

    insert into U_Variables Values
      (p_uvid, p_name, p_type, p_unit,
       p_comm, p_pid, p_sid, p_id, SYSDATE);

  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
  PROCEDURE Update_U_Variable (
    p_uvid	    IN 	    U_VARIABLES.UVID%TYPE,
    p_name		IN		U_VARIABLES.NAME%TYPE,
    p_type		IN		U_VARIABLES.TYPE%TYPE,
    p_unit		IN		U_VARIABLES.UNIT%TYPE,
    p_comm		IN		U_VARIABLES.COMM%TYPE,
    p_id		IN 		U_VARIABLES.ID%TYPE,
    p_message	IN OUT  varchar2) IS

    l_name 		U_VARIABLES.NAME%TYPE;
    l_type 		U_VARIABLES.UNIT%TYPE;
    l_unit 		U_VARIABLES.UNIT%TYPE;
    l_comm 		U_VARIABLES.COMM%TYPE;
    l_id 		U_VARIABLES.ID%TYPE;
    l_ts 		U_VARIABLES.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
        select name, type, unit, comm, id, ts INTO
          l_name, l_type, l_unit, l_comm, l_id, l_ts FROM
          U_Variables where
          uvid = p_uvid;
	    insert into U_Variables_Log Values(
		  p_uvid, l_name, l_type, l_unit, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the variable
    begin
	  if l_ok then
        update U_Variables set name = p_name, type = p_type,
          unit = p_unit, comm = p_comm, id = p_id, ts = SYSDATE
          where uvid = p_uvid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the unified variable [' || p_name || ']. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_U_Variable (
    p_uvid	  	in	    U_VARIABLES.UVID%TYPE,
    p_message   in out  varchar2)  is

  -- Delete R_UVid_Vid
  -- Delete u variable log

  begin
    p_message := '';
	delete from u_variables_log where uvid = p_uvid;
    delete from U_Variables where uvid = p_uvid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

PROCEDURE Create_U_Variable_Mapping (
	p_pid		IN		  R_UVID_VID.PID%TYPE,
	p_suid		IN		  R_UVID_VID.SUID%TYPE,
	p_uvid		IN		  R_UVID_VID.UVID%TYPE,
	p_vid		IN		  R_UVID_VID.VID%TYPE,
	p_message	IN OUT    varchar2) IS

    --l_suid 		R_UMID_MID.SUID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
   -- Create the mapping link
    begin
	  if l_ok then
        insert into R_UVID_VID Values(
		  p_pid, p_suid, p_uvid, p_vid, sysdate);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the mapping link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------
-- for file import ---

PROCEDURE Create_U_Variable_Mapping (
	p_pid		IN		  R_UVID_VID.PID%TYPE,
	p_suname	IN		  SAMPLING_UNITS.NAME%TYPE,
	p_uvname	IN		  U_VARIABLES.NAME%TYPE,
	p_vname		IN		  VARIABLES.NAME%TYPE,
	p_message	IN OUT    varchar2) IS

    --l_suid 		R_UMID_MID.SUID%TYPE;

	l_ok BOOLEAN := true;
	l_suid SAMPLING_UNITS.SUID%TYPE;
	l_uvid U_VARIABLES.UVID%TYPE;
	l_vid VARIABLES.VID%TYPE;
    l_sid SAMPLING_UNITS.SID%TYPE;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
    l_suid := 0;

	--- get suid

	begin
	select SUID, SID into l_suid, l_sid from SAMPLING_UNITS where NAME = p_suname;
	exception
	  when others then
	    l_ok := false;
		p_message := 'The Sampling Unit  [' || p_suname || '] does not ' ||
				  	 'exist in this project.';
	end;

	--- get uvid
   if l_ok then
	begin
	select UVID into l_uvid from U_VARIABLES where NAME = p_uvname AND PID = p_pid and SID = l_sid;
	exception
	  when others then
	    l_ok := false;
		p_message := 'The Unified Variable  [' || p_uvname || '] does not ' ||
				  	 'exist in this project for the given species';
	end;
  end if;

	--- get vid
   if l_ok then
	begin
	select VID into l_vid from VARIABLES where NAME = p_vname
    and SUID = l_suid;
	exception
	  when others then
	    l_ok := false;
		p_message := 'The  Variable  [' || p_vname || '] does not ' ||
				  	 'exist in the sampling unit [' || p_suname ||']';
	end;
  end if;

	-- delete if mapping exists
    begin
      if l_ok then
	    delete from r_uvid_vid
	  	   where UVID=l_uvid and SUID = l_suid and PID = p_pid;
	  end if;
	exception
	  when others then
	    l_ok := false;
		p_message := 'Failed to remove old mappings for Variable.' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;

	-- insert new mapping

	begin
	  if l_ok then
        insert into R_UVID_VID Values(p_pid, l_suid, l_uvid, l_vid, sysdate);
	  end if ;
	exception
	  when others then
	    p_message := 'Failed to create the mapping link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

 procedure Delete_U_Variable_Mapping (
    p_suid 		in      R_UVID_VID.SUID%TYPE,
	p_pid		in      R_UVID_VID.PID%TYPE,
	p_uvid	    in	    R_UVID_VID.UVID%TYPE,
	p_vid		in		R_UVID_VID.VID%TYPE,
    p_message	in out  varchar)  is

  begin
    p_message := '';
	delete from r_uvid_vid where uvid=p_uvid and vid=p_vid and pid=p_pid and suid = p_suid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  PROCEDURE Create_U_Variable_Set (
    p_uvsid	    IN OUT	  U_VARIABLE_SETS.UVSID%TYPE,
    p_name		IN		  U_VARIABLE_SETS.NAME%TYPE,
    p_comm		IN		  U_VARIABLE_SETS.COMM%TYPE,
	p_pid		IN		  U_VARIABLE_SETS.PID%TYPE,
    p_sid		IN		  U_VARIABLE_SETS.SID%TYPE,
    p_id		IN 		  U_VARIABLE_SETS.ID%TYPE,
    p_message	IN OUT    varchar2) IS

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

  BEGIN
    p_message := '';
    p_uvsid := 0;
    SELECT U_Variable_Sets_Seq.NEXTVAL INTO p_uvsid FROM Dual;
    INSERT INTO U_Variable_SETS VALUES
      (p_uvsid, p_name, p_comm, p_pid,
	  p_sid, p_id, SYSDATE);

  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;
----------------------------------------
  PROCEDURE Update_U_Variable_Set (
    p_uvsid	    IN 	    U_VARIABLE_SETS.UVSID%TYPE,
    p_name		IN		U_VARIABLE_SETS.NAME%TYPE,
    p_comm		IN		U_VARIABLE_SETS.COMM%TYPE,
    p_id		IN 		U_VARIABLE_SETS.ID%TYPE,
    p_message	IN OUT  varchar2) IS

    l_name 		U_VARIABLE_SETS.NAME%TYPE;
    l_comm 		U_VARIABLE_SETS.COMM%TYPE;
    l_id 		U_VARIABLE_SETS.ID%TYPE;
    l_ts 		U_VARIABLE_SETS.TS%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED EXCEPTION;
    PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED EXCEPTION;
    PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

  BEGIN
    p_message := '';
  -- Log the old data
    begin
	  if l_ok then
	    select name, comm, id, ts INTO
          l_name, l_comm, l_id, l_ts FROM
          U_Variable_Sets where
          uvsid = p_uvsid;
        insert into U_Variable_Sets_Log Values(
		  p_uvsid, l_name, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
 	end;
  -- Update the variable set
    begin
	  if l_ok then
	    update U_Variable_Sets set name = p_name,
          comm = p_comm, id = p_id, ts = SYSDATE
          where uvsid = p_uvsid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the unified variable set. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  EXCEPTION
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  END;

----------------------------------------

  procedure Delete_U_Variable_Set (
    p_uvsid	    in	    U_VARIABLE_SETS.UVSID%TYPE,
    p_message	in out  varchar2)  is

  -- Delete U_R_Var_Set
  -- Delete u variable set logs

  begin
    p_message := '';
	delete from u_variable_sets_log where uvsid = p_uvsid;
    delete from U_Variable_Sets where uvsid = p_uvsid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_U_Variable_Set_Link (
    p_uvsid	        in	    R_U_VAR_SET.UVSID%TYPE,
    p_u_var_name	in		U_VARIABLES.NAME%TYPE,
	p_pid			in		U_VARIABLES.PID%TYPE,
    p_sid			in		U_VARIABLES.SID%TYPE,
    p_id			in		R_U_VAR_SET.ID%TYPE,
    p_message		in out  varchar2) is

    l_uvid 		R_U_VAR_SET.UVID%TYPE;

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find the uvid for the unified variable
    begin
	  if l_ok then
	    select uvid into l_uvid from U_Variables
          where pid = p_pid and
		  		sid = p_sid and
		  		name = p_u_var_name ;
	  end if;
	exception
	  when others then
	    p_message := 'The unified variable [' || p_u_var_name || '] does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Create the link
    begin
	  if l_ok then
        insert into R_U_Var_Set values(p_uvsid, l_uvid, p_pid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the unified variable set link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_U_Variable_Set_Link (
    p_uvsid	        in	    R_U_VAR_SET.UVSID%TYPE,
    p_uvid			in		R_U_VAR_SET.UVID%TYPE,
	p_pid			in		R_U_VAR_SET.PID%TYPE,
    p_id			in		R_U_VAR_SET.ID%TYPE,
    p_message		in out  varchar2) is


	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Create the link
    begin
	  if l_ok then
        insert into R_U_Var_Set values(p_uvsid, p_uvid, p_pid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the unified variable set link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Delete_U_Variable_Set_Link (
    p_uvsid	    in	    R_U_VAR_SET.UVSID%TYPE,
    p_uvid		in		R_U_VAR_SET.UVID%TYPE,
    p_message	in out  varchar2) is

  begin
    p_message := '';
    delete from R_U_Var_Set
      where uvsid = p_uvsid and
	  		uvid = p_uvid;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Create_User (
    p_id	  in out	USERS.ID%TYPE,
    p_usr	  in		USERS.USR%TYPE,
	p_pwd	  in		USERS.PWD%TYPE,
    p_name	  in		USERS.NAME%TYPE,
	p_status  in		USERS.STATUS%TYPE,
    p_message in out  	varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find the id for the user
    begin
	  if l_ok then
	    select Users_Seq.nextval into p_id from dual ;
	  end if;
	exception
	  when others then
	    p_message := 'Internal error (Unable to step up user sequence). ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Create the user
    begin
	  if l_ok then
        insert into Users values(
		  p_id, p_usr, p_pwd, p_name, p_status);
	  end if;
	exception
	  when DUP_VAL_ON_INDEX then
	    p_message := 'The user already exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	  when others then
	    p_message := 'Failed to create the user. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Update_User (
    p_id	  in 	  USERS.ID%TYPE,
    p_usr	  in	  USERS.USR%TYPE,
	p_pwd	  in	  USERS.PWD%TYPE,
    p_name	  in	  USERS.NAME%TYPE,
	p_status  in	  USERS.STATUS%TYPE,
    p_message in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Update the user
    begin
	  if l_ok then
        Update Users set
		  usr = p_usr, pwd = p_pwd,
		  name = p_name, status = p_status
		  where
		    id = p_id ;
	  end if;
	exception
	  when DUP_VAL_ON_INDEX then
	    p_message := 'There is a naming conflict. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Delete_User (
    p_id	  in 	  USERS.ID%TYPE,
    p_message in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);
  -- This is NOT a good idea
  -- There are many views that depend on that the
  -- user exist. Perhaps it's better to somhow
  -- disable a user.

  begin
    p_message := '';
  -- Delete the user
    begin
	  if l_ok then
--        Delete from Users
--		  where id = p_id ;
 		  update users set status='D' where id = p_id;
	  end if;
	end ;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_Role (
    p_rid	  in out	ROLES_.RID%TYPE,
	p_pid	  in 		ROLES_.PID%TYPE,
    p_name	  in		ROLES_.NAME%TYPE,
	p_comm	  in		ROLES_.COMM%TYPE,
    p_message in out  	varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find the rid for the role
    begin
	  if l_ok then
	    select Roles_Seq.nextval into p_rid from dual ;
	  end if;
	exception
	  when others then
	    p_message := 'Internal error (Unable to step up roles sequence). ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Create the role
    begin
	  if l_ok then
        insert into Roles_ values(
		  p_rid, p_pid, p_name, p_comm);
	  end if;
	exception
	  when DUP_VAL_ON_INDEX then
	    p_message := 'The user already exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	  when others then
	    p_message := 'Failed to create the user. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Update_Role (
    p_rid	  in 	  ROLES_.RID%TYPE,
    p_name	  in	  ROLES_.NAME%TYPE,
	p_comm	  in	  ROLES_.COMM%TYPE,
    p_message in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Update the role
    begin
	  if l_ok then
        Update Roles_ set
		  name = p_name, comm = p_comm
		  where
		    rid = p_rid ;
	  end if;
	exception
	  when DUP_VAL_ON_INDEX then
	    p_message := 'There is a naming conflict. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Delete_Role (
    p_rid	  in 	  ROLES_.RID%TYPE,
    p_message in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);
  begin
    p_message := '';
  -- Delete from R_PRJ_RID (on delete cascade pid, rid)
    begin
	  if l_ok then
	  	 delete from roles_ where rid=p_rid;
	  end if;
	end ;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Add_Privilege (
    p_rid	  in 	  R_ROL_PRI.RID%TYPE,
	p_prid	  in	  R_ROL_PRI.PRID%TYPE,
    p_message in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);
  begin
    p_message := '';
	begin
	  if l_ok then
	    insert into r_rol_pri values(p_rid, p_prid);
	  end if;
	exception
	  when others then
	    p_message := 'The role or privilege does not exist.';
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Add_Privilege (
    p_rid	  in 	  R_ROL_PRI.RID%TYPE,
	p_pname	  in	  PRIVILEGES_.NAME%TYPE,
    p_message in out  varchar2) is

	l_ok BOOLEAN := true;
	l_prid R_ROL_PRI.PRID%TYPE;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);
  begin
    p_message := '';
	-- Look up the privilege id for this privilege
	begin
	  if l_ok then
	    select prid into l_prid from privileges_
		  where name=p_pname;
	  end if;
	exception
	  when others then
	    p_message := 'The privilege [' || p_pname || '] does not exist';
		l_ok := false;
	end;
	begin
	  if l_ok then
	    insert into r_rol_pri values(p_rid, l_prid);
	  end if;
	exception
	  when others then
	    p_message := 'The role does not exist';
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Remove_Privilege (
    p_rid	  in 	  R_ROL_PRI.RID%TYPE,
	p_prid	  in	  R_ROL_PRI.PRID%TYPE,
    p_message in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);
  begin
    p_message := '';
	if l_ok then
	  delete from r_rol_pri
	    where rid = p_rid and prid = p_prid ;
	end if;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Remove_All_Privileges (
    p_rid	  in 	  R_ROL_PRI.RID%TYPE,
    p_message in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);
  begin
    p_message := '';
	if l_ok then
	  delete from r_rol_pri
	    where rid = p_rid ;
	end if;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------

  procedure Create_Species_Link (
    p_pid	    in	    R_PRJ_SPC.PID%TYPE,
    p_sid		in		R_PRJ_SPC.SID%TYPE,
    p_message	in out  varchar2) is

	l_temp		NUMBER;
	l_ok 		BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Check if the species exist
    begin
	  if l_ok then
	    select sid into l_temp from species where
		  sid = p_sid;
	  end if;
	exception
	  when others then
	    p_message := 'The species does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Check if the project exist
    begin
      if l_ok then
	    select pid into l_temp from projects where
	      pid = p_pid ;
	  end if;
    exception
	  when others then
	    p_message := 'The project does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the link
    begin
	  if l_ok then
        insert into R_PRJ_SPC values(
		  p_pid, p_sid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the species link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Delete_Species_Link (
    p_pid	    in	    R_PRJ_SPC.PID%TYPE,
    p_sid		in		R_PRJ_SPC.SID%TYPE,
    p_message	in out  varchar2) is

	l_ok       BOOLEAN := true;
	-- The number of sampling units of this
	-- species linked to this project.
	l_num      NUMBER := 0;
	-- The species name in case of an error
	l_sname	   SPECIES.NAME%TYPE;

  begin
    p_message := '';
  -- Check if there are at least one sampling
  -- unit of this species linked to this project
    select count(r.suid) into l_num from
	  R_Prj_Su r, Sampling_Units su
	  where r.pid = p_pid and
	  		r.suid = su.suid and
			su.sid = p_sid;
	if l_num > 0 then
	-- Find the species name
	  select name into l_sname from
	    Species
		where sid = p_sid ;
	  p_message := 'There are ' || l_num ||
	  			   ' sampling unit(s) of the species ' ||
				   l_sname || ' linked into this project.';
	  l_ok := false;
	end if ;
	if l_ok then
      delete from R_Prj_Spc
        where pid = p_pid and sid = p_sid;
	end if;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Create_User_Link (
    p_pid	    in	    R_PRJ_ROL.PID%TYPE,
	p_rid		in		R_PRJ_ROL.RID%TYPE,
    p_id		in		R_PRJ_ROL.ID%TYPE,
    p_message	in out  varchar2) is

	l_temp		NUMBER;
	l_ok 		BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Check if the user exist
    begin
	  if l_ok then
	    select id into l_temp from Users where
		  id = p_id;
	  end if;
	exception
	  when others then
	    p_message := 'The user id does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Check if the project
    begin
	  if l_ok then
	    select pid into l_temp from Projects where
		  pid = p_pid;
	  end if;
	exception
	  when others then
	    p_message := 'The project does not exist. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the link
    begin
	  if l_ok then
        insert into R_PRJ_ROL values(
		  p_pid, p_id, p_rid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Update_User_Link (
    p_pid	    in	    R_PRJ_ROL.PID%TYPE,
    p_id		in		R_PRJ_ROL.ID%TYPE,
	p_rid		in		R_PRJ_ROL.RID%TYPE,
    p_message	in out  varchar2) is

	l_temp		NUMBER;
	l_ok 		BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Update the link
    if l_ok then
      Update R_PRJ_ROL set rid=p_rid where
		  pid = p_pid and id = p_id;
	end if;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Delete_User_Link (
    p_pid	    in	    R_PRJ_ROL.PID%TYPE,
    p_id		in		R_PRJ_ROL.ID%TYPE,
    p_message	in out  varchar2) is

	l_ok       BOOLEAN := true;

  begin
    p_message := '';
	if l_ok then
      delete from R_PRJ_ROL
        where pid = p_pid and id = p_id;
	end if;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Create_SU_Link (
    p_pid	    in	    R_PRJ_SU.PID%TYPE,
    p_suid		in		R_PRJ_SU.SUID%TYPE,
    p_message	in out  varchar2) is


	l_su_sid 	Sampling_units.SID%TYPE;
	l_sp_incl	NUMBER := 0;
	l_ok  		BOOLEAN := true;


    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Find the sampling unit's sid
    begin
	  if l_ok then
  	    select sid into l_su_sid from
	      sampling_units where suid = p_suid;
	  end if ;
	exception
	  when others then
	    p_message := 'Internal error (Sampling unit lacks a species type). ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Check that this project includes this species
  	begin
	  if l_ok then
	    select count(sid) into l_sp_incl
		  from R_Prj_Spc
		  where pid = p_pid and
		  		sid = l_su_sid;
		if l_sp_incl < 1 then
		  -- This project doesn't include this species
		  p_message := 'The sampling unit is not of a correct species. ';
		  l_ok := false;
		end if;
	  end if;
	exception
	  when others then
	    p_message := 'Unable to determine wether or not this project includes this species. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the link
    begin
	  if l_ok then
        insert into R_Prj_Su values(
		  p_pid, p_suid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the link. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Delete_SU_Link (
    p_pid	    in	    R_PRJ_SU.PID%TYPE,
    p_suid		in		R_PRJ_SU.SUID%TYPE,
    p_message	in out  varchar2) is

	l_ok       BOOLEAN := true;

  begin
    p_message := '';
	if l_ok then
      delete from R_PRj_Su
        where pid = p_pid and suid = p_suid;
	end if;
  exception
    when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Create_Project (
    p_pid	    in out  PROJECTS.PID%TYPE,
	p_name		in		PROJECTS.NAME%TYPE,
	p_comm		in		PROJECTS.COMM%TYPE,
	p_status	in		PROJECTS.STATUS%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
	p_pid := 0;
  -- Check the status parameter
    begin
	  if l_ok then
	    if p_status not in ('E', 'D') then
		  p_message := 'Illegal status [' || p_status || ']';
		  l_ok := false;
		end if;
	  end if;
	exception
	  when others then
	    p_message := 'Unable to determine value on status [' || p_status || ']' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Find the pid
    begin
	  if l_ok then
	    select Projects_Seq.nextval into p_pid from dual;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to step up project sequence. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the project
    begin
	  if l_ok then
        insert into projects values(
		  p_pid, p_name, p_comm, p_status);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Update_Project (
    p_pid	    in	    PROJECTS.PID%TYPE,
	p_name		in		PROJECTS.NAME%TYPE,
	p_comm		in		PROJECTS.COMM%TYPE,
	p_status	in		PROJECTS.STATUS%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Check the status parameter
    begin
	  if l_ok then
	    if p_status not in ('E', 'D') then
		  p_message := 'Illegal status [' || p_status || ']';
		  l_ok := false;
		end if;
	  end if;
	exception
	  when others then
	    p_message := 'Unable to determine value on status [' || p_status || ']' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Update the project
    begin
	  if l_ok then
        update projects set name = p_name,
			   				comm = p_comm,
							status = p_status
		  where pid = p_pid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Delete_Project (
    p_pid	    in	    PROJECTS.PID%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Delete all unified allele logs
    begin
	  if l_ok then
	    delete from U_Alleles_Log where
		  uaid in (select uaid from V_U_Alleles_3 where pid = p_pid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified alleles for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Delete all unified alleles
    begin
	  if l_ok then
	    delete from U_Alleles where
		  umid in (select umid from U_Markers
		             where pid = p_pid) ;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified alleles for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Delete all unified marker logs
    begin
	  if l_ok then
	    delete from U_Markers_Log where
		  umid in (select umid from U_Markers where pid = p_pid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified marker logs for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Delete all unified markers
    begin
	  if l_ok then
	    delete from u_markers where pid = p_pid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified markers for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Delete all unified marker set logs
    begin
	  if l_ok then
	    delete from U_Marker_Sets_Log where
		  umsid in (select umsid from U_Marker_Sets where pid = p_pid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified marker set logs, for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Delete all unified marker sets
    begin
	  if l_ok then
	    delete from U_Marker_Sets where
		  pid = p_pid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified marker sets for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Delete all unified variable log
    begin
	  if l_ok then
	    delete from U_Variables_Log where
		  uvid in (select uvid from U_Variables where pid = p_pid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified variable logs for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Delete all unified variables
    begin
	  if l_ok then
	    delete from U_Variables where
		  pid = p_pid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified variables for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Delete all unified variable set logs
    begin
	  if l_ok then
	    delete from U_Variable_Sets_Log where
		  uvsid in (select uvsid from U_Variable_Sets where pid = p_pid);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified variable set logs for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Delete all unified variable sets
    begin
	  if l_ok then
	    delete from U_Variable_Sets where
		  pid = p_pid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete all the unified variable sets for this project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  -- Delete all roles
    if l_ok then
	  delete from roles_ where pid = p_pid;
	end if;

  -- Delete the project
    begin
	  if l_ok then
	    delete from Projects where pid = p_pid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to delete project. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end ;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_Species (
    p_sid	    in out  SPECIES.SID%TYPE,
	p_name		in		SPECIES.NAME%TYPE,
	p_comm		in		SPECIES.COMM%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
	p_sid := 0;
  -- Find the sid
    begin
	  if l_ok then
	    select Species_Seq.nextval into p_sid from dual;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to step up species sequence. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
  -- Create the species
    begin
	  if l_ok then
        insert into Species values(
		  p_sid, p_name, p_comm);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the species. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Update_Species (
    p_sid	    in	    SPECIES.SID%TYPE,
	p_name		in		SPECIES.NAME%TYPE,
	p_comm		in		SPECIES.COMM%TYPE,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
  -- Update the species
    begin
	  if l_ok then
        update Species set name = p_name,
			   			   comm = p_comm
		  where sid = p_sid;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the species. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Delete_Species (
    p_sid	    in	    SPECIES.SID%TYPE,
    p_message	in out  varchar2) is

	cursor c_cids is select cid from chromosomes where sid = p_sid;
	l_ok BOOLEAN := true;
	l_cid  CHROMOSOMES.CID%TYPE;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_message := '';
	open c_cids;
	loop
	  fetch c_cids into l_cid;
	  if c_cids%FOUND then
	    Delete_Chromosome(l_cid, p_message);
		if p_message is not null and length(p_message) > 0 then
		  l_ok := false;
		end if;
	  else
	    close c_cids;
		exit;
	  end if;

	  if not l_ok then
	    exit;
	  end if;
	end loop;

	if l_ok then
	  delete from species where sid = p_sid;
	end if;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;


  procedure Create_Sample (
    p_said	in out	int,
    p_name	in	varchar2,
    p_tissue	in	varchar2,
    p_experimenter	in	varchar2,
    p_date	in	date,
    p_treatment	in	varchar2,
    p_storage	in	varchar2,
    p_comm	in	varchar2,
    p_iid	in	int,
    p_id	in	int,
    p_message	in out  varchar2) is

	l_ok  BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    p_said := 0;
	-- Find said
	begin
	  if l_ok then
        select Samples_Seq.Nextval into p_said from Dual;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to increment sample id ' ||
		  		  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
	end ;
	-- Create the row
	begin
	  if l_ok then
        insert into Samples values
      	  (p_said, p_name, p_tissue, p_experimenter, p_date,
           p_treatment, p_storage, p_comm, p_iid, p_id, SYSDATE);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to create the row. ' ||
				  	 'ORACEL error: ' || SQLERRM;
		l_ok := false;
	end;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

 procedure Create_Or_Update_Sample (
    p_identity	    in		varchar2,
    p_alias	        in		varchar2,
	p_name          in      varchar2,
    p_tissue	    in	    varchar2,
    p_experimenter	in  	varchar2,
    p_date	        in  	date,
    p_treatment	    in	    varchar2,
    p_storage	    in	    varchar2,
    p_comm	        in	    varchar2,
    p_suid	        in  	int,
    p_id	        in	    int,
    p_message	in out  varchar2) is

	l_ok  BOOLEAN := true;
	l_said int :=0;
	l_iid  int :=0;
	l_count int := 0;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    l_said := 0;

	-- check if alias or identity was sent


   begin

	if p_alias is null then
	 select IID into l_iid from INDIVIDUALS where IDENTITY=p_identity AND SUID=p_suid;
	else
	 select IID into l_iid from INDIVIDUALS where ALIAS=p_alias and SUID=p_suid;

	end if;

	-- if individual exists we can continue.
	if l_iid != 0  then
	 select count(said) into l_count from SAMPLES where IID=l_iid and NAME=p_name;
	 if l_count = 0 then
	   Create_Sample(l_said,p_name,p_tissue,p_experimenter,p_date,p_treatment,p_storage,p_comm,l_iid, p_id,p_message);
	 else
	    if l_count = 1 then
	     select SAID into l_said from SAMPLES where IID=l_iid and NAME=p_name;
		 Update_Sample(l_said,p_name,p_tissue,p_experimenter,p_date,p_treatment,p_storage,p_comm,p_id,p_message);

		end if;
	 end if;
	end if;
   end;

  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------


  procedure Update_Sample (
    p_said	in	int,
    p_name	in	varchar2,
    p_tissue	in	varchar2,
    p_experimenter	in	varchar2,
    p_date	in	date,
    p_treatment	in	varchar2,
    p_storage	in	varchar2,
    p_comm	in	varchar2,
    p_id	in	int,
    p_message	in out  varchar2) is

    l_said	int;
    l_name	varchar2(20);
    l_tissue	varchar2(20);
    l_experimenter	varchar2(20);
    l_date	date;
    l_treatment	varchar2(20);
    l_storage	varchar2(20);
    l_comm	varchar2(256);
    l_iid	int;
    l_id	int;
    l_ts	date;

    l_ok BOOLEAN := true;

    NULL_NOT_ALLOWED exception;
    pragma exception_init(NULL_NOT_ALLOWED, -1400);
    CHECK_VIOLATED exception;
    pragma exception_init(CHECK_VIOLATED, -2290);
    INTEG_VIOLATED exception;
    pragma exception_init(INTEG_VIOLATED, -2291);

  begin
    -- Log the old row
	begin
	  if l_ok then
	    select name, tissue_type, experimenter, date_,
		  treatment, storage, comm, id, ts into
		  l_name, l_tissue, l_experimenter, l_date,
		  l_treatment, l_storage, l_comm, l_id, l_ts
		  from
		  samples where said = p_said;
  	  end if;
	exception
	  when others then
	    p_message := 'Failed to read the old data.' ||
				  	 'ORACLE errror: ' || SQLERRM;
		l_ok := false;
	end;
	begin
	  if l_ok then
	    insert into samples_log values(
		  p_said, l_name, l_tissue, l_experimenter, l_date,
		  l_treatment, l_storage, l_comm, l_id, l_ts);
	  end if;
	exception
	  when others then
	    p_message := 'Failed to log the old data. ' ||
				  	 'ORACLE error: ' || SQLERRM;
		l_ok := false;
    end;
    -- Update the row
	begin
	  if l_ok then
        update Samples set
          name = p_name, tissue_type = p_tissue,
          experimenter = p_experimenter, date_ = p_date,
          treatment = p_treatment, storage = p_storage, comm = p_comm,
          id = p_id, ts = SYSDATE
          where said = p_said;
	  end if;
	exception
	  when others then
	    p_message := 'Failed to update the sample. ' ||
				  	 'ORACLE error: ' || SQLERRM;
	end ;
  exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------

  procedure Delete_Sample (
    p_said	in	int,
    p_message	in out  varchar2) is

	l_ok BOOLEAN := true;
  begin
    -- ON DELETE CASCADE for the log table
    delete from Samples
      where said = p_said;
	delete from Samples_log
	  where  said = p_said;

  exception
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_File_Generation(
  	p_fgid    in out  FILE_GENERATIONS.FGID%TYPE,
	p_name	  in 	  FILE_GENERATIONS.NAME%TYPE,
	p_mode	  in	  FILE_GENERATIONS.MODE_%TYPE,
	p_type	  in	  FILE_GENERATIONS.TYPE_%TYPE,
	p_msid	  in	  FILE_GENERATIONS.MSID%TYPE,
	p_vsid	  in	  FILE_GENERATIONS.VSID%TYPE,
	p_comm	  in	  FILE_GENERATIONS.COMM%TYPE,
	p_pid	  in	  FILE_GENERATIONS.PID%TYPE,
	p_id	  in	  FILE_GENERATIONS.ID%TYPE,
	p_message in out  varchar2) IS

	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
	CHECK_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
	INTEG_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

	begin
	  p_message := '';
	  p_fgid := 0;
	  -- Check parameters
	  begin
	    if l_ok then
		  if length(p_name) > 20 then
		    p_message := 'Name exceeds 20 characters';
			l_ok := false;
		  elsif p_mode not in ('S', 'M') then
		    p_message := 'Mode must be one of S, M';
			l_ok := false;
		  elsif length(p_type) > 20 then
		    p_message := 'Type exceeds 20 charcters';
			l_ok := false;
		  elsif length(p_comm) > 256 then
		    p_message := 'Comment exceeds 256 charcters';
			l_ok := false;
		  end if;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to check the parameters ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end ;
	-- Find FGID
	  begin
	    if l_ok then
		  select File_Generations_Seq.nextval into p_fgid from dual;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to increment file generation id. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	-- Create the file generation row
	  begin
	    if l_ok then
		  insert into File_Generations Values(
		    p_fgid, p_name, p_mode, p_type, p_msid, p_vsid,
			p_comm, p_pid, 0, p_id, sysdate);
		end if;
	  exception
	    when others then
		  p_message := 'Failed to create the file generation row. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Update_File_Generation(
  	p_fgid    in 	  FILE_GENERATIONS.FGID%TYPE,
	p_name	  in 	  FILE_GENERATIONS.NAME%TYPE,
	p_comm	  in	  FILE_GENERATIONS.COMM%TYPE,
	p_id	  in	  FILE_GENERATIONS.ID%TYPE,
	p_message in out  varchar2) IS

	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
	CHECK_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
	INTEG_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

	begin
	  p_message := '';
	  -- Check parameters
	  begin
	    if l_ok then
		  if length(p_name) > 20 then
		    p_message := 'Name exceeds 20 characters';
			l_ok := false;
		  elsif length(p_comm) > 256 then
		    p_message := 'Comment exceeds 256 charcters';
			l_ok := false;
		  end if;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to check the parameters ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end ;
	-- Update the row
	  begin
	    if l_ok then
		  update File_Generations set name=p_name, comm=p_comm
		    where fgid=p_fgid;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to update the file generation row. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

  procedure Abort_File_Generation(
  	p_fgid    in      FILE_GENERATIONS.FGID%TYPE,
	p_message in out  varchar2) IS

	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
	CHECK_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
	INTEG_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

	begin
	  p_message := '';
	-- Update the file generation row
	  begin
	    if l_ok then
		  update File_Generations set
		    abort_ = 1 where fgid = p_fgid;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to abort the file generation. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

  procedure Delete_File_Generation(
  	p_fgid    in      FILE_GENERATIONS.FGID%TYPE,
	p_message in out  varchar2) IS

	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
	CHECK_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
	INTEG_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

	begin
	  p_message := '';
	  -- Delete all data files
	  begin
	    if l_ok then
		  delete from data_files where fgid = p_fgid;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to delete the data files. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end ;
	-- Delete the file generations
	  begin
	    if l_ok then
		  delete from file_generations where fgid = p_fgid;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to delete the file generation. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Create_FG_FLT_Link(
  	p_fgid    in      R_FG_FLT.FGID%TYPE,
	p_suid	  in 	  R_FG_FLT.SUID%TYPE,
	p_fid	  in	  R_FG_FLT.FID%TYPE,
	p_gsid	  in	  R_FG_FLT.GSID%TYPE,
	p_message in out  varchar2) IS

	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
	CHECK_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
	INTEG_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

	begin
	  p_message := '';
	-- Create the file generation row
	  begin
	    if l_ok then
		  insert into R_FG_FLT Values(
		    p_fgid, p_suid, p_fid, p_gsid);
		end if;
	  exception
	    when others then
		  p_message := 'Failed to create the filter link for this file ' ||
		  			   'generation and sampling unit. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
  procedure Create_Data_File(
    p_dfid    in out    DATA_FILES.DFID%TYPE,
	p_fgid	  in 		DATA_FILES.FGID%TYPE,
	p_name	  in		DATA_FILES.NAME%TYPE,
	p_status  in		DATA_FILES.STATUS%TYPE,
	p_comm	  in		DATA_FILES.COMM%TYPE,
	p_id	  in		DATA_FILES.ID%TYPE,
	p_message in out	varchar2) is

	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
	CHECK_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
	INTEG_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

	begin
	  p_message := '';
	  p_dfid := 0;
	  -- Check parameters
	  begin
	    if l_ok then
		  if length(p_name) > 20 then
		    p_message := 'Name exceeds 20 characters';
			l_ok := false;
		  elsif length(p_status) > 8 then
		    p_message := 'Status exceeds 20 charcters';
			l_ok := false;
		  elsif length(p_comm) > 256 then
		    p_message := 'Comment exceeds 256 charcters';
			l_ok := false;
		  end if;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to check the parameters ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end ;
	-- Find DFID
	  begin
	    if l_ok then
		  select Data_Files_Seq.nextval into p_dfid from dual;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to increment file id. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	-- Create the data file row
	  begin
	    if l_ok then
		  insert into Data_Files Values(
		    p_dfid, p_fgid, p_name, p_status,
			p_comm, p_id, sysdate);
		end if;
	  exception
	    when others then
		  p_message := 'Failed to create the data file row. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;
----------------------------------------
  procedure Set_Data_File_Status(
    p_dfid    in        DATA_FILES.DFID%TYPE,
	p_status  in		DATA_FILES.STATUS%TYPE,
	p_message in out	varchar2) is

	NULL_NOT_ALLOWED EXCEPTION;
	PRAGMA EXCEPTION_INIT(NULL_NOT_ALLOWED, -1400);
	CHECK_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(CHECK_VIOLATED, -2290);
	INTEG_VIOLATED EXCEPTION;
	PRAGMA EXCEPTION_INIT(INTEG_VIOLATED, -2291);

	l_ok BOOLEAN := true;

	begin
	  p_message := '';
	  -- Check parameters
	  begin
	    if l_ok then
		  if length(p_status) > 8 then
		    p_message := 'Status exceeds 8 charcters';
			l_ok := false;
		  end if;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to check the parameters ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end ;
	-- Update the data file row
	  begin
	    if l_ok then
		  update Data_Files set
		    status = p_status, ts = sysdate
		    where dfid = p_dfid;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to update the data file row. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	exception
     when NO_DATA_FOUND
        then p_message := NO_DATA_FOUND_MESS ||
		  	   			  ' ORACLE error: ' || SQLERRM ;
      when TOO_MANY_ROWS
        then p_message := TOO_MANY_ROWS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when DUP_VAL_ON_INDEX
        then p_message := DUP_VAL_ON_INDEX_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when NULL_NOT_ALLOWED
        then p_message := NULL_NOT_ALLOWED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when CHECK_VIOLATED
        then p_message := CHECK_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when INTEG_VIOLATED
        then p_message := INTEG_VIOLATED_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
      when others
        then p_message := OTHERS_MESS ||
			 		   	  ' ORACLE error: ' || SQLERRM;
  end;

----------------------------------------
end GDBP;
/

