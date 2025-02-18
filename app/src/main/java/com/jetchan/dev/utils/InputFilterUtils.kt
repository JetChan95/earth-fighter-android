package com.jetchan.dev.utils

import android.text.InputFilter

object InputFilterUtils {
    /**
     * 创建一个只允许输入字母的过滤器
     */
    fun onlyLetters(): InputFilter {
        return InputFilter { source, start, end, dest, dstart, dend ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (Character.isLetter(c)) {
                    sb.append(c)
                }
            }
            if (source == sb) {
                null
            } else {
                sb.toString()
            }
        }
    }

    /**
     * 创建一个只允许输入数字的过滤器
     */
    fun onlyNumbers(): InputFilter {
        return InputFilter { source, start, end, dest, dstart, dend ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (Character.isDigit(c)) {
                    sb.append(c)
                }
            }
            if (source == sb) {
                null
            } else {
                sb.toString()
            }
        }
    }
}