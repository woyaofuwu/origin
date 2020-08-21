UPDATE tf_b_staff_appraise a SET a.appraise_code = decode(substr(TRIM(:RSRV_STR1),1,1),'U','U','H','H','M','M','L','L','H')
WHERE a.appraise_info = TRIM(:RSRV_STR2)