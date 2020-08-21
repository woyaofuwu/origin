SELECT to_char(trade_id) trade_id,accept_month,to_char(USER_ID) USER_ID,discnt_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date  FROM tf_b_trade_discnt
 WHERE trade_id = :TRADE_ID
   AND accept_month = :ACCEPT_MONTH
   AND discnt_code IN (SELECT to_number(para_code1) FROM td_s_commpara
                        WHERE param_attr = 964
                          AND param_code = to_char(:TRADE_TYPE_CODE)
                          AND SUBSYS_CODE = 'CSM'
                          AND SYSDATE BETWEEN start_date AND end_date
                          AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ'))