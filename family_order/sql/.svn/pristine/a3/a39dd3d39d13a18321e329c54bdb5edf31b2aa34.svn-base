SELECT  execute_id, execute_desc, serial_number, id_type, id,
        to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time, deal_tag, 
        deal_staff_id, deal_depart_id, deal_eparchy_code, rsrv_str1, rsrv_str2
  FROM  tf_f_recommend_result
 WHERE  serial_number=:SERIAL_NUMBER
   --AND  (instr(:DEAL_TAG,deal_tag)>0 OR :DEAL_TAG IS NULL)