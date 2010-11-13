----------------------------------------------------------------
--
-- This script creates all DB objects
--
-- This script must be run as SysAdm
--
--  2000-03-16	ANNY	First version
--  2000-03-23	ANNY	Added table Users
--  2000-03-23	ANNY	Added Log tables
--  2000-04-11	ANNY	Modified individuals
--  2000-05-03	ANNY	Added Individuals_Comm table
--                      Added Projects and Access tables
--  2000-05-08	ANNY	Added Variables and Phenotypes
--  2000-05-16	ANNY	Added Variable and Marker Sets
--  2000-05-19	ANNY	Added Filters
--  2000-09-21  TOBJ 	Modified genotypes and genotypes_log
--                    	to allow null in aid1 and aid2
--  2000-09-27	TOBJ	Modified users to accept 8 chars
--			in usr and pwd. Modified ORDER_NO in
--			R_MRK_SET to NUMBER instead of int.
--  2000-09-29  ROCA    Changed R_Mrk_Set to Position
--			Changed Position/order_no => position
--  			Added (SUID) to Phenotypes & Genotypes
--			Added level_ to Genotypes
--  2000-10-02  ROCA	Removed all references to foreign key's.
--  2000-10-05	ROCA	Removed Genes, Genes_log, Samples, samples_log
--			Added U_position, R_Uaid_aid, R_Umin_mid,R_Uvid_vid
--			Added U_Alleles, U_Markers, U_Marker_set, U_Variables	
--  2000-10-06  ROCA	Removed ID from Species
--	2000-10-09	TOBJ	Added sid to U_Marker_Sets and 
--						U_Variable_sets. Also changed sid to suid
--						in Marker_Sets and Variable_Sets
--  2000-10-12  TOBJ	Added the table U_R_VAR_SET
--  2000-10-13	TOBJ	Modified th table Users. Added the column
--						status.
--  2000-11-13	TOBJ	Merged all tables with comments to theire
--						correspong data table. Modefied the following
--						sequences to cache values according to 
--						the table belove. 
--						
--						sequence       old value    new value
--						------------------------------------
--						Individuals    20			 2000 
--						Genotypes	   20			 2000 
--						Groupings	   20			 100 
--						Groups		   20			 100 
--						Markers		   20			 500 
--						Phenotypes	   20			 2000 
--						Alleles 	   20			 1000	
--						Variables	   20			 100 
--						U_Alleles	   20			 2000 
--						U_Markers	   20			 500 
--						U_Variables	   20			 100 
-- 
-- 						Also changed the check constraint for 
--						project to only accept status {'E', 'D'}
--						instead of {'O', 'C'}. Added the 
--						object Role and all the necessary objects
--						to implement the meaning of roles (i.e.
--						relational tables). Added L-Markers
--						and L-Alleles.
--	2000-11-14	TOBJ	Removed ts from species.
--						Added "on delete cascade" and 
--						referencing on relational tables.
--						Removed ts and id from chromosomes.
--						Added the table Priv_Map for mapping
--						privileges to integer which the servlets
--						know about.
--						Added the objects Analyses and Data_File 
--  2000-11-20	TOBJ	Added storage information for all tables.
--						Added the state 'U' for the sex of 
--						individuals. Added the fields p1, p2 and
--						position to the tables L_Markers, 
--						U_Marker and Markers.
--						Increased the initial and next storage
--						values for individuals and genotypes.
--  2000-12-08			Added the constraint unique(pid, name)
--						on the table Roles_
--		
----------------------------------------------------------------

----------------------------------------------------------------
-- Start from scratch... ---------------------------------------
----------------------------------------------------------------

drop table Priv_Map;
drop table Positions;
drop table U_Positions;
drop table R_U_Var_Set;
drop table R_Uaid_aid;
drop table R_Umid_mid;
drop table R_Uvid_vid;

drop table R_Ana_Filt;

drop table R_Var_Set;
drop table R_Ind_Grp;
drop table R_Prj_SU;
drop table R_Prj_Spc;

drop table R_Prj_Rol;
drop table R_Rol_Pri;

drop sequence Filters_Seq;
drop table Filters_Log;
drop table Filters;

drop sequence Analyses_Seq;
drop table Analyses;
drop table Analyses_Log;

drop sequence Data_Files_Seq;
drop table Data_Files;
drop table Data_Files_Log;

