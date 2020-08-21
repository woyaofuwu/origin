SELECT TRUNC((SUM(sale_money)/SUM(SALE_NUM))* :CANSALE_NUM) sale_money,
       TRUNC((SUM(advance_pay)/SUM(SALE_NUM))* :CANSALE_NUM) advance_pay,
       TRUNC((SUM(old_money)/SUM(SALE_NUM))* :CANSALE_NUM) old_money,
       TRUNC((SUM(agent_fee)/SUM(SALE_NUM))* :CANSALE_NUM) agent_fee,discount
  FROM tf_b_cardsale_log a
 WHERE log_id=TO_NUMBER(:LOG_ID)
   AND eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND (:RES_KIND_CODE IS NULL OR res_kind_code=:RES_KIND_CODE)
   AND sale_type_code=:SALE_TYPE_CODE
   AND start_value<=:RES_NO_S
   AND end_value>=:RES_NO_E
   AND (:STOCK_ID IS NULL OR stock_id=:STOCK_ID)
   AND exists (SELECT 1
                 FROM tf_r_emptycard b
                WHERE empty_card_id>=:RES_NO_S
                  AND empty_card_id<=:RES_NO_E
                  AND eparchy_code=:EPARCHY_CODE
                  AND card_state_code=:CARD_STATE_CODE
                  AND (:STOCK_ID_O is null or stock_id_o=:STOCK_ID_O)
                  AND (:RSRV_TAG1 is null or (rsrv_tag1 is null or rsrv_tag1 = '0'))
                  AND (:RSRV_TAG2 is null or rsrv_tag1=:RSRV_TAG2)
                  AND a.log_id=b.rsrv_str1)
GROUP BY discount