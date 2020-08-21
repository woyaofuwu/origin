--IS_CACHE=Y
SELECT right_code,right_name,class_code,DECODE(right_attr,'1','界面控件权限','2','系统特权','菜单权限') right_attr,mod_code,to_char(help_index) help_index,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_funcright
 WHERE (:CLASS_CODE IS NULL OR class_code=:CLASS_CODE)
   AND (:RIGHT_ATTR IS NULL OR right_attr=:RIGHT_ATTR)
 ORDER BY right_attr,class_code,right_code