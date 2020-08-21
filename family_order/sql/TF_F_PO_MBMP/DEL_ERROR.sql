DELETE FROM  TF_F_PO_MBMP a
 WHERE exists (select 1 from TF_B_TRADE_PO_MBMP b 
                where b.trade_id = :TRADE_ID
                   and b.POSpecNumber =a.POSpecNumber
                   and b.user_id = a.user_id)
   and a.start_date >= a.end_date