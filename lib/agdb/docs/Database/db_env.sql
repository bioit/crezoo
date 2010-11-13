
create tablespace gdb_tables datafile 'F:\arexis\data\orcl\tables.dbf'
	   size 10M
	   autoextend on
	   next 500K
	   maxsize 250M;

create tablespace gdb_idx datafile 'F:\arexis\data\orcl\idx.dbf'
	   size 10M
	   autoextend on
	   next 500K
	   maxsize 250M;

create tablespace gdb_temp datafile 'F:\arexis\data\orcl\temp.dbf'
	   size 10M
	   autoextend on
	   next 500K
	   maxsize 250M;

create tablespace gdb_rb datafile 'F:\arexis\data\orcl\rb.dbf'
	   size 10M
	   autoextend on
	   next 500K
	   maxsize 250M;
	   
