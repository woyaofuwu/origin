SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,para_code6,para_code7,para_code8,para_code9,para_code10,para_code11,para_code12,para_code13,para_code14,para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara a
 WHERE (subsys_code = :SUBSYS_CODE
   AND param_attr = :PARAM_ATTR
   AND param_code = :PARAM_CODE
   AND para_code1 = :PARA_CODE1
   AND sysdate BETWEEN start_date AND end_date)
   AND (eparchy_code=:EPARCHY_CODE
   OR eparchy_code='ZZZZ') 
   AND NOT EXISTS(SELECT 1 FROM tf_f_user_svc
                   WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                     AND user_id = TO_NUMBER(:USER_ID)
                     AND service_id+0 = to_number(a.para_code2)
                     AND SYSDATE < end_date)
   AND EXISTS(SELECT 1 FROM td_b_product_svc
               WHERE product_id = :PRODUCT_ID
                 AND service_id = to_number(a.para_code2)
                 AND SYSDATE < end_date)
   AND NOT EXISTS(SELECT 1 FROM tf_f_user_otherserv
                   WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                     AND user_id = TO_NUMBER(:USER_ID)
                     AND TRIM(service_mode) = '8'
                     AND rsrv_str1 = a.para_code2
                     AND rsrv_str2 = '002'
                     AND process_tag = '0'
                     AND SYSDATE < end_date
                     AND SYSDATE < start_date+30)
   AND NOT EXISTS(SELECT 1 FROM tf_f_user_otherserv
                   WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                     AND user_id = TO_NUMBER(:USER_ID)
                     AND TRIM(service_mode) = '8'
                     AND rsrv_str1 = a.para_code2
                     AND rsrv_str2 = '003'
                     AND process_tag = '0'
                     AND SYSDATE < end_date
                     AND SYSDATE < start_date+90)
   AND NOT EXISTS(SELECT 1 FROM tf_f_user_otherserv
                   WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                     AND user_id = TO_NUMBER(:USER_ID)
                     AND TRIM(service_mode) = '8'
                     AND rsrv_str1 = a.para_code2
                     AND rsrv_str2 = '001'
                     AND process_tag = '0'
                     AND SYSDATE < end_date)