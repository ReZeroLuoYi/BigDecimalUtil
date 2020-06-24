package com.luoyi.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * BigDecimal工具类
 * <p>本类未对参数做严格校验，请使用者注意以下几点：<br>
 *     1.确保参数为number子类或者String类型数字<br>
 *     2.确保参数为不为null
 * </p>
 * @author luoyi9402
 * @since 20/3/10 上午 10:19
 */
@Slf4j
public class BigDecimalUtil {

    public final static BigDecimalUtil INSTANCE = new BigDecimalUtil();

    /**
     * 至少两位数值相加，默认结果四舍五入保留4位小数
     * <p>number1 + number2 + ... + number(n)</p>
     * @author luoyi9402
     * @since 20/3/10 上午 10:22
     */
    public BigDecimal add(Object number1, Object number2, Object... numberArr) {
        BigDecimal result = new BigDecimal(number1.toString()).add(new BigDecimal(number2.toString()));
        for (Object number : numberArr) {
            result = result.add((new BigDecimal(number.toString())));
        }
        return result.setScale(scale, roundingMode).stripTrailingZeros();
    }

    /**
     * 至少两个数值相减，默认结果四舍五入保留4位小数
     * <p>number1 - number2 - ... - number(n)</p>
     * @author luoyi9402
     * @since 20/3/10 上午 10:25
     */
    public BigDecimal subtract(Object number1, Object number2, Object... numberArr) {
        BigDecimal result = new BigDecimal(number1.toString()).subtract(new BigDecimal(number2.toString()));
        for (Object number : numberArr) {
            result = result.subtract((new BigDecimal(number.toString())));
        }
        return result.setScale(scale, roundingMode).stripTrailingZeros();
    }

    /**
     * 至少两个数值相乘，默认结果四舍五入保留4位小数
     * <p>number1 * number2 * ... * number(n)</p>
     * @author luoyi9402
     * @since 20/3/10 上午 10:46
     */
    public BigDecimal multiply(Object number1, Object number2, Object... numberArr) {
        BigDecimal result = new BigDecimal(number1.toString()).multiply(new BigDecimal(number2.toString()));
        for (Object number : numberArr) {
            result = result.multiply((new BigDecimal(number.toString())));
        }
        return result.setScale(scale, roundingMode).stripTrailingZeros();
    }

    /**
     * 至少两个数值相除，除数不能为0，默认结果四舍五入保留4位小数
     * <p>number1 / number2 / ... / number(n)</p>
     * <p>ps：外层需自己捕获ArithmeticException("Division by zero")</p>
     * @author luoyi9402
     * @since 20/3/10 上午 10:35
     */
    public BigDecimal divide(Object number1, Object number2, Object... numberArr) {
        BigDecimal result = new BigDecimal(number1.toString()).divide(new BigDecimal(number2.toString()), scale, roundingMode);
        for (Object number : numberArr) {
            result = result.divide((new BigDecimal(number.toString())), scale, roundingMode);
        }
        return result.stripTrailingZeros();
    }

    //----------more----------

