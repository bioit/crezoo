
drop function Get_Parent;

create function Get_Parent(
  p_iid	  in	number,
  p_pstr  in	varchar2) return number as
  l_iid number;
  l_len number;
  l_par char;
begin
  l_iid := p_iid;
  l_len := length(p_pstr);

  for i in 1..l_len loop
    l_par := substr(p_pstr, i, 1);
    if l_par = 'M' then
      select mother into l_iid from individuals where iid = l_iid;
    elsif l_par = 'F' then
      select father into l_iid from individuals where iid = l_iid;
    else
      return null;
    end if;
  end loop;

  return l_iid;
exception
  when others
    then return null;
end;
/

------------------------------------------------------------

drop function Get_Variable;

create function Get_Variable(
  p_iid	  in	number,
  p_var  in	varchar2) return number as
  l_vid number;
begin
  select vid into l_vid from variables v, sampling_units s, individuals i
    where i.iid = p_iid
      and i.suid = s.suid
      and v.suid = s.suid
      and v.name = p_var;

  return l_vid;
exception
  when others
    then return null;
end;
/

------------------------------------------------------------

drop function Get_Marker;

create function Get_Marker(
  p_iid	  in	number,
  p_mrk  in	varchar2) return number as
  l_mid number;
begin
  select mid into l_mid from markers m, sampling_units s, individuals i
    where i.iid = p_iid
      and i.suid = s.suid
      and m.suid = s.suid
      and m.name = p_mrk;

  return l_mid;
exception
  when others
    then return null;
end;
/

------------------------------------------------------------

drop public synonym I_Identity;

drop function I_Identity;

create function I_Identity(
  p_iid	  in	number,
  p_pstr  in	varchar2) return varchar2 as
  l_iid number;
  l_val varchar2(11);
begin
  l_iid := Get_Parent(p_iid, p_pstr);

  select identity into l_val from individuals
    where iid = l_iid;

  return l_val;
exception
  when others
    then return null;
end;
/

create public synonym I_Identity for GdbAdm.I_Identity;

------------------------------------------------------------

drop public synonym I_Birth_Date;

drop function I_Birth_Date;

create function I_Birth_Date(
  p_iid	  in	number,
  p_pstr  in	varchar2) return date as
  l_iid number;
  l_val date;
begin
  l_iid := Get_Parent(p_iid, p_pstr);

  select birth_date into l_val from individuals
    where iid = l_iid;

  return l_val;
exception
  when others
    then return null;
end;
/

create public synonym I_Birth_Date for GdbAdm.I_Birth_Date;

------------------------------------------------------------

drop public synonym P_Value;

drop function P_Value;

create function P_Value(
  p_iid	  in	number,
  p_pstr  in	varchar2,
  p_var	  in	varchar2) return varchar2 as
  l_iid number;
  l_vid number;
  l_val varchar2(20);
begin
  l_iid := Get_Parent(p_iid, p_pstr);
  l_vid := Get_Variable(p_iid, p_var);

  select value into l_val from phenotypes
    where vid = l_vid
      and iid = l_iid;

  return l_val;
exception
  when others
    then return null;
end;
/

create public synonym P_Value for GdbAdm.P_Value;

------------------------------------------------------------

drop public synonym G_A1;

drop function G_A1;

create function G_A1(
  p_iid	  in	number,
  p_pstr  in	varchar2,
  p_mrk	  in	varchar2) return varchar2 as
  l_iid number;
  l_mid number;
  l_val varchar2(15);
begin
  l_iid := Get_Parent(p_iid, p_pstr);
  l_mid := Get_Marker(p_iid, p_mrk);

  select name into l_val from alleles a, genotypes g
    where g.mid = l_mid
      and g.iid = l_iid
      and a.aid = g.aid1;


  return l_val;
exception
  when others
    then return null;
end;
/

create public synonym G_A1 for GdbAdm.G_A1;
------------------------------------------------------------

drop public synonym G_A2;

drop function G_A2;

create function G_A2(
  p_iid	  in	number,
  p_pstr  in	varchar2,
  p_mrk	  in	varchar2) return varchar2 as
  l_iid number;
  l_mid number;
  l_val varchar2(15);
begin
  l_iid := Get_Parent(p_iid, p_pstr);
  l_mid := Get_Marker(p_iid, p_mrk);

  select name into l_val from alleles a, genotypes g
    where g.mid = l_mid
      and g.iid = l_iid
      and a.aid = g.aid2;

  return l_val;
exception
  when others
    then return null;
end;
/

create public synonym G_A2 for GdbAdm.G_A2;
