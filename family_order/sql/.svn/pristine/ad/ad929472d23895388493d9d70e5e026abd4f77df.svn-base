SELECT trade_id,trade_type_code,in_mode_code,res_code,cust_name,pspt_id,process_tag,trade_staff_id,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,trade_eparchy_code,trade_city_code,trade_depart_id,to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,to_char(valid_date,'yyyy-mm-dd hh24:mi:ss') valid_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_str11,rsrv_str12,rsrv_str13,rsrv_str14,rsrv_str15,to_char(rsrv_date,'yyyy-mm-dd hh24:mi:ss') rsrv_date,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,to_char(rsrv_date4,'yyyy-mm-dd hh24:mi:ss') rsrv_date4,to_char(rsrv_date5,'yyyy-mm-dd hh24:mi:ss') rsrv_date5,
  rsrv_str16,rsrv_str17,rsrv_str18,rsrv_str19,rsrv_str20,rsrv_str21,rsrv_str22,rsrv_str23,rsrv_str24,rsrv_str25,rsrv_str26,rsrv_str27,rsrv_str28,rsrv_str29,rsrv_str30
  FROM tf_b_tradebook
     WHERE (trade_id=:TRADE_ID or :TRADE_ID is null )
   AND (trade_type_code=:TRADE_TYPE_CODE or :TRADE_TYPE_CODE is null)
   AND (in_mode_code=:IN_MODE_CODE or :IN_MODE_CODE is null)
   AND (res_code=:RES_CODE  or :RES_CODE is null)
   AND (pspt_id=:PSPT_ID or :PSPT_ID is null)
   AND (process_tag=:PROCESS_TAG or :PROCESS_TAG is null)
   AND (trade_eparchy_code=:TRADE_EPARCHY_CODE  or :TRADE_EPARCHY_CODE is null)
   AND (trade_city_code=:TRADE_CITY_CODE or :TRADE_CITY_CODE is null)
   AND (trade_staff_id=:TRADE_STAFF_ID or :TRADE_STAFF_ID is null)
   AND (accept_date>=TO_DATE(:START_DATE, 'YYYY-MM-DD') or :START_DATE is null)
   AND (accept_date>=TO_DATE(:END_DATE, 'YYYY-MM-DD') or :END_DATE is null)
   AND (rsrv_str15=:RSRV_STR15 or :RSRV_STR15 is null)
   AND (rsrv_str19=:RSRV_STR19 or :RSRV_STR19 is null)