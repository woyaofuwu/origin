select TO_CHAR(a.trade_id) trade_id,
        a.accept_month,
        a.user_id,
        a.merch_spec_code,
        a.merch_discnt_code,
        a.start_date,
        a.end_date,
        a.modify_tag
   FROM TF_B_TRADE_GRP_MERCH_DISCNT a
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))