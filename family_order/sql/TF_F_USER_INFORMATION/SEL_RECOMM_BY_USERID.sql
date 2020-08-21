SELECT to_char(trade_id) trade_id,to_char(user_id) user_id,notice_content,trade_attr,enable_tag,remark,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_information
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND trade_attr = '3'
   AND enable_tag = '1'
   AND sysdate between start_date and end_date