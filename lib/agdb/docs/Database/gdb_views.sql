----------------------------------------------------------------
--
-- $Log$
-- Revision 1.3  2005/01/31 12:41:17  heto
-- Adapted schema and views for postgresql. Original files still oracle dependent
--
-- Revision 1.2  2003/04/25 09:19:26  heto
-- View changed
--
-- Revision 1.1  2002/11/13 08:49:51  heto
-- Added the database scripts to the new CVS
--
-- Revision 1.5  2002/02/01 15:46:56  roca
-- Added V_Genotypes_6 for inheritance check
--
-- Revision 1.4  2001/05/21 08:00:33  frob
-- Added the status field to view V_Individuals_Log.
--
-- Revision 1.3  2001/05/09 14:29:11  frob
-- Added missing semicolon in V_R_UVID_VID_1.
--
-- Revision 1.2  2001/05/02 09:02:53  frob
-- Added missing samples views.
--
--
-- This script creates all DB views
--
-- This script must be run as GdbAdm
--
--  2000-10-09	TOBJ	First version
--  2000-11-13	TOBJ	Adopted views to the new version of 
--						objects (comments are no longer stored 
--						in separate comment tables for the 
--						indeviduals, phenotypes and genotypes 
--						objects) 
--	2000-11-23	TOBJ	Added views for enabled and disabled
--						individuals and sampling units 
--  2001-01-09	TOBJ	Modified views for File generations
--						(Previously known as analyses)
--	2001-01-10	TOBJ	Modified V_MARKERS_LOG to include 
--						chromosome name
--	2001-02-09	TOBJ	Modified the views for library markers
--						to include primers
--  2001-02-12  TOBJ	Modified the view v_sampling_units_inds
--						to only count enabled individuals.
--  2001-02-13	TOBJ	Modified all views that depend on the 
--						status of individuals or sampling units
--						to only return rows for enabled objects 
-- 
--	
----------------------------------------------------------------

----------------------------------------------------------------
-- Users -------------------------------------------------------
----------------------------------------------------------------

create or replace view V_Users_1 as
	   select u.id, u.usr, u.pwd, u.name, u.status
	   from Users  u ;

create or replace view V_USERS_2 (PID, PNAME, RID, RNAME, USR, ID, NAME, STATUS) as
	select p.pid, p.name, rol.rid, rol.name, u.usr, u.id, u.name, u.status 
	from Projects p, Roles_ rol, R_prj_rol r1, users u
	where 
	  p.pid = r1.pid and
	  u.id = r1.id and
	  r1.rid = rol.rid;
	
create or replace view V_Enabled_Users_1 as
	   select u.id, u.usr, u.pwd, u.name 
	   from Users  u 
	   where status = 'E' ;

create or replace view V_Enabled_Users_2 as
	   select u.id, u.usr, u.pwd, u.name as uname,
	   		  ro.rid, ro.name as rname,
			  pr.pid, pr.name as pname 
	   from Users u, Roles_ ro, Projects pr, R_Prj_Rol r
	   where u.status = 'E' and
	   		 u.id = r.id and
			 r.pid = pr.pid and
			 r.rid = ro.rid and
			 ro.pid = pr.pid and
			 pr.status = 'E';
			 
create or replace view V_USER_PRIV (PID, ID, RNAME, PRID, PNAME) as
	select r1.pid, r1.id, rol.name, pri.prid, pri.name 
	from r_prj_rol r1, roles_ rol, 
		 privileges_ pri, r_rol_pri r2
	where
		 r1.rid = rol.rid and
		 rol.rid = r2.rid and
		 r2.prid = pri.prid;		

CREATE OR REPLACE VIEW V_ROLE_PRIV_1 ( 
  RID, PRID, NAME, COMM, INCL) AS
  select distinct ro.rid, p.prid, p.name, p.comm,
    decode( (SELECT count(prid) from r_rol_pri where rid=r.rid and prid=p.prid), 0, 0, 1)
  from Roles_ ro, r_rol_pri r, privileges_ p
  where 
	r.rid(+) = ro.rid
  order by prid;
	 
			 
----------------------------------------------------------------
-- Projects ----------------------------------------------------
----------------------------------------------------------------

create or replace view V_Projects_1 as
	   select p.pid, p.name, p.comm, p.status
	   from Projects p;
	   
create or replace view V_PROJECTS_2 (SID, SNAME, PID, NAME, 
	   	  		  	   COMM, STATUS) AS
		select distinct s.sid, s.name, p.pid, p.name,
			   			p.comm, p.status 
		from projects p, species s, r_prj_spc r
		where
			 s.sid (+)= r.sid  and
			 r.pid (+)= p.pid;

CREATE OR REPLACE VIEW V_PROJECTS_3 (
  SID, SUID, ID,  PID, NAME, COMM, STATUS ) AS
  select r1.sid, r2.suid, r3.id,
  		 p.pid, p.name,	p.comm, p.status
  from
	r_prj_spc r1, r_prj_su r2,
	r_prj_rol r3, projects p
  where
    r1.pid (+) = p.pid and
	r2.pid (+) = p.pid and
	r3.pid (+) = p.pid ;
	
create or replace view V_Enabled_Projects as
	   select p.pid, p.name, p.comm
	   from Projects p where status = 'E';

create or replace view V_ROLES_1 (RID, NAME, COMM, PID) as
	select ro.rid, ro.name, ro.comm, ro.pid 
	from Roles_ ro;
			 
create or replace view V_PRIVILEGES_1 (PRID, NAME, COMM) as
	select p.prid, p.name, p.comm
	from privileges_ p;
	
create or replace view V_PRIVILEGES_2 (RID, PRID, NAME, COMM) as
	select r.rid, p.prid, p.name, p.comm
	from r_rol_pri r, privileges_ p
	where r.prid = p.prid;
	
create or replace view V_PRIVILEGES_3 (RID, RNAME, PRID, NAME, COMM) as
	select r.rid, ro.name, p.prid, p.name, p.comm
	from Roles_ ro, r_rol_pri r, privileges_ p
	where
	  ro.rid = r.rid and 
	  r.prid = p.prid;	

----------------------------------------------------------------
-- Species -----------------------------------------------------
----------------------------------------------------------------

create or replace view V_Species_1 as
	   select s.sid, s.name, s.comm
	   from Species s;

create or replace view V_Species_2 as
  select r.pid,
         s.sid, s.name, s.comm
  from R_Prj_Spc r, Species s
  where s.sid = r.sid;

----------------------------------------------------------------
-- Sampling Units ----------------------------------------------
----------------------------------------------------------------
create or replace view V_Sampling_Units_1 as
	   select s.SUID, s.NAME, s.COMM, s.SID, s.status, s.ID, s.TS
	   from Sampling_Units s ;
	   		
	   
