package itt.matthew.houseshare.Events;

import android.content.Context;
import android.content.Intent;



/**
 * Created by Matthew on 22/01/2016.
 */

public class MessageEvent {
    public final String message;
    public final int index;

    public MessageEvent(String message,int index) {
        this.message = message;
        this.index = index;
    }
}