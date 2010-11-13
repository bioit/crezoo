
drop function To_Number_Else_Null;

create function To_Number_Else_Null(
  p_x	in	varchar2) return number as
begin
  return to_number(p_x);
exception
  when others
    then return null;
end;

/

drop function To_Positive_Number_Else_Null;



CREATE OR REPLACE function To_Positive_Number_Else_Null(
  p_x	in	varchar2) return number as

 l_temp	NUMBER;
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
/