create or replace view V_Enabled_Sampling_Units_1 (
	   SUID, NAME, COMM, SID, USR, TS) as
 select s.SUID, s.NAME, s.COMM, s.SID, u.usr, s.TS
 from Sampling_Units s, users u 
 where
 	  s.status = 'E' and
	  u.id = s.id;

create or replace view V_Enabled_Sampling_Units_2 (
	   PID, SID, SUID, NAME) as
 select r.pid, s.SID, s.SUID, s.NAME
 from Sampling_Units s, r_prj_su r
 where
 	  s.status = 'E' and
	  r.suid = s.suid ;

create or replace view V_Sampling_Units_2 (
	   PID, SID, SNAME, SUID, NAME, COMM, STATUS, USR, TS) as
 select r.pid, s.sid, sp.name, s.SUID, s.NAME, 
 		s.COMM, s.status, u.usr, s.TS
 from Sampling_Units s, users u, species sp, r_prj_su r
 where
	  u.id = s.id and
	  r.suid = s.suid and
	  sp.sid = s.sid;

create or replace view V_Sampling_Units_Inds as
  select e.suid, count(iid) as inds
  from Individuals i, expobj e
  where e.eid=i.iid and status='E'
  group by e.suid;

create or replace view V_Sampling_Units_3 (
	   PID, SID, SNAME, SUID, NAME, COMM, STATUS, INDS, USR, TS) as
  select r.pid, su.sid, s.name,  su.suid, su.name, su.comm, 
	  	 su.status, sui.inds, u.usr, su.ts
  from R_Prj_SU r, Sampling_Units su, Species s, 
  	   Users u, v_sampling_units_inds sui
  where
  	   r.suid = su.suid and
	   u.id = su.id and
	   s.sid = su.sid and
	   sui.suid (+) = su.suid;
			 
create or replace view V_SAMPLING_UNITS_LOG (
 	  SUID, NAME, COMM, STATUS, USR, TS)  as
  select sul.suid, sul.name, sul.comm,
         sul.status, u.usr, sul.ts
  from Sampling_Units_Log sul,
       Users u
  where sul.id = u.id;


----------------------------------------------------------------
-- Samples -----------------------------------------------------
----------------------------------------------------------------

CREATE OR REPLACE VIEW V_SAMPLES_1 ( 
       SAID, NAME, TISSUE_TYPE, EXPERIMENTER, DATE_, TREATMENT, STORAGE, COMM, IID, ID, TS ) AS 
select said, name, tissue_type, experimenter, date_, treatment, storage, comm, iid, id, ts 
from samples;


CREATE OR REPLACE VIEW V_SAMPLES_2 ( 
       SAID, NAME, SUID, IID, IDENTITY, TISSUE_TYPE, STORAGE, USR, TS ) AS 
select s.said, s.name, i.suid, s.iid, i.identity, s.tissue_type, s.storage, u.usr, s.ts 
from samples s, individuals i, users u 
where s.iid = i.iid and s.id = u.id;


CREATE OR REPLACE VIEW V_SAMPLES_3 ( 
       SAID, NAME, SUID, IID, IDENTITY, TISSUE_TYPE, STORAGE, USR, TS, EXPERIMENTER, DATE_, COMM, TREATMENT ) AS 
select s.said, s.name, i.suid, s.iid, i.identity, s.tissue_type, s.storage, u.usr, s.ts,  s.experimenter, s.date_, s.comm, s.treatment 
from samples s, individuals i, users u  
where s.iid = i.iid and s.id = u.id;


CREATE OR REPLACE VIEW V_SAMPLES_LOG ( 
       SAID, NAME, TISSUE_TYPE, STORAGE, USR, TS, EXPERIMENTER, DATE_, COMM, TREATMENT ) AS 
select s.said, s.name, s.tissue_type, s.storage, u.usr, s.ts,  s.experimenter, s.date_, s.comm, s.treatment 
from samples_log s,  users u  
where  s.id = u.id;


----------------------------------------------------------------
-- Individuals -------------------------------------------------
----------------------------------------------------------------

create or replace view V_Individuals_1 as
	   select i.iid, i.identity, i.alias, i.father, i.mother,
	   		  i.sex, i.birth_date, i.comm, i.status, i.suid, i.id, i.ts
			  from Individuals i;
			  
create or replace view V_Individuals_2 as
  select i.suid, i.iid, i.identity, i.alias, i.sex, i.birth_date, i.comm,
         decode(i.father, null, null, decode(f.status, 'E', f.identity, '(' || f.identity || ')')) fidentity,
		 decode(i.mother, null, null, decode(m.status, 'E', m.identity, '(' || m.identity || ')')) midentity, 
		 i.status, u.usr, i.ts
  from Individuals i, Individuals f, 
	   Individuals m, Users u
  where 
		i.father = f.iid (+) and 
		i.mother = m.iid (+) and 
		i.id = u.id;

create or replace view V_Individuals_3 as
  select s.name suname, i.suid, i.iid, i.identity, i.alias, i.sex, i.birth_date, i.comm,
         decode(i.father, null, null, decode(f.status, 'E', f.identity, '(' || f.identity || ')')) fidentity,
		 decode(i.mother, null, null, decode(m.status, 'E', m.identity, '(' || m.identity || ')')) midentity, 
         i.status, u.usr, i.ts
  from Individuals i, Individuals f, 
	   Individuals m, Users u, sampling_units s
  where 
  		s.suid = i.suid and
		i.father = f.iid (+) and 
		i.mother = m.iid (+) and 
		i.id = u.id;

create or replace view V_Enabled_Individuals_1 (
	   SUID, IID, IDENTITY, ALIAS, FATHER, MOTHER, 
	   SEX, BIRTH_DATE, COMM, ID, TS) as
  select i.suid, i.iid, i.identity, i.alias, i.father, i.mother, 
  		 i.sex, i.birth_date, i.comm, i.id, i.ts
  from Individuals i
  where 
  		i.status = 'E' ;
		
create or replace view V_Enabled_Individuals_2 as
  select r.pid,
         su.suid, su.name suname,
         i.iid, i.identity, i.alias, i.sex, i.birth_date, i.comm,
         f.identity fidentity, m.identity midentity,
         u.usr, i.ts
  from R_Prj_SU r, Sampling_Units su,
       Individuals i, Individuals f, 
	   Individuals m, Users u
  where 
  		i.status = 'E' and
  		i.suid = r.suid and 
		i.suid = su.suid and 
		i.father = f.iid (+) and 
		i.mother = m.iid (+) and 
		i.id = u.id;

