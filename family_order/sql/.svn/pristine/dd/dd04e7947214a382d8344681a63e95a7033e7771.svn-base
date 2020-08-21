SELECT COUNT(1) recordcount FROM td_s_commpara a
WHERE subsys_code='CSM' AND param_attr=334 AND param_code='3'
AND para_code1=:PRODUCT_ID AND (SYSDATE BETWEEN start_date AND end_date)
AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ') AND EXISTS
(SELECT 1 FROM tf_f_user_mbmp_sub
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND biz_type_code=a.para_code4
   AND sp_id=a.para_code5
   AND sp_svc_id=a.para_code6
   AND biz_state_code='A'
   AND SYSDATE BETWEEN start_date AND end_date)