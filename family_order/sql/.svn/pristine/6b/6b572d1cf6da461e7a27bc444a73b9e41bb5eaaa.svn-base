INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
select to_number(:TRADE_ID),to_number(substr(:TRADE_ID,5,2)),:USER_ID,'1',discnt_code,'1',start_date,to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
  from tf_f_user_discnt a
 where a.user_id = to_number(:USER_ID)
   and a.partition_id=mod(to_number(:USER_ID),10000)
   and a.end_date>to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   and a.discnt_code in (select to_number(para_code1)
                           from td_s_commpara
                          where param_attr=32
                            and subsys_code='CSM'
                            AND param_code = :PARAM_CODE)