create or replace view V_Individuals_Log as
  select l.iid, l.identity, l.alias, l.sex, l.birth_date, l.comm,
         f.identity fidentity, m.identity midentity,
         u.usr, l.ts, l.status
  from Individuals_Log l, Individuals f, Individuals m,
       Users u
  where l.father = f.iid (+)
    and l.mother = m.iid (+)
    and l.id = u.id;

----------------------------------------------------------------
-- Groupings ---------------------------------------------------
----------------------------------------------------------------
create or replace view V_Groupings_1 as
	   select g.gsid, g.name, g.comm, g.suid, g.id, g.ts
	   from Groupings g, v_enabled_sampling_units_1 v_su
	   where g.suid = v_su.suid;
	   
create or replace view V_Groupings_Grps as
  select gsid, count(gid) grps
  from Groups group by gsid;

create or replace view V_Groupings_2 as
  select r.pid,
         v_su.suid, v_su.name suname,
         gs.gsid, gs.name name, gs.comm,
         gg.grps,
         u.usr, gs.ts
  from R_Prj_SU r, v_enabled_sampling_units_1 v_su, Groupings gs,
       V_Groupings_Grps gg,
       Users u
  where gs.suid = r.suid
    and gs.suid = v_su.suid
    and gs.gsid = gg.gsid (+)
    and gs.id = u.id;

create or replace view V_Groupings_Log as
  select gl.gsid, gl.name, gl.comm,
         u.usr, gl.ts
  from Groupings_Log gl,
       Users u
  where gl.id = u.id;

----------------------------------------------------------------
-- Groups ------------------------------------------------------
----------------------------------------------------------------
create or replace view V_Groups_Inds as
  select r.gid, count(r.gid) inds
  from R_Ind_Grp r, v_enabled_individuals_1 v_i 
  where r.iid = v_i.iid
  group by gid;

create or replace view V_Groups_1 as
  select g.gid, g.name, g.comm, g.gsid, g.id, g.ts
  from Groups g, Groupings gs, v_enabled_sampling_units_1 v_su
  where
  	   gs.gsid = g.gsid and
	   gs.suid = v_su.suid;

create or replace view V_Groups_2 as
  select r.pid,
         v_su.suid, v_su.name suname,
         gs.gsid, gs.name gsname,
         g.gid, g.name, g.comm,
         u.usr, g.ts
  from R_Prj_SU r, v_enabled_sampling_units_1 v_su, Groupings gs, 
  	   Groups g, Users u
  where gs.suid = r.suid
    and gs.suid = v_su.suid
    and g.gsid = gs.gsid
    and g.id = u.id;
	
create or replace view V_Groups_3 as
  select r.pid,
         v_su.suid, v_su.name suname,
         gs.gsid, gs.name gsname,
         g.gid, g.name, g.comm,
         gi.inds,
         u.usr, g.ts
  from R_Prj_SU r, v_enabled_sampling_units_1 v_su, Groupings gs, Groups g,
       V_Groups_Inds gi,
       Users u
  where gs.suid = r.suid
    and gs.suid = v_su.suid
    and g.gsid = gs.gsid
    and g.gid = gi.gid (+)
    and g.id = u.id;

create or replace view V_Groups_Log as
  select gl.gid, gl.name, gl.comm,
         u.usr, gl.ts
  from Groups_Log gl,
       Users u
  where gl.id = u.id;
  
create or replace view V_R_Ind_Grp_1 (IID, GID, ID, TS) as
  select r.iid, r.gid, r.id, r.ts 
  from r_ind_grp r, v_enabled_individuals_1 v_i
  where v_i.iid = r.iid;

create or replace view V_R_Ind_Grp_2 (IID, GSID, GID, GNAME, ID, TS) as
  select r.iid, g.gsid, r.gid, g.name, r.id, r.ts 
  from r_ind_grp r, Groups g, v_enabled_individuals_1 v_i
  where g.gid = r.gid and
  		r.iid = v_i.iid;

----------------------------------------------------------------
-- Variables ---------------------------------------------------
----------------------------------------------------------------
create or replace view V_VARIABLES_1 (
	   SUID, VID, NAME, TYPE, UNIT, COMM, ID, TS) as
  select v.suid, v.vid, v.name, v.type, v.unit, 
	   	 v.comm, v.id, v.ts 
  from Variables v, v_enabled_sampling_units_1 v_su
  where v_su.suid = v.suid;

create or replace view V_VARIABLES_2 (
	   SUID, VID, NAME, TYPE, UNIT, COMM, USR, TS) as
  select v.suid, v.vid, v.name, v.type, v.unit, 
	   	 v.comm, u.usr, v.ts 
  from Variables v, users u, v_enabled_sampling_units_1 v_su
  where
  	   u.id = v.id and
	   v.suid = v_su.suid;

create or replace view V_VARIABLES_3 (
	   PID, SID, SNAME, SUID, SUNAME, VID,
	   NAME, TYPE, UNIT, COMM, USR, TS) as
  select r.pid, v_su.sid, s.name, v_su.suid, v_su.name, 
  		 v.vid, v.name, v.type, v.unit, v.comm, 
		 u.usr, v.ts
  from R_Prj_Su r, v_enabled_sampling_units_1 v_su, Species s, 
  	   Variables v, Users u
  where 
  		r.suid = v.suid and
  		v.suid = v_su.suid and
		s.sid = v_su.sid and
    	v.id = u.id;
		
create or replace view V_VARIABLES_LOG (
	   VID, NAME, TYPE, UNIT, COMM, USR, TS) as
  select vl.vid, vl.name, vl.type, vl.unit, vl.comm,
         u.usr, vl.ts
  from Variables_Log vl, Users u
  where vl.id = u.id;

----------------------------------------------------------------
-- Variable Sets -----------------------------------------------
----------------------------------------------------------------
create or replace view V_VARIABLE_SETS_1 (
	   SUID, VSID, NAME, COMM, ID, TS) as
  select vs.suid, vs.vsid, vs.name, vs.comm, 
	   	 vs.id, vs.ts 
  from Variable_sets vs, v_enabled_sampling_units_1 v_su
  where vs.suid = v_su.suid;

create or replace view V_VARIABLE_SETS_2 (
	   SUID, VSID, NAME, COMM, USR, TS) as
  select vs.suid, vs.vsid, vs.name, vs.comm, 
	   	 u.usr, vs.ts 
  from Variable_sets vs, users u, v_enabled_sampling_units_1 v_su
  where 
  	   u.id = vs.id and
	   vs.suid = v_su.suid;
		
create or replace view V_Variable_Sets_3 as
  select r.pid, v_su.suid, v_su.name suname,
         vs.vsid, vs.name, vs.comm,
         u.usr, vs.ts
  from R_Prj_Su r, v_enabled_sampling_units_1 v_su, 
  	   Variable_Sets vs, Users u
  where r.suid = vs.suid and
  		vs.suid = v_su.suid and
		vs.id = u.id;

