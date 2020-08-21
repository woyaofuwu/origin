insert into tf_f_user_telephone(partition_id,user_id,stand_address,detail_address,sign_path,port_type,secret,stand_address_code,
   start_date,end_date,update_time,update_staff_id,update_depart_id,remark)
 values(to_number(:PARTITION_ID),to_number(:USER_ID),:STAND_ADDRESS,:DETAIL_ADDRESS,:SIGN_PATH,
   :PORT_TYPE,:SECRET,to_number(:STAND_ADDRESS_CODE),to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),
        to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'), to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'), :UPDATE_STAFF_ID,:UPDATE_DEPART_ID,:REMARK)