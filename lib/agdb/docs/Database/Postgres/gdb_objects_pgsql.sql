----------------------------------------------------------------
--
-- $Log$
-- Revision 1.3  2005/02/17 16:18:58  heto
-- Converted DbUMarker to PostgreSQL
-- Redesigned relations: r_uvar_var, r_umid_mid and r_uaid_aid due to errors in the design (redundant data in relations)
-- This design change affected some views!
--
-- Revision 1.2  2005/02/04 15:58:40  heto
-- Converting from Oracle to PostgreSQL or somewhat more SQL server independence.
--
-- Revision 1.1  2005/01/31 12:41:52  heto
-- Adapted schema and views for postgresql. Original files still oracle dependent
--
-- Revision 1.4  2004/03/09 06:51:32  wali
-- Moved the results, results_log, r_fg_ind, category, rtype, parser and r_pa_res tables to the development folder.
--
-- Revision 1.3  2003/12/09 09:00:52  wali
-- Results: Changed so that the result file must be given, the b_name must not be given, the ctg must be given.
-- Results_log: The same as above.
-- Results_log ny version: used in development to try the functionality without the blob.
--
-- The tables category and rtype are added.
--
-- Revision 1.2  2003/11/03 15:17:51  heto
-- Result extension
--
-- Revision 1.1  2002/11/13 08:49:50  heto
-- Added the database scripts to the new CVS
--
-- Revision 1.2  2001/05/14 06:47:30  frob
-- Modifed definition of field TISSUE_TYPE in table SAMPLES_LOG. Now the field has
-- same definition as it has in table SAMPLES.
--
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
--	2001-01-09	TOBJ	Modified the relational table R_ANA_FILT. 
--						Added the control field abort_ and removed 
--						not null on MSID and VSID to the table  
--						ANALYSES.
--  					Added foreign key references from log-
--						tables.
--						Added the table samples_log 
--						Changed the table name from analyses to 
--						file_generation. Changed the table name
--						R_ANA_FILT to R_FG_FLT 
--  2001-02-08	TOBJ	Added the columns P1 and P2 to the 
--						table L_MARKERS.
--  2003-10-13  LISA    Adde Result, R_Res_Ind and R_Res_Fil tables.		
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

drop table R_FG_FLT;

drop table R_Var_Set;
drop table R_Ind_Grp;
drop table R_Prj_SU;
drop table R_Prj_Spc;

drop table R_Prj_Rol;
drop table R_Rol_Pri;

--drop table R_FG_Ind;           --added by Lisa
--drop table R_Res_Pars; 

drop sequence Filters_Seq;
drop table Filters_Log;
drop table Filters;

drop sequence File_Generations_Seq;
drop table File_Generations_Log;
drop table File_Generations;

drop sequence Data_Files_Seq;
drop table Data_Files_Log;
drop table Data_Files;

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
drop table samples_log;
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

drop table Privileges_;

drop sequence Users_Seq;
drop table Users;

drop sequence Projects_Seq;
drop table Projects;

--drop sequence Results_Seq;
--drop table Results;
--drop table Results_Log;

--drop sequence Parser_Seq;
--drop table Parser;

--drop table Category;
--drop table Rtype;

----------------------------------------------------------------
-- Projects ----------------------------------------------------
----------------------------------------------------------------

create sequence Projects_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE PROJECTS ( 
  PID     INT   NOT NULL, 
  NAME    VARCHAR (20)  NOT NULL, 
  COMM    VARCHAR (256), 
  STATUS  CHAR (1)      NOT NULL, 
   CHECK (status in ('E', 'D')) , 
  UNIQUE (NAME)  , 
  PRIMARY KEY ( PID ) 
);


----------------------------------------------------------------
-- Users -------------------------------------------------------
----------------------------------------------------------------

create sequence Users_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE USERS ( 
  ID      INT   NOT NULL, 
  USR     VARCHAR (10)  NOT NULL, 
  PWD     VARCHAR (10)  NOT NULL, 
  NAME    VARCHAR (32), 
  STATUS  CHAR (1)      NOT NULL, 
   CHECK (status in( 'E', 'D')) , 
  UNIQUE (USR), 
  PRIMARY KEY ( ID ) 
);

----------------------------------------------------------------
-- Privileges --------------------------------------------------
----------------------------------------------------------------

