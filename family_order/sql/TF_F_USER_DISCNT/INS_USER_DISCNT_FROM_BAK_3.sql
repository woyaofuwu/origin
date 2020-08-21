INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,product_id,package_id,inst_id,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
SELECT MOD(user_id,10000),user_id,user_id_a,:PRODUCT_ID,:PACKAGE_ID,f_sys_getseqid('ZZZZ','seq_inst_id'),discnt_code,spec_tag,relation_type_code,start_date,end_date,sysdate
  FROM tf_b_trade_discnt_bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
   And discnt_code=TO_NUMBER(:DISCNT_CODE)
   AND discnt_code not in (950,1111,1237,1238,1239)
   AND Exists (SELECT 1 FROM td_s_commpara a
                where a.subsys_code ='CSM'
                and a.param_attr = 6017
                and a.param_code = relation_type_code
                and a.end_date > sysdate
                )
   AND end_date > SYSDATE