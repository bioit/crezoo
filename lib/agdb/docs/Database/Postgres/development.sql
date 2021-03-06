---------------------------------------------------------------
-- Development of aGDB 1.7 Beta
---------------------------------------------------------------
--
-- 
--
--


--drop table import_file_log;
--drop table Import_File;
--drop table Import_Session;
--drop table import_session_log;





grant all on gdbadm.results to gdbadm;
grant all on gdbadm.results_log to gdbadm;
grant all on gdbadm.r_fg_ind to gdbadm;
grant all on gdbadm.rtype to gdbadm;
grant all on gdbadm.category to gdbadm;
grant all on gdbadm.category_seq to gdbadm;
grant all on gdbadm.rtype_seq to gdbadm;
grant all on gdbadm.results_seq to gdbadm; 


----------------------------------------------------------------
-- Import_Set ----------------------------------------------
----------------------------------------------------------------

--create sequence gdbadm.Import_Session_Seq
--  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE gdbadm.IMPORT_SET
(
  ISID   		INTEGER       	NOT NULL,    	-- ID of the import set --INTEGER
  NAME   		VARCHAR (20)  	NOT NULL,   	-- The name of the import set
  STATUS 		VARCHAR (10),  		-- Status of the import
  COMM   		VARCHAR (256), 		-- Comment
  PID    		INTEGER   	NOT NULL,    	-- Project ID
  ID     		INTEGER   	NOT NULL,    	-- USER ID
  TS     		DATE          	NOT NULL,    	-- Date and time updated.
  C_TS		DATE			NULL,		-- Create timestamp
  CHK_SUID	INTEGER			NULL,
  CHK_SPECIES   INTEGER                 NULL,
  CHK_LEVEL	INTEGER			NULL,
  CHK_MODE	VARCHAR(2)		NULL,
  CHK_TS		DATE		NULL,
  UNIQUE (PID, NAME),
  PRIMARY KEY ( ISID ),
  FOREIGN KEY ( PID ) REFERENCES gdbadm.PROJECTS (PID),
  CHECK (CHK_MODE IN ('C','U','CU'))
);
-- alter table import_set add (chk_species integer);

CREATE TABLE gdbadm.IMPORT_SET_MSG
(
  MSGID	    INTEGER         NOT NULL,
  MSG       VARCHAR(500)    NOT NULL,
  ISID      INTEGER         NOT NULL,
  PRIMARY KEY (MSGID),
  FOREIGN KEY (ISID) REFERENCES gdbadm.IMPORT_SET(ISID)
);

--CREATE TABLE gdbadm.IMPORT_SET_LOG
--(
--  ISID   INTEGER   NOT NULL,
--  NAME   VARCHAR (20)  NOT NULL,
--  STATUS VARCHAR (8),             -- Status of the import
--  COMM   VARCHAR (256),
--  PID    INTEGER   NOT NULL,
--  ID     INTEGER   NOT NULL,
--  TS     DATE          NOT NULL
--);

----------------------------------------------------------------
-- Import_File ----------------------------------------------
----------------------------------------------------------------

--create sequence gdbadm.Import_File_Seq
--  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE gdbadm.IMPORT_FILE
(
  IFID          INTEGER   NOT NULL,                   -- ImportFileID INTEGER
  ISID          INTEGER   NOT NULL,               -- ImportSetID
  NAME          VARCHAR (100)  NOT NULL,             -- Name of file
  IMPORT_FILE   BLOB          null,                   -- The blob-file
  IMPORT_TYPE   VARCHAR (50),                        -- The mime type
  CHECKED_FILE  BLOB          NULL,                   -- A checked blob-file
  STATUS        VARCHAR (10),                         -- Status of the import
  COMM          VARCHAR (256),                       -- Comment
  LEN           NUMBER,                               -- the length of the blob  
  OBJECT_NAME   VARCHAR (38),                        -- the name of object, INDIVIDUAL, VARIABLE etc.
  CHK_SET_NAME  VARCHAR (38),                        -- the set name
  ID            INTEGER   NOT NULL,               -- USER ID
  TS            DATE          NOT NULL,               -- Timestamp
  UNIQUE (NAME, ISID),
  PRIMARY KEY ( IFID ),
  FOREIGN KEY ( ISID ) REFERENCES gdbadm.IMPORT_SET (ISID)
);
--ERRMSG      VARCHAR (1000),                      -- An error message for the file

-- alter table import_file add (import_file blob);
-- alter table import_file add (checked_file blob);
-- alter table import_file add (import_type VARCHAR (50));
-- alter table import_file add (len number);
-- alter table import_file modify (len integer);
-- alter table import_file add(object_name VARCHAR (38));
-- alter table import_file add(chk_set_name VARCHAR (38));

