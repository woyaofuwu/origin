--IS_CACHE=Y
SELECT eparchy_code,res_type_code,assign_tag,assign_tag_name,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,0 x_tag
  FROM td_s_assigntag
 WHERE eparchy_code=:EPARCHY_CODE
   AND ((:RES_TYPE_CODE IS NOT NULL AND res_type_code=:RES_TYPE_CODE) OR :RES_TYPE_CODE IS NULL)
   AND ((:ASSIGN_TAG IS NOT NULL AND assign_tag=:ASSIGN_TAG) OR :ASSIGN_TAG IS NULL)