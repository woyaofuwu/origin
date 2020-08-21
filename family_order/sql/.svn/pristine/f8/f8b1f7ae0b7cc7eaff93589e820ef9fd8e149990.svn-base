SELECT '' title, '本省' bs, '外省' ws, '' ww, '' zj FROM dual
UNION ALL
SELECT '呼叫次数' title, SUM(decode(a.home_type, '1', decode(a.asp,'1',nvl(a.trade_number,0),0), 0))||'' bs, 
       SUM(decode(a.home_type, '3', decode(a.asp,'1',nvl(a.trade_number,0),0), 0))||'' ws,
       SUM(decode(nvl(a.asp, '1'), '1', 0, nvl(a.trade_number,0)))||'' ww,
       SUM(decode(a.home_type, '1', decode(a.asp,'1',nvl(a.trade_number,0),0), 0))+SUM(decode(a.home_type, '3', decode(a.asp,'1',nvl(a.trade_number,0),0), 0))+SUM(decode(nvl(a.asp, '1'), '1', 0, nvl(a.trade_number,0)))||'' zj
FROM tl_bh_harassphone a
WHERE a.IN_TIME BETWEEN TO_DATE(:DATE_START4, 'yyyy-mm-dd hh24:mi:ss') AND
       TO_DATE(:DATE_END4, 'yyyy-mm-dd hh24:mi:ss')
AND a.PROV_CODE = :PARA_CODE6
UNION ALL
SELECT '号码个数' title, SUM(decode(a.home_type, '1', decode(a.asp,'1',1,0), 0))||'' bs, 
       SUM(decode(a.home_type, '3', decode(a.asp,'1',1,0), 0))||'' ws,
       SUM(decode(nvl(a.asp,'1'), '1', 0, 1))||'' ww,
       SUM(decode(a.home_type, '1', decode(a.asp,'1',1,0), 0))+SUM(decode(a.home_type, '3', decode(a.asp,'1',1,0), 0))+SUM(decode(nvl(a.asp,'1'), '1', 0, 1))||'' zj
FROM tl_bh_harassphone a WHERE a.rowid IN (SELECT MAX(b.rowid) FROM tl_bh_harassphone b WHERE a.serial_number=b.serial_number)
AND a.IN_TIME BETWEEN TO_DATE(:DATE_START4, 'yyyy-mm-dd hh24:mi:ss') AND
       TO_DATE(:DATE_END4, 'yyyy-mm-dd hh24:mi:ss')
AND a.PROV_CODE = :PARA_CODE6  
UNION ALL
SELECT '已停机呼叫量' title, SUM(decode(a.home_type, '1', decode(a.process_tag,'D',nvl(a.trade_number,0),0), 0))||'' bs, 
       '' ws,
       '' ww,
       SUM(decode(a.home_type, '1', decode(a.process_tag,'D',nvl(a.trade_number,0),0), 0))||'' zj
FROM tl_bh_harassphone a
WHERE a.IN_TIME BETWEEN TO_DATE(:DATE_START4, 'yyyy-mm-dd hh24:mi:ss') AND
       TO_DATE(:DATE_END4, 'yyyy-mm-dd hh24:mi:ss')
AND a.PROV_CODE = :PARA_CODE6 
UNION ALL
SELECT '已停机号码数' title, SUM(decode(a.home_type, '1', decode(a.process_tag,'D',1,0), 0))||'' bs, 
       '' ws,
       '' ww,
       SUM(decode(a.home_type, '1', decode(a.process_tag,'D',1,0), 0))||'' zj
FROM tl_bh_harassphone a WHERE a.rowid IN (SELECT MAX(b.rowid) FROM tl_bh_harassphone b WHERE a.serial_number=b.serial_number)
AND a.IN_TIME BETWEEN TO_DATE(:DATE_START4, 'yyyy-mm-dd hh24:mi:ss') AND
       TO_DATE(:DATE_END4, 'yyyy-mm-dd hh24:mi:ss')
AND a.PROV_CODE = :PARA_CODE6
UNION ALL
SELECT '拦截准确率' title, to_char(SUM(decode(a.home_type, '1', decode(a.process_tag,'D',1,0), 0))/SUM(decode(a.home_type, '1', decode(a.asp,'1',1,0), 0)), '0.00')||''  bs, 
       '' ws,
       '' ww,
       to_char(SUM(decode(a.home_type, '1', decode(a.process_tag,'D',1,0), 0))/SUM(decode(a.home_type, '1', decode(a.asp,'1',1,0), 0)), '0.00')||'' zj
FROM tl_bh_harassphone a WHERE a.rowid IN (SELECT MAX(b.rowid) FROM tl_bh_harassphone b WHERE a.serial_number=b.serial_number)
AND a.IN_TIME BETWEEN TO_DATE(:DATE_START4, 'yyyy-mm-dd hh24:mi:ss') AND
       TO_DATE(:DATE_END4, 'yyyy-mm-dd hh24:mi:ss')
AND a.PROV_CODE = :PARA_CODE6