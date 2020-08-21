--IS_CACHE=Y
SELECT product_id,service_id,force_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_prod_svc_member A
 WHERE product_id=:PRODUCT_ID
   AND sysdate BETWEEN start_date AND end_date
   AND (:TRADE_STAFF_ID='SUPERUSR' OR EXISTS (SELECT 1 FROM tf_m_staffdataright C,tf_m_roledataright D
                                                WHERE C.staff_id=:TRADE_STAFF_ID
                                                  AND C.data_code=D.role_code
                                                  AND C.right_attr='1'   --权限属性：1-数据角色权限
                                                  AND C.right_tag='1'    --权限标志：1-有效
                                                  AND D.data_type='4'    --数据类型：4-资费权限
                                                  AND D.data_code=TO_CHAR(A.service_id)))