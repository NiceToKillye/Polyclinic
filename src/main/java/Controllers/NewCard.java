package Controllers;

import Entities.Doctor;
import Entities.LinkedKey;
import Entities.PatientCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class NewCard implements Initializable {

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session = sessionFactory.openSession();
    ObservableList<Doctor> doctors;

    @FXML
    private DatePicker initialDateField;

    @FXML
    private ComboBox<Doctor> doctorMenu;


    int id;

    public NewCard(int id){
        this.id = id;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doctors = FXCollections.observableArrayList(
                session.createQuery("from Doctor", Doctor.class).getResultList());
        doctorMenu.setItems(doctors);
        doctorMenu.getSelectionModel().selectFirst();

        Callback<ListView<Doctor>, ListCell<Doctor>> cellCallback = new Callback<>() {
            @Override
            public ListCell<Doctor> call(ListView<Doctor> doctorListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Doctor doctor, boolean empty) {
                        super.updateItem(doctor, empty);
                        if (doctor == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(doctor.getSurname() + ": " + doctor.getSpeciality());
                        }
                    }
                };
            }
        };

        doctorMenu.setButtonCell(cellCallback.call(null));
        doctorMenu.setCellFactory(cellCallback);
    }


    @FXML
    void cancel(ActionEvent event) {
        session.close();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(ActionEvent event) {
        Doctor doc = doctorMenu.getValue();
        LocalDate initialDate = initialDateField.getValue();
        if(initialDate == null || initialDate.isBefore(LocalDate.now())){
            initialDateField.setStyle("-fx-control-inner-background: red;");
            return;
        }
        else {
            initialDateField.setStyle("-fx-control-inner-background: white;");
        }
        String docFIO = doc.getSurname() + " " + doc.getName() + " " + doc.getPatronymic();
        PatientCard card = new PatientCard(doc.getId(), id,initialDate, docFIO);
        LinkedKey key = new LinkedKey(id, doc.getId(), initialDate);
        card.setKey(key);
        try{
            session.beginTransaction();
            session.save(card);
            session.getTransaction().commit();
        }
        catch (Exception exception){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }
        session.close();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
