UPDATE TF_B_TRADE_USER T   SET T.CONTRACT_ID = :CONTRACT_ID WHERE T.TRADE_ID IN (select a.trade_id from tf_b_trade a where a.order_id = :ORDER_ID)
