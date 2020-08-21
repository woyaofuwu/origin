UPDATE td_m_cardsaleprice
   SET 
    value_d=TO_NUMBER(:VALUE_D),
    para_type_code=:PARA_TYPE_CODE,
    brand_tag=:BRAND_TAG,
    discount=:DISCOUNT,
    agent_fee=TO_NUMBER(:AGENT_FEE),
    sale_money=TO_NUMBER(:SALE_MONEY),
    old_money=TO_NUMBER(:OLD_MONEY),
    remark=:REMARK,update_time=SYSDATE,
    update_staff_id=:UPDATE_STAFF_ID,
    update_depart_id=:UPDATE_DEPART_ID,
    rsrv_tag1=:RSRV_TAG1,rsrv_tag2=:RSRV_TAG2,rsrv_tag3=:RSRV_TAG3,
    rsrv_date1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS'),
    rsrv_date2=TO_DATE(:RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS'),
    rsrv_date3=TO_DATE(:RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS'),
    rsrv_str1=:RSRV_STR1,rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3,
    rsrv_num1=:RSRV_NUM1,rsrv_num2=:RSRV_NUM2,rsrv_num3=:RSRV_NUM3  
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND card_type_code=:CARD_TYPE_CODE
   AND value_code=:VALUE_CODE
   AND product_id=:PRODUCT_ID
   AND value_u=TO_NUMBER(:VALUE_U)