--IS_CACHE=Y
select a.fee_type_code,a.fee_type,a.rsrv_str1 RATE,a.rsrv_str2 TYPE
 from td_b_feeregtype a
 where 1=1
 and sysdate between a.start_date and a.end_date
 and a.fee_type_code in('T2','T3','T4')
 order by a.fee_type_code
