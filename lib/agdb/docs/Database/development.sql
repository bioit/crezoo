---------------------------------------------------------------
-- Development of aGDB 1.7 Beta
---------------------------------------------------------------
--
-- 
--
--


drop table import_file_log;
drop table Import_File;
drop table Import_Session;
drop table import_session_log;

grant all on gdbadm.results to gdbadm;
grant all on gdbadm.r_fg_ind to gdbadm;
grant all on gdbadm.rtype to gdbadm;
grant all on gdbadm.category to gdbadm;
grant all on gdbadm.category_seq to gdbadm;
grant all on gdbadm.rtype_seq to gdbadm;





----------------------------------------------------------------
-- Import_Set ----------------------------------------------
----------------------------------------------------------------

--create sequence Import_Session_Seq
--  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE IMPORT_SET
(
  ISID   		INTEGER       	NOT NULL,    	-- ID of the import set --NUMBER (38)
  NAME   		VARCHAR2 (20)  	NOT NULL,   	-- The name of the import set
  STATUS 		VARCHAR2 (10),  		-- Status of the import
  COMM   		VARCHAR2 (256), 		-- Comment
  PID    		NUMBER (38)   	NOT NULL,    	-- Project ID
  ID     		NUMBER (38)   	NOT NULL,    	-- USER ID
  TS     		DATE          	NOT NULL,    	-- Date and time updated.
  C_TS		DATE			NULL,		-- Create timestamp
  CHK_SUID	INTEGER			NULL,
  CHK_SPECIES   INTEGER                 NULL,
  CHK_LEVEL	INTEGER			NULL,
  CHK_MODE	VARCHAR2(2)		NULL,
  CHK_TS		DATE		NULL,
  UNIQUE (PID, NAME),
  PRIMARY KEY ( ISID ),
  FOREIGN KEY ( PID ) REFERENCES PROJECTS (PID),
  CHECK (CHK_MODE IN ('C','U','CU'))
);
-- alter table import_set add (chk_species integer);

CREATE TABLE IMPORT_SET_MSG
(
  MSGID	    INTEGER         NOT NULL,
  MSG       VARCHAR(500)    NOT NULL,
  ISID      INTEGER         NOT NULL,
  PRIMARY KEY (MSGID),
  FOREIGN KEY (ISID) REFERENCES IMPORT_SET(ISID)
);

--CREATE TABLE IMPORT_SET_LOG
--(
--  ISID   INTEGER   NOT NULL,
--  NAME   VARCHAR2 (20)  NOT NULL,
--  STATUS VARCHAR2 (8),             -- Status of the import
--  COMM   VARCHAR2 (256),
--  PID    NUMBER (38)   NOT NULL,
--  ID     NUMBER (38)   NOT NULL,
--  TS     DATE          NOT NULL
--);

----------------------------------------------------------------
-- Import_File ----------------------------------------------
----------------------------------------------------------------

--create sequence Import_File_Seq
--  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE IMPORT_FILE
(
  IFID          INTEGER   NOT NULL,                   -- ImportFileID NUMBER (38)
  ISID          NUMBER (38)   NOT NULL,               -- ImportSetID
  NAME          VARCHAR2 (100)  NOT NULL,             -- Name of file
  IMPORT_FILE   BLOB          null,                   -- The blob-file
  IMPORT_TYPE   VARCHAR2 (50),                        -- The mime type
  CHECKED_FILE  BLOB          NULL,                   -- A checked blob-file
  STATUS        VARCHAR2 (10),                         -- Status of the import
  COMM          VARCHAR2 (256),                       -- Comment
  LEN           NUMBER,                               -- the length of the blob  
  OBJECT_NAME   VARCHAR2 (38),                        -- the name of object, INDIVIDUAL, VARIABLE etc.
  CHK_SET_NAME  VARCHAR2 (38),                        -- the set name
  ID            NUMBER (38)   NOT NULL,               -- USER ID
  TS            DATE          NOT NULL,               -- Timestamp
  UNIQUE (NAME, ISID),
  PRIMARY KEY ( IFID ),
  FOREIGN KEY ( ISID ) REFERENCES IMPORT_SET (ISID)
);
--ERRMSG      VARCHAR2 (1000),                      -- An error message for the file

