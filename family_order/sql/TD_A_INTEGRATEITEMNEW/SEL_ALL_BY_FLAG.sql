--IS_CACHE=Y
SELECT integrate_item_code,integrate_item,flag,note_item_code,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id 
  FROM td_a_integrateitemnew
 WHERE flag=:FLAG
 ORDER BY integrate_item_code