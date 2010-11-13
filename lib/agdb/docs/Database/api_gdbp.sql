----------------------------------------------------------------
--
-- This script creates the SU package 
--
-- This script must be run as SysAdm
--
--  2000-10-07	TOBJ	First version
--  See history for api_gdbp_b.sql
--
----------------------------------------------------------------

CREATE OR REPLACE package GDBP as
----------------------------------------

/*  procedure Log_Message (
    p_message in      ERR_MESSAGE.MESSAGE%TYPE,
    p_source  in      ERR_MESSAGE.SOURCE%TYPE,
    p_id      in      USERS.ID%TYPE DEFAULT NULL );
*/
----------------------------------------
  procedure Create_Sampling_Unit (
    p_pid     in      PROJECTS.PID%TYPE,
    p_suid    in out  SAMPLING_UNITS.SUID%TYPE,
    p_name    in      SAMPLING_UNITS.NAME%TYPE,
    p_comm	  in      SAMPLING_UNITS.COMM%TYPE,
    p_status  in	  SAMPLING_UNITS.STATUS%TYPE,
	p_sid     in      SPECIES.SID%TYPE,
    p_id      in      USERS.ID%TYPE,
    p_message in out  VARCHAR2 ) ;

  procedure Update_Sampling_Unit (
    p_suid	  in	  SAMPLING_UNITS.SUID%TYPE,
    p_name	  in	  SAMPLING_UNITS.NAME%TYPE,
	p_comm	  in	  SAMPLING_UNITS.COMM%TYPE,
    p_status  in	  SAMPLING_UNITS.STATUS%TYPE,
    p_id	  in	  USERS.ID%TYPE,
    p_message in out  VARCHAR2) ;

  procedure Delete_Sampling_Unit (
    p_pid     in      R_PRJ_SU.PID%TYPE,
    p_suid	  in	  SAMPLING_UNITS.SUID%TYPE,
    p_message in out  VARCHAR2) ;


----------------------------------------

  procedure Create_Individual (
    p_iid	     in out	INDIVIDUALS.IID%TYPE,
    p_identity	 in		INDIVIDUALS.IDENTITY%TYPE,
    p_alias		 in		INDIVIDUALS.ALIAS%TYPE,
    p_father	 in		INDIVIDUALS.FATHER%TYPE,
    p_mother	 in		INDIVIDUALS.MOTHER%TYPE,
    p_sex		 in		INDIVIDUALS.SEX%TYPE,
    p_birth_date in		INDIVIDUALS.BIRTH_DATE%TYPE,
    p_status	 in		INDIVIDUALS.STATUS%TYPE,
    p_comm		 in		INDIVIDUALS.COMM%TYPE,
    p_suid		 in		INDIVIDUALS.SUID%TYPE,
    p_id		 in		INDIVIDUALS.ID%TYPE,
    p_message	 in out varchar2) ;

  procedure Set_Parents (
    p_suid	    in	   INDIVIDUALS.SUID%TYPE,
    p_identity	in	   INDIVIDUALS.IDENTITY%TYPE,
    p_fidentity	in	   INDIVIDUALS.IDENTITY%TYPE,
    p_midentity	in	   INDIVIDUALS.IDENTITY%TYPE,
    p_message	in out varchar2) ;

	procedure Create_Or_Update_Individual (
    p_iid	     in out	INDIVIDUALS.IID%TYPE,
    p_identity	 in	   INDIVIDUALS.IDENTITY%TYPE,
    p_alias		 in	   INDIVIDUALS.ALIAS%TYPE,
    p_father	 in    INDIVIDUALS.FATHER%TYPE,
    p_mother	 in	   INDIVIDUALS.MOTHER%TYPE,
    p_sex		 in    INDIVIDUALS.SEX%TYPE,
    p_birth_date in	   INDIVIDUALS.BIRTH_DATE%TYPE,
    p_status     in	   INDIVIDUALS.STATUS%TYPE,
    p_comm		 in	   INDIVIDUALS.COMM%TYPE,
    p_suid		 in	   INDIVIDUALS.SUID%TYPE,
    p_id		 in	   INDIVIDUALS.ID%TYPE,
    p_message	 in out    varchar2);

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
    p_message	 in out varchar2) ;

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
    p_message	 in out varchar2) ;

  procedure Delete_Individual (
    p_iid	  in	  INDIVIDUALS.IID%TYPE,
    p_message in out  varchar2) ;

  procedure Check_Individual (
    p_iid	  in	  INDIVIDUALS.IID%TYPE,
    p_status in out   int) ;

