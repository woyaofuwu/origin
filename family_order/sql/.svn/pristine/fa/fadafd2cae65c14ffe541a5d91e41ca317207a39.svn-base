select month, prize_type_code, count(*) max_value
   from tf_f_user_lottery
  where 1 = 1
    and (:MONTH is null or month = :MONTH)
    and (:START_DATE is null or accept_date >= to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'))    
    and (:END_DATE is null or accept_date < to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'))
  group by month, prize_type_code
  order by month, prize_type_code asc