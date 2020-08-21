SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE partition_id=TO_NUMBER(:PARTITION_ID)
   AND user_id=TO_NUMBER(:USER_ID)
   AND EXISTS(SELECT 1 FROM td_s_commpara WHERE param_attr = :PARAM_ATTR AND param_code = :PARAM_CODE 
                 AND para_code1 = to_char(a.discnt_code) AND  sysdate BETWEEN start_date AND end_date
                 AND eparchy_code = :EPARCHY_CODE)
   AND end_date > SYSDATE
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade b, tf_b_trade_discnt c WHERE b.user_id = a.user_id
                 AND b.exec_time > SYSDATE AND b.trade_id = c.trade_id AND c.discnt_code = a.discnt_code
                 AND c.modify_tag = '1') 
UNION 
SELECT 0 partition_id,to_char(b.user_id) user_id,'' user_id_a,c.discnt_code,'' spec_tag,'' relation_type_code,to_char(c.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(c.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_b_trade b, tf_b_trade_discnt c
 WHERE b.User_Id = TO_NUMBER(:USER_ID)
   AND b.exec_time > SYSDATE AND b.trade_id = c.trade_id
   AND c.modify_tag = '0'
   AND EXISTS(SELECT 1 FROM td_s_commpara WHERE param_attr = :PARAM_ATTR AND param_code = :PARAM_CODE 
                 AND para_code1 = to_char(c.discnt_code) AND  sysdate BETWEEN start_date AND end_date
                 AND eparchy_code = :EPARCHY_CODE)