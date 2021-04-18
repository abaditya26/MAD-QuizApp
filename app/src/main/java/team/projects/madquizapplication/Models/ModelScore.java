package team.projects.madquizapplication.Models;

public class ModelScore {
    private String quizId, total, correct, uid;

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ModelScore() {
    }

    public ModelScore(String quizId, String total, String correct, String uid) {
        this.quizId = quizId;
        this.total = total;
        this.correct = correct;
        this.uid = uid;
    }
}
