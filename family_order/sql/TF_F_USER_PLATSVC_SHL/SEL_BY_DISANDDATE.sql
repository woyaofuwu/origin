SELECT a.serial_number, a.discnt_code, a.service_ID, decode(to_number(a.sms_tag-a.resms_tag), 0, '不退订', '退订')  hasCancel, a.rsrv_date1 
FROM TF_F_USER_PLATSVC_SHL a 
WHERE a.discnt_code = :DISCNT_CODE 
AND a.rsrv_date1 between to_date(:RSRV_DATE1, 'yyyy-mm-dd') AND to_date(:RSRV_DATE1, 'yyyy-mm-dd') + 1 - 1/84600 