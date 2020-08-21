INSERT INTO tf_f_recommend_result(execute_id, execute_desc, serial_number, id_type, id, deal_time, deal_tag,
                                  deal_staff_id, deal_depart_id, deal_eparchy_code, rsrv_str1, rsrv_str2)
SELECT execute_id, execute_desc, serial_number, id_type, id, to_date(:DEAL_TIME,'yyyy-mm-dd hh24:mi:ss'),
           :DEAL_TAG, :DEAL_STAFF_ID, :DEAL_DEPART_ID, :DEAL_EPARCHY_CODE, :RSRV_STR1, :RSRV_STR2
  FROM tf_f_recommend
 WHERE execute_id = :EXECUTE_ID
   AND serial_number = :SERIAL_NUMBER