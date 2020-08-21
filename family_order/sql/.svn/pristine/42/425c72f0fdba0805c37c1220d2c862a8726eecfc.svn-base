	 select b.* from tf_bh_trade a,TF_B_TRADE_DISCNT_BAK b 
	  where a.trade_id = b.trade_id
   and a.TRADE_ID = :TRADE_ID
	 and b.PRODUCT_ID=:PRODUCT_ID
	 and b.PACKAGE_ID=:PACKAGE_ID
	 and a.cancel_tag in ('0', '1')
	 order by b.end_date desc