SELECT card_type_code,decode(card_type_code, '2', '全球通积分实物礼品', '9','全球通积分助学礼品')  
VALUE_PRICE,eparchy_code,city_code,depart_id,to_char(sum(para_value9)) para_value9,to_char(sum(para_value10)) para_value10,to_char(sum(para_value11)) para_value11,to_char(sum(para_value12)) para_value12,
to_char(sum(para_value13)) para_value13,to_char(sum(para_value14)) para_value14,
to_char(sum(para_value15)) para_value15,to_char(sum(para_value16)) para_value16,
to_char(para_value17) para_value17,
to_char(sum(para_value18)) para_value18
 FROM tf_b_resdaystat_log
  WHERE res_type_code=:RES_TYPE_CODE
  AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
  AND (:CITY_CODE is null or city_code=:CITY_CODE)
  AND (:DEPART_ID is null or depart_id=:DEPART_ID)
  AND (:RES_KIND_CODE is null or card_type_code=:RES_KIND_CODE)
  AND  oper_flag='1'--营业厅
  AND stat_type='Y'--货物
  and oper_time >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
  and oper_time <=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
  group by eparchy_code,city_code,depart_id,card_type_code,para_value17