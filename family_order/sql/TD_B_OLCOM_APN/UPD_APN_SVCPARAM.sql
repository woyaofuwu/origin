UPDATE TD_B_OLCOM_APN
   SET para_type_code = :PARA_TYPE_CODE, para_code1 = :PARA_CODE1, para_code2 = :PARA_CODE2, rsrv_tag1 = :RSRV_TAG1,
       open_olcom_serv_code = :OPEN_OLCOM_SERV_CODE, close_olcom_serv_code = :CLOSE_OLCOM_SERV_CODE,
       serv_order = :SERV_ORDER, order_no = :ORDER_NO,
       start_date = TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'), end_date = TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),
       eparchy_code = :EPARCHY_CODE, remark = :REMARK, update_staff_id = :UPDATE_STAFF_ID, update_depart_id = :UPDATE_DEPART_ID,
       update_time = TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS')
 WHERE para_code = TO_NUMBER(:PARA_CODE)
   AND switch_type_code = :SWITCH_TYPE_CODE