INSERT INTO tf_b_trade_discnt(trade_id,accept_month,user_id,user_id_a,product_id,package_id,discnt_code,spec_tag,inst_id,modify_tag,start_date,end_date)
select to_number(:TRADE_ID),to_number(substrb(:TRADE_ID,5,2)),user_id,user_id_a,product_id,package_id,discnt_code,spec_tag,inst_id,'1',start_date,trunc(sysdate)-0.00001 
from tf_f_user_discnt a
where partition_id=mod(to_number(:USER_ID),10000)
  and user_id=to_number(:USER_ID)
  and start_date+0<end_date
  and end_date+0>sysdate
  and exists (select 1 from td_b_dtype_discnt
              where discnt_code=a.discnt_code
                and discnt_type_code='5')  --GPRS套餐
  and exists (select 1 from tf_b_trade_svc
              where trade_id=TO_NUMBER(:TRADE_ID)
                and accept_month=TO_NUMBER(SUBSTRB(:TRADE_ID,5,2))
                and user_id=to_number(:USER_ID)
                and service_id=22  --GPRS
                and modify_tag='1')
union all
select to_number(:TRADE_ID),to_number(substrb(:TRADE_ID,5,2)),user_id,user_id_a,product_id,package_id,discnt_code,spec_tag,inst_id,'1',start_date,trunc(sysdate)-0.00001
from tf_f_user_discnt a
where partition_id=mod(to_number(:USER_ID),10000)
  and user_id=to_number(:USER_ID)
  and start_date+0<end_date
  and end_date+0>sysdate
  and exists (select 1 from td_b_dtype_discnt
              where discnt_code=a.discnt_code
                and discnt_type_code='Q')  --彩铃套餐
  and exists (select 1 from tf_b_trade_svc
              where trade_id=TO_NUMBER(:TRADE_ID)
                and accept_month=TO_NUMBER(SUBSTRB(:TRADE_ID,5,2))
                and user_id=to_number(:USER_ID)
                and service_id=20  --彩铃
                and modify_tag='1')