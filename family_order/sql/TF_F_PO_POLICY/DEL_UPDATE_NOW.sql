UPDATE TF_F_PO_POLICY a
   SET end_date=(SELECT start_date-1/24/3600 FROM TF_B_TRADE_POPOLICY b
                  WHERE trade_id = :TRADE_ID
                    and sysdate between a.start_date and a.end_date
                    and rownum<2)
 WHERE exists (select 1 from TF_B_TRADE_POPOLICY  c where a.PORateNumber=c.PORateNumber
   and a.user_id=c.user_id
   and a.POSPECNUMBER =c.POSPECNUMBER 
   and trade_id = :TRADE_ID
   AND sysdate between a.start_date and a.end_date
   and oper_code <> '1')