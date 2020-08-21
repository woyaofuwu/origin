SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE a.user_id = :USER_ID
   AND a.partition_id = MOD(to_number(:USER_ID),10000)
   AND  a.end_date > SYSDATE
   AND a.discnt_code IN (SELECT to_number(para_code1) FROM td_s_commpara
                        WHERE param_attr = 964
                          AND param_code = to_char(:TRADE_TYPE_CODE)
                          AND SUBSYS_CODE = 'CSM'
                          AND SYSDATE BETWEEN start_date AND end_date
                          AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ'))
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_discnt
                   WHERE trade_id = :TRADE_ID
                     AND accept_month = :ACCEPT_MONTH
                     AND discnt_code = a.discnt_code
                     AND modify_tag = '1')