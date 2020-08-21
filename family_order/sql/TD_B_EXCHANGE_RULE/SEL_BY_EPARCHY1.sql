--IS_CACHE=Y
select a.amonths,a.brand_code,a.class_limit,a.deposit_code,a.end_date,a.eparchy_code,a.exchange_limit,a.exchange_mode,a.exchange_type_code,a.fmonths,a.gift_type_code,a.money_rate,a.remark,a.reward_limit,a.right_code,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rule_id,a.rule_name,a.score,a.score_num,a.score_type_code,a.start_date,a.status,a.unit,a.update_depart_id,a.update_staff_id,a.update_time
from  TD_B_EXCHANGE_RULE A
WHERE A.STATUS = '0'
and a.eparchy_code = :EPARCHY_CODE