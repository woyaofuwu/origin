INSERT INTO tf_f_user_trans(serial_number,user_id,para_code,rpay_deposit,trans_fee,rpay_deposit_code,deposit_code,rtn_months,left_months,left_deposit,in_date,gpay_deposit,mgift_fee,gpay_deposit_code,gift_deposit_code,gtotal_months,gleft_months,left_gdeposit,process_tag,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,start_date,end_date,cancel_time,cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id,rsrv_info1,rsrv_info2,rsrv_num1,rsrv_num2,rsrv_date1,rsrv_date2)
 VALUES(:SERIAL_NUMBER,TO_NUMBER(:USER_ID),:PARA_CODE,TO_NUMBER(:RPAY_DEPOSIT),TO_NUMBER(:TRANS_FEE),:RPAY_DEPOSIT_CODE,:DEPOSIT_CODE,:RTN_MONTHS,:LEFT_MONTHS,TO_NUMBER(:LEFT_DEPOSIT),sysdate,TO_NUMBER(:GPAY_DEPOSIT),TO_NUMBER(:MGIFT_FEE),:GPAY_DEPOSIT_CODE,:GIFT_DEPOSIT_CODE,:GTOTAL_MONTHS,:GLEFT_MONTHS,TO_NUMBER(:LEFT_GDEPOSIT),:PROCESS_TAG,:RECV_EPARCHY_CODE,:RECV_CITY_CODE,:RECV_DEPART_ID,:RECV_STAFF_ID,add_months(to_date(to_char(SYSDATE,'YYYY-MM'),'yyyy-mm'),1),add_months(to_date(to_char(SYSDATE,'YYYY-MM'),'yyyy-mm'),(:RTN_MONTHS+:GTOTAL_MONTHS)+1)-1,TO_DATE(:CANCEL_TIME,'YYYY-MM-DD HH24:MI:SS'),:CANCEL_EPARCHY_CODE,:CANCEL_CITY_CODE,:CANCEL_DEPART_ID,:CANCEL_STAFF_ID,:RSRV_INFO1,:RSRV_INFO2,TO_NUMBER(:RSRV_NUM1),TO_NUMBER(:RSRV_NUM2),TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS'))