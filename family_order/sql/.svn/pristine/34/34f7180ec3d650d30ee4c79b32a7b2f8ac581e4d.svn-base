DELETE FROM tf_f_postinfo a
 WHERE a.id = TO_NUMBER(:ID)
   AND EXISTS (SELECT 1 FROM tf_b_trade_postinfo_bak b
                WHERE b.trade_id = :TRADE_ID
                  AND b.id = a.id
                  AND b.id_type = a.id_type
                  AND b.start_date = a.start_date
                  AND b.post_cyc = a.post_cyc)