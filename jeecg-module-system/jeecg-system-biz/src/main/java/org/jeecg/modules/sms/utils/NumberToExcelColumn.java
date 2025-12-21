package org.jeecg.modules.sms.utils;

public class NumberToExcelColumn {

    /**
     * 数字转Excel列名（1→A，26→Z，27→AA，以此类推）
     * @param num 待转换的正整数（num > 0）
     * @return 对应的字母列名
     */
    public static String numberToColumn(int num) {
        // 校验输入合法性
        if (num <= 0) {
            throw new IllegalArgumentException("数字必须是正整数");
        }

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            // 关键：减1将1-26映射为0-25，适配ASCII码计算
            num--;
            // 取余得到当前位的字母（0→A，1→B...25→Z）
            char c = (char) ('A' + num % 26);
            // 追加到字符串（先得到低位，最后反转）
            sb.append(c);
            // 整除26处理下一位
            num = num / 26;
        }
        // 反转得到正确顺序
        return sb.reverse().toString();
    }

    // 测试示例
    public static void main(String[] args) {
        System.out.println(numberToColumn(Integer.parseInt("01")));    // 输出 A
        System.out.println(numberToColumn(26));   // 输出 Z
        System.out.println(numberToColumn(27));   // 输出 AA
        System.out.println(numberToColumn(52));   // 输出 AZ
        System.out.println(numberToColumn(53));   // 输出 BA
        System.out.println(numberToColumn(702));  // 输出 ZZ
        System.out.println(numberToColumn(703));  // 输出 AAA
    }
}