CREATE TABLE gdbadm.IMPORT_FILE_MSG
(
  MSGID	INTEGER       	NOT NULL,
  MSG     	VARCHAR(500) 	NOT NULL,
  IFID      	INTEGER          	NOT NULL,
  TS		DATE		NULL,
  PRIMARY KEY (MSGID),
  FOREIGN KEY (IFID) REFERENCES gdbadm.IMPORT_FILE(IFID)
);

CREATE TABLE gdbadm.IMPORT_FILE_LOG
(
  IFID    INTEGER           NOT NULL,
  NAME    VARCHAR (100)    NOT NULL,
  STATUS  VARCHAR (8),
  ERRMSG  VARCHAR (1000),
  COMM    VARCHAR (256),
  ID      INTEGER       NOT NULL,
  TS      DATE              NOT NULL,
  FOREIGN KEY (IFID) REFERENCES gdbadm.IMPORT_FILE (IFID) ON DELETE CASCADE
);

----------------------------------------------------------------
-- Results -----------------------------------------------------
----------------------------------------------------------------
--CTG=category  
--FLT_EXP=the expression of the filter used in the analysis
--B=batchfile
--BLOB=Binary large object
--CLOB=Character large object
----------------------------------------------------------------

create sequence gdbadm.Results_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE gdbadm.RESULTS ( 
  RESID       INTEGER   NOT NULL, 
  FGID        INTEGER,
  R_NAME      VARCHAR (80)  NOT NULL, 
  R_FILE      BYTEA           NULL,
  R_TYPE      INTEGER  NOT NULL,
  B_NAME      VARCHAR (80), 
  B_FILE      BYTEA,
  CTG         INTEGER   NOT NULL,
  COMM        VARCHAR (2000), 
  PID         INTEGER       NOT NULL,
  C_TS        DATE         NOT NULL,   
  ID          INTEGER   NOT NULL, 
  TS          DATE          NOT NULL, 
  PRIMARY KEY ( RESID )
);
--ALTER TABLE RESULTS
--ADD (PID     INTEGER   NULL); 
-- OBS!!! Change PID to NOT NULL when table is dropped!

CREATE TABLE gdbadm.RESULTS_LOG ( 
  RESID       INTEGER   NOT NULL, 
  FGID        INTEGER,  
  R_NAME      VARCHAR (80)  NOT NULL, 
  R_FILE      BYTEA,           
  R_TYPE      INTEGER  NOT NULL,   
  B_NAME      VARCHAR (80), 
  B_FILE      BYTEA,
  CTG         INTEGER   NOT NULL,
  COMM        VARCHAR (2000), 
  PID         INTEGER       NOT NULL,
  C_TS        DATE, 
  ID          INTEGER   NOT NULL, 
  TS          DATE          NOT NULL
 );

--ALTER TABLE RESULTS_LOG
--ADD (PID     INTEGER   NULL); 
-- OBS!!! Change PID to NOT NULL when table is dropped!


--If the result i deleted, it should be found in the result log.
-- This statement has not been executed
--ALTER TABLE RESULTS_LOG ADD 
-- FOREIGN KEY (RID) 
--  REFERENCES RESULT (RID) 
-- ON DELETE CASCADE;


----------------------------------------------------------------
-- R_FG_Ind-----------------------------------------------------
----------------------------------------------------------------

CREATE TABLE gdbadm.R_FG_IND (
  FGID   INTEGER   NOT NULL, 
  IID  INTEGER   NOT NULL, 
  SUID INTEGER   NOT NULL,
  PRIMARY KEY ( FGID, IID, SUID ) 
);

----------------------------------------------------------------
-- Parser ------------------------------------------------------
----------------------------------------------------------------
create sequence gdbadm.Parser_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE gdbadm.Parser ( 
  PAID   INTEGER   NOT NULL, 
  NAME  INTEGER   NOT NULL, 
  COMM        VARCHAR (2000), 
  PRIMARY KEY ( PAID) 
);


----------------------------------------------------------------
-- R_Res_Pars ------------------------------------------------------
----------------------------------------------------------------

CREATE TABLE gdbadm.R_Res_Pars ( 
  RID   INTEGER   NOT NULL, 
  PAID  INTEGER   NOT NULL, 
  PRIMARY KEY ( RID, PAID ) 
);


