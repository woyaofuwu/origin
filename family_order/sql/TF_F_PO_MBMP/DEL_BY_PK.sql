delete FROM TF_F_PO_MBMP a
 where exists (select 1 from TF_B_TRADE_PO_MBMP b
                where b.trade_id = :TRADE_ID
                  and b.user_id = a.user_id
                  and b.POSpecNumber =a.POSpecNumber 
                  and b.start_date = a.start_date)