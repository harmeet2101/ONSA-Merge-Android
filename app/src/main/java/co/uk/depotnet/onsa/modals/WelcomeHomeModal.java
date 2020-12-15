package co.uk.depotnet.onsa.modals;

public class WelcomeHomeModal {
    private int WH_id;
    private String WH_title;
    private int WH_icon;
    private int WH_color;

    public WelcomeHomeModal(int WH_id, String WH_title, int WH_icon, int WH_color) {
        this.WH_id = WH_id;
        this.WH_title = WH_title;
        this.WH_icon = WH_icon;
        this.WH_color = WH_color;
    }

    public int getWH_id() {
        return WH_id;
    }

    public void setWH_id(int WH_id) {
        this.WH_id = WH_id;
    }

    public String getWH_title() {
        return WH_title;
    }

    public void setWH_title(String WH_title) {
        this.WH_title = WH_title;
    }

    public int getWH_icon() {
        return WH_icon;
    }

    public void setWH_icon(int WH_icon) {
        this.WH_icon = WH_icon;
    }

    public int getWH_color() {
        return WH_color;
    }

    public void setWH_color(int WH_color) {
        this.WH_color = WH_color;
    }
}
