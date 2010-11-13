
create or replace function gdbadm.To_Number_Else_Null(
    p_x	varchar) 
returns integer as $$
begin
  --return to_number(p_x);
  return cast(p_x as integer);
exception
  when others
    then return null;
end;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE function gdbadm.To_Positive_Number_Else_Null(
  p_x	varchar) 
returns integer as $$
DECLARE
 l_temp	integer;
begin
  l_temp:=to_number(p_x);
  
  if l_temp > 0 then
  	return l_temp;
  else
  	return null;
  end if;

exception
  when others
    then return null;
end;
$$ LANGUAGE plpgsql;
