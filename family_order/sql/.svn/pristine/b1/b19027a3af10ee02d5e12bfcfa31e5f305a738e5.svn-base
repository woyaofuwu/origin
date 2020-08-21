--IS_CACHE=Y
SELECT /*+INDEX(a PK_TD_M_CARDSALEPRICE)*/ eparchy_code,res_type_code,card_type_code,product_id,to_char(value_u) value_u,to_char(value_d) value_d,para_type_code,brand_tag,value_code,discount,to_char(agent_fee) agent_fee,to_char(sale_money) sale_money,to_char(old_money) old_money,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_num1,rsrv_num2,rsrv_num3,0 x_tag 
  FROM td_m_cardsaleprice a
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND ((:CARD_TYPE_CODE IS NOT NULL AND card_type_code=:CARD_TYPE_CODE) OR :CARD_TYPE_CODE IS NULL) 
   AND ((:VALUE_U IS NOT NULL AND value_u=TO_NUMBER(:VALUE_U)) OR :VALUE_U IS NULL)
   AND ((:VALUE_CODE IS NOT NULL AND value_code=:VALUE_CODE) OR :VALUE_CODE IS NULL)
   AND ((:PRODUCT_ID != -1 AND product_id=:PRODUCT_ID) OR :PRODUCT_ID = -1)