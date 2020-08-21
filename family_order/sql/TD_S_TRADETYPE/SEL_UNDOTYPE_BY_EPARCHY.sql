--IS_CACHE=Y
SELECT trade_type_code,
       trade_type,
       eparchy_code,
       bpm_code,
       priority,
       trade_attr,
       back_tag,
       judge_owe_tag,
       extend_tag,
       prt_tradeff_tag,
       prt_traderr_tag,
       info_tag_set,
       intf_tag_set,
       identity_check_tag,
       res_release_tag,
       preopen_limit_tag,
       recomp_rent_tag,
       cancel_sp_tag,
       rtn_deposit_tag,
       rtn_foregift_tag,
       audit_tag,
       credit_trade_tag,
       credit_trade_detail_tag,
       credit_judge_tag,
       credit_yw_tag,
       credit_limit_tag,
       credit_limit_hour,
       credit_limit_day,
       tag_set,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       remark,
       update_staff_id,
       update_depart_id,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_tradetype
 WHERE eparchy_code = :EPARCHY_CODE
   AND SYSDATE BETWEEN start_date AND end_date
   AND back_tag <> '0'
   AND TRADE_TYPE_CODE <> 500 --排除批量的
   AND (NET_TYPE_CODE <> '18' OR NET_TYPE_CODE is null)
 ORDER BY trade_type_code