--IS_CACHE=Y
SELECT COUNT(1) recordcount FROM
   (select data_code from  tf_m_staffdataright
   where  right_attr = '0' and data_type='1'  and right_tag = '1'
   and data_code =:RIGHT_CODE and staff_id = :STAFF_ID
   UNION
   select B.data_code from tf_m_staffdataright A,tf_m_roledataright B
   WHERE  A.right_attr = '1' and A.data_type='1' and A.data_code = B.role_code and right_tag = '1'
   and B.data_code = :RIGHT_CODE  and A.staff_id = :STAFF_ID)