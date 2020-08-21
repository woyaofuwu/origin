DELETE FROM ts_a_goods_staff
 WHERE staff_id=:STAFF_ID
   AND rsrv_str2=:RES_KIND_CODE
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND in_date=to_char(sysdate,'yyyymmdd')