----------------------------------------------------------------
--
-- This script creates all DB objects
--
-- This script must be run as GdbAdm
--
----------------------------------------------------------------

----------------------------------------------------------------
-- Sampling_Units ----------------------------------------------
----------------------------------------------------------------

ALTER TABLE SAMPLING_UNITS_LOG ADD 
 FOREIGN KEY (SUID) 
  REFERENCES SAMPLING_UNITS (SUID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Individuals --------------------------------------------------
----------------------------------------------------------------

ALTER TABLE INDIVIDUALS_LOG ADD 
 FOREIGN KEY (IID) 
  REFERENCES INDIVIDUALS (IID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Groupings ---------------------------------------------------
----------------------------------------------------------------

ALTER TABLE GROUPINGS_LOG ADD 
 FOREIGN KEY (GSID) 
  REFERENCES GROUPINGS (GSID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Groups ------------------------------------------------------
----------------------------------------------------------------

ALTER TABLE GROUPS ADD 
 FOREIGN KEY (GSID) 
  REFERENCES GROUPINGS (GSID) 
 ON DELETE CASCADE;

ALTER TABLE GROUPS_LOG ADD 
 FOREIGN KEY (GID) 
  REFERENCES GROUPS (GID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Variables ---------------------------------------------------
----------------------------------------------------------------

ALTER TABLE VARIABLES_LOG ADD 
 FOREIGN KEY (VID) 
  REFERENCES VARIABLES (VID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Variable Sets -----------------------------------------------
----------------------------------------------------------------

ALTER TABLE VARIABLE_SETS_LOG ADD 
 FOREIGN KEY (VSID) 
  REFERENCES VARIABLE_SETS (VSID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Phenotypes --------------------------------------------------
----------------------------------------------------------------

ALTER TABLE PHENOTYPES_LOG ADD 
 FOREIGN KEY (VID, IID) 
  REFERENCES PHENOTYPES (VID, IID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Markers -----------------------------------------------------
----------------------------------------------------------------

ALTER TABLE MARKERS_LOG ADD 
 FOREIGN KEY (MID) 
  REFERENCES MARKERS (MID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Marker Sets -------------------------------------------------
----------------------------------------------------------------

ALTER TABLE MARKER_SETS_LOG ADD 
 FOREIGN KEY (MSID) 
  REFERENCES MARKER_SETS (MSID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Alleles -----------------------------------------------------
----------------------------------------------------------------

ALTER TABLE ALLELES ADD 
 FOREIGN KEY (MID) 
  REFERENCES MARKERS (MID) 
 ON DELETE CASCADE;

ALTER TABLE ALLELES_LOG ADD 
 FOREIGN KEY (AID) 
  REFERENCES ALLELES (AID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Genotypes ---------------------------------------------------
----------------------------------------------------------------

ALTER TABLE GENOTYPES_LOG ADD 
 FOREIGN KEY (MID, IID) 
  REFERENCES GENOTYPES (MID, IID) 
 ON DELETE CASCADE;

----------------------------------------------------------------
-- Filters -----------------------------------------------------
----------------------------------------------------------------

ALTER TABLE FILTERS_LOG ADD 
 FOREIGN KEY (FID) 
  REFERENCES FILTERS (FID) 
 ON DELETE CASCADE;

----------------------------------------------------------------

commit ;