----------------------------------------

  procedure Create_Grouping (
    p_gsid	    in out	GROUPINGS.GSID%TYPE,
    p_name		in		GROUPINGS.NAME%TYPE,
    p_comm		in		GROUPINGS.COMM%TYPE,
    p_suid		in		GROUPINGS.SUID%TYPE,
    p_id		in		GROUPINGS.ID%TYPE,
    p_message	in out  varchar2) ;


    procedure Copy_Grouping (
    p_togsid	   in out	GROUPINGS.GSID%TYPE,
    p_toname	   in		GROUPINGS.NAME%TYPE,
    p_tocomm	   in		GROUPINGS.COMM%TYPE,
    p_fromgsid	   in		GROUPINGS.GSID%TYPE,
    p_id	   in		GROUPINGS.ID%TYPE,
    p_suid	   in		GROUPINGS.SUID%TYPE,
    p_message  in out  	varchar2);

  procedure Create_Or_Update_Grouping (
    p_gsid	    in out	 GROUPINGS.GSID%TYPE,
    p_name		in		 GROUPINGS.NAME%TYPE,
    p_comm		in		 GROUPINGS.COMM%TYPE,
    p_suid		in		 GROUPINGS.SUID%TYPE,
    p_id		in		 GROUPINGS.ID%TYPE,
    p_message	in out   varchar2) ;

  procedure Update_Grouping (
    p_gsid	  in	  GROUPINGS.GSID%TYPE,
    p_name	  in	  GROUPINGS.NAME%TYPE,
    p_comm	  in	  GROUPINGS.COMM%TYPE,
    p_id	  in	  GROUPINGS.ID%TYPE,
    p_message in out  varchar2) ;

  procedure Delete_Grouping (
    p_gsid	  in	  GROUPINGS.GSID%TYPE,
    p_message in out  varchar2) ;

----------------------------------------

  procedure Create_Group (
    p_gid	   in out	GROUPS.GID%TYPE,
    p_name	   in		GROUPS.NAME%TYPE,
    p_comm	   in		GROUPS.COMM%TYPE,
    p_gsid	   in		GROUPS.GSID%TYPE,
    p_id	   in		GROUPS.ID%TYPE,
    p_message  in out  	varchar2) ;

    ----------------------------------------
procedure Copy_Group (
    p_togid	   in out	GROUPS.GID%TYPE,
    p_toname	   in		GROUPS.NAME%TYPE,
    p_tocomm	   in		GROUPS.COMM%TYPE,
    p_fromgsid	   in		GROUPS.GSID%TYPE,
    p_fromgid	   in		GROUPS.GID%TYPE,
    p_togsid	   in		GROUPS.GSID%TYPE,
    p_id	   in		GROUPS.ID%TYPE,
    p_message  in out  	varchar2);

  procedure Create_Or_Update_Group (
    p_gid	   in out	GROUPS.GID%TYPE,
    p_name	   in		GROUPS.NAME%TYPE,
    p_comm	   in		GROUPS.COMM%TYPE,
    p_gsid	   in		GROUPS.GSID%TYPE,
    p_id	   in		GROUPS.ID%TYPE,
    p_message  in out  	varchar2) ;

  procedure Update_Group (
    p_gid	  in	  GROUPS.GID%TYPE,
    p_name	  in	  GROUPS.NAME%TYPE,
    p_comm	  in	  GROUPS.COMM%TYPE,
    p_id	  in	  GROUPS.ID%TYPE,
    p_message in out  varchar2) ;

  procedure Delete_Group (
    p_gid	    in	    GROUPS.GID%TYPE,
    p_message	in out  varchar2) ;

  procedure Create_Group_Link (
    p_gsid	    in	    GROUPINGS.GSID%TYPE,
    p_identity	in		INDIVIDUALS.IDENTITY%TYPE,
    p_grp_name	in		GROUPS.NAME%TYPE,
    p_id		in		R_IND_GRP.ID%TYPE,
    p_message	in out  varchar2) ;

  procedure Create_Group_Link_Thr_Alias (
    p_gsid	    in	    GROUPINGS.GSID%TYPE,
    p_alias		in		INDIVIDUALS.ALIAS%TYPE,
    p_grp_name	in		GROUPS.NAME%TYPE,
    p_id		in		R_IND_GRP.ID%TYPE,
    p_message	in out  varchar2) ;

  procedure Create_Group_Link (
    p_gid	    in	    GROUPS.GID%TYPE,
    p_iid		in		INDIVIDUALS.IID%TYPE,
    p_id		in		R_IND_GRP.ID%TYPE,
    p_message	in out  varchar2) ;


  procedure Delete_Group_Link (
    p_iid	   in	   INDIVIDUALS.IID%TYPE,
    p_gid	   in	   GROUPS.GID%TYPE,
    p_message  in out  varchar2) ;

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
    p_message	 in out  varchar2) ;

  procedure Create_Phenotype_Thr_Alias (
    p_suid	     in	     PHENOTYPES.SUID%TYPE,
    p_alias		 in		 INDIVIDUALS.ALIAS%TYPE,
    p_name		 in		 VARIABLES.NAME%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) ;

  procedure Create_Phenotype (
    p_iid		 in		 PHENOTYPES.IID%TYPE,
    p_vid		 in		 PHENOTYPES.VID%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) ;

