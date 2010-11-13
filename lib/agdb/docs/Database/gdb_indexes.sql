----------------------------------------------------------------
--
-- This script creates all DB indexes
--
-- This script must be run as GdbAdm
--
----------------------------------------------------------------

DROP INDEX IDX_SAMPLING_UNITS_LOG_SUID;
DROP INDEX IDX_INDIVIDUALS_SUID;
DROP INDEX IDX_INDIVIDUALS_LOG_IID;
DROP INDEX IDX_GROUPINGS_SUID;
DROP INDEX IDX_GROUPINGS_LOG_GSID;
DROP INDEX IDX_GROUPS_GSID;
DROP INDEX IDX_GROUPS_LOG_GID;
DROP INDEX IDX_VARIABLES_SUID;
DROP INDEX IDX_VARIABLES_LOG_VID;
DROP INDEX IDX_PHENOTYPES_SUID;
DROP INDEX IDX_PHENOTYPES_LOG_VID_IID;
DROP INDEX IDX_MARKERS_SUID;
DROP INDEX IDX_MARKERS_CID;
DROP INDEX IDX_MARKERS_LOG_MID;
DROP INDEX IDX_ALLELES_MID;
DROP INDEX IDX_ALLELES_LOG_AID;
DROP INDEX IDX_GENOTYPES_SUID;
DROP INDEX IDX_GENOTYPES_LOG_MID_IID;

----------------------------------------------------------------
-- Sampling_Units ----------------------------------------------
----------------------------------------------------------------

CREATE INDEX IDX_SAMPLING_UNITS_LOG_SUID
  ON SAMPLING_UNITS_LOG (SUID);

----------------------------------------------------------------
-- Individuals --------------------------------------------------
----------------------------------------------------------------

CREATE INDEX IDX_INDIVIDUALS_SUID
  ON INDIVIDUALS (SUID);

CREATE INDEX IDX_INDIVIDUALS_LOG_IID
  ON INDIVIDUALS_LOG (IID);

----------------------------------------------------------------
-- Groupings ---------------------------------------------------
----------------------------------------------------------------

CREATE INDEX IDX_GROUPINGS_SUID
  ON GROUPINGS (SUID);

CREATE INDEX IDX_GROUPINGS_LOG_GSID
  ON GROUPINGS_LOG (GSID);

----------------------------------------------------------------
-- Groups ------------------------------------------------------
----------------------------------------------------------------

CREATE INDEX IDX_GROUPS_GSID
  ON GROUPS (GSID);

CREATE INDEX IDX_GROUPS_LOG_GID
  ON GROUPS_LOG (GID);

----------------------------------------------------------------
-- Variables ---------------------------------------------------
----------------------------------------------------------------

CREATE INDEX IDX_VARIABLES_SUID
  ON VARIABLES (SUID);

CREATE INDEX IDX_VARIABLES_LOG_VID
  ON VARIABLES_LOG (VID);

----------------------------------------------------------------
-- Variable Sets -----------------------------------------------
----------------------------------------------------------------

----------------------------------------------------------------
-- Phenotypes --------------------------------------------------
----------------------------------------------------------------

--CREATE INDEX IDX_PHENOTYPES_SUID
--  ON PHENOTYPES (SUID);

CREATE INDEX IDX_PHENOTYPES_LOG_VID_IID
  ON PHENOTYPES_LOG (VID, IID);

----------------------------------------------------------------
-- Markers -----------------------------------------------------
----------------------------------------------------------------

CREATE INDEX IDX_MARKERS_SUID
  ON MARKERS (SUID);

CREATE INDEX IDX_MARKERS_CID
  ON MARKERS (CID);

CREATE INDEX IDX_MARKERS_LOG_MID
  ON MARKERS_LOG (MID);

----------------------------------------------------------------
-- Marker Sets -------------------------------------------------
----------------------------------------------------------------

----------------------------------------------------------------
-- Alleles -----------------------------------------------------
----------------------------------------------------------------

CREATE INDEX IDX_ALLELES_MID
  ON ALLELES (MID);

CREATE INDEX IDX_ALLELES_LOG_AID
  ON ALLELES_LOG (AID);

----------------------------------------------------------------
-- Genotypes ---------------------------------------------------
----------------------------------------------------------------

--CREATE INDEX IDX_GENOTYPES_SUID
--  ON GENOTYPES (SUID);

CREATE INDEX IDX_GENOTYPES_LOG_MID_IID
  ON GENOTYPES_LOG (MID, IID);

----------------------------------------------------------------
-- Filters -----------------------------------------------------
----------------------------------------------------------------

----------------------------------------------------------------

commit ;


