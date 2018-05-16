package com.perfect.library.utils.math;

import com.perfect.library.utils.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/24 14:22.
 * Function :高精度数学计算管理类
 */
@SuppressWarnings("ALL")
public class MathManager {

    public enum ValueType {
        INT,
        FLOAT,
        DOUBLE,
        LONG,
        STRING;
    }

    private BigDecimal mBigDecimal_A;
    private BigDecimal mBigDecimal_B;

    private MathManager() {
    }

    public static MathManager create() {
        return new MathManager();
    }

    /**
     * 设置准备参与计算的两个数字
     *
     * @param a 第一个数字，字符串格式
     * @param b 第二个数字，字符串格式
     */
    public OperationManager setNumbers(String a, String b) {
        ExceptionUtils.checkObjectIsEmpty(a, "the string cannot empty");
        ExceptionUtils.checkObjectIsEmpty(b, "the string cannot empty");
        ExceptionUtils.checkStringIsNum2(a, "this can only number");
        ExceptionUtils.checkStringIsNum2(b, "this can only number");
        mBigDecimal_A = new BigDecimal(a);
        mBigDecimal_B = new BigDecimal(b);
        return new OperationManager();
    }

    /**
     * 设置需要进行处理的数字
     */
    public Operation1Manager setNumber(String num) {
        ExceptionUtils.checkObjectIsEmpty(num, "the string cannot empty");
        ExceptionUtils.checkStringIsNum2(num, "this can only number");
        mBigDecimal_A = new BigDecimal(num);
        return new Operation1Manager();
    }

    public class Operation1Manager {
        /**
         * 绝对值
         */
        public String abs() {
            return mBigDecimal_A.abs().toString();
        }

        /**
         * 确定小数位数的绝对值，多余数字四舍五入
         */
        public String abs(int decimalNum) {
            return abs(decimalNum, RoundingMode.HALF_UP);
        }

        /**
         * 确定小数位数的绝对值，多余数字按设置模式处理
         */
        public String abs(int decimalNum, RoundingMode roundingMode) {
            return mBigDecimal_A.abs().setScale(decimalNum, roundingMode).toString();
        }

        /**
         * 设置小数位数，多余的四舍五入
         */
        public String setDecimalNum(int decimalNum) {
            return setDecimalNum(decimalNum, RoundingMode.HALF_UP);
        }

        /**
         * 设置小数位数，多余的按设置模式处理
         */
        public String setDecimalNum(int decimalNum, RoundingMode roundingMode) {
            return mBigDecimal_A.setScale(decimalNum, roundingMode).toString();
        }
    }

    public class OperationManager {

        /**
         * 比较大小
         *
         * @return -1 a<b; 0 a=b; 1 a>b
         */
        public int compare() {
            return mBigDecimal_A.compareTo(mBigDecimal_B);
        }

        /**
         * 最大值
         */
        public String max() {
            return mBigDecimal_A.max(mBigDecimal_B).toString();
        }

        /**
         * 最小值
         */
        public String min() {
            return mBigDecimal_A.min(mBigDecimal_B).toString();
        }

        /**
         * 加减乘除操作
         *
         * @param valueType 设置返回值类型
         */
        public BaseOperationCallBack operation(ValueType valueType) {
            if (valueType == ValueType.DOUBLE) {
                return new DoubleManager(mBigDecimal_A, mBigDecimal_B);
            } else if (valueType == ValueType.FLOAT) {
                return new FloatManager(mBigDecimal_A, mBigDecimal_B);
            } else if (valueType == ValueType.INT) {
                return new IntegerManager(mBigDecimal_A, mBigDecimal_B);
            } else if (valueType == ValueType.LONG) {
                return new LongManager(mBigDecimal_A, mBigDecimal_B);
            } else if (valueType == ValueType.STRING) {
                return new StringManager(mBigDecimal_A, mBigDecimal_B);
            } else {
                return null;
            }
        }
    }

    public class BaseOperationCallBack<T> {

        protected BigDecimal mDecimal;
        protected BigDecimal mDecimal_A;
        protected BigDecimal mDecimal_B;
        protected int mDecimalNum;
        protected RoundingMode mRoundingMode;

        BaseOperationCallBack(BigDecimal a, BigDecimal b) {
            this.mDecimal_A = a;
            this.mDecimal_B = b;
        }

