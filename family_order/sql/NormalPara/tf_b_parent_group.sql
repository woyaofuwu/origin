SELECT ec_code paracode,ec_name paraname FROM tf_b_parent_group
 WHERE (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)