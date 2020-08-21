SELECT to_char(charge_id) charge_id,to_char(user_id) user_id,foregift_code,to_char(money) money,cancel_tag,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,to_char(cancel_time,'yyyy-mm-dd hh24:mi:ss') cancel_time,cancel_staff_id,cancel_depart_id 
  FROM tf_a_foregiftlog
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)