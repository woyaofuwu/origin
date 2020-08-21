UPDATE td_s_commpara
   SET  param_name=:PARAM_NAME,para_code1=:PARA_CODE1,para_code2=:PARA_CODE2,para_code3=:PARA_CODE3,para_code4=:PARA_CODE4,para_code5=:PARA_CODE5,para_code6=:PARA_CODE6,para_code7=:PARA_CODE7,para_code8=:PARA_CODE8,para_code9=:PARA_CODE9,para_code10=:PARA_CODE10,para_code11=:PARA_CODE11,para_code12=:PARA_CODE12,para_code13=:PARA_CODE13,
para_code14=:PARA_CODE14,para_code15=:PARA_CODE15,para_code16=:PARA_CODE16,para_code17=:PARA_CODE17,
para_code18=:PARA_CODE18,para_code19=:PARA_CODE19,para_code20=:PARA_CODE20,para_code21=:PARA_CODE21,
para_code22=:PARA_CODE22,para_code23=:PARA_CODE23,para_code24=:PARA_CODE24,para_code25=:PARA_CODE25,
start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),remark=:REMARK,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate
 WHERE subsys_code=:SUBSYS_CODE
   AND param_attr=:PARAM_ATTR
   AND param_code=:PARAM_CODE
   AND eparchy_code=:EPARCHY_CODE