DELETE FROM  TF_F_PO_POLICY a
 WHERE exists (select 1 from TF_B_TRADE_POPOLICY b 
                where b.trade_id = :TRADE_ID
                    and b.PORateNumber=a.PORateNumber
                   and b.user_id = a.user_id)
   and a.start_date >= a.end_date