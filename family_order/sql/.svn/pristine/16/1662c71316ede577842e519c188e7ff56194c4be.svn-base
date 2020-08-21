SELECT count(1) recordcount
  FROM tf_b_trade_discnt a
 WHERE trade_id=:TRADE_ID
   AND modify_tag='0'
   AND exists (select 1 from td_s_commpara
               where subsys_code='CSM'
                 and param_attr=1018
                 and param_code=:RSRV_STR5
                 and para_code5=to_char(a.discnt_code)
                 and para_code5 is not null
                 and eparchy_code=:TRADE_EPARCHY_CODE
                 and sysdate between start_date and end_date)
   AND (:TRADE_STAFF_ID!='SUPERUSR'
         AND NOT EXISTS (SELECT 1 FROM tf_m_staffdataright C,tf_m_roledataright D
                        WHERE C.staff_id=:TRADE_STAFF_ID
                          AND C.data_code=D.role_code
                          AND C.right_attr='1'   --权限属性：1-数据角色权限
                          AND C.right_tag='1'    --权限标志：1-有效
                          AND D.data_type='3'    --数据类型：3-资费权限
                          AND D.data_code=TO_CHAR(A.discnt_code)))