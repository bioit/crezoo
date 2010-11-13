--
-- PostgreSQL database dump
--

-- Started on 2010-07-06 04:09:17

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1042 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

CREATE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

--
-- TOC entry 730 (class 0 OID 0)
-- Name: chkpass; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE chkpass;


--
-- TOC entry 8 (class 1255 OID 59002)
-- Dependencies: 6 730
-- Name: chkpass_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION chkpass_in(cstring) RETURNS chkpass
    LANGUAGE c STRICT
    AS '$libdir/chkpass', 'chkpass_in';


--
-- TOC entry 20 (class 1255 OID 59003)
-- Dependencies: 6 730
-- Name: chkpass_out(chkpass); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION chkpass_out(chkpass) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/chkpass', 'chkpass_out';


--
-- TOC entry 729 (class 1247 OID 59001)
-- Dependencies: 6 8 20
-- Name: chkpass; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE chkpass (
    INTERNALLENGTH = 16,
    INPUT = chkpass_in,
    OUTPUT = chkpass_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 3122 (class 0 OID 0)
-- Dependencies: 729
-- Name: TYPE chkpass; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TYPE chkpass IS 'password type with checks';


--
-- TOC entry 733 (class 0 OID 0)
-- Name: cube; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE cube;


--
-- TOC entry 21 (class 1255 OID 59006)
-- Dependencies: 6 733
-- Name: cube_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_in(cstring) RETURNS cube
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_in';


--
-- TOC entry 22 (class 1255 OID 59007)
-- Dependencies: 6 733
-- Name: cube_out(cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_out(cube) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_out';


--
-- TOC entry 732 (class 1247 OID 59005)
-- Dependencies: 22 6 21
-- Name: cube; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE cube (
    INTERNALLENGTH = variable,
    INPUT = cube_in,
    OUTPUT = cube_out,
    ALIGNMENT = double,
    STORAGE = plain
);


--
-- TOC entry 3123 (class 0 OID 0)
-- Dependencies: 732
-- Name: TYPE cube; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TYPE cube IS 'multi-dimensional cube ''(FLOAT-1, FLOAT-2, ..., FLOAT-N), (FLOAT-1, FLOAT-2, ..., FLOAT-N)''';


--
-- TOC entry 735 (class 1247 OID 59011)
-- Dependencies: 6 2380
-- Name: dblink_pkey_results; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE dblink_pkey_results AS (
	"position" integer,
	colname text
);


--
-- TOC entry 738 (class 0 OID 0)
-- Name: gbtreekey16; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey16;


--
-- TOC entry 23 (class 1255 OID 59013)
-- Dependencies: 6 738
-- Name: gbtreekey16_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey16_in(cstring) RETURNS gbtreekey16
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_in';


--
-- TOC entry 24 (class 1255 OID 59014)
-- Dependencies: 6 738
-- Name: gbtreekey16_out(gbtreekey16); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey16_out(gbtreekey16) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_out';


--
-- TOC entry 737 (class 1247 OID 59012)
-- Dependencies: 24 23 6
-- Name: gbtreekey16; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey16 (
    INTERNALLENGTH = 16,
    INPUT = gbtreekey16_in,
    OUTPUT = gbtreekey16_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 741 (class 0 OID 0)
-- Name: gbtreekey32; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey32;


--
-- TOC entry 25 (class 1255 OID 59017)
-- Dependencies: 6 741
-- Name: gbtreekey32_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey32_in(cstring) RETURNS gbtreekey32
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_in';


--
-- TOC entry 26 (class 1255 OID 59018)
-- Dependencies: 6 741
-- Name: gbtreekey32_out(gbtreekey32); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey32_out(gbtreekey32) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_out';


--
-- TOC entry 740 (class 1247 OID 59016)
-- Dependencies: 26 25 6
-- Name: gbtreekey32; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey32 (
    INTERNALLENGTH = 32,
    INPUT = gbtreekey32_in,
    OUTPUT = gbtreekey32_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 744 (class 0 OID 0)
-- Name: gbtreekey4; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey4;


--
-- TOC entry 27 (class 1255 OID 59021)
-- Dependencies: 6 744
-- Name: gbtreekey4_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey4_in(cstring) RETURNS gbtreekey4
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_in';


--
-- TOC entry 28 (class 1255 OID 59022)
-- Dependencies: 6 744
-- Name: gbtreekey4_out(gbtreekey4); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey4_out(gbtreekey4) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_out';


--
-- TOC entry 743 (class 1247 OID 59020)
-- Dependencies: 27 28 6
-- Name: gbtreekey4; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey4 (
    INTERNALLENGTH = 4,
    INPUT = gbtreekey4_in,
    OUTPUT = gbtreekey4_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 747 (class 0 OID 0)
-- Name: gbtreekey8; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey8;


--
-- TOC entry 29 (class 1255 OID 59025)
-- Dependencies: 6 747
-- Name: gbtreekey8_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey8_in(cstring) RETURNS gbtreekey8
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_in';


--
-- TOC entry 30 (class 1255 OID 59026)
-- Dependencies: 6 747
-- Name: gbtreekey8_out(gbtreekey8); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey8_out(gbtreekey8) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_out';


--
-- TOC entry 746 (class 1247 OID 59024)
-- Dependencies: 30 29 6
-- Name: gbtreekey8; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey8 (
    INTERNALLENGTH = 8,
    INPUT = gbtreekey8_in,
    OUTPUT = gbtreekey8_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 750 (class 0 OID 0)
-- Name: gbtreekey_var; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey_var;


--
-- TOC entry 31 (class 1255 OID 59029)
-- Dependencies: 6 750
-- Name: gbtreekey_var_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey_var_in(cstring) RETURNS gbtreekey_var
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_in';


--
-- TOC entry 32 (class 1255 OID 59030)
-- Dependencies: 6 750
-- Name: gbtreekey_var_out(gbtreekey_var); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbtreekey_var_out(gbtreekey_var) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbtreekey_out';


--
-- TOC entry 749 (class 1247 OID 59028)
-- Dependencies: 32 6 31
-- Name: gbtreekey_var; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE gbtreekey_var (
    INTERNALLENGTH = variable,
    INPUT = gbtreekey_var_in,
    OUTPUT = gbtreekey_var_out,
    ALIGNMENT = int4,
    STORAGE = extended
);


--
-- TOC entry 753 (class 0 OID 0)
-- Name: gtrgm; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE gtrgm;


--
-- TOC entry 33 (class 1255 OID 59033)
-- Dependencies: 6 753
-- Name: gtrgm_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_in(cstring) RETURNS gtrgm
    LANGUAGE c STRICT
    AS '$libdir/pg_trgm', 'gtrgm_in';


--
-- TOC entry 34 (class 1255 OID 59034)
-- Dependencies: 6 753
-- Name: gtrgm_out(gtrgm); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_out(gtrgm) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/pg_trgm', 'gtrgm_out';


--
-- TOC entry 752 (class 1247 OID 59032)
-- Dependencies: 6 33 34
-- Name: gtrgm; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE gtrgm (
    INTERNALLENGTH = variable,
    INPUT = gtrgm_in,
    OUTPUT = gtrgm_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 756 (class 0 OID 0)
-- Name: intbig_gkey; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE intbig_gkey;


--
-- TOC entry 35 (class 1255 OID 59037)
-- Dependencies: 6 756
-- Name: _intbig_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _intbig_in(cstring) RETURNS intbig_gkey
    LANGUAGE c STRICT
    AS '$libdir/_int', '_intbig_in';


--
-- TOC entry 36 (class 1255 OID 59038)
-- Dependencies: 6 756
-- Name: _intbig_out(intbig_gkey); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _intbig_out(intbig_gkey) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/_int', '_intbig_out';


--
-- TOC entry 755 (class 1247 OID 59036)
-- Dependencies: 35 36 6
-- Name: intbig_gkey; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE intbig_gkey (
    INTERNALLENGTH = variable,
    INPUT = _intbig_in,
    OUTPUT = _intbig_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 758 (class 1247 OID 59040)
-- Dependencies: 6
-- Name: lo; Type: DOMAIN; Schema: public; Owner: -
--

CREATE DOMAIN lo AS oid;


--
-- TOC entry 760 (class 0 OID 0)
-- Name: lquery; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE lquery;


--
-- TOC entry 37 (class 1255 OID 59042)
-- Dependencies: 6 760
-- Name: lquery_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lquery_in(cstring) RETURNS lquery
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'lquery_in';


--
-- TOC entry 38 (class 1255 OID 59043)
-- Dependencies: 6 760
-- Name: lquery_out(lquery); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lquery_out(lquery) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'lquery_out';


--
-- TOC entry 759 (class 1247 OID 59041)
-- Dependencies: 6 37 38
-- Name: lquery; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE lquery (
    INTERNALLENGTH = variable,
    INPUT = lquery_in,
    OUTPUT = lquery_out,
    ALIGNMENT = int4,
    STORAGE = extended
);


--
-- TOC entry 763 (class 0 OID 0)
-- Name: ltree; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE ltree;


--
-- TOC entry 39 (class 1255 OID 59046)
-- Dependencies: 6 763
-- Name: ltree_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_in(cstring) RETURNS ltree
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'ltree_in';


--
-- TOC entry 40 (class 1255 OID 59047)
-- Dependencies: 6 763
-- Name: ltree_out(ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_out(ltree) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'ltree_out';


--
-- TOC entry 762 (class 1247 OID 59045)
-- Dependencies: 6 39 40
-- Name: ltree; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE ltree (
    INTERNALLENGTH = variable,
    INPUT = ltree_in,
    OUTPUT = ltree_out,
    ALIGNMENT = int4,
    STORAGE = extended
);


--
-- TOC entry 766 (class 0 OID 0)
-- Name: ltree_gist; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE ltree_gist;


--
-- TOC entry 41 (class 1255 OID 59050)
-- Dependencies: 6 766
-- Name: ltree_gist_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_gist_in(cstring) RETURNS ltree_gist
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'ltree_gist_in';


--
-- TOC entry 42 (class 1255 OID 59051)
-- Dependencies: 6 766
-- Name: ltree_gist_out(ltree_gist); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_gist_out(ltree_gist) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'ltree_gist_out';


--
-- TOC entry 765 (class 1247 OID 59049)
-- Dependencies: 6 42 41
-- Name: ltree_gist; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE ltree_gist (
    INTERNALLENGTH = variable,
    INPUT = ltree_gist_in,
    OUTPUT = ltree_gist_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 769 (class 0 OID 0)
-- Name: ltxtquery; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE ltxtquery;


--
-- TOC entry 43 (class 1255 OID 59054)
-- Dependencies: 6 769
-- Name: ltxtq_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltxtq_in(cstring) RETURNS ltxtquery
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'ltxtq_in';


--
-- TOC entry 44 (class 1255 OID 59055)
-- Dependencies: 6 769
-- Name: ltxtq_out(ltxtquery); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltxtq_out(ltxtquery) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'ltxtq_out';


--
-- TOC entry 768 (class 1247 OID 59053)
-- Dependencies: 43 44 6
-- Name: ltxtquery; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE ltxtquery (
    INTERNALLENGTH = variable,
    INPUT = ltxtq_in,
    OUTPUT = ltxtq_out,
    ALIGNMENT = int4,
    STORAGE = extended
);


--
-- TOC entry 771 (class 1247 OID 59059)
-- Dependencies: 6 2381
-- Name: pgstattuple_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE pgstattuple_type AS (
	table_len bigint,
	tuple_count bigint,
	tuple_len bigint,
	tuple_percent double precision,
	dead_tuple_count bigint,
	dead_tuple_len bigint,
	dead_tuple_percent double precision,
	free_space bigint,
	free_percent double precision
);


--
-- TOC entry 774 (class 0 OID 0)
-- Name: query_int; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE query_int;


--
-- TOC entry 45 (class 1255 OID 59061)
-- Dependencies: 6 774
-- Name: bqarr_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION bqarr_in(cstring) RETURNS query_int
    LANGUAGE c STRICT
    AS '$libdir/_int', 'bqarr_in';


--
-- TOC entry 46 (class 1255 OID 59062)
-- Dependencies: 6 774
-- Name: bqarr_out(query_int); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION bqarr_out(query_int) RETURNS cstring
    LANGUAGE c STRICT
    AS '$libdir/_int', 'bqarr_out';


--
-- TOC entry 773 (class 1247 OID 59060)
-- Dependencies: 46 45 6
-- Name: query_int; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE query_int (
    INTERNALLENGTH = variable,
    INPUT = bqarr_in,
    OUTPUT = bqarr_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 777 (class 0 OID 0)
-- Name: seg; Type: SHELL TYPE; Schema: public; Owner: -
--

CREATE TYPE seg;


--
-- TOC entry 47 (class 1255 OID 59065)
-- Dependencies: 6 777
-- Name: seg_in(cstring); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_in(cstring) RETURNS seg
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_in';


--
-- TOC entry 48 (class 1255 OID 59066)
-- Dependencies: 6 777
-- Name: seg_out(seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_out(seg) RETURNS cstring
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_out';


--
-- TOC entry 776 (class 1247 OID 59064)
-- Dependencies: 48 6 47
-- Name: seg; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE seg (
    INTERNALLENGTH = 12,
    INPUT = seg_in,
    OUTPUT = seg_out,
    ALIGNMENT = int4,
    STORAGE = plain
);


--
-- TOC entry 3124 (class 0 OID 0)
-- Dependencies: 776
-- Name: TYPE seg; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TYPE seg IS 'floating point interval ''FLOAT .. FLOAT'', ''.. FLOAT'', ''FLOAT ..'' or ''FLOAT''';


--
-- TOC entry 779 (class 1247 OID 59070)
-- Dependencies: 6 2382
-- Name: statinfo; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE statinfo AS (
	word text,
	ndoc integer,
	nentry integer
);


--
-- TOC entry 781 (class 1247 OID 59073)
-- Dependencies: 6 2383
-- Name: tablefunc_crosstab_2; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE tablefunc_crosstab_2 AS (
	row_name text,
	category_1 text,
	category_2 text
);


--
-- TOC entry 783 (class 1247 OID 59076)
-- Dependencies: 6 2384
-- Name: tablefunc_crosstab_3; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE tablefunc_crosstab_3 AS (
	row_name text,
	category_1 text,
	category_2 text,
	category_3 text
);


--
-- TOC entry 785 (class 1247 OID 59079)
-- Dependencies: 6 2385
-- Name: tablefunc_crosstab_4; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE tablefunc_crosstab_4 AS (
	row_name text,
	category_1 text,
	category_2 text,
	category_3 text,
	category_4 text
);


--
-- TOC entry 787 (class 1247 OID 59082)
-- Dependencies: 6 2386
-- Name: tokenout; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE tokenout AS (
	tokid integer,
	token text
);


--
-- TOC entry 789 (class 1247 OID 59085)
-- Dependencies: 6 2387
-- Name: tokentype; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE tokentype AS (
	tokid integer,
	alias text,
	descr text
);


--
-- TOC entry 49 (class 1255 OID 59086)
-- Dependencies: 6
-- Name: _get_parser_from_curcfg(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _get_parser_from_curcfg() RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $$ select prs_name from pg_ts_cfg where oid = show_curcfg() $$;


--
-- TOC entry 50 (class 1255 OID 59087)
-- Dependencies: 6
-- Name: _int_contained(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _int_contained(integer[], integer[]) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/_int', '_int_contained';


--
-- TOC entry 3125 (class 0 OID 0)
-- Dependencies: 50
-- Name: FUNCTION _int_contained(integer[], integer[]); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION _int_contained(integer[], integer[]) IS 'contained in';


--
-- TOC entry 51 (class 1255 OID 59088)
-- Dependencies: 6
-- Name: _int_contains(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _int_contains(integer[], integer[]) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/_int', '_int_contains';


--
-- TOC entry 3126 (class 0 OID 0)
-- Dependencies: 51
-- Name: FUNCTION _int_contains(integer[], integer[]); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION _int_contains(integer[], integer[]) IS 'contains';


--
-- TOC entry 52 (class 1255 OID 59089)
-- Dependencies: 6
-- Name: _int_different(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _int_different(integer[], integer[]) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/_int', '_int_different';


--
-- TOC entry 3127 (class 0 OID 0)
-- Dependencies: 52
-- Name: FUNCTION _int_different(integer[], integer[]); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION _int_different(integer[], integer[]) IS 'different';


--
-- TOC entry 53 (class 1255 OID 59090)
-- Dependencies: 6
-- Name: _int_inter(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _int_inter(integer[], integer[]) RETURNS integer[]
    LANGUAGE c STRICT
    AS '$libdir/_int', '_int_inter';


--
-- TOC entry 54 (class 1255 OID 59091)
-- Dependencies: 6
-- Name: _int_overlap(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _int_overlap(integer[], integer[]) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/_int', '_int_overlap';


--
-- TOC entry 3128 (class 0 OID 0)
-- Dependencies: 54
-- Name: FUNCTION _int_overlap(integer[], integer[]); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION _int_overlap(integer[], integer[]) IS 'overlaps';


--
-- TOC entry 55 (class 1255 OID 59092)
-- Dependencies: 6
-- Name: _int_same(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _int_same(integer[], integer[]) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/_int', '_int_same';


--
-- TOC entry 3129 (class 0 OID 0)
-- Dependencies: 55
-- Name: FUNCTION _int_same(integer[], integer[]); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION _int_same(integer[], integer[]) IS 'same as';


--
-- TOC entry 56 (class 1255 OID 59093)
-- Dependencies: 6
-- Name: _int_union(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _int_union(integer[], integer[]) RETURNS integer[]
    LANGUAGE c STRICT
    AS '$libdir/_int', '_int_union';


--
-- TOC entry 57 (class 1255 OID 59094)
-- Dependencies: 6 764 761
-- Name: _lt_q_regex(ltree[], lquery[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _lt_q_regex(ltree[], lquery[]) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_lt_q_regex';


--
-- TOC entry 58 (class 1255 OID 59095)
-- Dependencies: 764 761 6
-- Name: _lt_q_rregex(lquery[], ltree[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _lt_q_rregex(lquery[], ltree[]) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_lt_q_rregex';


--
-- TOC entry 59 (class 1255 OID 59096)
-- Dependencies: 6 759 762 764
-- Name: _ltq_extract_regex(ltree[], lquery); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltq_extract_regex(ltree[], lquery) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltq_extract_regex';


--
-- TOC entry 60 (class 1255 OID 59097)
-- Dependencies: 764 759 6
-- Name: _ltq_regex(ltree[], lquery); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltq_regex(ltree[], lquery) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltq_regex';


--
-- TOC entry 61 (class 1255 OID 59098)
-- Dependencies: 6 764 759
-- Name: _ltq_rregex(lquery, ltree[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltq_rregex(lquery, ltree[]) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltq_rregex';


--
-- TOC entry 62 (class 1255 OID 59099)
-- Dependencies: 6
-- Name: _ltree_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/ltree', '_ltree_compress';


--
-- TOC entry 63 (class 1255 OID 59100)
-- Dependencies: 6
-- Name: _ltree_consistent(internal, internal, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_consistent(internal, internal, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/ltree', '_ltree_consistent';


--
-- TOC entry 64 (class 1255 OID 59101)
-- Dependencies: 762 6 762 764
-- Name: _ltree_extract_isparent(ltree[], ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_extract_isparent(ltree[], ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltree_extract_isparent';


--
-- TOC entry 65 (class 1255 OID 59102)
-- Dependencies: 764 6 762 762
-- Name: _ltree_extract_risparent(ltree[], ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_extract_risparent(ltree[], ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltree_extract_risparent';


--
-- TOC entry 66 (class 1255 OID 59103)
-- Dependencies: 764 6 762
-- Name: _ltree_isparent(ltree[], ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_isparent(ltree[], ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltree_isparent';


--
-- TOC entry 67 (class 1255 OID 59104)
-- Dependencies: 6
-- Name: _ltree_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/ltree', '_ltree_penalty';


--
-- TOC entry 68 (class 1255 OID 59105)
-- Dependencies: 6
-- Name: _ltree_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/ltree', '_ltree_picksplit';


--
-- TOC entry 69 (class 1255 OID 59106)
-- Dependencies: 762 6 764
-- Name: _ltree_r_isparent(ltree, ltree[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_r_isparent(ltree, ltree[]) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltree_r_isparent';


--
-- TOC entry 70 (class 1255 OID 59107)
-- Dependencies: 762 6 764
-- Name: _ltree_r_risparent(ltree, ltree[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_r_risparent(ltree, ltree[]) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltree_r_risparent';


--
-- TOC entry 71 (class 1255 OID 59108)
-- Dependencies: 764 6 762
-- Name: _ltree_risparent(ltree[], ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_risparent(ltree[], ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltree_risparent';


--
-- TOC entry 72 (class 1255 OID 59109)
-- Dependencies: 6
-- Name: _ltree_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/ltree', '_ltree_same';


--
-- TOC entry 73 (class 1255 OID 59110)
-- Dependencies: 6
-- Name: _ltree_union(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltree_union(internal, internal) RETURNS integer
    LANGUAGE c
    AS '$libdir/ltree', '_ltree_union';


--
-- TOC entry 74 (class 1255 OID 59111)
-- Dependencies: 764 6 768
-- Name: _ltxtq_exec(ltree[], ltxtquery); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltxtq_exec(ltree[], ltxtquery) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltxtq_exec';


--
-- TOC entry 75 (class 1255 OID 59112)
-- Dependencies: 764 6 762 768
-- Name: _ltxtq_extract_exec(ltree[], ltxtquery); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltxtq_extract_exec(ltree[], ltxtquery) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltxtq_extract_exec';


--
-- TOC entry 76 (class 1255 OID 59113)
-- Dependencies: 768 6 764
-- Name: _ltxtq_rexec(ltxtquery, ltree[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION _ltxtq_rexec(ltxtquery, ltree[]) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_ltxtq_rexec';


--
-- TOC entry 83 (class 1255 OID 59114)
-- Dependencies: 6 1042
-- Name: addgeometrycolumn(character varying, character varying, character varying, character varying, integer, character varying, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION addgeometrycolumn(character varying, character varying, character varying, character varying, integer, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	catalog_name alias for $1;
	schema_name alias for $2;
	table_name alias for $3;
	column_name alias for $4;
	new_srid alias for $5;
	new_type alias for $6;
	new_dim alias for $7;

	rec RECORD;
	schema_ok bool;
	real_schema name;

	fixgeomres text;

BEGIN

	IF ( not ( (new_type ='GEOMETRY') or
		   (new_type ='GEOMETRYCOLLECTION') or
		   (new_type ='POINT') or 
		   (new_type ='MULTIPOINT') or
		   (new_type ='POLYGON') or
		   (new_type ='MULTIPOLYGON') or
		   (new_type ='LINESTRING') or
		   (new_type ='MULTILINESTRING') or
		   (new_type ='GEOMETRYCOLLECTIONM') or
		   (new_type ='POINTM') or 
		   (new_type ='MULTIPOINTM') or
		   (new_type ='POLYGONM') or
		   (new_type ='MULTIPOLYGONM') or
		   (new_type ='LINESTRINGM') or
		   (new_type ='MULTILINESTRINGM')) )
	THEN
		RAISE EXCEPTION 'Invalid type name - valid ones are: 
			GEOMETRY, GEOMETRYCOLLECTION, POINT, 
			MULTIPOINT, POLYGON, MULTIPOLYGON, 
			LINESTRING, MULTILINESTRING,
			GEOMETRYCOLLECTIONM, POINTM, 
			MULTIPOINTM, POLYGONM, MULTIPOLYGONM, 
			LINESTRINGM, or MULTILINESTRINGM ';
		return 'fail';
	END IF;

	IF ( (new_dim >4) or (new_dim <0) ) THEN
		RAISE EXCEPTION 'invalid dimension';
		return 'fail';
	END IF;

	IF ( (new_type LIKE '%M') and (new_dim!=3) ) THEN

		RAISE EXCEPTION 'TypeM needs 3 dimensions';
		return 'fail';
	END IF;


	IF ( schema_name != '' ) THEN
		schema_ok = 'f';
		FOR rec IN SELECT nspname FROM pg_namespace WHERE text(nspname) = schema_name LOOP
			schema_ok := 't';
		END LOOP;

		if ( schema_ok <> 't' ) THEN
			RAISE NOTICE 'Invalid schema name - using current_schema()';
			SELECT current_schema() into real_schema;
		ELSE
			real_schema = schema_name;
		END IF;

	ELSE
		SELECT current_schema() into real_schema;
	END IF;



	-- Add geometry column

	EXECUTE 'ALTER TABLE ' ||

		quote_ident(real_schema) || '.' || quote_ident(table_name)

		|| ' ADD COLUMN ' || quote_ident(column_name) || 
		' geometry ';


	-- Delete stale record in geometry_column (if any)

	EXECUTE 'DELETE FROM geometry_columns WHERE
		f_table_catalog = ' || quote_literal('') || 
		' AND f_table_schema = ' ||

		quote_literal(real_schema) || 

		' AND f_table_name = ' || quote_literal(table_name) ||
		' AND f_geometry_column = ' || quote_literal(column_name);


	-- Add record in geometry_column 

	EXECUTE 'INSERT INTO geometry_columns VALUES (' ||
		quote_literal('') || ',' ||

		quote_literal(real_schema) || ',' ||

		quote_literal(table_name) || ',' ||
		quote_literal(column_name) || ',' ||
		new_dim || ',' || new_srid || ',' ||
		quote_literal(new_type) || ')';

	-- Add table checks

	EXECUTE 'ALTER TABLE ' || 

		quote_ident(real_schema) || '.' || quote_ident(table_name)

		|| ' ADD CONSTRAINT ' 
		|| quote_ident('enforce_srid_' || column_name)
		|| ' CHECK (SRID(' || quote_ident(column_name) ||
		') = ' || new_srid || ')' ;

	EXECUTE 'ALTER TABLE ' || 

		quote_ident(real_schema) || '.' || quote_ident(table_name)

		|| ' ADD CONSTRAINT '
		|| quote_ident('enforce_dims_' || column_name)
		|| ' CHECK (ndims(' || quote_ident(column_name) ||
		') = ' || new_dim || ')' ;

	IF (not(new_type = 'GEOMETRY')) THEN
		EXECUTE 'ALTER TABLE ' || 

		quote_ident(real_schema) || '.' || quote_ident(table_name)

		|| ' ADD CONSTRAINT '
		|| quote_ident('enforce_geotype_' || column_name)
		|| ' CHECK (geometrytype(' ||
		quote_ident(column_name) || ')=' ||
		quote_literal(new_type) || ' OR (' ||
		quote_ident(column_name) || ') is null)';
	END IF;

	SELECT fix_geometry_columns() INTO fixgeomres;

	return 

		real_schema || '.' || 

		table_name || '.' || column_name ||
		' SRID:' || new_srid ||
		' TYPE:' || new_type || 
		' DIMS:' || new_dim || '
 ' ||
		'geometry_column ' || fixgeomres;
END;
$_$;


--
-- TOC entry 84 (class 1255 OID 59115)
-- Dependencies: 6 1042
-- Name: addgeometrycolumn(character varying, character varying, character varying, integer, character varying, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION addgeometrycolumn(character varying, character varying, character varying, integer, character varying, integer) RETURNS text
    LANGUAGE plpgsql STABLE STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT AddGeometryColumn('',$1,$2,$3,$4,$5,$6) into ret;
	RETURN ret;
END;
$_$;


--
-- TOC entry 85 (class 1255 OID 59116)
-- Dependencies: 1042 6
-- Name: addgeometrycolumn(character varying, character varying, integer, character varying, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION addgeometrycolumn(character varying, character varying, integer, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT AddGeometryColumn('','',$1,$2,$3,$4,$5) into ret;
	RETURN ret;
END;
$_$;


--
-- TOC entry 86 (class 1255 OID 59117)
-- Dependencies: 6
-- Name: armor(bytea); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION armor(bytea) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_armor';


--
-- TOC entry 87 (class 1255 OID 59118)
-- Dependencies: 6
-- Name: autoinc(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION autoinc() RETURNS trigger
    LANGUAGE c
    AS '$libdir/autoinc', 'autoinc';


--
-- TOC entry 88 (class 1255 OID 59119)
-- Dependencies: 773 6
-- Name: boolop(integer[], query_int); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION boolop(integer[], query_int) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/_int', 'boolop';


--
-- TOC entry 3130 (class 0 OID 0)
-- Dependencies: 88
-- Name: FUNCTION boolop(integer[], query_int); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION boolop(integer[], query_int) IS 'boolean operation with array';


--
-- TOC entry 89 (class 1255 OID 59120)
-- Dependencies: 6
-- Name: check_foreign_key(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION check_foreign_key() RETURNS trigger
    LANGUAGE c
    AS '$libdir/refint', 'check_foreign_key';


--
-- TOC entry 90 (class 1255 OID 59121)
-- Dependencies: 6
-- Name: check_primary_key(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION check_primary_key() RETURNS trigger
    LANGUAGE c
    AS '$libdir/refint', 'check_primary_key';


--
-- TOC entry 91 (class 1255 OID 59122)
-- Dependencies: 6
-- Name: connectby(text, text, text, text, integer, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION connectby(text, text, text, text, integer, text) RETURNS SETOF record
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'connectby_text';


--
-- TOC entry 92 (class 1255 OID 59123)
-- Dependencies: 6
-- Name: connectby(text, text, text, text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION connectby(text, text, text, text, integer) RETURNS SETOF record
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'connectby_text';


--
-- TOC entry 93 (class 1255 OID 59124)
-- Dependencies: 6
-- Name: connectby(text, text, text, text, text, integer, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION connectby(text, text, text, text, text, integer, text) RETURNS SETOF record
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'connectby_text_serial';


--
-- TOC entry 94 (class 1255 OID 59125)
-- Dependencies: 6
-- Name: connectby(text, text, text, text, text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION connectby(text, text, text, text, text, integer) RETURNS SETOF record
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'connectby_text_serial';


--
-- TOC entry 95 (class 1255 OID 59126)
-- Dependencies: 6
-- Name: crosstab(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION crosstab(text) RETURNS SETOF record
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'crosstab';


--
-- TOC entry 96 (class 1255 OID 59127)
-- Dependencies: 6
-- Name: crosstab(text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION crosstab(text, integer) RETURNS SETOF record
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'crosstab';


--
-- TOC entry 97 (class 1255 OID 59128)
-- Dependencies: 6
-- Name: crosstab(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION crosstab(text, text) RETURNS SETOF record
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'crosstab_hash';


--
-- TOC entry 98 (class 1255 OID 59129)
-- Dependencies: 6 781
-- Name: crosstab2(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION crosstab2(text) RETURNS SETOF tablefunc_crosstab_2
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'crosstab';


--
-- TOC entry 99 (class 1255 OID 59130)
-- Dependencies: 6 783
-- Name: crosstab3(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION crosstab3(text) RETURNS SETOF tablefunc_crosstab_3
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'crosstab';


--
-- TOC entry 100 (class 1255 OID 59131)
-- Dependencies: 6 785
-- Name: crosstab4(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION crosstab4(text) RETURNS SETOF tablefunc_crosstab_4
    LANGUAGE c STABLE STRICT
    AS '$libdir/tablefunc', 'crosstab';


--
-- TOC entry 101 (class 1255 OID 59132)
-- Dependencies: 6
-- Name: crypt(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION crypt(text, text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_crypt';


--
-- TOC entry 102 (class 1255 OID 59133)
-- Dependencies: 6 732
-- Name: cube(double precision); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube(double precision) RETURNS cube
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_f8';


--
-- TOC entry 103 (class 1255 OID 59134)
-- Dependencies: 6 732
-- Name: cube(double precision, double precision); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube(double precision, double precision) RETURNS cube
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_f8_f8';


--
-- TOC entry 104 (class 1255 OID 59135)
-- Dependencies: 732 732 6
-- Name: cube(cube, double precision); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube(cube, double precision) RETURNS cube
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_c_f8';


--
-- TOC entry 105 (class 1255 OID 59136)
-- Dependencies: 732 732 6
-- Name: cube(cube, double precision, double precision); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube(cube, double precision, double precision) RETURNS cube
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_c_f8_f8';


--
-- TOC entry 106 (class 1255 OID 59137)
-- Dependencies: 6 732 732
-- Name: cube_cmp(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_cmp(cube, cube) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_cmp';


--
-- TOC entry 3131 (class 0 OID 0)
-- Dependencies: 106
-- Name: FUNCTION cube_cmp(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_cmp(cube, cube) IS 'btree comparison function';


--
-- TOC entry 107 (class 1255 OID 59138)
-- Dependencies: 732 732 6
-- Name: cube_contained(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_contained(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_contained';


--
-- TOC entry 3132 (class 0 OID 0)
-- Dependencies: 107
-- Name: FUNCTION cube_contained(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_contained(cube, cube) IS 'contained in';


--
-- TOC entry 108 (class 1255 OID 59139)
-- Dependencies: 732 6 732
-- Name: cube_contains(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_contains(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_contains';


--
-- TOC entry 3133 (class 0 OID 0)
-- Dependencies: 108
-- Name: FUNCTION cube_contains(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_contains(cube, cube) IS 'contains';


--
-- TOC entry 109 (class 1255 OID 59140)
-- Dependencies: 6 732
-- Name: cube_dim(cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_dim(cube) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_dim';


--
-- TOC entry 110 (class 1255 OID 59141)
-- Dependencies: 6 732 732
-- Name: cube_distance(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_distance(cube, cube) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_distance';


--
-- TOC entry 111 (class 1255 OID 59142)
-- Dependencies: 732 6 732
-- Name: cube_enlarge(cube, double precision, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_enlarge(cube, double precision, integer) RETURNS cube
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_enlarge';


--
-- TOC entry 112 (class 1255 OID 59143)
-- Dependencies: 6 732 732
-- Name: cube_eq(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_eq(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_eq';


--
-- TOC entry 3134 (class 0 OID 0)
-- Dependencies: 112
-- Name: FUNCTION cube_eq(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_eq(cube, cube) IS 'same as';


--
-- TOC entry 113 (class 1255 OID 59144)
-- Dependencies: 6 732 732
-- Name: cube_ge(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_ge(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_ge';


--
-- TOC entry 3135 (class 0 OID 0)
-- Dependencies: 113
-- Name: FUNCTION cube_ge(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_ge(cube, cube) IS 'greater than or equal to';


--
-- TOC entry 114 (class 1255 OID 59145)
-- Dependencies: 732 6 732
-- Name: cube_gt(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_gt(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_gt';


--
-- TOC entry 3136 (class 0 OID 0)
-- Dependencies: 114
-- Name: FUNCTION cube_gt(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_gt(cube, cube) IS 'greater than';


--
-- TOC entry 77 (class 1255 OID 59146)
-- Dependencies: 6 732 732 732
-- Name: cube_inter(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_inter(cube, cube) RETURNS cube
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_inter';


--
-- TOC entry 78 (class 1255 OID 59147)
-- Dependencies: 732 6
-- Name: cube_is_point(cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_is_point(cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_is_point';


--
-- TOC entry 79 (class 1255 OID 59148)
-- Dependencies: 732 6 732
-- Name: cube_le(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_le(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_le';


--
-- TOC entry 3137 (class 0 OID 0)
-- Dependencies: 79
-- Name: FUNCTION cube_le(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_le(cube, cube) IS 'lower than or equal to';


--
-- TOC entry 80 (class 1255 OID 59149)
-- Dependencies: 732 6
-- Name: cube_ll_coord(cube, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_ll_coord(cube, integer) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_ll_coord';


--
-- TOC entry 81 (class 1255 OID 59150)
-- Dependencies: 6 732 732
-- Name: cube_lt(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_lt(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_lt';


--
-- TOC entry 3138 (class 0 OID 0)
-- Dependencies: 81
-- Name: FUNCTION cube_lt(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_lt(cube, cube) IS 'lower than';


--
-- TOC entry 82 (class 1255 OID 59151)
-- Dependencies: 732 732 6
-- Name: cube_ne(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_ne(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_ne';


--
-- TOC entry 3139 (class 0 OID 0)
-- Dependencies: 82
-- Name: FUNCTION cube_ne(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_ne(cube, cube) IS 'different';


--
-- TOC entry 115 (class 1255 OID 59152)
-- Dependencies: 6 732 732
-- Name: cube_overlap(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_overlap(cube, cube) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_overlap';


--
-- TOC entry 3140 (class 0 OID 0)
-- Dependencies: 115
-- Name: FUNCTION cube_overlap(cube, cube); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION cube_overlap(cube, cube) IS 'overlaps';


--
-- TOC entry 116 (class 1255 OID 59153)
-- Dependencies: 6 732
-- Name: cube_size(cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_size(cube) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_size';


--
-- TOC entry 117 (class 1255 OID 59154)
-- Dependencies: 732 732 732 6
-- Name: cube_union(cube, cube); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_union(cube, cube) RETURNS cube
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_union';


--
-- TOC entry 118 (class 1255 OID 59155)
-- Dependencies: 732 6
-- Name: cube_ur_coord(cube, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION cube_ur_coord(cube, integer) RETURNS double precision
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/cube', 'cube_ur_coord';


--
-- TOC entry 119 (class 1255 OID 59156)
-- Dependencies: 6
-- Name: dblink(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink(text, text) RETURNS SETOF record
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_record';


--
-- TOC entry 120 (class 1255 OID 59157)
-- Dependencies: 6
-- Name: dblink(text, text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink(text, text, boolean) RETURNS SETOF record
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_record';


--
-- TOC entry 121 (class 1255 OID 59158)
-- Dependencies: 6
-- Name: dblink(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink(text) RETURNS SETOF record
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_record';


--
-- TOC entry 122 (class 1255 OID 59159)
-- Dependencies: 6
-- Name: dblink(text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink(text, boolean) RETURNS SETOF record
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_record';


--
-- TOC entry 123 (class 1255 OID 59160)
-- Dependencies: 6
-- Name: dblink_build_sql_delete(text, int2vector, integer, text[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_build_sql_delete(text, int2vector, integer, text[]) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_build_sql_delete';


--
-- TOC entry 124 (class 1255 OID 59161)
-- Dependencies: 6
-- Name: dblink_build_sql_insert(text, int2vector, integer, text[], text[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_build_sql_insert(text, int2vector, integer, text[], text[]) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_build_sql_insert';


--
-- TOC entry 125 (class 1255 OID 59162)
-- Dependencies: 6
-- Name: dblink_build_sql_update(text, int2vector, integer, text[], text[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_build_sql_update(text, int2vector, integer, text[], text[]) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_build_sql_update';


--
-- TOC entry 126 (class 1255 OID 59163)
-- Dependencies: 6
-- Name: dblink_close(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_close(text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_close';


--
-- TOC entry 127 (class 1255 OID 59164)
-- Dependencies: 6
-- Name: dblink_close(text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_close(text, boolean) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_close';


--
-- TOC entry 128 (class 1255 OID 59165)
-- Dependencies: 6
-- Name: dblink_close(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_close(text, text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_close';


--
-- TOC entry 129 (class 1255 OID 59166)
-- Dependencies: 6
-- Name: dblink_close(text, text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_close(text, text, boolean) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_close';


--
-- TOC entry 130 (class 1255 OID 59167)
-- Dependencies: 6
-- Name: dblink_connect(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_connect(text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_connect';


--
-- TOC entry 131 (class 1255 OID 59168)
-- Dependencies: 6
-- Name: dblink_connect(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_connect(text, text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_connect';


--
-- TOC entry 132 (class 1255 OID 59169)
-- Dependencies: 6
-- Name: dblink_current_query(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_current_query() RETURNS text
    LANGUAGE c
    AS '$libdir/dblink', 'dblink_current_query';


--
-- TOC entry 133 (class 1255 OID 59170)
-- Dependencies: 6
-- Name: dblink_disconnect(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_disconnect() RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_disconnect';


--
-- TOC entry 134 (class 1255 OID 59171)
-- Dependencies: 6
-- Name: dblink_disconnect(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_disconnect(text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_disconnect';


--
-- TOC entry 135 (class 1255 OID 59172)
-- Dependencies: 6
-- Name: dblink_exec(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_exec(text, text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_exec';


--
-- TOC entry 136 (class 1255 OID 59173)
-- Dependencies: 6
-- Name: dblink_exec(text, text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_exec(text, text, boolean) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_exec';


--
-- TOC entry 137 (class 1255 OID 59174)
-- Dependencies: 6
-- Name: dblink_exec(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_exec(text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_exec';


--
-- TOC entry 138 (class 1255 OID 59175)
-- Dependencies: 6
-- Name: dblink_exec(text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_exec(text, boolean) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_exec';


--
-- TOC entry 139 (class 1255 OID 59176)
-- Dependencies: 6
-- Name: dblink_fetch(text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_fetch(text, integer) RETURNS SETOF record
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_fetch';


--
-- TOC entry 140 (class 1255 OID 59177)
-- Dependencies: 6
-- Name: dblink_fetch(text, integer, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_fetch(text, integer, boolean) RETURNS SETOF record
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_fetch';


--
-- TOC entry 141 (class 1255 OID 59178)
-- Dependencies: 6
-- Name: dblink_fetch(text, text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_fetch(text, text, integer) RETURNS SETOF record
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_fetch';


--
-- TOC entry 142 (class 1255 OID 59179)
-- Dependencies: 6
-- Name: dblink_fetch(text, text, integer, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_fetch(text, text, integer, boolean) RETURNS SETOF record
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_fetch';


--
-- TOC entry 143 (class 1255 OID 59180)
-- Dependencies: 6 735
-- Name: dblink_get_pkey(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_get_pkey(text) RETURNS SETOF dblink_pkey_results
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_get_pkey';


--
-- TOC entry 144 (class 1255 OID 59181)
-- Dependencies: 6
-- Name: dblink_open(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_open(text, text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_open';


--
-- TOC entry 145 (class 1255 OID 59182)
-- Dependencies: 6
-- Name: dblink_open(text, text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_open(text, text, boolean) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_open';


--
-- TOC entry 146 (class 1255 OID 59183)
-- Dependencies: 6
-- Name: dblink_open(text, text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_open(text, text, text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_open';


--
-- TOC entry 147 (class 1255 OID 59184)
-- Dependencies: 6
-- Name: dblink_open(text, text, text, boolean); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dblink_open(text, text, text, boolean) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/dblink', 'dblink_open';


--
-- TOC entry 148 (class 1255 OID 59185)
-- Dependencies: 6
-- Name: dearmor(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dearmor(text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_dearmor';


--
-- TOC entry 149 (class 1255 OID 59186)
-- Dependencies: 6
-- Name: decrypt(bytea, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION decrypt(bytea, bytea, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_decrypt';


--
-- TOC entry 150 (class 1255 OID 59187)
-- Dependencies: 6
-- Name: decrypt_iv(bytea, bytea, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION decrypt_iv(bytea, bytea, bytea, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_decrypt_iv';


--
-- TOC entry 151 (class 1255 OID 59188)
-- Dependencies: 6
-- Name: difference(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION difference(text, text) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'difference';


--
-- TOC entry 152 (class 1255 OID 59189)
-- Dependencies: 6
-- Name: digest(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION digest(text, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_digest';


--
-- TOC entry 153 (class 1255 OID 59190)
-- Dependencies: 6
-- Name: digest(bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION digest(bytea, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_digest';


--
-- TOC entry 154 (class 1255 OID 59191)
-- Dependencies: 6
-- Name: dmetaphone(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dmetaphone(text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'dmetaphone';


--
-- TOC entry 155 (class 1255 OID 59192)
-- Dependencies: 6
-- Name: dmetaphone_alt(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dmetaphone_alt(text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'dmetaphone_alt';


--
-- TOC entry 156 (class 1255 OID 59193)
-- Dependencies: 6 1042
-- Name: dropgeometrycolumn(character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dropgeometrycolumn(character varying, character varying, character varying, character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	catalog_name alias for $1; 
	schema_name alias for $2;
	table_name alias for $3;
	column_name alias for $4;
	myrec RECORD;
	okay boolean;
	real_schema name;

BEGIN



	-- Find, check or fix schema_name
	IF ( schema_name != '' ) THEN
		okay = 'f';

		FOR myrec IN SELECT nspname FROM pg_namespace WHERE text(nspname) = schema_name LOOP
			okay := 't';
		END LOOP;

		IF ( okay <> 't' ) THEN
			RAISE NOTICE 'Invalid schema name - using current_schema()';
			SELECT current_schema() into real_schema;
		ELSE
			real_schema = schema_name;
		END IF;
	ELSE
		SELECT current_schema() into real_schema;
	END IF;


 	-- Find out if the column is in the geometry_columns table
	okay = 'f';
	FOR myrec IN SELECT * from geometry_columns where f_table_schema = text(real_schema) and f_table_name = table_name and f_geometry_column = column_name LOOP
		okay := 't';
	END LOOP; 
	IF (okay <> 't') THEN 
		RAISE EXCEPTION 'column not found in geometry_columns table';
		RETURN 'f';
	END IF;

	-- Remove ref from geometry_columns table
	EXECUTE 'delete from geometry_columns where f_table_schema = ' ||
		quote_literal(real_schema) || ' and f_table_name = ' ||
		quote_literal(table_name)  || ' and f_geometry_column = ' ||
		quote_literal(column_name);
	

	-- Remove table column
	EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) || '.' ||
		quote_ident(table_name) || ' DROP COLUMN ' ||
		quote_ident(column_name);



	RETURN real_schema || '.' || table_name || '.' || column_name ||' effectively removed.';
	
END;
$_$;


--
-- TOC entry 157 (class 1255 OID 59194)
-- Dependencies: 1042 6
-- Name: dropgeometrycolumn(character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dropgeometrycolumn(character varying, character varying, character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret text;
BEGIN
	SELECT DropGeometryColumn('',$1,$2,$3) into ret;
	RETURN ret;
END;
$_$;


--
-- TOC entry 158 (class 1255 OID 59195)
-- Dependencies: 1042 6
-- Name: dropgeometrycolumn(character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dropgeometrycolumn(character varying, character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret text;
BEGIN
	SELECT DropGeometryColumn('','',$1,$2) into ret;
	RETURN ret;
END;
$_$;


--
-- TOC entry 159 (class 1255 OID 59196)
-- Dependencies: 6 1042
-- Name: dropgeometrytable(character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dropgeometrytable(character varying, character varying, character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	catalog_name alias for $1; 
	schema_name alias for $2;
	table_name alias for $3;
	real_schema name;

BEGIN


	IF ( schema_name = '' ) THEN
		SELECT current_schema() into real_schema;
	ELSE
		real_schema = schema_name;
	END IF;


	-- Remove refs from geometry_columns table
	EXECUTE 'DELETE FROM geometry_columns WHERE ' ||

		'f_table_schema = ' || quote_literal(real_schema) ||
		' AND ' ||

		' f_table_name = ' || quote_literal(table_name);
	
	-- Remove table 
	EXECUTE 'DROP TABLE '

		|| quote_ident(real_schema) || '.' ||

		quote_ident(table_name);

	RETURN

		real_schema || '.' ||

		table_name ||' dropped.';
	
END;
$_$;


--
-- TOC entry 160 (class 1255 OID 59197)
-- Dependencies: 6
-- Name: dropgeometrytable(character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dropgeometrytable(character varying, character varying) RETURNS text
    LANGUAGE sql STRICT
    AS $_$SELECT DropGeometryTable('',$1,$2)$_$;


--
-- TOC entry 161 (class 1255 OID 59198)
-- Dependencies: 6
-- Name: dropgeometrytable(character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION dropgeometrytable(character varying) RETURNS text
    LANGUAGE sql STRICT
    AS $_$SELECT DropGeometryTable('','',$1)$_$;


--
-- TOC entry 162 (class 1255 OID 59199)
-- Dependencies: 6
-- Name: encrypt(bytea, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION encrypt(bytea, bytea, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_encrypt';


--
-- TOC entry 163 (class 1255 OID 59200)
-- Dependencies: 6
-- Name: encrypt_iv(bytea, bytea, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION encrypt_iv(bytea, bytea, bytea, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_encrypt_iv';


--
-- TOC entry 164 (class 1255 OID 59201)
-- Dependencies: 729 6
-- Name: eq(chkpass, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION eq(chkpass, text) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/chkpass', 'chkpass_eq';


--
-- TOC entry 165 (class 1255 OID 59202)
-- Dependencies: 6 1042
-- Name: find_srid(character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION find_srid(character varying, character varying, character varying) RETURNS integer
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$DECLARE
   schem text;
   tabl text;
   sr int4;
BEGIN
   IF $1 IS NULL THEN
      RAISE EXCEPTION 'find_srid() - schema is NULL!';
   END IF;
   IF $2 IS NULL THEN
      RAISE EXCEPTION 'find_srid() - table name is NULL!';
   END IF;
   IF $3 IS NULL THEN
      RAISE EXCEPTION 'find_srid() - column name is NULL!';
   END IF;
   schem = $1;
   tabl = $2;
-- if the table contains a . and the schema is empty
-- split the table into a schema and a table
-- otherwise drop through to default behavior
   IF ( schem = '' and tabl LIKE '%.%' ) THEN
     schem = substr(tabl,1,strpos(tabl,'.')-1);
     tabl = substr(tabl,length(schem)+2);
   ELSE
     schem = schem || '%';
   END IF;

   select SRID into sr from geometry_columns where f_table_schema like schem and f_table_name = tabl and f_geometry_column = $3;
   IF NOT FOUND THEN
       RAISE EXCEPTION 'find_srid() - couldnt find the corresponding SRID - is the geometry registered in the GEOMETRY_COLUMNS table?  Is there an uppercase/lowercase missmatch?';
   END IF;
  return sr;
END;
$_$;


--
-- TOC entry 166 (class 1255 OID 59203)
-- Dependencies: 6 1042
-- Name: fix_geometry_columns(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION fix_geometry_columns() RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
	mislinked record;
	result text;
	linked integer;
	deleted integer;

	foundschema integer;

BEGIN


	-- Since 7.3 schema support has been added.
	-- Previous postgis versions used to put the database name in
	-- the schema column. This needs to be fixed, so we try to 
	-- set the correct schema for each geometry_colums record
	-- looking at table, column, type and srid.
	UPDATE geometry_columns SET f_table_schema = n.nspname
		FROM pg_namespace n, pg_class c, pg_attribute a,
			pg_constraint sridcheck, pg_constraint typecheck
                WHERE ( f_table_schema is NULL
		OR f_table_schema = ''
                OR f_table_schema NOT IN (
                        SELECT nspname::varchar
                        FROM pg_namespace nn, pg_class cc, pg_attribute aa
                        WHERE cc.relnamespace = nn.oid
                        AND cc.relname = f_table_name::name
                        AND aa.attrelid = cc.oid
                        AND aa.attname = f_geometry_column::name))
                AND f_table_name::name = c.relname
                AND c.oid = a.attrelid
                AND c.relnamespace = n.oid
                AND f_geometry_column::name = a.attname

                AND sridcheck.conrelid = c.oid
		AND sridcheck.consrc LIKE '(srid(% = %)'
                AND sridcheck.consrc ~ textcat(' = ', srid::text)

                AND typecheck.conrelid = c.oid
		AND typecheck.consrc LIKE
	'((geometrytype(%) = ''%''::text) OR (% IS NULL))'
                AND typecheck.consrc ~ textcat(' = ''', type::text)

                AND NOT EXISTS (
                        SELECT oid FROM geometry_columns gc
                        WHERE c.relname::varchar = gc.f_table_name
                        AND n.nspname::varchar = gc.f_table_schema
                        AND a.attname::varchar = gc.f_geometry_column
                );

	GET DIAGNOSTICS foundschema = ROW_COUNT;



	-- no linkage to system table needed
	return 'fixed:'||foundschema::text;


	-- fix linking to system tables
	SELECT 0 INTO linked;
	FOR mislinked in
		SELECT gc.oid as gcrec,
			a.attrelid as attrelid, a.attnum as attnum
                FROM geometry_columns gc, pg_class c,

		pg_namespace n, pg_attribute a

                WHERE ( gc.attrelid IS NULL OR gc.attrelid != a.attrelid 
			OR gc.varattnum IS NULL OR gc.varattnum != a.attnum)

                AND n.nspname = gc.f_table_schema::name
                AND c.relnamespace = n.oid

                AND c.relname = gc.f_table_name::name
                AND a.attname = f_geometry_column::name
                AND a.attrelid = c.oid
	LOOP
		UPDATE geometry_columns SET
			attrelid = mislinked.attrelid,
			varattnum = mislinked.attnum,
			stats = NULL
			WHERE geometry_columns.oid = mislinked.gcrec;
		SELECT linked+1 INTO linked;
	END LOOP; 

	-- remove stale records
	DELETE FROM geometry_columns WHERE attrelid IS NULL;

	GET DIAGNOSTICS deleted = ROW_COUNT;

	result = 

		'fixed:' || foundschema::text ||

		' linked:' || linked::text || 
		' deleted:' || deleted::text;

	return result;

END;
$$;


--
-- TOC entry 167 (class 1255 OID 59204)
-- Dependencies: 6
-- Name: g_cube_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_cube_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/cube', 'g_cube_compress';


--
-- TOC entry 168 (class 1255 OID 59205)
-- Dependencies: 6 732
-- Name: g_cube_consistent(internal, cube, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_cube_consistent(internal, cube, integer) RETURNS boolean
    LANGUAGE c
    AS '$libdir/cube', 'g_cube_consistent';


--
-- TOC entry 169 (class 1255 OID 59206)
-- Dependencies: 6
-- Name: g_cube_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_cube_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/cube', 'g_cube_decompress';


--
-- TOC entry 170 (class 1255 OID 59207)
-- Dependencies: 6
-- Name: g_cube_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_cube_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/cube', 'g_cube_penalty';


--
-- TOC entry 171 (class 1255 OID 59208)
-- Dependencies: 6
-- Name: g_cube_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_cube_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/cube', 'g_cube_picksplit';


--
-- TOC entry 172 (class 1255 OID 59209)
-- Dependencies: 732 732 6
-- Name: g_cube_same(cube, cube, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_cube_same(cube, cube, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/cube', 'g_cube_same';


--
-- TOC entry 173 (class 1255 OID 59210)
-- Dependencies: 732 6
-- Name: g_cube_union(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_cube_union(internal, internal) RETURNS cube
    LANGUAGE c
    AS '$libdir/cube', 'g_cube_union';


--
-- TOC entry 174 (class 1255 OID 59211)
-- Dependencies: 6
-- Name: g_int_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_int_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/_int', 'g_int_compress';


--
-- TOC entry 175 (class 1255 OID 59212)
-- Dependencies: 6
-- Name: g_int_consistent(internal, integer[], integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_int_consistent(internal, integer[], integer) RETURNS boolean
    LANGUAGE c
    AS '$libdir/_int', 'g_int_consistent';


--
-- TOC entry 176 (class 1255 OID 59213)
-- Dependencies: 6
-- Name: g_int_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_int_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/_int', 'g_int_decompress';


--
-- TOC entry 177 (class 1255 OID 59214)
-- Dependencies: 6
-- Name: g_int_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_int_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/_int', 'g_int_penalty';


--
-- TOC entry 178 (class 1255 OID 59215)
-- Dependencies: 6
-- Name: g_int_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_int_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/_int', 'g_int_picksplit';


--
-- TOC entry 179 (class 1255 OID 59216)
-- Dependencies: 6
-- Name: g_int_same(integer[], integer[], internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_int_same(integer[], integer[], internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/_int', 'g_int_same';


--
-- TOC entry 180 (class 1255 OID 59217)
-- Dependencies: 6
-- Name: g_int_union(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_int_union(internal, internal) RETURNS integer[]
    LANGUAGE c
    AS '$libdir/_int', 'g_int_union';


--
-- TOC entry 181 (class 1255 OID 59218)
-- Dependencies: 6
-- Name: g_intbig_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_intbig_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/_int', 'g_intbig_compress';


--
-- TOC entry 182 (class 1255 OID 59219)
-- Dependencies: 6
-- Name: g_intbig_consistent(internal, internal, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_intbig_consistent(internal, internal, integer) RETURNS boolean
    LANGUAGE c
    AS '$libdir/_int', 'g_intbig_consistent';


--
-- TOC entry 183 (class 1255 OID 59220)
-- Dependencies: 6
-- Name: g_intbig_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_intbig_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/_int', 'g_intbig_decompress';


--
-- TOC entry 184 (class 1255 OID 59221)
-- Dependencies: 6
-- Name: g_intbig_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_intbig_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/_int', 'g_intbig_penalty';


--
-- TOC entry 185 (class 1255 OID 59222)
-- Dependencies: 6
-- Name: g_intbig_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_intbig_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/_int', 'g_intbig_picksplit';


--
-- TOC entry 186 (class 1255 OID 59223)
-- Dependencies: 6
-- Name: g_intbig_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_intbig_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/_int', 'g_intbig_same';


--
-- TOC entry 187 (class 1255 OID 59224)
-- Dependencies: 6
-- Name: g_intbig_union(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION g_intbig_union(internal, internal) RETURNS integer[]
    LANGUAGE c
    AS '$libdir/_int', 'g_intbig_union';


--
-- TOC entry 188 (class 1255 OID 59225)
-- Dependencies: 6
-- Name: gbt_bit_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bit_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bit_compress';


--
-- TOC entry 189 (class 1255 OID 59226)
-- Dependencies: 6
-- Name: gbt_bit_consistent(internal, bit, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bit_consistent(internal, bit, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bit_consistent';


--
-- TOC entry 190 (class 1255 OID 59227)
-- Dependencies: 6
-- Name: gbt_bit_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bit_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_bit_penalty';


--
-- TOC entry 191 (class 1255 OID 59228)
-- Dependencies: 6
-- Name: gbt_bit_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bit_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bit_picksplit';


--
-- TOC entry 192 (class 1255 OID 59229)
-- Dependencies: 6
-- Name: gbt_bit_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bit_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bit_same';


--
-- TOC entry 193 (class 1255 OID 59230)
-- Dependencies: 6 749
-- Name: gbt_bit_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bit_union(bytea, internal) RETURNS gbtreekey_var
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bit_union';


--
-- TOC entry 194 (class 1255 OID 59231)
-- Dependencies: 6
-- Name: gbt_bpchar_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bpchar_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bpchar_compress';


--
-- TOC entry 195 (class 1255 OID 59232)
-- Dependencies: 6
-- Name: gbt_bpchar_consistent(internal, character, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bpchar_consistent(internal, character, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bpchar_consistent';


--
-- TOC entry 196 (class 1255 OID 59233)
-- Dependencies: 6
-- Name: gbt_bytea_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bytea_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bytea_compress';


--
-- TOC entry 197 (class 1255 OID 59234)
-- Dependencies: 6
-- Name: gbt_bytea_consistent(internal, bytea, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bytea_consistent(internal, bytea, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bytea_consistent';


--
-- TOC entry 198 (class 1255 OID 59235)
-- Dependencies: 6
-- Name: gbt_bytea_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bytea_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_bytea_penalty';


--
-- TOC entry 199 (class 1255 OID 59236)
-- Dependencies: 6
-- Name: gbt_bytea_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bytea_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bytea_picksplit';


--
-- TOC entry 200 (class 1255 OID 59237)
-- Dependencies: 6
-- Name: gbt_bytea_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bytea_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bytea_same';


--
-- TOC entry 201 (class 1255 OID 59238)
-- Dependencies: 749 6
-- Name: gbt_bytea_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_bytea_union(bytea, internal) RETURNS gbtreekey_var
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_bytea_union';


--
-- TOC entry 202 (class 1255 OID 59239)
-- Dependencies: 6
-- Name: gbt_cash_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_cash_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_cash_compress';


--
-- TOC entry 203 (class 1255 OID 59240)
-- Dependencies: 6
-- Name: gbt_cash_consistent(internal, money, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_cash_consistent(internal, money, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_cash_consistent';


--
-- TOC entry 204 (class 1255 OID 59241)
-- Dependencies: 6
-- Name: gbt_cash_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_cash_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_cash_penalty';


--
-- TOC entry 205 (class 1255 OID 59242)
-- Dependencies: 6
-- Name: gbt_cash_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_cash_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_cash_picksplit';


--
-- TOC entry 206 (class 1255 OID 59243)
-- Dependencies: 6
-- Name: gbt_cash_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_cash_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_cash_same';


--
-- TOC entry 207 (class 1255 OID 59244)
-- Dependencies: 746 6
-- Name: gbt_cash_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_cash_union(bytea, internal) RETURNS gbtreekey8
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_cash_union';


--
-- TOC entry 208 (class 1255 OID 59245)
-- Dependencies: 6
-- Name: gbt_date_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_date_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_date_compress';


--
-- TOC entry 210 (class 1255 OID 59246)
-- Dependencies: 6
-- Name: gbt_date_consistent(internal, date, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_date_consistent(internal, date, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_date_consistent';


--
-- TOC entry 211 (class 1255 OID 59247)
-- Dependencies: 6
-- Name: gbt_date_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_date_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_date_penalty';


--
-- TOC entry 212 (class 1255 OID 59248)
-- Dependencies: 6
-- Name: gbt_date_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_date_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_date_picksplit';


--
-- TOC entry 213 (class 1255 OID 59249)
-- Dependencies: 6
-- Name: gbt_date_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_date_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_date_same';


--
-- TOC entry 214 (class 1255 OID 59250)
-- Dependencies: 6 746
-- Name: gbt_date_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_date_union(bytea, internal) RETURNS gbtreekey8
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_date_union';


--
-- TOC entry 215 (class 1255 OID 59251)
-- Dependencies: 6
-- Name: gbt_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_decompress';


--
-- TOC entry 216 (class 1255 OID 59252)
-- Dependencies: 6
-- Name: gbt_float4_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float4_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float4_compress';


--
-- TOC entry 217 (class 1255 OID 59253)
-- Dependencies: 6
-- Name: gbt_float4_consistent(internal, real, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float4_consistent(internal, real, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float4_consistent';


--
-- TOC entry 218 (class 1255 OID 59254)
-- Dependencies: 6
-- Name: gbt_float4_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float4_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_float4_penalty';


--
-- TOC entry 219 (class 1255 OID 59255)
-- Dependencies: 6
-- Name: gbt_float4_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float4_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float4_picksplit';


--
-- TOC entry 220 (class 1255 OID 59256)
-- Dependencies: 6
-- Name: gbt_float4_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float4_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float4_same';


--
-- TOC entry 221 (class 1255 OID 59257)
-- Dependencies: 746 6
-- Name: gbt_float4_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float4_union(bytea, internal) RETURNS gbtreekey8
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float4_union';


--
-- TOC entry 222 (class 1255 OID 59258)
-- Dependencies: 6
-- Name: gbt_float8_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float8_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float8_compress';


--
-- TOC entry 223 (class 1255 OID 59259)
-- Dependencies: 6
-- Name: gbt_float8_consistent(internal, double precision, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float8_consistent(internal, double precision, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float8_consistent';


--
-- TOC entry 224 (class 1255 OID 59260)
-- Dependencies: 6
-- Name: gbt_float8_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float8_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_float8_penalty';


--
-- TOC entry 225 (class 1255 OID 59261)
-- Dependencies: 6
-- Name: gbt_float8_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float8_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float8_picksplit';


--
-- TOC entry 226 (class 1255 OID 59262)
-- Dependencies: 6
-- Name: gbt_float8_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float8_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float8_same';


--
-- TOC entry 227 (class 1255 OID 59263)
-- Dependencies: 737 6
-- Name: gbt_float8_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_float8_union(bytea, internal) RETURNS gbtreekey16
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_float8_union';


--
-- TOC entry 228 (class 1255 OID 59264)
-- Dependencies: 6
-- Name: gbt_inet_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_inet_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_inet_compress';


--
-- TOC entry 229 (class 1255 OID 59265)
-- Dependencies: 6
-- Name: gbt_inet_consistent(internal, inet, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_inet_consistent(internal, inet, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_inet_consistent';


--
-- TOC entry 230 (class 1255 OID 59266)
-- Dependencies: 6
-- Name: gbt_inet_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_inet_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_inet_penalty';


--
-- TOC entry 231 (class 1255 OID 59267)
-- Dependencies: 6
-- Name: gbt_inet_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_inet_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_inet_picksplit';


--
-- TOC entry 232 (class 1255 OID 59268)
-- Dependencies: 6
-- Name: gbt_inet_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_inet_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_inet_same';


--
-- TOC entry 233 (class 1255 OID 59269)
-- Dependencies: 6 737
-- Name: gbt_inet_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_inet_union(bytea, internal) RETURNS gbtreekey16
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_inet_union';


--
-- TOC entry 234 (class 1255 OID 59270)
-- Dependencies: 6
-- Name: gbt_int2_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int2_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int2_compress';


--
-- TOC entry 235 (class 1255 OID 59271)
-- Dependencies: 6
-- Name: gbt_int2_consistent(internal, smallint, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int2_consistent(internal, smallint, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int2_consistent';


--
-- TOC entry 236 (class 1255 OID 59272)
-- Dependencies: 6
-- Name: gbt_int2_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int2_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_int2_penalty';


--
-- TOC entry 237 (class 1255 OID 59273)
-- Dependencies: 6
-- Name: gbt_int2_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int2_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int2_picksplit';


--
-- TOC entry 238 (class 1255 OID 59274)
-- Dependencies: 6
-- Name: gbt_int2_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int2_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int2_same';


--
-- TOC entry 239 (class 1255 OID 59275)
-- Dependencies: 6 743
-- Name: gbt_int2_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int2_union(bytea, internal) RETURNS gbtreekey4
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int2_union';


--
-- TOC entry 240 (class 1255 OID 59276)
-- Dependencies: 6
-- Name: gbt_int4_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int4_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int4_compress';


--
-- TOC entry 241 (class 1255 OID 59277)
-- Dependencies: 6
-- Name: gbt_int4_consistent(internal, integer, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int4_consistent(internal, integer, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int4_consistent';


--
-- TOC entry 242 (class 1255 OID 59278)
-- Dependencies: 6
-- Name: gbt_int4_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int4_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_int4_penalty';


--
-- TOC entry 243 (class 1255 OID 59279)
-- Dependencies: 6
-- Name: gbt_int4_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int4_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int4_picksplit';


--
-- TOC entry 244 (class 1255 OID 59280)
-- Dependencies: 6
-- Name: gbt_int4_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int4_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int4_same';


--
-- TOC entry 245 (class 1255 OID 59281)
-- Dependencies: 746 6
-- Name: gbt_int4_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int4_union(bytea, internal) RETURNS gbtreekey8
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int4_union';


--
-- TOC entry 246 (class 1255 OID 59282)
-- Dependencies: 6
-- Name: gbt_int8_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int8_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int8_compress';


--
-- TOC entry 247 (class 1255 OID 59283)
-- Dependencies: 6
-- Name: gbt_int8_consistent(internal, bigint, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int8_consistent(internal, bigint, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int8_consistent';


--
-- TOC entry 248 (class 1255 OID 59284)
-- Dependencies: 6
-- Name: gbt_int8_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int8_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_int8_penalty';


--
-- TOC entry 249 (class 1255 OID 59285)
-- Dependencies: 6
-- Name: gbt_int8_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int8_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int8_picksplit';


--
-- TOC entry 250 (class 1255 OID 59286)
-- Dependencies: 6
-- Name: gbt_int8_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int8_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int8_same';


--
-- TOC entry 251 (class 1255 OID 59287)
-- Dependencies: 737 6
-- Name: gbt_int8_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_int8_union(bytea, internal) RETURNS gbtreekey16
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_int8_union';


--
-- TOC entry 252 (class 1255 OID 59288)
-- Dependencies: 6
-- Name: gbt_intv_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_intv_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_intv_compress';


--
-- TOC entry 253 (class 1255 OID 59289)
-- Dependencies: 6
-- Name: gbt_intv_consistent(internal, interval, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_intv_consistent(internal, interval, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_intv_consistent';


--
-- TOC entry 254 (class 1255 OID 59290)
-- Dependencies: 6
-- Name: gbt_intv_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_intv_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_intv_decompress';


--
-- TOC entry 255 (class 1255 OID 59291)
-- Dependencies: 6
-- Name: gbt_intv_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_intv_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_intv_penalty';


--
-- TOC entry 256 (class 1255 OID 59292)
-- Dependencies: 6
-- Name: gbt_intv_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_intv_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_intv_picksplit';


--
-- TOC entry 257 (class 1255 OID 59293)
-- Dependencies: 6
-- Name: gbt_intv_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_intv_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_intv_same';


--
-- TOC entry 258 (class 1255 OID 59294)
-- Dependencies: 6 740
-- Name: gbt_intv_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_intv_union(bytea, internal) RETURNS gbtreekey32
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_intv_union';


--
-- TOC entry 259 (class 1255 OID 59295)
-- Dependencies: 6
-- Name: gbt_macad_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_macad_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_macad_compress';


--
-- TOC entry 260 (class 1255 OID 59296)
-- Dependencies: 6
-- Name: gbt_macad_consistent(internal, macaddr, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_macad_consistent(internal, macaddr, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_macad_consistent';


--
-- TOC entry 261 (class 1255 OID 59297)
-- Dependencies: 6
-- Name: gbt_macad_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_macad_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_macad_penalty';


--
-- TOC entry 262 (class 1255 OID 59298)
-- Dependencies: 6
-- Name: gbt_macad_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_macad_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_macad_picksplit';


--
-- TOC entry 263 (class 1255 OID 59299)
-- Dependencies: 6
-- Name: gbt_macad_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_macad_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_macad_same';


--
-- TOC entry 264 (class 1255 OID 59300)
-- Dependencies: 6 737
-- Name: gbt_macad_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_macad_union(bytea, internal) RETURNS gbtreekey16
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_macad_union';


--
-- TOC entry 265 (class 1255 OID 59301)
-- Dependencies: 6
-- Name: gbt_numeric_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_numeric_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_numeric_compress';


--
-- TOC entry 266 (class 1255 OID 59302)
-- Dependencies: 6
-- Name: gbt_numeric_consistent(internal, numeric, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_numeric_consistent(internal, numeric, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_numeric_consistent';


--
-- TOC entry 267 (class 1255 OID 59303)
-- Dependencies: 6
-- Name: gbt_numeric_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_numeric_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_numeric_penalty';


--
-- TOC entry 268 (class 1255 OID 59304)
-- Dependencies: 6
-- Name: gbt_numeric_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_numeric_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_numeric_picksplit';


--
-- TOC entry 269 (class 1255 OID 59305)
-- Dependencies: 6
-- Name: gbt_numeric_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_numeric_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_numeric_same';


--
-- TOC entry 270 (class 1255 OID 59306)
-- Dependencies: 6 749
-- Name: gbt_numeric_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_numeric_union(bytea, internal) RETURNS gbtreekey_var
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_numeric_union';


--
-- TOC entry 271 (class 1255 OID 59307)
-- Dependencies: 6
-- Name: gbt_oid_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_oid_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_oid_compress';


--
-- TOC entry 272 (class 1255 OID 59308)
-- Dependencies: 6
-- Name: gbt_oid_consistent(internal, oid, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_oid_consistent(internal, oid, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_oid_consistent';


--
-- TOC entry 273 (class 1255 OID 59309)
-- Dependencies: 6
-- Name: gbt_oid_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_oid_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_oid_penalty';


--
-- TOC entry 274 (class 1255 OID 59310)
-- Dependencies: 6
-- Name: gbt_oid_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_oid_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_oid_picksplit';


--
-- TOC entry 275 (class 1255 OID 59311)
-- Dependencies: 6
-- Name: gbt_oid_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_oid_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_oid_same';


--
-- TOC entry 276 (class 1255 OID 59312)
-- Dependencies: 6 746
-- Name: gbt_oid_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_oid_union(bytea, internal) RETURNS gbtreekey8
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_oid_union';


--
-- TOC entry 277 (class 1255 OID 59313)
-- Dependencies: 6
-- Name: gbt_text_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_text_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_text_compress';


--
-- TOC entry 278 (class 1255 OID 59314)
-- Dependencies: 6
-- Name: gbt_text_consistent(internal, text, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_text_consistent(internal, text, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_text_consistent';


--
-- TOC entry 279 (class 1255 OID 59315)
-- Dependencies: 6
-- Name: gbt_text_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_text_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_text_penalty';


--
-- TOC entry 280 (class 1255 OID 59316)
-- Dependencies: 6
-- Name: gbt_text_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_text_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_text_picksplit';


--
-- TOC entry 281 (class 1255 OID 59317)
-- Dependencies: 6
-- Name: gbt_text_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_text_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_text_same';


--
-- TOC entry 282 (class 1255 OID 59318)
-- Dependencies: 6 749
-- Name: gbt_text_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_text_union(bytea, internal) RETURNS gbtreekey_var
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_text_union';


--
-- TOC entry 283 (class 1255 OID 59319)
-- Dependencies: 6
-- Name: gbt_time_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_time_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_time_compress';


--
-- TOC entry 284 (class 1255 OID 59320)
-- Dependencies: 6
-- Name: gbt_time_consistent(internal, time without time zone, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_time_consistent(internal, time without time zone, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_time_consistent';


--
-- TOC entry 285 (class 1255 OID 59321)
-- Dependencies: 6
-- Name: gbt_time_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_time_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_time_penalty';


--
-- TOC entry 286 (class 1255 OID 59322)
-- Dependencies: 6
-- Name: gbt_time_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_time_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_time_picksplit';


--
-- TOC entry 287 (class 1255 OID 59323)
-- Dependencies: 6
-- Name: gbt_time_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_time_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_time_same';


--
-- TOC entry 288 (class 1255 OID 59324)
-- Dependencies: 737 6
-- Name: gbt_time_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_time_union(bytea, internal) RETURNS gbtreekey16
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_time_union';


--
-- TOC entry 289 (class 1255 OID 59325)
-- Dependencies: 6
-- Name: gbt_timetz_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_timetz_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_timetz_compress';


--
-- TOC entry 290 (class 1255 OID 59326)
-- Dependencies: 6
-- Name: gbt_timetz_consistent(internal, time with time zone, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_timetz_consistent(internal, time with time zone, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_timetz_consistent';


--
-- TOC entry 291 (class 1255 OID 59327)
-- Dependencies: 6
-- Name: gbt_ts_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_ts_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_ts_compress';


--
-- TOC entry 292 (class 1255 OID 59328)
-- Dependencies: 6
-- Name: gbt_ts_consistent(internal, timestamp without time zone, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_ts_consistent(internal, timestamp without time zone, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_ts_consistent';


--
-- TOC entry 293 (class 1255 OID 59329)
-- Dependencies: 6
-- Name: gbt_ts_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_ts_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/btree_gist', 'gbt_ts_penalty';


--
-- TOC entry 294 (class 1255 OID 59330)
-- Dependencies: 6
-- Name: gbt_ts_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_ts_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_ts_picksplit';


--
-- TOC entry 295 (class 1255 OID 59331)
-- Dependencies: 6
-- Name: gbt_ts_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_ts_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_ts_same';


--
-- TOC entry 296 (class 1255 OID 59332)
-- Dependencies: 6 737
-- Name: gbt_ts_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_ts_union(bytea, internal) RETURNS gbtreekey16
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_ts_union';


--
-- TOC entry 297 (class 1255 OID 59333)
-- Dependencies: 6
-- Name: gbt_tstz_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_tstz_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_tstz_compress';


--
-- TOC entry 298 (class 1255 OID 59334)
-- Dependencies: 6
-- Name: gbt_tstz_consistent(internal, timestamp with time zone, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_tstz_consistent(internal, timestamp with time zone, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_tstz_consistent';


--
-- TOC entry 299 (class 1255 OID 59335)
-- Dependencies: 6
-- Name: gbt_var_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gbt_var_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/btree_gist', 'gbt_var_decompress';


--
-- TOC entry 300 (class 1255 OID 59336)
-- Dependencies: 6
-- Name: gen_salt(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gen_salt(text) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pg_gen_salt';


--
-- TOC entry 301 (class 1255 OID 59337)
-- Dependencies: 6
-- Name: gen_salt(text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gen_salt(text, integer) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pg_gen_salt_rounds';


--
-- TOC entry 302 (class 1255 OID 59338)
-- Dependencies: 1042 6
-- Name: get_proj4_from_srid(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION get_proj4_from_srid(integer) RETURNS text
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
BEGIN
	RETURN proj4text::text FROM spatial_ref_sys WHERE srid= $1;
END;
$_$;


--
-- TOC entry 303 (class 1255 OID 59339)
-- Dependencies: 6
-- Name: get_timetravel(name); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION get_timetravel(name) RETURNS integer
    LANGUAGE c STRICT
    AS '$libdir/timetravel', 'get_timetravel';


--
-- TOC entry 304 (class 1255 OID 59340)
-- Dependencies: 6
-- Name: gseg_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gseg_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/seg', 'gseg_compress';


--
-- TOC entry 305 (class 1255 OID 59341)
-- Dependencies: 776 6
-- Name: gseg_consistent(internal, seg, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gseg_consistent(internal, seg, integer) RETURNS boolean
    LANGUAGE c
    AS '$libdir/seg', 'gseg_consistent';


--
-- TOC entry 306 (class 1255 OID 59342)
-- Dependencies: 6
-- Name: gseg_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gseg_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/seg', 'gseg_decompress';


--
-- TOC entry 307 (class 1255 OID 59343)
-- Dependencies: 6
-- Name: gseg_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gseg_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/seg', 'gseg_penalty';


--
-- TOC entry 308 (class 1255 OID 59344)
-- Dependencies: 6
-- Name: gseg_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gseg_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/seg', 'gseg_picksplit';


--
-- TOC entry 309 (class 1255 OID 59345)
-- Dependencies: 776 776 6
-- Name: gseg_same(seg, seg, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gseg_same(seg, seg, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/seg', 'gseg_same';


--
-- TOC entry 310 (class 1255 OID 59346)
-- Dependencies: 776 6
-- Name: gseg_union(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gseg_union(internal, internal) RETURNS seg
    LANGUAGE c
    AS '$libdir/seg', 'gseg_union';


--
-- TOC entry 311 (class 1255 OID 59347)
-- Dependencies: 6
-- Name: gtrgm_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/pg_trgm', 'gtrgm_compress';


--
-- TOC entry 312 (class 1255 OID 59348)
-- Dependencies: 752 6
-- Name: gtrgm_consistent(gtrgm, internal, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_consistent(gtrgm, internal, integer) RETURNS boolean
    LANGUAGE c
    AS '$libdir/pg_trgm', 'gtrgm_consistent';


--
-- TOC entry 313 (class 1255 OID 59349)
-- Dependencies: 6
-- Name: gtrgm_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/pg_trgm', 'gtrgm_decompress';


--
-- TOC entry 314 (class 1255 OID 59350)
-- Dependencies: 6
-- Name: gtrgm_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/pg_trgm', 'gtrgm_penalty';


--
-- TOC entry 315 (class 1255 OID 59351)
-- Dependencies: 6
-- Name: gtrgm_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/pg_trgm', 'gtrgm_picksplit';


--
-- TOC entry 316 (class 1255 OID 59352)
-- Dependencies: 752 752 6
-- Name: gtrgm_same(gtrgm, gtrgm, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_same(gtrgm, gtrgm, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/pg_trgm', 'gtrgm_same';


--
-- TOC entry 317 (class 1255 OID 59353)
-- Dependencies: 6
-- Name: gtrgm_union(bytea, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION gtrgm_union(bytea, internal) RETURNS integer[]
    LANGUAGE c
    AS '$libdir/pg_trgm', 'gtrgm_union';


--
-- TOC entry 318 (class 1255 OID 59354)
-- Dependencies: 6
-- Name: hmac(text, text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION hmac(text, text, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_hmac';


--
-- TOC entry 319 (class 1255 OID 59355)
-- Dependencies: 6
-- Name: hmac(bytea, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION hmac(bytea, bytea, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pg_hmac';


--
-- TOC entry 320 (class 1255 OID 59356)
-- Dependencies: 6
-- Name: icount(integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION icount(integer[]) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'icount';


--
-- TOC entry 321 (class 1255 OID 59357)
-- Dependencies: 6
-- Name: idx(integer[], integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION idx(integer[], integer) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'idx';


--
-- TOC entry 322 (class 1255 OID 59358)
-- Dependencies: 6 762 762
-- Name: index(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION index(ltree, ltree) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_index';


--
-- TOC entry 323 (class 1255 OID 59359)
-- Dependencies: 762 762 6
-- Name: index(ltree, ltree, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION index(ltree, ltree, integer) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_index';


--
-- TOC entry 324 (class 1255 OID 59360)
-- Dependencies: 6
-- Name: insert_username(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION insert_username() RETURNS trigger
    LANGUAGE c
    AS '$libdir/insert_username', 'insert_username';


--
-- TOC entry 325 (class 1255 OID 59361)
-- Dependencies: 6
-- Name: intarray_del_elem(integer[], integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION intarray_del_elem(integer[], integer) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'intarray_del_elem';


--
-- TOC entry 326 (class 1255 OID 59362)
-- Dependencies: 6
-- Name: intarray_push_array(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION intarray_push_array(integer[], integer[]) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'intarray_push_array';


--
-- TOC entry 327 (class 1255 OID 59363)
-- Dependencies: 6
-- Name: intarray_push_elem(integer[], integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION intarray_push_elem(integer[], integer) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'intarray_push_elem';


--
-- TOC entry 328 (class 1255 OID 59364)
-- Dependencies: 6
-- Name: intset(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION intset(integer) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'intset';


--
-- TOC entry 329 (class 1255 OID 59365)
-- Dependencies: 6
-- Name: intset_subtract(integer[], integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION intset_subtract(integer[], integer[]) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'intset_subtract';


--
-- TOC entry 330 (class 1255 OID 59366)
-- Dependencies: 6
-- Name: intset_union_elem(integer[], integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION intset_union_elem(integer[], integer) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'intset_union_elem';


--
-- TOC entry 331 (class 1255 OID 59367)
-- Dependencies: 6 764 762
-- Name: lca(ltree[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lca(ltree[]) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', '_lca';


--
-- TOC entry 332 (class 1255 OID 59368)
-- Dependencies: 6 762 762 762
-- Name: lca(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lca(ltree, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lca';


--
-- TOC entry 333 (class 1255 OID 59369)
-- Dependencies: 762 6 762 762 762
-- Name: lca(ltree, ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lca(ltree, ltree, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lca';


--
-- TOC entry 334 (class 1255 OID 59370)
-- Dependencies: 6 762 762 762 762 762
-- Name: lca(ltree, ltree, ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lca(ltree, ltree, ltree, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lca';


--
-- TOC entry 335 (class 1255 OID 59371)
-- Dependencies: 762 762 762 762 762 6 762
-- Name: lca(ltree, ltree, ltree, ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lca(ltree, ltree, ltree, ltree, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lca';


--
-- TOC entry 336 (class 1255 OID 59372)
-- Dependencies: 762 6 762 762 762 762 762 762
-- Name: lca(ltree, ltree, ltree, ltree, ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lca(ltree, ltree, ltree, ltree, ltree, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lca';


--
-- TOC entry 337 (class 1255 OID 59373)
-- Dependencies: 762 762 762 762 762 762 762 6 762
-- Name: lca(ltree, ltree, ltree, ltree, ltree, ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lca(ltree, ltree, ltree, ltree, ltree, ltree, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lca';


--
-- TOC entry 338 (class 1255 OID 59374)
-- Dependencies: 762 762 762 762 762 762 762 762 762 6
-- Name: lca(ltree, ltree, ltree, ltree, ltree, ltree, ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lca(ltree, ltree, ltree, ltree, ltree, ltree, ltree, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lca';


--
-- TOC entry 339 (class 1255 OID 59375)
-- Dependencies: 6
-- Name: levenshtein(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION levenshtein(text, text) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'levenshtein';


--
-- TOC entry 340 (class 1255 OID 59376)
-- Dependencies: 6
-- Name: lo_manage(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lo_manage() RETURNS trigger
    LANGUAGE c
    AS '$libdir/lo', 'lo_manage';


--
-- TOC entry 341 (class 1255 OID 59377)
-- Dependencies: 758 6
-- Name: lo_oid(lo); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lo_oid(lo) RETURNS oid
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$SELECT $1::pg_catalog.oid$_$;


--
-- TOC entry 342 (class 1255 OID 59378)
-- Dependencies: 761 762 6
-- Name: lt_q_regex(ltree, lquery[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lt_q_regex(ltree, lquery[]) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lt_q_regex';


--
-- TOC entry 343 (class 1255 OID 59379)
-- Dependencies: 762 761 6
-- Name: lt_q_rregex(lquery[], ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION lt_q_rregex(lquery[], ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'lt_q_rregex';


--
-- TOC entry 344 (class 1255 OID 59380)
-- Dependencies: 762 6 759
-- Name: ltq_regex(ltree, lquery); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltq_regex(ltree, lquery) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltq_regex';


--
-- TOC entry 345 (class 1255 OID 59381)
-- Dependencies: 759 6 762
-- Name: ltq_rregex(lquery, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltq_rregex(lquery, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltq_rregex';


--
-- TOC entry 346 (class 1255 OID 59382)
-- Dependencies: 762 6
-- Name: ltree2text(ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree2text(ltree) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree2text';


--
-- TOC entry 347 (class 1255 OID 59383)
-- Dependencies: 762 762 762 6
-- Name: ltree_addltree(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_addltree(ltree, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_addltree';


--
-- TOC entry 348 (class 1255 OID 59384)
-- Dependencies: 762 6 762
-- Name: ltree_addtext(ltree, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_addtext(ltree, text) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_addtext';


--
-- TOC entry 349 (class 1255 OID 59385)
-- Dependencies: 762 6 762
-- Name: ltree_cmp(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_cmp(ltree, ltree) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_cmp';


--
-- TOC entry 350 (class 1255 OID 59386)
-- Dependencies: 6
-- Name: ltree_compress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_compress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/ltree', 'ltree_compress';


--
-- TOC entry 351 (class 1255 OID 59387)
-- Dependencies: 6
-- Name: ltree_consistent(internal, internal, smallint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_consistent(internal, internal, smallint) RETURNS boolean
    LANGUAGE c
    AS '$libdir/ltree', 'ltree_consistent';


--
-- TOC entry 352 (class 1255 OID 59388)
-- Dependencies: 6
-- Name: ltree_decompress(internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_decompress(internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/ltree', 'ltree_decompress';


--
-- TOC entry 353 (class 1255 OID 59389)
-- Dependencies: 762 6 762
-- Name: ltree_eq(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_eq(ltree, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_eq';


--
-- TOC entry 354 (class 1255 OID 59390)
-- Dependencies: 762 6 762
-- Name: ltree_ge(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_ge(ltree, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_ge';


--
-- TOC entry 355 (class 1255 OID 59391)
-- Dependencies: 762 762 6
-- Name: ltree_gt(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_gt(ltree, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_gt';


--
-- TOC entry 356 (class 1255 OID 59392)
-- Dependencies: 6 762 762
-- Name: ltree_isparent(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_isparent(ltree, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_isparent';


--
-- TOC entry 357 (class 1255 OID 59393)
-- Dependencies: 6 762 762
-- Name: ltree_le(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_le(ltree, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_le';


--
-- TOC entry 358 (class 1255 OID 59394)
-- Dependencies: 6 762 762
-- Name: ltree_lt(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_lt(ltree, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_lt';


--
-- TOC entry 359 (class 1255 OID 59395)
-- Dependencies: 762 6 762
-- Name: ltree_ne(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_ne(ltree, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_ne';


--
-- TOC entry 360 (class 1255 OID 59396)
-- Dependencies: 6
-- Name: ltree_penalty(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_penalty(internal, internal, internal) RETURNS internal
    LANGUAGE c STRICT
    AS '$libdir/ltree', 'ltree_penalty';


--
-- TOC entry 361 (class 1255 OID 59397)
-- Dependencies: 6
-- Name: ltree_picksplit(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_picksplit(internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/ltree', 'ltree_picksplit';


--
-- TOC entry 362 (class 1255 OID 59398)
-- Dependencies: 762 762 6
-- Name: ltree_risparent(ltree, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_risparent(ltree, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_risparent';


--
-- TOC entry 363 (class 1255 OID 59399)
-- Dependencies: 6
-- Name: ltree_same(internal, internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_same(internal, internal, internal) RETURNS internal
    LANGUAGE c
    AS '$libdir/ltree', 'ltree_same';


--
-- TOC entry 364 (class 1255 OID 59400)
-- Dependencies: 762 6 762
-- Name: ltree_textadd(text, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_textadd(text, ltree) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltree_textadd';


--
-- TOC entry 365 (class 1255 OID 59401)
-- Dependencies: 6
-- Name: ltree_union(internal, internal); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltree_union(internal, internal) RETURNS integer
    LANGUAGE c
    AS '$libdir/ltree', 'ltree_union';


--
-- TOC entry 366 (class 1255 OID 59402)
-- Dependencies: 762 6 768
-- Name: ltxtq_exec(ltree, ltxtquery); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltxtq_exec(ltree, ltxtquery) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltxtq_exec';


--
-- TOC entry 367 (class 1255 OID 59403)
-- Dependencies: 762 6 768
-- Name: ltxtq_rexec(ltxtquery, ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ltxtq_rexec(ltxtquery, ltree) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'ltxtq_rexec';


--
-- TOC entry 368 (class 1255 OID 59404)
-- Dependencies: 6
-- Name: metaphone(text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION metaphone(text, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'metaphone';


--
-- TOC entry 369 (class 1255 OID 59405)
-- Dependencies: 6
-- Name: moddatetime(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION moddatetime() RETURNS trigger
    LANGUAGE c
    AS '$libdir/moddatetime', 'moddatetime';


--
-- TOC entry 370 (class 1255 OID 59406)
-- Dependencies: 729 6
-- Name: ne(chkpass, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION ne(chkpass, text) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/chkpass', 'chkpass_ne';


--
-- TOC entry 371 (class 1255 OID 59407)
-- Dependencies: 6 762
-- Name: nlevel(ltree); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION nlevel(ltree) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'nlevel';


--
-- TOC entry 372 (class 1255 OID 59408)
-- Dependencies: 6
-- Name: normal_rand(integer, double precision, double precision); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION normal_rand(integer, double precision, double precision) RETURNS SETOF double precision
    LANGUAGE c STRICT
    AS '$libdir/tablefunc', 'normal_rand';


--
-- TOC entry 373 (class 1255 OID 59409)
-- Dependencies: 6
-- Name: pgp_key_id(bytea); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_key_id(bytea) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_key_id_w';


--
-- TOC entry 374 (class 1255 OID 59410)
-- Dependencies: 6
-- Name: pgp_pub_decrypt(bytea, bytea); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_decrypt(bytea, bytea) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_decrypt_text';


--
-- TOC entry 375 (class 1255 OID 59411)
-- Dependencies: 6
-- Name: pgp_pub_decrypt(bytea, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_decrypt(bytea, bytea, text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_decrypt_text';


--
-- TOC entry 376 (class 1255 OID 59412)
-- Dependencies: 6
-- Name: pgp_pub_decrypt(bytea, bytea, text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_decrypt(bytea, bytea, text, text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_decrypt_text';


--
-- TOC entry 377 (class 1255 OID 59413)
-- Dependencies: 6
-- Name: pgp_pub_decrypt_bytea(bytea, bytea); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_decrypt_bytea(bytea, bytea) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_decrypt_bytea';


--
-- TOC entry 378 (class 1255 OID 59414)
-- Dependencies: 6
-- Name: pgp_pub_decrypt_bytea(bytea, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_decrypt_bytea(bytea, bytea, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_decrypt_bytea';


--
-- TOC entry 379 (class 1255 OID 59415)
-- Dependencies: 6
-- Name: pgp_pub_decrypt_bytea(bytea, bytea, text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_decrypt_bytea(bytea, bytea, text, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_decrypt_bytea';


--
-- TOC entry 380 (class 1255 OID 59416)
-- Dependencies: 6
-- Name: pgp_pub_encrypt(text, bytea); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_encrypt(text, bytea) RETURNS bytea
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_encrypt_text';


--
-- TOC entry 381 (class 1255 OID 59417)
-- Dependencies: 6
-- Name: pgp_pub_encrypt(text, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_encrypt(text, bytea, text) RETURNS bytea
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_encrypt_text';


--
-- TOC entry 382 (class 1255 OID 59418)
-- Dependencies: 6
-- Name: pgp_pub_encrypt_bytea(bytea, bytea); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_encrypt_bytea(bytea, bytea) RETURNS bytea
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_encrypt_bytea';


--
-- TOC entry 383 (class 1255 OID 59419)
-- Dependencies: 6
-- Name: pgp_pub_encrypt_bytea(bytea, bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_pub_encrypt_bytea(bytea, bytea, text) RETURNS bytea
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pgp_pub_encrypt_bytea';


--
-- TOC entry 384 (class 1255 OID 59420)
-- Dependencies: 6
-- Name: pgp_sym_decrypt(bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_sym_decrypt(bytea, text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_sym_decrypt_text';


--
-- TOC entry 385 (class 1255 OID 59421)
-- Dependencies: 6
-- Name: pgp_sym_decrypt(bytea, text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_sym_decrypt(bytea, text, text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_sym_decrypt_text';


--
-- TOC entry 386 (class 1255 OID 59422)
-- Dependencies: 6
-- Name: pgp_sym_decrypt_bytea(bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_sym_decrypt_bytea(bytea, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_sym_decrypt_bytea';


--
-- TOC entry 387 (class 1255 OID 59423)
-- Dependencies: 6
-- Name: pgp_sym_decrypt_bytea(bytea, text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_sym_decrypt_bytea(bytea, text, text) RETURNS bytea
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pgcrypto', 'pgp_sym_decrypt_bytea';


--
-- TOC entry 388 (class 1255 OID 59424)
-- Dependencies: 6
-- Name: pgp_sym_encrypt(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_sym_encrypt(text, text) RETURNS bytea
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pgp_sym_encrypt_text';


--
-- TOC entry 389 (class 1255 OID 59425)
-- Dependencies: 6
-- Name: pgp_sym_encrypt(text, text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_sym_encrypt(text, text, text) RETURNS bytea
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pgp_sym_encrypt_text';


--
-- TOC entry 390 (class 1255 OID 59426)
-- Dependencies: 6
-- Name: pgp_sym_encrypt_bytea(bytea, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_sym_encrypt_bytea(bytea, text) RETURNS bytea
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pgp_sym_encrypt_bytea';


--
-- TOC entry 391 (class 1255 OID 59427)
-- Dependencies: 6
-- Name: pgp_sym_encrypt_bytea(bytea, text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgp_sym_encrypt_bytea(bytea, text, text) RETURNS bytea
    LANGUAGE c STRICT
    AS '$libdir/pgcrypto', 'pgp_sym_encrypt_bytea';


--
-- TOC entry 392 (class 1255 OID 59428)
-- Dependencies: 6 771
-- Name: pgstattuple(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgstattuple(text) RETURNS pgstattuple_type
    LANGUAGE c STRICT
    AS '$libdir/pgstattuple', 'pgstattuple';


--
-- TOC entry 393 (class 1255 OID 59429)
-- Dependencies: 6 771
-- Name: pgstattuple(oid); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION pgstattuple(oid) RETURNS pgstattuple_type
    LANGUAGE c STRICT
    AS '$libdir/pgstattuple', 'pgstattuplebyid';


--
-- TOC entry 394 (class 1255 OID 59430)
-- Dependencies: 6 1042
-- Name: postgis_full_version(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION postgis_full_version() RETURNS text
    LANGUAGE plpgsql IMMUTABLE
    AS $$
DECLARE
	libver text;
	projver text;
	geosver text;
	usestats bool;
	dbproc text;
	relproc text;
	fullver text;
BEGIN
	SELECT postgis_lib_version() INTO libver;
	SELECT postgis_proj_version() INTO projver;
	SELECT postgis_geos_version() INTO geosver;
	SELECT postgis_uses_stats() INTO usestats;
	SELECT postgis_scripts_installed() INTO dbproc;
	SELECT postgis_scripts_released() INTO relproc;

	fullver = 'POSTGIS="' || libver || '"';

	IF  geosver IS NOT NULL THEN
		fullver = fullver || ' GEOS="' || geosver || '"';
	END IF;

	IF  projver IS NOT NULL THEN
		fullver = fullver || ' PROJ="' || projver || '"';
	END IF;

	IF usestats THEN
		fullver = fullver || ' USE_STATS';
	END IF;

	fullver = fullver || ' DBPROC="' || dbproc || '"';
	fullver = fullver || ' RELPROC="' || relproc || '"';

	IF dbproc != relproc THEN
		fullver = fullver || ' (needs proc upgrade)';
	END IF;

	RETURN fullver;
END
$$;


--
-- TOC entry 395 (class 1255 OID 59431)
-- Dependencies: 6
-- Name: postgis_scripts_build_date(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION postgis_scripts_build_date() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$SELECT '2005-10-22 12:51:42'::text AS version$$;


--
-- TOC entry 396 (class 1255 OID 59432)
-- Dependencies: 6
-- Name: postgis_scripts_installed(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION postgis_scripts_installed() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$SELECT '0.3.0'::text AS version$$;


--
-- TOC entry 397 (class 1255 OID 59433)
-- Dependencies: 6
-- Name: postgis_version(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION postgis_version() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$SELECT '1.0 USE_GEOS=1 USE_PROJ=1 USE_STATS=1'::text AS version$$;


--
-- TOC entry 399 (class 1255 OID 59434)
-- Dependencies: 6 1042
-- Name: probe_geometry_columns(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION probe_geometry_columns() RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
	inserted integer;
	oldcount integer;
	probed integer;
	stale integer;
BEGIN

	SELECT count(*) INTO oldcount FROM geometry_columns;

	SELECT count(*) INTO probed
		FROM pg_class c, pg_attribute a, pg_type t, 

			pg_namespace n,
			pg_constraint sridcheck, pg_constraint typecheck


		WHERE t.typname = 'geometry'
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid

		AND c.relnamespace = n.oid
		AND sridcheck.connamespace = n.oid
		AND typecheck.connamespace = n.oid



		AND sridcheck.conrelid = c.oid
		AND sridcheck.consrc LIKE '(srid('||a.attname||') = %)'
		AND typecheck.conrelid = c.oid
		AND typecheck.consrc LIKE
	'((geometrytype('||a.attname||') = ''%''::text) OR (% IS NULL))'

		;

	INSERT INTO geometry_columns SELECT
		''::varchar as f_table_catalogue,

		n.nspname::varchar as f_table_schema,

		c.relname::varchar as f_table_name,
		a.attname::varchar as f_geometry_column,
		2 as coord_dimension,

		trim(both  ' =)' from substr(sridcheck.consrc,
			strpos(sridcheck.consrc, '=')))::integer as srid,
		trim(both ' =)''' from substr(typecheck.consrc, 
			strpos(typecheck.consrc, '='),
			strpos(typecheck.consrc, '::')-
			strpos(typecheck.consrc, '=')
			))::varchar as type



		FROM pg_class c, pg_attribute a, pg_type t, 

			pg_namespace n,
			pg_constraint sridcheck, pg_constraint typecheck

		WHERE t.typname = 'geometry'
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid

		AND c.relnamespace = n.oid
		AND sridcheck.connamespace = n.oid
		AND typecheck.connamespace = n.oid
		AND sridcheck.conrelid = c.oid
		AND sridcheck.consrc LIKE '(srid('||a.attname||') = %)'
		AND typecheck.conrelid = c.oid
		AND typecheck.consrc LIKE
	'((geometrytype('||a.attname||') = ''%''::text) OR (% IS NULL))'


                AND NOT EXISTS (
                        SELECT oid FROM geometry_columns gc
                        WHERE c.relname::varchar = gc.f_table_name

                        AND n.nspname::varchar = gc.f_table_schema

                        AND a.attname::varchar = gc.f_geometry_column
                );

	GET DIAGNOSTICS inserted = ROW_COUNT;

	IF oldcount > probed THEN
		stale = oldcount-probed;
	ELSE
		stale = 0;
	END IF;

        RETURN 'probed:'||probed||
		' inserted:'||inserted||
		' conflicts:'||probed-inserted||
		' stale:'||stale;
END

$$;


--
-- TOC entry 400 (class 1255 OID 59435)
-- Dependencies: 6 773
-- Name: querytree(query_int); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION querytree(query_int) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/_int', 'querytree';


--
-- TOC entry 401 (class 1255 OID 59436)
-- Dependencies: 6 729
-- Name: raw(chkpass); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION raw(chkpass) RETURNS text
    LANGUAGE c STRICT
    AS '$libdir/chkpass', 'chkpass_rout';


--
-- TOC entry 402 (class 1255 OID 59437)
-- Dependencies: 6 773
-- Name: rboolop(query_int, integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION rboolop(query_int, integer[]) RETURNS boolean
    LANGUAGE c STRICT
    AS '$libdir/_int', 'rboolop';


--
-- TOC entry 3141 (class 0 OID 0)
-- Dependencies: 402
-- Name: FUNCTION rboolop(query_int, integer[]); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION rboolop(query_int, integer[]) IS 'boolean operation with array';


--
-- TOC entry 403 (class 1255 OID 59438)
-- Dependencies: 6
-- Name: rename_geometry_table_constraints(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION rename_geometry_table_constraints() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$
SELECT 'rename_geometry_table_constraint() is obsoleted'::text
$$;


--
-- TOC entry 404 (class 1255 OID 59439)
-- Dependencies: 6 776 776
-- Name: seg_cmp(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_cmp(seg, seg) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_cmp';


--
-- TOC entry 3142 (class 0 OID 0)
-- Dependencies: 404
-- Name: FUNCTION seg_cmp(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_cmp(seg, seg) IS 'btree comparison function';


--
-- TOC entry 405 (class 1255 OID 59440)
-- Dependencies: 6 776 776
-- Name: seg_contained(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_contained(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_contained';


--
-- TOC entry 3143 (class 0 OID 0)
-- Dependencies: 405
-- Name: FUNCTION seg_contained(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_contained(seg, seg) IS 'contained in';


--
-- TOC entry 406 (class 1255 OID 59441)
-- Dependencies: 776 6 776
-- Name: seg_contains(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_contains(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_contains';


--
-- TOC entry 3144 (class 0 OID 0)
-- Dependencies: 406
-- Name: FUNCTION seg_contains(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_contains(seg, seg) IS 'contains';


--
-- TOC entry 407 (class 1255 OID 59442)
-- Dependencies: 776 6 776
-- Name: seg_different(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_different(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_different';


--
-- TOC entry 3145 (class 0 OID 0)
-- Dependencies: 407
-- Name: FUNCTION seg_different(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_different(seg, seg) IS 'different';


--
-- TOC entry 408 (class 1255 OID 59443)
-- Dependencies: 776 6 776
-- Name: seg_ge(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_ge(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_ge';


--
-- TOC entry 3146 (class 0 OID 0)
-- Dependencies: 408
-- Name: FUNCTION seg_ge(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_ge(seg, seg) IS 'greater than or equal';


--
-- TOC entry 409 (class 1255 OID 59444)
-- Dependencies: 776 6 776
-- Name: seg_gt(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_gt(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_gt';


--
-- TOC entry 3147 (class 0 OID 0)
-- Dependencies: 409
-- Name: FUNCTION seg_gt(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_gt(seg, seg) IS 'greater than';


--
-- TOC entry 410 (class 1255 OID 59445)
-- Dependencies: 776 776 6 776
-- Name: seg_inter(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_inter(seg, seg) RETURNS seg
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_inter';


--
-- TOC entry 411 (class 1255 OID 59446)
-- Dependencies: 776 6 776
-- Name: seg_le(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_le(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_le';


--
-- TOC entry 3148 (class 0 OID 0)
-- Dependencies: 411
-- Name: FUNCTION seg_le(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_le(seg, seg) IS 'less than or equal';


--
-- TOC entry 412 (class 1255 OID 59447)
-- Dependencies: 776 6 776
-- Name: seg_left(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_left(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_left';


--
-- TOC entry 3149 (class 0 OID 0)
-- Dependencies: 412
-- Name: FUNCTION seg_left(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_left(seg, seg) IS 'is left of';


--
-- TOC entry 413 (class 1255 OID 59448)
-- Dependencies: 776 6
-- Name: seg_lower(seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_lower(seg) RETURNS real
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_lower';


--
-- TOC entry 414 (class 1255 OID 59449)
-- Dependencies: 776 776 6
-- Name: seg_lt(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_lt(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_lt';


--
-- TOC entry 3150 (class 0 OID 0)
-- Dependencies: 414
-- Name: FUNCTION seg_lt(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_lt(seg, seg) IS 'less than';


--
-- TOC entry 415 (class 1255 OID 59450)
-- Dependencies: 776 776 6
-- Name: seg_over_left(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_over_left(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_over_left';


--
-- TOC entry 3151 (class 0 OID 0)
-- Dependencies: 415
-- Name: FUNCTION seg_over_left(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_over_left(seg, seg) IS 'overlaps or is left of';


--
-- TOC entry 416 (class 1255 OID 59451)
-- Dependencies: 776 776 6
-- Name: seg_over_right(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_over_right(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_over_right';


--
-- TOC entry 3152 (class 0 OID 0)
-- Dependencies: 416
-- Name: FUNCTION seg_over_right(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_over_right(seg, seg) IS 'overlaps or is right of';


--
-- TOC entry 417 (class 1255 OID 59452)
-- Dependencies: 776 6 776
-- Name: seg_overlap(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_overlap(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_overlap';


--
-- TOC entry 3153 (class 0 OID 0)
-- Dependencies: 417
-- Name: FUNCTION seg_overlap(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_overlap(seg, seg) IS 'overlaps';


--
-- TOC entry 418 (class 1255 OID 59453)
-- Dependencies: 776 776 6
-- Name: seg_right(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_right(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_right';


--
-- TOC entry 3154 (class 0 OID 0)
-- Dependencies: 418
-- Name: FUNCTION seg_right(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_right(seg, seg) IS 'is right of';


--
-- TOC entry 419 (class 1255 OID 59454)
-- Dependencies: 776 6 776
-- Name: seg_same(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_same(seg, seg) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_same';


--
-- TOC entry 3155 (class 0 OID 0)
-- Dependencies: 419
-- Name: FUNCTION seg_same(seg, seg); Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON FUNCTION seg_same(seg, seg) IS 'same as';


--
-- TOC entry 420 (class 1255 OID 59455)
-- Dependencies: 776 6
-- Name: seg_size(seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_size(seg) RETURNS real
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_size';


--
-- TOC entry 421 (class 1255 OID 59456)
-- Dependencies: 776 776 776 6
-- Name: seg_union(seg, seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_union(seg, seg) RETURNS seg
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_union';


--
-- TOC entry 422 (class 1255 OID 59457)
-- Dependencies: 776 6
-- Name: seg_upper(seg); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION seg_upper(seg) RETURNS real
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/seg', 'seg_upper';


--
-- TOC entry 423 (class 1255 OID 59458)
-- Dependencies: 6
-- Name: set_limit(real); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION set_limit(real) RETURNS real
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pg_trgm', 'set_limit';


--
-- TOC entry 424 (class 1255 OID 59459)
-- Dependencies: 6
-- Name: set_timetravel(name, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION set_timetravel(name, integer) RETURNS integer
    LANGUAGE c STRICT
    AS '$libdir/timetravel', 'set_timetravel';


--
-- TOC entry 425 (class 1255 OID 59460)
-- Dependencies: 6
-- Name: show_limit(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION show_limit() RETURNS real
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pg_trgm', 'show_limit';


--
-- TOC entry 426 (class 1255 OID 59461)
-- Dependencies: 6
-- Name: show_trgm(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION show_trgm(text) RETURNS text[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pg_trgm', 'show_trgm';


--
-- TOC entry 427 (class 1255 OID 59462)
-- Dependencies: 6
-- Name: similarity(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION similarity(text, text) RETURNS real
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pg_trgm', 'similarity';


--
-- TOC entry 428 (class 1255 OID 59463)
-- Dependencies: 6
-- Name: similarity_op(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION similarity_op(text, text) RETURNS boolean
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/pg_trgm', 'similarity_op';


--
-- TOC entry 429 (class 1255 OID 59464)
-- Dependencies: 6
-- Name: sort(integer[], text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION sort(integer[], text) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'sort';


--
-- TOC entry 430 (class 1255 OID 59465)
-- Dependencies: 6
-- Name: sort(integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION sort(integer[]) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'sort';


--
-- TOC entry 431 (class 1255 OID 59466)
-- Dependencies: 6
-- Name: sort_asc(integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION sort_asc(integer[]) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'sort_asc';


--
-- TOC entry 432 (class 1255 OID 59467)
-- Dependencies: 6
-- Name: sort_desc(integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION sort_desc(integer[]) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'sort_desc';


--
-- TOC entry 433 (class 1255 OID 59468)
-- Dependencies: 6
-- Name: soundex(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION soundex(text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'soundex';


--
-- TOC entry 434 (class 1255 OID 59469)
-- Dependencies: 6
-- Name: subarray(integer[], integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION subarray(integer[], integer, integer) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'subarray';


--
-- TOC entry 209 (class 1255 OID 59470)
-- Dependencies: 6
-- Name: subarray(integer[], integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION subarray(integer[], integer) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'subarray';


--
-- TOC entry 398 (class 1255 OID 59471)
-- Dependencies: 762 6 762
-- Name: subltree(ltree, integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION subltree(ltree, integer, integer) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'subltree';


--
-- TOC entry 435 (class 1255 OID 59472)
-- Dependencies: 6 762 762
-- Name: subpath(ltree, integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION subpath(ltree, integer, integer) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'subpath';


--
-- TOC entry 436 (class 1255 OID 59473)
-- Dependencies: 6 762 762
-- Name: subpath(ltree, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION subpath(ltree, integer) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'subpath';


--
-- TOC entry 437 (class 1255 OID 59474)
-- Dependencies: 762 6
-- Name: text2ltree(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION text2ltree(text) RETURNS ltree
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/ltree', 'text2ltree';


--
-- TOC entry 438 (class 1255 OID 59475)
-- Dependencies: 6
-- Name: text_soundex(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION text_soundex(text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'soundex';


--
-- TOC entry 439 (class 1255 OID 59476)
-- Dependencies: 6
-- Name: timetravel(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION timetravel() RETURNS trigger
    LANGUAGE c
    AS '$libdir/timetravel', 'timetravel';


--
-- TOC entry 440 (class 1255 OID 59477)
-- Dependencies: 6
-- Name: uniq(integer[]); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION uniq(integer[]) RETURNS integer[]
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/_int', 'uniq';


--
-- TOC entry 441 (class 1255 OID 59478)
-- Dependencies: 6
-- Name: update_geometry_stats(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION update_geometry_stats() RETURNS text
    LANGUAGE sql
    AS $$ SELECT 'update_geometry_stats() has been obsoleted. Statistics are automatically built running the ANALYZE command'::text$$;


--
-- TOC entry 442 (class 1255 OID 59479)
-- Dependencies: 6
-- Name: update_geometry_stats(character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION update_geometry_stats(character varying, character varying) RETURNS text
    LANGUAGE sql
    AS $$SELECT update_geometry_stats();$$;


--
-- TOC entry 443 (class 1255 OID 59480)
-- Dependencies: 6 1042
-- Name: updategeometrysrid(character varying, character varying, character varying, character varying, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION updategeometrysrid(character varying, character varying, character varying, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	catalog_name alias for $1; 
	schema_name alias for $2;
	table_name alias for $3;
	column_name alias for $4;
	new_srid alias for $5;
	myrec RECORD;
	okay boolean;
	cname varchar;
	real_schema name;

BEGIN



	-- Find, check or fix schema_name
	IF ( schema_name != '' ) THEN
		okay = 'f';

		FOR myrec IN SELECT nspname FROM pg_namespace WHERE text(nspname) = schema_name LOOP
			okay := 't';
		END LOOP;

		IF ( okay <> 't' ) THEN
			RAISE EXCEPTION 'Invalid schema name';
		ELSE
			real_schema = schema_name;
		END IF;
	ELSE
		SELECT INTO real_schema current_schema()::text;
	END IF;


 	-- Find out if the column is in the geometry_columns table
	okay = 'f';
	FOR myrec IN SELECT * from geometry_columns where f_table_schema = text(real_schema) and f_table_name = table_name and f_geometry_column = column_name LOOP
		okay := 't';
	END LOOP; 
	IF (okay <> 't') THEN 
		RAISE EXCEPTION 'column not found in geometry_columns table';
		RETURN 'f';
	END IF;

	-- Update ref from geometry_columns table
	EXECUTE 'UPDATE geometry_columns SET SRID = ' || new_srid || 
		' where f_table_schema = ' ||
		quote_literal(real_schema) || ' and f_table_name = ' ||
		quote_literal(table_name)  || ' and f_geometry_column = ' ||
		quote_literal(column_name);
	
	-- Make up constraint name
	cname = 'enforce_srid_'  || column_name;

	-- Drop enforce_srid constraint

	EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) ||
		'.' || quote_ident(table_name) ||

		' DROP constraint ' || quote_ident(cname);

	-- Update geometries SRID

	EXECUTE 'UPDATE ' || quote_ident(real_schema) ||
		'.' || quote_ident(table_name) ||

		' SET ' || quote_ident(column_name) ||
		' = setSRID(' || quote_ident(column_name) ||
		', ' || new_srid || ')';

	-- Reset enforce_srid constraint

	EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) ||
		'.' || quote_ident(table_name) ||

		' ADD constraint ' || quote_ident(cname) ||
		' CHECK (srid(' || quote_ident(column_name) ||
		') = ' || new_srid || ')';

	RETURN real_schema || '.' || table_name || '.' || column_name ||' SRID changed to ' || new_srid;
	
END;
$_$;


--
-- TOC entry 444 (class 1255 OID 59481)
-- Dependencies: 6 1042
-- Name: updategeometrysrid(character varying, character varying, character varying, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION updategeometrysrid(character varying, character varying, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT UpdateGeometrySRID('',$1,$2,$3,$4) into ret;
	RETURN ret;
END;
$_$;


--
-- TOC entry 445 (class 1255 OID 59482)
-- Dependencies: 1042 6
-- Name: updategeometrysrid(character varying, character varying, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION updategeometrysrid(character varying, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT UpdateGeometrySRID('','',$1,$2,$3) into ret;
	RETURN ret;
END;
$_$;


--
-- TOC entry 1747 (class 2617 OID 59483)
-- Dependencies: 6 320
-- Name: #; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR # (
    PROCEDURE = icount,
    RIGHTARG = integer[]
);


--
-- TOC entry 1748 (class 2617 OID 59484)
-- Dependencies: 6 321
-- Name: #; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR # (
    PROCEDURE = idx,
    LEFTARG = integer[],
    RIGHTARG = integer
);


--
-- TOC entry 1749 (class 2617 OID 59485)
-- Dependencies: 428 6
-- Name: %; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR % (
    PROCEDURE = similarity_op,
    LEFTARG = text,
    RIGHTARG = text,
    COMMUTATOR = %,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1750 (class 2617 OID 59486)
-- Dependencies: 53 6
-- Name: &; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR & (
    PROCEDURE = _int_inter,
    LEFTARG = integer[],
    RIGHTARG = integer[],
    COMMUTATOR = &
);


--
-- TOC entry 1751 (class 2617 OID 59487)
-- Dependencies: 732 115 732 6
-- Name: &&; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR && (
    PROCEDURE = cube_overlap,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = &&,
    RESTRICT = areasel,
    JOIN = areajoinsel
);


--
-- TOC entry 1752 (class 2617 OID 59488)
-- Dependencies: 6 54
-- Name: &&; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR && (
    PROCEDURE = _int_overlap,
    LEFTARG = integer[],
    RIGHTARG = integer[],
    COMMUTATOR = &&,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1753 (class 2617 OID 59489)
-- Dependencies: 776 6 776 417
-- Name: &&; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR && (
    PROCEDURE = seg_overlap,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = &&,
    RESTRICT = areasel,
    JOIN = areajoinsel
);


--
-- TOC entry 1754 (class 2617 OID 59490)
-- Dependencies: 776 6 415 776
-- Name: &<; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR &< (
    PROCEDURE = seg_over_left,
    LEFTARG = seg,
    RIGHTARG = seg,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


--
-- TOC entry 1755 (class 2617 OID 59491)
-- Dependencies: 6 776 776 416
-- Name: &>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR &> (
    PROCEDURE = seg_over_right,
    LEFTARG = seg,
    RIGHTARG = seg,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


--
-- TOC entry 1756 (class 2617 OID 59492)
-- Dependencies: 6 327
-- Name: +; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR + (
    PROCEDURE = intarray_push_elem,
    LEFTARG = integer[],
    RIGHTARG = integer
);


--
-- TOC entry 1757 (class 2617 OID 59493)
-- Dependencies: 326 6
-- Name: +; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR + (
    PROCEDURE = intarray_push_array,
    LEFTARG = integer[],
    RIGHTARG = integer[],
    COMMUTATOR = +
);


--
-- TOC entry 1758 (class 2617 OID 59494)
-- Dependencies: 6 325
-- Name: -; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR - (
    PROCEDURE = intarray_del_elem,
    LEFTARG = integer[],
    RIGHTARG = integer
);


--
-- TOC entry 1759 (class 2617 OID 59495)
-- Dependencies: 6 329
-- Name: -; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR - (
    PROCEDURE = intset_subtract,
    LEFTARG = integer[],
    RIGHTARG = integer[]
);


--
-- TOC entry 1760 (class 2617 OID 59498)
-- Dependencies: 6 81 732 732
-- Name: <; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR < (
    PROCEDURE = cube_lt,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = >,
    NEGATOR = >=,
    RESTRICT = scalarltsel,
    JOIN = scalarltjoinsel
);


--
-- TOC entry 1762 (class 2617 OID 59501)
-- Dependencies: 762 358 6 762
-- Name: <; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR < (
    PROCEDURE = ltree_lt,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = >,
    NEGATOR = >=,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1764 (class 2617 OID 59504)
-- Dependencies: 776 776 6 414
-- Name: <; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR < (
    PROCEDURE = seg_lt,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = >,
    NEGATOR = >=,
    RESTRICT = scalarltsel,
    JOIN = scalarltjoinsel
);


--
-- TOC entry 1766 (class 2617 OID 59506)
-- Dependencies: 776 412 6 776
-- Name: <<; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR << (
    PROCEDURE = seg_left,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = >>,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


--
-- TOC entry 1767 (class 2617 OID 59507)
-- Dependencies: 732 6 732 79
-- Name: <=; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <= (
    PROCEDURE = cube_le,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = >=,
    NEGATOR = >,
    RESTRICT = scalarltsel,
    JOIN = scalarltjoinsel
);


--
-- TOC entry 1769 (class 2617 OID 59508)
-- Dependencies: 762 762 357 6
-- Name: <=; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <= (
    PROCEDURE = ltree_le,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = >=,
    NEGATOR = >,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1772 (class 2617 OID 59509)
-- Dependencies: 776 411 6 776
-- Name: <=; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <= (
    PROCEDURE = seg_le,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = >=,
    NEGATOR = >,
    RESTRICT = scalarltsel,
    JOIN = scalarltjoinsel
);


--
-- TOC entry 1775 (class 2617 OID 59511)
-- Dependencies: 6 370 729
-- Name: <>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <> (
    PROCEDURE = ne,
    LEFTARG = chkpass,
    RIGHTARG = text,
    NEGATOR = =
);


--
-- TOC entry 1777 (class 2617 OID 59513)
-- Dependencies: 732 82 6 732
-- Name: <>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <> (
    PROCEDURE = cube_ne,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = <>,
    NEGATOR = =,
    RESTRICT = neqsel,
    JOIN = neqjoinsel
);


--
-- TOC entry 1778 (class 2617 OID 59515)
-- Dependencies: 359 762 762 6
-- Name: <>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <> (
    PROCEDURE = ltree_ne,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = <>,
    NEGATOR = =,
    RESTRICT = neqsel,
    JOIN = neqjoinsel
);


--
-- TOC entry 1780 (class 2617 OID 59517)
-- Dependencies: 407 776 776 6
-- Name: <>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <> (
    PROCEDURE = seg_different,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = <>,
    NEGATOR = =,
    RESTRICT = neqsel,
    JOIN = neqjoinsel
);


--
-- TOC entry 1782 (class 2617 OID 59519)
-- Dependencies: 362 6 762 762
-- Name: <@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <@ (
    PROCEDURE = ltree_risparent,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = @>,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1784 (class 2617 OID 59521)
-- Dependencies: 762 764 69 6
-- Name: <@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <@ (
    PROCEDURE = _ltree_r_isparent,
    LEFTARG = ltree,
    RIGHTARG = ltree[],
    COMMUTATOR = @>,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1763 (class 2617 OID 59523)
-- Dependencies: 762 764 6 71
-- Name: <@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR <@ (
    PROCEDURE = _ltree_risparent,
    LEFTARG = ltree[],
    RIGHTARG = ltree,
    COMMUTATOR = @>,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1765 (class 2617 OID 59510)
-- Dependencies: 164 6 729
-- Name: =; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR = (
    PROCEDURE = eq,
    LEFTARG = chkpass,
    RIGHTARG = text,
    COMMUTATOR = =,
    NEGATOR = <>
);


--
-- TOC entry 1786 (class 2617 OID 59512)
-- Dependencies: 732 6 112 732
-- Name: =; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR = (
    PROCEDURE = cube_eq,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = =,
    NEGATOR = <>,
    MERGES,
    RESTRICT = eqsel,
    JOIN = eqjoinsel
);


--
-- TOC entry 1787 (class 2617 OID 59514)
-- Dependencies: 353 6 762 762
-- Name: =; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR = (
    PROCEDURE = ltree_eq,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = =,
    NEGATOR = <>,
    MERGES,
    RESTRICT = eqsel,
    JOIN = eqjoinsel
);


--
-- TOC entry 1788 (class 2617 OID 59516)
-- Dependencies: 776 419 776 6
-- Name: =; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR = (
    PROCEDURE = seg_same,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = =,
    NEGATOR = <>,
    MERGES,
    RESTRICT = eqsel,
    JOIN = eqjoinsel
);


--
-- TOC entry 1789 (class 2617 OID 59496)
-- Dependencies: 6 114 732 732
-- Name: >; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR > (
    PROCEDURE = cube_gt,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = <,
    NEGATOR = <=,
    RESTRICT = scalargtsel,
    JOIN = scalargtjoinsel
);


--
-- TOC entry 1790 (class 2617 OID 59499)
-- Dependencies: 762 355 6 762
-- Name: >; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR > (
    PROCEDURE = ltree_gt,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = <,
    NEGATOR = <=,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1791 (class 2617 OID 59502)
-- Dependencies: 776 6 409 776
-- Name: >; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR > (
    PROCEDURE = seg_gt,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = <,
    NEGATOR = <=,
    RESTRICT = scalargtsel,
    JOIN = scalargtjoinsel
);


--
-- TOC entry 1792 (class 2617 OID 59497)
-- Dependencies: 732 113 6 732
-- Name: >=; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR >= (
    PROCEDURE = cube_ge,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = <=,
    NEGATOR = <,
    RESTRICT = scalargtsel,
    JOIN = scalargtjoinsel
);


--
-- TOC entry 1793 (class 2617 OID 59500)
-- Dependencies: 762 6 762 354
-- Name: >=; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR >= (
    PROCEDURE = ltree_ge,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = <=,
    NEGATOR = <,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1794 (class 2617 OID 59503)
-- Dependencies: 6 408 776 776
-- Name: >=; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR >= (
    PROCEDURE = seg_ge,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = <=,
    NEGATOR = <,
    RESTRICT = scalargtsel,
    JOIN = scalargtjoinsel
);


--
-- TOC entry 1795 (class 2617 OID 59505)
-- Dependencies: 776 418 776 6
-- Name: >>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR >> (
    PROCEDURE = seg_right,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = <<,
    RESTRICT = positionsel,
    JOIN = positionjoinsel
);


--
-- TOC entry 1797 (class 2617 OID 59525)
-- Dependencies: 762 342 761 6
-- Name: ?; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ? (
    PROCEDURE = lt_q_regex,
    LEFTARG = ltree,
    RIGHTARG = lquery[],
    COMMUTATOR = ?,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1796 (class 2617 OID 59526)
-- Dependencies: 762 6 761 343
-- Name: ?; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ? (
    PROCEDURE = lt_q_rregex,
    LEFTARG = lquery[],
    RIGHTARG = ltree,
    COMMUTATOR = ?,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1770 (class 2617 OID 59527)
-- Dependencies: 6 761 57 764
-- Name: ?; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ? (
    PROCEDURE = _lt_q_regex,
    LEFTARG = ltree[],
    RIGHTARG = lquery[],
    COMMUTATOR = ?,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1768 (class 2617 OID 59528)
-- Dependencies: 6 764 58 761
-- Name: ?; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ? (
    PROCEDURE = _lt_q_rregex,
    LEFTARG = lquery[],
    RIGHTARG = ltree[],
    COMMUTATOR = ?,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1771 (class 2617 OID 59529)
-- Dependencies: 764 762 762 65 6
-- Name: ?<@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ?<@ (
    PROCEDURE = _ltree_extract_risparent,
    LEFTARG = ltree[],
    RIGHTARG = ltree
);


--
-- TOC entry 1773 (class 2617 OID 59530)
-- Dependencies: 75 762 768 764 6
-- Name: ?@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ?@ (
    PROCEDURE = _ltxtq_extract_exec,
    LEFTARG = ltree[],
    RIGHTARG = ltxtquery
);


--
-- TOC entry 1774 (class 2617 OID 59531)
-- Dependencies: 762 64 762 6 764
-- Name: ?@>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ?@> (
    PROCEDURE = _ltree_extract_isparent,
    LEFTARG = ltree[],
    RIGHTARG = ltree
);


--
-- TOC entry 1776 (class 2617 OID 59532)
-- Dependencies: 762 59 6 764 759
-- Name: ?~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ?~ (
    PROCEDURE = _ltq_extract_regex,
    LEFTARG = ltree[],
    RIGHTARG = lquery
);


--
-- TOC entry 1779 (class 2617 OID 59534)
-- Dependencies: 732 108 6 732
-- Name: @; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @ (
    PROCEDURE = cube_contains,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = ~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1798 (class 2617 OID 59536)
-- Dependencies: 51 6
-- Name: @; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @ (
    PROCEDURE = _int_contains,
    LEFTARG = integer[],
    RIGHTARG = integer[],
    COMMUTATOR = ~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1801 (class 2617 OID 59537)
-- Dependencies: 762 768 366 6
-- Name: @; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @ (
    PROCEDURE = ltxtq_exec,
    LEFTARG = ltree,
    RIGHTARG = ltxtquery,
    COMMUTATOR = @,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1800 (class 2617 OID 59538)
-- Dependencies: 367 6 768 762
-- Name: @; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @ (
    PROCEDURE = ltxtq_rexec,
    LEFTARG = ltxtquery,
    RIGHTARG = ltree,
    COMMUTATOR = @,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1803 (class 2617 OID 59539)
-- Dependencies: 764 768 74 6
-- Name: @; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @ (
    PROCEDURE = _ltxtq_exec,
    LEFTARG = ltree[],
    RIGHTARG = ltxtquery,
    COMMUTATOR = @,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1802 (class 2617 OID 59540)
-- Dependencies: 6 764 76 768
-- Name: @; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @ (
    PROCEDURE = _ltxtq_rexec,
    LEFTARG = ltxtquery,
    RIGHTARG = ltree[],
    COMMUTATOR = @,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1804 (class 2617 OID 59542)
-- Dependencies: 776 406 776 6
-- Name: @; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @ (
    PROCEDURE = seg_contains,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = ~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1806 (class 2617 OID 59518)
-- Dependencies: 6 762 356 762
-- Name: @>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @> (
    PROCEDURE = ltree_isparent,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = <@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1783 (class 2617 OID 59520)
-- Dependencies: 66 6 764 762
-- Name: @>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @> (
    PROCEDURE = _ltree_isparent,
    LEFTARG = ltree[],
    RIGHTARG = ltree,
    COMMUTATOR = <@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1761 (class 2617 OID 59522)
-- Dependencies: 764 6 70 762
-- Name: @>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @> (
    PROCEDURE = _ltree_r_risparent,
    LEFTARG = ltree,
    RIGHTARG = ltree[],
    COMMUTATOR = <@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1807 (class 2617 OID 59544)
-- Dependencies: 88 773 6
-- Name: @@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR @@ (
    PROCEDURE = boolop,
    LEFTARG = integer[],
    RIGHTARG = query_int,
    COMMUTATOR = ~~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1808 (class 2617 OID 59546)
-- Dependencies: 6 762 762 362
-- Name: ^<@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^<@ (
    PROCEDURE = ltree_risparent,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = ^@>,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1810 (class 2617 OID 59548)
-- Dependencies: 762 69 764 6
-- Name: ^<@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^<@ (
    PROCEDURE = _ltree_r_isparent,
    LEFTARG = ltree,
    RIGHTARG = ltree[],
    COMMUTATOR = ^@>,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1811 (class 2617 OID 59550)
-- Dependencies: 6 71 762 764
-- Name: ^<@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^<@ (
    PROCEDURE = _ltree_risparent,
    LEFTARG = ltree[],
    RIGHTARG = ltree,
    COMMUTATOR = ^@>,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1813 (class 2617 OID 59551)
-- Dependencies: 6 761 342 762
-- Name: ^?; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^? (
    PROCEDURE = lt_q_regex,
    LEFTARG = ltree,
    RIGHTARG = lquery[],
    COMMUTATOR = ^?,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1812 (class 2617 OID 59552)
-- Dependencies: 762 6 761 343
-- Name: ^?; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^? (
    PROCEDURE = lt_q_rregex,
    LEFTARG = lquery[],
    RIGHTARG = ltree,
    COMMUTATOR = ^?,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1815 (class 2617 OID 59553)
-- Dependencies: 6 57 761 764
-- Name: ^?; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^? (
    PROCEDURE = _lt_q_regex,
    LEFTARG = ltree[],
    RIGHTARG = lquery[],
    COMMUTATOR = ^?,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1814 (class 2617 OID 59554)
-- Dependencies: 764 761 58 6
-- Name: ^?; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^? (
    PROCEDURE = _lt_q_rregex,
    LEFTARG = lquery[],
    RIGHTARG = ltree[],
    COMMUTATOR = ^?,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1817 (class 2617 OID 59555)
-- Dependencies: 366 768 762 6
-- Name: ^@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^@ (
    PROCEDURE = ltxtq_exec,
    LEFTARG = ltree,
    RIGHTARG = ltxtquery,
    COMMUTATOR = ^@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1816 (class 2617 OID 59556)
-- Dependencies: 367 6 768 762
-- Name: ^@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^@ (
    PROCEDURE = ltxtq_rexec,
    LEFTARG = ltxtquery,
    RIGHTARG = ltree,
    COMMUTATOR = ^@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1819 (class 2617 OID 59557)
-- Dependencies: 74 768 6 764
-- Name: ^@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^@ (
    PROCEDURE = _ltxtq_exec,
    LEFTARG = ltree[],
    RIGHTARG = ltxtquery,
    COMMUTATOR = ^@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1818 (class 2617 OID 59558)
-- Dependencies: 768 76 764 6
-- Name: ^@; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^@ (
    PROCEDURE = _ltxtq_rexec,
    LEFTARG = ltxtquery,
    RIGHTARG = ltree[],
    COMMUTATOR = ^@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1820 (class 2617 OID 59545)
-- Dependencies: 762 6 356 762
-- Name: ^@>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^@> (
    PROCEDURE = ltree_isparent,
    LEFTARG = ltree,
    RIGHTARG = ltree,
    COMMUTATOR = ^<@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1821 (class 2617 OID 59547)
-- Dependencies: 66 6 764 762
-- Name: ^@>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^@> (
    PROCEDURE = _ltree_isparent,
    LEFTARG = ltree[],
    RIGHTARG = ltree,
    COMMUTATOR = ^<@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1822 (class 2617 OID 59549)
-- Dependencies: 70 6 762 764
-- Name: ^@>; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^@> (
    PROCEDURE = _ltree_r_risparent,
    LEFTARG = ltree,
    RIGHTARG = ltree[],
    COMMUTATOR = ^<@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1824 (class 2617 OID 59559)
-- Dependencies: 6 762 759 344
-- Name: ^~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^~ (
    PROCEDURE = ltq_regex,
    LEFTARG = ltree,
    RIGHTARG = lquery,
    COMMUTATOR = ^~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1823 (class 2617 OID 59560)
-- Dependencies: 6 759 762 345
-- Name: ^~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^~ (
    PROCEDURE = ltq_rregex,
    LEFTARG = lquery,
    RIGHTARG = ltree,
    COMMUTATOR = ^~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1826 (class 2617 OID 59561)
-- Dependencies: 6 764 759 60
-- Name: ^~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^~ (
    PROCEDURE = _ltq_regex,
    LEFTARG = ltree[],
    RIGHTARG = lquery,
    COMMUTATOR = ^~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1825 (class 2617 OID 59562)
-- Dependencies: 61 6 759 764
-- Name: ^~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ^~ (
    PROCEDURE = _ltq_rregex,
    LEFTARG = lquery,
    RIGHTARG = ltree[],
    COMMUTATOR = ^~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1827 (class 2617 OID 59563)
-- Dependencies: 6 330
-- Name: |; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR | (
    PROCEDURE = intset_union_elem,
    LEFTARG = integer[],
    RIGHTARG = integer
);


--
-- TOC entry 1828 (class 2617 OID 59564)
-- Dependencies: 56 6
-- Name: |; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR | (
    PROCEDURE = _int_union,
    LEFTARG = integer[],
    RIGHTARG = integer[],
    COMMUTATOR = |
);


--
-- TOC entry 1829 (class 2617 OID 59565)
-- Dependencies: 347 762 762 762 6
-- Name: ||; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR || (
    PROCEDURE = ltree_addltree,
    LEFTARG = ltree,
    RIGHTARG = ltree
);


--
-- TOC entry 1830 (class 2617 OID 59566)
-- Dependencies: 348 762 6 762
-- Name: ||; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR || (
    PROCEDURE = ltree_addtext,
    LEFTARG = ltree,
    RIGHTARG = text
);


--
-- TOC entry 1831 (class 2617 OID 59567)
-- Dependencies: 6 762 762 364
-- Name: ||; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR || (
    PROCEDURE = ltree_textadd,
    LEFTARG = text,
    RIGHTARG = ltree
);


--
-- TOC entry 1809 (class 2617 OID 59533)
-- Dependencies: 732 6 107 732
-- Name: ~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ~ (
    PROCEDURE = cube_contained,
    LEFTARG = cube,
    RIGHTARG = cube,
    COMMUTATOR = @,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1781 (class 2617 OID 59535)
-- Dependencies: 6 50
-- Name: ~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ~ (
    PROCEDURE = _int_contained,
    LEFTARG = integer[],
    RIGHTARG = integer[],
    COMMUTATOR = @,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1799 (class 2617 OID 59541)
-- Dependencies: 6 776 776 405
-- Name: ~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ~ (
    PROCEDURE = seg_contained,
    LEFTARG = seg,
    RIGHTARG = seg,
    COMMUTATOR = @,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1833 (class 2617 OID 59568)
-- Dependencies: 344 6 762 759
-- Name: ~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ~ (
    PROCEDURE = ltq_regex,
    LEFTARG = ltree,
    RIGHTARG = lquery,
    COMMUTATOR = ~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1832 (class 2617 OID 59569)
-- Dependencies: 6 759 762 345
-- Name: ~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ~ (
    PROCEDURE = ltq_rregex,
    LEFTARG = lquery,
    RIGHTARG = ltree,
    COMMUTATOR = ~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1835 (class 2617 OID 59570)
-- Dependencies: 764 6 60 759
-- Name: ~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ~ (
    PROCEDURE = _ltq_regex,
    LEFTARG = ltree[],
    RIGHTARG = lquery,
    COMMUTATOR = ~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1834 (class 2617 OID 59571)
-- Dependencies: 61 764 6 759
-- Name: ~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ~ (
    PROCEDURE = _ltq_rregex,
    LEFTARG = lquery,
    RIGHTARG = ltree[],
    COMMUTATOR = ~,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1805 (class 2617 OID 59543)
-- Dependencies: 773 6 402
-- Name: ~~; Type: OPERATOR; Schema: public; Owner: -
--

CREATE OPERATOR ~~ (
    PROCEDURE = rboolop,
    LEFTARG = query_int,
    RIGHTARG = integer[],
    COMMUTATOR = @@,
    RESTRICT = contsel,
    JOIN = contjoinsel
);


--
-- TOC entry 1947 (class 2616 OID 59573)
-- Dependencies: 2084 6 732
-- Name: cube_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS cube_ops
    DEFAULT FOR TYPE cube USING btree AS
    OPERATOR 1 <(cube,cube) ,
    OPERATOR 2 <=(cube,cube) ,
    OPERATOR 3 =(cube,cube) ,
    OPERATOR 4 >=(cube,cube) ,
    OPERATOR 5 >(cube,cube) ,
    FUNCTION 1 cube_cmp(cube,cube);


--
-- TOC entry 1948 (class 2616 OID 59581)
-- Dependencies: 2085 6
-- Name: gist__int_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist__int_ops
    DEFAULT FOR TYPE integer[] USING gist AS
    OPERATOR 3 &&(integer[],integer[]) ,
    OPERATOR 6 =(anyarray,anyarray) ,
    OPERATOR 7 @(integer[],integer[]) ,
    OPERATOR 8 ~(integer[],integer[]) ,
    OPERATOR 20 @@(integer[],query_int) ,
    FUNCTION 1 g_int_consistent(internal,integer[],integer) ,
    FUNCTION 2 g_int_union(internal,internal) ,
    FUNCTION 3 g_int_compress(internal) ,
    FUNCTION 4 g_int_decompress(internal) ,
    FUNCTION 5 g_int_penalty(internal,internal,internal) ,
    FUNCTION 6 g_int_picksplit(internal,internal) ,
    FUNCTION 7 g_int_same(integer[],integer[],internal);


--
-- TOC entry 1949 (class 2616 OID 59595)
-- Dependencies: 2086 6 755
-- Name: gist__intbig_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist__intbig_ops
    FOR TYPE integer[] USING gist AS
    STORAGE intbig_gkey ,
    OPERATOR 3 &&(integer[],integer[]) ,
    OPERATOR 6 =(anyarray,anyarray) ,
    OPERATOR 7 @(integer[],integer[]) ,
    OPERATOR 8 ~(integer[],integer[]) ,
    OPERATOR 20 @@(integer[],query_int) ,
    FUNCTION 1 g_intbig_consistent(internal,internal,integer) ,
    FUNCTION 2 g_intbig_union(internal,internal) ,
    FUNCTION 3 g_intbig_compress(internal) ,
    FUNCTION 4 g_intbig_decompress(internal) ,
    FUNCTION 5 g_intbig_penalty(internal,internal,internal) ,
    FUNCTION 6 g_intbig_picksplit(internal,internal) ,
    FUNCTION 7 g_intbig_same(internal,internal,internal);


--
-- TOC entry 1950 (class 2616 OID 59609)
-- Dependencies: 764 2087 6 765
-- Name: gist__ltree_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist__ltree_ops
    DEFAULT FOR TYPE ltree[] USING gist AS
    STORAGE ltree_gist ,
    OPERATOR 10 <@(ltree[],ltree) ,
    OPERATOR 11 @>(ltree,ltree[]) ,
    OPERATOR 12 ~(ltree[],lquery) ,
    OPERATOR 13 ~(lquery,ltree[]) ,
    OPERATOR 14 @(ltree[],ltxtquery) ,
    OPERATOR 15 @(ltxtquery,ltree[]) ,
    OPERATOR 16 ?(ltree[],lquery[]) ,
    OPERATOR 17 ?(lquery[],ltree[]) ,
    FUNCTION 1 _ltree_consistent(internal,internal,smallint) ,
    FUNCTION 2 _ltree_union(internal,internal) ,
    FUNCTION 3 _ltree_compress(internal) ,
    FUNCTION 4 ltree_decompress(internal) ,
    FUNCTION 5 _ltree_penalty(internal,internal,internal) ,
    FUNCTION 6 _ltree_picksplit(internal,internal) ,
    FUNCTION 7 _ltree_same(internal,internal,internal);


--
-- TOC entry 1951 (class 2616 OID 59626)
-- Dependencies: 2088 6 749
-- Name: gist_bit_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_bit_ops
    DEFAULT FOR TYPE bit USING gist AS
    STORAGE gbtreekey_var ,
    OPERATOR 1 <(bit,bit) ,
    OPERATOR 2 <=(bit,bit) ,
    OPERATOR 3 =(bit,bit) ,
    OPERATOR 4 >=(bit,bit) ,
    OPERATOR 5 >(bit,bit) ,
    FUNCTION 1 gbt_bit_consistent(internal,bit,smallint) ,
    FUNCTION 2 gbt_bit_union(bytea,internal) ,
    FUNCTION 3 gbt_bit_compress(internal) ,
    FUNCTION 4 gbt_var_decompress(internal) ,
    FUNCTION 5 gbt_bit_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_bit_picksplit(internal,internal) ,
    FUNCTION 7 gbt_bit_same(internal,internal,internal);


--
-- TOC entry 1952 (class 2616 OID 59640)
-- Dependencies: 749 2089 6
-- Name: gist_bpchar_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_bpchar_ops
    DEFAULT FOR TYPE character USING gist AS
    STORAGE gbtreekey_var ,
    OPERATOR 1 <(character,character) ,
    OPERATOR 2 <=(character,character) ,
    OPERATOR 3 =(character,character) ,
    OPERATOR 4 >=(character,character) ,
    OPERATOR 5 >(character,character) ,
    FUNCTION 1 gbt_bpchar_consistent(internal,character,smallint) ,
    FUNCTION 2 gbt_text_union(bytea,internal) ,
    FUNCTION 3 gbt_bpchar_compress(internal) ,
    FUNCTION 4 gbt_var_decompress(internal) ,
    FUNCTION 5 gbt_text_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_text_picksplit(internal,internal) ,
    FUNCTION 7 gbt_text_same(internal,internal,internal);


--
-- TOC entry 1953 (class 2616 OID 59654)
-- Dependencies: 6 749 2090
-- Name: gist_bytea_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_bytea_ops
    DEFAULT FOR TYPE bytea USING gist AS
    STORAGE gbtreekey_var ,
    OPERATOR 1 <(bytea,bytea) ,
    OPERATOR 2 <=(bytea,bytea) ,
    OPERATOR 3 =(bytea,bytea) ,
    OPERATOR 4 >=(bytea,bytea) ,
    OPERATOR 5 >(bytea,bytea) ,
    FUNCTION 1 gbt_bytea_consistent(internal,bytea,smallint) ,
    FUNCTION 2 gbt_bytea_union(bytea,internal) ,
    FUNCTION 3 gbt_bytea_compress(internal) ,
    FUNCTION 4 gbt_var_decompress(internal) ,
    FUNCTION 5 gbt_bytea_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_bytea_picksplit(internal,internal) ,
    FUNCTION 7 gbt_bytea_same(internal,internal,internal);


--
-- TOC entry 1954 (class 2616 OID 59668)
-- Dependencies: 6 746 2091
-- Name: gist_cash_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_cash_ops
    DEFAULT FOR TYPE money USING gist AS
    STORAGE gbtreekey8 ,
    OPERATOR 1 <(money,money) ,
    OPERATOR 2 <=(money,money) ,
    OPERATOR 3 =(money,money) ,
    OPERATOR 4 >=(money,money) ,
    OPERATOR 5 >(money,money) ,
    FUNCTION 1 gbt_cash_consistent(internal,money,smallint) ,
    FUNCTION 2 gbt_cash_union(bytea,internal) ,
    FUNCTION 3 gbt_cash_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_cash_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_cash_picksplit(internal,internal) ,
    FUNCTION 7 gbt_cash_same(internal,internal,internal);


--
-- TOC entry 1955 (class 2616 OID 59682)
-- Dependencies: 2092 6 732
-- Name: gist_cube_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_cube_ops
    DEFAULT FOR TYPE cube USING gist AS
    OPERATOR 3 &&(cube,cube) ,
    OPERATOR 6 =(cube,cube) ,
    OPERATOR 7 @(cube,cube) ,
    OPERATOR 8 ~(cube,cube) ,
    FUNCTION 1 g_cube_consistent(internal,cube,integer) ,
    FUNCTION 2 g_cube_union(internal,internal) ,
    FUNCTION 3 g_cube_compress(internal) ,
    FUNCTION 4 g_cube_decompress(internal) ,
    FUNCTION 5 g_cube_penalty(internal,internal,internal) ,
    FUNCTION 6 g_cube_picksplit(internal,internal) ,
    FUNCTION 7 g_cube_same(cube,cube,internal);


--
-- TOC entry 1956 (class 2616 OID 59695)
-- Dependencies: 746 6 2093
-- Name: gist_date_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_date_ops
    DEFAULT FOR TYPE date USING gist AS
    STORAGE gbtreekey8 ,
    OPERATOR 1 <(date,date) ,
    OPERATOR 2 <=(date,date) ,
    OPERATOR 3 =(date,date) ,
    OPERATOR 4 >=(date,date) ,
    OPERATOR 5 >(date,date) ,
    FUNCTION 1 gbt_date_consistent(internal,date,smallint) ,
    FUNCTION 2 gbt_date_union(bytea,internal) ,
    FUNCTION 3 gbt_date_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_date_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_date_picksplit(internal,internal) ,
    FUNCTION 7 gbt_date_same(internal,internal,internal);


--
-- TOC entry 1957 (class 2616 OID 59709)
-- Dependencies: 746 2094 6
-- Name: gist_float4_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_float4_ops
    DEFAULT FOR TYPE real USING gist AS
    STORAGE gbtreekey8 ,
    OPERATOR 1 <(real,real) ,
    OPERATOR 2 <=(real,real) ,
    OPERATOR 3 =(real,real) ,
    OPERATOR 4 >=(real,real) ,
    OPERATOR 5 >(real,real) ,
    FUNCTION 1 gbt_float4_consistent(internal,real,smallint) ,
    FUNCTION 2 gbt_float4_union(bytea,internal) ,
    FUNCTION 3 gbt_float4_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_float4_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_float4_picksplit(internal,internal) ,
    FUNCTION 7 gbt_float4_same(internal,internal,internal);


--
-- TOC entry 1958 (class 2616 OID 59723)
-- Dependencies: 6 737 2095
-- Name: gist_float8_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_float8_ops
    DEFAULT FOR TYPE double precision USING gist AS
    STORAGE gbtreekey16 ,
    OPERATOR 1 <(double precision,double precision) ,
    OPERATOR 2 <=(double precision,double precision) ,
    OPERATOR 3 =(double precision,double precision) ,
    OPERATOR 4 >=(double precision,double precision) ,
    OPERATOR 5 >(double precision,double precision) ,
    FUNCTION 1 gbt_float8_consistent(internal,double precision,smallint) ,
    FUNCTION 2 gbt_float8_union(bytea,internal) ,
    FUNCTION 3 gbt_float8_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_float8_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_float8_picksplit(internal,internal) ,
    FUNCTION 7 gbt_float8_same(internal,internal,internal);


--
-- TOC entry 1959 (class 2616 OID 59737)
-- Dependencies: 737 2096 6
-- Name: gist_inet_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_inet_ops
    DEFAULT FOR TYPE inet USING gist AS
    STORAGE gbtreekey16 ,
    OPERATOR 1 <(inet,inet) ,
    OPERATOR 2 <=(inet,inet) ,
    OPERATOR 3 =(inet,inet) ,
    OPERATOR 4 >=(inet,inet) ,
    OPERATOR 5 >(inet,inet) ,
    FUNCTION 1 gbt_inet_consistent(internal,inet,smallint) ,
    FUNCTION 2 gbt_inet_union(bytea,internal) ,
    FUNCTION 3 gbt_inet_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_inet_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_inet_picksplit(internal,internal) ,
    FUNCTION 7 gbt_inet_same(internal,internal,internal);


--
-- TOC entry 1960 (class 2616 OID 59751)
-- Dependencies: 6 743 2097
-- Name: gist_int2_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_int2_ops
    DEFAULT FOR TYPE smallint USING gist AS
    STORAGE gbtreekey4 ,
    OPERATOR 1 <(smallint,smallint) ,
    OPERATOR 2 <=(smallint,smallint) ,
    OPERATOR 3 =(smallint,smallint) ,
    OPERATOR 4 >=(smallint,smallint) ,
    OPERATOR 5 >(smallint,smallint) ,
    FUNCTION 1 gbt_int2_consistent(internal,smallint,smallint) ,
    FUNCTION 2 gbt_int2_union(bytea,internal) ,
    FUNCTION 3 gbt_int2_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_int2_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_int2_picksplit(internal,internal) ,
    FUNCTION 7 gbt_int2_same(internal,internal,internal);


--
-- TOC entry 1961 (class 2616 OID 59765)
-- Dependencies: 746 2098 6
-- Name: gist_int4_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_int4_ops
    DEFAULT FOR TYPE integer USING gist AS
    STORAGE gbtreekey8 ,
    OPERATOR 1 <(integer,integer) ,
    OPERATOR 2 <=(integer,integer) ,
    OPERATOR 3 =(integer,integer) ,
    OPERATOR 4 >=(integer,integer) ,
    OPERATOR 5 >(integer,integer) ,
    FUNCTION 1 gbt_int4_consistent(internal,integer,smallint) ,
    FUNCTION 2 gbt_int4_union(bytea,internal) ,
    FUNCTION 3 gbt_int4_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_int4_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_int4_picksplit(internal,internal) ,
    FUNCTION 7 gbt_int4_same(internal,internal,internal);


--
-- TOC entry 1962 (class 2616 OID 59779)
-- Dependencies: 2099 6 737
-- Name: gist_int8_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_int8_ops
    DEFAULT FOR TYPE bigint USING gist AS
    STORAGE gbtreekey16 ,
    OPERATOR 1 <(bigint,bigint) ,
    OPERATOR 2 <=(bigint,bigint) ,
    OPERATOR 3 =(bigint,bigint) ,
    OPERATOR 4 >=(bigint,bigint) ,
    OPERATOR 5 >(bigint,bigint) ,
    FUNCTION 1 gbt_int8_consistent(internal,bigint,smallint) ,
    FUNCTION 2 gbt_int8_union(bytea,internal) ,
    FUNCTION 3 gbt_int8_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_int8_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_int8_picksplit(internal,internal) ,
    FUNCTION 7 gbt_int8_same(internal,internal,internal);


--
-- TOC entry 1963 (class 2616 OID 59793)
-- Dependencies: 2100 6 740
-- Name: gist_interval_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_interval_ops
    DEFAULT FOR TYPE interval USING gist AS
    STORAGE gbtreekey32 ,
    OPERATOR 1 <(interval,interval) ,
    OPERATOR 2 <=(interval,interval) ,
    OPERATOR 3 =(interval,interval) ,
    OPERATOR 4 >=(interval,interval) ,
    OPERATOR 5 >(interval,interval) ,
    FUNCTION 1 gbt_intv_consistent(internal,interval,smallint) ,
    FUNCTION 2 gbt_intv_union(bytea,internal) ,
    FUNCTION 3 gbt_intv_compress(internal) ,
    FUNCTION 4 gbt_intv_decompress(internal) ,
    FUNCTION 5 gbt_intv_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_intv_picksplit(internal,internal) ,
    FUNCTION 7 gbt_intv_same(internal,internal,internal);


--
-- TOC entry 1964 (class 2616 OID 59807)
-- Dependencies: 762 765 6 2101
-- Name: gist_ltree_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_ltree_ops
    DEFAULT FOR TYPE ltree USING gist AS
    STORAGE ltree_gist ,
    OPERATOR 1 <(ltree,ltree) ,
    OPERATOR 2 <=(ltree,ltree) ,
    OPERATOR 3 =(ltree,ltree) ,
    OPERATOR 4 >=(ltree,ltree) ,
    OPERATOR 5 >(ltree,ltree) ,
    OPERATOR 10 @>(ltree,ltree) ,
    OPERATOR 11 <@(ltree,ltree) ,
    OPERATOR 12 ~(ltree,lquery) ,
    OPERATOR 13 ~(lquery,ltree) ,
    OPERATOR 14 @(ltree,ltxtquery) ,
    OPERATOR 15 @(ltxtquery,ltree) ,
    OPERATOR 16 ?(ltree,lquery[]) ,
    OPERATOR 17 ?(lquery[],ltree) ,
    FUNCTION 1 ltree_consistent(internal,internal,smallint) ,
    FUNCTION 2 ltree_union(internal,internal) ,
    FUNCTION 3 ltree_compress(internal) ,
    FUNCTION 4 ltree_decompress(internal) ,
    FUNCTION 5 ltree_penalty(internal,internal,internal) ,
    FUNCTION 6 ltree_picksplit(internal,internal) ,
    FUNCTION 7 ltree_same(internal,internal,internal);


--
-- TOC entry 1965 (class 2616 OID 59829)
-- Dependencies: 6 2102 737
-- Name: gist_macaddr_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_macaddr_ops
    DEFAULT FOR TYPE macaddr USING gist AS
    STORAGE gbtreekey16 ,
    OPERATOR 1 <(macaddr,macaddr) ,
    OPERATOR 2 <=(macaddr,macaddr) ,
    OPERATOR 3 =(macaddr,macaddr) ,
    OPERATOR 4 >=(macaddr,macaddr) ,
    OPERATOR 5 >(macaddr,macaddr) ,
    FUNCTION 1 gbt_macad_consistent(internal,macaddr,smallint) ,
    FUNCTION 2 gbt_macad_union(bytea,internal) ,
    FUNCTION 3 gbt_macad_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_macad_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_macad_picksplit(internal,internal) ,
    FUNCTION 7 gbt_macad_same(internal,internal,internal);


--
-- TOC entry 1966 (class 2616 OID 59843)
-- Dependencies: 6 749 2103
-- Name: gist_numeric_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_numeric_ops
    DEFAULT FOR TYPE numeric USING gist AS
    STORAGE gbtreekey_var ,
    OPERATOR 1 <(numeric,numeric) ,
    OPERATOR 2 <=(numeric,numeric) ,
    OPERATOR 3 =(numeric,numeric) ,
    OPERATOR 4 >=(numeric,numeric) ,
    OPERATOR 5 >(numeric,numeric) ,
    FUNCTION 1 gbt_numeric_consistent(internal,numeric,smallint) ,
    FUNCTION 2 gbt_numeric_union(bytea,internal) ,
    FUNCTION 3 gbt_numeric_compress(internal) ,
    FUNCTION 4 gbt_var_decompress(internal) ,
    FUNCTION 5 gbt_numeric_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_numeric_picksplit(internal,internal) ,
    FUNCTION 7 gbt_numeric_same(internal,internal,internal);


--
-- TOC entry 1967 (class 2616 OID 59857)
-- Dependencies: 2104 6 746
-- Name: gist_oid_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_oid_ops
    DEFAULT FOR TYPE oid USING gist AS
    STORAGE gbtreekey8 ,
    OPERATOR 1 <(oid,oid) ,
    OPERATOR 2 <=(oid,oid) ,
    OPERATOR 3 =(oid,oid) ,
    OPERATOR 4 >=(oid,oid) ,
    OPERATOR 5 >(oid,oid) ,
    FUNCTION 1 gbt_oid_consistent(internal,oid,smallint) ,
    FUNCTION 2 gbt_oid_union(bytea,internal) ,
    FUNCTION 3 gbt_oid_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_oid_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_oid_picksplit(internal,internal) ,
    FUNCTION 7 gbt_oid_same(internal,internal,internal);


--
-- TOC entry 1968 (class 2616 OID 59871)
-- Dependencies: 6 2105 776
-- Name: gist_seg_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_seg_ops
    DEFAULT FOR TYPE seg USING gist AS
    OPERATOR 1 <<(seg,seg) ,
    OPERATOR 2 &<(seg,seg) ,
    OPERATOR 3 &&(seg,seg) ,
    OPERATOR 4 &>(seg,seg) ,
    OPERATOR 5 >>(seg,seg) ,
    OPERATOR 6 =(seg,seg) ,
    OPERATOR 7 @(seg,seg) ,
    OPERATOR 8 ~(seg,seg) ,
    FUNCTION 1 gseg_consistent(internal,seg,integer) ,
    FUNCTION 2 gseg_union(internal,internal) ,
    FUNCTION 3 gseg_compress(internal) ,
    FUNCTION 4 gseg_decompress(internal) ,
    FUNCTION 5 gseg_penalty(internal,internal,internal) ,
    FUNCTION 6 gseg_picksplit(internal,internal) ,
    FUNCTION 7 gseg_same(seg,seg,internal);


--
-- TOC entry 1969 (class 2616 OID 59888)
-- Dependencies: 2106 6 749
-- Name: gist_text_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_text_ops
    DEFAULT FOR TYPE text USING gist AS
    STORAGE gbtreekey_var ,
    OPERATOR 1 <(text,text) ,
    OPERATOR 2 <=(text,text) ,
    OPERATOR 3 =(text,text) ,
    OPERATOR 4 >=(text,text) ,
    OPERATOR 5 >(text,text) ,
    FUNCTION 1 gbt_text_consistent(internal,text,smallint) ,
    FUNCTION 2 gbt_text_union(bytea,internal) ,
    FUNCTION 3 gbt_text_compress(internal) ,
    FUNCTION 4 gbt_var_decompress(internal) ,
    FUNCTION 5 gbt_text_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_text_picksplit(internal,internal) ,
    FUNCTION 7 gbt_text_same(internal,internal,internal);


--
-- TOC entry 1970 (class 2616 OID 59902)
-- Dependencies: 6 737 2107
-- Name: gist_time_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_time_ops
    DEFAULT FOR TYPE time without time zone USING gist AS
    STORAGE gbtreekey16 ,
    OPERATOR 1 <(time without time zone,time without time zone) ,
    OPERATOR 2 <=(time without time zone,time without time zone) ,
    OPERATOR 3 =(time without time zone,time without time zone) ,
    OPERATOR 4 >=(time without time zone,time without time zone) ,
    OPERATOR 5 >(time without time zone,time without time zone) ,
    FUNCTION 1 gbt_time_consistent(internal,time without time zone,smallint) ,
    FUNCTION 2 gbt_time_union(bytea,internal) ,
    FUNCTION 3 gbt_time_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_time_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_time_picksplit(internal,internal) ,
    FUNCTION 7 gbt_time_same(internal,internal,internal);


--
-- TOC entry 1971 (class 2616 OID 59916)
-- Dependencies: 2108 6 737
-- Name: gist_timestamp_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_timestamp_ops
    DEFAULT FOR TYPE timestamp without time zone USING gist AS
    STORAGE gbtreekey16 ,
    OPERATOR 1 <(timestamp without time zone,timestamp without time zone) ,
    OPERATOR 2 <=(timestamp without time zone,timestamp without time zone) ,
    OPERATOR 3 =(timestamp without time zone,timestamp without time zone) ,
    OPERATOR 4 >=(timestamp without time zone,timestamp without time zone) ,
    OPERATOR 5 >(timestamp without time zone,timestamp without time zone) ,
    FUNCTION 1 gbt_ts_consistent(internal,timestamp without time zone,smallint) ,
    FUNCTION 2 gbt_ts_union(bytea,internal) ,
    FUNCTION 3 gbt_ts_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_ts_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_ts_picksplit(internal,internal) ,
    FUNCTION 7 gbt_ts_same(internal,internal,internal);


--
-- TOC entry 1972 (class 2616 OID 59930)
-- Dependencies: 6 2109 737
-- Name: gist_timestamptz_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_timestamptz_ops
    DEFAULT FOR TYPE timestamp with time zone USING gist AS
    STORAGE gbtreekey16 ,
    OPERATOR 1 <(timestamp with time zone,timestamp with time zone) ,
    OPERATOR 2 <=(timestamp with time zone,timestamp with time zone) ,
    OPERATOR 3 =(timestamp with time zone,timestamp with time zone) ,
    OPERATOR 4 >=(timestamp with time zone,timestamp with time zone) ,
    OPERATOR 5 >(timestamp with time zone,timestamp with time zone) ,
    FUNCTION 1 gbt_tstz_consistent(internal,timestamp with time zone,smallint) ,
    FUNCTION 2 gbt_ts_union(bytea,internal) ,
    FUNCTION 3 gbt_tstz_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_ts_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_ts_picksplit(internal,internal) ,
    FUNCTION 7 gbt_ts_same(internal,internal,internal);


--
-- TOC entry 1973 (class 2616 OID 59944)
-- Dependencies: 6 2110 737
-- Name: gist_timetz_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_timetz_ops
    DEFAULT FOR TYPE time with time zone USING gist AS
    STORAGE gbtreekey16 ,
    OPERATOR 1 <(time with time zone,time with time zone) ,
    OPERATOR 2 <=(time with time zone,time with time zone) ,
    OPERATOR 3 =(time with time zone,time with time zone) ,
    OPERATOR 4 >=(time with time zone,time with time zone) ,
    OPERATOR 5 >(time with time zone,time with time zone) ,
    FUNCTION 1 gbt_timetz_consistent(internal,time with time zone,smallint) ,
    FUNCTION 2 gbt_time_union(bytea,internal) ,
    FUNCTION 3 gbt_timetz_compress(internal) ,
    FUNCTION 4 gbt_decompress(internal) ,
    FUNCTION 5 gbt_time_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_time_picksplit(internal,internal) ,
    FUNCTION 7 gbt_time_same(internal,internal,internal);


--
-- TOC entry 1974 (class 2616 OID 59958)
-- Dependencies: 2111 6 752
-- Name: gist_trgm_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_trgm_ops
    FOR TYPE text USING gist AS
    STORAGE gtrgm ,
    OPERATOR 1 %(text,text) ,
    FUNCTION 1 gtrgm_consistent(gtrgm,internal,integer) ,
    FUNCTION 2 gtrgm_union(bytea,internal) ,
    FUNCTION 3 gtrgm_compress(internal) ,
    FUNCTION 4 gtrgm_decompress(internal) ,
    FUNCTION 5 gtrgm_penalty(internal,internal,internal) ,
    FUNCTION 6 gtrgm_picksplit(internal,internal) ,
    FUNCTION 7 gtrgm_same(gtrgm,gtrgm,internal);


--
-- TOC entry 1975 (class 2616 OID 59968)
-- Dependencies: 6 749 2112
-- Name: gist_vbit_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS gist_vbit_ops
    DEFAULT FOR TYPE bit varying USING gist AS
    STORAGE gbtreekey_var ,
    OPERATOR 1 <(bit varying,bit varying) ,
    OPERATOR 2 <=(bit varying,bit varying) ,
    OPERATOR 3 =(bit varying,bit varying) ,
    OPERATOR 4 >=(bit varying,bit varying) ,
    OPERATOR 5 >(bit varying,bit varying) ,
    FUNCTION 1 gbt_bit_consistent(internal,bit,smallint) ,
    FUNCTION 2 gbt_bit_union(bytea,internal) ,
    FUNCTION 3 gbt_bit_compress(internal) ,
    FUNCTION 4 gbt_var_decompress(internal) ,
    FUNCTION 5 gbt_bit_penalty(internal,internal,internal) ,
    FUNCTION 6 gbt_bit_picksplit(internal,internal) ,
    FUNCTION 7 gbt_bit_same(internal,internal,internal);


--
-- TOC entry 1976 (class 2616 OID 59982)
-- Dependencies: 2113 6 762
-- Name: ltree_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS ltree_ops
    DEFAULT FOR TYPE ltree USING btree AS
    OPERATOR 1 <(ltree,ltree) ,
    OPERATOR 2 <=(ltree,ltree) ,
    OPERATOR 3 =(ltree,ltree) ,
    OPERATOR 4 >=(ltree,ltree) ,
    OPERATOR 5 >(ltree,ltree) ,
    FUNCTION 1 ltree_cmp(ltree,ltree);


--
-- TOC entry 1977 (class 2616 OID 59990)
-- Dependencies: 776 2114 6
-- Name: seg_ops; Type: OPERATOR CLASS; Schema: public; Owner: -
--

CREATE OPERATOR CLASS seg_ops
    DEFAULT FOR TYPE seg USING btree AS
    OPERATOR 1 <(seg,seg) ,
    OPERATOR 2 <=(seg,seg) ,
    OPERATOR 3 =(seg,seg) ,
    OPERATOR 4 >=(seg,seg) ,
    OPERATOR 5 >(seg,seg) ,
    FUNCTION 1 seg_cmp(seg,seg);


--
-- TOC entry 2388 (class 1259 OID 59997)
-- Dependencies: 6
-- Name: alleles_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE alleles_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 2389 (class 1259 OID 59999)
-- Dependencies: 6
-- Name: anatomy; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE anatomy (
    anid integer NOT NULL,
    termlevel integer NOT NULL,
    ts character varying(255) NOT NULL,
    rts character varying(255),
    tsname character varying(255) NOT NULL,
    tsaddname character varying(255),
    emap character varying(255) NOT NULL
);


--
-- TOC entry 2390 (class 1259 OID 60005)
-- Dependencies: 6
-- Name: anatomy_model_r; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE anatomy_model_r (
    eid integer NOT NULL,
    anid integer NOT NULL
);


--
-- TOC entry 2391 (class 1259 OID 60008)
-- Dependencies: 6
-- Name: anatomy_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE anatomy_seq
    START WITH 13730
    INCREMENT BY 1
    MAXVALUE 2000000000
    NO MINVALUE
    CACHE 1
    CYCLE;


--
-- TOC entry 2392 (class 1259 OID 60010)
-- Dependencies: 6
-- Name: available_genetic_back; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE available_genetic_back (
    aid integer NOT NULL,
    avbackname character varying,
    pid integer NOT NULL
);


--
-- TOC entry 2393 (class 1259 OID 60016)
-- Dependencies: 6
-- Name: available_genetic_back_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE available_genetic_back_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2394 (class 1259 OID 60018)
-- Dependencies: 6
-- Name: chromosomes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE chromosomes (
    cid integer NOT NULL,
    name character varying(2) NOT NULL,
    comm character varying(256),
    sid integer NOT NULL,
    abbr character varying
);


--
-- TOC entry 2395 (class 1259 OID 60024)
-- Dependencies: 6
-- Name: chromosomes_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE chromosomes_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 2000000000
    NO MINVALUE
    CACHE 1
    CYCLE;


--
-- TOC entry 2396 (class 1259 OID 60026)
-- Dependencies: 6
-- Name: data_files_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE data_files_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2397 (class 1259 OID 60028)
-- Dependencies: 2800 6
-- Name: expobj; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE expobj (
    eid integer NOT NULL,
    identity character varying(256) NOT NULL,
    alias character varying(256),
    suid integer NOT NULL,
    status character varying(1) NOT NULL,
    id integer NOT NULL,
    ts date NOT NULL,
    comm character varying(256),
    CONSTRAINT expobj_status_check CHECK ((((status)::text = 'E'::text) OR ((status)::text = 'D'::text)))
);


--
-- TOC entry 2398 (class 1259 OID 60035)
-- Dependencies: 6
-- Name: expobj_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE expobj_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2399 (class 1259 OID 60037)
-- Dependencies: 6
-- Name: expression; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE expression (
    exid integer NOT NULL,
    exanatomy character varying(255),
    excomm character varying(255)
);


--
-- TOC entry 2400 (class 1259 OID 60043)
-- Dependencies: 6
-- Name: expression_file_r; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE expression_file_r (
    exid integer NOT NULL,
    fileid integer NOT NULL
);


--
-- TOC entry 2401 (class 1259 OID 60046)
-- Dependencies: 6
-- Name: expression_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE expression_id_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 2000000000
    NO MINVALUE
    CACHE 1
    CYCLE;


--
-- TOC entry 2402 (class 1259 OID 60048)
-- Dependencies: 6
-- Name: expression_model_r; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE expression_model_r (
    eid integer NOT NULL,
    exid integer NOT NULL
);


--
-- TOC entry 2403 (class 1259 OID 60051)
-- Dependencies: 6
-- Name: file; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE file (
    fileid integer NOT NULL,
    name character varying(255) NOT NULL,
    comm character varying,
    file bytea,
    mimetype character varying(255),
    filetype character varying(255),
    ts timestamp without time zone,
    id integer,
    pid integer NOT NULL
);


--
-- TOC entry 2404 (class 1259 OID 60057)
-- Dependencies: 6
-- Name: file_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE file_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2405 (class 1259 OID 60059)
-- Dependencies: 6
-- Name: filters; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE filters (
    fid integer NOT NULL,
    name character varying(20) NOT NULL,
    expression character varying(2000),
    comm character varying(256),
    pid integer NOT NULL,
    sid integer NOT NULL,
    id integer NOT NULL,
    ts date NOT NULL
);


--
-- TOC entry 2406 (class 1259 OID 60065)
-- Dependencies: 6
-- Name: filters_log; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE filters_log (
    fid integer NOT NULL,
    name character varying(20) NOT NULL,
    expression character varying(2000),
    comm character varying(256),
    sid integer NOT NULL,
    id integer NOT NULL,
    ts date NOT NULL
);


--
-- TOC entry 2407 (class 1259 OID 60071)
-- Dependencies: 6
-- Name: filters_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE filters_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2408 (class 1259 OID 60073)
-- Dependencies: 6
-- Name: gene; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE gene (
    gaid integer NOT NULL,
    name character varying NOT NULL,
    comm character varying,
    id integer NOT NULL,
    ts timestamp without time zone,
    pid integer NOT NULL,
    mgiid character varying,
    genesymbol character varying,
    geneexpress character varying,
    idgene character varying,
    idensembl character varying,
    cid integer,
    driver_note text,
    molecular_note text,
    molecular_note_link text,
    common_name text,
    distinguish text
);


--
-- TOC entry 2409 (class 1259 OID 60079)
-- Dependencies: 6
-- Name: gene_effected_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE gene_effected_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2410 (class 1259 OID 60081)
-- Dependencies: 6
-- Name: gene_ontology_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE gene_ontology_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2411 (class 1259 OID 60083)
-- Dependencies: 6
-- Name: gene_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE gene_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2412 (class 1259 OID 60085)
-- Dependencies: 6
-- Name: genetic_back; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE genetic_back (
    eid integer NOT NULL,
    dna_origin integer,
    targeted_back integer,
    host_back integer,
    backcrossing_strain integer,
    backcrosses character varying,
    gbid integer NOT NULL
);


--
-- TOC entry 2413 (class 1259 OID 60091)
-- Dependencies: 6
-- Name: genetic_back_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE genetic_back_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2414 (class 1259 OID 60093)
-- Dependencies: 6
-- Name: genetic_back_values; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE genetic_back_values (
    bid integer NOT NULL,
    backname character varying,
    pid integer
);


--
-- TOC entry 2415 (class 1259 OID 60099)
-- Dependencies: 6
-- Name: genetic_back_values_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE genetic_back_values_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2416 (class 1259 OID 60101)
-- Dependencies: 6
-- Name: genetic_modification_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE genetic_modification_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2417 (class 1259 OID 60103)
-- Dependencies: 6
-- Name: genotypes_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE genotypes_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2418 (class 1259 OID 60105)
-- Dependencies: 6
-- Name: groupings_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE groupings_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 100
    CYCLE;


--
-- TOC entry 2419 (class 1259 OID 60107)
-- Dependencies: 6
-- Name: groups_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE groups_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 100
    CYCLE;


--
-- TOC entry 2420 (class 1259 OID 60109)
-- Dependencies: 6
-- Name: individuals_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE individuals_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2421 (class 1259 OID 60111)
-- Dependencies: 6
-- Name: is_cm; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE is_cm (
    iscmid integer NOT NULL,
    isite character varying(255),
    cnumber character varying(255)
);


--
-- TOC entry 2422 (class 1259 OID 60117)
-- Dependencies: 6
-- Name: is_cm_model_r; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE is_cm_model_r (
    eid integer NOT NULL,
    iscmid integer NOT NULL
);


--
-- TOC entry 2423 (class 1259 OID 60120)
-- Dependencies: 6
-- Name: is_cn_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE is_cn_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 2000000000
    NO MINVALUE
    CACHE 1
    CYCLE;


--
-- TOC entry 2424 (class 1259 OID 60122)
-- Dependencies: 6
-- Name: l_alleles_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE l_alleles_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2425 (class 1259 OID 60124)
-- Dependencies: 6
-- Name: l_markers_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE l_markers_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2426 (class 1259 OID 60126)
-- Dependencies: 6
-- Name: link; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE link (
    linkid integer NOT NULL,
    name character varying NOT NULL,
    url character varying(255) NOT NULL,
    comm character varying,
    id integer,
    ts timestamp without time zone,
    pid integer
);


--
-- TOC entry 2427 (class 1259 OID 60132)
-- Dependencies: 6
-- Name: link_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE link_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2428 (class 1259 OID 60134)
-- Dependencies: 6
-- Name: marker_sets_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE marker_sets_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2429 (class 1259 OID 60136)
-- Dependencies: 6
-- Name: markers_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE markers_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 500
    CYCLE;


--
-- TOC entry 2430 (class 1259 OID 60138)
-- Dependencies: 2801 6
-- Name: model; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE model (
    eid integer NOT NULL,
    background character varying,
    availability character varying,
    contact integer,
    application integer,
    apptext character varying,
    ts timestamp without time zone,
    genotyping integer,
    handling integer,
    strain integer,
    level integer,
    desired_level integer DEFAULT 1,
    donating_investigator text,
    inducible text,
    former_names text
);


--
-- TOC entry 2431 (class 1259 OID 60145)
-- Dependencies: 6
-- Name: mutation_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE mutation_type (
    id integer NOT NULL,
    name character varying NOT NULL,
    abbreviation character varying,
    pid integer NOT NULL
);


--
-- TOC entry 2432 (class 1259 OID 60151)
-- Dependencies: 6
-- Name: mutation_type_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE mutation_type_seq
    START WITH 1015
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1015
    CYCLE;


--
-- TOC entry 2433 (class 1259 OID 60153)
-- Dependencies: 6
-- Name: pathway_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE pathway_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


SET default_with_oids = true;

--
-- TOC entry 2434 (class 1259 OID 60155)
-- Dependencies: 6
-- Name: pg_ts_cfg; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pg_ts_cfg (
    ts_name text NOT NULL,
    prs_name text NOT NULL,
    locale text
);


--
-- TOC entry 2435 (class 1259 OID 60161)
-- Dependencies: 6
-- Name: pg_ts_cfgmap; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pg_ts_cfgmap (
    ts_name text NOT NULL,
    tok_alias text NOT NULL,
    dict_name text[]
);


--
-- TOC entry 2436 (class 1259 OID 60167)
-- Dependencies: 6
-- Name: pg_ts_dict; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pg_ts_dict (
    dict_name text NOT NULL,
    dict_init regprocedure,
    dict_initoption text,
    dict_lexize regprocedure NOT NULL,
    dict_comment text
);


--
-- TOC entry 2437 (class 1259 OID 60173)
-- Dependencies: 6
-- Name: pg_ts_parser; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pg_ts_parser (
    prs_name text NOT NULL,
    prs_start regprocedure NOT NULL,
    prs_nexttoken regprocedure NOT NULL,
    prs_end regprocedure NOT NULL,
    prs_headline regprocedure NOT NULL,
    prs_lextype regprocedure NOT NULL,
    prs_comment text
);


SET default_with_oids = false;

--
-- TOC entry 2438 (class 1259 OID 60179)
-- Dependencies: 6
-- Name: pheno_alt_id; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_alt_id (
    id integer NOT NULL,
    alt_id character varying
);


--
-- TOC entry 2439 (class 1259 OID 60185)
-- Dependencies: 6
-- Name: pheno_alt_id_r; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_alt_id_r (
    pheno_id integer NOT NULL,
    pheno_alt_id integer NOT NULL
);


--
-- TOC entry 2440 (class 1259 OID 60188)
-- Dependencies: 6
-- Name: pheno_alt_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE pheno_alt_id_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 2000000000
    NO MINVALUE
    CACHE 1
    CYCLE;


--
-- TOC entry 2441 (class 1259 OID 60190)
-- Dependencies: 6
-- Name: pheno_is_a; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_is_a (
    id_a integer NOT NULL,
    id_b integer NOT NULL
);


--
-- TOC entry 2442 (class 1259 OID 60193)
-- Dependencies: 6
-- Name: pheno_model_r; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_model_r (
    eid integer NOT NULL,
    mp01 integer NOT NULL,
    mp02 integer NOT NULL,
    mp03 integer NOT NULL,
    mp04 integer NOT NULL,
    mp05 integer NOT NULL,
    mp06 integer NOT NULL,
    mp07 integer NOT NULL,
    mp08 integer NOT NULL,
    mp09 integer NOT NULL
);


--
-- TOC entry 2443 (class 1259 OID 60196)
-- Dependencies: 2802 6
-- Name: pheno_ontology; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_ontology (
    id integer NOT NULL,
    name character varying NOT NULL,
    def character varying,
    def_ref character varying,
    is_obsolete integer DEFAULT 0,
    comm character varying
);


--
-- TOC entry 2444 (class 1259 OID 60203)
-- Dependencies: 6
-- Name: pheno_synonym; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_synonym (
    id integer NOT NULL,
    synonym character varying
);


--
-- TOC entry 2445 (class 1259 OID 60209)
-- Dependencies: 6
-- Name: pheno_synonym_r; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_synonym_r (
    pheno_id integer NOT NULL,
    pheno_synonym integer NOT NULL,
    attribute character varying
);


--
-- TOC entry 2446 (class 1259 OID 60215)
-- Dependencies: 6
-- Name: pheno_synonym_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE pheno_synonym_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 2000000000
    NO MINVALUE
    CACHE 1
    CYCLE;


--
-- TOC entry 2447 (class 1259 OID 60217)
-- Dependencies: 6
-- Name: pheno_xref; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_xref (
    id integer NOT NULL,
    xref character varying
);


--
-- TOC entry 2448 (class 1259 OID 60223)
-- Dependencies: 6
-- Name: pheno_xref_r; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pheno_xref_r (
    pheno_id integer NOT NULL,
    xref_id integer NOT NULL
);


--
-- TOC entry 2449 (class 1259 OID 60226)
-- Dependencies: 6
-- Name: pheno_xref_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE pheno_xref_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 2000000000
    NO MINVALUE
    CACHE 1
    CYCLE;


--
-- TOC entry 2450 (class 1259 OID 60228)
-- Dependencies: 6
-- Name: phenotypes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE phenotypes (
    vid integer NOT NULL,
    iid integer NOT NULL,
    suid integer NOT NULL,
    value character varying(20) NOT NULL,
    date_ date,
    reference character varying(32),
    id integer NOT NULL,
    ts date NOT NULL,
    comm character varying(256)
);


--
-- TOC entry 2451 (class 1259 OID 60231)
-- Dependencies: 6
-- Name: phenotypes_log; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE phenotypes_log (
    vid integer NOT NULL,
    iid integer NOT NULL,
    value character varying(20) NOT NULL,
    date_ date,
    reference character varying(32),
    comm character varying(256),
    id integer NOT NULL,
    ts date NOT NULL
);


--
-- TOC entry 2452 (class 1259 OID 60234)
-- Dependencies: 6
-- Name: phenotypes_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE phenotypes_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2453 (class 1259 OID 60236)
-- Dependencies: 6
-- Name: priv_map; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE priv_map (
    priv_number integer NOT NULL,
    name character varying(12) NOT NULL
);


--
-- TOC entry 2454 (class 1259 OID 60239)
-- Dependencies: 6
-- Name: privileges_; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE privileges_ (
    prid integer NOT NULL,
    name character varying(12) NOT NULL,
    comm character varying(256)
);


--
-- TOC entry 2455 (class 1259 OID 60242)
-- Dependencies: 6
-- Name: process_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE process_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2456 (class 1259 OID 60244)
-- Dependencies: 2803 6
-- Name: projects; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE projects (
    pid integer NOT NULL,
    name character varying(20) NOT NULL,
    comm character varying(256),
    status character(1) NOT NULL,
    CONSTRAINT projects_status_check CHECK (((status = 'E'::bpchar) OR (status = 'D'::bpchar)))
);


--
-- TOC entry 2457 (class 1259 OID 60248)
-- Dependencies: 6
-- Name: projects_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE projects_seq
    START WITH 99
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 99
    CACHE 1
    CYCLE;


--
-- TOC entry 2458 (class 1259 OID 60250)
-- Dependencies: 6
-- Name: protein_complex_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE protein_complex_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2459 (class 1259 OID 60252)
-- Dependencies: 6
-- Name: protein_domain_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE protein_domain_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2460 (class 1259 OID 60254)
-- Dependencies: 6
-- Name: protein_family_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE protein_family_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2461 (class 1259 OID 60256)
-- Dependencies: 6
-- Name: protein_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE protein_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2462 (class 1259 OID 60258)
-- Dependencies: 6
-- Name: r_expression_emap; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_expression_emap (
    exid integer NOT NULL,
    emap text NOT NULL
);


--
-- TOC entry 2463 (class 1259 OID 60264)
-- Dependencies: 6
-- Name: r_expression_ontology; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_expression_ontology (
    exid integer NOT NULL,
    oid text NOT NULL,
    namespace text NOT NULL
);


--
-- TOC entry 2464 (class 1259 OID 60270)
-- Dependencies: 6
-- Name: r_file_su; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_file_su (
    suid integer NOT NULL,
    fileid integer NOT NULL
);


--
-- TOC entry 2465 (class 1259 OID 60273)
-- Dependencies: 2804 6
-- Name: r_gene_model; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_gene_model (
    gaid integer NOT NULL,
    eid integer NOT NULL,
    cid integer DEFAULT 24
);


--
-- TOC entry 2466 (class 1259 OID 60277)
-- Dependencies: 6
-- Name: r_link_su; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_link_su (
    suid integer NOT NULL,
    linkid integer NOT NULL
);


--
-- TOC entry 2467 (class 1259 OID 60280)
-- Dependencies: 6
-- Name: r_model_imsr; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_model_imsr (
    eid integer NOT NULL,
    imsr integer NOT NULL,
    ts timestamp without time zone NOT NULL
);


--
-- TOC entry 2468 (class 1259 OID 60283)
-- Dependencies: 2805 2806 6
-- Name: r_model_repositories_avgenback; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_model_repositories_avgenback (
    eid integer NOT NULL,
    rid integer NOT NULL,
    aid integer NOT NULL,
    stateid integer DEFAULT 1006 NOT NULL,
    typeid integer DEFAULT 1016 NOT NULL
);


--
-- TOC entry 2469 (class 1259 OID 60288)
-- Dependencies: 6
-- Name: r_model_strain; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_model_strain (
    model integer NOT NULL,
    strain integer NOT NULL
);


--
-- TOC entry 2470 (class 1259 OID 60291)
-- Dependencies: 6
-- Name: r_model_strain_allele_mutation_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_model_strain_allele_mutation_type (
    model integer NOT NULL,
    strain_allele integer NOT NULL,
    mutation_type integer NOT NULL,
    attribute text
);


--
-- TOC entry 2471 (class 1259 OID 60297)
-- Dependencies: 6
-- Name: r_mutation_type_strain_allele; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_mutation_type_strain_allele (
    mutationtype integer NOT NULL,
    strainallele integer NOT NULL
);


--
-- TOC entry 2472 (class 1259 OID 60300)
-- Dependencies: 6
-- Name: r_prj_rol; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_prj_rol (
    pid integer NOT NULL,
    id integer NOT NULL,
    rid integer NOT NULL
);


--
-- TOC entry 2473 (class 1259 OID 60303)
-- Dependencies: 6
-- Name: r_prj_spc; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_prj_spc (
    pid integer NOT NULL,
    sid integer NOT NULL
);


--
-- TOC entry 2474 (class 1259 OID 60306)
-- Dependencies: 6
-- Name: r_prj_su; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_prj_su (
    pid integer NOT NULL,
    suid integer NOT NULL
);


--
-- TOC entry 2475 (class 1259 OID 60309)
-- Dependencies: 6
-- Name: r_ref_model; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_ref_model (
    refid integer NOT NULL,
    eid integer NOT NULL
);


--
-- TOC entry 2476 (class 1259 OID 60312)
-- Dependencies: 6
-- Name: r_resource_model; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_resource_model (
    eid integer NOT NULL,
    resourceid integer NOT NULL
);


--
-- TOC entry 2477 (class 1259 OID 60315)
-- Dependencies: 6
-- Name: r_rol_pri; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_rol_pri (
    rid integer NOT NULL,
    prid integer NOT NULL
);


--
-- TOC entry 2478 (class 1259 OID 60318)
-- Dependencies: 6
-- Name: r_strain_allele_gene; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_strain_allele_gene (
    aid integer NOT NULL,
    gid integer NOT NULL
);


--
-- TOC entry 2479 (class 1259 OID 60321)
-- Dependencies: 6
-- Name: r_strain_strain_allele_mutation_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_strain_strain_allele_mutation_type (
    strain integer NOT NULL,
    strain_allele integer NOT NULL,
    mutation_type integer NOT NULL,
    attribute text
);


--
-- TOC entry 2480 (class 1259 OID 60327)
-- Dependencies: 6
-- Name: r_strain_strain_state; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_strain_strain_state (
    strainid integer NOT NULL,
    stateid integer NOT NULL
);


--
-- TOC entry 2481 (class 1259 OID 60330)
-- Dependencies: 6
-- Name: r_strain_strain_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE r_strain_strain_type (
    strainid integer NOT NULL,
    typeid integer NOT NULL
);


--
-- TOC entry 2482 (class 1259 OID 60333)
-- Dependencies: 2807 6
-- Name: reference; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE reference (
    refid integer NOT NULL,
    name character varying NOT NULL,
    comm character varying,
    linkid integer,
    fileid integer,
    id integer NOT NULL,
    ts timestamp without time zone,
    pid integer NOT NULL,
    pubmed text,
    "primary" boolean DEFAULT false
);


--
-- TOC entry 2483 (class 1259 OID 60340)
-- Dependencies: 6
-- Name: reference_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE reference_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2484 (class 1259 OID 60342)
-- Dependencies: 2808 6
-- Name: repositories; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE repositories (
    rid integer NOT NULL,
    reponame character varying,
    pid integer NOT NULL,
    hasdb integer DEFAULT 0,
    mouseurl character varying,
    repourl character varying
);


--
-- TOC entry 2485 (class 1259 OID 60349)
-- Dependencies: 6
-- Name: repositories_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE repositories_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2486 (class 1259 OID 60351)
-- Dependencies: 6
-- Name: research_application; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE research_application (
    raid integer NOT NULL,
    name character varying NOT NULL,
    comm character varying,
    id integer,
    ts timestamp without time zone,
    pid integer NOT NULL
);


--
-- TOC entry 2487 (class 1259 OID 60357)
-- Dependencies: 6
-- Name: research_application_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE research_application_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2488 (class 1259 OID 60359)
-- Dependencies: 6
-- Name: resource; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE resource (
    resource_id integer NOT NULL,
    name character varying NOT NULL,
    comm character varying,
    file_id integer,
    link_id integer,
    category_id integer,
    user_id integer NOT NULL,
    ts timestamp without time zone,
    project_id integer NOT NULL
);


--
-- TOC entry 2489 (class 1259 OID 60365)
-- Dependencies: 6
-- Name: resource_category; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE resource_category (
    category_id integer NOT NULL,
    name character varying NOT NULL,
    comm character varying,
    parent_id integer,
    project_id integer NOT NULL,
    user_id integer NOT NULL,
    ts timestamp without time zone
);


--
-- TOC entry 2490 (class 1259 OID 60371)
-- Dependencies: 6
-- Name: resource_category_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_category_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2491 (class 1259 OID 60373)
-- Dependencies: 6
-- Name: resource_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2492 (class 1259 OID 60375)
-- Dependencies: 6
-- Name: roles_; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE roles_ (
    rid integer NOT NULL,
    pid integer NOT NULL,
    name character varying(20) NOT NULL,
    comm character varying(256)
);


--
-- TOC entry 2493 (class 1259 OID 60378)
-- Dependencies: 6
-- Name: roles_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE roles_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2494 (class 1259 OID 60380)
-- Dependencies: 6
-- Name: samples_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE samples_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2495 (class 1259 OID 60382)
-- Dependencies: 2809 6
-- Name: sampling_units; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE sampling_units (
    suid integer NOT NULL,
    name character varying(20) NOT NULL,
    comm character varying(256),
    status character varying(1) NOT NULL,
    sid integer NOT NULL,
    id integer NOT NULL,
    ts date NOT NULL,
    CONSTRAINT sampling_units_status_check CHECK ((((status)::text = 'E'::text) OR ((status)::text = 'D'::text)))
);


--
-- TOC entry 2496 (class 1259 OID 60386)
-- Dependencies: 6
-- Name: sampling_units_log; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE sampling_units_log (
    suid integer NOT NULL,
    name character varying(20) NOT NULL,
    comm character varying(256),
    status character varying(1) NOT NULL,
    id integer NOT NULL,
    ts date NOT NULL
);


--
-- TOC entry 2497 (class 1259 OID 60389)
-- Dependencies: 6
-- Name: sampling_units_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE sampling_units_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2498 (class 1259 OID 60391)
-- Dependencies: 6
-- Name: simplelog; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE simplelog (
    logid integer NOT NULL,
    ts timestamp without time zone,
    txt character varying
);


--
-- TOC entry 2499 (class 1259 OID 60397)
-- Dependencies: 6
-- Name: simplelog_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE simplelog_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2500 (class 1259 OID 60399)
-- Dependencies: 6
-- Name: spatial_ref_sys; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE spatial_ref_sys (
    srid integer NOT NULL,
    auth_name character varying(256),
    auth_srid integer,
    srtext character varying(2048),
    proj4text character varying(2048)
);


--
-- TOC entry 2501 (class 1259 OID 60405)
-- Dependencies: 6
-- Name: species; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE species (
    sid integer NOT NULL,
    name character varying(20) NOT NULL,
    comm character varying(256)
);


--
-- TOC entry 2502 (class 1259 OID 60408)
-- Dependencies: 6
-- Name: species_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE species_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2503 (class 1259 OID 60410)
-- Dependencies: 6
-- Name: strain; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE strain (
    strainid integer NOT NULL,
    mgiid character varying,
    designation character varying NOT NULL,
    pid integer NOT NULL
);


--
-- TOC entry 2504 (class 1259 OID 60416)
-- Dependencies: 6
-- Name: strain_allele; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE strain_allele (
    id integer NOT NULL,
    mgiid character varying,
    symbol character varying,
    name character varying NOT NULL,
    mutationtype integer,
    gene integer,
    attributes character varying,
    strainid integer,
    made_by text,
    origin_strain text,
    mgi_url text
);


--
-- TOC entry 2505 (class 1259 OID 60422)
-- Dependencies: 6
-- Name: strain_allele_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE strain_allele_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2506 (class 1259 OID 60424)
-- Dependencies: 6
-- Name: strain_link_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE strain_link_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 2000000000
    NO MINVALUE
    CACHE 1
    CYCLE;


--
-- TOC entry 2507 (class 1259 OID 60426)
-- Dependencies: 6
-- Name: strain_links; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE strain_links (
    strainid integer NOT NULL,
    repository text NOT NULL,
    externalid text NOT NULL,
    strainurl text NOT NULL,
    id integer NOT NULL
);


--
-- TOC entry 2508 (class 1259 OID 60432)
-- Dependencies: 6
-- Name: strain_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE strain_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2509 (class 1259 OID 60434)
-- Dependencies: 6
-- Name: strain_state; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE strain_state (
    id integer NOT NULL,
    name character varying NOT NULL,
    abbreviation character varying,
    pid integer NOT NULL
);


--
-- TOC entry 2510 (class 1259 OID 60440)
-- Dependencies: 6
-- Name: strain_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE strain_type (
    id integer NOT NULL,
    name character varying NOT NULL,
    abbreviation character varying,
    pid integer NOT NULL
);


--
-- TOC entry 2511 (class 1259 OID 60446)
-- Dependencies: 6
-- Name: u_alleles_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE u_alleles_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 2000
    CYCLE;


--
-- TOC entry 2512 (class 1259 OID 60448)
-- Dependencies: 6
-- Name: u_marker_sets_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE u_marker_sets_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2513 (class 1259 OID 60450)
-- Dependencies: 6
-- Name: u_markers_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE u_markers_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 500
    CYCLE;


--
-- TOC entry 2514 (class 1259 OID 60452)
-- Dependencies: 6
-- Name: u_variable_sets_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE u_variable_sets_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2515 (class 1259 OID 60454)
-- Dependencies: 6
-- Name: u_variables_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE u_variables_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 100
    CYCLE;


--
-- TOC entry 2516 (class 1259 OID 60456)
-- Dependencies: 2810 6
-- Name: users; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    usr character varying(10) NOT NULL,
    pwd character varying(10),
    name character varying(32),
    status character(1) NOT NULL,
    group_name character varying,
    group_phone character varying,
    group_addr character varying,
    group_link integer,
    user_link integer,
    email character varying,
    admin boolean,
    crypt character varying(50),
    CONSTRAINT users_status_check CHECK (((status = 'E'::bpchar) OR (status = 'D'::bpchar)))
);


--
-- TOC entry 2517 (class 1259 OID 60463)
-- Dependencies: 6
-- Name: users_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE users_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2518 (class 1259 OID 60465)
-- Dependencies: 2608 6
-- Name: v_sampling_units_3; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW v_sampling_units_3 AS
    SELECT r.pid, su.sid, s.name AS sname, su.suid, su.name, su.comm, su.status, u.usr, su.ts FROM r_prj_su r, sampling_units su, species s, users u WHERE (((r.suid = su.suid) AND (u.id = su.id)) AND (s.sid = su.sid));


--
-- TOC entry 2519 (class 1259 OID 60469)
-- Dependencies: 6
-- Name: variable_sets_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE variable_sets_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 1
    CYCLE;


--
-- TOC entry 2520 (class 1259 OID 60471)
-- Dependencies: 2811 6
-- Name: variables; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE variables (
    vid integer NOT NULL,
    name character varying(20) NOT NULL,
    type character varying(1) NOT NULL,
    unit character varying(10),
    comm character varying(256),
    suid integer NOT NULL,
    id integer NOT NULL,
    ts date NOT NULL,
    CONSTRAINT variables_type_check CHECK ((((type)::text = 'E'::text) OR ((type)::text = 'N'::text)))
);


--
-- TOC entry 2521 (class 1259 OID 60475)
-- Dependencies: 6
-- Name: variables_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE variables_seq
    START WITH 1001
    INCREMENT BY 1
    MAXVALUE 2000000000
    MINVALUE 1001
    CACHE 100
    CYCLE;


--
-- TOC entry 2815 (class 2606 OID 60538)
-- Dependencies: 2390 2390 2390
-- Name: anatomy_model_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY anatomy_model_r
    ADD CONSTRAINT anatomy_model_pk PRIMARY KEY (eid, anid);


--
-- TOC entry 2813 (class 2606 OID 60540)
-- Dependencies: 2389 2389
-- Name: anatomy_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY anatomy
    ADD CONSTRAINT anatomy_pk PRIMARY KEY (anid);


--
-- TOC entry 2817 (class 2606 OID 60542)
-- Dependencies: 2392 2392
-- Name: available_genetic_back_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY available_genetic_back
    ADD CONSTRAINT available_genetic_back_pkey PRIMARY KEY (aid);


--
-- TOC entry 2819 (class 2606 OID 60544)
-- Dependencies: 2394 2394 2394
-- Name: chromosomes_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY chromosomes
    ADD CONSTRAINT chromosomes_name_key UNIQUE (name, sid);


--
-- TOC entry 2821 (class 2606 OID 60546)
-- Dependencies: 2394 2394
-- Name: chromosomes_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY chromosomes
    ADD CONSTRAINT chromosomes_pkey PRIMARY KEY (cid);


--
-- TOC entry 2827 (class 2606 OID 60548)
-- Dependencies: 2399 2399
-- Name: exp_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY expression
    ADD CONSTRAINT exp_pk PRIMARY KEY (exid);


--
-- TOC entry 2823 (class 2606 OID 60550)
-- Dependencies: 2397 2397
-- Name: expobj_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY expobj
    ADD CONSTRAINT expobj_pkey PRIMARY KEY (eid);


--
-- TOC entry 2825 (class 2606 OID 60552)
-- Dependencies: 2397 2397 2397
-- Name: expobj_suid_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY expobj
    ADD CONSTRAINT expobj_suid_key UNIQUE (suid, identity);


--
-- TOC entry 2829 (class 2606 OID 60554)
-- Dependencies: 2400 2400 2400
-- Name: expression_file_r_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY expression_file_r
    ADD CONSTRAINT expression_file_r_pk PRIMARY KEY (exid, fileid);


--
-- TOC entry 2831 (class 2606 OID 60556)
-- Dependencies: 2402 2402 2402
-- Name: expression_model_r_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY expression_model_r
    ADD CONSTRAINT expression_model_r_pk PRIMARY KEY (eid, exid);


--
-- TOC entry 2833 (class 2606 OID 60558)
-- Dependencies: 2403 2403
-- Name: file_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_pkey PRIMARY KEY (fileid);


--
-- TOC entry 2835 (class 2606 OID 60560)
-- Dependencies: 2405 2405 2405
-- Name: filters_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY filters
    ADD CONSTRAINT filters_name_key UNIQUE (name, pid);


--
-- TOC entry 2837 (class 2606 OID 60562)
-- Dependencies: 2405 2405
-- Name: filters_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY filters
    ADD CONSTRAINT filters_pkey PRIMARY KEY (fid);


--
-- TOC entry 2851 (class 2606 OID 60564)
-- Dependencies: 2412 2412
-- Name: gbid_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY genetic_back
    ADD CONSTRAINT gbid_pkey PRIMARY KEY (gbid);


--
-- TOC entry 2839 (class 2606 OID 60566)
-- Dependencies: 2408 2408
-- Name: gene_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY gene
    ADD CONSTRAINT gene_name_key UNIQUE (name);


--
-- TOC entry 2841 (class 2606 OID 60568)
-- Dependencies: 2408 2408
-- Name: gene_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY gene
    ADD CONSTRAINT gene_pkey PRIMARY KEY (gaid);


--
-- TOC entry 2854 (class 2606 OID 60570)
-- Dependencies: 2414 2414
-- Name: genetic_back_values_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY genetic_back_values
    ADD CONSTRAINT genetic_back_values_pkey PRIMARY KEY (bid);


--
-- TOC entry 2858 (class 2606 OID 60572)
-- Dependencies: 2422 2422 2422
-- Name: is_cm_model_r_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY is_cm_model_r
    ADD CONSTRAINT is_cm_model_r_pk PRIMARY KEY (eid, iscmid);


--
-- TOC entry 2856 (class 2606 OID 60574)
-- Dependencies: 2421 2421
-- Name: is_cm_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY is_cm
    ADD CONSTRAINT is_cm_pk PRIMARY KEY (iscmid);


--
-- TOC entry 2860 (class 2606 OID 60576)
-- Dependencies: 2426 2426
-- Name: link_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY link
    ADD CONSTRAINT link_pkey PRIMARY KEY (linkid);


--
-- TOC entry 2863 (class 2606 OID 60578)
-- Dependencies: 2430 2430
-- Name: model_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY model
    ADD CONSTRAINT model_pkey PRIMARY KEY (eid);


--
-- TOC entry 2865 (class 2606 OID 60580)
-- Dependencies: 2431 2431
-- Name: mutation_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY mutation_type
    ADD CONSTRAINT mutation_type_pkey PRIMARY KEY (id);


--
-- TOC entry 2867 (class 2606 OID 60582)
-- Dependencies: 2434 2434
-- Name: pg_ts_cfg_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pg_ts_cfg
    ADD CONSTRAINT pg_ts_cfg_pkey PRIMARY KEY (ts_name);


--
-- TOC entry 2869 (class 2606 OID 60584)
-- Dependencies: 2435 2435 2435
-- Name: pg_ts_cfgmap_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pg_ts_cfgmap
    ADD CONSTRAINT pg_ts_cfgmap_pkey PRIMARY KEY (ts_name, tok_alias);


--
-- TOC entry 2871 (class 2606 OID 60586)
-- Dependencies: 2436 2436
-- Name: pg_ts_dict_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pg_ts_dict
    ADD CONSTRAINT pg_ts_dict_pkey PRIMARY KEY (dict_name);


--
-- TOC entry 2873 (class 2606 OID 60588)
-- Dependencies: 2437 2437
-- Name: pg_ts_parser_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pg_ts_parser
    ADD CONSTRAINT pg_ts_parser_pkey PRIMARY KEY (prs_name);


--
-- TOC entry 2875 (class 2606 OID 60590)
-- Dependencies: 2438 2438
-- Name: pheno_alt_id_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_alt_id
    ADD CONSTRAINT pheno_alt_id_pk PRIMARY KEY (id);


--
-- TOC entry 2879 (class 2606 OID 60592)
-- Dependencies: 2439 2439 2439
-- Name: pheno_alt_id_r_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_alt_id_r
    ADD CONSTRAINT pheno_alt_id_r_pk PRIMARY KEY (pheno_id, pheno_alt_id);


--
-- TOC entry 2877 (class 2606 OID 60594)
-- Dependencies: 2438 2438
-- Name: pheno_alt_id_unique; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_alt_id
    ADD CONSTRAINT pheno_alt_id_unique UNIQUE (alt_id);


--
-- TOC entry 2881 (class 2606 OID 60596)
-- Dependencies: 2441 2441 2441
-- Name: pheno_is_a_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_is_a
    ADD CONSTRAINT pheno_is_a_pk PRIMARY KEY (id_a, id_b);


--
-- TOC entry 2883 (class 2606 OID 60598)
-- Dependencies: 2442 2442 2442 2442 2442 2442 2442 2442 2442 2442 2442
-- Name: pheno_model_r_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_model_r_pk PRIMARY KEY (eid, mp01, mp02, mp03, mp04, mp05, mp06, mp07, mp08, mp09);


--
-- TOC entry 2885 (class 2606 OID 60600)
-- Dependencies: 2443 2443
-- Name: pheno_ontology_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_ontology
    ADD CONSTRAINT pheno_ontology_pk PRIMARY KEY (id);


--
-- TOC entry 2887 (class 2606 OID 60602)
-- Dependencies: 2444 2444
-- Name: pheno_synonym_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_synonym
    ADD CONSTRAINT pheno_synonym_pk PRIMARY KEY (id);


--
-- TOC entry 2891 (class 2606 OID 60604)
-- Dependencies: 2445 2445 2445
-- Name: pheno_synonym_r_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_synonym_r
    ADD CONSTRAINT pheno_synonym_r_pk PRIMARY KEY (pheno_id, pheno_synonym);


--
-- TOC entry 2889 (class 2606 OID 60606)
-- Dependencies: 2444 2444
-- Name: pheno_synonym_unique; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_synonym
    ADD CONSTRAINT pheno_synonym_unique UNIQUE (synonym);


--
-- TOC entry 2893 (class 2606 OID 60608)
-- Dependencies: 2447 2447
-- Name: pheno_xref_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_xref
    ADD CONSTRAINT pheno_xref_pk PRIMARY KEY (id);


--
-- TOC entry 2897 (class 2606 OID 60610)
-- Dependencies: 2448 2448 2448
-- Name: pheno_xref_r_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_xref_r
    ADD CONSTRAINT pheno_xref_r_pk PRIMARY KEY (pheno_id, xref_id);


--
-- TOC entry 2895 (class 2606 OID 60612)
-- Dependencies: 2447 2447
-- Name: pheno_xref_unique; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pheno_xref
    ADD CONSTRAINT pheno_xref_unique UNIQUE (xref);


--
-- TOC entry 2900 (class 2606 OID 60614)
-- Dependencies: 2450 2450 2450
-- Name: phenotypes_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY phenotypes
    ADD CONSTRAINT phenotypes_pkey PRIMARY KEY (vid, iid);


--
-- TOC entry 2941 (class 2606 OID 60616)
-- Dependencies: 2476 2476 2476
-- Name: pk_resource_model; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_resource_model
    ADD CONSTRAINT pk_resource_model PRIMARY KEY (eid, resourceid);


--
-- TOC entry 2902 (class 2606 OID 60618)
-- Dependencies: 2454 2454
-- Name: privileges__name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY privileges_
    ADD CONSTRAINT privileges__name_key UNIQUE (name);


--
-- TOC entry 2904 (class 2606 OID 60620)
-- Dependencies: 2454 2454
-- Name: privileges__pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY privileges_
    ADD CONSTRAINT privileges__pkey PRIMARY KEY (prid);


--
-- TOC entry 2906 (class 2606 OID 60622)
-- Dependencies: 2456 2456
-- Name: projects_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT projects_name_key UNIQUE (name);


--
-- TOC entry 2908 (class 2606 OID 60624)
-- Dependencies: 2456 2456
-- Name: projects_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (pid);


--
-- TOC entry 2910 (class 2606 OID 60626)
-- Dependencies: 2462 2462 2462
-- Name: r_e_e_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_expression_emap
    ADD CONSTRAINT r_e_e_pk PRIMARY KEY (exid, emap);


--
-- TOC entry 2912 (class 2606 OID 60628)
-- Dependencies: 2463 2463 2463 2463
-- Name: r_e_o_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_expression_ontology
    ADD CONSTRAINT r_e_o_pk PRIMARY KEY (exid, oid, namespace);


--
-- TOC entry 2914 (class 2606 OID 60630)
-- Dependencies: 2464 2464 2464
-- Name: r_file_su_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_file_su
    ADD CONSTRAINT r_file_su_pkey PRIMARY KEY (suid, fileid);


--
-- TOC entry 2916 (class 2606 OID 60632)
-- Dependencies: 2465 2465 2465
-- Name: r_gene_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_gene_model
    ADD CONSTRAINT r_gene_model_pkey PRIMARY KEY (gaid, eid);


--
-- TOC entry 2918 (class 2606 OID 60634)
-- Dependencies: 2466 2466 2466
-- Name: r_link_su_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_link_su
    ADD CONSTRAINT r_link_su_pkey PRIMARY KEY (suid, linkid);


--
-- TOC entry 2926 (class 2606 OID 60636)
-- Dependencies: 2469 2469 2469
-- Name: r_m_s_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_model_strain
    ADD CONSTRAINT r_m_s_pk PRIMARY KEY (model, strain);


--
-- TOC entry 2928 (class 2606 OID 60638)
-- Dependencies: 2470 2470 2470 2470
-- Name: r_m_sa_mt_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_model_strain_allele_mutation_type
    ADD CONSTRAINT r_m_sa_mt_pk PRIMARY KEY (model, strain_allele, mutation_type);


--
-- TOC entry 2924 (class 2606 OID 60640)
-- Dependencies: 2468 2468 2468 2468 2468 2468
-- Name: r_mod_rep_avg_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_model_repositories_avgenback
    ADD CONSTRAINT r_mod_rep_avg_pkey PRIMARY KEY (eid, rid, aid, stateid, typeid);


--
-- TOC entry 2920 (class 2606 OID 60642)
-- Dependencies: 2467 2467 2467 2467
-- Name: r_model_imsr_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_model_imsr
    ADD CONSTRAINT r_model_imsr_pk PRIMARY KEY (eid, imsr, ts);


--
-- TOC entry 2931 (class 2606 OID 60644)
-- Dependencies: 2471 2471 2471
-- Name: r_mutation_type_strain_allele_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_mutation_type_strain_allele
    ADD CONSTRAINT r_mutation_type_strain_allele_pkey PRIMARY KEY (mutationtype, strainallele);


--
-- TOC entry 2933 (class 2606 OID 60646)
-- Dependencies: 2472 2472 2472
-- Name: r_prj_rol_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_prj_rol
    ADD CONSTRAINT r_prj_rol_pkey PRIMARY KEY (pid, id);


--
-- TOC entry 2935 (class 2606 OID 60648)
-- Dependencies: 2473 2473 2473
-- Name: r_prj_spc_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_prj_spc
    ADD CONSTRAINT r_prj_spc_pkey PRIMARY KEY (pid, sid);


--
-- TOC entry 2937 (class 2606 OID 60650)
-- Dependencies: 2474 2474 2474
-- Name: r_prj_su_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_prj_su
    ADD CONSTRAINT r_prj_su_pkey PRIMARY KEY (suid, pid);


--
-- TOC entry 2939 (class 2606 OID 60652)
-- Dependencies: 2475 2475 2475
-- Name: r_ref_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_ref_model
    ADD CONSTRAINT r_ref_model_pkey PRIMARY KEY (refid, eid);


--
-- TOC entry 2943 (class 2606 OID 60654)
-- Dependencies: 2477 2477 2477
-- Name: r_rol_pri_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_rol_pri
    ADD CONSTRAINT r_rol_pri_pkey PRIMARY KEY (rid, prid);


--
-- TOC entry 2945 (class 2606 OID 60656)
-- Dependencies: 2478 2478 2478
-- Name: r_s_a_g_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_strain_allele_gene
    ADD CONSTRAINT r_s_a_g_pk PRIMARY KEY (aid, gid);


--
-- TOC entry 2947 (class 2606 OID 60658)
-- Dependencies: 2479 2479 2479 2479
-- Name: r_s_sa_mt_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_strain_strain_allele_mutation_type
    ADD CONSTRAINT r_s_sa_mt_pk PRIMARY KEY (strain, strain_allele, mutation_type);


--
-- TOC entry 2950 (class 2606 OID 60660)
-- Dependencies: 2480 2480 2480
-- Name: r_strain_strain_state_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_strain_strain_state
    ADD CONSTRAINT r_strain_strain_state_pkey PRIMARY KEY (strainid, stateid);


--
-- TOC entry 2953 (class 2606 OID 60662)
-- Dependencies: 2481 2481 2481
-- Name: r_strain_strain_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY r_strain_strain_type
    ADD CONSTRAINT r_strain_strain_type_pkey PRIMARY KEY (strainid, typeid);


--
-- TOC entry 2955 (class 2606 OID 60664)
-- Dependencies: 2482 2482
-- Name: reference_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY reference
    ADD CONSTRAINT reference_pkey PRIMARY KEY (refid);


--
-- TOC entry 2957 (class 2606 OID 60666)
-- Dependencies: 2484 2484
-- Name: repositories_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY repositories
    ADD CONSTRAINT repositories_pkey PRIMARY KEY (rid);


--
-- TOC entry 2959 (class 2606 OID 60668)
-- Dependencies: 2486 2486
-- Name: research_application_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY research_application
    ADD CONSTRAINT research_application_pkey PRIMARY KEY (raid);


--
-- TOC entry 2963 (class 2606 OID 60670)
-- Dependencies: 2489 2489
-- Name: resource_category_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY resource_category
    ADD CONSTRAINT resource_category_pkey PRIMARY KEY (category_id);


--
-- TOC entry 2961 (class 2606 OID 60672)
-- Dependencies: 2488 2488
-- Name: resource_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY resource
    ADD CONSTRAINT resource_pkey PRIMARY KEY (resource_id);


--
-- TOC entry 2965 (class 2606 OID 60674)
-- Dependencies: 2492 2492 2492
-- Name: roles__pid_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY roles_
    ADD CONSTRAINT roles__pid_key UNIQUE (pid, name);


--
-- TOC entry 2967 (class 2606 OID 60676)
-- Dependencies: 2492 2492
-- Name: roles__pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY roles_
    ADD CONSTRAINT roles__pkey PRIMARY KEY (rid);


--
-- TOC entry 2986 (class 2606 OID 60678)
-- Dependencies: 2507 2507
-- Name: s_l_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY strain_links
    ADD CONSTRAINT s_l_pk PRIMARY KEY (id);


--
-- TOC entry 2969 (class 2606 OID 60680)
-- Dependencies: 2495 2495
-- Name: sampling_units_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY sampling_units
    ADD CONSTRAINT sampling_units_name_key UNIQUE (name);


--
-- TOC entry 2971 (class 2606 OID 60682)
-- Dependencies: 2495 2495
-- Name: sampling_units_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY sampling_units
    ADD CONSTRAINT sampling_units_pkey PRIMARY KEY (suid);


--
-- TOC entry 2973 (class 2606 OID 60684)
-- Dependencies: 2498 2498
-- Name: simplelog_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY simplelog
    ADD CONSTRAINT simplelog_pkey PRIMARY KEY (logid);


--
-- TOC entry 2975 (class 2606 OID 60686)
-- Dependencies: 2500 2500
-- Name: spatial_ref_sys_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY spatial_ref_sys
    ADD CONSTRAINT spatial_ref_sys_pkey PRIMARY KEY (srid);


--
-- TOC entry 2977 (class 2606 OID 60688)
-- Dependencies: 2501 2501
-- Name: species_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY species
    ADD CONSTRAINT species_name_key UNIQUE (name);


--
-- TOC entry 2979 (class 2606 OID 60690)
-- Dependencies: 2501 2501
-- Name: species_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY species
    ADD CONSTRAINT species_pkey PRIMARY KEY (sid);


--
-- TOC entry 2984 (class 2606 OID 60692)
-- Dependencies: 2504 2504
-- Name: strain_allele_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY strain_allele
    ADD CONSTRAINT strain_allele_pkey PRIMARY KEY (id);


--
-- TOC entry 2981 (class 2606 OID 60694)
-- Dependencies: 2503 2503
-- Name: strain_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY strain
    ADD CONSTRAINT strain_pkey PRIMARY KEY (strainid);


--
-- TOC entry 2988 (class 2606 OID 60696)
-- Dependencies: 2509 2509
-- Name: strain_state_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY strain_state
    ADD CONSTRAINT strain_state_pkey PRIMARY KEY (id);


--
-- TOC entry 2990 (class 2606 OID 60698)
-- Dependencies: 2510 2510
-- Name: strain_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY strain_type
    ADD CONSTRAINT strain_type_pkey PRIMARY KEY (id);


--
-- TOC entry 2992 (class 2606 OID 60700)
-- Dependencies: 2516 2516
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 2994 (class 2606 OID 60702)
-- Dependencies: 2516 2516
-- Name: users_usr_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_usr_key UNIQUE (usr);


--
-- TOC entry 2996 (class 2606 OID 60704)
-- Dependencies: 2520 2520 2520
-- Name: variables_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY variables
    ADD CONSTRAINT variables_name_key UNIQUE (name, suid);


--
-- TOC entry 2998 (class 2606 OID 60706)
-- Dependencies: 2520 2520
-- Name: variables_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY variables
    ADD CONSTRAINT variables_pkey PRIMARY KEY (vid);


--
-- TOC entry 2842 (class 1259 OID 60707)
-- Dependencies: 2412
-- Name: fki_backcrossing_str_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_backcrossing_str_fkey ON genetic_back USING btree (backcrossing_strain);


--
-- TOC entry 2843 (class 1259 OID 60708)
-- Dependencies: 2412
-- Name: fki_dna_origin_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_dna_origin_fkey ON genetic_back USING btree (dna_origin);


--
-- TOC entry 2844 (class 1259 OID 60709)
-- Dependencies: 2412
-- Name: fki_eid_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_eid_fkey ON genetic_back USING btree (eid);


--
-- TOC entry 2845 (class 1259 OID 60710)
-- Dependencies: 2412
-- Name: fki_eid_model_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_eid_model_fkey ON genetic_back USING btree (eid);


--
-- TOC entry 2846 (class 1259 OID 60711)
-- Dependencies: 2412
-- Name: fki_host_back_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_host_back_fkey ON genetic_back USING btree (host_back);


--
-- TOC entry 2861 (class 1259 OID 60712)
-- Dependencies: 2430
-- Name: fki_model_strain_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_model_strain_fkey ON model USING btree (strain);


--
-- TOC entry 2898 (class 1259 OID 60713)
-- Dependencies: 2450
-- Name: fki_phenotypes_iid_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_phenotypes_iid_fkey ON phenotypes USING btree (iid);


--
-- TOC entry 2852 (class 1259 OID 60714)
-- Dependencies: 2414
-- Name: fki_pid_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_pid_fkey ON genetic_back_values USING btree (pid);


--
-- TOC entry 2929 (class 1259 OID 60715)
-- Dependencies: 2471
-- Name: fki_r_mutation_type_strain_allele_strainallele_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_r_mutation_type_strain_allele_strainallele_fkey ON r_mutation_type_strain_allele USING btree (strainallele);


--
-- TOC entry 2921 (class 1259 OID 60716)
-- Dependencies: 2468
-- Name: fki_r_state_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_r_state_fkey ON r_model_repositories_avgenback USING btree (stateid);


--
-- TOC entry 2948 (class 1259 OID 60717)
-- Dependencies: 2480
-- Name: fki_r_strain_strain_state_strainid_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_r_strain_strain_state_strainid_fkey ON r_strain_strain_state USING btree (strainid);


--
-- TOC entry 2951 (class 1259 OID 60718)
-- Dependencies: 2481
-- Name: fki_r_strain_strain_type_strainid_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_r_strain_strain_type_strainid_fkey ON r_strain_strain_type USING btree (strainid);


--
-- TOC entry 2922 (class 1259 OID 60719)
-- Dependencies: 2468
-- Name: fki_r_type_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_r_type_fkey ON r_model_repositories_avgenback USING btree (typeid);


--
-- TOC entry 2982 (class 1259 OID 60720)
-- Dependencies: 2504
-- Name: fki_strainid_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_strainid_fkey ON strain_allele USING btree (strainid);


--
-- TOC entry 2847 (class 1259 OID 60721)
-- Dependencies: 2412
-- Name: fki_targeted_back_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_targeted_back_fkey ON genetic_back USING btree (targeted_back);


--
-- TOC entry 2848 (class 1259 OID 60722)
-- Dependencies: 2412
-- Name: fki_test_con; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_test_con ON genetic_back USING btree (dna_origin);


--
-- TOC entry 2849 (class 1259 OID 60723)
-- Dependencies: 2412
-- Name: fki_test_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_test_fkey ON genetic_back USING btree (dna_origin);


--
-- TOC entry 2999 (class 2606 OID 60724)
-- Dependencies: 2390 2389 2812
-- Name: anatomy_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY anatomy_model_r
    ADD CONSTRAINT anatomy_fk FOREIGN KEY (anid) REFERENCES anatomy(anid) ON DELETE CASCADE;


--
-- TOC entry 3011 (class 2606 OID 60729)
-- Dependencies: 2412 2414 2853
-- Name: backcrossing_str_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY genetic_back
    ADD CONSTRAINT backcrossing_str_fkey FOREIGN KEY (backcrossing_strain) REFERENCES genetic_back_values(bid);


--
-- TOC entry 3012 (class 2606 OID 60734)
-- Dependencies: 2412 2414 2853
-- Name: dna_origin_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY genetic_back
    ADD CONSTRAINT dna_origin_fkey FOREIGN KEY (dna_origin) REFERENCES genetic_back_values(bid);


--
-- TOC entry 3013 (class 2606 OID 60739)
-- Dependencies: 2412 2430 2862
-- Name: eid_model_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY genetic_back
    ADD CONSTRAINT eid_model_fkey FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3001 (class 2606 OID 60744)
-- Dependencies: 2397 2495 2970
-- Name: expobj_suid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY expobj
    ADD CONSTRAINT expobj_suid_fkey FOREIGN KEY (suid) REFERENCES sampling_units(suid);


--
-- TOC entry 3002 (class 2606 OID 60749)
-- Dependencies: 2400 2399 2826
-- Name: expression_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY expression_file_r
    ADD CONSTRAINT expression_fk FOREIGN KEY (exid) REFERENCES expression(exid) ON DELETE CASCADE;


--
-- TOC entry 3004 (class 2606 OID 60754)
-- Dependencies: 2402 2399 2826
-- Name: expression_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY expression_model_r
    ADD CONSTRAINT expression_fk FOREIGN KEY (exid) REFERENCES expression(exid) ON DELETE CASCADE;


--
-- TOC entry 3003 (class 2606 OID 60759)
-- Dependencies: 2400 2403 2832
-- Name: file_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY expression_file_r
    ADD CONSTRAINT file_fk FOREIGN KEY (fileid) REFERENCES file(fileid) ON DELETE CASCADE;


--
-- TOC entry 3006 (class 2606 OID 60764)
-- Dependencies: 2403 2516 2991
-- Name: file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_id_fkey FOREIGN KEY (id) REFERENCES users(id);


--
-- TOC entry 3007 (class 2606 OID 60769)
-- Dependencies: 2403 2456 2907
-- Name: file_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid);


--
-- TOC entry 3008 (class 2606 OID 60774)
-- Dependencies: 2406 2836 2405
-- Name: filters_log_fid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY filters_log
    ADD CONSTRAINT filters_log_fid_fkey FOREIGN KEY (fid) REFERENCES filters(fid) ON DELETE CASCADE;


--
-- TOC entry 3077 (class 2606 OID 60779)
-- Dependencies: 2488 2960 2476
-- Name: fk_resource_model; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_resource_model
    ADD CONSTRAINT fk_resource_model FOREIGN KEY (resourceid) REFERENCES resource(resource_id) ON DELETE CASCADE;


--
-- TOC entry 3009 (class 2606 OID 60784)
-- Dependencies: 2394 2820 2408
-- Name: gene_chromosome_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY gene
    ADD CONSTRAINT gene_chromosome_fk FOREIGN KEY (cid) REFERENCES chromosomes(cid);


--
-- TOC entry 3010 (class 2606 OID 60789)
-- Dependencies: 2456 2408 2907
-- Name: gene_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY gene
    ADD CONSTRAINT gene_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid) ON DELETE CASCADE;


--
-- TOC entry 3014 (class 2606 OID 60794)
-- Dependencies: 2414 2412 2853
-- Name: host_back_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY genetic_back
    ADD CONSTRAINT host_back_fkey FOREIGN KEY (host_back) REFERENCES genetic_back_values(bid);


--
-- TOC entry 3017 (class 2606 OID 60799)
-- Dependencies: 2430 2422 2862
-- Name: is_cm_model_r_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY is_cm_model_r
    ADD CONSTRAINT is_cm_model_r_fk_1 FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3018 (class 2606 OID 60804)
-- Dependencies: 2421 2422 2855
-- Name: is_cm_model_r_fk_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY is_cm_model_r
    ADD CONSTRAINT is_cm_model_r_fk_2 FOREIGN KEY (iscmid) REFERENCES is_cm(iscmid) ON DELETE CASCADE;


--
-- TOC entry 3019 (class 2606 OID 60809)
-- Dependencies: 2907 2456 2426
-- Name: link_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY link
    ADD CONSTRAINT link_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid);


--
-- TOC entry 3020 (class 2606 OID 60814)
-- Dependencies: 2486 2430 2958
-- Name: model_application_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY model
    ADD CONSTRAINT model_application_fkey FOREIGN KEY (application) REFERENCES research_application(raid);


--
-- TOC entry 3021 (class 2606 OID 60819)
-- Dependencies: 2991 2430 2516
-- Name: model_contact_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY model
    ADD CONSTRAINT model_contact_fkey FOREIGN KEY (contact) REFERENCES users(id);


--
-- TOC entry 3022 (class 2606 OID 60824)
-- Dependencies: 2822 2430 2397
-- Name: model_eid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY model
    ADD CONSTRAINT model_eid_fkey FOREIGN KEY (eid) REFERENCES expobj(eid);


--
-- TOC entry 3031 (class 2606 OID 60829)
-- Dependencies: 2862 2442 2430
-- Name: model_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT model_fk FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3005 (class 2606 OID 60834)
-- Dependencies: 2402 2862 2430
-- Name: model_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY expression_model_r
    ADD CONSTRAINT model_fk FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3000 (class 2606 OID 60839)
-- Dependencies: 2862 2390 2430
-- Name: model_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY anatomy_model_r
    ADD CONSTRAINT model_fk FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3023 (class 2606 OID 60844)
-- Dependencies: 2403 2430 2832
-- Name: model_genotyping_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY model
    ADD CONSTRAINT model_genotyping_fkey FOREIGN KEY (genotyping) REFERENCES file(fileid) ON DELETE SET NULL;


--
-- TOC entry 3024 (class 2606 OID 60849)
-- Dependencies: 2403 2430 2832
-- Name: model_handling_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY model
    ADD CONSTRAINT model_handling_fkey FOREIGN KEY (handling) REFERENCES file(fileid) ON DELETE SET NULL;


--
-- TOC entry 3025 (class 2606 OID 60854)
-- Dependencies: 2430 2503 2980
-- Name: model_strain_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY model
    ADD CONSTRAINT model_strain_fkey FOREIGN KEY (strain) REFERENCES strain(strainid) ON DELETE SET NULL;


--
-- TOC entry 3026 (class 2606 OID 60859)
-- Dependencies: 2431 2456 2907
-- Name: mutation_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY mutation_type
    ADD CONSTRAINT mutation_type_fkey FOREIGN KEY (pid) REFERENCES projects(pid);


--
-- TOC entry 3027 (class 2606 OID 60864)
-- Dependencies: 2439 2438 2874
-- Name: pheno_alt_id_r_alt_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_alt_id_r
    ADD CONSTRAINT pheno_alt_id_r_alt_id_fk FOREIGN KEY (pheno_alt_id) REFERENCES pheno_alt_id(id);


--
-- TOC entry 3028 (class 2606 OID 60869)
-- Dependencies: 2443 2439 2884
-- Name: pheno_alt_id_r_pheno_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_alt_id_r
    ADD CONSTRAINT pheno_alt_id_r_pheno_fk FOREIGN KEY (pheno_id) REFERENCES pheno_ontology(id);


--
-- TOC entry 3032 (class 2606 OID 60874)
-- Dependencies: 2442 2884 2443
-- Name: pheno_fk_00; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_00 FOREIGN KEY (mp01) REFERENCES pheno_ontology(id) ON DELETE CASCADE;


--
-- TOC entry 3033 (class 2606 OID 60879)
-- Dependencies: 2442 2443 2884
-- Name: pheno_fk_01; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_01 FOREIGN KEY (mp02) REFERENCES pheno_ontology(id) ON DELETE SET NULL;


--
-- TOC entry 3034 (class 2606 OID 60884)
-- Dependencies: 2442 2884 2443
-- Name: pheno_fk_02; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_02 FOREIGN KEY (mp03) REFERENCES pheno_ontology(id) ON DELETE SET NULL;


--
-- TOC entry 3035 (class 2606 OID 60889)
-- Dependencies: 2884 2442 2443
-- Name: pheno_fk_03; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_03 FOREIGN KEY (mp04) REFERENCES pheno_ontology(id) ON DELETE SET NULL;


--
-- TOC entry 3036 (class 2606 OID 60894)
-- Dependencies: 2442 2443 2884
-- Name: pheno_fk_04; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_04 FOREIGN KEY (mp05) REFERENCES pheno_ontology(id) ON DELETE SET NULL;


--
-- TOC entry 3037 (class 2606 OID 60899)
-- Dependencies: 2884 2442 2443
-- Name: pheno_fk_05; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_05 FOREIGN KEY (mp06) REFERENCES pheno_ontology(id) ON DELETE SET NULL;


--
-- TOC entry 3038 (class 2606 OID 60904)
-- Dependencies: 2442 2443 2884
-- Name: pheno_fk_06; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_06 FOREIGN KEY (mp07) REFERENCES pheno_ontology(id) ON DELETE SET NULL;


--
-- TOC entry 3039 (class 2606 OID 60909)
-- Dependencies: 2443 2442 2884
-- Name: pheno_fk_07; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_07 FOREIGN KEY (mp08) REFERENCES pheno_ontology(id) ON DELETE SET NULL;


--
-- TOC entry 3040 (class 2606 OID 60914)
-- Dependencies: 2442 2443 2884
-- Name: pheno_fk_08; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_model_r
    ADD CONSTRAINT pheno_fk_08 FOREIGN KEY (mp09) REFERENCES pheno_ontology(id) ON DELETE SET NULL;


--
-- TOC entry 3029 (class 2606 OID 60919)
-- Dependencies: 2443 2441 2884
-- Name: pheno_is_a_fk_a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_is_a
    ADD CONSTRAINT pheno_is_a_fk_a FOREIGN KEY (id_a) REFERENCES pheno_ontology(id);


--
-- TOC entry 3030 (class 2606 OID 60924)
-- Dependencies: 2884 2441 2443
-- Name: pheno_is_a_fk_b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_is_a
    ADD CONSTRAINT pheno_is_a_fk_b FOREIGN KEY (id_b) REFERENCES pheno_ontology(id);


--
-- TOC entry 3041 (class 2606 OID 60929)
-- Dependencies: 2443 2445 2884
-- Name: pheno_synonym_r_pheno_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_synonym_r
    ADD CONSTRAINT pheno_synonym_r_pheno_fk FOREIGN KEY (pheno_id) REFERENCES pheno_ontology(id);


--
-- TOC entry 3042 (class 2606 OID 60934)
-- Dependencies: 2886 2445 2444
-- Name: pheno_synonym_r_synonym_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_synonym_r
    ADD CONSTRAINT pheno_synonym_r_synonym_fk FOREIGN KEY (pheno_synonym) REFERENCES pheno_synonym(id);


--
-- TOC entry 3043 (class 2606 OID 60939)
-- Dependencies: 2448 2443 2884
-- Name: pheno_xref_r_pheno_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_xref_r
    ADD CONSTRAINT pheno_xref_r_pheno_fk FOREIGN KEY (pheno_id) REFERENCES pheno_ontology(id);


--
-- TOC entry 3044 (class 2606 OID 60944)
-- Dependencies: 2892 2448 2447
-- Name: pheno_xref_r_xref_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pheno_xref_r
    ADD CONSTRAINT pheno_xref_r_xref_fk FOREIGN KEY (xref_id) REFERENCES pheno_xref(id);


--
-- TOC entry 3045 (class 2606 OID 60949)
-- Dependencies: 2991 2516 2450
-- Name: phenotypes_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phenotypes
    ADD CONSTRAINT phenotypes_id_fkey FOREIGN KEY (id) REFERENCES users(id);


--
-- TOC entry 3046 (class 2606 OID 60954)
-- Dependencies: 2822 2450 2397
-- Name: phenotypes_iid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phenotypes
    ADD CONSTRAINT phenotypes_iid_fkey FOREIGN KEY (iid) REFERENCES expobj(eid) ON DELETE CASCADE;


--
-- TOC entry 3049 (class 2606 OID 60959)
-- Dependencies: 2450 2899 2451 2451 2450
-- Name: phenotypes_log_vid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phenotypes_log
    ADD CONSTRAINT phenotypes_log_vid_fkey FOREIGN KEY (vid, iid) REFERENCES phenotypes(vid, iid) ON DELETE CASCADE;


--
-- TOC entry 3047 (class 2606 OID 60964)
-- Dependencies: 2495 2450 2970
-- Name: phenotypes_suid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phenotypes
    ADD CONSTRAINT phenotypes_suid_fkey FOREIGN KEY (suid) REFERENCES sampling_units(suid);


--
-- TOC entry 3048 (class 2606 OID 60969)
-- Dependencies: 2520 2997 2450
-- Name: phenotypes_vid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY phenotypes
    ADD CONSTRAINT phenotypes_vid_fkey FOREIGN KEY (vid) REFERENCES variables(vid);


--
-- TOC entry 3016 (class 2606 OID 60974)
-- Dependencies: 2907 2456 2414
-- Name: pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY genetic_back_values
    ADD CONSTRAINT pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid);


--
-- TOC entry 3060 (class 2606 OID 60979)
-- Dependencies: 2816 2468 2392
-- Name: r_avback_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_repositories_avgenback
    ADD CONSTRAINT r_avback_fkey FOREIGN KEY (aid) REFERENCES available_genetic_back(aid) ON DELETE CASCADE;


--
-- TOC entry 3050 (class 2606 OID 60984)
-- Dependencies: 2462 2399 2826
-- Name: r_e_e_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_expression_emap
    ADD CONSTRAINT r_e_e_fk_1 FOREIGN KEY (exid) REFERENCES expression(exid) ON DELETE CASCADE;


--
-- TOC entry 3051 (class 2606 OID 60989)
-- Dependencies: 2399 2826 2463
-- Name: r_e_o_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_expression_ontology
    ADD CONSTRAINT r_e_o_fk_1 FOREIGN KEY (exid) REFERENCES expression(exid) ON DELETE CASCADE;


--
-- TOC entry 3052 (class 2606 OID 60994)
-- Dependencies: 2403 2464 2832
-- Name: r_file_su_fileid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_file_su
    ADD CONSTRAINT r_file_su_fileid_fkey FOREIGN KEY (fileid) REFERENCES file(fileid) ON DELETE CASCADE;


--
-- TOC entry 3053 (class 2606 OID 60999)
-- Dependencies: 2495 2464 2970
-- Name: r_file_su_suid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_file_su
    ADD CONSTRAINT r_file_su_suid_fkey FOREIGN KEY (suid) REFERENCES sampling_units(suid) ON DELETE CASCADE;


--
-- TOC entry 3054 (class 2606 OID 61004)
-- Dependencies: 2394 2465 2820
-- Name: r_gene_model_cid; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_gene_model
    ADD CONSTRAINT r_gene_model_cid FOREIGN KEY (cid) REFERENCES chromosomes(cid) ON DELETE CASCADE;


--
-- TOC entry 3055 (class 2606 OID 61009)
-- Dependencies: 2465 2862 2430
-- Name: r_gene_model_eid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_gene_model
    ADD CONSTRAINT r_gene_model_eid_fkey FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3056 (class 2606 OID 61014)
-- Dependencies: 2840 2408 2465
-- Name: r_gene_model_gaid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_gene_model
    ADD CONSTRAINT r_gene_model_gaid_fkey FOREIGN KEY (gaid) REFERENCES gene(gaid) ON DELETE CASCADE;


--
-- TOC entry 3057 (class 2606 OID 61019)
-- Dependencies: 2466 2859 2426
-- Name: r_link_su_linkid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_link_su
    ADD CONSTRAINT r_link_su_linkid_fkey FOREIGN KEY (linkid) REFERENCES link(linkid) ON DELETE CASCADE;


--
-- TOC entry 3058 (class 2606 OID 61024)
-- Dependencies: 2495 2970 2466
-- Name: r_link_su_suid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_link_su
    ADD CONSTRAINT r_link_su_suid_fkey FOREIGN KEY (suid) REFERENCES sampling_units(suid) ON DELETE CASCADE;


--
-- TOC entry 3065 (class 2606 OID 61029)
-- Dependencies: 2469 2430 2862
-- Name: r_m_s_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_strain
    ADD CONSTRAINT r_m_s_fk_1 FOREIGN KEY (model) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3066 (class 2606 OID 61034)
-- Dependencies: 2469 2503 2980
-- Name: r_m_s_fk_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_strain
    ADD CONSTRAINT r_m_s_fk_2 FOREIGN KEY (strain) REFERENCES strain(strainid) ON DELETE CASCADE;


--
-- TOC entry 3067 (class 2606 OID 61039)
-- Dependencies: 2430 2862 2470
-- Name: r_m_sa_mt_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_strain_allele_mutation_type
    ADD CONSTRAINT r_m_sa_mt_fk_1 FOREIGN KEY (model) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3068 (class 2606 OID 61044)
-- Dependencies: 2504 2983 2470
-- Name: r_m_sa_mt_fk_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_strain_allele_mutation_type
    ADD CONSTRAINT r_m_sa_mt_fk_2 FOREIGN KEY (strain_allele) REFERENCES strain_allele(id) ON DELETE CASCADE;


--
-- TOC entry 3061 (class 2606 OID 61049)
-- Dependencies: 2468 2430 2862
-- Name: r_model_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_repositories_avgenback
    ADD CONSTRAINT r_model_fkey FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3059 (class 2606 OID 61054)
-- Dependencies: 2430 2467 2862
-- Name: r_model_imsr_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_imsr
    ADD CONSTRAINT r_model_imsr_fk FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3070 (class 2606 OID 61059)
-- Dependencies: 2472 2991 2516
-- Name: r_prj_rol_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_prj_rol
    ADD CONSTRAINT r_prj_rol_id_fkey FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE;


--
-- TOC entry 3071 (class 2606 OID 61064)
-- Dependencies: 2907 2456 2472
-- Name: r_prj_rol_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_prj_rol
    ADD CONSTRAINT r_prj_rol_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid) ON DELETE CASCADE;


--
-- TOC entry 3072 (class 2606 OID 61069)
-- Dependencies: 2492 2966 2472
-- Name: r_prj_rol_rid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_prj_rol
    ADD CONSTRAINT r_prj_rol_rid_fkey FOREIGN KEY (rid) REFERENCES roles_(rid) ON DELETE CASCADE;


--
-- TOC entry 3073 (class 2606 OID 61074)
-- Dependencies: 2474 2456 2907
-- Name: r_prj_su_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_prj_su
    ADD CONSTRAINT r_prj_su_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid) ON DELETE CASCADE;


--
-- TOC entry 3074 (class 2606 OID 61079)
-- Dependencies: 2474 2495 2970
-- Name: r_prj_su_suid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_prj_su
    ADD CONSTRAINT r_prj_su_suid_fkey FOREIGN KEY (suid) REFERENCES sampling_units(suid) ON DELETE CASCADE;


--
-- TOC entry 3075 (class 2606 OID 61084)
-- Dependencies: 2475 2430 2862
-- Name: r_ref_model_eid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_ref_model
    ADD CONSTRAINT r_ref_model_eid_fkey FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3076 (class 2606 OID 61089)
-- Dependencies: 2475 2482 2954
-- Name: r_ref_model_refid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_ref_model
    ADD CONSTRAINT r_ref_model_refid_fkey FOREIGN KEY (refid) REFERENCES reference(refid) ON DELETE CASCADE;


--
-- TOC entry 3062 (class 2606 OID 61094)
-- Dependencies: 2468 2484 2956
-- Name: r_repo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_repositories_avgenback
    ADD CONSTRAINT r_repo_fkey FOREIGN KEY (rid) REFERENCES repositories(rid) ON DELETE CASCADE;


--
-- TOC entry 3078 (class 2606 OID 61099)
-- Dependencies: 2476 2862 2430
-- Name: r_resource_model_eid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_resource_model
    ADD CONSTRAINT r_resource_model_eid_fkey FOREIGN KEY (eid) REFERENCES model(eid) ON DELETE CASCADE;


--
-- TOC entry 3079 (class 2606 OID 61104)
-- Dependencies: 2477 2454 2903
-- Name: r_rol_pri_prid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_rol_pri
    ADD CONSTRAINT r_rol_pri_prid_fkey FOREIGN KEY (prid) REFERENCES privileges_(prid) ON DELETE CASCADE;


--
-- TOC entry 3080 (class 2606 OID 61109)
-- Dependencies: 2966 2492 2477
-- Name: r_rol_pri_rid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_rol_pri
    ADD CONSTRAINT r_rol_pri_rid_fkey FOREIGN KEY (rid) REFERENCES roles_(rid) ON DELETE CASCADE;


--
-- TOC entry 3081 (class 2606 OID 61114)
-- Dependencies: 2504 2478 2983
-- Name: r_s_a_g_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_allele_gene
    ADD CONSTRAINT r_s_a_g_fk_1 FOREIGN KEY (aid) REFERENCES strain_allele(id) ON DELETE CASCADE;


--
-- TOC entry 3082 (class 2606 OID 61119)
-- Dependencies: 2408 2840 2478
-- Name: r_s_a_g_fk_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_allele_gene
    ADD CONSTRAINT r_s_a_g_fk_2 FOREIGN KEY (gid) REFERENCES gene(gaid) ON DELETE CASCADE;


--
-- TOC entry 3083 (class 2606 OID 61124)
-- Dependencies: 2980 2503 2479
-- Name: r_s_sa_mt_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_strain_allele_mutation_type
    ADD CONSTRAINT r_s_sa_mt_fk_1 FOREIGN KEY (strain) REFERENCES strain(strainid) ON DELETE CASCADE;


--
-- TOC entry 3084 (class 2606 OID 61129)
-- Dependencies: 2983 2479 2504
-- Name: r_s_sa_mt_fk_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_strain_allele_mutation_type
    ADD CONSTRAINT r_s_sa_mt_fk_2 FOREIGN KEY (strain_allele) REFERENCES strain_allele(id) ON DELETE CASCADE;


--
-- TOC entry 3085 (class 2606 OID 61134)
-- Dependencies: 2864 2431 2479
-- Name: r_s_sa_mt_fk_3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_strain_allele_mutation_type
    ADD CONSTRAINT r_s_sa_mt_fk_3 FOREIGN KEY (mutation_type) REFERENCES mutation_type(id) ON DELETE CASCADE;


--
-- TOC entry 3069 (class 2606 OID 61139)
-- Dependencies: 2470 2431 2864
-- Name: r_s_sa_mt_fk_3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_strain_allele_mutation_type
    ADD CONSTRAINT r_s_sa_mt_fk_3 FOREIGN KEY (mutation_type) REFERENCES mutation_type(id) ON DELETE CASCADE;


--
-- TOC entry 3063 (class 2606 OID 61144)
-- Dependencies: 2509 2468 2987
-- Name: r_state_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_repositories_avgenback
    ADD CONSTRAINT r_state_fkey FOREIGN KEY (stateid) REFERENCES strain_state(id) ON DELETE CASCADE;


--
-- TOC entry 3086 (class 2606 OID 61149)
-- Dependencies: 2480 2987 2509
-- Name: r_strain_strain_state_stateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_strain_state
    ADD CONSTRAINT r_strain_strain_state_stateid_fkey FOREIGN KEY (stateid) REFERENCES strain_state(id);


--
-- TOC entry 3087 (class 2606 OID 61154)
-- Dependencies: 2503 2480 2980
-- Name: r_strain_strain_state_strainid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_strain_state
    ADD CONSTRAINT r_strain_strain_state_strainid_fkey FOREIGN KEY (strainid) REFERENCES strain(strainid) ON DELETE CASCADE;


--
-- TOC entry 3088 (class 2606 OID 61159)
-- Dependencies: 2980 2481 2503
-- Name: r_strain_strain_type_strainid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_strain_type
    ADD CONSTRAINT r_strain_strain_type_strainid_fkey FOREIGN KEY (strainid) REFERENCES strain(strainid) ON DELETE CASCADE;


--
-- TOC entry 3089 (class 2606 OID 61164)
-- Dependencies: 2989 2481 2510
-- Name: r_strain_strain_type_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_strain_strain_type
    ADD CONSTRAINT r_strain_strain_type_typeid_fkey FOREIGN KEY (typeid) REFERENCES strain_type(id);


--
-- TOC entry 3064 (class 2606 OID 61169)
-- Dependencies: 2989 2468 2510
-- Name: r_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY r_model_repositories_avgenback
    ADD CONSTRAINT r_type_fkey FOREIGN KEY (typeid) REFERENCES strain_type(id) ON DELETE CASCADE;


--
-- TOC entry 3090 (class 2606 OID 61174)
-- Dependencies: 2832 2482 2403
-- Name: reference_fileid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reference
    ADD CONSTRAINT reference_fileid_fkey FOREIGN KEY (fileid) REFERENCES file(fileid) ON DELETE SET NULL;


--
-- TOC entry 3091 (class 2606 OID 61179)
-- Dependencies: 2482 2516 2991
-- Name: reference_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reference
    ADD CONSTRAINT reference_id_fkey FOREIGN KEY (id) REFERENCES users(id);


--
-- TOC entry 3092 (class 2606 OID 61184)
-- Dependencies: 2859 2482 2426
-- Name: reference_linkid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reference
    ADD CONSTRAINT reference_linkid_fkey FOREIGN KEY (linkid) REFERENCES link(linkid) ON DELETE SET NULL;


--
-- TOC entry 3093 (class 2606 OID 61189)
-- Dependencies: 2907 2482 2456
-- Name: reference_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reference
    ADD CONSTRAINT reference_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid) ON DELETE CASCADE;


--
-- TOC entry 3094 (class 2606 OID 61194)
-- Dependencies: 2991 2516 2486
-- Name: research_application_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY research_application
    ADD CONSTRAINT research_application_id_fkey FOREIGN KEY (id) REFERENCES users(id);


--
-- TOC entry 3095 (class 2606 OID 61199)
-- Dependencies: 2907 2456 2486
-- Name: research_application_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY research_application
    ADD CONSTRAINT research_application_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid);


--
-- TOC entry 3096 (class 2606 OID 61204)
-- Dependencies: 2962 2489 2488
-- Name: resource_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resource
    ADD CONSTRAINT resource_category_id_fkey FOREIGN KEY (category_id) REFERENCES resource_category(category_id) ON DELETE CASCADE;


--
-- TOC entry 3101 (class 2606 OID 61209)
-- Dependencies: 2962 2489 2489
-- Name: resource_category_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resource_category
    ADD CONSTRAINT resource_category_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES resource_category(category_id) ON DELETE CASCADE;


--
-- TOC entry 3102 (class 2606 OID 61214)
-- Dependencies: 2456 2489 2907
-- Name: resource_category_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resource_category
    ADD CONSTRAINT resource_category_project_id_fkey FOREIGN KEY (project_id) REFERENCES projects(pid) ON DELETE CASCADE;


--
-- TOC entry 3103 (class 2606 OID 61219)
-- Dependencies: 2516 2489 2991
-- Name: resource_category_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resource_category
    ADD CONSTRAINT resource_category_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);


--
-- TOC entry 3097 (class 2606 OID 61224)
-- Dependencies: 2403 2488 2832
-- Name: resource_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resource
    ADD CONSTRAINT resource_file_id_fkey FOREIGN KEY (file_id) REFERENCES file(fileid) ON DELETE SET NULL;


--
-- TOC entry 3098 (class 2606 OID 61229)
-- Dependencies: 2426 2859 2488
-- Name: resource_link_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resource
    ADD CONSTRAINT resource_link_id_fkey FOREIGN KEY (link_id) REFERENCES link(linkid) ON DELETE SET NULL;


--
-- TOC entry 3099 (class 2606 OID 61234)
-- Dependencies: 2907 2456 2488
-- Name: resource_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resource
    ADD CONSTRAINT resource_project_id_fkey FOREIGN KEY (project_id) REFERENCES projects(pid) ON DELETE CASCADE;


--
-- TOC entry 3100 (class 2606 OID 61239)
-- Dependencies: 2991 2516 2488
-- Name: resource_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resource
    ADD CONSTRAINT resource_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);


--
-- TOC entry 3110 (class 2606 OID 61244)
-- Dependencies: 2507 2980 2503
-- Name: s_l_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY strain_links
    ADD CONSTRAINT s_l_fk_1 FOREIGN KEY (strainid) REFERENCES strain(strainid) ON DELETE CASCADE;


--
-- TOC entry 3104 (class 2606 OID 61249)
-- Dependencies: 2516 2495 2991
-- Name: sampling_units_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sampling_units
    ADD CONSTRAINT sampling_units_id_fkey FOREIGN KEY (id) REFERENCES users(id);


--
-- TOC entry 3106 (class 2606 OID 61254)
-- Dependencies: 2495 2496 2970
-- Name: sampling_units_log_suid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sampling_units_log
    ADD CONSTRAINT sampling_units_log_suid_fkey FOREIGN KEY (suid) REFERENCES sampling_units(suid) ON DELETE CASCADE;


--
-- TOC entry 3105 (class 2606 OID 61259)
-- Dependencies: 2501 2495 2978
-- Name: sampling_units_sid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sampling_units
    ADD CONSTRAINT sampling_units_sid_fkey FOREIGN KEY (sid) REFERENCES species(sid);


--
-- TOC entry 3108 (class 2606 OID 61264)
-- Dependencies: 2504 2408 2840
-- Name: strain_allele_gene_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY strain_allele
    ADD CONSTRAINT strain_allele_gene_fkey FOREIGN KEY (gene) REFERENCES gene(gaid) ON DELETE SET NULL;


--
-- TOC entry 3109 (class 2606 OID 61269)
-- Dependencies: 2431 2864 2504
-- Name: strain_allele_mutationtype_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY strain_allele
    ADD CONSTRAINT strain_allele_mutationtype_fkey FOREIGN KEY (mutationtype) REFERENCES mutation_type(id);


--
-- TOC entry 3107 (class 2606 OID 61274)
-- Dependencies: 2456 2907 2503
-- Name: strain_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY strain
    ADD CONSTRAINT strain_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid);


--
-- TOC entry 3111 (class 2606 OID 61279)
-- Dependencies: 2907 2456 2509
-- Name: strain_state_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY strain_state
    ADD CONSTRAINT strain_state_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid);


--
-- TOC entry 3112 (class 2606 OID 61284)
-- Dependencies: 2456 2907 2510
-- Name: strain_type_pid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY strain_type
    ADD CONSTRAINT strain_type_pid_fkey FOREIGN KEY (pid) REFERENCES projects(pid);


--
-- TOC entry 3015 (class 2606 OID 61289)
-- Dependencies: 2412 2414 2853
-- Name: targeted_back_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY genetic_back
    ADD CONSTRAINT targeted_back_fkey FOREIGN KEY (targeted_back) REFERENCES genetic_back_values(bid);


--
-- TOC entry 3113 (class 2606 OID 61294)
-- Dependencies: 2859 2516 2426
-- Name: users_group_link_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_group_link_fkey FOREIGN KEY (group_link) REFERENCES link(linkid);


--
-- TOC entry 3114 (class 2606 OID 61299)
-- Dependencies: 2426 2516 2859
-- Name: users_user_link_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_user_link_fkey FOREIGN KEY (user_link) REFERENCES link(linkid);


--
-- TOC entry 3115 (class 2606 OID 61304)
-- Dependencies: 2516 2991 2520
-- Name: variables_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY variables
    ADD CONSTRAINT variables_id_fkey FOREIGN KEY (id) REFERENCES users(id);


--
-- TOC entry 3116 (class 2606 OID 61309)
-- Dependencies: 2970 2495 2520
-- Name: variables_suid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY variables
    ADD CONSTRAINT variables_suid_fkey FOREIGN KEY (suid) REFERENCES sampling_units(suid);


--
-- TOC entry 3121 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2010-07-06 04:09:18

--
-- PostgreSQL database dump complete
--

