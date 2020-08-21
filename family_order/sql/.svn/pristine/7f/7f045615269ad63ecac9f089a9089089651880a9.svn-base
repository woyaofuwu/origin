SELECT '' title,substr(b.serial_number,3) serial_number,b.prov_code, sum(nvl(b.trade_number, 0)) trade_number
FROM tl_bh_harassphone b
WHERE b.IN_TIME BETWEEN TO_DATE(:DATE_START4, 'yyyy-mm-dd hh24:mi:ss') AND
       TO_DATE(:DATE_END4, 'yyyy-mm-dd hh24:mi:ss')
AND b.PROV_CODE = :PARA_CODE6
GROUP BY b.serial_number,b.prov_code
UNION ALL
SELECT '总计' title, COUNT(serial_number)||'' serial_number,'', SUM(trade_number) trade_number
FROM (
    SELECT '',substr(b.serial_number,3) serial_number, sum(nvl(b.trade_number, 0)) trade_number
    FROM tl_bh_harassphone b
    WHERE b.IN_TIME BETWEEN TO_DATE(:DATE_START4, 'yyyy-mm-dd hh24:mi:ss') AND
           TO_DATE(:DATE_END4, 'yyyy-mm-dd hh24:mi:ss')
    AND b.PROV_CODE = :PARA_CODE6
    GROUP BY b.serial_number
)