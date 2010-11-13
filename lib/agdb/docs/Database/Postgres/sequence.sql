-- Setting the sequences is very important after import!!!




select setval('gdbadm.Projects_Seq', max(pid)) from gdbadm.Projects;
select setval('gdbadm.Users_Seq', max(id)) from gdbadm.Users;
select setval('gdbadm.Roles_Seq', max(rid)) from gdbadm.Roles_;
select setval('gdbadm.Species_Seq', max(sid)) from gdbadm.Species;
select setval('gdbadm.Sampling_Units_Seq', max(suid)) from gdbadm.Sampling_Units;
select setval('gdbadm.Individuals_Seq', max(iid)) from gdbadm.Individuals;
select setval('gdbadm.Samples_Seq', max(said)) from gdbadm.Samples;
select setval('gdbadm.Groupings_Seq', max(gsid)) from gdbadm.Groupings;
select setval('gdbadm.Groups_Seq', max(gid)) from gdbadm.Groups;
select setval('gdbadm.U_Variables_Seq', max(uvid)) from gdbadm.U_Variables;
select setval('gdbadm.U_Variable_Sets_Seq', max(uvsid)) from gdbadm.U_Variable_Sets;
select setval('gdbadm.Variables_Seq', max(vid)) from gdbadm.Variables;
select setval('gdbadm.Variable_Sets_Seq', max(vsid)) from gdbadm.Variable_Sets;
--select setval('gdbadm.Phenotypes_Seq', max()) from gdbadm.Phenotypes;
select setval('gdbadm.Chromosomes_Seq', max(cid)) from gdbadm.Chromosomes;
select setval('gdbadm.U_Markers_Seq', max(umid)) from gdbadm.U_Markers;
select setval('gdbadm.U_Marker_Sets_Seq', max(umsid)) from gdbadm.U_Marker_Sets;
select setval('gdbadm.U_Alleles_Seq', max(uaid)) from gdbadm.U_Alleles;
select setval('gdbadm.L_Markers_Seq', max(lmid)) from gdbadm.L_Markers;
select setval('gdbadm.L_Alleles_Seq', max(laid)) from gdbadm.L_Alleles;
select setval('gdbadm.Markers_Seq', max(mid)) from gdbadm.Markers;
select setval('gdbadm.Marker_Sets_Seq', max(msid)) from gdbadm.Marker_Sets;
select setval('gdbadm.Alleles_Seq', max(aid)) from gdbadm.Alleles;
--select setval('gdbadm.Genotypes_Seq', max()) from gdbadm.Genotypes;
select setval('gdbadm.Filters_Seq', max(fid)) from gdbadm.Filters;
select setval('gdbadm.File_Generations_Seq', max(fgid)) from gdbadm.File_Generations;
select setval('gdbadm.Data_Files_Seq', max(dfid)) from gdbadm.Data_Files;
