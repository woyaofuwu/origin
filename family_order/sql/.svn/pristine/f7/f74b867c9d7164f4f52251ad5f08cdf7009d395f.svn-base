--IS_CACHE=Y
SELECT action_code,exchange_type_code,action_name,score,reward_limit,action_limit,discnt_code,deposit_code,fmonths,amonths,fenabled_tag,aenabled_tag,class_limit,brand_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,action_nv1,action_nv2,action_nv3,action_cv1,action_cv2,action_cv3,action_cv4,action_cv5,to_char(action_d1,'yyyy-mm-dd hh24:mi:ss') action_d1,to_char(action_d2,'yyyy-mm-dd hh24:mi:ss') action_d2,to_char(action_d3,'yyyy-mm-dd hh24:mi:ss') action_d3,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time ,right_code
  FROM td_b_score_action
 WHERE (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
       AND sysdate BETWEEN start_date AND end_date
       AND (:TRADE_STAFF_ID='SUPERUSR' or right_code is NULL OR right_code in(
                                           SELECT data_code
                                             FROM tf_m_staffdataright
                                            WHERE staff_id=:TRADE_STAFF_ID
                                              AND data_type ='1' AND right_attr='0' AND right_tag='1' ))