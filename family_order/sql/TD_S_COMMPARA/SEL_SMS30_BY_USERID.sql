select '' SUBSYS_CODE, 0 PARAM_ATTR, '' PARAM_CODE, '' PARAM_NAME,recv_object para_code1, 
'' para_code2, '' para_code3, '' para_code4, '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' PARA_CODE9 , 
'' PARA_CODE10 ,'' PARA_CODE11 ,'' PARA_CODE12 , '' PARA_CODE13 ,
'' PARA_CODE14 , '' PARA_CODE15 ,
'' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 ,
'' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 ,
'' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 ,
'' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , 
'' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , 
'' UPDATE_DEPART_ID , '' UPDATE_TIME
from ti_o_sms where id = to_number(:PARA_CODE1)
AND sms_type_code='30'
AND to_char(refer_time,'yyyymm') = to_char(SYSDATE,'yyyymm')
union all
select '' SUBSYS_CODE, 0 PARAM_ATTR, '' PARAM_CODE, '' PARAM_NAME,recv_object para_code1, 
'' para_code2, '' para_code3, '' para_code4, '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' PARA_CODE9 , 
'' PARA_CODE10 ,'' PARA_CODE11 ,'' PARA_CODE12 , '' PARA_CODE13 ,
'' PARA_CODE14 , '' PARA_CODE15 ,
'' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 ,
'' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 ,
'' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 ,
'' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , 
'' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , 
'' UPDATE_DEPART_ID , '' UPDATE_TIME
from ti_oh_sms where id = to_number(:PARA_CODE1)
AND sms_type_code='30'
AND to_char(refer_time,'yyyymm') = to_char(SYSDATE,'yyyymm')