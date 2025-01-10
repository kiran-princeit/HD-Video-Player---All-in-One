package hd.video.player.videoplayer.mplayer.masterplayer;

public class IconModel {
    private String icontitle;
    private int imageview;

    public int getImageview() {
        return this.imageview;
    }

    public void setImageview(int i) {
        this.imageview = i;
    }

    public String getIcontitle() {
        return this.icontitle;
    }


    public IconModel(int i, String str) {
        this.imageview = i;
        this.icontitle = str;
    }
}
