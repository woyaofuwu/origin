UPDATE TI_B_DM_BUSI
   SET cancel_tag=:CANCEL_TAG,cancel_time=sysdate,cancel_eparchy_code=:CANCEL_EPARCHY_CODE,cancel_city_code=:CANCEL_CITY_CODE,cancel_depart_id=:CANCEL_DEPART_ID,
   cancel_staff_id=:CANCEL_STAFF_ID,rsrv_str1=:RSRV_STR1,rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3,cancel_accountnum=:CANCEL_ACCOUNTNUM 
 WHERE operateid=:OPERATEID