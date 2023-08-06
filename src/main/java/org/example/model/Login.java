package org.example.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logins")
public class Login {
    @Id
    private String id;
    private String email;
    private String password;

    public Login() {
    }

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}


    // Przydatna metoda do reprezentacji obiektu jako string
    @Override
    public String toString() {
        return "Login{" +
                "id='" + id + '\'' +
                ", title='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}