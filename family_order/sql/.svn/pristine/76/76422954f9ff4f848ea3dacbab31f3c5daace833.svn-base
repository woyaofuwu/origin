SELECT a.user_id para_code1,a.discnt_code para_code2,
to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') para_code3,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') para_code4,
'' para_code5,'' para_code6,'' para_code7,'' para_code8, '' PARA_CODE9 , 
'' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 ,'' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME,'' subsys_code,0 param_attr,'' param_code,b.param_name
  FROM tf_f_user_discnt a,td_s_commpara b
 WHERE b.subsys_code=:SUBSYS_CODE
   AND b.param_attr=:PARAM_ATTR
   AND a.discnt_code=TO_NUMBER(b.param_code)
   AND a.user_id=TO_NUMBER(:PARA_CODE11)
  AND a.partition_id=MOD(TO_NUMBER(:PARA_CODE11),10000)
  AND b.end_date>SYSDATE
  AND a.end_date>=TRUNC(ADD_MONTHS(SYSDATE,-2),'MM')
  AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')