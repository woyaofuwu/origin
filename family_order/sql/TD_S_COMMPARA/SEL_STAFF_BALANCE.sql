SELECT trunc(subj_balance/100)  para_code1,
'' para_code2,
'' para_code3,
'' para_code4,
'' para_code5,
'' para_code6,'' para_code7,'' para_code8,'' para_code9,
'' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from  CHNL_CHANNEL ch, chnl_dat_book z ,CHNL_CHACCT A 
where CH.channel_code IN
(SELECT c.depart_code FROM td_m_staff b,td_m_depart c WHERE b.staff_id=:PARA_CODE1
AND b.dimission_tag='0'
AND b.depart_id=c.depart_id AND c.validflag='0')
AND CH.CHACCT_ID =A.CHACCT_ID 
AND Z.CHACCT_ID =A.CHACCT_ID 
AND subj_id ='8'   
AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)	     
AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)