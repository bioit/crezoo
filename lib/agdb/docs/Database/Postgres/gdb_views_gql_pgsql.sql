----------------------------------------------------------------
--
-- This script creates all GQL views
--
-- This script must be run as SysAdm
--
--  2000-05-18	ANNY	First version
--  2000-06-14	TOBJ	Added the coloumn su.name in 
--						V_Individuals_GQL
--  2000-09-28	TOBJ	Modified V_GENOTYPES_GQL to make
--						an outer join with the allele
--						tables.
-- 	2000-10-20	TOBJ 	Adopted views to new database modell 
--  2001-02-13  TOBJ	Modified views to not include disabled
--						sampling units or individuals. 
--	
----------------------------------------------------------------

----------------------------------------------------------------
-- Individuals -------------------------------------------------
----------------------------------------------------------------

--CREATE OR REPLACE VIEW V_Individuals_GQL AS
--  SELECT r.pid,
--         su.sid, su.suid, su.name, 
--         i.iid, i.identity, i.alias, i.sex, i.birth_date,
--         decode(f.status, 'E', f.identity, null) fidentity, 
--		 decode(m.status, 'E', m.identity, null)  midentity
--  FROM R_PRJ_SU r, SAMPLING_UNITS su,
--       INDIVIDUALS i, INDIVIDUALS f, INDIVIDUALS m
--  WHERE su.status = 'E'
--    AND i.status = 'E'
--    AND i.suid = r.suid
--    AND i.suid = su.suid
--    AND i.father = f.iid (+)
--    AND i.mother = m.iid (+);

CREATE OR REPLACE VIEW V_Individuals_GQL AS
  SELECT r.pid,
         su.sid, su.suid, su.name, 
         i.iid, i.identity, i.alias, i.sex, i.birth_date,
         (select case when f.status = 'E' then f.identity else null end) as fidentity,
         (select case when m.status = 'E' then m.identity else null end) as midentity
         --decode(f.status, 'E', f.identity, null) fidentity, 
         --decode(m.status, 'E', m.identity, null)  midentity
  FROM  R_PRJ_SU r, 
        SAMPLING_UNITS su,
        INDIVIDUALS i 
            left join INDIVIDUALS f on i.father = f.iid  
            left join INDIVIDUALS m on i.mother = m.iid
  WHERE su.status = 'E'
    AND i.status = 'E'
    AND i.suid = r.suid
    AND i.suid = su.suid;
    
    

----------------------------------------------------------------
-- Phenotypes --------------------------------------------------
----------------------------------------------------------------

CREATE OR REPLACE VIEW V_Phenotypes_GQL AS
  SELECT pt.iid, pt.vid,
         pt.value, pt.DATE_
  FROM PHENOTYPES pt, 
  	   V_ENABLED_INDIVIDUALS_1 v_i
  WHERE v_i.iid = pt.iid;

----------------------------------------------------------------
-- Genotypes ---------------------------------------------------
----------------------------------------------------------------

--CREATE OR REPLACE VIEW V_Genotypes_GQL AS
--  SELECT gt.iid, gt.mid,
--         a1.name as a1name, a2.name as a2name,
--         gt.raw1, gt.raw2
--  FROM GENOTYPES gt, ALLELES a1, ALLELES a2,
--  	   V_ENABLED_INDIVIDUALS_1 v_i
--  WHERE gt.iid = v_i.iid
--    AND gt.aid1 = a1.aid (+)
--    AND gt.aid2 = a2.aid (+) ;


CREATE OR REPLACE VIEW V_Genotypes_GQL AS
  SELECT gt.iid, gt.mid,
         a1.name as a1name, a2.name as a2name,
         gt.raw1, gt.raw2
  FROM  GENOTYPES gt 
            left join ALLELES a1 on gt.aid1 = a1.aid
            left join ALLELES a2 on gt.aid2 = a2.aid,
        V_ENABLED_INDIVIDUALS_1 v_i
  WHERE gt.iid = v_i.iid;
   
    

----------------------------------------------------------------
-- Sets --------------------------------------------------------
----------------------------------------------------------------

CREATE OR REPLACE VIEW V_SETS_GQL AS
  SELECT s.suid, gs.gsid, gr.gid, r.iid 
  FROM SAMPLING_UNITS s, GROUPINGS gs, GROUPS gr, R_IND_GRP r 
  WHERE s.status = 'E' 
    AND gs.suid = s.suid
    AND gr.gsid = gs.gsid
    AND r.gid = gr.gid;

----------------------------------------------------------------

COMMIT ;

