--IS_CACHE=Y
select a.fee_type_code,a.fee_type,a.parent_fee_type_code,b.fee_type as parent_fee_type,
a.rsrv_str1 RATE,a.rsrv_str2 TYPE
 from td_b_feeregtype a,td_b_feeregtype b
 where a.parent_fee_type_code = b.fee_type_code
 and a.fee_type_level='2'
 and b.fee_type_level='1'
 and sysdate between a.start_date and a.end_date
 and sysdate between b.start_date and b.end_date