CREATE TABLE PRIVILEGES_ ( 
  PRID  INT   NOT NULL, 
  NAME  VARCHAR (12)  NOT NULL, 
  COMM  VARCHAR (256), 
  UNIQUE ( NAME ), 
  PRIMARY KEY ( PRID ) 
);

----------------------------------------------------------------
-- Roles -------------------------------------------------------
----------------------------------------------------------------
create sequence Roles_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;  

CREATE TABLE ROLES_ ( 
  RID   INT   NOT NULL, 
  PID   INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  UNIQUE (PID, NAME), 
  PRIMARY KEY ( RID ) 
);

----------------------------------------------------------------
-- Species -----------------------------------------------------
----------------------------------------------------------------

create sequence Species_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle ;

CREATE TABLE SPECIES ( 
  SID   INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  UNIQUE (NAME), 
  PRIMARY KEY ( SID ) 
);

----------------------------------------------------------------
-- Sampling_Units ----------------------------------------------
----------------------------------------------------------------

create sequence Sampling_Units_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle ;

CREATE TABLE SAMPLING_UNITS 
( 
  SUID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  STATUS VARCHAR(1)	NOT NULL,
  SID   INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL,
    CHECK (status in ('E', 'D') ), 
  UNIQUE (NAME) , 
  PRIMARY KEY ( SUID ) 
);


CREATE TABLE SAMPLING_UNITS_LOG 
( 
  SUID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  STATUS VARCHAR (1)	NOT NULL,
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);

ALTER TABLE SAMPLING_UNITS_LOG ADD 
 FOREIGN KEY (SUID) 
  REFERENCES SAMPLING_UNITS (SUID) 
 ON DELETE CASCADE;

   
----------------------------------------------------------------
-- Individuals --------------------------------------------------
----------------------------------------------------------------

create sequence Individuals_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000 ;

CREATE TABLE INDIVIDUALS ( 
  IID         INT   NOT NULL, 
  IDENTITY    VARCHAR (11)  NOT NULL, 
  ALIAS       VARCHAR (11), 
  FATHER      INT, 
  MOTHER      INT, 
  SEX         CHAR (1)      NOT NULL, 
  BIRTH_DATE  DATE, 
  STATUS	  VARCHAR(1)	NOT NULL,
  SUID        INT   NOT NULL, 
  ID          INT   NOT NULL, 
  TS          DATE          NOT NULL, 
  COMM        VARCHAR (256), 
   CHECK (sex in ('M', 'F', 'U')) , 
   CHECK (status in ('E', 'D') ),
  UNIQUE ( SUID, IDENTITY)  , 
  PRIMARY KEY ( IID ) 
);

CREATE TABLE INDIVIDUALS_LOG ( 
  IID         INT   NOT NULL, 
  IDENTITY    VARCHAR (11)  NOT NULL, 
  ALIAS       VARCHAR (11), 
  FATHER      INT, 
  MOTHER      INT, 
  SEX         CHAR (1)      NOT NULL, 
  BIRTH_DATE  DATE, 
  STATUS	  VARCHAR(1)	NOT NULL,
  COMM        VARCHAR (256), 
  ID          INT   NOT NULL, 
  TS          DATE          NOT NULL,
  SUID        INT    -- ADDED BY HETO 2005-02-04
);

