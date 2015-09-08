package com.example.realtalk.realtalk;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by alexgomes on 2015-08-15.
 */

public class FontManager {

    public enum Font{
        JustAnotherHandRegular,
        MontSerratBold,
        MontSerratRegular,
        OpenSansRegular,
        RobotoRegular
    }


    public static Typeface setFont(Context context, Font fontName){
        Typeface myTypeface = Typeface.createFromAsset(context.getResources().getAssets(), FontName(fontName));
        return myTypeface;
    }

    private static String FontName(Font font){
        String f;
        switch (font){
            case JustAnotherHandRegular:
                f = "JustAnotherHand-Regular.ttf";
                break;
            case MontSerratRegular:
                f = "Montserrat-Regular.ttf";
                break;
            case MontSerratBold:
                f = "Montserrat-Bold.ttf";
                break;
            case OpenSansRegular:
                f = "OpenSans-Regular.ttf";
                break;
            case RobotoRegular:
                f = "Roboto-Regular.ttf";
                break;
            default:
                throw new IllegalArgumentException("Invalid font name: " + font);
        }
        return f;
    }
}