drop sequence Genotypes_Seq;
drop table Genotypes_Log;

drop table Genotypes;

drop sequence Alleles_Seq;
drop table Alleles_Log;
drop table Alleles;

drop sequence Marker_Sets_Seq;
drop table Marker_Sets_Log;
drop table Marker_Sets;

drop sequence Markers_Seq;
drop table Markers_Log;
drop table Markers;

drop sequence U_Alleles_Seq;
drop table U_Alleles_Log;
drop table U_Alleles;

drop sequence U_Marker_Sets_Seq;
drop table U_Marker_Sets_Log;
drop table U_Marker_Sets;

drop sequence U_Markers_Seq;
drop table U_Markers_Log;
drop table U_Markers;

drop sequence Chromosomes_Seq;

drop table Chromosomes;

drop sequence L_Markers_Seq;
drop table L_Markers;

drop sequence L_Alleles_Seq;
drop table L_Alleles;

drop sequence Phenotypes_Seq;
drop table Phenotypes_Log;

drop table Phenotypes;

drop sequence Variable_Sets_Seq;
drop table Variable_Sets_Log;
drop table Variable_Sets;

drop sequence Variables_Seq;
drop table Variables_Log;
drop table Variables;


drop sequence U_Variable_Sets_Seq;
drop table U_Variable_Sets_Log;
drop table U_Variable_Sets;

drop sequence U_Variables_Seq;
drop table U_Variables_Log;
drop table U_Variables;

drop sequence Groups_Seq;
drop table Groups_Log;
drop table Groups;

drop sequence Groupings_Seq;
drop table Groupings_Log;
drop table Groupings;

drop sequence Samples_Seq;
drop table samples;

drop sequence Individuals_Seq;
drop table Individuals_Log;

drop table Individuals;

drop sequence Sampling_Units_Seq;
drop table Sampling_Units_Log;
drop table Sampling_Units;

drop sequence Species_Seq;
drop table Species;

drop sequence Roles_Seq;
drop table Roles_;

--drop sequence Privileges_Seq;
drop table Privileges_;

drop sequence Users_Seq;
drop table Users;

drop sequence Projects_Seq;
drop table Projects;

----------------------------------------------------------------
-- Projects ----------------------------------------------------
----------------------------------------------------------------

