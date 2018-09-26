package com.teamspeak.ts3sdkclient.ts3sdk.states;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({Visibility.ENTER_VISIBILITY, Visibility.RETAIN_VISIBILITY, Visibility.LEAVE_VISIBILITY})
public @interface Visibility {
    int ENTER_VISIBILITY = 0;
    int RETAIN_VISIBILITY = 1;
    int LEAVE_VISIBILITY = 2;
}