--IS_CACHE=Y
SELECT rule_id,
       rule_name,
       gift_type_code,
       score,
       score_type_code,
       exchange_type_code,
       exchange_mode,
       score_num,
       unit,
       exchange_limit,
       reward_limit,
       money_rate,
       fmonths,
       deposit_code,
       amonths,
       class_limit,
       eparchy_code,
       brand_code,
       right_code,
       start_date,
       end_date,
       status,
       remark,
       update_time,
       update_depart_id,
       update_staff_id,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       rsrv_str6,
       rsrv_str7,
       rsrv_str8,
       rsrv_str9,
       rsrv_str10
  FROM td_b_exchange_rule
 WHERE (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
   AND sysdate BETWEEN start_date AND end_date
   AND (:TRADE_STAFF_ID = 'SUPERUSR' or right_code is NULL OR
       right_code in (SELECT data_code
                         FROM tf_m_staffdataright
                        WHERE staff_id = :TRADE_STAFF_ID
                          AND data_type = '1'
                          AND right_attr = '0'
                          AND right_tag = '1'))