package team.projects.madquizapplication.Models;

public class UserModel {
    private String uid, email, name, phone, image, role;

    public UserModel() {
    }

    public UserModel(String uid, String email, String name, String phone) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.image = "default";
        this.role = "user";
    }

    public UserModel(String uid, String email, String name, String phone, String image) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.role = "user";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
