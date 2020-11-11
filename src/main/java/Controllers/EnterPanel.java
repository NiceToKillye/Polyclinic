package Controllers;

import Entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class EnterPanel {

    public EnterPanel(){

    }

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session = sessionFactory.openSession();

    Map<String, String> admins;
    Map<String, String> receptionists;
    Map<String, String> doctors;
    Map<String, String> patients;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    private Button getInButton;

    @FXML
    private Label warning;

    @FXML
    void getIn(ActionEvent event) throws IOException {

        String login = loginField.getText();
        String password = passwordField.getText();

        if(password.isEmpty() || login.isEmpty()){
            return;
        }

        admins = session.createQuery("from Admin", Admin.class).getResultList()
                .stream().collect(Collectors.toMap(Admin::getLogin, Admin::getPassword));

        receptionists = session.createQuery("from Receptionist", Receptionist.class).getResultList()
                .stream().collect(Collectors.toMap(Receptionist::getLogin, Receptionist::getPassword));

        doctors = session.createQuery("from Doctor", Doctor.class).getResultList()
                .stream().collect(Collectors.toMap(Doctor::getLogin, Doctor::getPassword));

        patients = session.createQuery("from Patient", Patient.class).getResultList()
                .stream().collect(Collectors.toMap(Patient::getLogin, Patient::getPassword));

        if (password.equals(admins.get(login))){
            System.out.println("Log in as Admin:" + " Login" + login + " Password:" + password);
            changeScene(event, "/adminPanel", "Admin Panel");
            session.close();
        }
        else if (password.equals(receptionists.get(login))){
            System.out.println("Log in as Receptionist:" + " Login" + login + " Password:" + password);
            changeScene(event, "/receptionistPanel", "Receptionist Panel");
            session.close();
        }
        else if (password.equals(doctors.get(login))){
            System.out.println("Log in as Doctor:" + " Login" + login + " Password:" + password);
            changeScene(event, "/doctorPanel", "Doctor Panel");
            session.close();
        }
        else if (password.equals(patients.get(login))){
            System.out.println("Log in as Patients:" + " Login" + login + " Password:" + password);
            changeScene(event, "/patientPanel", "Patient Panel");
            session.close();
        }
        else{
            warning.setVisible(true);
        }
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private void changeScene(ActionEvent event, String panel, String title) throws IOException {
        Scene scene = new Scene(loadFXML(panel));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }


}
