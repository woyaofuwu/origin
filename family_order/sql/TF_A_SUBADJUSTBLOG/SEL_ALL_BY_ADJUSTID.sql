SELECT to_char(adjust_id) adjust_id,detail_item_code,to_char(nvl(adjust_per,0)) adjust_per,to_char(nvl(adjust_fee,0)) adjust_fee,to_char(adjust_time,'yyyy-mm-dd hh24:mi:ss') adjust_time 
  FROM tf_a_subadjustblog
 WHERE adjust_id=TO_NUMBER(:ADJUST_ID)