    /**
     * divide补充
     * @author luoyi9402
     * @since 20/5/9 下午 5:51
     */
    public BigDecimal divideTry(Object number1, Object number2) {
        try {
            return new BigDecimal(number1.toString()).divide(new BigDecimal(number2.toString()), scale, roundingMode).stripTrailingZeros();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 两个数值相除，被除数不能为0，默认结果四舍五入保留4位小数
     * <p>number1 / number2</p>
     * @author luoyi9402
     * @since 20/3/10 上午 10:35
     * @param ifDivisorNullReturn 如果除数为0的返回值，null or BigDecimal.ZERO
     */
    public BigDecimal divideWithJudge(Object number1, Object number2, BigDecimal ifDivisorNullReturn) {
        BigDecimal bg2 = new BigDecimal(number2.toString());
        if (BigDecimal.ZERO.compareTo(bg2) == 0) {
            return ifDivisorNullReturn;
        }
        return new BigDecimal(number1.toString()).divide(bg2, scale, roundingMode).stripTrailingZeros();
    }

    /**
     * 比较两个数值，not null，number1.compare(number2)
     */
    public int compare(Object number1, Object number2) {
        return new BigDecimal(number1.toString()).compareTo(new BigDecimal(number2.toString()));
    }

    /**
     * 判断两个数值是否相等，not null，number1.equals(number2)
     */
    public boolean equals(Object number1, Object number2) {
        return new BigDecimal(number1.toString()).equals(new BigDecimal(number2.toString()));
    }

    /**
     * 两个数值相加，无精度设置
     */
    public BigDecimal addNotSetScale(Object number1, Object number2) {
        return new BigDecimal(number1.toString()).add(new BigDecimal(number2.toString()));
    }

    /**
     * 两个数值相减，无精度设置
     */
    public BigDecimal subtractNotSetScale(Object number1, Object number2) {
        return new BigDecimal(number1.toString()).subtract(new BigDecimal(number2.toString()));
    }

    /**
     * 两个数值相乘，无精度设置
     */
    public BigDecimal multiplyNotSetScale(Object number1, Object number2) {
        return new BigDecimal(number1.toString()).multiply(new BigDecimal(number2.toString()));
    }

    /**
     * 格式化当前数字
     */
    public BigDecimal format(Object number) {
        if (number == null) {
            return null;
        }
        return new BigDecimal(number.toString()).setScale(scale, roundingMode).stripTrailingZeros();
    }

    /**
     * 解析基本计算式，按顺序输入各参数，每个符号占一个参数，仅支持加减乘除和括号：+、-、*、/、(、)，不可识别中文括号，且数值与括号间乘号不可省略
     *
     * <br>  示例： 奖励 =（毛利-标准值）*系数
     * <br>    BigDecimal 毛利 = new BigDecimal("8844.43");
     * <br>    int 标准值 = 5000;
     * <br>    String 系数 = "0.01";
     * <br>    BigDecimal 奖励 = BigDecimalUtil.INSTANCE.compute( "(" , 毛利, "-", 标准值, ")", "*", 系数 );
     * @author luoyi9402
     * @since 20/3/10 下午 5:50
     */
    public BigDecimal compute(Object... objectArr) {
        return computeByArr(objectArr);
    }

    /**
     * 解析基本计算式，先输入公式，再按顺序输入各参数，运算仅支持加减乘除和括号：+、-、*、/、(、)，可识别中文括号，数值与括号间乘号不可省略
     *
     * <br>  示例： 奖励 =（毛利-标准值）*系数
     * <br>    BigDecimal grossProfit = new BigDecimal("8844.43");
     * <br>    int base = 5000;
     * <br>    String rate = "0.01";
     * <br>    BigDecimal profit = BigDecimalUtil.INSTANCE.computeByFormula("奖励 =（毛利-标准值）*系数" , grossProfit, base, rate);
     * @author luoyi9402
     * @since 20/3/11 下午 3:08
     * @param formula 计算公式，例如：奖励 =（毛利-标准值）*系数
     * @param numberArr 按顺序传入各参数值，注：等号前的字符会被截取，仅需传入等号后的参数
     */
    public BigDecimal computeByFormula(String formula, Object... numberArr) {
        //格式化公式
        String equalSign = "=";
        if (formula.contains(equalSign)) {
            formula = formula.substring(formula.indexOf(equalSign) + 1);
        }
        formula = formula.replace("（", "(").replace("）", ")").replace(" ", "");
        //截取公式各值
        String operatorRegex = "[+\\-*/()]";
        String[] parameterArr = formula.split(operatorRegex);
        //split结果含有空字符串
        List<String> parameterList = new ArrayList<>();
        for (String parameter : parameterArr) {
            if (parameter.length() > 0) {
                parameterList.add(parameter);
            }
        }
        if (parameterList.size() != numberArr.length) {
            throw new RuntimeException("参数无法对应，请检查参数个数");
        }
        //将参数与运算符按顺序排列
        ArrayList<Object> objectList = new ArrayList<>();
        int numIndex = 0;
        while (formula.length() > 0) {
            String firstChar = String.valueOf(formula.charAt(0));
            if (firstChar.matches(operatorRegex)) {
                formula = formula.substring(1);
                objectList.add(firstChar);
            } else {
                formula = formula.substring(parameterList.get(numIndex).length());
                objectList.add(numberArr[numIndex]);
                numIndex++;
            }
        }
        return computeByArr(objectList.toArray());
    }

    /**
     * 创建一个数字栈一个操作符栈
     * 遇到操作数则送入数字栈
     * 遇到操作符则对比上一个操作符，若上一个操作符优先级高于等于本次运算符，则运算上个运算符，否则则将本次运算符推入操作符栈
     *  */
    private BigDecimal computeByArr(Object[] objectArr) {
        log.debug("尝试解析计算:{}", Arrays.toString(objectArr).replace(",", ""));
        //计算栈
        Deque<BigDecimal> numberStack = new LinkedList<>();
        Deque<Operator> operatorStack = new LinkedList<>();
        try {
            for (Object object : objectArr) {
                //判断是否是操作符
                Operator operator = object instanceof String ? Operator.getByCode(object.toString()) : null;
                //是操作符
                if (operator != null) {
                    //上个运算符
                    Operator lastOperator = operatorStack.peek();
                    while (lastOperator != null) {
                        //若上一个操作符优先级高于等于本次运算符，则运算上个运算符
                        if (lastOperator.getPriority() <= operator.getPriority() && !Operator.PARENTHESIS_LEFT.equals(lastOperator)) {
                            //计算上个操作符
                            operatorStack.pop();
                            BigDecimal bg2 = numberStack.pop();
                            BigDecimal bg1 = numberStack.pop();
                            BigDecimal bg0 = computeByOperator(bg1, bg2, lastOperator);
                            //将结果压入栈
                            numberStack.push(bg0);
                            //迭代判断上个运算符是否需要运算
                            lastOperator = operatorStack.peek();
                        }
                        else {
                            break;
                        }
                    }
                    //去除括号
                    if (Operator.PARENTHESIS_RIGHT.equals(operator) && Operator.PARENTHESIS_LEFT.equals(lastOperator)) {
                        operatorStack.pop();
                    }
                    //否则则将本次运算符推入操作符栈
                    else {
                        operatorStack.push(operator);
                    }
                }
                //数字直接压入数字栈
                else {
                    //try捕获非数字异常
                    try {
                        numberStack.push(new BigDecimal(object.toString()));
                    } catch (NumberFormatException exception) {
                        log.error("不支持的运算符\"{}\"", object);
                        throw exception;
                    }
                }
            }
            //最后只会剩运算等级由低到高的结果，按顺序计算最终结果
            BigDecimal result = numberStack.pop();
            if (numberStack.size() != operatorStack.size()) {
                throw new RuntimeException("解析错误，请检查计算式是否完整");
            }
            while (!numberStack.isEmpty()) {
                result = computeByOperator(numberStack.pop(), result, operatorStack.pop());
            }
            //返回格式化后的数据
            return result.setScale(scale, roundingMode).stripTrailingZeros();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("解析错误，请检查计算式是否完整");
        }
    }

    private BigDecimal computeByOperator(BigDecimal bg1, BigDecimal bg2, Operator operator) {
        BigDecimal result;
        switch (operator) {
            case ADD: {
                result = bg1.add(bg2);
                break;
            }
            case SUBTRACT: {
                result = bg1.subtract(bg2);
                break;
            }
            case MULTIPLY: {
                result = bg1.multiply(bg2);
                break;
            }
            case DIVIDE: {
                result = bg1.divide(bg2, scale, roundingMode);
                break;
            }
            default: {
                throw new RuntimeException("不支持的操作符");
            }
        }
        return result;
    }

    @AllArgsConstructor
    private enum Operator{
        /** 加 */
        ADD("+", 4),
        /** 减 */
        SUBTRACT("-", 4),
        /** 乘 */
        MULTIPLY("*", 3),
        /** 除 */
        DIVIDE("/", 3),
        /** 左括号 */
        PARENTHESIS_LEFT("(", 1),
        /** 右括号 */
        PARENTHESIS_RIGHT(")", 16);

        /** 符号 */
        @Getter
        private String code;
        /** 优先级，1级为最高 */
        @Getter
        private int priority;

        public static Operator getByCode(String operatorStr) {
            for (Operator operator : Operator.values()) {
                if (operator.code.equals(operatorStr)) {
                    return operator;
                }
            }
            return null;
        }
    }


    public BigDecimalUtil() {
        this.scale = 4;
        this.roundingMode = BigDecimal.ROUND_HALF_UP;
    }

    public BigDecimalUtil(int scale, int roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    /** 保留小数位 */
    private int scale;
    /** 小数舍入模式 */
    private int roundingMode;

    /**
     * 判断是否在起始值和终止值之间，包含尾，不包含头，不用比较null值
     * @param value 目标值
     * @param startValue 起始值
     * @param endValue 终止值
     * @return
     */
    public boolean isBetween(BigDecimal value,BigDecimal startValue,BigDecimal endValue){
        boolean result=true;
        if(startValue!=null){
            result=value.compareTo(startValue)>0;
        }
        if(endValue!=null){
            result=result&&value.compareTo(endValue)<=0;
        }
        return result;
    }

    /**
     * 获取最终值
     * @param value 目标值
     * @param minValue 最小值
     * @param maxValue 最大值
     * @return
     */
    public BigDecimal getRealValue(BigDecimal value,BigDecimal minValue,BigDecimal maxValue){
        if(minValue!=null){
            if(value.compareTo(minValue)<=0){
                return minValue;
            }
        }
        if(maxValue!=null){
            if(value.compareTo(maxValue)>=0){
                return maxValue;
            }
        }
        return value;
    }

    public BigDecimal convertNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 给object中所有BigDecimal类型的为null的值设为0
     * @author luoyi9402
     * @since 20/4/18 下午 4:37
     */
    public static void setFieldsNullToZero(Object object) {
        setFieldsNullToZero(object.getClass(), object);
    }

    private static void setFieldsNullToZero(Class<?> clazz, Object object) {
        try {
            //查询是否有父类，递归调用
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                setFieldsNullToZero(superclass, object);
            }
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                Class<?> fieldType = field.getType();
                if (fieldType.equals(BigDecimal.class)) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    Object fieldValue = field.get(object);
                    if (fieldValue == null) {
                        field.set(object, BigDecimal.ZERO);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            log.error("", e);
            throw new RuntimeException("赋值失败");
        }
    }

    /**
     * 为object中所有BigDecimal类型的值执行function
     * <p>ps:通过类反射实现重新赋值，需要注意并发情况下是否有其他线程修改对象字段权限为false</p>
     * <P>2020年6月23日，增加对父类成员变量的支持</P>
     * @author luoyi9402
     * @since 20/4/18 下午 4:44
     */
    public static void applyFieldsValue(Object object, Function<BigDecimal, BigDecimal> function) {
        applyFieldsValue(object.getClass(), object, function);
    }

    /**
     * 2020年6月23日，增加对父类成员变量的支持
     * @author luoyi9402
     * @since 20/6/23 下午 5:04
     */
    private static void applyFieldsValue(Class<?> clazz, Object object, Function<BigDecimal, BigDecimal> function) {
        try {
            //查询是否有父类，递归调用
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                applyFieldsValue(superclass, object, function);
            }
            //获取所有字段（包括私有字段）
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                //获取字段类型，并判断是否是BigDecimal
                Class<?> fieldType = field.getType();
                if (fieldType.equals(BigDecimal.class)) {
                    //field isAccessible 需要为true才能通过反射赋值
                    //注意：为避免反复修改访问权限（且需要加锁），直接修改权限为true
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    //获取字段原值
                    BigDecimal fieldValue = (BigDecimal) field.get(object);
                    //将function返回值重新赋值给该字段
                    field.set(object, function.apply(fieldValue));
                }
            }
        } catch (IllegalAccessException e) {
            log.error("", e);
            throw new RuntimeException("赋值失败");
        }
    }

    /**
     * 格式化object中所有BigDecimal类型的值
     * @author luoyi9402
     * @since 20/4/18 下午 4:48
     */
    public void formatFields(Object object) {
        applyFieldsValue(object, this::format);
    }
}