create sequence Projects_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE PROJECTS ( 
  PID     NUMBER (38)   NOT NULL, 
  NAME    VARCHAR2 (20)  NOT NULL, 
  COMM    VARCHAR2 (256), 
  STATUS  CHAR (1)      NOT NULL, 
   CHECK (status in ('E', 'D')) , 
  UNIQUE (NAME)    USING INDEX  
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( PID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Users -------------------------------------------------------
----------------------------------------------------------------

create sequence Users_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE USERS ( 
  ID      NUMBER (38)   NOT NULL, 
  USR     VARCHAR2 (10)  NOT NULL, 
  PWD     VARCHAR2 (10)  NOT NULL, 
  NAME    VARCHAR2 (32), 
  STATUS  CHAR (1)      NOT NULL, 
   CHECK (status in( 'E', 'D')) , 
  UNIQUE (USR)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( ID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Privileges --------------------------------------------------
----------------------------------------------------------------
--create sequence Privileges_Seq
--  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE PRIVILEGES_ ( 
  PRID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (12)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  UNIQUE ( NAME ), 
  PRIMARY KEY ( PRID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 
----------------------------------------------------------------
-- Roles -------------------------------------------------------
----------------------------------------------------------------
create sequence Roles_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;  

CREATE TABLE ROLES_ ( 
  RID   NUMBER (38)   NOT NULL, 
  PID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  UNIQUE (PID, NAME)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( RID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Species -----------------------------------------------------
----------------------------------------------------------------

create sequence Species_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE SPECIES ( 
  SID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  UNIQUE (NAME)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( SID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Sampling_Units ----------------------------------------------
----------------------------------------------------------------

create sequence Sampling_Units_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE SAMPLING_UNITS ( 
  SUID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  STATUS VARCHAR2(1)	NOT NULL,
  SID   NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL,
    CHECK (status in ('E', 'D') ), 
  UNIQUE (NAME)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( SUID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 


CREATE TABLE SAMPLING_UNITS_LOG ( 
  SUID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  STATUS VARCHAR2 (1)	NOT NULL,
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Individuals --------------------------------------------------
----------------------------------------------------------------

create sequence Individuals_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000 order ;

CREATE TABLE INDIVIDUALS ( 
  IID         NUMBER (38)   NOT NULL, 
  IDENTITY    VARCHAR2 (11)  NOT NULL, 
  ALIAS       VARCHAR2 (11), 
  FATHER      NUMBER (38), 
  MOTHER      NUMBER (38), 
  SEX         CHAR (1)      NOT NULL, 
  BIRTH_DATE  DATE, 
  STATUS	  VARCHAR2(1)	NOT NULL,
  SUID        NUMBER (38)   NOT NULL, 
  ID          NUMBER (38)   NOT NULL, 
  TS          DATE          NOT NULL, 
  COMM        VARCHAR2 (256), 
   CHECK (sex in ('M', 'F', 'U')) , 
   CHECK (status in ('E', 'D') ),
  UNIQUE ( SUID, IDENTITY)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 100K NEXT 100K PCTINCREASE 50 ), 
  PRIMARY KEY ( IID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 100K NEXT 100K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 1048576
   NEXT 1048576
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE INDIVIDUALS_LOG ( 
  IID         NUMBER (38)   NOT NULL, 
  IDENTITY    VARCHAR2 (11)  NOT NULL, 
  ALIAS       VARCHAR2 (11), 
  FATHER      NUMBER (38), 
  MOTHER      NUMBER (38), 
  SEX         CHAR (1)      NOT NULL, 
  BIRTH_DATE  DATE, 
  STATUS	  VARCHAR2(1)	NOT NULL,
  COMM        VARCHAR2 (256), 
  ID          NUMBER (38)   NOT NULL, 
  TS          DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 


----------------------------------------------------------------
-- Samples -----------------------------------------------------
----------------------------------------------------------------
create sequence Samples_Seq 
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000 order ;
  
CREATE TABLE SAMPLES ( 
  SAID          NUMBER (38)   NOT NULL, 
  NAME          VARCHAR2 (20)  NOT NULL, 
  TISSUE_TYPE   VARCHAR2 (20)  NOT NULL, 
  EXPERIMENTER  VARCHAR2 (32), 
  DATE_         DATE, 
  TREATMENT     VARCHAR2 (20), 
  STORAGE       VARCHAR2 (20), 
  COMM          VARCHAR2 (256), 
  IID           NUMBER (38)   NOT NULL, 
  ID            NUMBER (38)   NOT NULL, 
  TS            DATE          NOT NULL, 
  UNIQUE (NAME, IID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( SAID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE SAMPLES ADD CONSTRAINT CT_SAMPLES_IID
 FOREIGN KEY (IID) 
  REFERENCES INDIVIDUALS (IID) 
 ON DELETE CASCADE;

ALTER TABLE SAMPLES ADD CONSTRAINT CT_SAMPLES_ID
 FOREIGN KEY (ID) 
  REFERENCES USERS (ID) 
 ON DELETE CASCADE;



----------------------------------------------------------------
-- Groupings ---------------------------------------------------
----------------------------------------------------------------

create sequence Groupings_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 100 order ;

CREATE TABLE GROUPINGS ( 
  GSID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  SUID  NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, SUID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( GSID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 


CREATE TABLE GROUPINGS_LOG ( 
  GSID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Groups ------------------------------------------------------
----------------------------------------------------------------

create sequence Groups_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 100 order ;

CREATE TABLE GROUPS ( 
  GID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  GSID  NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, GSID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( GID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE GROUPS_LOG ( 
  GID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- U_Variables ---------------------------------------------------
----------------------------------------------------------------

create sequence U_Variables_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 100 order ;

CREATE TABLE U_VARIABLES ( 
  UVID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  TYPE  VARCHAR2 (1)  NOT NULL, 
  UNIT  VARCHAR2 (10), 
  COMM  VARCHAR2 (256), 
  PID   NUMBER (38)   NOT NULL, 
  SID   NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
   CHECK (type in ('E', 'N')) , 
  UNIQUE (NAME, PID, SID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( UVID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE U_VARIABLES_LOG ( 
  UVID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  TYPE  VARCHAR2 (1)  NOT NULL, 
  UNIT  VARCHAR2 (10), 
  COMM  VARCHAR2 (256), 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- U_Variable Sets -----------------------------------------------
----------------------------------------------------------------

create sequence U_Variable_Sets_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE U_VARIABLE_SETS ( 
  UVSID  NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  COMM   VARCHAR2 (256), 
  PID    NUMBER (38)   NOT NULL, 
  SID    NUMBER (38)   NOT NULL, 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL, 
  UNIQUE (NAME, PID, SID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( UVSID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE U_VARIABLE_SETS_LOG ( 
  UVSID  NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  COMM   VARCHAR2 (256), 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Variables ---------------------------------------------------
----------------------------------------------------------------

create sequence Variables_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 100 order ;

CREATE TABLE VARIABLES ( 
  VID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  TYPE  VARCHAR2 (1)  NOT NULL, 
  UNIT  VARCHAR2 (10), 
  COMM  VARCHAR2 (256), 
  SUID  NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
   CHECK (type in ('E', 'N')) , 
  UNIQUE (NAME, SUID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( VID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE VARIABLES_LOG ( 
  VID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  TYPE  VARCHAR2 (1)  NOT NULL, 
  UNIT  VARCHAR2 (10), 
  COMM  VARCHAR2 (256), 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Variable Sets -----------------------------------------------
----------------------------------------------------------------

create sequence Variable_Sets_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE VARIABLE_SETS ( 
  VSID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  SUID  NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, SUID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( VSID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE VARIABLE_SETS_LOG ( 
  VSID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Phenotypes --------------------------------------------------
----------------------------------------------------------------

create sequence Phenotypes_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000 order ;

CREATE TABLE PHENOTYPES ( 
  VID        NUMBER (38)   NOT NULL, 
  IID        NUMBER (38)   NOT NULL, 
  SUID       NUMBER (38)   NOT NULL, 
  VALUE      VARCHAR2 (20)  NOT NULL, 
  DATE_      DATE, 
  REFERENCE  VARCHAR2 (32), 
  ID         NUMBER (38)   NOT NULL, 
  TS         DATE          NOT NULL, 
  COMM       VARCHAR2 (256), 
  PRIMARY KEY ( VID, IID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE PHENOTYPES_LOG ( 
  VID        NUMBER (38)   NOT NULL, 
  IID        NUMBER (38)   NOT NULL, 
  VALUE      VARCHAR2 (20)  NOT NULL, 
  DATE_      DATE, 
  REFERENCE  VARCHAR2 (32), 
  COMM       VARCHAR2 (256), 
  ID         NUMBER (38)   NOT NULL, 
  TS         DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Chromosomes -------------------------------------------------
----------------------------------------------------------------

create sequence Chromosomes_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE CHROMOSOMES ( 
  CID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (2)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  SID   NUMBER (38)   NOT NULL, 
  UNIQUE (NAME, SID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( CID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 


----------------------------------------------------------------
-- U_Markers -----------------------------------------------------
----------------------------------------------------------------

create sequence U_Markers_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 500 order ;

CREATE TABLE U_MARKERS ( 
  UMID   NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  ALIAS  VARCHAR2 (20), 
  COMM   VARCHAR2 (256), 
  POSITION  NUMBER, 
  PID	 NUMBER (38)   NOT NULL,
  SID	 NUMBER (38)   NOT NULL,
  CID    NUMBER (38)   NOT NULL, 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL, 
  UNIQUE (NAME, PID, SID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( UMID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE U_MARKERS_LOG ( 
  UMID   NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  ALIAS  VARCHAR2 (20), 
  COMM   VARCHAR2 (256), 
  POSITION  NUMBER, 
  CID		NUMBER (38)	   NOT NULL,
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 


----------------------------------------------------------------
-- U_Marker Sets -------------------------------------------------
----------------------------------------------------------------

create sequence U_Marker_Sets_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE U_MARKER_SETS ( 
  UMSID  NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  COMM   VARCHAR2 (256), 
  PID    NUMBER (38)   NOT NULL, 
  SID    NUMBER (38)   NOT NULL, 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL, 
  UNIQUE (NAME, PID, SID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( UMSID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE U_MARKER_SETS_LOG ( 
  UMSID  NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  COMM   VARCHAR2 (256), 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- U_Alleles -----------------------------------------------------
----------------------------------------------------------------

create sequence U_Alleles_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000 order ;

CREATE TABLE U_ALLELES ( 
  UAID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  UMID  NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, UMID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( UAID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE U_ALLELES_LOG ( 
  UAID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- L_Markers -----------------------------------------------------
----------------------------------------------------------------

create sequence L_Markers_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE L_MARKERS ( 
  LMID   NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  ALIAS  VARCHAR2 (20), 
  COMM   VARCHAR2 (256), 
  SID    NUMBER (38)   NOT NULL, 
  CID    NUMBER (38)   NOT NULL, 
  POSITION  NUMBER, 
  UNIQUE (NAME, SID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( LMID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 
----------------------------------------------------------------
-- L_Alleles ---------------------------------------------------
----------------------------------------------------------------

create sequence L_Alleles_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE L_ALLELES ( 
  LAID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  LMID  NUMBER (38)   NOT NULL, 
  UNIQUE (NAME, LMID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( LAID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Markers -----------------------------------------------------
----------------------------------------------------------------

create sequence Markers_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 500 order ;

CREATE TABLE MARKERS ( 
  MID       NUMBER (38)   NOT NULL, 
  NAME      VARCHAR2 (20)  NOT NULL, 
  ALIAS     VARCHAR2 (20), 
  COMM      VARCHAR2 (256), 
  SUID      NUMBER (38)   NOT NULL, 
  CID       NUMBER (38)   NOT NULL, 
  P1        VARCHAR2 (40), 
  P2        VARCHAR2 (40), 
  POSITION  NUMBER, 
  ID        NUMBER (38)   NOT NULL, 
  TS        DATE          NOT NULL, 
  UNIQUE (NAME, SUID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( MID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE MARKERS_LOG ( 
  MID    NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  ALIAS  VARCHAR2 (20), 
  COMM   VARCHAR2 (256),
  P1        VARCHAR2 (40), 
  P2        VARCHAR2 (40), 
  POSITION  NUMBER, 
  CID		NUMBER (38)	   NOT NULL,   
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 


----------------------------------------------------------------
-- Marker Sets -------------------------------------------------
----------------------------------------------------------------

create sequence Marker_Sets_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE MARKER_SETS ( 
  MSID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  SUID  NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, SUID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( MSID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE MARKER_SETS_LOG ( 
  MSID  NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Alleles -----------------------------------------------------
----------------------------------------------------------------

create sequence Alleles_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000 order ;

CREATE TABLE ALLELES ( 
  AID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  MID   NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, MID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( AID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE ALLELES_LOG ( 
  AID   NUMBER (38)   NOT NULL, 
  NAME  VARCHAR2 (20)  NOT NULL, 
  COMM  VARCHAR2 (256), 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Genotypes ---------------------------------------------------
----------------------------------------------------------------

create sequence Genotypes_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000 order ;

CREATE TABLE GENOTYPES ( 
  MID        NUMBER (38)   NOT NULL, 
  IID        NUMBER (38)   NOT NULL, 
  AID1       NUMBER (38), 
  AID2       NUMBER (38), 
  SUID       NUMBER (38)   NOT NULL, 
  LEVEL_     NUMBER (38)   NOT NULL, 
  RAW1       VARCHAR2 (20), 
  RAW2       VARCHAR2 (20), 
  REFERENCE  VARCHAR2 (32), 
  ID         NUMBER (38)   NOT NULL, 
  TS         DATE          NOT NULL, 
  COMM       VARCHAR2 (256), 
  PRIMARY KEY ( MID, IID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 100K NEXT 100K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 1048576
   NEXT 1048576
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 255
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 


CREATE TABLE GENOTYPES_LOG ( 
  MID        NUMBER (38)   NOT NULL, 
  IID        NUMBER (38)   NOT NULL, 
  AID1       NUMBER (38), 
  AID2       NUMBER (38), 
  LEVEL_     NUMBER (38)   NOT NULL, 
  RAW1       VARCHAR2 (20), 
  RAW2       VARCHAR2 (20), 
  REFERENCE  VARCHAR2 (32), 
  COMM       VARCHAR2 (256), 
  ID         NUMBER (38)   NOT NULL, 
  TS         DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Filters -----------------------------------------------------
----------------------------------------------------------------

create sequence Filters_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE FILTERS ( 
  FID         NUMBER (38)   NOT NULL, 
  NAME        VARCHAR2 (20)  NOT NULL, 
  EXPRESSION  VARCHAR2 (2000), 
  COMM        VARCHAR2 (256), 
  PID         NUMBER (38)   NOT NULL, 
  SID         NUMBER (38)   NOT NULL, 
  ID          NUMBER (38)   NOT NULL, 
  TS          DATE          NOT NULL, 
  UNIQUE (NAME, PID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( FID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE FILTERS_LOG ( 
  FID         NUMBER (38)   NOT NULL, 
  NAME        VARCHAR2 (20)  NOT NULL, 
  EXPRESSION  VARCHAR2 (2000), 
  COMM        VARCHAR2 (256), 
  SID         NUMBER (38)   NOT NULL, 
  ID          NUMBER (38)   NOT NULL, 
  TS          DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- Analyses -----------------------------------------------------
----------------------------------------------------------------

create sequence Analyses_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE ANALYSES ( 
  ANID   NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  MODE_  VARCHAR2 (1)  NOT NULL, 
  TYPE_  VARCHAR2 (20)  NOT NULL, 
  MSID   NUMBER (38)   NOT NULL, 
  VSID   NUMBER (38)   NOT NULL, 
  COMM   VARCHAR2 (256), 
  PID    NUMBER (38)   NOT NULL, 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL, 
   CHECK (mode_ in ('S', 'M') ) , 
  UNIQUE (NAME, PID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( ANID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE ANALYSES_LOG ( 
  ANID   NUMBER (38)   NOT NULL, 
  NAME   VARCHAR2 (20)  NOT NULL, 
  MODE_  VARCHAR2 (1)  NOT NULL, 
  TYPE_  VARCHAR2 (20)  NOT NULL, 
  MSID   NUMBER (38)   NOT NULL, 
  VSID   NUMBER (38)   NOT NULL, 
  COMM   VARCHAR2 (256), 
  PID    NUMBER (38)   NOT NULL, 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 
----------------------------------------------------------------
-- Data_Files --------------------------------------------------
----------------------------------------------------------------

create sequence Data_Files_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle order ;

CREATE TABLE DATA_FILES ( 
  DFID    NUMBER (38)   NOT NULL, 
  ANID    NUMBER (38)   NOT NULL, 
  NAME    VARCHAR2 (20)  NOT NULL, 
  STATUS  VARCHAR2 (8), 
  COMM    VARCHAR2 (256), 
  ID      NUMBER (38)   NOT NULL, 
  TS      DATE          NOT NULL, 
  UNIQUE (NAME, ANID)    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ), 
  PRIMARY KEY ( DFID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

CREATE TABLE DATA_FILES_LOG ( 
  DFID    NUMBER (38)   NOT NULL, 
  NAME    VARCHAR2 (20)  NOT NULL, 
  STATUS  VARCHAR2 (8), 
  COMM    VARCHAR2 (256), 
  ID      NUMBER (38)   NOT NULL, 
  TS      DATE          NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- R_Prj_Spc ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_PRJ_SPC ( 
  PID  NUMBER (38)   NOT NULL, 
  SID  NUMBER (38)   NOT NULL, 
  PRIMARY KEY ( PID, SID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 


----------------------------------------------------------------
-- R_Prj_SU ----------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_PRJ_SU ( 
  PID   NUMBER (38)   NOT NULL, 
  SUID  NUMBER (38)   NOT NULL, 
  PRIMARY KEY ( SUID, PID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_PRJ_SU ADD 
 FOREIGN KEY (PID) 
  REFERENCES PROJECTS (PID) 
 ON DELETE CASCADE;

ALTER TABLE R_PRJ_SU ADD 
 FOREIGN KEY (SUID) 
  REFERENCES SAMPLING_UNITS (SUID) 
 ON DELETE CASCADE;


----------------------------------------------------------------
-- R_Ind_Grp ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_IND_GRP ( 
  IID  NUMBER (38)   NOT NULL, 
  GID  NUMBER (38)   NOT NULL, 
  ID   NUMBER (38)   NOT NULL, 
  TS   DATE          NOT NULL, 
  PRIMARY KEY ( IID, GID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_IND_GRP ADD 
 FOREIGN KEY (IID) 
  REFERENCES INDIVIDUALS (IID) 
 ON DELETE CASCADE;

ALTER TABLE R_IND_GRP ADD 
 FOREIGN KEY (GID) 
  REFERENCES GROUPS (GID) 
 ON DELETE CASCADE;
----------------------------------------------------------------
-- R_Var_Set ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_VAR_SET ( 
  VSID  NUMBER (38)   NOT NULL, 
  VID   NUMBER (38)   NOT NULL, 
  ID    NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  PRIMARY KEY ( VSID, VID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_VAR_SET ADD 
 FOREIGN KEY (VSID) 
  REFERENCES VARIABLE_SETS (VSID) 
 ON DELETE CASCADE;

ALTER TABLE R_VAR_SET ADD 
 FOREIGN KEY (VID) 
  REFERENCES VARIABLES (VID) 
 ON DELETE CASCADE;


----------------------------------------------------------------
-- R_U_Var_Set -------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_U_VAR_SET ( 
  UVSID  NUMBER (38)   NOT NULL, 
  UVID   NUMBER (38)   NOT NULL, 
  PID	 NUMBER (38)   NOT NULL,
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL, 
  PRIMARY KEY ( UVSID, UVID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_U_VAR_SET ADD 
 FOREIGN KEY (UVSID) 
  REFERENCES U_VARIABLE_SETS (UVSID) 
 ON DELETE CASCADE;

ALTER TABLE R_U_VAR_SET ADD 
 FOREIGN KEY (UVID) 
  REFERENCES U_VARIABLES (UVID) 
 ON DELETE CASCADE;

ALTER TABLE R_U_VAR_SET ADD 
 FOREIGN KEY (PID) 
  REFERENCES PROJECTS (PID) 
 ON DELETE CASCADE;
----------------------------------------------------------------
-- U_Positions ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE U_POSITIONS ( 
  UMSID  NUMBER (38)   NOT NULL, 
  UMID   NUMBER (38)   NOT NULL, 
  VALUE  NUMBER , 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL, 
   CHECK (VALUE >= 0), 
  PRIMARY KEY ( UMSID, UMID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE U_POSITIONS ADD 
 FOREIGN KEY (UMSID) 
  REFERENCES U_MARKER_SETS (UMSID) 
 ON DELETE CASCADE;

ALTER TABLE U_POSITIONS ADD 
 FOREIGN KEY (UMID) 
  REFERENCES U_MARKERS (UMID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Position ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE POSITIONS ( 
  MSID   NUMBER (38)   NOT NULL, 
  MID    NUMBER (38)   NOT NULL, 
  VALUE  NUMBER , 
  ID     NUMBER (38)   NOT NULL, 
  TS     DATE          NOT NULL, 
   CHECK (VALUE >= 0), 
  PRIMARY KEY ( MSID, MID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE POSITIONS ADD 
 FOREIGN KEY (MSID) 
  REFERENCES MARKER_SETS (MSID) 
 ON DELETE CASCADE;

ALTER TABLE POSITIONS ADD 
 FOREIGN KEY (MID) 
  REFERENCES MARKERS (MID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- R_Umid_mid ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_UMID_MID ( 
  PID   NUMBER (38)   NOT NULL, 
  SUID  NUMBER (38)   NOT NULL, 
  UMID  NUMBER (38)   NOT NULL, 
  MID   NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  PRIMARY KEY ( PID, SUID, UMID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_UMID_MID ADD 
 FOREIGN KEY (PID) 
  REFERENCES PROJECTS (PID) 
 ON DELETE CASCADE;

ALTER TABLE R_UMID_MID ADD 
 FOREIGN KEY (SUID) 
  REFERENCES SAMPLING_UNITS (SUID) 
 ON DELETE CASCADE;

ALTER TABLE R_UMID_MID ADD 
 FOREIGN KEY (UMID) 
  REFERENCES U_MARKERS (UMID) 
 ON DELETE CASCADE;

ALTER TABLE R_UMID_MID ADD 
 FOREIGN KEY (MID) 
  REFERENCES MARKERS (MID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- R_Uaid_aid ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_UAID_AID ( 
  PID   NUMBER (38)   NOT NULL, 
  UMID  NUMBER (38)   NOT NULL, 
  AID   NUMBER (38)   NOT NULL, 
  UAID  NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  PRIMARY KEY ( PID, UMID, AID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_UAID_AID ADD 
 FOREIGN KEY (PID) 
  REFERENCES PROJECTS (PID) 
 ON DELETE CASCADE;

ALTER TABLE R_UAID_AID ADD 
 FOREIGN KEY (UMID) 
  REFERENCES U_MARKERS (UMID) 
 ON DELETE CASCADE;

ALTER TABLE R_UAID_AID ADD 
 FOREIGN KEY (AID) 
  REFERENCES ALLELES (AID) 
 ON DELETE CASCADE;

ALTER TABLE R_UAID_AID ADD 
 FOREIGN KEY (UAID) 
  REFERENCES U_ALLELES (UAID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- R_Uvid_vid ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_UVID_VID ( 
  PID   NUMBER (38)   NOT NULL, 
  SID   NUMBER (38)   NOT NULL, 
  UVID  NUMBER (38)   NOT NULL, 
  VID   NUMBER (38)   NOT NULL, 
  TS    DATE          NOT NULL, 
  PRIMARY KEY ( PID, SID, UVID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_UVID_VID ADD 
 FOREIGN KEY (PID) 
  REFERENCES PROJECTS (PID) 
 ON DELETE CASCADE;

ALTER TABLE R_UVID_VID ADD 
 FOREIGN KEY (SID) 
  REFERENCES SPECIES (SID) 
 ON DELETE CASCADE;

ALTER TABLE R_UVID_VID ADD 
 FOREIGN KEY (UVID) 
  REFERENCES U_VARIABLES (UVID) 
 ON DELETE CASCADE;

ALTER TABLE R_UVID_VID ADD 
 FOREIGN KEY (VID) 
  REFERENCES VARIABLES (VID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- R_Prj_Rol ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_PRJ_ROL ( 
  PID  NUMBER (38)   NOT NULL, 
  ID   NUMBER (38)   NOT NULL, 
  RID  NUMBER (38)   NOT NULL, 
  PRIMARY KEY ( PID, ID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_PRJ_ROL ADD 
 FOREIGN KEY (PID) 
  REFERENCES PROJECTS (PID) 
 ON DELETE CASCADE;

ALTER TABLE R_PRJ_ROL ADD 
 FOREIGN KEY (ID) 
  REFERENCES USERS (ID) 
 ON DELETE CASCADE;

ALTER TABLE R_PRJ_ROL ADD 
 FOREIGN KEY (RID) 
  REFERENCES ROLES_ (RID) 
 ON DELETE CASCADE;


----------------------------------------------------------------
-- R_Rol_Pri ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_ROL_PRI ( 
  RID   NUMBER (38)   NOT NULL, 
  PRID  NUMBER (38)   NOT NULL, 
  PRIMARY KEY ( RID, PRID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_ROL_PRI ADD 
 FOREIGN KEY (RID) 
  REFERENCES ROLES_ (RID) 
 ON DELETE CASCADE;

ALTER TABLE R_ROL_PRI ADD 
 FOREIGN KEY (PRID) 
  REFERENCES PRIVILEGES_ (PRID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Priv_Map ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE PRIV_MAP ( 
  PRIV_NUMBER  NUMBER (38)   NOT NULL, 
  NAME         VARCHAR2 (12)  NOT NULL)
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

----------------------------------------------------------------
-- R_Ana_Filt ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_ANA_FILT ( 
  SUID  NUMBER (38)   NOT NULL, 
  FID   NUMBER (38)   NOT NULL, 
  ANID  NUMBER (38)   NOT NULL, 
  PRIMARY KEY ( SUID, FID, ANID ) 
    USING INDEX 
     TABLESPACE USER_DATA PCTFREE 10
     STORAGE ( INITIAL 10K NEXT 10K PCTINCREASE 50 ))
   TABLESPACE USER_DATA
   PCTUSED 40
   INITRANS 1
   MAXTRANS 255
 STORAGE ( 
   INITIAL 10240
   NEXT 10240
   PCTINCREASE 50
   MINEXTENTS 1
   MAXEXTENTS 121
   FREELISTS 1 FREELIST GROUPS 1 )
   NOCACHE; 

ALTER TABLE R_ANA_FILT ADD 
 FOREIGN KEY (SUID) 
  REFERENCES SAMPLING_UNITS (SUID) 
 ON DELETE CASCADE;

ALTER TABLE R_ANA_FILT ADD 
 FOREIGN KEY (FID) 
  REFERENCES FILTERS (FID) 
 ON DELETE CASCADE;

ALTER TABLE R_ANA_FILT ADD 
 FOREIGN KEY (ANID) 
  REFERENCES ANALYSES (ANID) 
 ON DELETE CASCADE;

----------------------------------------------------------------

commit ;


