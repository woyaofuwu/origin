--IS_CACHE=Y
SELECT db_source,sid,host,port,jndi,eparchy_list,
       ''''||REPLACE(TRIM(eparchy_list),',',''',''')||'''' para_value
  FROM td_s_dbconfig
 WHERE UPPER(sid) = UPPER(NVL(:SID,(SELECT instance_name FROM V$instance WHERE instance_number=1)))