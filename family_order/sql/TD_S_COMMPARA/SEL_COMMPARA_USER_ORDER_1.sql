SELECT SUBSYS_CODE,PARAM_ATTR,PARAM_CODE,PARAM_NAME,PARA_CODE1,PARA_CODE2,PARA_CODE3,PARA_CODE4,PARA_CODE5,PARA_CODE6,PARA_CODE7,PARA_CODE8,PARA_CODE9,PARA_CODE10,PARA_CODE11,PARA_CODE12,PARA_CODE13,PARA_CODE14,PARA_CODE15,PARA_CODE16,PARA_CODE17,PARA_CODE18,PARA_CODE19,PARA_CODE20,PARA_CODE21,PARA_CODE22,PARA_CODE23,PARA_CODE24,PARA_CODE25,to_char(PARA_CODE26,'yyyy-mm-dd hh24:mi:ss') PARA_CODE26,to_char(PARA_CODE27,'yyyy-mm-dd hh24:mi:ss') PARA_CODE27,to_char(PARA_CODE28,'yyyy-mm-dd hh24:mi:ss') PARA_CODE28,to_char(PARA_CODE29,'yyyy-mm-dd hh24:mi:ss') PARA_CODE29,to_char(PARA_CODE30,'yyyy-mm-dd hh24:mi:ss') PARA_CODE30,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,EPARCHY_CODE,REMARK,UPDATE_STAFF_ID,UPDATE_DEPART_ID,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  FROM TD_S_COMMPARA a
WHERE subsys_code=:SUBSYS_CODE
 AND param_attr = :PARAM_ATTR
 AND (param_code = to_char(:PARAM_CODE) or param_code = '9999')
 AND para_code3 = :PARA_CODE3
 AND (para_code4 = :PARA_CODE4 or para_code4 = '-1')
 AND (para_code5 = :PARA_CODE5 or para_code5 = 'ZZZZ' )
 AND para_code6 = :PARA_CODE6
 AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
 AND sysdate BETWEEN start_date AND end_date
 and exists (select 1 
                    from tf_f_user_plat_order b 
                   where b.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                     and b.user_id=:USER_ID 
                     and b.sp_code = a.para_code2 
                     and b.biz_code = a.para_code1 
                      and sysdate between b.start_date and b.end_date)