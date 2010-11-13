-----------------------------------------------------------
-- This Script initializes the application
-- with all the supported privileges.
--
-- Note: 
-- New privileges can be added as long as 
-- the GENO_W0 to GENO_W9 is defined as
-- a serie, where GENO_W0 has the lowest 
-- number and GENO_W9 the highset. There 
-- may not be any holes in the serie 
--
--   
-- 2003-11-26 liwa added 43-48

delete from privileges_ ;

insert into privileges_ values(1, 'PROJECT_ADM', 'Add or delete project members and roles');

insert into privileges_ values(2, 'PROJECT_STA', 'View project statistics');

insert into privileges_ values(3, 'SU_W', 'Create, update and delete sampling units');

insert into privileges_ values(4, 'SU_R', 'View sampling units');

insert into privileges_ values(5, 'GRP_W', 'Create, update and delete groupings and groups');

insert into privileges_ values(6, 'GRP_R', 'View groupings and groups');

insert into privileges_ values(7, 'IND_W', 'Create, update and delete individuals and samples');

insert into privileges_ values(8, 'IND_R', 'View individuals and samples');

insert into privileges_ values(9, 'VAR_W', 'Create, update and delete variables');

insert into privileges_ values(10, 'VAR_R', 'View variables');

insert into privileges_ values(11, 'VARS_W', 'Create, update and delete variable sets');

insert into privileges_ values(12, 'VARS_R', 'View variable sets');

insert into privileges_ values(13, 'UVAR_W', 'Create, update and delete unified variables');

insert into privileges_ values(14, 'UVAR_R', 'View unified variables');

insert into privileges_ values(15, 'UVARS_W', 'Create, update and delete unified variable sets');

insert into privileges_ values(16, 'UVARS_R', 'View unified variable sets');

insert into privileges_ values(17, 'PHENO_W', 'Create, update and delete phenotypes');

insert into privileges_ values(18, 'PHENO_R', 'View phenotypes');

insert into privileges_ values(19, 'MRK_W', 'Create, update and delete markers and alleles');

insert into privileges_ values(20, 'MRK_R', 'View markers and alleles');

insert into privileges_ values(21, 'LMRK_R', 'View and copy library markers');

insert into privileges_ values(22, 'MRKS_W', 'Create, update and delete marker sets');

insert into privileges_ values(23, 'MRKS_R', 'View marker sets');

insert into privileges_ values(24, 'UMRK_W', 'Create, update and delete unified markers');

insert into privileges_ values(25, 'UMRK_R', 'View unified markers');

insert into privileges_ values(26, 'UMRKS_W', 'Create, update and delete unified marker sets');

insert into privileges_ values(27, 'UMRKS_R', 'View unified marker sets');

insert into privileges_ values(28, 'GENO_W0', 'Create, update and delete genotypes with level=0');

insert into privileges_ values(29, 'GENO_W1', 'Create, update and delete genotypes with level<=1');

insert into privileges_ values(30, 'GENO_W2', 'Create, update and delete genotypes with level<=2');

insert into privileges_ values(31, 'GENO_W3', 'Create, update and delete genotypes with level<=3');

insert into privileges_ values(32, 'GENO_W4', 'Create, update and delete genotypes with level<=4');

insert into privileges_ values(33, 'GENO_W5', 'Create, update and delete genotypes with level<=5');

insert into privileges_ values(34, 'GENO_W6', 'Create, update and delete genotypes with level<=6');

insert into privileges_ values(35, 'GENO_W7', 'Create, update and delete genotypes with level<=7');

insert into privileges_ values(36, 'GENO_W8', 'Create, update and delete genotypes with level<=8');

insert into privileges_ values(37, 'GENO_W9', 'Create, update and delete genotypes with level<=9');

insert into privileges_ values(38, 'GENO_R', 'View genotypes');

insert into privileges_ values(39, 'FLT_W', 'Create, update and delete filters');

insert into privileges_ values(40, 'FLT_R', 'View filters');

insert into privileges_ values(41, 'ANA_W', 'Create, update and delete file generations');

insert into privileges_ values(42, 'ANA_R', 'View file generations and data files');

insert into privileges_ values(43, 'RES_R', 'View Results');

insert into privileges_ values(44, 'RES_W', 'Create, update and delete results');

insert into privileges_ values(45, 'CTG_R', 'View category');

insert into privileges_ values(46, 'CTG_W', 'Create, update and delete categories');

insert into privileges_ values(47, 'RTYPE_R', 'View result types');

insert into privileges_ values(48, 'RTYPE_W', 'Create, update and delete types');