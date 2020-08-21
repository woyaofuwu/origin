INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,start_date,end_date,user_id_a,product_id,package_id,inst_id,campn_id,update_time,update_staff_id,update_depart_id)
SELECT MOD(A.USER_ID, 10000),A.USER_ID,-1,discnt_code,:SPEC_TAG,start_date,end_date,user_id_a,product_id,package_id,inst_id,campn_id,sysdate,update_staff_id,update_depart_id
  FROM tf_b_trade_discnt A
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag = '0'
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_discnt
                    WHERE user_id = A.user_id
                      AND partition_id = MOD(A.USER_ID, 10000)
                      AND discnt_code = a.discnt_code
                      AND end_date > start_date
                      AND end_date > a.start_date
                      )