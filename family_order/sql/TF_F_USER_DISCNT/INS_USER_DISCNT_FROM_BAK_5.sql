INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,product_id,package_id,inst_id,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
SELECT MOD(user_id,10000),user_id,user_id_a,:PRODUCT_ID,:PACKAGE_ID,f_sys_getseqid('ZZZZ','seq_inst_id'),discnt_code,spec_tag,relation_type_code,start_date,end_date,sysdate
  FROM tf_b_trade_discnt_bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
   And discnt_code = TO_NUMBER(:DISCNT_CODE)
   AND relation_type_code IN (SELECT relation_type_code FROM td_s_relation WHERE relation_kind = 'F') --恢复亲情关系
   AND end_date > SYSDATE