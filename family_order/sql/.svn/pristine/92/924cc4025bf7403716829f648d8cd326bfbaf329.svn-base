SELECT TRUNC((SUM(sale_money)/SUM(SALE_NUM))* :CANSALE_NUM) sale_money,
       TRUNC((SUM(advance_pay)/SUM(SALE_NUM))* :CANSALE_NUM) advance_pay,
       TRUNC((SUM(old_money)/SUM(SALE_NUM))* :CANSALE_NUM) old_money,
       TRUNC((SUM(agent_fee)/SUM(SALE_NUM))* :CANSALE_NUM) agent_fee,discount
  FROM tf_b_cardsale_log a
 WHERE sub_log_id=TO_NUMBER(:LOG_ID)
   AND eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND (:RES_KIND_CODE IS NULL OR (:RES_KIND_CODE IS NOT NULL AND res_kind_code=:RES_KIND_CODE))
   AND sale_type_code=:SALE_TYPE_CODE
   AND start_value<=:RES_NO_S
   AND end_value>=:RES_NO_E
   AND (:STOCK_ID IS NULL OR  (:STOCK_ID IS NOT NULL AND stock_id=:STOCK_ID))
   AND exists (SELECT 1
                 FROM tf_r_valuecard b
                WHERE value_card_no>=:RES_NO_S
                  AND value_card_no<=:RES_NO_E
                  AND (card_state_code||''=:CARD_STATE_CODE OR card_state_code||''='3') 
                  AND sale_tag||''=:SALE_TAG
                  AND fee_tag||''=:FEE_TAG
                  AND eparchy_code=:EPARCHY_CODE
                  AND TRUNC(end_date)>SYSDATE
                  AND (:STOCK_ID_O IS NULL OR (:STOCK_ID_O IS NOT NULL AND stock_id_o=:STOCK_ID_O))
                  AND a.sub_log_id=b.sale_log_id||'')
GROUP BY discount