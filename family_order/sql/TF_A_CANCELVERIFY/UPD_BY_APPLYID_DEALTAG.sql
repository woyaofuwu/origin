UPDATE tf_a_cancelverify
   SET chk_time=TO_DATE(:CHK_TIME, 'YYYY-MM-DD HH24:MI:SS'),chk_eparchy_code=:CHK_EPARCHY_CODE,chk_city_code=:CHK_CITY_CODE,chk_depart_id=:CHK_DEPART_ID,chk_staff_id=:CHK_STAFF_ID,chk_tag=:CHK_TAG,deal_tag='1',chk_remark=:CHK_REMARK  
 WHERE apply_id=TO_NUMBER(:APPLY_ID)
   AND deal_tag=:DEAL_TAG