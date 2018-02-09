package com.xjs.routerwrapper.compiler.utils;


import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * @author xjs
 *         on  2018/1/13
 *         desc:
 */

public class Logger {

    private Messager messager;
    private static final String TAG = "Builder:";

    public Logger(Messager messager) {
        this.messager = messager;
    }

    public void info(CharSequence charSequence) {
        messager.printMessage(Diagnostic.Kind.NOTE, TAG + charSequence);
    }

    public void error(CharSequence charSequence) {
        messager.printMessage(Diagnostic.Kind.ERROR, TAG + charSequence);
    }
    public void error(StackTraceElement[] stackTrace) {
        messager.printMessage(Diagnostic.Kind.ERROR, TAG + formatStackTrace(stackTrace));

    }


    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
