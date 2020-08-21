--IS_CACHE=Y
SELECT  COUNT(1) recordcount
FROM td_s_commpara A
WHERE subsys_code = 'CSM'
AND param_attr = 1022
AND SYSDATE BETWEEN start_date AND end_date
AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
AND param_code = :TRADE_TYPE_CODE
And (trunc(to_date(:ACCEPT_DATE,'YYYY-MM-DD HH24:MI:SS') + to_number(para_code1)) < Sysdate
      Or (para_code2 IS Not Null And
           NOT EXISTS (SELECT d.data_code FROM tf_m_staffdataright C,tf_m_roledataright D
                          WHERE C.staff_id = :STAFF_ID
                            AND C.data_code = D.role_code
                            AND C.right_attr = '1'   --权限属性：1-数据角色权限
                            AND C.right_tag = '1'    --权限标志：1-有效
                            AND D.data_type = '1'    --数据类型：1-数据特权
                            AND D.data_code = A.para_code2)
            AND NOT EXISTS (SELECT e.data_code FROM tf_m_staffdataright e
                                WHERE e.staff_id = :STAFF_ID
                                  AND e.right_attr = '0'
                                  AND e.data_type = '1'
                                  AND e.right_tag = '1'
                                  AND e.data_code = A.para_code2))
    )