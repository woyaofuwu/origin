--IS_CACHE=Y
SELECT tag_code,tag_name,subsys_code,tag_info,tag_char,tag_number,to_char(tag_date,'yyyy-mm-dd hh24:mi:ss') tag_date,tag_sequid,use_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code 
  FROM td_s_tag
 WHERE subsys_code=:SUBSYS_CODE
   AND use_tag=:USE_TAG
   AND start_date < sysdate
   AND end_date >= sysdate
   AND eparchy_code=:EPARCHY_CODE