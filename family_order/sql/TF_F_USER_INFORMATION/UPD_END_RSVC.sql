UPDATE tf_f_user_information
   SET enable_tag=:ENABLE_TAG,end_date=sysdate,update_time=sysdate  
 WHERE trade_id=(select max(trade_id) from tf_f_user_information b
                    where b.user_id=TO_NUMBER(:USER_ID)
                      and  b.TRADE_ATTR=:TRADE_ATTR
                   and b.ENABLE_TAG='1' 
                       and sysdate between b.start_date and b.end_date)