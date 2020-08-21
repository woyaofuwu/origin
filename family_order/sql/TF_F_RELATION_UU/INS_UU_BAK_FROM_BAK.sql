INSERT INTO tf_b_trade_relation_uu_bak(trade_id,accept_month,partition_id,user_id_a,serial_number_a,user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,start_date,end_date,update_time)
(SELECT to_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),MOD(user_id_b,10000),user_id_a,serial_number_a,user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,TRUNC(SYSDATE),end_date,sysdate
  FROM tf_b_trade_relation_uu_bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id_b = TO_NUMBER(:USER_ID)
   AND relation_type_code NOT IN (SELECT relation_type_code FROM td_s_relation WHERE relation_kind = 'F') --排除亲情关系
   AND relation_type_code not in('26','66')   
   AND end_date > SYSDATE
UNION ALL
SELECT to_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),MOD(user_id_b,10000),user_id_a,serial_number_a,user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,sysdate,end_date,sysdate
  FROM tf_b_trade_relation_uu_bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id_b = TO_NUMBER(:USER_ID)
   AND relation_type_code='17'
   AND end_date > SYSDATE)