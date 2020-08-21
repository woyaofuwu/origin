SELECT to_char(inout_code) inout_code,to_char(oper_time,'yyyy-mm-dd hh24:mi:ss') oper_time,oper_staff_id,oper_flag,res_type_code,card_type_code,value_code,eparchy_code,city_code,depart_id,staff_id,to_char(oper_num) oper_num,to_char(batch_id) batch_id,to_char(log_id) log_id,start_res_no,end_res_no,rsrv_tag1,rsrv_tag2,rsrv_tag3,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,to_char(para_value9) para_value9,to_char(para_value10) para_value10,to_char(para_value11) para_value11,to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,remark2 
FROM tf_b_res_inout_log
WHERE oper_time>=TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
AND oper_time<=TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
AND eparchy_code =:EPARCHY_CODE 
AND (:OPER_STAFF_ID is null or  oper_staff_id =:OPER_STAFF_ID) 
AND  (:VALUE_CODE is null  or  value_code =:VALUE_CODE)
AND (:DEPART_ID is null or depart_id =:DEPART_ID)
AND (:CARD_TYPE_CODE is null or card_type_code =:CARD_TYPE_CODE)
AND res_type_code= :RES_TYPE_CODE
AND oper_flag=:OPER_FLAG