-- alter table import_file add (import_file blob);
-- alter table import_file add (checked_file blob);
-- alter table import_file add (import_type varchar2 (50));
-- alter table import_file add (len number);
-- alter table import_file modify (len integer);
-- alter table import_file add(object_name varchar2 (38));
-- alter table import_file add(chk_set_name varchar2 (38));

CREATE TABLE IMPORT_FILE_MSG
(
  MSGID	INTEGER       	NOT NULL,
  MSG     	VARCHAR(500) 	NOT NULL,
  IFID      	INTEGER          	NOT NULL,
  TS		DATE		NULL,
  PRIMARY KEY (MSGID),
  FOREIGN KEY (IFID) REFERENCES IMPORT_FILE(IFID)
);

CREATE TABLE IMPORT_FILE_LOG
(
  IFID    INTEGER           NOT NULL,
  NAME    VARCHAR2 (100)    NOT NULL,
  STATUS  VARCHAR2 (8),
  ERRMSG  VARCHAR2 (1000),
  COMM    VARCHAR2 (256),
  ID      NUMBER (38)       NOT NULL,
  TS      DATE              NOT NULL,
  FOREIGN KEY (IFID) REFERENCES IMPORT_FILE (IFID) ON DELETE CASCADE
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

create sequence Results_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE RESULTS ( 
  RESID       NUMBER (38)   NOT NULL, 
  FGID        NUMBER (38),
  R_NAME      VARCHAR2 (80)  NOT NULL, 
  R_FILE      BLOB           NOT NULL,
  R_TYPE      NUMBER (38)  NOT NULL,
  B_NAME      VARCHAR2 (80), 
  B_FILE      CLOB,
  CTG         NUMBER (38)   NOT NULL,
  COMM        VARCHAR2 (2000), 
  C_TS        DATE         NOT NULL,   
  ID          NUMBER (38)   NOT NULL, 
  TS          DATE          NOT NULL, 
  PRIMARY KEY ( RESID )
);
--ALTER TABLE RESULTS
--ADD (PID     NUMBER (38)   NULL); 
-- OBS!!! Change PID to NOT NULL when table is dropped!

CREATE TABLE RESULTS_LOG ( 
  RESID       NUMBER (38)   NOT NULL, 
  FGID        NUMBER (38),  
  R_NAME      VARCHAR2 (80)  NOT NULL, 
  R_FILE      BLOB,           
  R_TYPE      NUMBER (38)  NOT NULL,   
  B_NAME      VARCHAR2 (80), 
  B_FILE      CLOB,
  CTG         NUMBER (38)   NOT NULL,
  COMM        VARCHAR2 (2000), 
  C_TS        DATE, 
  ID          NUMBER (38)   NOT NULL, 
  TS          DATE          NOT NULL
 );

--ALTER TABLE RESULTS_LOG
--ADD (PID     NUMBER (38)   NULL); 
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

CREATE TABLE R_FG_IND (
  FGID   NUMBER (38)   NOT NULL, 
  IID  NUMBER (38)   NOT NULL, 
  SUID NUMBER (38)   NOT NULL,
  PRIMARY KEY ( FGID, IID, SUID ) 
);

----------------------------------------------------------------
-- Parser ------------------------------------------------------
----------------------------------------------------------------
create sequence Parser_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE Parser ( 
  PAID   NUMBER (38)   NOT NULL, 
  NAME  NUMBER (38)   NOT NULL, 
  COMM        VARCHAR2 (2000), 
  PRIMARY KEY ( PAID) 
);


----------------------------------------------------------------
-- R_Res_Pars ------------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_Res_Pars ( 
  RID   NUMBER (38)   NOT NULL, 
  PAID  NUMBER (38)   NOT NULL, 
  PRIMARY KEY ( RID, PAID ) 
);


----------------------------------------------------------------
-- Category ----------------------------------------------------
----------------------------------------------------------------
create sequence Category_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE CATEGORY 
( 
  CTGID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (38)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  ID          NUMBER (38)   NOT NULL, 
  TS          DATE          NOT NULL,
  PRIMARY KEY ( CTGID ) 
);

----------------------------------------------------------------
-- ResultType --------------------------------------------------
----------------------------------------------------------------
create sequence RType_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE RTYPE
(
  RTID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (38)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  ID          NUMBER (38)   NOT NULL, 
  TS          DATE          NOT NULL,
  PRIMARY KEY ( RTID ) 
);


----------------------------------------------------------------
-- IMPORT SETS --------------------------------------------
----------------------------------------------------------------

create or replace view V_IMPORT_SET_1 (
	   PID, ISID, NAME, STATUS, COMM, ID, TS) as
  select a.pid, a.isid, a.name, a.status, a.comm, a.id, a.ts
  from import_set a;

create or replace view V_IMPORT_SET_2 (
	   PID, ISID, NAME, STATUS, COMM, USR, TS,C_TS,CHK_MODE,CHK_LEVEL,CHK_SUID,CHK_TS) as
  select a.pid, a.isid, a.name, a.status, a.comm, u.usr, a.ts, a.c_ts, a.chk_mode, chk_level, chk_suid, chk_ts
  from import_set a, users u
  where
  	   u.id = a.id ;

--outer join to get columns even if chk_suid does not exist in import_set
create or replace view V_IMPORT_SET_3 (
	   PID, ISID, NAME, STATUS, COMM, USR, TS,C_TS,CHK_MODE,CHK_LEVEL,CHK_SUID, SP_NAME,CHK_TS,SU_NAME) as
  select a.pid, a.isid, a.name, a.status, a.comm, u.usr, a.ts, a.c_ts, a.chk_mode, chk_level, 
         chk_suid, sp.name,chk_ts, su.name 
  from sampling_units su, import_set a, users u, species sp
  where
           su.suid(+)=a.chk_suid and 
	   sp.sid(+)=a.chk_species and	
  	   u.id = a.id;
----------------------------------------------------------------
-- IMPORT FILES ------------------------------------------------
----------------------------------------------------------------

create or replace view V_IMPORT_FILES_1 (
	   ISID, IFID, NAME, STATUS, COMM, ID, TS) as
  select df.isid, df.ifid, df.name, df.status, df.comm, df.id, df.ts
  from import_file df;

create or replace view V_IMPORT_FILES_2 (
	   ISID, IFID, NAME, STATUS, COMM, LEN, USR, TS) as
  select df.isid, df.ifid, df.name, df.status, df.comm, df.len, u.USR, df.ts
  from import_file df, users u
  where
  	   u.id = df.id;

create or replace view V_IMPORT_FILES_3 (
	   ISID, ANNAME, DFID, NAME, STATUS, COMM, USR, TS) as
  select df.isid, a.name, df.ifid, df.name, df.status, df.comm, u.USR, df.ts
  from import_file df, users u, import_set a
  where
  	   u.id = df.id and
	   a.isid = df.isid;

--------------------------------------------------------------------------
------RESULTS, view suid with proper pid----------------------------------
--------------------------------------------------------------------------

create or replace view V_Enabled_Sampling_Units_3 (
	   PID, SID, SUID, NAME) as
 select r.pid, s.SID, s.SUID, s.NAME
 from Sampling_Units s, r_prj_su r, r_fg_ind rfi
 where
 	  s.status = 'E' and
	  r.suid = s.suid and
          rfi.suid = s.suid ;
