SELECT partition_id,to_char(user_id) user_id
		,trade_type_code,trade_id,product_id
		,brand_code,brand_no,serial_number
		,imsi,vip_id
		,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date
		,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
		,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
		,update_staff_id,update_depart_id,remark
  FROM tf_f_user_infochange
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   and trade_type_code =TO_NUMBER(:TRADE_TYPE_CODE)
   ORDER BY start_date Desc