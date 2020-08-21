SELECT c.SUBSYS_CODE ,c.PARAM_ATTR ,c.PARAM_CODE ,c.PARAM_NAME ,c.para_code1,d.para_code2,c.para_code3,c.para_code4,c.para_code5,c.para_code6,c.para_code7,c.para_code8,c.PARA_CODE9 ,c.PARA_CODE10 ,c.PARA_CODE11 ,c.PARA_CODE12 ,c.PARA_CODE13 ,c.PARA_CODE14 ,c.PARA_CODE15 ,c.PARA_CODE16 ,c.PARA_CODE17 ,c.PARA_CODE18 ,c.PARA_CODE19 ,c.PARA_CODE20 ,c.PARA_CODE21 ,c.PARA_CODE22 ,c.PARA_CODE23 ,c.PARA_CODE24 ,c.PARA_CODE25 ,c.PARA_CODE26 ,c.PARA_CODE27 ,c.PARA_CODE28 ,c.PARA_CODE29 ,c.PARA_CODE30 ,c.START_DATE ,c.END_DATE ,c.EPARCHY_CODE ,d.REMARK ,c.UPDATE_STAFF_ID ,c.UPDATE_DEPART_ID ,c.UPDATE_TIME
 FROM Td_s_Commpara c,
(select '' SUBSYS_CODE , 0 PARAM_ATTR , '' PARAM_CODE , '' PARAM_NAME , '' para_code1, rsrv_str1 para_code2, '' para_code3, '' para_code4, '' para_code5, '' para_code6, '' para_code7, '' para_code8, '' PARA_CODE9 , '' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , rsrv_str2 REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME
  FROM (SELECT a.rsrv_str1,a.rsrv_str2
         FROM tf_f_user_otherserv a
         WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
           AND user_id = TO_NUMBER(:USER_ID)
           AND TRIM(service_mode) = '8'
           AND TRIM(process_tag) = 'i'
           AND SYSDATE < end_date
          AND NOT EXISTS(SELECT 1 FROM tf_f_user_otherserv
                           WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                             AND user_id = TO_NUMBER(:USER_ID)
                             AND TRIM(service_mode) = '8'
                             AND rsrv_str7 = trim(a.rsrv_str1)
                             AND rsrv_str8 = '001'
                             AND process_tag = '0'
                             AND SYSDATE < end_date
                             AND SYSDATE < start_date+45
                             )
           AND NOT EXISTS(SELECT 1 FROM tf_f_user_otherserv
                           WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                             AND user_id = TO_NUMBER(:USER_ID)
                             AND TRIM(service_mode) = '8'
                             AND rsrv_str7 = trim(a.rsrv_str1)
                             AND rsrv_str8 = '003'
                             AND process_tag = '0'
                             AND SYSDATE < end_date
                             AND SYSDATE < start_date+90)
                            
         group by a.rsrv_str1,a.rsrv_str2 )) d
WHERE c.subsys_code = 'CSM' AND c.param_attr = '1201'
      AND c.para_code1 = d.para_code2 
      AND c.end_date  > SYSDATE