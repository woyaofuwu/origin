SELECT discnt_code_a,discnt_code_b,limit_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code
  FROM td_s_discnt_limit
 WHERE discnt_code_a IN(SELECT discnt_code
                          FROM tf_b_trade_discnt
                         WHERE trade_id = :TRADE_ID
                           AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                           AND modify_tag = '0')
   AND discnt_code_b IN(SELECT discnt_code
                          FROM tf_f_user_discnt a
                         WHERE a.user_id = :USER_ID
                           AND (a.spec_tag = :SPEC_TAG OR :SPEC_TAG IS NULL)
                           AND (a.relation_type_code = :RELATION_TYPE_CODE OR :RELATION_TYPE_CODE IS NULL)
                           AND SYSDATE < a.end_date
                           AND NOT EXISTS(SELECT 1
                                            FROM tf_b_trade_discnt
                                           WHERE trade_id = :TRADE_ID
                                             AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                                             AND a.user_id = id
                                             AND a.discnt_code = discnt_code
                                             AND modify_tag = '1'))
   AND limit_tag=:LIMIT_TAG
   AND SYSDATE BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')   
UNION ALL
SELECT discnt_code_a,discnt_code_b,limit_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code
  FROM td_s_discnt_limit
 WHERE discnt_code_b IN(SELECT discnt_code
                          FROM tf_b_trade_discnt
                         WHERE trade_id = :TRADE_ID
                           AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                           AND modify_tag = '0')
   AND discnt_code_a IN(SELECT discnt_code
                          FROM tf_f_user_discnt a
                         WHERE a.user_id = :USER_ID
                           AND (a.spec_tag = :SPEC_TAG OR :SPEC_TAG IS NULL)
                           AND (a.relation_type_code = :RELATION_TYPE_CODE OR :RELATION_TYPE_CODE IS NULL)
                           AND SYSDATE < a.end_date
                           AND NOT EXISTS(SELECT 1
                                            FROM tf_b_trade_discnt
                                           WHERE trade_id = :TRADE_ID
                                             AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                                             AND a.user_id = id
                                             AND a.discnt_code = discnt_code
                                             AND modify_tag = '1'))
   AND limit_tag=:LIMIT_TAG
   AND SYSDATE BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')