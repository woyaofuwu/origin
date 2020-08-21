INSERT INTO tf_f_recommend_result(execute_id, execute_desc, serial_number, id_type, id, deal_time, deal_tag,
                                  deal_staff_id, deal_depart_id, deal_eparchy_code, rsrv_str1, rsrv_str2)
SELECT :EXECUTE_ID, '新品牌推荐', :SERIAL_NUMBER, '0', '03', sysdate,
           :DEAL_TAG, :DEAL_STAFF_ID, :DEAL_DEPART_ID, :DEAL_EPARCHY_CODE, :RSRV_STR1, :RSRV_STR2
  FROM dual