ALTER TABLE INDIVIDUALS_LOG ADD 
 FOREIGN KEY (IID) 
  REFERENCES INDIVIDUALS (IID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Samples -----------------------------------------------------
----------------------------------------------------------------
create sequence Samples_Seq 
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000  ;
  
CREATE TABLE SAMPLES 
( 
  SAID          INT   NOT NULL, 
  NAME          VARCHAR (20)  NOT NULL, 
  TISSUE_TYPE   VARCHAR (20), 
  EXPERIMENTER  VARCHAR (32), 
  DATE_         DATE, 
  TREATMENT     VARCHAR (20), 
  STORAGE       VARCHAR (20), 
  COMM          VARCHAR (256), 
  IID           INT   NOT NULL, 
  ID            INT   NOT NULL, 
  TS            DATE          NOT NULL, 
  UNIQUE (NAME, IID)    , 
  PRIMARY KEY ( SAID ) 
);

ALTER TABLE SAMPLES ADD CONSTRAINT CT_SAMPLES_IID
 FOREIGN KEY (IID) 
  REFERENCES INDIVIDUALS (IID) 
 ON DELETE CASCADE;

ALTER TABLE SAMPLES ADD CONSTRAINT CT_SAMPLES_ID
 FOREIGN KEY (ID) 
  REFERENCES USERS (ID) 
 ON DELETE CASCADE;


CREATE TABLE SAMPLES_LOG ( 
  SAID          INT   NOT NULL, 
  NAME          VARCHAR (20)  NOT NULL, 
  TISSUE_TYPE   VARCHAR (20),
  EXPERIMENTER  VARCHAR (32), 
  DATE_         DATE, 
  TREATMENT     VARCHAR (20), 
  STORAGE       VARCHAR (20), 
  COMM          VARCHAR (256), 
  ID            INT   NOT NULL, 
  TS            DATE          NOT NULL
);


ALTER TABLE SAMPLES_LOG ADD 
 FOREIGN KEY (SAID)
  REFERENCES SAMPLES (SAID) ON DELETE CASCADE;


CREATE INDEX IDX_SAMPLES_LOG_SAID ON 
  SAMPLES_LOG(SAID) ; 

----------------------------------------------------------------
-- Groupings ---------------------------------------------------
----------------------------------------------------------------

create sequence Groupings_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 100  ;

CREATE TABLE GROUPINGS ( 
  GSID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  SUID  INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, SUID)    ,
  PRIMARY KEY ( GSID ) 
);

CREATE TABLE GROUPINGS_LOG ( 
  GSID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);

ALTER TABLE GROUPINGS_LOG ADD 
 FOREIGN KEY (GSID) 
  REFERENCES GROUPINGS (GSID) 
 ON DELETE CASCADE;
   
----------------------------------------------------------------
-- Groups ------------------------------------------------------
----------------------------------------------------------------

create sequence Groups_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 100  ;

CREATE TABLE GROUPS ( 
  GID   INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  GSID  INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, GSID)    ,
  PRIMARY KEY ( GID ) 
);

ALTER TABLE GROUPS ADD 
 FOREIGN KEY (GSID) 
  REFERENCES GROUPINGS (GSID) 
 ON DELETE CASCADE;

CREATE TABLE GROUPS_LOG ( 
  GID   INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);
   


ALTER TABLE GROUPS_LOG ADD 
 FOREIGN KEY (GID) 
  REFERENCES GROUPS (GID) 
 ON DELETE CASCADE;
   
----------------------------------------------------------------
-- U_Variables ---------------------------------------------------
----------------------------------------------------------------

create sequence U_Variables_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 100  ;

CREATE TABLE U_VARIABLES ( 
  UVID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  TYPE  VARCHAR (1)  NOT NULL, 
  UNIT  VARCHAR (10), 
  COMM  VARCHAR (256), 
  PID   INT   NOT NULL, 
  SID   INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
   CHECK (type in ('E', 'N')) , 
  UNIQUE (NAME, PID, SID), 
  PRIMARY KEY ( UVID ) 
);

CREATE TABLE U_VARIABLES_LOG ( 
  UVID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  TYPE  VARCHAR (1)  NOT NULL, 
  UNIT  VARCHAR (10), 
  COMM  VARCHAR (256), 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);

----------------------------------------------------------------
-- U_Variable Sets -----------------------------------------------
----------------------------------------------------------------

create sequence U_Variable_Sets_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE U_VARIABLE_SETS ( 
  UVSID  INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  COMM   VARCHAR (256), 
  PID    INT   NOT NULL, 
  SID    INT   NOT NULL, 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL, 
  UNIQUE (NAME, PID, SID)    , 
  PRIMARY KEY ( UVSID ) 
);

CREATE TABLE U_VARIABLE_SETS_LOG ( 
  UVSID  INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  COMM   VARCHAR (256), 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL
);

----------------------------------------------------------------
-- Variables ---------------------------------------------------
----------------------------------------------------------------

create sequence Variables_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 100  ;

CREATE TABLE VARIABLES ( 
  VID   INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  TYPE  VARCHAR (1)  NOT NULL, 
  UNIT  VARCHAR (10), 
  COMM  VARCHAR (256), 
  SUID  INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
   CHECK (type in ('E', 'N')) , 
  UNIQUE (NAME, SUID)  , 
  PRIMARY KEY ( VID ) 
);