create or replace view V_Variable_Sets_4 as
  select r.pid, v_su.suid, v_su.name suname,
  		 v_su.sid, s.name sname, 
         vs.vsid, vs.name, vs.comm,
         u.usr, vs.ts
  from R_Prj_Su r, v_enabled_sampling_units_1 v_su, 
  	   Variable_Sets vs, Users u, Species s
  where r.suid = vs.suid and
  		vs.suid = v_su.suid and
  		s.sid = v_su.sid and
    	vs.id = u.id;

create or replace view V_Variable_Sets_Log as
  select vsl.vsid, vsl.name, vsl.comm,
         u.usr, vsl.ts
  from Variable_Sets_Log vsl,
       Users u
  where vsl.id = u.id;
  
create or replace view V_R_VAR_SET_1 (VSID, VID, ID, TS) AS
	 select r.vsid, r.vid, r.id, r.ts 
	 from R_VAR_SET r, variable_sets vs, v_enabled_sampling_units_1 v_su
	 where
	     r.vsid = vs.vsid and
		 vs.suid = v_su.suid ;
		 	  
create or replace view V_R_VAR_SET_2 (VSID, VSNAME, VID, VNAME, ID, TS) AS
	 select vs.vsid, vs.name, v.vid, v.name, r.id, r.ts 
	 from Variable_sets vs, Variables v, R_VAR_SET r, v_enabled_sampling_units_1 v_su
	 where vs.vsid = r.vsid and
	 	   v.vid = r.vid and
		   vs.suid = v_su.suid;

----------------------------------------------------------------
-- Phenotypes --------------------------------------------------
----------------------------------------------------------------
create or replace view V_Phenotypes_1 as
	   select p.vid, p.iid, p.suid, p.value,
	   		  p.date_, p.reference, p.id, p.ts 
		from phenotypes p, v_enabled_individuals_1 v_i,
			 v_enabled_sampling_units_1 v_su
		where
			 p.suid = v_su.suid and
			 p.iid = v_i.iid ;

create or replace view V_Phenotypes_2 as
  select r.pid,
         v_su.name suname,
         v.vid, v.name, v.type, v.unit,
         v_i.iid, v_i.identity,
         pt.value, pt.date_, pt.reference, pt.suid,
		 pt.comm, u.usr, pt.ts
  from R_Prj_Su r, Variables v,
       v_enabled_sampling_units_1 v_su, 
	   v_enabled_individuals_1 v_i,
       Phenotypes pt, Users u
  where r.suid = v_su.suid and
    	v.vid = pt.vid and
		v_su.suid = pt.suid and
		v_i.iid = pt.iid and
		u.id = pt.id ; 
  
  

create or replace view V_Phenotypes_3 as
  select r.pid,
         s.name sname,
         v.vid, v.name, v.type, v.unit,
         v_su.name suname, v_su.sid,
         v_i.iid, v_i.identity,
         pt.value, pt.date_, pt.reference, pt.comm,
         u.usr, pt.ts, pt.suid
  from Species s, Variables v,
       R_Prj_SU r, v_enabled_sampling_units_1 v_su, 
	   v_enabled_individuals_1 v_i,
       Phenotypes pt, Users u
  where
  	   s.sid = v_su.sid and
	   v.vid = pt.vid and
	   r.suid = pt.suid and
	   v_su.suid = pt.suid and
	   v_i.iid = pt.iid and
	   u.id = pt.id;
  
create or replace view V_Phenotypes_Log as
  select ptl.vid, ptl.iid, ptl.value, ptl.date_, ptl.reference,
         ptl.comm,
         u.usr, ptl.ts
  from Phenotypes_Log ptl,
       Users u
  where ptl.id = u.id;

----------------------------------------------------------------
-- Chromosomes -------------------------------------------------
----------------------------------------------------------------
create or replace view V_Chromosomes_1 as
	   select c.cid, c.name, c.comm, c.sid	   		  
			  from Chromosomes c;

create or replace view V_Chromosomes_2 as
  select s.sid, s.name sname,
         c.cid, c.name, c.comm         
  from Species s, Chromosomes c
  where c.sid = s.sid;


----------------------------------------------------------------
-- Markers -----------------------------------------------------
----------------------------------------------------------------
create or replace view V_MARKERS_1 (
	   CID, SUID, MID, NAME, ALIAS, P1, P2, POSITION, COMM, ID, TS) as
  select m.cid, m.suid, m.mid, m.name, m.alias, m.p1, m.p2, 
  		 m.position, m.comm, m.id, m.ts
  from markers m, v_enabled_sampling_units_1 v_su
  where 
  		m.suid = v_su.suid ;
  
create or replace view V_MARKERS_2 (
	   CID, SUID, MID, NAME, ALIAS, P1, P2, POSITION, COMM, USR, TS) as
  select m.cid, m.suid, m.mid, m.name, m.alias, m.p1, m.p2, 
  		 m.position, m.comm, u.usr, m.ts
  from markers m, users u, v_enabled_sampling_units_1 v_su
  where
  	   m.suid = v_su.suid and
  	   u.id = m.id;

create or replace view V_MARKERS_3 (
	   SID, SNAME, CID, CNAME, SUID, SUNAME,
	   MID, NAME, ALIAS, P1, P2, POSITION, COMM, USR, TS) as
  select s.sid, s.name,
  		 c.cid, c.name,
		 v_su.suid, v_su.name,
         m.mid, m.name, m.alias, 
		 m.p1, m.p2, m.position, m.comm,
         u.usr, m.ts
  from Species s, Chromosomes c, Markers m,
       Users u, v_sampling_units_1 v_su
  where 
		v_su.suid = m.suid and
		s.sid = v_su.sid and
		c.cid = m.cid and
		u.id = m.id ;
		
create or replace view V_Markers_Log (
  MID, NAME, ALIAS, COMM, P1, P2, POSITION,
  CID, CNAME, USR, TS)  as
  select ml.mid, ml.name, ml.alias, ml.comm,
  		 ml.p1, ml.p2, ml.position, ml.cid,
         c.name, u.usr, ml.ts
  from Markers_Log ml,
  	   Chromosomes c,
       Users u
  where ml.cid = c.cid and
  		ml.id = u.id;

----------------------------------------------------------------
-- Library Markers ---------------------------------------------
----------------------------------------------------------------
create or replace view V_L_MARKERS_1 (
	   SID, CID, LMID, NAME, ALIAS, P1, P2, POSITION, COMM) as
  select l.sid, l.cid, l.lmid, l.name, l.alias, l.p1, l.p2, l.position, l.comm
  from l_markers l ;
  
