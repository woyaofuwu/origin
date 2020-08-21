SELECT COUNT(*) recordcount
 FROM tf_b_trade a
 WHERE a.trade_id=:TRADE_ID
   AND (:STAFF_ID='SUPERUSR'
          OR EXISTS (SELECT 1 FROM tf_m_staffdataright C,tf_m_roledataright D
                                                WHERE C.staff_id=:STAFF_ID
                                                  AND C.data_code=D.role_code
                                                  AND C.right_attr='1'   --权限属性：1-数据角色权限
                                                  AND C.right_tag='1'    --权限标志：1-有效
                                                  AND D.data_type='K'    --数据类型：K-包权限
                                                  AND D.rsvalue1 in('0','2')   ----权限用途：0-使用，2-使用和授权
                                                  AND D.data_code=a.rsrv_str2)
          OR EXISTS (SELECT 1 FROM tf_m_staffdataright C
                                                WHERE C.staff_id=:STAFF_ID
                                                  AND C.right_attr='0'   --权限属性：0-数据权限
                                                  AND C.right_tag='1'    --权限标志：1-有效
                                                  AND C.data_type='K'    --数据类型：K-包权限
                                                  AND C.rsvalue1 in('0','2')   ----权限用途：0-使用，2-使用和授权
                                                  AND C.data_code=a.rsrv_str2))