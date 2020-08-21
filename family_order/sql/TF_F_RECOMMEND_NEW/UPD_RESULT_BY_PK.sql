UPDATE tf_f_recommend_new a
   SET a.trade_id = :TRADE_ID,
       a.deal_time = TO_DATE(:DEAL_TIME,'yyyy-mm-dd hh24:mi:ss'),
       a.deal_tag = :DEAL_TAG,
       a.deal_staff_id = :DEAL_STAFF_ID,
       a.deal_depart_id = :DEAL_DEPART_ID,
       a.deal_eparchy_code = :DEAL_EPARCHY_CODE,
       a.start_date = (
                        SELECT TRUNC(SYSDATE)+MAX(days)
                          FROM (
                                  SELECT DECODE( :DEAL_TAG,
                                                 '0',TO_NUMBER(NVL(para_code5,'1000')),
                                                 '1',TO_NUMBER(NVL(para_code7,'0')),
                                                 '2',TO_NUMBER(NVL(para_code9,'1000')),
                                                 0
                                         ) days
                                    FROM ucr_cen1.td_s_commpara
                                   WHERE subsys_code = 'CSM'
                                     AND param_attr = '9996'
                                     AND param_code = '1'
                                     AND para_code1 = :CHANNEL_CODE
                                  UNION ALL
                                  SELECT 0 days FROM dual
                               )
                      )
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.channel_code = :CHANNEL_CODE
   AND a.execute_id = :EXECUTE_ID