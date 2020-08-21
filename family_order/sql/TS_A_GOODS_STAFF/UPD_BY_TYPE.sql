UPDATE ts_a_goods_staff
   SET total_num=TO_NUMBER(:TOTAL_NUM),oper_staff_id=:OPER_STAFF_ID,oper_time=sysdate,oper_depart_id=:OPER_DEPART_ID,remark=:REMARK  
 WHERE staff_id=:STAFF_ID
   AND rsrv_str2=:RES_KIND_CODE
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND in_date=to_char(sysdate,'yyyymmdd')