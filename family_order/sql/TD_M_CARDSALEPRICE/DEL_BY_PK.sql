DELETE FROM td_m_cardsaleprice
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND card_type_code=:CARD_TYPE_CODE
   AND value_u=TO_NUMBER(:VALUE_U)
   AND value_code=:VALUE_CODE
   AND product_id=:PRODUCT_ID