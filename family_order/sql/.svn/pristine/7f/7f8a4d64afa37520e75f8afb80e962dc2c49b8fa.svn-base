INSERT INTO TD_B_OLCOM_APN(para_code, para_type_code, switch_type_code, para_code1, para_code2, rsrv_tag1,
                           open_olcom_serv_code, close_olcom_serv_code, serv_order, order_no, start_date, end_date,
                           eparchy_code, remark, update_staff_id, update_depart_id, update_time)
                    VALUES(:PARA_CODE, :PARA_TYPE_CODE, :SWITCH_TYPE_CODE, :PARA_CODE1, :PARA_CODE2, :RSRV_TAG1,  
                           :OPEN_OLCOM_SERV_CODE, :CLOSE_OLCOM_SERV_CODE, :SERV_ORDER, :ORDER_NO,
                           TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'), TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),
                           :EPARCHY_CODE, :REMARK, :UPDATE_STAFF_ID, :UPDATE_DEPART_ID, TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'))