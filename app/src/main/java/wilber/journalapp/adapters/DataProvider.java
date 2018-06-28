package wilber.journalapp.adapters;

public class DataProvider {
    private  String time;
    private String title;
    private String body;
    private String last_edited;

    public String getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(String last_edited) {
        this.last_edited = last_edited;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DataProvider(String time, String title,String body,String last_edited){
        this.time=time;
        this.title=title;
        this.body=body;
        this.last_edited=last_edited;
    }
}
