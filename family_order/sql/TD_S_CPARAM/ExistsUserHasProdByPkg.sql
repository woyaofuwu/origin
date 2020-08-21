select count(1) recordcount
  from tf_f_user_infochange t
 where user_id = to_number(:USER_ID)
  and  partition_id=mod(to_number(:USER_ID),10000)
  and  end_date > add_months(trunc(sysdate, 'mm'), 1)
  and exists(
    select 1 from td_S_commpara
    where subsys_code = 'CSM'
      and param_attr = :PARAM_ATTR
      and param_CODE = to_char(:PARAM_CODE)
      and (eparchy_code = :TRADE_EPARCHY_CODE or eparchy_code = 'ZZZZ')
      and para_code1 = to_char(t.product_id)
      and sysdate between start_date and end_date
  )