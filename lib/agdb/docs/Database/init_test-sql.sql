declare
  l_sid int;
  l_cid int;
  l_mid int;
  l_id1 int;
  l_pid1 int;
  l_prid1 int;
  l_prid2 int;
  l_rid int;
  l_suid int;
  l_sex varchar2(1);
  l_iid int;
  l_l_mid int;

  cursor c_chrom is select cid, name from chromosomes;
  cursor c_ind is select iid from individuals;
  cursor c_mark is select mid from markers; 
  cursor c_l_mark is select lmid from l_markers; 
  l_i int;
  l_ext varchar2(10);
  l_cname varchar2(20);
begin
  delete from l_alleles;
  delete from l_markers;

  delete from u_alleles_log;
  delete from u_alleles;
  delete from u_marker_sets_log;
  delete from u_marker_sets;
  delete from u_markers;
  delete from u_variable_sets_log;
  delete from u_variables;

  delete from genotypes_log;
  delete from genotypes;
  delete from phenotypes_log;
  delete from phenotypes;
  delete from individuals_log;
  delete from individuals;
  delete from groups_log;
  delete from groups;
  delete from groupings_log;
  delete from groupings;
  delete from marker_sets_log;
  delete from marker_sets;
  delete from variable_sets_log;
  delete from variable_sets;
  delete from alleles_log;
  delete from alleles;
  delete from markers_log;
  delete from markers;
  delete from variables_log;
  delete from variables;  
  delete from sampling_units_log;
  delete from sampling_units;
  
  delete from roles_;
  delete from privileges_;
  delete from projects;
  delete from users;
  delete from chromosomes;
  delete from species;
  
  select species_seq.nextval into l_sid from dual;
  insert into species values(l_sid, 'HUMAN', null);
  
  select users_seq.nextval into l_id1 from dual;
  insert into users values(l_id1, 'TOBJ', 'PTOBJ', 'Tomas', 'E');
  
  select projects_seq.nextval into l_pid1 from dual;
  insert into projects values(l_pid1, 'PR001', null, 'E');
  
  select privileges_seq.nextval into l_prid1 from dual;
  insert into privileges_ values (l_prid1, 'PROJECT_ADM', null);
  select privileges_seq.nextval into l_prid2 from dual;
  insert into privileges_ values (l_prid2, 'TEST_PRIV', null);
  
  select roles_seq.nextval into l_rid from dual;
  insert into Roles_ values(l_rid, l_pid1, 'ADMIN', null);
  
  insert into r_prj_rol values(l_pid1, l_id1, l_rid);
  
  for l_i in 1..22 loop
    insert into chromosomes values(
	  chromosomes_seq.nextval, to_char(l_i), null, l_sid);
  end loop;
  insert into chromosomes values(
    chromosomes_seq.nextval, 'X', null, l_sid);
  insert into chromosomes values(
    chromosomes_seq.nextval, 'Y', null, l_sid);
	
  select sampling_units_seq.nextval into l_suid from dual;
  insert into sampling_units values( l_suid, 'SU001', null, 'E', l_sid, l_id1, sysdate);
  
  insert into r_prj_spc values(l_pid1, l_sid);
  insert into r_prj_su values(l_pid1, l_suid);



  open c_chrom;
    loop
	  fetch c_chrom into l_cid, l_cname;
	  if c_chrom%FOUND then
	    if l_cname IN('X', 'Y') then
		  l_ext := '0' || l_cname;
		else
		  if to_number(l_cname) < 10 then
		    l_ext := '0' || l_cname;
		  else
		    l_ext := l_cname;
		  end if;
		end if;
	    for l_i in 1..5 loop
		  insert into l_markers values(
		    l_markers_seq.nextval, 'MARK' || l_ext ||'0'|| to_char(l_i), 'ALIAS00' || to_char(l_i), 
			null, l_sid, l_cid, l_i*2.34);
		end loop;
	  else
	    close c_chrom;
		exit;
	  end if;
	end loop;
	
	open c_l_mark;
	loop
	  fetch c_l_mark into l_l_mid;
	  if c_l_mark%FOUND then
	    for l_i in 1..9 loop
		  insert into l_alleles values(
		      alleles_seq.nextval, 'ALLELE0' || to_char(l_i), null, l_l_mid);
		end loop; 
	  else 
	    close c_l_mark;
		exit;
	  end if;
	end loop;
	
	
	
	

  open c_chrom;
    loop
	  fetch c_chrom into l_cid, l_cname;
	  if c_chrom%FOUND then
	    if l_cname IN('X', 'Y') then
		  l_ext := '0' || l_cname;
		else
		  if to_number(l_cname) < 10 then
		    l_ext := '0' || l_cname;
		  else
		    l_ext := l_cname;
		  end if;
		end if;
	    for l_i in 1..5 loop
		  insert into markers values(
		    markers_seq.nextval, 'MARK' || l_ext ||'0'|| to_char(l_i), 'ALIAS00' || to_char(l_i), 
			null, l_suid, l_cid, 'p1', 'p2', 2.34, l_id1, sysdate);
		end loop;
	  else
	    close c_chrom;
		exit;
	  end if;
	end loop;
	for l_i in 1..1000 loop
	  if l_i < 10 then
	  	l_sex := 'U';
	    l_ext := '000' || to_char(l_i);
	  elsif l_i < 100 then
	    l_ext := '00' || to_char(l_i);
		l_sex := 'F';
	  elsif l_i < 1000 then
	    l_ext := '0' || to_char (l_i);
	  else 
	    l_ext := to_char(l_i);
		l_sex := 'M';
	  end if;
	  insert into individuals values(
	    individuals_seq.nextval, 'ID' || l_ext, 'ALIAS' || l_ext, null, null, l_sex, sysdate-2000 + l_i/24, 'E', l_suid, l_id1, sysdate, null);
	end loop;
	
	open c_mark;
	loop
	  fetch c_mark into l_mid;
	  if c_mark%FOUND then
	    for l_i in 1..9 loop
		  insert into alleles values(
		      alleles_seq.nextval, 'ALLELE0' || to_char(l_i), null, l_mid, l_id1, sysdate);
		end loop; 
	    open c_ind;
		loop
		  fetch c_ind into l_iid;
		  if c_ind%FOUND then
		    insert into genotypes values(
			    l_mid, l_iid, null, null, l_suid, 0, null, null, null, l_id1, sysdate, null);
		  else
		    close c_ind;
			exit;
		  end if;
		end loop;
	  else 
	    close c_mark;
		exit;
	  end if;
	end loop;
 
  	 
	

end;