----------------------------------------------------------------
-- Category ----------------------------------------------------
----------------------------------------------------------------
create sequence gdbadm.Category_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE gdbadm.CATEGORY 
( 
  CTGID  INTEGER   NOT NULL, 
  NAME  VARCHAR (38)  NOT NULL, 
  COMM  VARCHAR (256), 
  ID          INTEGER   NOT NULL, 
  TS          DATE          NOT NULL,
  PRIMARY KEY ( CTGID ) 
);

----------------------------------------------------------------
-- ResultType --------------------------------------------------
----------------------------------------------------------------
create sequence gdbadm.RType_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE gdbadm.RTYPE
(
  RTID   INTEGER   NOT NULL, 
  NAME  VARCHAR (38)  NOT NULL, 
  COMM  VARCHAR (256), 
  ID          INTEGER   NOT NULL, 
  TS          DATE          NOT NULL,
  PRIMARY KEY ( RTID ) 
);


----------------------------------------------------------------
-- IMPORT SETS --------------------------------------------
----------------------------------------------------------------

create or replace view gdbadm.V_IMPORT_SET_1 (
	   PID, ISID, NAME, STATUS, COMM, ID, TS) as
  select a.pid, a.isid, a.name, a.status, a.comm, a.id, a.ts
  from import_set a;

create or replace view gdbadm. V_IMPORT_SET_2 (
	   PID, ISID, NAME, STATUS, COMM, USR, TS,C_TS,CHK_MODE,CHK_LEVEL,CHK_SUID,CHK_TS) as
  select a.pid, a.isid, a.name, a.status, a.comm, u.usr, a.ts, a.c_ts, a.chk_mode, chk_level, chk_suid, chk_ts
  from import_set a, users u
  where
  	   u.id = a.id ;

--outer join to get columns even if chk_suid does not exist in import_set
--create or replace view gdbadm.V_IMPORT_SET_3 (
--	   PID, ISID, NAME, STATUS, COMM, USR, TS,C_TS,CHK_MODE,CHK_LEVEL,CHK_SUID, SP_NAME,CHK_TS,SU_NAME) as
--  select a.pid, a.isid, a.name, a.status, a.comm, u.usr, a.ts, a.c_ts, a.chk_mode, chk_level, 
--         chk_suid, sp.name,chk_ts, su.name 
--  from sampling_units su, import_set a, users u, species sp
--  where
--           su.suid(+)=a.chk_suid and 
--	   sp.sid(+)=a.chk_species and	
--  	   u.id = a.id;


create or replace view gdbadm.V_IMPORT_SET_3 (
	   PID, ISID, NAME, STATUS, COMM, USR, TS,C_TS,CHK_MODE,CHK_LEVEL,CHK_SUID, SP_NAME,CHK_TS,SU_NAME) as
  select a.pid, a.isid, a.name, a.status, a.comm, u.usr, a.ts, a.c_ts, a.chk_mode, chk_level, 
         chk_suid, sp.name,chk_ts, su.name 
  from  
        import_set a 
            left join sampling_units su on su.suid=a.chk_suid
            left join species sp on sp.sid=a.chk_species,
        users u
  where
       u.id = a.id;


----------------------------------------------------------------
-- IMPORT FILES ------------------------------------------------
----------------------------------------------------------------

create or replace view gdbadm.V_IMPORT_FILES_1 (
	   ISID, IFID, NAME, STATUS, COMM, ID, TS) as
  select df.isid, df.ifid, df.name, df.status, df.comm, df.id, df.ts
  from gdbadm.import_file df;

create or replace view gdbadm.V_IMPORT_FILES_2 (
	   ISID, IFID, NAME, STATUS, COMM, LEN, USR, TS) as
  select df.isid, df.ifid, df.name, df.status, df.comm, df.len, u.USR, df.ts
  from gdbadm.import_file df, gdbadm.users u
  where
  	   u.id = df.id;

create or replace view gdbadm.V_IMPORT_FILES_3 (
	   ISID, ANNAME, DFID, NAME, STATUS, COMM, USR, TS) as
  select df.isid, a.name, df.ifid, df.name, df.status, df.comm, u.USR, df.ts
  from gdbadm.import_file df, gdbadm.users u, gdbadm.import_set a
  where
  	   u.id = df.id and
	   a.isid = df.isid;

--------------------------------------------------------------------------
------RESULTS, view suid with proper pid----------------------------------
--------------------------------------------------------------------------

create or replace view gdbadm.V_Enabled_Sampling_Units_3 (
	   PID, SID, SUID, NAME) as
 select r.pid, s.SID, s.SUID, s.NAME
 from gdbadm.Sampling_Units s, gdbadm.r_prj_su r, gdbadm.r_fg_ind rfi
 where
 	  s.status = 'E' and
	  r.suid = s.suid and
          rfi.suid = s.suid ;
