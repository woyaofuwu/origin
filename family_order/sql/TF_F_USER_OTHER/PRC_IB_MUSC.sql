DECLARE
iv_user_id NUMBER(16):=TO_NUMBER(:user_id);
iv_rsrv_str2 VARCHAR2(20):=:rsrv_str2;--00申请;01:取消
iv_biz_type_code CHAR(2):='98';--音乐会员
iv_curdate DATE:=SYSDATE;
iv_org_domain VARCHAR2(8);
BEGIN
:code:=-1;
:info:='TRADE OK!';
:x_tag:=0;
:rsrv_str3:='11';
INSERT INTO tf_f_user_mbmp
(
partition_id,
user_id,
serial_number,
biz_type_code,
org_domain,
opr_source,
passwd,
biz_state_code,
start_date,
end_date,
rsrv_num1,
rsrv_num2,
rsrv_num3,
rsrv_num4,
rsrv_num5,
rsrv_str1,
rsrv_str2,
rsrv_str3,
rsrv_str4,
rsrv_str5,
rsrv_date1,
rsrv_date2,
rsrv_date3,
remark,
update_staff_id,
update_depart_id,
update_time
)
SELECT partition_id,
user_id,
serial_number,
biz_type_code,
org_domain,
opr_source,
passwd,
DECODE(biz_state_code,'A','E','E','A'),
iv_curdate,
end_date,
rsrv_num1,
rsrv_num2,
rsrv_num3,
rsrv_num4,
rsrv_num5,
rsrv_str1,
rsrv_str2,
rsrv_str3,
DECODE(biz_state_code,'A','UNDO',''),
rsrv_str5,
rsrv_date1,
rsrv_date2,
rsrv_date3,
DECODE(biz_state_code,'A','销户处理','E','复机处理'),
update_staff_id,
update_depart_id,
iv_curdate
FROM tf_f_user_mbmp
WHERE biz_type_code=iv_biz_type_code AND user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
AND iv_curdate BETWEEN start_date AND end_date+0 AND biz_state_code= DECODE(iv_rsrv_str2,'01','A','E')
AND NVL(rsrv_str4,'DO')=DECODE(iv_rsrv_str2,'01',NVL(rsrv_str4,'DO'),'UNDO') AND ROWNUM<2;
IF SQL%ROWCOUNT>0 THEN
UPDATE tf_f_user_mbmp
SET end_date=iv_curdate-1/24/3600,
rsrv_str4=DECODE(rsrv_str4,'UNDO','',rsrv_str4)
WHERE biz_type_code=iv_biz_type_code AND user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
AND iv_curdate-1/24/3600 BETWEEN start_date AND end_date+0 AND ROWNUM<2 RETURNING org_domain INTO iv_org_domain;
BEGIN
SELECT info_value
INTO :rsrv_str3
FROM  tf_f_user_mbmp_plus
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
AND biz_type_code=iv_biz_type_code AND org_domain=iv_org_domain
AND info_code='302' ;
EXCEPTION
WHEN OTHERS THEN
NULL;
END;
:x_tag:=1;--调用
END IF;
:code:=0;
END;