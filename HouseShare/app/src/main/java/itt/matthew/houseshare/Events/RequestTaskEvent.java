package itt.matthew.houseshare.Events;

/**
 * Created by Matth on 22/04/2016.
 */
public class RequestTaskEvent {


    private int reqestID;

    public RequestTaskEvent(int reqestID) {
        this.reqestID = reqestID;
    }

    public int getReqestID() {
        return reqestID;
    }

    public void setReqestID(int reqestID) {
        this.reqestID = reqestID;
    }

}
