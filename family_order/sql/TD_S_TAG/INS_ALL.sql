INSERT INTO td_s_tag(eparchy_code,tag_code,tag_name,subsys_code,tag_info,tag_char,tag_number,tag_date,tag_sequid,use_tag,start_date,end_date,remark,update_time,update_staff_id,update_depart_id)
 VALUES(:EPARCHY_CODE,:TAG_CODE,:TAG_NAME,:SUBSYS_CODE,:TAG_INFO,:TAG_CHAR,TO_NUMBER(:TAG_NUMBER),
TO_DATE(:TAG_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_NUMBER(:TAG_SEQUID),:USE_TAG,
SYSDATE,SYSDATE+3600,
:REMARK,SYSDATE,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID)