package e.orz.cof;

import android.net.Uri;

import java.util.ArrayList;

public class Content {
    private String userName;
    private Uri portrait;
    private String text;
    private int upNum;
    private ArrayList<String> photoList;
    private ArrayList<String> commentList;

    public int getUpNum() {
        return upNum;
    }

    public void setUpNum(int upNum) {
        this.upNum = upNum;
    }

    public void setPhotoList(ArrayList<String> photoList) {
        this.photoList = photoList;
    }

    public ArrayList<String> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<String> commentList) {
        this.commentList = commentList;
    }



    public Content(){
        photoList = new ArrayList<>();
    }

    public Content(String userName, Uri portrait, String text, int upNum){
        this.userName = userName;
        this.portrait = portrait;
        this.text = text;
        this.upNum = upNum;
        photoList = new ArrayList<>();
    }




    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPortrait(Uri portrait) {
        this.portrait = portrait;
    }


    public String getUserName() {
        return userName;
    }

    public Uri getPortrait() {
        return portrait;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getPhotoList() {
        return photoList;
    }

    public void addPhoto(String photo){
        photoList.add(photo);
    }
    public void addComment(String comment){
        commentList.add(comment);
    }
}
