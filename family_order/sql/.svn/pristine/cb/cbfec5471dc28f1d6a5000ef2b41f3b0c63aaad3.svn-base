SELECT to_char(id) id,id_type_code,disn_type_code,start_acyc_id,end_acyc_id,refer_item_code,effect_item_code,discnt_mode,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id 
  FROM tf_f_specific_discnt
 WHERE id=TO_NUMBER(:ID)
 AND end_acyc_id >=(SELECT acyc_id FROM td_a_acycpara WHERE SYSDATE BETWEEN acyc_start_time AND acyc_end_time-1/24/3600)
AND start_acyc_id<=end_acyc_id