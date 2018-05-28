package com.heilov.heilov.Utils;

import android.content.Context;

public interface Observable {

    void attachObserver(Observer o);
    void deattachObserver(Observer o);
    void notify(Context c,String message);
}
