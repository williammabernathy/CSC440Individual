package application.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController
{
    private static Connection connection;

    @FXML private Button loginButton;
    @FXML private Button forgotPasswordButton;
    @FXML private TextField passwordTextField;
    @FXML private TextField useranameTextField;
    private static String loggedUser;

    public void handleLoginButtonAction(MouseEvent mouseEvent) throws IOException {
        // Authenticate user here
        // and display landing page if true
        String validateUser, validatePass;
        int verification = 0;
        setupConnection();

        //get username text
        validateUser = useranameTextField.getText();
        //get password text
        validatePass = passwordTextField.getText();
        //call validation method
        verification = validateLogin(validateUser, validatePass);

        //check if password matched
        if (verification == 1)
        {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../../resources/view/landingPage.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1251, 787);
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setTitle("Bank Management System");
                stage.setScene(scene);
                useranameTextField.clear();
                passwordTextField.clear();
                stage.show();
            } catch (IOException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create loading window", e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Username or Password", ButtonType.OK);
            alert.showAndWait();
            useranameTextField.clear();
            passwordTextField.clear();
        }
    }

    public static int validateLogin(String enteredUser, String enteredPass)
    {
        int count = 0;
        try
        {
            String query = "select * from employee where employeeUsername = '"+enteredUser+"' AND employeePassword = PASSWORD('"+enteredPass+"')";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next())
            {
                LoginController.setUsername(rs.getString("employeeUsername"));
                count++;
            }
            st.close();
        }
        catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            // logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }

        return count;
    }

    /*  Database Connection  */
    public static Connection setupConnection()
    {
        final String driver = "com.mysql.jdbc.Driver";
        String dbConnection = "jdbc:mysql://127.0.0.1:3306/bankmanagementsystem";
        String username = "root";
        String password = "root";

        try
        {
            Class.forName(driver);
        }
        catch (Exception e)
        {
            //System.out.println("catch driver");
        }

        try
        {
            connection = DriverManager.getConnection(dbConnection, username, password);
            return connection;
        }
        catch (Exception e)
        {
            //System.out.println("catch connection");
        }

        return connection;
    }

    public static void setUsername(String user) {
        loggedUser = user;
    }

    public static String getUsername() {
        return loggedUser;
    }
}
