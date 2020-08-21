SELECT serial_number_code,moffice_id,imsi,stock_id,city_code,eparchy_code,stock_level,staff_id
FROM tf_r_simcard_idle
WHERE sim_card_no=:SIM_CARD_NO
AND sim_state_code='1' 
AND precode_tag='1'