SELECT TRUNC((SUM(sale_money)/SUM(SALE_NUM))* :CANSALE_NUM) sale_money,
       TRUNC((SUM(advance_pay)/SUM(SALE_NUM))* :CANSALE_NUM) advance_pay,
       TRUNC((SUM(old_money)/SUM(SALE_NUM))* :CANSALE_NUM) old_money,
       TRUNC((SUM(agent_fee)/SUM(SALE_NUM))* :CANSALE_NUM) agent_fee,discount
  FROM tf_b_cardsale_log a
 WHERE log_id=TO_NUMBER(:LOG_ID)
   AND eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND (:RES_KIND_CODE is null or res_kind_code=:RES_KIND_CODE)
   AND sale_type_code=:SALE_TYPE_CODE
   AND start_value<=:RES_NO_S
   AND end_value>=:RES_NO_E
   AND (:STOCK_ID is null or stock_id=:STOCK_ID)
   AND exists (SELECT 1
                 FROM tf_r_simcard_use b
                WHERE sim_card_no>=:RES_NO_S
                  AND sim_card_no<=:RES_NO_E
                  AND eparchy_code=:EPARCHY_CODE
                  AND (:OPEN_MODE is null or open_mode=:OPEN_MODE)
                  AND (:PREOPEN_TAG is null or preopen_tag=:PREOPEN_TAG)
                  AND (:FEE_TAG is null or fee_tag=:FEE_TAG)
                  AND (:SIM_STATE_CODE is null or sim_state_code=:SIM_STATE_CODE )
                  AND (:CITY_CODE is null or city_code=:CITY_CODE)
                  AND (:STOCK_ID_O IS NULL OR stock_id_o=:STOCK_ID_O)
                  AND (:STAFF_ID is null or staff_id=:STAFF_ID)
                  AND a.log_id=b.sale_log_id||'')
GROUP BY discount