SELECT COUNT(1) recordcount
FROM(
SELECT 1
FROM tf_r_mphonecode_idle a
WHERE a.serial_number = :SERIAL_NUMBER
  AND EXISTS(SELECT 1
                   FROM td_m_res_commpara b
                  WHERE b.eparchy_code = :EPARCHY_CODE
                    AND b.para_attr = 1031
                    AND '9' = b.para_code1
                    AND instr(b.para_code2,:BRAND_CODE)>0)
  AND (a.brand_tag IS NULL OR a.brand_tag='0')
UNION ALL
SELECT 1
FROM tf_r_mphonecode_idle a
WHERE a.serial_number = :SERIAL_NUMBER
  AND EXISTS(SELECT 1
                   FROM td_m_res_commpara b
                  WHERE b.eparchy_code = :EPARCHY_CODE
                    AND b.para_attr = 1031
                    AND a.brand_tag = b.para_code1
                    AND instr(b.para_code2,:BRAND_CODE)=0)
  AND (a.brand_tag IS NOT NULL AND a.brand_tag!='0')
 )