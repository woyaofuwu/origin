UPDATE TF_F_PO_MBMP a
   SET end_date=(SELECT start_date-1/24/3600 FROM TF_B_TRADE_PO_MBMP b
                  WHERE trade_id = :TRADE_ID
                    and sysdate between a.start_date and a.end_date)
 WHERE exists (select 1 from TF_B_TRADE_PO_MBMP c where 
   trade_id = :TRADE_ID
   and c.user_id = a.user_id
   and c.POSpecNumber = a.POSpecNumber
   and oper_code <> '1'
   and sysdate between a.start_date and a.end_date)