procedure Create_Or_Update_Phenotype (
    p_suid	     in	     PHENOTYPES.SUID%TYPE,
    p_identity	 in		 INDIVIDUALS.IDENTITY%TYPE,
    p_vname		 in		 VARIABLES.NAME%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) ;

 procedure Create_Or_Update_P_Thr_Alias (
    p_suid	     in	     PHENOTYPES.SUID%TYPE,
    p_alias	 	 in		 INDIVIDUALS.ALIAS%TYPE,
    p_vname		 in		 VARIABLES.NAME%TYPE,
    p_value		 in		 PHENOTYPES.VALUE%TYPE,
    p_date		 in		 PHENOTYPES.DATE_%TYPE,
    p_reference	 in		 PHENOTYPES.REFERENCE%TYPE,
    p_comm		 in		 PHENOTYPES.COMM%TYPE,
    p_id		 in		 PHENOTYPES.ID%TYPE,
    p_message	 in out  varchar2) ;


  procedure Update_Phenotype (
    p_vid	      in	  PHENOTYPES.VID%TYPE,
    p_iid		  in	  PHENOTYPES.IID%TYPE,
    p_value		  in	  PHENOTYPES.VALUE%TYPE,
    p_date		  in	  PHENOTYPES.DATE_%TYPE,
    p_reference	  in	  PHENOTYPES.REFERENCE%TYPE,
    p_comm		  in	  PHENOTYPES.COMM%TYPE,
    p_id		  in	  PHENOTYPES.ID%TYPE,
    p_message	  in out  varchar2) ;

  procedure Update_Phenotype (
    p_vname		  in	  VARIABLES.NAME%TYPE,
    p_identity    in	  INDIVIDUALS.IDENTITY%TYPE,
    p_value		  in	  PHENOTYPES.VALUE%TYPE,
    p_date		  in	  PHENOTYPES.DATE_%TYPE,
    p_reference	  in	  PHENOTYPES.REFERENCE%TYPE,
    p_comm		  in	  PHENOTYPES.COMM%TYPE,
	p_suid		  in	  PHENOTYPES.SUID%TYPE,
    p_id		  in	  PHENOTYPES.ID%TYPE,
    p_message	  in out  varchar2) ;

  procedure Update_Phenotype_Thr_Alias (
    p_vname		  in	  VARIABLES.NAME%TYPE,
    p_alias    	  in	  INDIVIDUALS.ALIAS%TYPE,
    p_value		  in	  PHENOTYPES.VALUE%TYPE,
    p_date		  in	  PHENOTYPES.DATE_%TYPE,
    p_reference	  in	  PHENOTYPES.REFERENCE%TYPE,
    p_comm		  in	  PHENOTYPES.COMM%TYPE,
	p_suid		  in	  PHENOTYPES.SUID%TYPE,
    p_id		  in	  PHENOTYPES.ID%TYPE,
    p_message	  in out  varchar2) ;

  procedure Delete_Phenotype (
    p_vid	    in	    PHENOTYPES.VID%TYPE,
    p_iid	    in		PHENOTYPES.IID%TYPE,
    p_message	in out  varchar2) ;

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
    p_message	 in out  varchar2) ;


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
    p_message	 in out  varchar2) ;

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
    p_message	 in out  varchar2) ;

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
    p_message	 in out  varchar2) ;

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
    p_message	 in out  varchar2) ;

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
    p_message		in out     varchar2) ;

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
    p_message		in out     varchar2) ;

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
    p_message		in out     varchar2) ;

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
    p_message		in out     varchar2) ;



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
    p_message	 in out  varchar2);


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
    p_message	 in out  varchar2);

  procedure Delete_Genotype (
    p_mid	   in	   GENOTYPES.MID%TYPE,
    p_iid	   in	   GENOTYPES.IID%TYPE,
    p_message  in out  varchar2)  ;

----------------------------------------

  procedure Create_Filter (
    p_pid	      in	    FILTERS.PID%TYPE,
    p_fid		  in out	FILTERS.FID%TYPE,
    p_name		  in		FILTERS.NAME%TYPE,
    p_expression  in		FILTERS.EXPRESSION%TYPE,
    p_comm		  in		FILTERS.COMM%TYPE,
    p_sid		  in		FILTERS.SID%TYPE,
    p_id		  in		FILTERS.ID%TYPE,
    p_message	  in out  	varchar2) ;


  procedure Update_Filter (
    p_fid	      in	  FILTERS.FID%TYPE,
    p_name		  in	  FILTERS.NAME%TYPE,
    p_expression  in	  FILTERS.EXPRESSION%TYPE,
    p_comm		  in	  FILTERS.COMM%TYPE,
    p_sid		  in	  FILTERS.SID%TYPE,
    p_id		  in	  FILTERS.ID%TYPE,
    p_message	  in out  varchar2) ;

  procedure Delete_Filter (
    p_fid	    in	    FILTERS.FID%TYPE,
    p_message	in out  varchar2)  ;

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
    p_message	IN OUT    varchar2) ;
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
    p_message	IN OUT    varchar2) ;
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
    p_message	IN OUT   varchar2) ;
----------------------------------------

  procedure Delete_Marker (
    p_mid	    in	    MARKERS.MID%TYPE,
    p_message	in out  varchar)  ;

----------------------------------------

  PROCEDURE Copy_Library_Marker (
    p_mid	    IN OUT	  MARKERS.MID%TYPE,
    p_lmid		IN		  L_MARKERS.LMID%TYPE,
    p_suid		IN		  MARKERS.SUID%TYPE,
	p_id		IN		  MARKERS.ID%TYPE,
    p_message	IN OUT    varchar2) ;
----------------------------------------
  PROCEDURE Create_Marker_Set (
    p_msid	   IN OUT	MARKER_SETS.MSID%TYPE,
    p_name	   IN		MARKER_SETS.NAME%TYPE,
    p_comm	   IN		MARKER_SETS.COMM%TYPE,
    p_suid	   IN		MARKER_SETS.SUID%TYPE,
    p_id	   IN 		MARKER_SETS.ID%TYPE,
    p_message  IN OUT   varchar2) ;
----------------------------------------
  PROCEDURE Update_Marker_Set (
    p_msid	   IN 	   MARKER_SETS.MSID%TYPE,
    p_name	   IN	   MARKER_SETS.NAME%TYPE,
    p_comm	   IN	   MARKER_SETS.COMM%TYPE,
    p_id	   IN 	   MARKER_SETS.ID%TYPE,
    p_message  IN OUT  varchar2) ;
