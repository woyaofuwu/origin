--IS_CACHE=Y
SELECT para_code, para_type_code, switch_type_code, para_code1, para_code2, rsrv_tag1,
       open_olcom_serv_code, close_olcom_serv_code, serv_order, order_no, 
       start_date, end_date, eparchy_code, remark,
       update_staff_id, update_depart_id, update_time
 FROM TD_B_OLCOM_APN
WHERE (para_code = :PARA_CODE or :PARA_CODE = '0')
  AND (switch_type_code = :SWITCH_TYPE_CODE or :SWITCH_TYPE_CODE IS NULL)
  AND (rsrv_tag1 = :RSRV_TAG1 or :RSRV_TAG1 IS NULL)
ORDER BY para_code, switch_type_code