create or replace view V_L_MARKERS_2 (
	   SNAME, SID, CNAME, CID, LMID, NAME, ALIAS, P1, P2,  POSITION, COMM) as
  select s.name, l.sid, c.name, l.cid, l.lmid, l.name, l.alias,  
  		 l.p1, l.p2, l.position, l.comm
  from Species s, Chromosomes c, l_markers l
  where
  	   s.sid = l.sid and
	   c.cid = l.cid;



----------------------------------------------------------------
-- Marker Sets -------------------------------------------------
----------------------------------------------------------------
create or replace view V_MARKER_SETS_1 (
	   SUID, MSID, NAME, COMM, ID, TS) as
  select ms.suid, ms.msid, ms.name, ms.comm, 
  		 ms.id, ms.ts 
  from Marker_sets ms, v_enabled_sampling_units_1 v_su
  where
  	   ms.suid = v_su.suid;		

create or replace view V_MARKER_SETS_2 (
	   SUID, MSID, NAME, COMM, USR, TS) as
  select ms.suid, ms.msid, ms.name, ms.comm, 
  		 u.usr, ms.ts 
  from Marker_sets ms, users u, v_enabled_sampling_units_1 v_su
  where
  	   ms.suid = v_su.suid and
  	   u.id = ms.id ;		
		
create or replace view V_MARKER_SETS_3 (
	   SID, SNAME, SUID, SUNAME, MSID, 
	   NAME, COMM, USR, TS) as
  select s.sid, s.name, v_su.suid, v_su.name,
         ms.msid, ms.name, ms.comm,
         u.usr, ms.ts
  from Species s, Marker_Sets ms,
       Users u, v_sampling_units_1 v_su
  where
  	   s.sid = v_su.sid and
	   v_su.suid = ms.suid and
	   u.id = ms.id ;
	   
create or replace view V_MARKER_SETS_LOG (
	   MSID, NAME, COMM, USR, TS) as
  select msl.msid, msl.name, msl.comm,
         u.usr, msl.ts
  from Marker_Sets_Log msl,
       Users u
  where msl.id = u.id;
  
create or replace view V_Positions_1 (
	   MID, MNAME, DEF_POSITION, OVER_POSITION, POSITION, MSID, MSNAME) as
  select m.mid, m.name, m.position, p.value,
  		 decode(p.value, NULL, m.position, p.value),
		 ms.msid, ms.name
  from Markers m, Positions p, Marker_Sets ms, v_enabled_sampling_units_1 v_su
  where
  	   m.mid = p.mid and
	   p.msid = ms.msid and
	   ms.suid = v_su.suid;

CREATE OR REPLACE VIEW V_POSITIONS_2 ( 
  CID, CNAME, MID, MNAME, DEF_POSITION, OVER_POSITION, POSITION, MSID, MSNAME) AS
  select c.cid, c.name, m.mid, m.name, m.position, p.value,
  		 decode(p.value, NULL, m.position, p.value),
		 p.msid, ms.name
  from Markers m, Positions p, Marker_Sets ms, 
  	   v_enabled_sampling_units_1 v_su, Chromosomes c
  where
  	   m.mid = p.mid and
	   p.msid = ms.msid and
	   ms.suid = v_su.suid and
	   c.cid = m.CID;
	   
----------------------------------------------------------------
-- Alleles -----------------------------------------------------
----------------------------------------------------------------
create or replace view V_Alleles_1 as
	   select a.aid, a.name, a.comm, a.mid, a.id, a.ts
	   from Alleles a, markers m, v_enabled_sampling_units_1 v_su
	   where a.mid = m.mid and
	   		 m.suid = v_su.suid;

create or replace view V_Alleles_2 (
	   AID, NAME, COMM, MID, USR, TS) as
  select a.aid, a.name, a.comm, a.mid, u.id, a.ts
  from Alleles a, users u, markers m, 
  	   v_enabled_sampling_units_1 v_su
  where 
  	  u.id = a.id and
	  a.mid = m.mid and
	  m.suid = v_su.suid ;
	   		

create or replace view V_Alleles_3 (
	   SID, SNAME, SUID, SUNAME, CID, CNAME,
	   MID, MNAME, AID, NAME, COMM, USR, TS) as
  select s.sid, s.name sname,
		 v_su.suid, v_su.name suname, 
         c.cid, c.name cname,
         m.mid, m.name mname,
         a.aid, a.name, a.comm,
         u.usr, a.ts
  from Species s, Chromosomes c, Markers m, Alleles a,
       Users u, v_enabled_sampling_units_1 v_su
  where
	   s.sid = v_su.sid and
	   s.sid = c.sid and
	   c.cid = m.cid and
	   m.suid = v_su.suid and
	   m.mid = a.mid and
	   u.id = a.id ; 
  
create or replace view V_Alleles_Log as
  select al.aid, al.name, al.comm,
         u.usr, al.ts
  from Alleles_Log al,
       Users u
  where al.id = u.id;

----------------------------------------------------------------
-- Library Alleles ---------------------------------------------
----------------------------------------------------------------
create or replace view V_L_Alleles_1 as
	   select la.laid, la.name, la.comm, la.lmid
	   from L_Alleles la;

	   		

----------------------------------------------------------------
-- Genotypes ---------------------------------------------------
----------------------------------------------------------------
create or replace view V_Genotypes_1 (
	   MID, IID, SUID, AID1, AID2, RAW1, RAW2, LEVEL_,
	   REFERENCE, COMM, ID, TS) as
  select g.mid, g.iid, g.suid, g.aid1, g.aid2,   
	   		  g.raw1, g.raw2, g.level_, g.reference,
			  g.comm, g.id, g.ts
  from Genotypes g, v_enabled_sampling_units_1 v_su,
  	   v_enabled_individuals_1 v_i 
  where 
  		g.suid = v_su.suid and
		g.iid = v_i.iid;

create or replace view V_Genotypes_2 (
	   MID, IID, SUID, AID1, AID2, RAW1, RAW2, LEVEL_,
	   REFERENCE, COMM, USR, TS) as
  select g.mid, g.iid, g.suid, g.aid1, g.aid2,   
	   		  g.raw1, g.raw2, g.level_, g.reference,
			  g.comm, u.usr, g.ts
  from users u, Genotypes g,
  	   v_enabled_sampling_units_1 v_su,
	   v_enabled_individuals_1 v_i
  where 
    u.id = g.id and
	g.suid = v_su.suid and
	g.id = v_i.iid;
			  
