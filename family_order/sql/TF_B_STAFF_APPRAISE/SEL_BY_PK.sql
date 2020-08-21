SELECT staff_id,depart_id,city_code,eparchy_code,
       trade_id,trade_type_code,serial_number,
       appraise_code,appraise_info,appraise_time,
       update_time,update_staff_id,update_depart_id
FROM tf_b_staff_appraise WHERE trade_id = TO_NUMBER(:TRADE_ID)