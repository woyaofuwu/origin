SELECT to_char(money) money
  FROM tf_chl_datbook
 WHERE chnl_id = :CHANNEL_ID 
   and chnl_obj_type = '005'
   and chnl_deposit = '700'