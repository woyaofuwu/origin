SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND start_date<end_date
   AND end_date>sysdate
   AND exists (select 1 from tf_b_trade_discnt
              where trade_id=to_number(:TRADE_ID)
               and accept_month=to_number(substrb(:TRADE_ID,5,2))
               and modify_tag='1'
               and discnt_code=a.discnt_code)