--IS_CACHE=Y
SELECT right_code,right_name,class_code,right_attr,mod_code,help_index,remark,update_time,update_staff_id,update_depart_id
FROM
(SELECT right_code,right_name,class_code,DECODE(right_attr,'1','界面控件权限','2','系统特权','菜单权限') right_attr,mod_code,to_char(help_index) help_index,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_funcright
 WHERE (:RIGHT_CODE IS NULL OR right_code=:RIGHT_CODE)
 UNION
SELECT data_code right_code,data_name right_name,class_code,DECODE(data_type,'0','资源权限','数据特权') right_attr,'' mod_code,to_char(help_index) help_index,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,'' update_depart_id 
  FROM td_m_dataright)