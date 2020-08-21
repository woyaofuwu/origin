UPDATE tf_f_specific_discnt
   SET end_acyc_id=:END_ACYC_ID,rsrv_num1=:RSRV_NUM1,rsrv_str2=:RSRV_STR2,remark=:REMARK,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID  
 WHERE id=TO_NUMBER(:ID)
   AND id_type_code=:ID_TYPE_CODE
   AND disn_type_code=:DISN_TYPE_CODE
   AND SYSDATE BETWEEN TO_DATE(rsrv_str1,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(rsrv_str2,'YYYY-MM-DD HH24:MI:SS')
   AND rsrv_num5=0