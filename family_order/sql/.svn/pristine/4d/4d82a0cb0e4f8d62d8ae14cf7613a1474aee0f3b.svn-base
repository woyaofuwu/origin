select card_type_code,Rsrv_Tag1,DECODE(Rsrv_Tag2,'1','库存统计','0','销售统计','') Rsrv_Tag2,Rsrv_Tag3,TO_NUMBER(para_value3)/100 para_value3,sum(para_value1) para_value1,sum(TO_NUMBER(para_value2)/100)  para_value2
from(
select a.card_type_code card_type_code,a.Rsrv_Tag1 Rsrv_Tag1,a.Rsrv_Tag2 Rsrv_Tag2,a.Rsrv_Tag3 Rsrv_Tag3,b.sale_money para_value3,para_value1,para_value2
 from tf_b_resdaystat_log a,td_m_cardsaleprice b
where a.card_type_code=b.card_type_code and a.rsrv_tag1=b.value_code
 and a.eparchy_code=:EPARCHY_CODE and b.eparchy_code=:EPARCHY_CODE
 and a.res_type_code='1' and b.res_type_code='1'
 and a.rsrv_tag2='0'
 and (:STOCK_ID is null or a.depart_id=:STOCK_ID)
 and (:CITY_CODE is null or a.city_code=:CITY_CODE)
 and a.oper_time >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
 and a.oper_time <=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
union all
select a.card_type_code card_type_code,a.Rsrv_Tag1 Rsrv_Tag1,a.Rsrv_Tag2 Rsrv_Tag2,a.Rsrv_Tag3 Rsrv_Tag3,b.sale_money para_value3,para_value1,para_value2
 from tf_b_resdaystat_log a,td_m_cardsaleprice b
where a.card_type_code=b.card_type_code and a.rsrv_tag1=b.value_code
 and a.eparchy_code=:EPARCHY_CODE and b.eparchy_code=:EPARCHY_CODE
 and a.res_type_code='1' and b.res_type_code='1'
 and a.rsrv_tag2='1'
 and (:STOCK_ID is null or a.depart_id=:STOCK_ID)
 and (:CITY_CODE is null or a.city_code=:CITY_CODE)
 and a.oper_time >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
 and a.oper_time <=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
)
group by card_type_code,Rsrv_Tag1,Rsrv_Tag2,Rsrv_Tag3,para_value3