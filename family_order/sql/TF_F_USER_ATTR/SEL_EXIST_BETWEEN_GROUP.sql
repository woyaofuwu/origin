select 1 from tf_f_user_attr a 
 where a.attr_code=:ATTR_CODE and a.attr_value=:ATTR_VALUE and (sysdate between a.start_date and a.end_date)
 and exists(
   select 1 from tf_f_user t where a.user_id=t.user_id and t.partition_id=mod(a.user_id,10000)
    and t.remove_tag='0' and t.cust_id!=:CUST_ID
 )
 and rownum=1