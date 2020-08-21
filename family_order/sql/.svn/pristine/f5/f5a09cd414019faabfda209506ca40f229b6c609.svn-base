SELECT  oth.user_id, oth.rsrv_value_code, oth.rsrv_value, oth.rsrv_num1, oth.rsrv_num2, oth.rsrv_num3,   
         oth.rsrv_str1, oth.rsrv_str2, oth.rsrv_str3, oth.rsrv_str4, oth.trade_id, 
         to_char(oth.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, to_char(oth.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date, 
         to_char(oth.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time, oth.update_depart_id, oth.update_depart_id, oth.inst_id 
 FROM tf_f_user u, tf_f_user_other oth 
 WHERE  u.user_id = oth.user_id  
       AND u.partition_id = mod(u.user_id,10000) 
			 AND  oth.user_id = :USER_ID 
			 AND  oth.rsrv_str4 = :USER_ID_A 
       AND  oth.RSRV_VALUE_CODE = 'VGPR' and u.remove_tag = '0'  
       AND  oth.start_date < sysdate  AND   oth.end_date > sysdate