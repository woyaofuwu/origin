DELETE FROM tf_f_relation_uu a
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND user_id_b = TO_NUMBER(:USER_ID_B)
   AND relation_type_code NOT IN (SELECT relation_type_code FROM td_s_relation WHERE relation_kind = 'F') --排除亲情关系
   AND (end_date > SYSDATE OR EXISTS
       (SELECT 1 FROM tf_b_trade_relation_uu_bak
         WHERE trade_id = TO_NUMBER(:TRADE_ID)
           AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
           AND user_id_a = a.user_id_a
           AND user_id_b = a.user_id_b
           AND relation_type_code = a.relation_type_code
           AND start_date = a.start_date))