create or replace view V_Genotypes_3 (
	   CID, CNAME, MID, MNAME, SUID, SUNAME,
	   IID, IDENTITY, AID1, AID2, A1NAME, A2NAME,
	   RAW1, RAW2, LEVEL_, REFERENCE, COMM, USR, TS) as
  select c.cid, c.name,
         m.mid, m.name,
         v_su.suid, v_su.name,
         v_i.iid, v_i.identity,
		 gt.aid1, gt.aid2, a1.name, a2.name ,
         gt.raw1, gt.raw2, gt.level_, gt.reference,
		 gt.comm, u.usr, gt.ts
  from Chromosomes c, Markers m,
       Alleles a1, Alleles a2,
       v_enabled_sampling_units_1 v_su,
	   v_enabled_individuals_1 v_i,
       Genotypes gt, Users u
  where
	   v_su.suid = gt.suid and
	   c.cid = m.cid and
	   m.mid = gt.mid and
	   v_i.iid = gt.iid and
	   gt.aid1 = a1.aid (+) and
	   gt.aid2 = a2.aid (+) and
	   u.id = gt.id ;

create or replace view V_Genotypes_4 (
	   PID, SID, SNAME, CID, CNAME, MID, MNAME, SUID, SUNAME,
	   IID, IDENTITY, AID1, AID2, A1NAME, A2NAME,
	   RAW1, RAW2, LEVEL_, REFERENCE, COMM, USR, TS) as
  select r.pid, s.sid, s.name, c.cid, c.name cname,
         m.mid, m.name mname,
         v_su.suid, v_su.name suname,
         v_i.iid, v_i.identity,
		 gt.aid1, gt.aid2, a1.name, a2.name ,
         gt.raw1, gt.raw2, gt.level_, gt.reference, 
		 gt.comm, u.usr, gt.ts
  from R_Prj_Su r, Species s, Chromosomes c, Markers m,
       Alleles a1, Alleles a2,
       v_enabled_sampling_units_1 v_su, 
	   v_enabled_individuals_1 v_i,
       Genotypes gt, Users u
  where
  	   r.suid = gt.suid and
	   s.sid = v_su.sid and
	   v_su.suid = gt.suid and
	   c.cid = m.cid and
	   m.mid = gt.mid and
	   v_i.iid = gt.iid and
	   gt.aid1 = a1.aid (+) and
	   gt.aid2 = a2.aid (+) and
	   u.id = gt.id ;

create or replace view V_Genotypes_5 (
	   PID, MID, IID, SUID, AID1, AID2, RAW1, RAW2, LEVEL_,
	   REFERENCE, COMM, ID, TS) as
  select p.pid, g.mid, g.iid, g.suid, g.aid1, g.aid2,   
	   		  g.raw1, g.raw2, g.level_, g.reference,
			  g.comm, g.id, g.ts
  from Genotypes g, r_prj_su p,
  	   v_enabled_sampling_units_1 v_su,
	   v_enabled_individuals_1 v_i 
  where 
    p.suid = g.suid and
	g.suid = v_su.suid and
	g.iid = v_i.iid ;
	   

CREATE OR REPLACE VIEW V_GENOTYPES_6 ( MID, 
NAME, IID, IDENTITY, ALIAS, A1NAME, 
A2NAME, REFERENCE, FATHER, MOTHER, 
RAW1, RAW2, COMM ) AS select m.mid, m.name, 
         i.iid, i.identity, i.alias, 
         a1.name a1name, a2.name a2name, g.reference, 
         i.father, i.mother,  g.raw1,  g.raw2,  g.comm 
  from markers m, 
       individuals i, 
       genotypes g, 
       alleles a1, alleles a2 
  where 
	   i.status = 'E' and 
           i.iid = g.iid and 
           m.mid = g.mid and 
           g.aid1  = a1.aid (+) and 
           g.aid2 = a2.aid (+);

	   
create or replace view V_Genotypes_Log (
	   MID, IID, AID1, AID2, A1NAME, A2NAME,
	   RAW1, RAW2, LEVEL_, REFERENCE, COMM, USR, TS) as
  select gtl.mid, gtl.iid,
	 gtl.aid1, gtl.aid2, a1.name, a2.name,
         gtl.raw1, gtl.raw2, gtl.level_,
		 gtl.reference, gtl.comm,
         u.usr, gtl.ts
  from Genotypes_Log gtl,
       Alleles a1, Alleles a2,
       Users u
  where gtl.aid1 = a1.aid (+)
    and gtl.aid2 = a2.aid (+)
    and gtl.id = u.id;

----------------------------------------------------------------
-- Filters -----------------------------------------------------
----------------------------------------------------------------
create or replace view V_Filters_1 as
	   select f.fid, f.name, f.expression, 
	   		  f.comm, f.pid, f.sid, f.id, f.ts
		from Filters f ;
		
create or replace view V_Filters_2 as
  select f.pid,
         f.sid, s.name sname,
         f.fid, f.name, f.expression, f.comm,
         u.usr, f.ts
  from Species s, Filters f,
       Users u
  where f.sid = s.sid
    and f.id = u.id;

create or replace view V_Filters_Log as
  select s.sid, s.name sname,
         fl.fid, fl.name, fl.expression, fl.comm,
         u.usr, fl.ts
  from Filters_Log fl, Species s,
       Users u
  where fl.sid = s.sid
    and fl.id = u.id;

----------------------------------------------------------------
-- U-Markers ---------------------------------------------------
----------------------------------------------------------------
create or replace view V_U_Markers_1 (
	   PID, SID, CID, UMID, NAME, ALIAS, POSITION, COMM, ID, TS) as
	   select um.pid, um.sid, um.cid, um.umid, um.name, um.alias,  
	   		  um.position, um.comm, um.id, um.ts
		from U_Markers um ;

create or replace view V_U_Markers_2 (
	   PID, SID, CID, UMID, NAME, ALIAS, POSITION, COMM, USR, TS) as
	   select um.pid, um.sid, um.cid, um.umid, um.name, um.alias,  
	   		  um.position, um.comm, u.usr, um.ts
		from U_Markers um, Users u
		where
			 u.id = um.id ;
		
create or replace view V_U_Markers_3 (
	   PID, SID, SNAME, CID, CNAME, UMID,
	   NAME, ALIAS, POSITION, COMM, USR, TS) as
  select um.pid, s.sid, s.name, c.cid, c.name, 
  		 um.umid, um.name, um.alias, 
		 um.position, um.comm,	   		   
		 u.usr, um.ts
		from U_Markers um, Species s, 
			 Chromosomes c, Users u
		where
			 u.id = um.id and
			 s.sid = um.sid and
			 c.cid = um.cid ;

create or replace view V_U_MARKERS_LOG (
	   UMID, NAME, ALIAS, POSITION, COMM, CID, CNAME, USR, TS) as
  select uml.umid, uml.name, uml.alias, 
  		 uml.position, uml.comm, uml.cid, 
         c.name, u.usr, uml.ts
  from U_Markers_Log uml,
  	   Chromosomes c,
       Users u
  where 
  		c.cid = uml.cid and
  		u.id = uml.id;

