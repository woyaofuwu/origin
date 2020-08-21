--IS_CACHE=Y
SELECT eparchy_code,tag_code,tag_name,subsys_code,tag_info,tag_char,to_char(tag_number) tag_number,to_char(tag_date,'yyyy-mm-dd hh24:mi:ss') tag_date,to_char(tag_sequid) tag_sequid,use_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_s_tag
 WHERE subsys_code=:SUBSYS_CODE
   AND use_tag=:USE_TAG
   AND start_date < sysdate
   AND end_date >= sysdate