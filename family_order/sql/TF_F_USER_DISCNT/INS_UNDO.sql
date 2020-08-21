INSERT INTO tf_f_user_discnt(partition_id,user_id,inst_id,user_id_a,product_id,package_id,campn_id,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
SELECT MOD(user_id,10000),user_id,inst_id,user_id_a,product_id,package_id,campn_id,discnt_code,spec_tag,relation_type_code,start_date,end_date,sysdate
  FROM tf_b_trade_discnt_bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
   AND (relation_type_code IS NULL OR relation_type_code NOT LIKE '1%') --排除亲情关系