CREATE OR REPLACE VIEW V_R_UMID_MID_1 ( 
  PID, SUID, UMID, MID, TS) AS
  select r.pid, r.suid, r.umid, r.mid, r.ts 
	from r_umid_mid r, v_enabled_sampling_units_1 v_su
	where
		 r.suid = v_su.suid;

CREATE OR REPLACE VIEW V_R_UMID_MID_2 ( 
  PID, SUID, SUNAME, UMID, MID, MNAME, TS ) AS
  select r.pid, r.suid, v_su.name, r.umid, r.mid, m.name, r.ts 
	from r_umid_mid r, markers m, v_enabled_sampling_units_1 v_su 
	where r.mid = m.mid and
		  v_su.suid = r.suid;

CREATE OR REPLACE VIEW V_R_UAID_AID_1 ( 
  PID, UMID, AID, UAID, TS) AS
  select r.pid, r.umid, r.aid, r.uaid, r.ts 
	from r_uaid_aid r, markers m, alleles a,
		 v_enabled_sampling_units_1 v_su
	where
		 r.aid = a.aid and
		 a.mid = m.mid and
		 v_su.suid = m.suid;

  
----------------------------------------------------------------
-- U-Marker_Sets -----------------------------------------------
----------------------------------------------------------------
create or replace view V_U_MARKER_SETS_1 (
	   PID, SID, UMSID, NAME, COMM, ID, TS) as
  select ums.pid, ums.sid, ums.umsid, ums.name, 
  		 ums.comm, ums.id, ums.ts
		from U_Marker_Sets ums ;

create or replace view V_U_MARKER_SETS_2 (
	   PID, SID, UMSID, NAME, COMM, USR, TS) as
  select ums.pid, ums.sid, ums.umsid, ums.name, 
  		 ums.comm, u.usr, ums.ts
  from U_Marker_Sets ums, Users u
  where
  	   u.id = ums.id;

create or replace view V_U_MARKER_SETS_3 (
	   PID, SID, SNAME, UMSID, NAME, COMM, USR, TS) as
  select ums.pid, ums.sid, ums.name, ums.umsid, 
  		 ums.name, ums.comm, u.usr, ums.ts
  from U_Marker_Sets ums, Users u, species s
  where
  	   u.id = ums.id and
	   s.sid = ums.sid;
		
create or replace view V_U_MARKER_SETS_LOG (
	   UMSID, NAME, COMM, USR, TS) as
  select umsl.umsid, umsl.name, umsl.comm,
	   		  u.usr, umsl.ts
  from U_Marker_sets_log umsl, Users u
  where
  	   u.id = umsl.id ;

create or replace view V_U_Positions_1 (
	   UMID, UMNAME, DEF_POSITION, OVER_POSITION, POSITION, UMSID, UMSNAME) as
  select um.umid, um.name, um.position, up.value,
  		 decode(up.value, NULL, um.position, up.value),
		 ums.umsid, ums.name
  from U_Markers um, U_Positions up, U_Marker_Sets ums
  where
  	   um.umid = up.umid and
	   up.umsid = ums.umsid ;

CREATE OR REPLACE VIEW V_U_POSITIONS_2 ( 
  CID, CNAME, UMID, UMNAME, DEF_POSITION, OVER_POSITION, POSITION, UMSID, UMSNAME) AS
  select c.cid, c.name, um.umid, um.name, um.position, up.value,
  		 decode(up.value, NULL, um.position, up.value),
		 up.umsid, ums.name
  from U_Markers um, U_Positions up, U_Marker_Sets ums, Chromosomes c
  where
  	   um.umid = up.umid and
	   up.umsid = ums.umsid and
	   c.cid = um.CID ;
	   

----------------------------------------------------------------
-- U-Variable ---------------------------------------------
----------------------------------------------------------------

create or replace view V_U_VARIABLES_1 (
	   PID, SID, UVID, NAME, TYPE, UNIT, COMM, ID, TS) as
	   select uv.pid, uv.sid, uv.uvid, uv.name, uv.type, 
	   		  uv.unit,  uv.comm, uv.id, uv.ts
		from U_Variables uv ;

create or replace view V_U_VARIABLES_2 (
	   PID, SID, UVID, NAME, TYPE, UNIT, COMM, USR, TS) as
  select uv.pid, uv.sid, uv.uvid, uv.name, uv.type, 
	   		  uv.unit, uv.comm, u.usr, uv.ts
  from U_Variables uv, users u
  where
  	   u.id = uv.id ;
		
create or replace view V_U_VARIABLES_3 (
	   PID, SID, SNAME, UVID, NAME, TYPE, UNIT, COMM, USR, TS) as
  select uv.pid, uv.sid, s.name, uv.uvid, uv.name, uv.type, 
	 	 uv.unit, uv.comm, u.usr, uv.ts
  from U_Variables uv, users u, species s
  where
  	   u.id = uv.id and
	   s.sid = uv.sid;
			 
create or replace view V_U_VARIABLES_LOG (
	   UVID, NAME, TYPE, UNIT, COMM, USR, TS) as
  select uvl.uvid, uvl.name, uvl.type, uvl.unit, 
  		 uvl.comm, u.usr, uvl.ts
  from U_Variables_Log uvl, Users u
  where
  	   u.id = uvl.id ;
 
CREATE OR REPLACE VIEW V_R_UVID_VID_1 ( 
	   PID, SUID, UVID, VID, VNAME, SUNAME, TS ) AS
  select r.pid, r.suid, r.uvid, r.vid, v.name, s.name, r.ts    
	from r_uvid_vid r, variables v, sampling_units s    
	where r.vid = v.vid and    
		  s.suid = r.suid;


																
----------------------------------------------------------------
-- U_Variable_Sets ---------------------------------------------
----------------------------------------------------------------
create or replace view V_U_VARIABLE_SETS_1 (
	   PID, SID, UVSID, NAME, COMM, ID, TS) as
  select uvs.pid, uvs.sid, uvs.uvsid, uvs.name, 
  		 uvs.comm, uvs.id, uvs.ts
  from U_Variable_Sets uvs ;

create or replace view V_U_VARIABLE_SETS_2 (
	   PID, SID, UVSID, NAME, COMM, USR, TS) as
  select uvs.pid, uvs.sid, uvs.uvsid, uvs.name, 
  		 uvs.comm, u.usr, uvs.ts
  from U_Variable_Sets uvs, users u
  where
  	   u.id = uvs.id ;

