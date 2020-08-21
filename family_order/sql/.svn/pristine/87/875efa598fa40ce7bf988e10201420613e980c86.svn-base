SELECT a.PACKAGE_ID para_code1,b.PACKAGE_NAME para_code2,
a.DISCNT_CODE para_code3,
decode(a.ACTIVE_FLAG,'0','未激活','1','已激活','2','已退定','未知') para_code4,a.IMEI para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
a.ACTIVE_TIME start_date,a.INVALID_DATE end_date,  '' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_F_MOBILE_DISCNT_USER a,TD_B_MOBILE_DISCNT b
 WHERE a.PACKAGE_ID=b.PACKAGE_ID
    AND    a.serial_number= :PARA_CODE1
    AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
    AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
    AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
    AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
    AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
    AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
    AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
    AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
    AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)