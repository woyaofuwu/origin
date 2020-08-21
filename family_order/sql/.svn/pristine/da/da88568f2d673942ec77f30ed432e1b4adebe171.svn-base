INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,product_id,package_id,inst_id,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
SELECT MOD(user_id,10000),user_id,user_id_a,product_id,package_id,inst_id,discnt_code,spec_tag,relation_type_code,start_date,end_date,sysdate
  FROM TF_B_TRADE_DISCNT_BAK
 WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND USER_ID = TO_NUMBER(:USER_ID)
   AND END_DATE > SYSDATE
   AND PACKAGE_ID IN (SELECT T.PACKAGE_ID
                        FROM TF_F_USER_SALE_ACTIVE T
                       WHERE T.USER_ID = TO_NUMBER(:USER_ID)
                         AND T.END_DATE > SYSDATE)