--IS_CACHE=Y
SELECT DEVICE_MODEL_CODE paracode,DEVICE_MODEL paraname FROM td_s_device_model
 WHERE EPARCHY_CODE=:TRADE_EPARCHY_CODE