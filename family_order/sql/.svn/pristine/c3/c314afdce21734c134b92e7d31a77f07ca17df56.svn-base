--IS_CACHE=Y
SELECT a.discnt_code_b discnt_code_b
  FROM td_s_discnt_limit a
 where a.DISCNT_CODE_A = :DISCNT_CODE
   and a.limit_tag = '3'
   and sysdate between a.start_date and a.end_date
   and (a.eparchy_code = :EPARCHY_CODE or a.eparchy_code = 'ZZZZ')
   AND (:TRADE_STAFF_ID='SUPERUSR' OR EXISTS (SELECT 1 FROM tf_m_staffdataright C,tf_m_roledataright D
                                                WHERE C.staff_id=:TRADE_STAFF_ID
                                                  AND C.data_code=D.role_code
                                                  AND C.right_attr='1'   --权限属性：1-数据角色权限
                                                  AND C.right_tag='1'    --权限标志：1-有效
                                                  AND D.data_type='3'    --数据类型：3-优惠权限
                                                  AND D.data_code=TO_CHAR(a.discnt_code_b)))