----------------------------------------
  procedure Delete_Marker_Set (
    p_msid	    in	    MARKER_SETS.MSID%TYPE,
    p_message	in out  varchar2)  ;

----------------------------------------
  procedure Create_Marker_Set_Link (
    p_msid	    in	     MARKER_SETS.MSID%TYPE,
    p_mark_name	in		 MARKERS.NAME%TYPE,
    p_position	in		 POSITIONS.VALUE%TYPE,
    p_suid		in		 MARKERS.SUID%TYPE,
    p_id		in		 POSITIONS.ID%TYPE,
    p_message	in out   varchar2) ;

  procedure Create_Marker_Set_Link (
    p_msid	    in	     MARKER_SETS.MSID%TYPE,
    p_mid  		in		 MARKERS.MID%TYPE,
    p_position	in		 POSITIONS.VALUE%TYPE,
    p_id		in		 POSITIONS.ID%TYPE,
    p_message	in out   varchar2) ;

  procedure Update_Marker_Set_Link (
    p_msid	    in	     POSITIONS.MSID%TYPE,
    p_mid		in		 POSITIONS.MID%TYPE,
    p_position	in		 POSITIONS.VALUE%TYPE,
    p_id		in		 POSITIONS.ID%TYPE,
    p_message	in out   varchar2) ;

----------------------------------------
  procedure Delete_Marker_Set_Link (
    p_msid	   in	  POSITIONS.MSID%TYPE,
    p_mid	   in	  POSITIONS.MID%TYPE,
    p_message  in out varchar2) ;
----------------------------------------
  procedure Create_Allele (
    p_aid	    in out	ALLELES.AID%TYPE,
    p_name	    in		ALLELES.NAME%TYPE,
    p_comm		in		ALLELES.COMM%TYPE,
    p_mname		in		MARKERS.NAME%TYPE,
    p_suid		in		MARKERS.SUID%TYPE,
    p_id	    in		ALLELES.ID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Create_Allele (
    p_aid	    in out	ALLELES.AID%TYPE,
    p_name	    in		ALLELES.NAME%TYPE,
    p_comm		in		ALLELES.COMM%TYPE,
    p_mid		in		ALLELES.MID%TYPE,
    p_id	    in		ALLELES.ID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Update_Allele (
    p_aid	    in	    ALLELES.AID%TYPE,
    p_name		in		ALLELES.NAME%TYPE,
    p_comm		in		ALLELES.COMM%TYPE,
    p_id		in		ALLELES.ID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Delete_Allele (
    p_aid	   in	   ALLELES.AID%TYPE,
    p_message  in out  varchar2) ;
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
    p_message	IN OUT    varchar2) ;
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
    p_message	IN OUT    varchar2) ;
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
    p_message	IN OUT   varchar2) ;
----------------------------------------
  PROCEDURE Copy_Marker (
    p_lmid	    IN OUT	  L_MARKERS.LMID%TYPE,
    p_mid		IN		  MARKERS.MID%TYPE,
    p_message	IN OUT    varchar2) ;
----------------------------------------


  procedure Delete_L_Marker (
    p_lmid	    in	    L_MARKERS.LMID%TYPE,
    p_message	in out  varchar)  ;
----------------------------------------

  procedure Create_L_Allele (
    p_laid	    in out	L_ALLELES.LAID%TYPE,
    p_name	    in		L_ALLELES.NAME%TYPE,
    p_comm		in		L_ALLELES.COMM%TYPE,
    p_mname		in		L_MARKERS.NAME%TYPE,
    p_sid		in		L_MARKERS.SID%TYPE,
    p_message	in out  varchar2) ;

