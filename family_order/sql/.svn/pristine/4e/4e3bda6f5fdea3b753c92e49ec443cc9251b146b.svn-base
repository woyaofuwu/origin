SELECT pspt_type_code,pspt_id,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,eparchy_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(in_date,'yyyy-mm-dd hh24:mi:ss') in_date,staff_id,depart_id,remark 
  FROM tf_f_whiteuser
 WHERE (pspt_type_code=:PSPT_TYPE_CODE or :PSPT_TYPE_CODE is NULL)
   AND pspt_id=:PSPT_ID 
   AND (eparchy_code=:EPARCHY_CODE or :EPARCHY_CODE is NULL)
   AND end_date>= SYSDATE