CREATE TABLE VARIABLES_LOG ( 
  VID   INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  TYPE  VARCHAR (1)  NOT NULL, 
  UNIT  VARCHAR (10), 
  COMM  VARCHAR (256), 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);
  
ALTER TABLE VARIABLES_LOG ADD 
 FOREIGN KEY (VID) 
  REFERENCES VARIABLES (VID) 
 ON DELETE CASCADE;


----------------------------------------------------------------
-- Variable Sets -----------------------------------------------
----------------------------------------------------------------

create sequence Variable_Sets_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE VARIABLE_SETS ( 
  VSID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  SUID  INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, SUID)  ,
  PRIMARY KEY ( VSID ) 
);

CREATE TABLE VARIABLE_SETS_LOG ( 
  VSID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);

ALTER TABLE VARIABLE_SETS_LOG ADD 
 FOREIGN KEY (VSID) 
  REFERENCES VARIABLE_SETS (VSID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Phenotypes --------------------------------------------------
----------------------------------------------------------------

create sequence Phenotypes_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000  ;

CREATE TABLE PHENOTYPES ( 
  VID        INT   NOT NULL, 
  IID        INT   NOT NULL, 
  SUID       INT   NOT NULL, 
  VALUE      VARCHAR (20)  NOT NULL, 
  DATE_      DATE, 
  REFERENCE  VARCHAR (32), 
  ID         INT   NOT NULL, 
  TS         DATE          NOT NULL, 
  COMM       VARCHAR (256), 
  PRIMARY KEY ( VID, IID ) 
);

CREATE TABLE PHENOTYPES_LOG ( 
  VID        INT   NOT NULL, 
  IID        INT   NOT NULL, 
  VALUE      VARCHAR (20)  NOT NULL, 
  DATE_      DATE, 
  REFERENCE  VARCHAR (32), 
  COMM       VARCHAR (256), 
  ID         INT   NOT NULL, 
  TS         DATE          NOT NULL
);

ALTER TABLE PHENOTYPES_LOG ADD 
 FOREIGN KEY (VID, IID) 
  REFERENCES PHENOTYPES (VID, IID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Chromosomes -------------------------------------------------
----------------------------------------------------------------

create sequence Chromosomes_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE CHROMOSOMES ( 
  CID   INT   NOT NULL, 
  NAME  VARCHAR (2)  NOT NULL, 
  COMM  VARCHAR (256), 
  SID   INT   NOT NULL, 
  UNIQUE (NAME, SID) , 
  PRIMARY KEY ( CID ) 
);


----------------------------------------------------------------
-- U_Markers -----------------------------------------------------
----------------------------------------------------------------

create sequence U_Markers_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 500  ;

CREATE TABLE U_MARKERS ( 
  UMID   INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  ALIAS  VARCHAR (20), 
  COMM   VARCHAR (256), 
  POSITION  FLOAT, 
  PID	 INT   NOT NULL,
  SID	 INT   NOT NULL,
  CID    INT   NOT NULL, 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL, 
  UNIQUE (NAME, PID, SID)  ,
  PRIMARY KEY ( UMID ) 
);

CREATE TABLE U_MARKERS_LOG ( 
  UMID   INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  ALIAS  VARCHAR (20), 
  COMM   VARCHAR (256), 
  POSITION  FLOAT, 
  CID		INT	   NOT NULL,
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL
);
  

----------------------------------------------------------------
-- U_Marker Sets -------------------------------------------------
----------------------------------------------------------------

create sequence U_Marker_Sets_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE U_MARKER_SETS ( 
  UMSID  INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  COMM   VARCHAR (256), 
  PID    INT   NOT NULL, 
  SID    INT   NOT NULL, 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL, 
  UNIQUE (NAME, PID, SID), 
  PRIMARY KEY ( UMSID ) 
);

CREATE TABLE U_MARKER_SETS_LOG ( 
  UMSID  INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  COMM   VARCHAR (256), 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL
);

----------------------------------------------------------------
-- U_Alleles -----------------------------------------------------
----------------------------------------------------------------

create sequence U_Alleles_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000  ;

CREATE TABLE U_ALLELES ( 
  UAID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  UMID  INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, UMID), 
  PRIMARY KEY ( UAID ) 
);

CREATE TABLE U_ALLELES_LOG ( 
  UAID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);

----------------------------------------------------------------
-- L_Markers -----------------------------------------------------
----------------------------------------------------------------

create sequence L_Markers_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE L_MARKERS ( 
  LMID      INT   NOT NULL, 
  NAME      VARCHAR (20)  NOT NULL, 
  ALIAS     VARCHAR (20), 
  COMM      VARCHAR (256), 
  SID       INT   NOT NULL, 
  CID       INT   NOT NULL, 
  P1        VARCHAR (40), 
  P2        VARCHAR (40), 
  POSITION  FLOAT, 
  UNIQUE (NAME, SID)  , 
  PRIMARY KEY ( LMID ) 
);
  
----------------------------------------------------------------
-- L_Alleles ---------------------------------------------------
----------------------------------------------------------------

create sequence L_Alleles_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE L_ALLELES ( 
  LAID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  LMID  INT   NOT NULL, 
  UNIQUE (NAME, LMID)    , 
  PRIMARY KEY ( LAID ) 
);
  

----------------------------------------------------------------
-- Markers -----------------------------------------------------
----------------------------------------------------------------

create sequence Markers_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 500  ;

CREATE TABLE MARKERS ( 
  MID       INT   NOT NULL, 
  NAME      VARCHAR (20)  NOT NULL, 
  ALIAS     VARCHAR (20), 
  COMM      VARCHAR (256), 
  SUID      INT   NOT NULL, 
  CID       INT   NOT NULL, 
  P1        VARCHAR (40), 
  P2        VARCHAR (40), 
  POSITION  FLOAT, 
  ID        INT   NOT NULL, 
  TS        DATE          NOT NULL, 
  UNIQUE (NAME, SUID)  , 
  PRIMARY KEY ( MID ) 
);
  

CREATE TABLE MARKERS_LOG ( 
  MID    INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  ALIAS  VARCHAR (20), 
  COMM   VARCHAR (256),
  P1        VARCHAR (40), 
  P2        VARCHAR (40), 
  POSITION  FLOAT, 
  CID		INT	   NOT NULL,   
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL
);
  

ALTER TABLE MARKERS_LOG ADD 
 FOREIGN KEY (MID) 
  REFERENCES MARKERS (MID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Marker Sets -------------------------------------------------
----------------------------------------------------------------

create sequence Marker_Sets_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE MARKER_SETS ( 
  MSID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  SUID  INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, SUID)    , 
  PRIMARY KEY ( MSID ) 
);

CREATE TABLE MARKER_SETS_LOG ( 
  MSID  INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);

ALTER TABLE MARKER_SETS_LOG ADD 
 FOREIGN KEY (MSID) 
  REFERENCES MARKER_SETS (MSID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Alleles -----------------------------------------------------
----------------------------------------------------------------

create sequence Alleles_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000  ;

CREATE TABLE ALLELES ( 
  AID   INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  MID   INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  UNIQUE (NAME, MID)   , 
  PRIMARY KEY ( AID ) 
);

ALTER TABLE ALLELES ADD 
 FOREIGN KEY (MID) 
  REFERENCES MARKERS (MID) 
 ON DELETE CASCADE;

CREATE TABLE ALLELES_LOG ( 
  AID   INT   NOT NULL, 
  NAME  VARCHAR (20)  NOT NULL, 
  COMM  VARCHAR (256), 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL
);


ALTER TABLE ALLELES_LOG ADD 
 FOREIGN KEY (AID) 
  REFERENCES ALLELES (AID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Genotypes ---------------------------------------------------
----------------------------------------------------------------

create sequence Genotypes_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle cache 2000  ;

CREATE TABLE GENOTYPES ( 
  MID        INT   NOT NULL, 
  IID        INT   NOT NULL, 
  AID1       INT, 
  AID2       INT, 
  SUID       INT   NOT NULL, 
  LEVEL_     INT   NOT NULL, 
  RAW1       VARCHAR (20), 
  RAW2       VARCHAR (20), 
  REFERENCE  VARCHAR (32), 
  ID         INT   NOT NULL, 
  TS         DATE          NOT NULL, 
  COMM       VARCHAR (256), 
  PRIMARY KEY ( MID, IID ) 
);


CREATE TABLE GENOTYPES_LOG ( 
  MID        INT   NOT NULL, 
  IID        INT   NOT NULL, 
  AID1       INT, 
  AID2       INT, 
  LEVEL_     INT   NOT NULL, 
  RAW1       VARCHAR (20), 
  RAW2       VARCHAR (20), 
  REFERENCE  VARCHAR (32), 
  COMM       VARCHAR (256), 
  ID         INT   NOT NULL, 
  TS         DATE          NOT NULL
);

ALTER TABLE GENOTYPES_LOG ADD 
 FOREIGN KEY (MID, IID) 
  REFERENCES GENOTYPES (MID, IID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Filters -----------------------------------------------------
----------------------------------------------------------------

create sequence Filters_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE FILTERS ( 
  FID         INT   NOT NULL, 
  NAME        VARCHAR (20)  NOT NULL, 
  EXPRESSION  VARCHAR (2000), 
  COMM        VARCHAR (256), 
  PID         INT   NOT NULL, 
  SID         INT   NOT NULL, 
  ID          INT   NOT NULL, 
  TS          DATE          NOT NULL, 
  UNIQUE (NAME, PID)    , 
  PRIMARY KEY ( FID ) 
);

CREATE TABLE FILTERS_LOG ( 
  FID         INT   NOT NULL, 
  NAME        VARCHAR (20)  NOT NULL, 
  EXPRESSION  VARCHAR (2000), 
  COMM        VARCHAR (256), 
  SID         INT   NOT NULL, 
  ID          INT   NOT NULL, 
  TS          DATE          NOT NULL
);

ALTER TABLE FILTERS_LOG ADD 
 FOREIGN KEY (FID) 
  REFERENCES FILTERS (FID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- File Generation ---------------------------------------------
----------------------------------------------------------------

create sequence File_Generations_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE FILE_GENERATIONS ( 
  FGID   INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  MODE_  VARCHAR (1)  NOT NULL, 
  TYPE_  VARCHAR (20)  NOT NULL, 
  MSID   INT   , 
  VSID   INT   , 
  COMM   VARCHAR (256), 
  PID    INT   NOT NULL,
  ABORT_ INT   NOT NULL, 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL, 
   CHECK (mode_ in ('S', 'M') ) , 
  UNIQUE (PID, NAME)    , 
  PRIMARY KEY ( FGID ) 
);

CREATE TABLE FILE_GENERATIONS_LOG ( 
  FGID   INT   NOT NULL, 
  NAME   VARCHAR (20)  NOT NULL, 
  MODE_  VARCHAR (1)  NOT NULL, 
  TYPE_  VARCHAR (20)  NOT NULL, 
  MSID   INT   NOT NULL, 
  VSID   INT   NOT NULL, 
  COMM   VARCHAR (256), 
  PID    INT   NOT NULL, 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL
);

----------------------------------------------------------------
-- Data_Files --------------------------------------------------
----------------------------------------------------------------

create sequence Data_Files_Seq
  minvalue 1001 maxvalue 2000000000 start with 1001 cycle  ;

CREATE TABLE DATA_FILES ( 
  DFID    INT   NOT NULL, 
  FGID    INT   NOT NULL, 
  NAME    VARCHAR (20)  NOT NULL, 
  STATUS  VARCHAR (8), 
  COMM    VARCHAR (256), 
  ID      INT   NOT NULL, 
  TS      DATE          NOT NULL, 
  UNIQUE (NAME, FGID)    , 
  PRIMARY KEY ( DFID ) 
);

CREATE TABLE DATA_FILES_LOG ( 
  DFID    INT   NOT NULL, 
  NAME    VARCHAR (20)  NOT NULL, 
  STATUS  VARCHAR (8), 
  COMM    VARCHAR (256), 
  ID      INT   NOT NULL, 
  TS      DATE          NOT NULL
);

ALTER TABLE DATA_FILES_LOG ADD
 FOREIGN KEY (DFID) 
  REFERENCES DATA_FILES (DFID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- R_Prj_Spc ---------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_PRJ_SPC ( 
  PID  INT   NOT NULL, 
  SID  INT   NOT NULL, 
  PRIMARY KEY ( PID, SID ) 
);


----------------------------------------------------------------
-- R_Prj_SU ----------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_PRJ_SU ( 
  PID   INT   NOT NULL, 
  SUID  INT   NOT NULL, 
  PRIMARY KEY ( SUID, PID ) 
);

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
  IID  INT   NOT NULL, 
  GID  INT   NOT NULL, 
  ID   INT   NOT NULL, 
  TS   DATE          NOT NULL, 
  PRIMARY KEY ( IID, GID ) 
);

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
  VSID  INT   NOT NULL, 
  VID   INT   NOT NULL, 
  ID    INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  PRIMARY KEY ( VSID, VID ) 
);

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
  UVSID  INT   NOT NULL, 
  UVID   INT   NOT NULL, 
  PID	 INT   NOT NULL,
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL, 
  PRIMARY KEY ( UVSID, UVID ) 
);

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
  UMSID  INT   NOT NULL, 
  UMID   INT   NOT NULL, 
  VALUE  FLOAT , 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL, 
   CHECK (VALUE >= 0), 
  PRIMARY KEY ( UMSID, UMID ) 
);

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
  MSID   INT   NOT NULL, 
  MID    INT   NOT NULL, 
  VALUE  FLOAT , 
  ID     INT   NOT NULL, 
  TS     DATE          NOT NULL, 
   CHECK (VALUE >= 0), 
  PRIMARY KEY ( MSID, MID ) 
);

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
--  PID   INT   NOT NULL, 
--  SUID  INT   NOT NULL, 
  UMID  INT   NOT NULL, 
  MID   INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  PRIMARY KEY ( MID ) 
);

