--IS_CACHE=Y
SELECT role_code,role_name,DECODE(role_attr,'0','功能角色','1','资源数据角色','2','数据特权角色','6','所有数据权限角色',role_attr) role_attr,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_role
 WHERE role_code LIKE :ROLE_CODE
   AND role_attr IN ('0','1','2','6','7')
 ORDER BY role_attr,role_code