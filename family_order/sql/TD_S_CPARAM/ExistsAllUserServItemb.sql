SELECT SUM(recordnum) recordcount
  FROM
(SELECT  /*+ ordered use_nl(b,a) */COUNT(1) recordnum
  FROM td_b_attr_itemb b,tf_f_user_attr a
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.rsrv_num1 = :SERVICE_ID
   AND a.end_date > SYSDATE
   AND a.inst_type = 'S'
   AND a.attr_code = 'serv_para1'
   AND a.attr_value = b.attr_field_code
   AND a.rsrv_num1 = b.id AND b.id_type = 'S'
   AND b.attr_field_code = :ITEM_FIELD_CODE
   AND b.end_date > SYSDATE
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_svc
                   WHERE trade_id = TO_NUMBER(:TRADE_ID)
                     AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
                     AND service_id = :SERVICE_ID
                     AND (modify_tag = '2' or modify_tag = '1' or modify_tag = '5'))
UNION ALL
SELECT COUNT(1) recordnum
  FROM tf_b_trade_attr c
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND rsrv_num1 = :SERVICE_ID
   AND c.inst_type = 'S'
   AND (modify_tag = '0' or modify_tag = '2' or modify_tag = '4')
   AND c.attr_code = 'serv_para1'
   AND c.attr_value = :ITEM_FIELD_CODE)