----------------------------------------
  procedure Create_L_Allele (
    p_laid	    in out	L_ALLELES.LAID%TYPE,
    p_name	    in		L_ALLELES.NAME%TYPE,
    p_comm		in		L_ALLELES.COMM%TYPE,
    p_lmid		in		L_ALLELES.LMID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Update_L_Allele (
    p_laid	    in	    L_ALLELES.LAID%TYPE,
    p_name		in		L_ALLELES.NAME%TYPE,
    p_comm		in		L_ALLELES.COMM%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Delete_L_Allele (
    p_laid	   in	   L_ALLELES.LAID%TYPE,
    p_message  in out  varchar2) ;
----------------------------------------

 PROCEDURE Create_Variable (
    p_vid	   IN OUT	VARIABLES.VID%TYPE,
    p_name	   IN		VARIABLES.NAME%TYPE,
    p_type	   IN		VARIABLES.TYPE%TYPE,
    p_unit	   IN		VARIABLES.UNIT%TYPE,
    p_comm	   IN		VARIABLES.COMM%TYPE,
    p_suid	   IN		VARIABLES.SUID%TYPE,
    p_id	   IN 		VARIABLES.ID%TYPE,
    p_message  IN OUT  	varchar2) ;
----------------------------------------
  PROCEDURE Update_Variable (
    p_vid	    IN 	    VARIABLES.VID%TYPE,
    p_name		IN		VARIABLES.NAME%TYPE,
    p_type		IN		VARIABLES.TYPE%TYPE,
    p_unit		IN		VARIABLES.UNIT%TYPE,
    p_comm		IN		VARIABLES.COMM%TYPE,
    p_id		IN 		VARIABLES.ID%TYPE,
    p_message	IN OUT  varchar2) ;

----------------------------------------
  procedure Delete_Variable (
    p_vid	  	in	    VARIABLES.VID%TYPE,
    p_message   in out  varchar2)  ;

----------------------------------------
  PROCEDURE Create_Variable_Set (
    p_vsid	    IN OUT	  VARIABLE_SETS.VSID%TYPE,
    p_name		IN		  VARIABLE_SETS.NAME%TYPE,
    p_comm		IN		  VARIABLE_SETS.COMM%TYPE,
    p_suid		IN		  VARIABLE_SETS.SUID%TYPE,
    p_id		IN 		  VARIABLE_SETS.ID%TYPE,
    p_message	IN OUT    varchar2) ;
----------------------------------------
  PROCEDURE Update_Variable_Set (
    p_vsid	    IN 	    VARIABLE_SETS.VSID%TYPE,
    p_name		IN		VARIABLE_SETS.NAME%TYPE,
    p_comm		IN		VARIABLE_SETS.COMM%TYPE,
    p_id		IN 		VARIABLE_SETS.ID%TYPE,
    p_message	IN OUT  varchar2) ;

----------------------------------------

  procedure Delete_Variable_Set (
    p_vsid	    in	    VARIABLE_SETS.VSID%TYPE,
    p_message	in out  varchar2)  ;
----------------------------------------

  procedure Create_Variable_Set_Link (
    p_vsid	    in	    R_VAR_SET.VSID%TYPE,
    p_var_name	in		VARIABLES.NAME%TYPE,
    p_suid		in		VARIABLES.SUID%TYPE,
    p_id		in		R_VAR_SET.ID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Create_Variable_Set_Link (
    p_vsid	    in	    R_VAR_SET.VSID%TYPE,
    p_vid		in		VARIABLES.VID%TYPE,
    p_id		in		R_VAR_SET.ID%TYPE,
    p_message	in out  varchar2) ;

----------------------------------------
  procedure Delete_Variable_Set_Link (
    p_vsid	    in	    R_VAR_SET.VSID%TYPE,
    p_vid		in		R_VAR_SET.VID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  PROCEDURE Create_Chromosome (
    p_cid	   IN OUT	CHROMOSOMES.CID%TYPE,
    p_name	   IN		CHROMOSOMES.NAME%TYPE,
    p_comm	   IN		CHROMOSOMES.COMM%TYPE,
    p_sid	   IN		CHROMOSOMES.SID%TYPE,
    p_message  IN OUT   varchar2) ;
----------------------------------------
  PROCEDURE Update_Chromosome (
    p_cid	   IN 	    CHROMOSOMES.CID%TYPE,
    p_name	   IN	  	CHROMOSOMES.NAME%TYPE,
    p_comm	   IN 	  	CHROMOSOMES.COMM%TYPE,
    p_message  IN OUT 	varchar2) ;
----------------------------------------
  procedure Delete_Chromosome (
    p_cid	    in	    CHROMOSOMES.CID%TYPE,
    p_message	in out  varchar2)  ;
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
    p_message	IN OUT    varchar2) ;
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
    p_message	IN OUT    varchar2) ;
----------------------------------------
  PROCEDURE Update_U_Marker (
    p_umid	    IN 	     U_MARKERS.UMID%TYPE,
    p_name		IN		 U_MARKERS.NAME%TYPE,
    p_alias		IN		 U_MARKERS.ALIAS%TYPE,
    p_comm		IN		 U_MARKERS.COMM%TYPE,
	p_position  IN		 U_MARKERS.POSITION%TYPE,
	p_cid		IN		 U_MARKERS.CID%TYPE,
    p_id		IN 		 U_MARKERS.ID%TYPE,
    p_message	IN OUT   varchar2) ;
----------------------------------------

  procedure Delete_U_Marker (
    p_umid	    in	    U_MARKERS.UMID%TYPE,
    p_message	in out  varchar)  ;
----------------------------------------

  PROCEDURE Create_U_Marker_Set (
    p_umsid	   IN OUT	U_MARKER_SETS.UMSID%TYPE,
    p_name	   IN		U_MARKER_SETS.NAME%TYPE,
    p_comm	   IN		U_MARKER_SETS.COMM%TYPE,
	p_pid	   IN		U_MARKER_SETS.PID%TYPE,
    p_sid	   IN		U_MARKER_SETS.SID%TYPE,
    p_id	   IN 		U_MARKER_SETS.ID%TYPE,
    p_message  IN OUT   varchar2) ;
----------------------------------------
  PROCEDURE Update_U_Marker_Set (
    p_umsid	   IN 	   U_MARKER_SETS.UMSID%TYPE,
    p_name	   IN	   U_MARKER_SETS.NAME%TYPE,
    p_comm	   IN	   U_MARKER_SETS.COMM%TYPE,
    p_id	   IN 	   U_MARKER_SETS.ID%TYPE,
    p_message  IN OUT  varchar2) ;
----------------------------------------

  procedure Delete_U_Marker_Set (
    p_umsid	    in	    U_MARKER_SETS.UMSID%TYPE,
    p_message	in out  varchar2)  ;
----------------------------------------

  procedure Create_U_Marker_Set_Link (
    p_umsid	        in	     U_MARKER_SETS.UMSID%TYPE,
    p_u_mark_name	in		 U_MARKERS.NAME%TYPE,
    p_u_position	in		 U_POSITIONS.VALUE%TYPE,
	p_pid			in		 U_MARKER_SETS.PID%TYPE,
    p_sid			in		 U_MARKERS.SID%TYPE,
    p_id			in		 U_POSITIONS.ID%TYPE,
    p_message	 	in out   varchar2) ;

  procedure Create_U_Marker_Set_Link (
    p_umsid	        in	     U_POSITIONS.UMSID%TYPE,
    p_umid			in		 U_POSITIONS.UMID%TYPE,
    p_u_position	in		 U_POSITIONS.VALUE%TYPE,
    p_id			in		 U_POSITIONS.ID%TYPE,
    p_message	 	in out   varchar2) ;


  procedure Update_U_Marker_Set_Link (
    p_umsid	    in	     U_POSITIONS.UMSID%TYPE,
    p_umid		in		 U_POSITIONS.UMID%TYPE,
    p_position	in		 U_POSITIONS.VALUE%TYPE,
    p_id		in		 U_POSITIONS.ID%TYPE,
    p_message	in out   varchar2) ;

----------------------------------------
  procedure Delete_U_Marker_Set_Link (
    p_umsid	   in	  U_POSITIONS.UMSID%TYPE,
    p_umid	   in	  U_POSITIONS.UMID%TYPE,
    p_message  in out varchar2) ;

----------------------------------------
  procedure Create_U_Allele (
    p_uaid	    in out	U_ALLELES.UAID%TYPE,
    p_name	    in		U_ALLELES.NAME%TYPE,
    p_comm		in		U_ALLELES.COMM%TYPE,
    p_umname	in		U_MARKERS.NAME%TYPE,
    p_sid		in		U_MARKERS.SID%TYPE,
    p_id	    in		U_ALLELES.ID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Create_U_Allele (
    p_uaid	    in out	U_ALLELES.UAID%TYPE,
    p_name	    in		U_ALLELES.NAME%TYPE,
    p_comm		in		U_ALLELES.COMM%TYPE,
    p_umid		in		U_MARKERS.UMID%TYPE,
    p_id	    in		U_ALLELES.ID%TYPE,
    p_message	in out  varchar2) ;

----------------------------------------
  procedure Update_U_Allele (
    p_uaid	    in	    U_ALLELES.UAID%TYPE,
    p_name		in		U_ALLELES.NAME%TYPE,
    p_comm		in		U_ALLELES.COMM%TYPE,
    p_id		in		U_ALLELES.ID%TYPE,
    p_message	in out  varchar2) ;

----------------------------------------
  procedure Delete_U_Allele (
    p_uaid	   in	   U_ALLELES.UAID%TYPE,
    p_message  in out  varchar2) ;

----------------------------------------
  PROCEDURE Create_U_Marker_Mapping (
	p_pid		IN		  R_UMID_MID.PID%TYPE,
	p_umid		IN		  R_UMID_MID.UMID%TYPE,
	p_mid		IN		  R_UMID_MID.MID%TYPE,
    p_message	IN OUT    varchar2) ;
----------------------------------------

  procedure Create_U_Marker_Mapping (
	p_umid 		 IN OUT		U_Markers.UMID%TYPE,
	p_mid		 IN OUT		Markers.MID%TYPE,
	p_pid 		 IN			R_UMID_MID.PID%TYPE,
	p_umname	 in			U_MARKERS.NAME%TYPE,
	p_suname	 in			Sampling_Units.NAME%TYPE,
	p_mname		 in			Markers.name%TYPE,
	p_message	 in out		varchar2) ;

----------------------------------------

  procedure Delete_U_Marker_Mapping (
    p_umid	    in	    R_UMID_MID.UMID%TYPE,
	p_mid		in		R_UMID_MID.MID%TYPE,
    p_message	in out  varchar)  ;
----------------------------------------
  PROCEDURE Create_U_Allele_Mapping (
	p_pid		IN		  R_UAID_AID.PID%TYPE,
	p_uaid		IN		  R_UAID_AID.UAID%TYPE,
	p_aid		IN		  R_UAID_AID.AID%TYPE,
    p_message	IN OUT    varchar2) ;
----------------------------------------
  procedure Create_U_Allele_Mapping (
	p_pid 		  IN		R_UAID_AID.PID%TYPE,
	p_mid		  IN		ALLELES.MID%TYPE,
	p_aname		  IN		ALLELES.NAME%TYPE,
	p_umid		  IN		U_ALLELES.UMID%TYPE,
	p_uaname	  IN		U_ALLELES.NAME%TYPE,
	p_message	 in out		varchar2) ;

----------------------------------------

  procedure Delete_U_Allele_Mapping (
  	p_pid       in		R_UAID_AID.PID%TYPE,
	p_aid		in		R_UAID_AID.AID%TYPE,
    p_message	in out  varchar)  ;
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
    p_message  IN OUT  	varchar2) ;

----------------------------------------
  PROCEDURE Update_U_Variable (
    p_uvid	    IN 	    U_VARIABLES.UVID%TYPE,
    p_name		IN		U_VARIABLES.NAME%TYPE,
    p_type		IN		U_VARIABLES.TYPE%TYPE,
    p_unit		IN		U_VARIABLES.UNIT%TYPE,
    p_comm		IN		U_VARIABLES.COMM%TYPE,
    p_id		IN 		U_VARIABLES.ID%TYPE,
    p_message	IN OUT  varchar2) ;

----------------------------------------

  procedure Delete_U_Variable (
    p_uvid	  	in	    U_VARIABLES.UVID%TYPE,
    p_message   in out  varchar2)  ;
----------------------------------------


PROCEDURE Create_U_Variable_Mapping (
	p_pid		IN		  R_UVID_VID.PID%TYPE,
	p_suid		IN		  R_UVID_VID.SUID%TYPE,
	p_uvid		IN		  R_UVID_VID.UVID%TYPE,
	p_vid		IN		  R_UVID_VID.VID%TYPE,
	p_message	IN OUT    varchar2);


-----------------------------------------
PROCEDURE Create_U_Variable_Mapping (
	p_pid		IN		  R_UVID_VID.PID%TYPE,
	p_suname	IN		  SAMPLING_UNITS.NAME%TYPE,
	p_uvname	IN		  U_VARIABLES.NAME%TYPE,
	p_vname		IN		  VARIABLES.NAME%TYPE,
	p_message	IN OUT    varchar2);


----------------------------------------
 procedure Delete_U_Variable_Mapping (
    p_suid 		in      R_UVID_VID.SUID%TYPE,
	p_pid		in      R_UVID_VID.PID%TYPE,
	p_uvid	    in	    R_UVID_VID.UVID%TYPE,
	p_vid		in		R_UVID_VID.VID%TYPE,
    p_message	in out  varchar);

-----------------------------------------

  PROCEDURE Create_U_Variable_Set (
    p_uvsid	    IN OUT	  U_VARIABLE_SETS.UVSID%TYPE,
    p_name		IN		  U_VARIABLE_SETS.NAME%TYPE,
    p_comm		IN		  U_VARIABLE_SETS.COMM%TYPE,
	p_pid		IN		  U_VARIABLE_SETS.PID%TYPE,
    p_sid		IN		  U_VARIABLE_SETS.SID%TYPE,
    p_id		IN 		  U_VARIABLE_SETS.ID%TYPE,
    p_message	IN OUT    varchar2) ;
----------------------------------------
  PROCEDURE Update_U_Variable_Set (
    p_uvsid	    IN 	    U_VARIABLE_SETS.UVSID%TYPE,
    p_name		IN		U_VARIABLE_SETS.NAME%TYPE,
    p_comm		IN		U_VARIABLE_SETS.COMM%TYPE,
    p_id		IN 		U_VARIABLE_SETS.ID%TYPE,
    p_message	IN OUT  varchar2) ;

----------------------------------------

  procedure Delete_U_Variable_Set (
    p_uvsid	    in	    U_VARIABLE_SETS.UVSID%TYPE,
    p_message	in out  varchar2)  ;
----------------------------------------

  procedure Create_U_Variable_Set_Link (
    p_uvsid	        in	    R_U_VAR_SET.UVSID%TYPE,
    p_u_var_name	in		U_VARIABLES.NAME%TYPE,
	p_pid			in		U_VARIABLES.PID%TYPE,
    p_sid			in		U_VARIABLES.SID%TYPE,
    p_id			in		R_U_VAR_SET.ID%TYPE,
    p_message		in out  varchar2) ;

  procedure Create_U_Variable_Set_Link (
    p_uvsid	        in	    R_U_VAR_SET.UVSID%TYPE,
    p_uvid			in		R_U_VAR_SET.UVID%TYPE,
	p_pid			in		R_U_VAR_SET.PID%TYPE,
    p_id			in		R_U_VAR_SET.ID%TYPE,
    p_message		in out  varchar2) ;

----------------------------------------
  procedure Delete_U_Variable_Set_Link (
    p_uvsid	    in	    R_U_VAR_SET.UVSID%TYPE,
    p_uvid		in		R_U_VAR_SET.UVID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------

  procedure Create_User (
    p_id	  in out	USERS.ID%TYPE,
    p_usr	  in		USERS.USR%TYPE,
	p_pwd	  in		USERS.PWD%TYPE,
    p_name	  in		USERS.NAME%TYPE,
	p_status  in		USERS.STATUS%TYPE,
    p_message in out  	varchar2) ;

----------------------------------------
  procedure Update_User (
    p_id	  in 	  USERS.ID%TYPE,
    p_usr	  in	  USERS.USR%TYPE,
	p_pwd	  in	  USERS.PWD%TYPE,
    p_name	  in	  USERS.NAME%TYPE,
	p_status  in	  USERS.STATUS%TYPE,
    p_message in out  varchar2) ;

----------------------------------------
  procedure Delete_User (
    p_id	  in 	  USERS.ID%TYPE,
    p_message in out  varchar2) ;
----------------------------------------
  procedure Create_Role (
    p_rid	  in out	ROLES_.RID%TYPE,
	p_pid	  in 		ROLES_.PID%TYPE,
    p_name	  in		ROLES_.NAME%TYPE,
	p_comm	  in		ROLES_.COMM%TYPE,
    p_message in out  	varchar2) ;
----------------------------------------
  procedure Update_Role (
    p_rid	  in 	  ROLES_.RID%TYPE,
    p_name	  in	  ROLES_.NAME%TYPE,
	p_comm	  in	  ROLES_.COMM%TYPE,
    p_message in out  varchar2) ;

----------------------------------------
  procedure Delete_Role (
    p_rid	  in 	  ROLES_.RID%TYPE,
    p_message in out  varchar2) ;
----------------------------------------
  procedure Add_Privilege (
    p_rid	  in 	  R_ROL_PRI.RID%TYPE,
	p_prid	  in	  R_ROL_PRI.PRID%TYPE,
    p_message in out  varchar2) ;
----------------------------------------
  procedure Add_Privilege (
    p_rid	  in 	  R_ROL_PRI.RID%TYPE,
	p_pname	  in	  PRIVILEGES_.NAME%TYPE,
    p_message in out  varchar2) ;

----------------------------------------
  procedure Remove_Privilege (
    p_rid	  in 	  R_ROL_PRI.RID%TYPE,
	p_prid	  in	  R_ROL_PRI.PRID%TYPE,
    p_message in out  varchar2) ;
----------------------------------------
  procedure Remove_All_Privileges (
    p_rid	  in 	  R_ROL_PRI.RID%TYPE,
    p_message in out  varchar2) ;
----------------------------------------
  procedure Create_Species_Link (
    p_pid	    in	    R_PRJ_SPC.PID%TYPE,
    p_sid		in		R_PRJ_SPC.SID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Delete_Species_Link (
    p_pid	    in	    R_PRJ_SPC.PID%TYPE,
    p_sid		in		R_PRJ_SPC.SID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Create_User_Link (
    p_pid	    in	    R_PRJ_ROL.PID%TYPE,
    p_rid	    in	    R_PRJ_ROL.RID%TYPE,
    p_id		in		R_PRJ_ROL.ID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------

  procedure Update_User_Link (
    p_pid	    in	    R_PRJ_ROL.PID%TYPE,
    p_id		in		R_PRJ_ROL.ID%TYPE,
	p_rid		in		R_PRJ_ROL.RID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Delete_User_Link (
    p_pid	    in	    R_PRJ_ROL.PID%TYPE,
    p_id		in		R_PRJ_ROL.ID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Create_SU_Link (
    p_pid	    in	    R_PRJ_SU.PID%TYPE,
    p_suid		in		R_PRJ_SU.SUID%TYPE,
    p_message	in out  varchar2) ;

----------------------------------------
  procedure Delete_SU_Link (
    p_pid	    in	    R_PRJ_SU.PID%TYPE,
    p_suid		in		R_PRJ_SU.SUID%TYPE,
    p_message	in out  varchar2) ;

----------------------------------------
  procedure Create_Project (
    p_pid	    in out  PROJECTS.PID%TYPE,
	p_name		in		PROJECTS.NAME%TYPE,
	p_comm		in		PROJECTS.COMM%TYPE,
	p_status	in		PROJECTS.STATUS%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Update_Project (
    p_pid	    in	    PROJECTS.PID%TYPE,
	p_name		in		PROJECTS.NAME%TYPE,
	p_comm		in		PROJECTS.COMM%TYPE,
	p_status	in		PROJECTS.STATUS%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Delete_Project (
    p_pid	    in	    PROJECTS.PID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Create_Species (
    p_sid	    in out  SPECIES.SID%TYPE,
	p_name		in		SPECIES.NAME%TYPE,
	p_comm		in		SPECIES.COMM%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Update_Species (
    p_sid	    in	    SPECIES.SID%TYPE,
	p_name		in		SPECIES.NAME%TYPE,
	p_comm		in		SPECIES.COMM%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
  procedure Delete_Species (
    p_sid	    in	    SPECIES.SID%TYPE,
    p_message	in out  varchar2) ;
----------------------------------------
----------------------------------------
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
    p_message	in out  varchar2) ;

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
    p_message	in out  varchar2) ;

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
    p_message	in out  varchar2);

  procedure Delete_Sample (
    p_said	in	int,
    p_message	in out  varchar2) ;

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
	p_message in out  varchar2) ;

  procedure Update_File_Generation(
  	p_fgid    in 	  FILE_GENERATIONS.FGID%TYPE,
	p_name	  in 	  FILE_GENERATIONS.NAME%TYPE,
	p_comm	  in	  FILE_GENERATIONS.COMM%TYPE,
	p_id	  in	  FILE_GENERATIONS.ID%TYPE,
	p_message in out  varchar2) ;

  procedure Abort_File_Generation(
  	p_fgid    in      FILE_GENERATIONS.FGID%TYPE,
	p_message in out  varchar2) ;

  procedure Delete_File_Generation(
  	p_fgid    in      FILE_GENERATIONS.FGID%TYPE,
	p_message in out  varchar2) ;

----------------------------------------
  procedure Create_FG_FLT_Link(
  	p_fgid    in      R_FG_FLT.FGID%TYPE,
	p_suid	  in 	  R_FG_FLT.SUID%TYPE,
	p_fid	  in	  R_FG_FLT.FID%TYPE,
	p_gsid	  in	  R_FG_FLT.GSID%TYPE,
	p_message in out  varchar2) ;
----------------------------------------

  procedure Create_Data_File(
    p_dfid    in out    DATA_FILES.DFID%TYPE,
	p_fgid	  in 		DATA_FILES.FGID%TYPE,
	p_name	  in		DATA_FILES.NAME%TYPE,
	p_status  in		DATA_FILES.STATUS%TYPE,
	p_comm	  in		DATA_FILES.COMM%TYPE,
	p_id	  in		DATA_FILES.ID%TYPE,
	p_message in out	varchar2) ;

  procedure Set_Data_File_Status(
	p_dfid    in   		DATA_FILES.DFID%TYPE,
	p_status  in		DATA_FILES.STATUS%TYPE,
	p_message in out	varchar2) ;


----------------------------------------------

end GDBP;
/