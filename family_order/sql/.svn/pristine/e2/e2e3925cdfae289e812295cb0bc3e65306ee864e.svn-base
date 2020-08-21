--IS_CACHE=Y
SELECT bcyc_id,
       to_char(acyc_start_time, 'yyyy-mm-dd hh24:mi:ss') acyc_start_time,
       to_char(acyc_end_time, 'yyyy-mm-dd hh24:mi:ss') acyc_end_time,
       use_tag,
       (trunc(nlate_fee_time1) - to_date('19000101', 'yyyymmdd')) ilatefeetime1,
       (trunc(nlate_fee_time2) - to_date('19000101', 'yyyymmdd')) ilatefeetime2,
       late_fee_ratio1,
       late_fee_ratio2
  FROM td_a_acycpara
 WHERE acyc_id = :ACYC_ID