package team.projects.madquizapplication.Models;

public class ModelQuizData {
    private String quizId, quizName, quizImage, authorName, authorId, password;
    private boolean isPrivate;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public ModelQuizData(String quizId, String quizName, String quizImage, String authorName, String authorId, String password, boolean isPrivate) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.quizImage = quizImage;
        this.authorName = authorName;
        this.authorId = authorId;
        this.password = password;
        this.isPrivate = isPrivate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public ModelQuizData() {
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getQuizImage() {
        return quizImage;
    }

    public void setQuizImage(String quizImage) {
        this.quizImage = quizImage;
    }
}