        /**
         * 设置小数位数，多余的四舍五入
         */
        public BaseOperationCallBack setDecimalNum(int decimalNum) {
            return setDecimalNum(decimalNum, RoundingMode.HALF_UP);
        }

        /**
         * 设置小数位数，多余的按设置模式处理
         */
        public BaseOperationCallBack setDecimalNum(int decimalNum, RoundingMode roundingMode) {
            this.mDecimalNum = decimalNum;
            this.mRoundingMode = roundingMode;
            return this;
        }

        public T add() {
            return null;
        }

        public T subtract() {
            return null;
        }

        public T multiply() {
            return null;
        }

        public T divide() {
            return null;
        }
    }

    private class DoubleManager extends BaseOperationCallBack<Double> {

        DoubleManager(BigDecimal a, BigDecimal b) {
            super(a, b);
        }

        @Override
        public Double add() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.add(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.add(mDecimal_B);
            }
            return mDecimal.doubleValue();
        }

        @Override
        public Double subtract() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.subtract(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.subtract(mDecimal_B);
            }
            return mDecimal.doubleValue();
        }

        @Override
        public Double multiply() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.multiply(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.multiply(mDecimal_B);
            }
            return mDecimal.doubleValue();
        }

        @Override
        public Double divide() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.divide(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.divide(mDecimal_B);
            }
            return mDecimal.doubleValue();
        }
    }

    private class FloatManager extends BaseOperationCallBack<Float> {

        FloatManager(BigDecimal a, BigDecimal b) {
            super(a, b);
        }

        @Override
        public Float add() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.add(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.add(mDecimal_B);
            }
            return mDecimal.floatValue();
        }

        @Override
        public Float subtract() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.subtract(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.subtract(mDecimal_B);
            }
            return mDecimal.floatValue();
        }

        @Override
        public Float multiply() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.multiply(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.multiply(mDecimal_B);
            }
            return mDecimal.floatValue();
        }

        @Override
        public Float divide() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.divide(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.divide(mDecimal_B);
            }
            return mDecimal.floatValue();
        }
    }

    private class IntegerManager extends BaseOperationCallBack<Integer> {

        IntegerManager(BigDecimal a, BigDecimal b) {
            super(a, b);
        }

        @Override
        public Integer add() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.add(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.add(mDecimal_B);
            }
            return mDecimal.intValue();
        }

        @Override
        public Integer subtract() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.subtract(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.subtract(mDecimal_B);
            }
            return mDecimal.intValue();
        }

        @Override
        public Integer multiply() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.multiply(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.multiply(mDecimal_B);
            }
            return mDecimal.intValue();
        }

        @Override
        public Integer divide() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.divide(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.divide(mDecimal_B);
            }
            return mDecimal.intValue();
        }
    }

    private class LongManager extends BaseOperationCallBack<Long> {

        LongManager(BigDecimal a, BigDecimal b) {
            super(a, b);
        }

        @Override
        public Long add() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.add(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.add(mDecimal_B);
            }
            return mDecimal.longValue();
        }

        @Override
        public Long subtract() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.subtract(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.subtract(mDecimal_B);
            }
            return mDecimal.longValue();
        }

        @Override
        public Long multiply() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.multiply(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.multiply(mDecimal_B);
            }
            return mDecimal.longValue();
        }

        @Override
        public Long divide() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.divide(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.divide(mDecimal_B);
            }
            return mDecimal.longValue();
        }
    }

    private class StringManager extends BaseOperationCallBack<String> {

        StringManager(BigDecimal a, BigDecimal b) {
            super(a, b);
        }

        @Override
        public String add() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.add(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.add(mDecimal_B);
            }
            return mDecimal.toString();
        }

        @Override
        public String subtract() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.subtract(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.subtract(mDecimal_B);
            }
            return mDecimal.toString();
        }

        @Override
        public String multiply() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.multiply(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.multiply(mDecimal_B);
            }
            return mDecimal.toString();
        }

        @Override
        public String divide() {
            if (mRoundingMode != null) {
                mDecimal = mDecimal_A.divide(mDecimal_B).setScale(mDecimalNum, mRoundingMode);
            } else {
                mDecimal = mDecimal_A.divide(mDecimal_B);
            }
            return mDecimal.toString();
        }
    }

}
