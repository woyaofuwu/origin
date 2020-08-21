select eparchy_code,card_type_code,rsrv_tag1,sum(a) para_value9 ,sum(b+d)  para_value10 ,
sum(c) para_value11
from
(
--本期增加（调拨到业务区）
SELECT eparchy_code,card_type_code,rsrv_tag1,sum(para_value9) a,0 b, 0 c,0 d 
  FROM tf_b_resdaystat_log
 WHERE res_type_code||''=:RES_TYPE_CODE
   AND eparchy_code=:EPARCHY_CODE
   AND city_code=:CITY_CODE
   AND stat_type=:STAT_TYPE
   AND (:VALUE_CODE IS NULL OR rsrv_tag1=:VALUE_CODE)
   AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
   AND RDVALUE1 >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
   AND RDVALUE1 <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
   AND para_value1='EASSIGNTOC' 
   group by eparchy_code,card_type_code,rsrv_tag1
--本期减少（业务区返销到地市）
union all
SELECT eparchy_code,card_type_code,rsrv_tag1,0 a,sum(para_value9) b,0 c,0 d 
  FROM tf_b_resdaystat_log
 WHERE res_type_code||''=:RES_TYPE_CODE
   AND eparchy_code=:EPARCHY_CODE
   AND city_code=:CITY_CODE
   AND stat_type=:STAT_TYPE
   AND (:VALUE_CODE IS NULL OR rsrv_tag1=:VALUE_CODE)
   AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
   AND RDVALUE1 >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
   AND RDVALUE1 <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
   AND para_value1='CBACKTOE' 
   group by eparchy_code,card_type_code,rsrv_tag1
--本期减少（业务区销售）
union all
SELECT eparchy_code,card_type_code,rsrv_tag1,0 a,0 b,0 c,sum(para_value9) d 
  FROM tf_b_resdaystat_log
 WHERE res_type_code||''=:RES_TYPE_CODE
   AND eparchy_code=:EPARCHY_CODE
   AND city_code=:CITY_CODE
   AND stat_type=:STAT_TYPE
   AND (:VALUE_CODE IS NULL OR rsrv_tag1=:VALUE_CODE)
   AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
   AND RDVALUE1 >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
   AND RDVALUE1 <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
   AND para_value1='OUTSTORE' 
   group by eparchy_code,card_type_code,rsrv_tag1
union all
--期末
SELECT eparchy_code,card_type_code,rsrv_tag1,0 a,0 b ,sum(para_value9) c ,0 d
  FROM tf_b_resdaystat_log
 WHERE res_type_code||''=:RES_TYPE_CODE
   AND eparchy_code=:EPARCHY_CODE
   AND city_code=:CITY_CODE
   AND stat_type='U'
   AND oper_flag='C'
   AND (:VALUE_CODE IS NULL OR rsrv_tag1=:VALUE_CODE)
   AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
   AND RDVALUE1 >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
   AND RDVALUE1 <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
   AND para_value1='AUTOSTAT' 
   AND rsrv_tag2='1'
   group by eparchy_code,card_type_code,rsrv_tag1
)
group by eparchy_code,card_type_code,rsrv_tag1