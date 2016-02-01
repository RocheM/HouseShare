package itt.matthew.houseshare.Events;

/**
 * Created by Matthew on 27/01/2016.
 */
public class RequestDetailsEvent {

    private char requestFlag = 'h';

    public RequestDetailsEvent(char requestFlag){
        this.requestFlag = requestFlag;

    }

    public char getRequestFlag() {
        return requestFlag;
    }

    public void setRequestFlag(char requestFlag) {
        this.requestFlag = requestFlag;
    }
}
