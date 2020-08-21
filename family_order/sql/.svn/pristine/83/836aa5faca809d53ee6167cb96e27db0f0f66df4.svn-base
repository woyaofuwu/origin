SELECT TRUNC((SUM(sale_money)/SUM(SALE_NUM))* :CANSALE_NUM) sale_money,
       TRUNC((SUM(advance_pay)/SUM(SALE_NUM))* :CANSALE_NUM) advance_pay,
       TRUNC((SUM(old_money)/SUM(SALE_NUM))* :CANSALE_NUM) old_money,
       TRUNC((SUM(agent_fee)/SUM(SALE_NUM))* :CANSALE_NUM) agent_fee,discount
  FROM tf_b_cardsale_log a
 WHERE log_id=TO_NUMBER(:LOG_ID)
   AND eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND (:RES_KIND_CODE IS NULL OR (:RES_KIND_CODE IS NOT NULL AND res_kind_code=:RES_KIND_CODE))
   AND sale_type_code=:SALE_TYPE_CODE
   AND start_value<=:RES_NO_S
   AND end_value>=:RES_NO_E
   AND (:STOCK_ID IS NULL OR  (:STOCK_ID IS NOT NULL AND stock_id=:STOCK_ID))
   AND exists (SELECT 1
                 FROM tf_r_simcard_idle b
                WHERE sim_card_no>=:RES_NO_S
                  AND sim_card_no<=:RES_NO_E
                  AND eparchy_code=:EPARCHY_CODE
                  AND city_code=:CITY_CODE
                  AND fee_tag||''=:FEE_TAG4
                  AND stock_level||''=:STOCK_LEVEL
                  AND sim_state_code||''=:SIM_STATE_CODE4 
                  AND (:STOCK_ID IS NULL OR (:STOCK_ID IS NOT NULL AND stock_id=:STOCK_ID))
                  AND (:STOCK_ID_O IS NULL OR (:STOCK_ID_O IS NOT NULL AND stock_id_o=:STOCK_ID_O))
                  AND a.log_id=b.sale_log_id||'')
GROUP BY discount