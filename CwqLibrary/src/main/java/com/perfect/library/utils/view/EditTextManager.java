package com.perfect.library.utils.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Canghaixiao.
 * Time : 2017/5/10 11:25.
 * Function :EditText控件管理类(部分兼容TextView)
 */
@SuppressWarnings("unused")
public class EditTextManager {

    public interface onTextLengthChangedListener {
        void onChanged(View view, String value, boolean isEmpty);
    }

    private static final String INPUT_NUMBER = "0123456789";
    private static final String INPUT_LETTER_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String INPUT_LETTER_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String INPUT_SYMBOL = "`~!@#$%^&*()_+-=[]{}|;':,./\\，。、；’【】、";

    private Pattern mEmojiPattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    private onTextLengthChangedListener mTextLengthChangedListener;
    private boolean isCanEmoji = true;
    private View mView;

    public static EditTextManager getManager(View view) {
        return new EditTextManager(view);
    }

    private EditTextManager(View view) {
        this.mView = view;
    }

    public void addTextChangedListener(onTextLengthChangedListener listener) {
        this.mTextLengthChangedListener = listener;
        if (mView != null) {
            if (mView instanceof EditText) {
                ((EditText) mView).addTextChangedListener(new ManagerTextWatcher());
            } else if (mView instanceof TextView) {
                ((TextView) mView).addTextChangedListener(new ManagerTextWatcher());
            } else {
                setTypeError();
            }
        }
    }

    private void setTypeError() {
        throw new IllegalAccessError("The view type error,The view must be TextView or EditText");
    }

    private boolean checkViewIsEditText() {
        if (mView != null) {
            if (mView instanceof EditText) {
                return true;
            }
        }
        setTypeError();
        return false;
    }

    public EditTextManager setInputOnlyNumber() {
        isCanEmoji = false;
        if (checkViewIsEditText()) {
            setKeyType((EditText) mView, INPUT_NUMBER);
        }
        return this;
    }

    public EditTextManager setInputOnlyLetterLowercase() {
        isCanEmoji = false;
        if (checkViewIsEditText()) {
            setKeyType((EditText) mView, INPUT_LETTER_LOWERCASE);
        }
        return this;
    }

    public EditTextManager setInputOnlyLetterUppercase() {
        isCanEmoji = false;
        if (checkViewIsEditText()) {
            setKeyType((EditText) mView, INPUT_LETTER_UPPERCASE);
        }
        return this;
    }

    public EditTextManager setInputNumberAndLetterLowercase() {
        isCanEmoji = false;
        if (checkViewIsEditText()) {
            setKeyType((EditText) mView, INPUT_NUMBER + INPUT_LETTER_LOWERCASE);
        }
        return this;
    }

    public EditTextManager setInputNumberAndLetterUppercase() {
        isCanEmoji = false;
        if (checkViewIsEditText()) {
            setKeyType((EditText) mView, INPUT_NUMBER + INPUT_LETTER_UPPERCASE);
        }
        return this;
    }

    public EditTextManager setInputNumberAndLetter() {
        isCanEmoji = false;
        if (checkViewIsEditText()) {
            setKeyType((EditText) mView, INPUT_NUMBER + INPUT_LETTER_LOWERCASE + INPUT_LETTER_UPPERCASE);
        }
        return this;
    }

    public EditTextManager setInputAllButHanzi() {
        isCanEmoji = false;
        if (checkViewIsEditText()) {
            setKeyType((EditText) mView, INPUT_LETTER_LOWERCASE + INPUT_LETTER_UPPERCASE + INPUT_NUMBER + INPUT_SYMBOL);
        }
        return this;
    }

    public EditTextManager setInputAllButEmoji() {
        isCanEmoji = false;
        checkViewIsEditText();
        setNoEmoji((EditText) mView);
        return this;
    }

    private void setKeyType(EditText editText, final String chars) {
        final int inputType = editText.getInputType();
        editText.setKeyListener(new NumberKeyListener() {
            @NonNull
            @Override
            protected char[] getAcceptedChars() {
                return chars.toCharArray();
            }

            @Override
            public int getInputType() {
                return inputType == InputType.TYPE_NULL ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : inputType;
            }
        });
    }

    private void setNoEmoji(EditText editText) {
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        //第一重过滤,这个方法能过滤掉大部分表情,但有极个别的过滤不掉,故加上下面的方法进行双重过滤
                        Matcher emojiMatcher = mEmojiPattern.matcher(source);
                        if (emojiMatcher.find()) {
                            //不支持输入表情
                            return "";
                        }
                        //第二重过滤,增强排查力度,如果还有漏网之鱼，则进行第三次过滤(暂不考虑第三次过滤)
                        if (containsEmoji(source.toString())) {
                            //不支持输入表情
                            return "";
                        }
                        return null;
                    }
                }
        });
    }

    /**
     * check this string is has emoji
     */
    private static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check this char is emoji
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    private class ManagerTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            if (value.length() > 0) {
                setChanged(value, false);
            } else {
                setChanged(value, true);
            }
        }
    }

    private void setChanged(String value, boolean isEmpty) {
        if (mTextLengthChangedListener != null) {
            if (mView != null) {
                mTextLengthChangedListener.onChanged(mView, value, isEmpty);
            }
        }
    }

    /**
     * Only this method,The view could be all but EditText
     */
    public void hideSoftInput() {
        if (mView != null) {
            mView.requestFocus();
            InputMethodManager imm = (InputMethodManager) mView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
            }
        }
    }
}
