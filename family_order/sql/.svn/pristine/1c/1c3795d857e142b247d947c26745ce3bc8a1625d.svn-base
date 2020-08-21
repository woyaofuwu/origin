SELECT COUNT(1) recordcount FROM td_b_package_ext a
Where (eparchy_code=:EPARCHY_CODE  Or  eparchy_code ='ZZZZ')
AND a.rsrv_str3 ='1'
AND EXISTS (SELECT 1 FROM tf_b_trade
    WHERE trade_id=TO_NUMBER(:TRADE_ID)
    AND rsrv_str2=a.package_id)