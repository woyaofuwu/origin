SELECT to_char(trade_id) trade_id,to_char(user_id) user_id,notice_content,trade_attr,enable_tag,remark,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_information a
 WHERE  a.trade_id =(select max(trade_id) from tf_f_user_information b
                    where b.user_id=TO_NUMBER(:USER_ID)
                      and  b.TRADE_ATTR!='1'
                   and b.ENABLE_TAG='1' 
                       and sysdate between b.start_date and b.end_date)