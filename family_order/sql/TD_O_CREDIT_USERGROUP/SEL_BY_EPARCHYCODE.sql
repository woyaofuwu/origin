--IS_CACHE=Y
SELECT user_group_id,name,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,eparchy_code 
  FROM td_o_credit_usergroup
 WHERE eparchy_code=:EPARCHY_CODE