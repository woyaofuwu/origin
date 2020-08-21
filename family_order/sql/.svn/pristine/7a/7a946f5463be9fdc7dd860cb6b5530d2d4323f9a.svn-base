--IS_CACHE=Y
SELECT COUNT(1) recordcount
  FROM tf_m_staffdataright a
 WHERE (a.staff_id = :STAFF_ID
   AND a.data_code = :DATA_CODE
   AND a.right_attr = '0'
   AND a.data_type = '1'
   AND a.right_tag = '1')
    OR exists(SELECT 1 FROM tf_m_roledataright b
               WHERE a.staff_id = :STAFF_ID
                 AND a.data_code = b.role_code
                 AND a.right_attr = '1'   --权限属性：1-数据角色权限
                 AND a.right_tag = '1'    --权限标志：1-有效
                 AND a.data_type = '1'    --数据类型：1-数据特权
                 AND b.data_code = :DATA_CODE)