create or replace view V_U_VARIABLE_SETS_3 (
	   PID, SID, SNAME, UVSID, NAME, COMM, USR, TS) as
  select uvs.pid, uvs.sid, s.name, uvs.uvsid, 
  		 uvs.name, uvs.comm, u.usr, uvs.ts
  from U_Variable_Sets uvs, users u, species s
  where
  	   u.id = uvs.id and
	   s.sid = uvs.sid;
			 
create or replace view V_U_VARIABLE_SETS_LOG (
	   UVSID, NAME, COMM, USR, TS) as
  select uvsl.uvsid, uvsl.name, 
	  	 uvsl.comm, u.usr, uvsl.ts
  from U_Variable_Sets_Log uvsl, Users u
  where
  	   u.id = uvsl.id ;
		
create or replace view V_R_U_VAR_SET_1 (
  UVSID, UVID, PID, ID, TS) AS
  select uvsid, uvid, pid, id, ts 
  from r_u_var_set;
   
create or replace view V_R_U_VAR_SET_2 (
  UVSID, UVSNAME, UVID, UVNAME, PID, ID, TS) AS
  select uvs.uvsid, uvs.name, uv.uvid, uv.name, r.pid, r.id, r.ts 
  from U_Variable_sets uvs, 
  	   U_Variables uv,
	   r_u_var_set r
  where 
  	   uvs.uvsid = r.uvsid and
	   uv.uvid = r.uvid;

----------------------------------------------------------------
-- U_ALLELES ---------------------------------------------------
----------------------------------------------------------------

create or replace view V_U_ALLELES_1 (
	   UMID, UAID, NAME, COMM, ID, TS) as
  select ua.umid, ua.uaid, ua.name, 
  		 ua.comm, ua.id, ua.ts
  from U_Alleles ua ;

create or replace view V_U_ALLELES_2 (
	   UMID, UAID, NAME, COMM, USR, TS) as
  select ua.umid, ua.uaid, ua.name, 
  		 ua.comm, ua.id, ua.ts
  from U_Alleles ua, users u
  where 
  		u.id = ua.id;

create or replace view V_U_ALLELES_3 (
	   PID, UMID, UAID, NAME, COMM, TS) as
  select um.pid, ua.umid, ua.uaid, ua.name, 
  		 ua.comm, ua.ts
  from U_Alleles ua, U_Markers um
  where 
  		um.umid = ua.umid;
		
create or replace view V_U_ALLELES_4 (
	   PID, SID, SNAME, CID, CNAME, 
	   UMID, UMNAME, UAID, NAME, 
	   COMM, USR, TS) as
  select um.pid, c.sid, s.name, um.cid, c.name,
  		 ua.umid, um.name, ua.uaid, ua.name, 
  		 ua.comm, u.usr, ua.ts
  from species s, chromosomes c, u_markers um,
  	   U_Alleles ua, users u
  where
  	   s.sid = c.sid and
	   c.cid = um.cid and
	   um.umid = ua.umid and 
  	   u.id = ua.id;
							
create or replace view V_U_ALLELES_LOG (
	   UAID, NAME, COMM, USR, TS) as
  select ual.uaid, ual.name, ual.comm,
		 u.usr, ual.ts
  from U_Alleles_Log ual, Users u
  where 
  		u.id = ual.id ;
		
----------------------------------------------------------------
-- File Generations --------------------------------------------
----------------------------------------------------------------

create or replace view V_FILE_GENERATIONS_1 (
	   PID, FGID, NAME, MODE_, TYPE, 
	   XMSID, XVSID, COMM, ABORT_, ID, TS) as 
  select a.pid, a.fgid, a.name, a.mode_,
  		 a.type_, a.msid, a.vsid, a.comm,
		 a.abort_, a.id, a.ts
  from file_generations a; 
  
create or replace view V_FILE_GENERATIONS_2 (
	   PID, FGID, NAME, MODE_, TYPE, 
	   XMSID, XVSID, COMM, ABORT_, USR, TS) as 
  select a.pid, a.fgid, a.name, a.mode_,
  		 a.type_, a.msid, a.vsid, a.comm,
		 a.abort_, u.usr, a.ts
  from file_generations a, users u
  where
  	   u.id = a.id ; 

create or replace view V_FILE_GENERATIONS_3 (
	   PID, FGID, NAME, MODE_, TYPE, 
	   XMSID, XMSNAME, XVSID, XVSNAME, 
	   COMM, ABORT_, USR, TS) as 
  select a.pid, a.fgid, a.name, a.mode_,
  		 a.type_, a.msid,
		 decode(mode_, 'S', (select name from marker_sets where msid=a.msid),
		 	   (select name from u_marker_sets where pid=a.pid and umsid=a.msid) ),
		 a.vsid, 
		 decode(mode_, 'S', (select name from variable_sets where vsid=a.vsid),
		 	   (select name from u_variable_sets where pid=a.pid and uvsid=a.vsid) ),
		 a.comm, a.abort_, u.usr, a.ts
  from file_generations a, users u
  where
  	   u.id = a.id; 

create or replace view V_FILE_GENERATIONS_LOG (
	   FGID, PARA1) as
  select '*', 'TBI' 
  from dual; 

create or replace view V_R_FG_FLT_1 (
	   FGID, SUID, NAME, FID, GSID, EXPRESSION, SID) as
  SELECT R.FGID, R.SUID, SU.NAME, R.FID, R.GSID, F.EXPRESSION, F.SID
  FROM R_FG_FLT R, FILTERS F, SAMPLING_UNITS SU
  WHERE R.SUID=SU.SUID AND R.FID=F.FID;	
      
----------------------------------------------------------------
-- DATA FILES --------------------------------------------------
----------------------------------------------------------------

create or replace view V_DATA_FILES_1 (
	   FGID, DFID, NAME, STATUS, COMM, ID, TS) as 
  select df.fgid, df.dfid, df.name, df.status,
  		 df.comm, df.id, df.ts
  from data_files df; 
  
create or replace view V_DATA_FILES_2 (
	   FGID, DFID, NAME, STATUS, COMM, USR, TS) as 
  select df.fgid, df.dfid, df.name, df.status,
  		 df.comm, u.USR, df.ts
  from data_files df, users u
  where
  	   u.id = df.id; 

create or replace view V_DATA_FILES_3 (
	   FGID, ANNAME, DFID, NAME, STATUS, COMM, USR, TS) as 
  select df.fgid, a.name, df.dfid, df.name, df.status,
  		 df.comm, u.USR, df.ts
  from data_files df, users u, file_generations a
  where
  	   u.id = df.id and
	   a.fgid = df.fgid; 

create or replace view V_DATA_FILES_LOG (
	   DFID, PARA1) as
  select '*', 'TBI' 
  from dual; 

----------------------------------------------------------------

commit ;

