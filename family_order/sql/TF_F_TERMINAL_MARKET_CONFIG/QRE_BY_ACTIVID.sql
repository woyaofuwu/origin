select t.TERMINAL_IN_PRICE,
       t.TERMINAL_SAL_PRICE,
       t.BARE_SAL_PRICE,
       t.SENT_PHONE_BILL,
       t.GIFT_ITEMS,
       t.RETURN_BILL,
       to_char(t.ACTIV_DATE,'yyyy-mm-dd') ACTIV_DATE,
       to_char(t.RETURN_DATE,'yyyy-mm-dd') RETURN_DATE,    
       t.TOTAL,
       t.SENT_GIFT,
       t.APPOINT_AMOUNT,
       t.SUBSIDY,
       t.ACTIV_ID,
       t.REMOVE_TAG
  FROM TF_F_TERMINAL_MARKET_CONFIG t
 where t.ACTIV_ID = to_char(:ACTIV_ID)