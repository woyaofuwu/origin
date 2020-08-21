delete FROM TF_F_PRODUCT_POLICY a
 where exists (select 1 from TF_B_TRADE_POPOLICY b
                where b.trade_id = :TRADE_ID
                  and b.user_id = a.user_id
                  and b.PORateNumber=a.PORateNumber
                  and b.start_date = a.start_date)