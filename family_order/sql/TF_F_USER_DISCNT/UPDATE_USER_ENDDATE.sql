update TF_F_USER_DISCNT set end_date=to_date(to_char(last_day(sysdate -30),'yyyy-MM-dd')||' 23:59:59','yyyy-MM-dd hh24:mi:ss'),update_time = sysdate 
where partition_id = mod(to_number(:USER_ID),10000)
and user_id = to_number(:USER_ID)
and product_id != -1
and end_date > sysdate