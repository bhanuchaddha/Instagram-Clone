package com.ben.instagramclone;

import android.content.Context;
import android.content.Intent;

public class Util {

public static <T> void navigateTo(Class<T> clazz, Context context){
    context.startActivity(new Intent(context, clazz));
}
}
