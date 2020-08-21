INSERT INTO tf_fh_user_white(partition_id,user_id,serial_number,sp_id,sp_name,biz_code,biz_desc,start_date,
            end_date,staff_id,update_time,rsrv_str16,rsrv_str17,rsrv_str18,rsrv_str19,rsrv_str20) 
SELECT partition_id,user_id,serial_number,sp_id,sp_name,biz_code,biz_desc,start_date,
       to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),staff_id,update_time,rsrv_str16,rsrv_str17,rsrv_str18,
       rsrv_str19,rsrv_str20 
  FROM tf_f_user_white 
 WHERE partition_id = MOD(to_number(:USER_ID),10000)
   AND user_id = to_number(:USER_ID)
   AND serial_number = :SERIAL_NUMBER
   AND (biz_code=:BIZ_CODE or :BIZ_CODE = '*')