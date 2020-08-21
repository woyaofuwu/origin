INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,start_date,end_date,update_time)
SELECT TO_NUMBER(:PARTITION_ID),TO_NUMBER(:USER_ID),-1,discnt_code,'0',start_date,end_date,sysdate
  FROM tf_b_trade_discnt a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND id = TO_NUMBER(:USER_ID)
   AND id_type = '1'
   AND modify_tag = '0'
   AND a.discnt_code=:DISCNT_CODE
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_discnt
                    WHERE user_id = TO_NUMBER(:USER_ID)
                      AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                      AND discnt_code = a.discnt_code
                      AND end_date > start_date
                      AND end_date > a.start_date)