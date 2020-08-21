SELECT decode((SELECT COUNT(1) FROM tf_bh_trade
				WHERE accept_date > to_date(:ACCEPT_DATE ,'yyyy-mm-dd hh24:mi:ss') 
				AND user_id = to_number(:USER_ID)
        AND trade_type_code in (192, 100, 7230, 7240, 7302, 264)),0,'出保','不出保') isinsurance
FROM dual