--ALTER TABLE R_UMID_MID ADD 
-- FOREIGN KEY (PID) 
--  REFERENCES PROJECTS (PID) 
-- ON DELETE CASCADE;

--ALTER TABLE R_UMID_MID ADD 
-- FOREIGN KEY (SUID) 
--  REFERENCES SAMPLING_UNITS (SUID) 
-- ON DELETE CASCADE;

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
--  PID   INT   NOT NULL, 
--  UMID  INT   NOT NULL, 
  AID   INT   NOT NULL, 
  UAID  INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  PRIMARY KEY (  AID ) 
);

--ALTER TABLE R_UAID_AID ADD 
-- FOREIGN KEY (PID) 
--  REFERENCES PROJECTS (PID) 
-- ON DELETE CASCADE;

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
--  PID   INT   NOT NULL, 
--  SUID  INT   NOT NULL, 
  UVID  INT   NOT NULL, 
  VID   INT   NOT NULL, 
  TS    DATE          NOT NULL, 
  PRIMARY KEY ( VID ) 
);

--ALTER TABLE R_UVID_VID ADD 
-- FOREIGN KEY (PID) 
--  REFERENCES PROJECTS (PID) 
-- ON DELETE CASCADE;

--ALTER TABLE R_UVID_VID ADD 
-- FOREIGN KEY (SUID) 
--  REFERENCES SAMPLING_UNITS (SUID) 
-- ON DELETE CASCADE;

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
  PID  INT   NOT NULL, 
  ID   INT   NOT NULL, 
  RID  INT   NOT NULL, 
  PRIMARY KEY ( PID, ID ) 
);

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
  RID   INT   NOT NULL, 
  PRID  INT   NOT NULL, 
  PRIMARY KEY ( RID, PRID ) 
);

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
  PRIV_NUMBER  INT   NOT NULL, 
  NAME         VARCHAR (12)  NOT NULL
);

----------------------------------------------------------------
-- R_FG_FLT ----------------------------------------------------
----------------------------------------------------------------

CREATE TABLE R_FG_FLT ( 
  FGID  INT   NOT NULL, 
  SUID  INT   NOT NULL, 
  FID   INT   NOT NULL,
  GSID	INT	  	  	  ,
  PRIMARY KEY ( FGID, SUID ) 
);

ALTER TABLE R_FG_FLT ADD 
 FOREIGN KEY (SUID) 
  REFERENCES SAMPLING_UNITS (SUID) ;

ALTER TABLE R_FG_FLT ADD 
 FOREIGN KEY (FID) 
  REFERENCES FILTERS (FID) ;

ALTER TABLE R_FG_FLT ADD 
 FOREIGN KEY (FGID) 
  REFERENCES FILE_GENERATIONS (FGID) ON DELETE CASCADE;

ALTER TABLE R_FG_FLT ADD
 FOREIGN KEY (GSID)
  REFERENCES GROUPINGS (GSID) ;

----------------------------------------------------------------
commit ;


