--IS_CACHE=Y
SELECT value_code,value_name,value_attr,value1,use_tag,to_char(condition1) condition1,to_char(condition2) condition2,condition3,condition4,condition5,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id ,0 x_tag
  FROM td_m_black_control
 WHERE value_code=:VALUE_CODE